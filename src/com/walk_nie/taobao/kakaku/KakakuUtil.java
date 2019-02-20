package com.walk_nie.taobao.kakaku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.walk_nie.taobao.kakaku.taobao.KakakuTaobaoPriceObject;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;


public class KakakuUtil {
	public static String kakakuUrlPrefix = "http://kakaku.com/item/";
	
	public static PriceObject parseItemPriceByShop(Element element) {
		Elements tdElements = element.parent().select("td");
		PriceObject priceObj = new PriceObject();
		Elements priceElements = tdElements.get(1).select("p");
		priceObj.price  = (int) beatufyPrice(priceElements.get(0).text());
		Elements pointElement = priceElements.select(".sPoint");
		if(!pointElement.isEmpty()){
			priceObj.tFee = (int)beatufyPrice(pointElement.get(0).text());
		}else{
			priceObj.point = 0;
		}
		priceObj.priceGap = priceElements.get(1).text();
		priceObj.isStock= tdElements.get(3).text().equals("有");
		String tFeeStr = tdElements.get(2).select("p").text();
		if(tFeeStr.indexOf("無料") >= 0){
			priceObj.tFee = 0;
		}else{
			priceObj.tFee = (int)beatufyPrice(tFeeStr);
		}
		if (tdElements.get(3).select("p").text().indexOf("問合せ") >= 0) {
			priceObj.isStock = false;
		}else{
			priceObj.isStock = true;
		}
		return priceObj;
		
	}

	public static void parseItemPrice(Document doc, KakakuObject obj) throws  IOException {

		double minPrice = beatufyPrice(doc.select("#priceBox")
				.select("#minPrice").select("a").text());
		if (minPrice == 0 || minPrice == -99999) {
			obj.priceMin = null;
			return;
		}
		Elements shopNameList = doc.select("table").select(".shopnameL");
		if(shopNameList.isEmpty()){
			obj.priceMin = null;
			return ;
		}
//		if(obj.id.toUpperCase().startsWith("J")){
//			return;
//		}
		String itemUrl = KakakuUtil.kakakuUrlPrefix + obj.id + "/?page=2" ;
		Document docPage = KakakuUtil.urlToDocumentKakaku(itemUrl);
		Elements shopNameListPage = docPage.select("table").select(".shopnameL");
		if(!shopNameListPage.isEmpty()){
			shopNameList.addAll(shopNameListPage);
		}
		itemUrl = KakakuUtil.kakakuUrlPrefix + obj.id + "/?page=3" ;
		docPage = KakakuUtil.urlToDocumentKakaku(itemUrl);
		shopNameListPage = docPage.select("table").select(".shopnameL");
		if(!shopNameListPage.isEmpty()){
			shopNameList.addAll(shopNameListPage);
		}
		itemUrl = KakakuUtil.kakakuUrlPrefix + obj.id + "/?page=4" ;
		docPage = KakakuUtil.urlToDocumentKakaku(itemUrl);
		shopNameListPage = docPage.select("table").select(".shopnameL");
		if(!shopNameListPage.isEmpty()){
			shopNameList.addAll(shopNameListPage);
		}
		
		obj.priceMin = parseItemPriceByShop(shopNameList.get(0));
		for (Element element : shopNameList) {
			String shopName = element.select(".wordwrapShop").text();
			if ("ヨドバシ.com".equals(shopName)) {
				obj.priceYodobashi = parseItemPriceByShop(element);
			}
			if ("ノジマオンライン".equals(shopName)) {
				obj.priceNojima = parseItemPriceByShop(element);
			}
			if ("ヤマダウェブコム".equals(shopName)) {
				obj.priceYamada = parseItemPriceByShop(element);
			}
			if ("ビックカメラ.com".equals(shopName)) {
				obj.priceBiccamera = parseItemPriceByShop(element);
			}
			if ("eイヤホン".equals(shopName)) {
				obj.priceEEarPhone = parseItemPriceByShop(element);
			}
			if ("Amazon.co.jp".equals(shopName)) {
				obj.priceAmazon = parseItemPriceByShop(element);
			}
			if (StringUtils.isEmpty(obj.sku)) {
				//obj.sku = findItemSku(doc);
			}
		}
		String priceUp = doc.select("#priceBox").select(".priceRate")
				.select(".priceUp").text();
		String priceDown = doc.select("#priceBox").select(".priceRate")
				.select(".priceDown").text();
		String priceWave = "".equals(priceUp) ? priceDown : priceUp;
		priceWave = priceWave.replaceAll("円", "");
		priceWave = priceWave.replaceAll("↓", "");
		priceWave = priceWave.replaceAll("↑", "");
		priceWave = priceWave.replaceAll(",", "");
		obj.priceWave = priceWave;
	}

