package com.walk_nie.taobao.yonex.badminton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.walk_nie.taobao.util.TaobaoUtil;

public class YonexBadProductParser {
	// change me!!
	double rate = 0.060;
	private final static double benefitRate = 0.001;
	
	String urlPrefix = "http://www.yonex.co.jp";
	
	// TODO picture
	//boolean downloadPicture = false;
	boolean detailDisp = true;
	
	public List<GoodsObject> scanItem() throws IOException {

		//List<GoodsObject> goodsList = scanStrings();
		List<GoodsObject> goodsList = scanRacquets();
		
		return goodsList;
	}
	protected List<GoodsObject> scanRacquets() throws ClientProtocolException, IOException{
		List<GoodsObject> goodsList = new ArrayList<GoodsObject>();
		//String url = "http://www.yonex.co.jp/badminton/racquets/";
		//String url = "http://www.yonex.co.jp/badminton/shoes/";
		//String url = "http://www.yonex.co.jp/tennis/racquets/";
		String url = "http://www.yonex.co.jp/tennis/shoes/";
		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
		Elements prodEles  = doc.select("ul.prodList").select("li");
		//int i = 0;
		for(Element prodEle :prodEles){
			//if(i>12)break;
			//i++;
			GoodsObject obj = new GoodsObject();
			String pUrl  = urlPrefix + prodEle.select("a").attr("href");
			Document pDoc = TaobaoUtil.urlToDocumentByUTF8(pUrl);
			Element contentEle = pDoc.select("div.contents").get(0);
			obj.titleEN = contentEle.select("h3.prodName").text(); 
			obj.titleJP = contentEle.select("h3.prodNameKana").select("span.name").text(); 
			obj.kataban =contentEle.select("h3.prodNameKana").select("span.kataban").text();
			
			obj.price = "XXXX";
			
			obj.productPlace = contentEle.select("span.seisan").text();

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
			obj.detailDisp = rank + spec;
			
			obj.pictureList = processRacquetForPicture(obj, contentEle);
			
			goodsList.add(obj);
		}
		return goodsList;
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
	protected List<GoodsObject> scanStrings() throws ClientProtocolException, IOException{
		List<GoodsObject> goodsList = new ArrayList<GoodsObject>();
		String url = "http://www.yonex.co.jp/badminton/strings/";
		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
		Elements prodEles  = doc.select("div.prodArchive");
		for(Element prodEle :prodEles){
			GoodsObject obj = new GoodsObject();
			obj.titleJP = prodEle.select("span.name_jp").text();
			obj.titleEN = prodEle.select("span.name_en").text();
			obj.kataban = prodEle.select("span.kataban").text();
			obj.title = translateTitle(obj);
			obj.priceOrg = prodEle.select("h4.prodPrice").text()
					.replace("日本製", "")
					.replace("台湾製", "")
					.replace("＋税", "")
					.replace("¥", "")
					.replace(",", "")
					.replace(" ", "");
			obj.price = convertToCNY(obj);
			obj.productPlace = prodEle.select("span.seisan").text();
			obj.detailDisp = prodEle.select("table").get(0).outerHtml();
			Elements picEles = prodEle.select("li.pic");
			List<String> picList = new ArrayList<String>();
			for(Element picEle:picEles){
				String picUrl = urlPrefix + picEle.select("a").attr("href");
				if(!picList.contains(picUrl)){
					picList.add(picUrl);
				}
			}
			obj.pictureList = picList;
			
			goodsList.add(obj);
		}
		return goodsList;
	}
	private String translateTitle(GoodsObject goodsObj) {
		
		//return "羽毛球线羽线/" + goodsObj.kataban + "/" + goodsObj.titleEN;
		//return "羽毛球拍/" + goodsObj.titleEN+ "/" + goodsObj.productPlace;
		//return "网球球拍/" + goodsObj.titleEN+ "/" + goodsObj.productPlace;
		//return "羽毛球鞋/" + goodsObj.titleEN + "/" + goodsObj.kataban;
		return "网球鞋/" + goodsObj.titleEN + "/" + goodsObj.kataban;
	}
	

	private String convertToCNY(GoodsObject item) {
		try {
			int price = Integer.parseInt(item.priceOrg);
			int emsFee = 0;
//			int weight = Integer.parseInt(item.weight);
//			if (weight < 1000) {
//				emsFee= 1800;
//			} else if (weight < 2000) {
//				emsFee= 3000;
//			} else if (weight < 3000) {
//				emsFee= 4000;
//			} else if (weight < 4000) {
//				emsFee= 5000;
//			} else {
//				emsFee= 6000;
//			}
			double priceCNY = (price + emsFee) * rate;
			priceCNY = priceCNY + priceCNY * benefitRate;
			return String.valueOf(Math.round(priceCNY));
		} catch (Exception ex) {
			ex.printStackTrace();
			return "XXXXXX";
		}
	}

}
