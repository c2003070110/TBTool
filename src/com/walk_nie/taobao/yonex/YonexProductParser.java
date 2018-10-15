package com.walk_nie.taobao.yonex.badminton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.TaobaoUtil;

public class YonexProductParser extends BaseBaobeiParser {
	
	String urlPrefix = "http://www.yonex.co.jp";
	
	boolean detailDisp = true;
	// 1:badminton racquets;2:badminton shoes;3:tennis racquets;4:tennis shoes;
	private int categoryType = 0;
	
	public List<GoodsObject> scanItem() throws IOException {
		List<GoodsObject> goodsList = new ArrayList<GoodsObject>();
		if (categoryType == 1) {
			String url = "http://www.yonex.co.jp/badminton/racquets/";
			categoryType = 1;
			List<GoodsObject> list = scanRacquets(url);
			goodsList.addAll(list);
		} else if (categoryType == 2) {
			String url = "http://www.yonex.co.jp/badminton/shoes/";
			List<GoodsObject> list = scanRacquets(url);
			goodsList.addAll(list);
		} else if (categoryType == 3) {
			String url = "http://www.yonex.co.jp/tennis/racquets/";
			List<GoodsObject> list = scanRacquets(url);
			goodsList.addAll(list);
		} else if (categoryType == 4) {
			String url = "http://www.yonex.co.jp/tennis/shoes/";
			List<GoodsObject> list = scanRacquets(url);
			goodsList.addAll(list);
		} else {
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
	protected List<GoodsObject> scanRacquets(String url) throws ClientProtocolException, IOException{
		List<GoodsObject> goodsList = new ArrayList<GoodsObject>();
		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
		Elements prodEles  = doc.select("ul.prodList").select("li");
		//int i = 0;
		for(Element prodEle :prodEles){
			//if(i>12)break;
			//i++;
			GoodsObject obj = new GoodsObject();
			obj.categoryType = categoryType;
			
			String pUrl  = urlPrefix + prodEle.select("a").attr("href");
			Document pDoc = TaobaoUtil.urlToDocumentByUTF8(pUrl);
			Element contentEle = pDoc.select("div.contents").get(0);
			obj.titleEN = contentEle.select("h3.prodName").text(); 
			obj.titleJP = contentEle.select("h3.prodNameKana").select("span.name").text(); 
			obj.kataban =contentEle.select("h3.prodNameKana").select("span.kataban").text();
			
			// 官方价格不可取。rakuten调查后 定价
			obj.price = "XXXX";
			
			obj.productPlace = translateProductPlace(contentEle.select("span.seisan").text());

			// rap 
//			if("中国製".equals(obj.productPlace) || "台湾製".equals(obj.productPlace)){
//				continue;
//			}

			Elements els = contentEle.select("ul.rank");
			String rank = "";
			if (els.size() == 2) {
				rank = processRacquetForRank(obj, els.get(0));
				rank += processRacquetForRank(obj, els.get(0));
			}
			obj.title = translateTitle(obj);
			
			String spec = contentEle.select("table.specTbl").outerHtml();
			obj.detailDisp = rank + spec.replaceAll("\n", "");
			
			obj.pictureList = processRacquetForPicture(obj, contentEle);
			
			goodsList.add(obj);
		}
		return goodsList;
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
		List<String> picList = new ArrayList<String>();
		Elements picEles = contentEle.select("img.cloudzoom-gallery");
		for(Element picEle:picEles){
			String picUrl = urlPrefix + picEle.attr("src");
			if(!picList.contains(picUrl)){
				picList.add(picUrl);
			}
		}
		return picList;
	}
	private String processRacquetForRank(GoodsObject obj, Element contentEle) {
		StringBuffer sb = new StringBuffer();
		sb.append("<p>");
		sb.append(contentEle.select("li").get(0).text());
		sb.append(":");
		sb.append(contentEle.select("li").get(1).text());
		sb.append(";");
		sb.append(contentEle.select("li").get(2).text());
		sb.append(";");
		sb.append(contentEle.select("li").get(3).text());
		sb.append(";");
		sb.append("</p>");
		return sb.toString();
	}
//	protected List<GoodsObject> scanStrings() throws ClientProtocolException, IOException{
//		List<GoodsObject> goodsList = new ArrayList<GoodsObject>();
//		String url = "http://www.yonex.co.jp/badminton/strings/";
//		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
//		Elements prodEles  = doc.select("div.prodArchive");
//		for(Element prodEle :prodEles){
//			GoodsObject obj = new GoodsObject();
//			obj.titleJP = prodEle.select("span.name_jp").text();
//			obj.titleEN = prodEle.select("span.name_en").text();
//			obj.kataban = prodEle.select("span.kataban").text();
//			obj.title = translateTitle(obj);
//			obj.priceOrg = prodEle.select("h4.prodPrice").text()
//					.replace("日本製", "")
//					.replace("台湾製", "")
//					.replace("＋税", "")
//					.replace("¥", "")
//					.replace(",", "")
//					.replace(" ", "");
//			obj.price = addEMSFee(obj);
//			obj.productPlace = prodEle.select("span.seisan").text();
//			obj.detailDisp = prodEle.select("table").get(0).outerHtml();
//			Elements picEles = prodEle.select("li.pic");
//			List<String> picList = new ArrayList<String>();
//			for(Element picEle:picEles){
//				String picUrl = urlPrefix + picEle.select("a").attr("href");
//				if(!picList.contains(picUrl)){
//					picList.add(picUrl);
//				}
//			}
//			obj.pictureList = picList;
//			
//			goodsList.add(obj);
//		}
//		return goodsList;
//	}
	private String translateTitle(GoodsObject goodsObj) {
		if(goodsObj.categoryType == 1){
			return "羽毛球拍/" + goodsObj.titleEN + "/" + goodsObj.kataban+ "/" + goodsObj.productPlace;
		}else if(goodsObj.categoryType == 2){
			return "羽毛球鞋/" + goodsObj.titleEN + "/" + goodsObj.kataban + "/" + goodsObj.productPlace;
		}else if(goodsObj.categoryType == 3){
			return "网球球拍/" + goodsObj.titleEN + "/" + goodsObj.kataban+ "/" + goodsObj.productPlace;
		}else if(goodsObj.categoryType == 4){
			return "网球鞋/" + goodsObj.titleEN + "/" + goodsObj.kataban + "/" + goodsObj.productPlace;
		}else {
			return "";
		}
		//return "羽毛球线羽线/" + goodsObj.kataban + "/" + goodsObj.titleEN;
	}

	public YonexProductParser setCategoryType(int categoryType){
		this.categoryType = categoryType;
		return this;
	}
}
