package com.walk_nie.taobao.mizuno.shoes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.mizuno.GoodsObject;
import com.walk_nie.taobao.mizuno.MizunoUtil;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;


public class MinunoShoesParser extends BaseBaobeiParser{
    
    
    public GoodsObject parseProductByProductUrl(String prodUrl) throws IOException {
        GoodsObject prodObj = new GoodsObject();
        prodObj.productUrl = prodUrl;
        parseProduct(prodObj);
    	return prodObj;
    }

    public List<GoodsObject> parseProductByCategoryUrl(String categoryUrl) throws IOException{
        List<GoodsObject> prodList = Lists.newArrayList();
        parseCategory(prodList,categoryUrl);
        
        parseProducts(prodList);
        
        List<GoodsObject> filteredProdList = filter(prodList);
        
        translate(filteredProdList);
        
        return prodList;
    }
    private void parseCategory(List<GoodsObject> prodList, String categoryUrl) throws IOException {
        Document doc = getDocument(categoryUrl);
        Elements prodEls = doc.select("div#main").select("div.compo_item-list_cmn _disp").select("div.list");
        for(Element rootEl :prodEls){
            GoodsObject prodObj = new GoodsObject();
            Element root = rootEl.select("div.list-cont").get(0);
            
            prodObj.productUrl = root.select("p.img").select("a.productMainLink").attr("href");
            
            prodList.add(prodObj);
        }
    }
    private void parseProducts(List<GoodsObject> prodList) throws IOException {
        for(GoodsObject prod :prodList){
            parseProduct(prod);
        }
    }
    
    private void parseProduct(GoodsObject prodObj) throws IOException {
    	String url = prodObj.productUrl;
    	if(!url.startsWith("http")){
    		url = MizunoUtil.urlPrefix + prodObj.productUrl;
    		prodObj.productUrl = url;
    	}
        Document doc = getDocument(url);
        Element mainEl = doc.select("div#main").get(0);
        Elements inputEls = mainEl.select("input");
        for(Element el:inputEls){
        	if("PARAM_GOODS_NO".equals(el.attr("name"))){
        		prodObj.goodsNo = el.attr("value");
        	}
        	if("ATT_GRP_ID".equals(el.attr("name"))){
        		prodObj.kataban = el.attr("value");
        	}
        }
        Element headerEl = mainEl.select("div.item_detail").get(0);
        Elements els = headerEl.select("div.item_detail_summary").select("h1");
        if(!els.isEmpty()){
        	prodObj.titleJP = els.get(0).text();
        }
        Elements genderEls = headerEl.select("span#icon_sex");
        for(Element ele :genderEls){
            String alt = ele.text();
            if(alt.equals("レディース")){
                prodObj.gender = 2;
            }else{
                prodObj.gender = 3; 
            }
        }
        if(prodObj.titleJP.contains("メンズ")){
            prodObj.gender = 1; 
        }else if(prodObj.titleJP.contains("レディース")){
            prodObj.gender = 2; 
        }else if(prodObj.titleJP.contains("ユニセックス")){
            prodObj.gender = 3; 
        }
        
        Element priceEl= headerEl.select("div#PRICE_AREA_PC").get(0);
        String price = priceEl.select("span.price").get(0).text();
        price = price.replaceAll("¥", ""); 
        price = price.replaceAll(",", ""); 
        prodObj.priceOrg = price;
    
        Elements colorEls = headerEl.select("ul.list_colors").select("a");
        if(colorEls.size() > 1){
            for(Element colorEl:colorEls){
                String text = colorEl.select("img").attr("src");
                prodObj.colorPicUrlList.add(text);
                 text = colorEl.text();
                prodObj.colorNameList.add(text);
            }
		}

		Elements sizeEls = headerEl.select("ul.list_size").get(0).select("li");
		if (sizeEls.size() > 1) {
			for (Element el : sizeEls) {
				String text = el.text();
				prodObj.sizeNameList.add(text);
			}
		}
        
        Elements picEls = headerEl.select("div.thumbnail-colors").select("ul").get(1).select("img");
        for(Element picEl:picEls){
            String picUrl = picEl.attr("data-zoom-image");
            if(!StringUtil.isBlank(picUrl)){
                prodObj.dressOnPicsUrlList.add(picUrl);
            }
        }
		// specArea to picture!
		screenshotProductDetailDesp(prodObj);
    }

	private void screenshotProductDetailDesp(GoodsObject obj) throws IOException {

		String fileNameFmt = "detail_%s.png";
		String fileName = String.format(fileNameFmt, obj.kataban);
		File despFile = new File(MizunoUtil.getPictureSavePath(obj), fileName);
		if (!despFile.exists()) {
			WebDriver webDriver = WebDriverUtil.getWebDriver(obj.productUrl);
			List<WebElement> eles = webDriver.findElements(By.className("sec01"));
			List<WebElement> scrnShotEles = Lists.newArrayList();
			for (WebElement ele : eles) {
				List<WebElement> h3Eles = ele.findElements(By.tagName("h3"));
				String h3Text = "";
				if (h3Eles != null && !h3Eles.isEmpty()) {
					h3Text = h3Eles.get(0).getText();
				}
				if ("商品仕様".equals(h3Text) || "サイズについて".equals(h3Text) || "特長".equals(h3Text)) {
					scrnShotEles.add(ele);
				}
			}
			if (!scrnShotEles.isEmpty()) {
				WebDriverUtil.screenShotV2(webDriver, scrnShotEles, despFile.getAbsolutePath(), null);
				obj.detailScreenShotPicFile = despFile.getAbsolutePath();
			}
		}
	}

	private List<GoodsObject> filter(List<GoodsObject> prodList) {
        List<GoodsObject> filterdList = Lists.newArrayList();
        List<String> urls = Lists.newArrayList();
        for(GoodsObject prod :prodList){
            if(!urls.contains(prod.productUrl)){
                urls.add(prod.productUrl);
                filterdList.add(prod);
            }
        }
        return filterdList;
    }

    private void translate(List<GoodsObject> prodList) {
        for(GoodsObject prod :prodList){
            translateTitle(prod);
        }
    }
    
    private void translateTitle(GoodsObject prod) {
        String title = prod.titleOrg;
        // TODO 
        title = title.replaceAll("", "");
        prod.title = title;
    }
    private Document getDocument(String url) throws IOException{
        return TaobaoUtil.urlToDocument(url,"Shift_JIS");
    }
    
}
