package com.walk_nie.taobao.kakaku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;

public abstract class KakakuBaobeiParser  extends BaseBaobeiParser{
    protected File categoryUrlsFile = null;
    protected String categoryUrl = "";
    protected List<String> categoryUrls = Lists.newArrayList();
    
    public KakakuBaobeiParser(){
    }

    public List<KakakuObject> parse() throws IOException, Exception {
        List<KakakuObject> itemList = new ArrayList<KakakuObject>();

        if (categoryUrlsFile != null) {
            // get item ID only!
            itemList = parseItemIdFromCategoryFile(categoryUrlsFile);
        }else if(!CollectionUtils.isEmpty(categoryUrls)){
            // get item ID only!
            itemList = parseItemIdFromCategoryUrls(categoryUrls);
        }else if(!StringUtils.isEmpty(categoryUrl)){
            // get item ID only!
            itemList = parseItemIdFromCategoryUrl(categoryUrl);
        }

        List<KakakuObject> filterItemList = new ArrayList<KakakuObject>();
        for (KakakuObject obj : itemList) {
            KakakuObject objTemp = parseDetail(obj);
            if(objTemp == null){
                System.out.println("404 Page.Id=" + obj.id);
                continue;
            }
            if (isAllowToBaobei(objTemp)) {
                parseMakerSite(objTemp);
                filterItemList.add(objTemp);
            }
        }
        return filterItemList;
    }

    private List<KakakuObject> parseItemIdFromTaobaoList(List<BaobeiPublishObject> toUpdatebaobeiList2) {
        List<KakakuObject> list = new ArrayList<KakakuObject>();
//        String prefix = "KKKU-";
        for (BaobeiPublishObject baobei : toUpdatebaobeiList2) {
            KakakuObject obj = new KakakuObject();
//            if(!baobei.outer_id.startsWith(prefix)){
//                continue;
//            }
//            obj.id = baobei.outer_id.substring(prefix.length()+1);
            obj.id = baobei.outer_id;
            list.add(obj);
        }
        return list;
    }

	private List<KakakuObject> parseItemIdFromCategoryUrls(List<String> scanUrls)
			throws IOException, Exception {
		List<KakakuObject> list = new ArrayList<KakakuObject>();
        for (String scanUrl : scanUrls) {
    		List<KakakuObject> rslt = KakakuUtil.parseCategoryUrl(scanUrl);
            for (KakakuObject obj : rslt) {
                if (!isPublished(obj)) {
                    list.add(obj);
                }
            }
        }
		return list;
	}

	private List<KakakuObject> parseItemIdFromCategoryUrl(String scanUrl)
			throws IOException, Exception {

		List<KakakuObject> list = new ArrayList<KakakuObject>();
		List<KakakuObject> rslt = KakakuUtil.parseCategoryUrl(scanUrl);
		for (KakakuObject obj : rslt) {
			if (!isPublished(obj)) {
				list.add(obj);
			}
		}
		return list;
	}

    private List<KakakuObject> parseItemIdFromCategoryFile(File scanUrlsFile2) throws IOException, Exception {

        List<KakakuObject> list = new ArrayList<KakakuObject>();

        BufferedReader br = null;
        try {
            String str = null;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(scanUrlsFile2), "UTF-8"));
            while ((str = br.readLine()) != null) {
                if (!StringUtil.isBlank(str) && !str.startsWith("#")) {
                    List<KakakuObject> rslt = KakakuUtil.parseCategoryUrl(str);
                    for (KakakuObject obj : rslt) {
                        if (!isPublished(obj)) {
                            list.add(obj);
                        }
                    }
                }
            }
        } finally {
            if (br != null)
                br.close();
        }
        return list;
    }

    private KakakuObject parseDetail(KakakuObject objT) throws  IOException {
        KakakuObject obj = new KakakuObject();
        obj.id = objT.id;
        String itemUrl = KakakuUtil.kakakuUrlPrefix + objT.id;
        Document doc = KakakuUtil.urlToDocumentKakaku(itemUrl);
        if(is404(doc)){
            return null;
        }
        KakakuUtil.parseItemMaker(doc, obj);

        KakakuUtil.parseItemPrice(doc, obj);

        KakakuUtil.parseItemPicture(doc, obj);

        parseItemSpec(doc, itemUrl, obj);

        parseItemVariation(doc, obj);

        obj.sku = KakakuUtil.findItemSku(doc);
        
        return obj;
    }

    private boolean is404(Document doc) {
        Elements els = doc.select("dvi#forbidden-msg");
        if (!els.isEmpty())
            return true;
        return false;
    }

    private boolean isPublished(KakakuObject kakakuObj) {
        // TODO
        List<String> publishedItems = Lists.newArrayList();
        for (String baobei : publishedItems) {
            if (kakakuObj.id.equals(baobei)) {
                return true;
            }
        }
        return false;
    }

    protected void parseItemBasicSpec(Document doc, KakakuObject obj) {

        Elements specBox = doc.select("div").select("#productInfoBox")
                .select("#specBox").select("#specInfo").select("a");
        for (Element spec : specBox) {
            if (spec.text().equals("メーカー製品情報ページ")) {
                obj.spec.productInfoUrl = spec.attr("href");
            }
            if (spec.text().equals("メーカー仕様表")) {
                obj.spec.specInfoUrl = spec.attr("href");
            }
        }
    }
    protected void parseItemVariation(Document doc, KakakuObject obj)
            throws  IOException {

        Elements variation = doc.select("div").select("#productInfoBox")
                .select(".variation");
        if (!variation.isEmpty()) {
            Elements colors = variation.select("tr");
            for (Element color : colors) {
                Elements td = color.select("td");
                String colorName = td.get(0).select("img").get(0).attr("title");
                String href = td.get(3).select("a").get(0).attr("href");
                KakakuObject colorObj = new KakakuObject();
                Document colorDoc = KakakuUtil.urlToDocumentKakaku(href);
                KakakuUtil.parseItemMaker(colorDoc, colorObj);
                KakakuUtil.parseItemPrice(colorDoc, colorObj);
                parseItemSpec(colorDoc, href, colorObj);
                if (colorObj.priceMin != null) {
                    colorObj.sku = KakakuUtil.findItemSku(colorDoc);
                    colorObj.colorName = colorName;
                    obj.colorList.add(colorObj);
                } else {
                    System.out.println("[WARN][price is ZERO] " + colorObj);
                }
            }
        }
    }

    public KakakuBaobeiParser setScanCategoryUrlsFile(File scanUrlsFile) {
        this.categoryUrlsFile = scanUrlsFile;
        return this;
    }

    public KakakuBaobeiParser setScanCategoryUrl(String scanUrl) {
        this.categoryUrl = scanUrl;
        return this;
    }

    public KakakuBaobeiParser setScanCategoryUrl(List<String> scanUrls) {
        this.categoryUrls = scanUrls;
        return this;
    }

    protected abstract void parseItemSpec(Document doc, String itemUrl,
            KakakuObject obj) throws  IOException;
    
    protected abstract boolean isAllowToBaobei(KakakuObject kakakuObj);
    protected abstract void parseMakerSite(KakakuObject kakakuObj)throws  IOException;
}
