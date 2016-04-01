package com.walk_nie.taobao.mangazenkan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.walk_nie.taobao.util.TaobaoUtil;

public class MangazenkanProductParser {
	// change me!!
	double rate3 = 0.060;
	double rate2 = rate3 + 0.002;
	double rate1 = rate2 + 0.002;
	double rate =rate1 + 0.002;
	
	private final static double benefitRate = 0.08;

	String urlPrefix = "http://Mangazenkan.fcart.jp";
	List<String> visitedProductId = new ArrayList<String>();
	List<String> exceptProductId = new ArrayList<String>();
	{
		exceptProductId.add("10201718");// 水木しげる漫画大全集
		exceptProductId.add("10225251");// 弱虫ペダル スペシャルセット (全49冊)
		exceptProductId.add("10120490");// 弱虫ペダル
		exceptProductId.add("10120489");// 弱虫ペダル
		exceptProductId.add("10183342");// 弱虫ペダル
		exceptProductId.add("3659");// ベイビーステップ

		exceptProductId.add("10189937");// 
		
		exceptProductId.add("");// 
		exceptProductId.add("");// 
		exceptProductId.add("");// 
		exceptProductId.add("");// 
		exceptProductId.add("");// 
		exceptProductId.add("");// 
	}
	Map<String,String> titleJPCNMap = new HashMap<String,String>();{
		titleJPCNMap.put("名探偵コナン", "名侦探柯南");
		titleJPCNMap.put("ワンピース ONE PIECE", "海贼王");
		titleJPCNMap.put("", "");
		titleJPCNMap.put("", "");
		titleJPCNMap.put("", "");
		titleJPCNMap.put("", "");
		titleJPCNMap.put("", "");
		titleJPCNMap.put("", "");
		titleJPCNMap.put("", "");
		titleJPCNMap.put("", "");
	}

	public List<MangazenkanGoodsObject> scanItem() throws IOException {

		List<MangazenkanGoodsObject> goodsList = new ArrayList<MangazenkanGoodsObject>();
		String url = "";
		List<MangazenkanGoodsObject> scanRslt = null;
		// たった今これが売れました
		url = "http://www.mangazenkan.com/contents/new_sale.php?product_type=1";
		scanRslt = scanItemByRankNow(url);
		goodsList.addAll(scanRslt);
		
		// メディア化作品情報
		url = "http://www.mangazenkan.com/special/394.html";
		scanRslt = scanItemByMediarize(url);
		goodsList.addAll(scanRslt);

		// 2015年 年間ランキング1000(1-100)
		url = "http://www.mangazenkan.com/ranking/2015/12-100.html";
		scanRslt = scanItemByRank2015(url);
		goodsList.addAll(scanRslt);
		
		// 2015年 年間ランキング1000(101-200)
		url = "http://www.mangazenkan.com/special/1442.html";
		scanRslt = scanItemByRank2015(url);
		goodsList.addAll(scanRslt);
		
		// 2015年 年間ランキング1000(201-300)
		url = "http://www.mangazenkan.com/special/1443.html";
		scanRslt = scanItemByRank2015(url);
		goodsList.addAll(scanRslt);
		
		return goodsList;
	}

	private List<MangazenkanGoodsObject> scanItemByRankNow(String url) throws ClientProtocolException, IOException {
		List<MangazenkanGoodsObject> goodsList = new ArrayList<MangazenkanGoodsObject>();
		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
		Elements itemEls = doc.select("div#content").select("li").select("a");
		for(Element itemEl : itemEls){
			String href = itemEl.attr("href");
			MangazenkanGoodsObject book = parseBook(href);
			if (book != null) {
				goodsList.add(book);
				//return goodsList;
			}
		}
		return goodsList;
	}

	private List<MangazenkanGoodsObject> scanItemByMediarize(String url) throws ClientProtocolException, IOException {
		List<MangazenkanGoodsObject> goodsList = new ArrayList<MangazenkanGoodsObject>();
		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
		Elements itemEls = doc.select("table.book-cover-grid").select("td").select("a");
		for(Element itemEl : itemEls){
			String href = itemEl.attr("href");
			MangazenkanGoodsObject book = parseBook(href);
			if(book != null) goodsList.add(book);
		}
		return goodsList;
	}

	public List<MangazenkanGoodsObject> scanItemByRank2015(String url) throws IOException {
		List<MangazenkanGoodsObject> goodsList = new ArrayList<MangazenkanGoodsObject>();

		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
		Elements itemEls = doc.select("table.book-cover-grid").select("td").select("a");
		for(Element itemEl : itemEls){
			String href = itemEl.attr("href");
			if(!href.startsWith("http://www.mangazenkan.com/item")){
				continue;
			}
			MangazenkanGoodsObject book = parseBook(href);
			if(book != null) goodsList.add(book);
		}
		return goodsList;
	}