	public static double beatufyPrice(String price) {
		if (price == null || price.equals("")) {
			return -99999;
		}
		String newPrice = price.replaceAll("¥", "");
		newPrice = newPrice.replaceAll("¥", "");
		newPrice = newPrice.replaceAll("～", "");
		newPrice = newPrice.replaceAll("〜", "");
		newPrice = newPrice.replaceAll(",", "");
		newPrice = newPrice.replaceAll(" （最安）", "");
		newPrice = newPrice.replaceAll("相当", "");

		return Double.parseDouble(newPrice);
	}

	public static void parseItemMaker(Document doc, KakakuObject obj) {
		String itemId = doc.select("div").select(".hslice").attr("id");
		obj.id = itemId;
		obj.itemMaker = translateMaker(doc.select(".makerLabel").text());
		String itemType = doc.select("#titleBox").select(".boxL")
				.text();
		itemType = com.walk_nie.taobao.kakaku.explore.earphone.EarphoneUtil.translateType(itemType);
		itemType = com.walk_nie.taobao.kakaku.explore.shaver.ShaverUtil.translateType(itemType);
		obj.itemType = itemType;
		Elements elements = doc.select("div").select("#priceBox")
				.select("#subInfoRow2").select("li");
		for (Element e : elements) {
			if (e.text().startsWith("登録日：")) {
				obj.sellStartDate = e.text().replace("登録日：", "");
			}
		}
	}

	public static String translateMaker(String maker) {
		maker = maker.replaceAll("　", "");
		if (maker.equals("オーディオテクニカ")) {
			return "Audio Technica/铁三角";
		} else if (maker.equals("ゼンハイザー")) {
			return "森海塞尔";
		} else if (maker.equals("パナソニック")) {
			return "Panasonic/松下";
		} else if (maker.equals("JVC")) {
			return "JVC/杰伟世";
		} else if (maker.equals("パイオニア")) {
			return "Pioneer/先锋";
		} else if (maker.equals("ヤマハ")) {
			return "Yamaha/雅马哈";
		} else if (maker.equals("エレコム")) {
			return "Elemcom/宜丽客";
		} else if (maker.equals("AKG")) {
			return "AKG/爱科技";
		} else if (maker.equals("フィリップス")) {
			return "Philips/飞利浦";
		} else if (maker.equalsIgnoreCase("DENON")) {
			return "Denon/天龙";
		} else if (maker.equals("ONKYO")) {
			return "Onkyo/安桥";
		} else if (maker.equals("ブラウン")) {
			return "Braun/博朗";
		} else if (maker.equals("SONY")) {
			return "Sony/索尼";
		} else if (maker.equals("")) {
			return "";
		} else {
			return maker;
		}
	}

