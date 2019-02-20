package com.walk_nie.taobao.righton;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;


import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;


public class RightonParser extends BaseBaobeiParser{
	List<SizeTipObject> sizeTipList = Lists.newArrayList();
    
    public List<GoodsObject> parseProductByProductUrl(List<String> prodUrlList) throws IOException {
		List<GoodsObject> prodList = Lists.newArrayList();
		for (String prodUrl : prodUrlList) {
			GoodsObject prodObj = new GoodsObject();
			prodObj.productUrl = prodUrl;
			parseProduct(prodObj);
		}
		List<GoodsObject> filteredProdList = filter(prodList);
		translate(filteredProdList);
		return filteredProdList;
	}

	public List<GoodsObject> parseProductByCategoryUrl(List<String> categoryUrlList) throws IOException {
		List<GoodsObject> filteredProdList = Lists.newArrayList();
		for (String categoryUrl : categoryUrlList) {
			List<GoodsObject> prodList = Lists.newArrayList();
			parseCategory(prodList, categoryUrl);

			parseProducts(prodList);
			filteredProdList.addAll(prodList);

			filteredProdList = filter(filteredProdList);
		}

		translate(filteredProdList);

		return filteredProdList;
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
    		url = RightonUtil.urlPrefix + prodObj.productUrl;
    		prodObj.productUrl = url;
    	}
        Document doc = getDocument(url);
        Element mainEl = doc.select("div.container-fluid-update").get(0);

        // ALL OF IMG
		Elements picEls = mainEl.select("img");
		for (Element picEl : picEls) {
			String picUrl = picEl.attr("src");
			if (!StringUtil.isBlank(picUrl) && picUrl.toUpperCase().endsWith("_L.jpg".toUpperCase())
					&& !prodObj.dressOnPicsUrlList.contains(picUrl)) {
				prodObj.dressOnPicsUrlList.add(picUrl);
			}
		}

        Element brandEle = mainEl.select("div.product-details-box").get(0).select("a").get(0);
    	prodObj.brand = brandEle.text().replace("/brand/", "");
    	
        Element titleEle = mainEl.select("h3").get(0);
    	prodObj.titleJP = titleEle.text();

        Elements h4Eles = mainEl.select("h4");
        for(Element el:h4Eles){
        	String txt = el.text();
        	if(txt.indexOf("品番") != -1){
        		prodObj.kataban = txt.replace("品番 : ", "");
        	}
        }

        Element priceEle = mainEl.select("div.product-price-details").get(0).select("div.price").get(0);
        String price = priceEle.text();
        price = price.replace("+税", "");
        price = price.replace("¥", "");
        price = price.replace(",", "");
        prodObj.priceOrg = price;
        // color
		for (int i = 1; i < 15; i++) {
			try{
				Element sizeEle = mainEl.select("div#scolor_" + i).get(0);
		        Element ele = sizeEle.select("img").get(0);
	        	String picUrl = ele.attr("src");
				if (!StringUtil.isBlank(picUrl)) {
					String tmp = picUrl.substring(picUrl.lastIndexOf("/"));
					tmp = tmp.substring(0, tmp.indexOf("."));
	        		String[] spl = tmp.split("_");
	        		if(spl.length != 4) continue;
	        		prodObj.goodsNo = spl[0];
					prodObj.colorNameList.add(spl[1]);
					getColorPicture(prodObj, spl[1]);
	        	}
			}catch(Exception e){
				break;
			}
		}
		// size
		for (int i = 1; i < 15; i++) {
			try{
				Element sizeEle = mainEl.select("div#size_btn" + i).get(0);
		        Element ele = sizeEle.select("div.size").get(0);
        		prodObj.sizeNameList.add(ele.text());
			}catch(Exception e){
				break;
			}
		}
		
		Elements trEls = mainEl.select("table.table").select("tr");
		for (Element el : trEls) {
			Elements tdEls = el.select("td");
			if(tdEls.size() !=2)continue;
			if(tdEls.get(0).text().equals("原産国"))prodObj.producePlace = tdEls.get(1).text();
		}
		
