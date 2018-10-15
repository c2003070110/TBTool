package com.walk_nie.taobao.yonex;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;

public class YonexProductParser extends BaseBaobeiParser {
	
	String urlPrefix = "http://www.yonex.co.jp";
    public static String rootPathName = "out/yonex/";
	
	boolean detailDisp = true;
	// 1:badminton racquets;2:badminton shoes;3:tennis racquets;4:tennis shoes;
	private int categoryType = 0;
	
	public List<GoodsObject> scanItem() throws IOException {
		List<GoodsObject> goodsList = Lists.newArrayList();
		if (categoryType == 1) {
			// 1:badminton racquets;
			String url = "http://www.yonex.co.jp/badminton/racquets/";
			categoryType = 1;
			List<GoodsObject> list = scanRacquets(url);
			goodsList.addAll(list);
		} else if (categoryType == 2) {
			// 2:badminton shoes;
			String url = "http://www.yonex.co.jp/badminton/shoes/";
			List<GoodsObject> list = scanRacquets(url);
			goodsList.addAll(list);
		} else if (categoryType == 3) {
			// 3:tennis racquets;
			String url = "http://www.yonex.co.jp/tennis/racquets/";
			List<GoodsObject> list = scanRacquets(url);
			goodsList.addAll(list);
		} else if (categoryType == 4) {
			// 4:tennis shoes;
			String url = "http://www.yonex.co.jp/tennis/shoes/";
			List<GoodsObject> list = scanRacquets(url);
			goodsList.addAll(list);
		} else {
			// all
			String url = "http://www.yonex.co.jp/badminton/racquets/";
			categoryType = 1;
			List<GoodsObject> list = scanRacquets(url);
			goodsList.addAll(list);

			url = "http://www.yonex.co.jp/badminton/shoes/";
			categoryType = 2;
			list = scanRacquets(url);
			goodsList.addAll(list);

			url = "http://www.yonex.co.jp/tennis/racquets/";
			categoryType = 3;
			list = scanRacquets(url);
			goodsList.addAll(list);

			url = "http://www.yonex.co.jp/tennis/shoes/";
			categoryType = 4;
			list = scanRacquets(url);
			goodsList.addAll(list);
		}
		return goodsList;
	}
	protected List<GoodsObject> scanRacquets(String url) throws  IOException{
		List<GoodsObject> goodsList = Lists.newArrayList();
		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
		Elements prodEles  = doc.select("ul.prodList").select("li");
		//int i = 0;
		for(Element prodEle :prodEles){
			//if(i>12)break;
			//i++;
			GoodsObject obj = new GoodsObject();
			obj.categoryType = categoryType;
			
			String pUrl  = urlPrefix + prodEle.select("a").attr("href");
			obj.productUrl = pUrl;
			Document productDoc = TaobaoUtil.urlToDocumentByUTF8(pUrl);
			Element contentEle = productDoc.select("div.contents").get(0);
			obj.title = contentEle.select("h3.prodName").text(); 
			obj.titleJP = contentEle.select("h3.prodNameKana").select("span.name").text(); 
			obj.kataban =contentEle.select("h3.prodNameKana").select("span.kataban").text();
			
			// 官方价格不可取。rakuten调查后 定价
			obj.price = "XXXX";
			//obj.title = translateTitle(obj);
			
			obj.producePlace = translateProductPlace(contentEle.select("span.seisan").text());

			// rank To Object!
			Elements els = contentEle.select("ul.rank");
			if (els.size() == 2) {
				processRacquetForRank(obj, els.get(0));
				processRacquetForRank(obj, els.get(1));
			}
			
			// specArea to picture!
			screenshotProductDetailDesp(obj);
			
			obj.pictureList = processRacquetForPicture(obj, contentEle);
			
			goodsList.add(obj);
		}
		return goodsList;
	}
	
	private void screenshotProductDetailDesp(GoodsObject obj) throws IOException {
		String fileNameFmt = "detail_%s.png";
		String fileName = String.format(fileNameFmt, obj.kataban);
		File despFile = new File(rootPathName + "/" + getCategoryTypeName(obj.categoryType), fileName);
		if (!despFile.exists()) {
			WebDriver webDriver = WebDriverUtil.getWebDriver(obj.productUrl);
			List<WebElement> ele = webDriver.findElements(By.className("specArea"));

			WebDriverUtil.screenShotV2(webDriver, ele, despFile.getAbsolutePath(), null);
		}
	}
	
	private String getCategoryTypeName(int categoryType) {
		if (categoryType == 1) {
			return "badminRacquets";
		} else if (categoryType == 2) {
			return "badminShoes";
		} else if (categoryType == 3) {
			return "tennisRacquets";
		} else if (categoryType == 4) {
			return "tennisShoes";
		}
		return "";
	}
	private String translateProductPlace(String text) {
		if(text.equals("ベトナム製")){
			return "越南";
		}
		if(text.equals("中国製")){
			return "中国";
		}
		if(text.equals("日本製")){
			return "日本";
		}
		if(text.equals("インドネシア製")){
			return "印尼";
		}
		if(text.equals("台湾製")){
			return "台湾";
		}
		return text;
	}
	private List<String> processRacquetForPicture(GoodsObject obj,
			Element contentEle) {
		List<String> picList = Lists.newArrayList();
		Elements picEles = contentEle.select("img.cloudzoom-gallery");
		for(Element picEle:picEles){
			String picUrl = urlPrefix + picEle.attr("src");
			if(!picList.contains(picUrl)){
				picList.add(picUrl);
			}
		}
		return picList;
	}

	private void processRacquetForRank(GoodsObject obj, Element contentEle) {
		GoodsRankObject rankObj = new GoodsRankObject();
		Elements els = contentEle.select("li");
		rankObj.rankName = els.get(0).text();
		String key = els.get(1).text();
		String val = els.get(1).select("span").text();
		rankObj.leve1 = key.replace(val, "");
		rankObj.leve1Val = val;
		key = els.get(2).text();
		val = els.get(2).select("span").text();
		rankObj.leve2 = key.replace(val, "");
		rankObj.leve2Val = val;
		key = els.get(3).text();
		val = els.get(3).select("span").text();
		rankObj.leve3 = key.replace(val, "");
		rankObj.leve3Val = val;
		obj.rankList.add(rankObj);
	}

	public YonexProductParser setCategoryType(int categoryType){
		this.categoryType = categoryType;
		return this;
	}
}