	public static String findItemSku(Document doc) {
		Elements shopNameList = doc.select("table").select(".shopnameL");
		for (Element element : shopNameList) {
			Elements as = element.parent().select("a");
			for (Element e : as) {
				try {
					String href = URLDecoder.decode(e.attr("href"), "utf-8");
					if (StringUtil.isBlank(href))
						continue;
					if (href.indexOf("JAN_CODE") > 0) {
						String ur11 = href.substring(href.indexOf("JAN_CODE"));
						ur11 = URLDecoder.decode(ur11, "utf-8");
						String[] params = ur11.split("&");
						return params[0].substring("JAN_CODE".length() + 3);
					}
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
		}
		return "";
	}

	public static Document urlToDocumentKakaku(String url)
			throws  IOException {
		return TaobaoUtil.urlToDocument(url, "shift_jis");
	}

	public static void parseItemPicture(Document doc, KakakuObject obj) {
		String urlFmt1 = "http://img1.kakaku.k-img.com/images/productimage/fullscale/%s.jpg";
		obj.pictureUrlList.add(String.format(urlFmt1, obj.id));
		obj.pictureNameList.add(obj.id+"-0");
		String urlFmt2 = "http://img1.kakaku.k-img.com/images/productimage/fullscale/%s_%04d.jpg";
		Elements elements = doc.select("div").select("#imgBox").select("img");
        for (int i = 1; i < elements.size(); i++) {
            obj.pictureUrlList.add(String.format(urlFmt2, obj.id, i));
            obj.pictureNameList.add(obj.id + "-" + i);
        }
	}

	public static String readInBaobeiMiaoshuTemplate(KakakuObject item,String miaoshuTemplateFile) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			File file = new File(miaoshuTemplateFile);
			String str = null;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
		} finally {
			if (br != null)
				br.close();
		}
		String specUrl = !StringUtil.isBlank(item.spec.productInfoUrl)?item.spec.productInfoUrl:item.spec.specInfoUrl;
		String productInfo = "";
		if (!StringUtil.isBlank(specUrl)) {
			productInfo = String.format("以官网公布为准(日文): %s",
					item.spec.productInfoUrl);
		}

		return sb.toString().replace("$product_url$", productInfo);
	}

//	public static int getEMSFee(int weight) {
//		if (weight == 0) {
//			return 99999;
//		}
//		if ((weight) < 250) {
//			return 900;
//		}
//		if ((weight) < 450) {
//			return 1100;
//		}
//		if ((weight) < 550) {
//			return 1240;
//		}
//		if ((weight) < 650) {
//			return 1380;
//		}
//		if ((weight) < 750) {
//			return 1520;
//		}
//		if ((weight) < 850) {
//			return 1660;
//		}
//		if ((weight) < 950) {
//			return 1800;
//		}
//		if ((weight) < 1150) {
//			return 2100;
//		}
//		if ((weight) < 1350) {
//			return 2400;
//		}
//		if ((weight) < 1600) {
//			return 2700;
//		}
//		if ((weight) < 1850) {
//			return 3000;
//		}
//		if ((weight) < 2350) {
//			return 3500;
//		}
//		if ((weight) < 2850) {
//			return 4000;
//		}
//		return 9999;
//	}
	public static String convertColor(String src) {
		// no nessesary to convert color!
		if (!"".equals(src)) {
			return src;
		}
		if ("レッド".equals(src)) {
			return "红色";
		} else if ("ブラック".equals(src)) {
			return "黑色";
		} else if ("ピンク".equals(src)) {
			return "粉红色";
		} else if ("ネイビーイエロー".equals(src)) {
			return "藏青色";
		} else if ("ホワイト".equals(src)) {
			return "白色";
		} else if ("ブルー".equals(src)) {
			return "蓝色";
		} else if ("カモフラージュ".equals(src)) {
			return "迷彩色";
		} else if ("ライトピンク".equals(src)) {
			return "浅粉色";
		} else if ("ライトブルー".equals(src)) {
			return "浅蓝色";
		} else if ("レオパード".equals(src)) {
			return "豹纹";
		} else if ("ライトパープル".equals(src)) {
			return "淡紫";
		} else if ("イエロー".equals(src)) {
			return "黄";
		} else if ("イエローピンク".equals(src)) {
			return "粉色";
		} else if ("オレンジ".equals(src)) {
			return "橙色";
		} else if ("カーキ".equals(src)) {
			return "卡其色";
		} else if ("ガンメタリック".equals(src)) {
			return "黑金色";
		} else if ("グリーン".equals(src)) {
			return "绿色";
		} else if ("ゴールド".equals(src)) {
			return "金色";
		} else if ("シルバー".equals(src)) {
			return "银白色";
		} else if ("パープル".equals(src)) {
			return "紫色";
		} else if ("ブラウン".equals(src)) {
			return "棕色";
		} else if ("ネイビー".equals(src)) {
			return "海蓝";
		} else if ("ブラックレッド".equals(src)) {
			return "黑红";
		} else if ("ライトグリーン".equals(src)) {
			return "浅绿";
		} else if ("ホワイトクレイジー".equals(src)) {
			return "白色";
		} else if ("ブラッククレイジー".equals(src)) {
			return "黑";
		} else if ("ブラックグリーン".equals(src)) {
			return "黑绿";
		} else if ("クレイジー".equals(src)) {
			return "灰色";
		} else if ("ブラックゴールド".equals(src)) {
			return "黑金";
		} else if ("ピンクパープル".equals(src)) {
			return "粉紫";
		} else if ("グレー".equals(src)) {
			return "灰色";
		} else if ("ブラックピンク".equals(src)) {
			return "黑粉";
		} else if ("ホワイトピンク".equals(src)) {
			return "白粉";
		} else if ("".equals(src)) {
			return "";
		}
		return src;
	}