		screenshotProductDetailDespAndSizeTable(prodObj);
		//processProductSizeTable(prodObj, mainEl);
		
    }

	protected void processProductSizeTable(GoodsObject goodsObj, Element mainEl) throws IOException{
		Element sizeEle = mainEl.select("div.size-img").get(0);
		Element el = sizeEle.select("img").get(0);
		String picUrl = el.attr("src");
		
		File file = new File(RightonUtil.rootPathName,
				RightonUtil.sizeTipFileName);
		if (!file.exists()){
			downloadAndSaveSizeTable(goodsObj,picUrl);
			return;
		}
		if (sizeTipList.isEmpty()) {
			List<String> lines = Files.readLines(file, Charset.forName("UTF-8"));
			for (String line : lines) {
				String[] splits = line.split("\t");
				SizeTipObject obj = new SizeTipObject();
				obj.pictureFileName = splits[1];
				obj.pictureUrl = splits[2];
				sizeTipList.add(obj);
			}
		}
		boolean hasExisted = false;
		for(SizeTipObject obj:sizeTipList){
			if(obj.pictureUrl.equals(picUrl)){
				goodsObj.sizeTipPics.add(obj.pictureFileName);
				hasExisted = true;
				break;
			}
		}
		if(!hasExisted){
			downloadAndSaveSizeTable(goodsObj,picUrl);
		}
	}
	
	private void downloadAndSaveSizeTable(GoodsObject goodsObj, String picUrl)
			throws  IOException {
		String tmp = picUrl.substring(picUrl.lastIndexOf("/"));
		String pictureName = "sizeTable_" + tmp;

		File rootPath = new File(RightonUtil.rootPathName + "/sizeTable/");
		File picFile = TaobaoUtil.downloadPicture(rootPath.getAbsolutePath(), picUrl, pictureName, null);
		
		goodsObj.sizeTipPics.add(picFile.getAbsolutePath());

		SizeTipObject obj = new SizeTipObject();
		obj.pictureFileName = picFile.getAbsolutePath();
		obj.pictureUrl = picUrl;
		sizeTipList.add(obj);
	}

	private void getColorPicture(GoodsObject prodObj, String colorName) {
		for(String url:prodObj.dressOnPicsUrlList){
			if(url.indexOf("_" + colorName + "_")!=-1){
				prodObj.colorPicUrlList.add(url);
				return;
			}
		}
	}

	private void screenshotProductDetailDespAndSizeTable(GoodsObject obj) throws IOException {
		String fileNameFmt = "detail_%s.png";
		String fileName = String.format(fileNameFmt, obj.kataban);
		File despFile = new File(RightonUtil.getPictureSavePath(obj), fileName);
		if (!despFile.exists()) {
			WebDriver webDriver = WebDriverUtil.getWebDriver(obj.productUrl);
//			List<WebElement> eles = webDriver.findElements(By.cssSelector("a[class=\"nav-link\"]"));
//			for (WebElement e : eles) {
//				if (e.getText().equals("アイテム情報")) {
//					e.click();
//					break;
//				}
//			}
			List<WebElement> scrnShotEles = webDriver.findElements(By.cssSelector("div[class=\"staff-style-panel-body\"]"));
			if (!scrnShotEles.isEmpty()) {
				WebDriverUtil.screenShotV2(webDriver, scrnShotEles, despFile.getAbsolutePath(), null);
				obj.detailScreenShotPicFile = despFile.getAbsolutePath();
			}
			
			fileNameFmt = "sizeTip_%s.png";
			fileName = String.format(fileNameFmt, obj.kataban);
			despFile = new File(RightonUtil.getPictureSavePath(obj), fileName);

//			for (WebElement e : eles) {
//				if (e.getText().equals("サイズ情報")) {
//					e.click();
//					break;
//				}
//			}
			scrnShotEles = webDriver.findElements(By.cssSelector("div[class=\"product-item-info-size\"]"));
			if (!scrnShotEles.isEmpty()) {
				WebDriverUtil.screenShotV2(webDriver, scrnShotEles, despFile.getAbsolutePath(), null);
				obj.sizeTipScreenShotPicFile = despFile.getAbsolutePath();
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
