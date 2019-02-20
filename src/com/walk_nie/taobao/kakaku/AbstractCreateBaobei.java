package com.walk_nie.taobao.kakaku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public abstract class AbstractCreateBaobei {

	private String scanUrlsFile = "";
	private List<BaobeiPublishObject> toUpdatebaobeiList;
	
	private String taobeiTemplateFile = "";
	private String publishedBaobeiFile = "";
	private String miaoshuTemplateFile = "";
	private String outputFile = "";
	protected List<String> publishedItems = Lists.newArrayList();
	protected BaobeiPublishObject baobeiTemplate = new BaobeiPublishObject();
	
	protected String outputPicFolder = "";

	protected abstract boolean isAllowToBaobei(KakakuObject obj);

	protected abstract String composeBaobeiLine(KakakuObject item,
			BaobeiPublishObject baobeiTemplate) throws Exception;

	public AbstractCreateBaobei setScanUrlsFile(String scanUrlsFile) {
		this.scanUrlsFile = scanUrlsFile;
		return this;
	}

	public AbstractCreateBaobei setTaobeiTemplateFile(String taobeiTemplateFile) {
		this.taobeiTemplateFile = taobeiTemplateFile;
		return this;
	}

	public AbstractCreateBaobei setPublishedBaobeiFile(
			String publishedBaobeiFile) {
		this.publishedBaobeiFile = publishedBaobeiFile;
		return this;
	}

	public AbstractCreateBaobei setMiaoshuTemplateFile(
			String miaoshuTemplateFile) {
		this.miaoshuTemplateFile = miaoshuTemplateFile;
		return this;
	}

	public String getMiaoshuTemplateFile() {
		return this.miaoshuTemplateFile;
	}

	public AbstractCreateBaobei setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}

	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			    
			List<KakakuObject> itemIdList = Lists.newArrayList();
			if (itemIdList.isEmpty())
				return;

			String outFilePathPrice = String.format(outputFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			File csvFile = new File(outFilePathPrice);
			outputPicFolder = TaobaoUtil.getPictureFolder(csvFile);;
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());

			for (KakakuObject obj : itemIdList) {
				writeOut(priceBw, obj);
			}

			System.out.println("-------- FINISH--------");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (priceBw != null)
				try {
					priceBw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}


    private void writeOut(BufferedWriter priceBw, KakakuObject item)
			throws Exception {
		try {
			// TODO !
			 parsePicture(item);
		} catch (Exception e) {
			System.out.println("[WARN] Downloading picture was failure! id = "
					+ item.id);
			e.printStackTrace();
			return;
		}
		priceBw.write(composeBaobeiLine(item, baobeiTemplate));
		priceBw.flush();
	}

	private List<KakakuObject> readIn() throws Exception {


		baobeiTemplate = TaobaoUtil.readInBaobeiTemplate(taobeiTemplateFile);

		BufferedReader br = null;
		if (!StringUtil.isBlank(this.publishedBaobeiFile)) {
			try {
				File file = new File(publishedBaobeiFile);
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), "UTF-8"));
				String str = null;
				while ((str = br.readLine()) != null) {
					if (!StringUtil.isBlank(str) && !str.startsWith("#")) {
						// title \t itemId
						String[] splitStr = str.split("\t");
						if (splitStr.length != 2)
							continue;
						publishedItems.add(splitStr[1]);
					}
				}
			} finally {
				if (br != null)
					br.close();
			}
		}
		return null;
	}
	private List<KakakuObject> readTaobaoList(List<BaobeiPublishObject> toUpdatebaobeiList2) {
        List<KakakuObject> list = new ArrayList<KakakuObject>();
        for(BaobeiPublishObject baobei:toUpdatebaobeiList2){
            KakakuObject obj = new KakakuObject();
            obj.id = baobei.outer_id;
            list.add(obj);
        }
        return list;
    }

    private List<KakakuObject> readFromUrl(String scanUrlsFile2) throws IOException, Exception {

        List<KakakuObject> list = new ArrayList<KakakuObject>();

        BufferedReader br = null;
	    try {
            File file = new File(scanUrlsFile2);
            String str = null;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    file), "UTF-8"));
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

    private boolean isPublished(KakakuObject kakakuObj) {
		for(String baobei :publishedItems){
			if(kakakuObj.id.equals(baobei)){
				return true;
			}
		}
		return false;
	}

	private KakakuObject readItemIn(KakakuObject objT) throws 
			IOException {
		KakakuObject obj = new KakakuObject();
		obj.id = objT.id;
		String itemUrl = KakakuUtil.kakakuUrlPrefix + objT.id;
		Document doc = KakakuUtil.urlToDocumentKakaku(itemUrl);
		KakakuUtil.parseItemMaker(doc, obj);
		
		KakakuUtil.parseItemPrice(doc, obj);
		
		KakakuUtil.parseItemPicture(doc, obj);
		
		parseItemSpec(doc, itemUrl, obj);
		
		parseItemVariation(doc, obj);

		obj.sku = KakakuUtil.findItemSku(doc);
		return obj;
	}

	public void parseItemVariation(Document doc, KakakuObject obj)
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

	protected abstract void parseItemSpec(Document doc, String itemUrl,
			KakakuObject obj) throws  IOException;

	protected void parsePicture(KakakuObject obj)
			throws  IOException {
		String pictureUrlFmt = "http://img1.kakaku.k-img.com/images/productimage/fullscale/%s.jpg";
		if (!obj.colorList.isEmpty()) {
			for (KakakuObject color : obj.colorList) {
				System.out.println("[downloading picture]"
						+ String.format(pictureUrlFmt, color.id));
				TaobaoUtil.downloadPicture(outputPicFolder,
						String.format(pictureUrlFmt, color.id), color.id);
			}
		} else {
			TaobaoUtil.downloadPicture(outputPicFolder,
					String.format(pictureUrlFmt, obj.id), obj.id);
		}
		try {
			parsePictureFromMaker(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected String composeBaobeiMiaoshu(KakakuObject item) throws IOException {
		String templateStr = KakakuUtil.readInBaobeiMiaoshuTemplate(item, this.getMiaoshuTemplateFile());
		String extr = getExtraBaobeiMiaoshu(item);
		return extr + templateStr;
	}

	protected abstract String getExtraBaobeiMiaoshu(KakakuObject obj)
			throws IOException;


	protected abstract void parsePictureFromMaker(KakakuObject obj)
			throws  IOException;
	
    protected void setToUpdateBaobeiList(List<BaobeiPublishObject> baobeiList){
        toUpdatebaobeiList = baobeiList;
    }


}