	public static int getPriceForOther(PriceObject priceObj) {
		int price = priceObj.price;
		int tenPoint =(int)( price * 0.1);
		if(priceObj.point <= tenPoint){
			price += price * 0.1;
		}
		if (priceObj.tFee != 0) {
			price += priceObj.tFee;
		}
		return price;
	}


	public static void addTaobaoInfo(KakakuObject item) throws Exception {
		String taobaoUrl = "http://s.taobao.com/search?sort=default&q=%s";
		if (item.itemType.indexOf("[") > 0)
			return;
		String itemMaker= "";
		if(item.itemMaker.indexOf("/")>0){
			itemMaker = item.itemMaker.substring(0,item.itemMaker.indexOf("/"));
		}
		Document doc = WebDriverUtil.urlToDocumentByWebDriver(String.format(taobaoUrl, itemMaker+" "+item.itemType));
		Elements noquery = doc.select("div").select(".combobar-noquery");
		if (!noquery.isEmpty())
			return;
		Elements elements = doc.select("div").select("#mainsrp-itemlist")
				.select("#J_itemlistCont");
		if (elements.isEmpty())
			return;
		for (int i = 0; i < elements.get(0).children().size(); i++) {
			Element element = elements.get(0).children().get(i);
			KakakuTaobaoPriceObject taobao = new KakakuTaobaoPriceObject();
			taobao.kakakuId = item.id;
			taobao.itemMaker = item.itemMaker;
			taobao.itemType = item.itemType;
			String price = element.select(".price").text().replaceAll("¥", "");
			try {
				taobao.price = Double.parseDouble(price);
			} catch (Exception ex) {
				taobao.price = 0;
			}
			String dealCnt = element.select(".deal-cnt").text()
					.replaceAll("付款", "");
			dealCnt = dealCnt.replaceAll("人", "");
			try {
				taobao.dealCnt = Integer.parseInt(dealCnt);
			} catch (Exception ex) {
				taobao.dealCnt = 0;
			}
			taobao.shipmentfee = element.select(".ship").text();
			taobao.title = element.select(".title").text();
			taobao.shopName = element.select(".shop").text();
			taobao.location = element.select(".location").text();
			item.taobaoList.add(taobao);
			if (i > 10)
				break;
		}
	}

	public static String composeTaobaoPrice(KakakuObject item,String split) {
		StringBuffer sb = new StringBuffer();
		if (item.taobaoList.size() > 0) {
			sb.append(item.taobaoList.get(0).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 1) {
			sb.append(item.taobaoList.get(1).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 2) {
			sb.append(item.taobaoList.get(2).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 3) {
			sb.append(item.taobaoList.get(3).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 4) {
			sb.append(item.taobaoList.get(4).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		
		if (item.taobaoList.size() > 5) {
			sb.append(item.taobaoList.get(5).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 6) {
			sb.append(item.taobaoList.get(6).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 7) {
			sb.append(item.taobaoList.get(7).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 8) {
			sb.append(item.taobaoList.get(8).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 9) {
			sb.append(item.taobaoList.get(9).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		return sb.toString();
	}

	public  static List<KakakuObject> parseCategoryUrl(String exploreUrl) throws Exception {
		List<KakakuObject> list = new ArrayList<KakakuObject>();
		Document doc = KakakuUtil.urlToDocumentKakaku(exploreUrl);

		Elements elements = doc.select("table").select("#compTblList")
				.select(".ckitanker");
		for (Element element : elements) {
			KakakuObject obj = new KakakuObject();
			String href = element.attr("href");
			if (href.endsWith("/")) {
				href = href.substring(0, href.lastIndexOf("/"));
			}
			obj.id = href.substring(href.lastIndexOf("/") + 1);
			list.add(obj);
		}
		return list;
	}
}