	protected MangazenkanGoodsObject parseBook(String href) throws ClientProtocolException, IOException {
		String productId = href.substring(href.lastIndexOf("/"));
		if (productId.lastIndexOf(".") < 0) {
			return null;
		}
		productId = productId.substring(1,productId.lastIndexOf("."));
		if(visitedProductId.contains(productId) || exceptProductId.contains(productId)){
			return null;
		}
		
//		if(!productId.equals("10328334")){
//			return null;
//		}
		
		visitedProductId.add(productId);
		
		MangazenkanGoodsObject book = new MangazenkanGoodsObject();
		Document bookDoc = TaobaoUtil.urlToDocumentByUTF8(href);
		
		Element mainEl = bookDoc.select("div.main_col_01").get(0);
		
		book.productId = productId;
		
		Elements breakEles = mainEl.select("div.breadcrumb").select("a");
		book.title = translateTitle(breakEles.get(breakEles.size()-1).text());
		
		Elements nameEles = mainEl.select("div.hd01b_second").select("span[itemprop=name");
		book.name = nameEles.text().replace(" 【ライトノベル】", "");
		if(book.name.indexOf("全巻") !=-1
				|| book.name.indexOf("全") !=-1) book.finished = true;
		book.name =translateTitle(book.name );
		
		Element mainSpecTbl = mainEl.select("table.main_spec").get(0);
		Elements specPs = mainSpecTbl.select("tr");
		if(specPs.size() > 4){
			int idx = 0;
			book.brand = specPs.get(idx++).select("td").get(1).text();
			book.author = specPs.get(idx++).select("td").get(1).text();
			book.latestPublishDate = specPs.get(idx++).select("td").get(1).text();
			book.bookPublishType = specPs.get(idx++).select("td").get(1).text().replace("▶︎", "").replace("版型とは", "");
			book.categoryName = specPs.get(idx++).select("td").get(1).text();
		}
		//あらすじ
		Elements despEles = mainEl.select("p[itemprop=description");
		if(!despEles.isEmpty()){
			book.detailDisp = despEles.text();
		}
		//全巻表紙画像の一覧
		Elements picsEls = mainEl.select("div.float_clear").select("div.main_list").select("img");
		if(!picsEls.isEmpty()){
			for(Element picEl : picsEls ){
				String src = picEl.attr("src");
				book.pictureList.add(src);
			}
		}else{
			String src = mainEl.select("img#cover-flip-img-1").attr("src");
			book.pictureList.add(src);
		}
		
		book.volCount = book.pictureList.size();

		Element cartEle = bookDoc.select("div#cartarea").get(0);
		book.priceOrg = cartEle.select("span.item-price").text();
		
		calcWeight(book);
		
		convertToCNY(book);
		
		return book;
	}

	private String translateTitle(String name) {
		name = name.replace("新書版", "新书版");
		name = name.replace("文庫版", "文库版");
		name = name.replace("新装版", "新装版");
		return name;
	}

	private void convertToCNY(MangazenkanGoodsObject item) {

		String price = item.priceOrg;
		price = price.replace(",", "");
		price = price.replace("￥", "");
		item.price = price;
		int priceI = Integer.parseInt(price);
		item.priceCNY =String.valueOf(Math.round((priceI + priceI * benefitRate) * getRate(item)));
		
		int emsFee = TaobaoUtil.getEmsFee(item.totalWeight);
		item.priceCNYEMS =String.valueOf(Math.round((emsFee + emsFee * benefitRate) * getRate(item)));
		
		int salFee = TaobaoUtil.getSalFee(item.totalWeight);
		item.priceCNYSAL =String.valueOf(Math.round((salFee + salFee * benefitRate) * getRate(item)));
		
		int seaFee = TaobaoUtil.getSeaFee(item.totalWeight);
		item.priceCNYSEA=String.valueOf(Math.round((seaFee + seaFee * benefitRate) * getRate(item)));
	}
	
	private double getRate(MangazenkanGoodsObject item) {
		int priceI = Integer.parseInt(item.price);
		if(priceI > 30000){
			return rate3;
		}
		if(priceI > 20000){
			return rate2;
		}
		if(priceI > 10000){
			return rate1;
		}
		return rate;
	}

	private void calcWeight(MangazenkanGoodsObject book) {

		int bookWeight = 0;
		if("新書版".equals(book.bookPublishType)){
			bookWeight = 190;
		} else if("B6版".equals(book.bookPublishType)){
			bookWeight = 210;
		} else if("文庫版".equals(book.bookPublishType)){
			bookWeight = 280;
		}else{
			bookWeight = 350;
		}
		int extraWeight = 0;
		if(book.volCount == 1) {
			extraWeight = 100;
		}else if(book.volCount <= 5) {
			extraWeight = 200;
		}else if(book.volCount <= 10) {
			extraWeight = 300;
		}else{
			extraWeight = 500;
		}
		book.totalWeight = bookWeight * book.volCount + extraWeight;
	}

}
