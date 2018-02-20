package com.walk_nie.taobao.edwin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.WebDriverUtil;

public class EdwinProductParser extends BaseBaobeiParser {
	public String sizeTblUrl = "http://edwin-ec.jp/disp/CSfSizeTable.jsp?GOODS_NO=%s";

	public GoodsObject parseByItemURL(String url) {
		GoodsObject goodsObj = new GoodsObject();

		WebDriver webDriver = WebDriverUtil.getFirefoxWebDriver();
		webDriver.get(url);
		Document doc = Jsoup.parse(webDriver.getPageSource());
		Element el = doc.getElementById("GOODS_NO");
		if (el != null) {
			goodsObj.goods_no = el.attr("value");
		}
		el = doc.getElementById("GOODS_NM_TXT");
		if (el != null) {
			goodsObj.titleOrg = el.text();
		}
		// 商品番号
		el = doc.getElementById("AS_GOODS_NO_TXT");
		if (el != null) {
			goodsObj.as_goods_no = el.text();
		}
		// シリーズ
		el = doc.getElementById("SERIES");
		if (el != null) {
			goodsObj.seriesName = el.text();
		}
		// 価格
		el = doc.getElementById("GOODS_SALE_PRICE");
		if (el != null) {
			goodsObj.goods_sale_price = toPrice(el.attr("value"));
		}
		el = doc.getElementById("GOODS_REAL_PRICE");
		if (el != null) {
			goodsObj.goods_real_price = toPrice(el.attr("value"));
		}
		el = doc.getElementById("GOODS_SALE_BARE_PRICE");
		if (el != null) {
			goodsObj.goods_sale_bare_price = toPrice(el.attr("value"));
		}
		el = doc.getElementById("GOODS_REAL_BARE_PRICE");
		if (el != null) {
			goodsObj.goods_real_bare_price = toPrice(el.attr("value"));
		}
		// 股上
		el = doc.getElementById("GOODS_RISE");
		if (el != null) {
			goodsObj.riseType = el.text();
		}
		// 素材
		el = doc.getElementById("MATE_VAL");
		if (el != null) {
			goodsObj.meterialJP = el.text();
		}
		// 原産国
		el = doc.getElementById("ORG_TP_TXT");
		if (el != null) {
			goodsObj.producePlace = el.text().replace("原産国", "").trim();
		}
		// ECコード
		el = doc.getElementById("ATT_GRP_ID_TXT");
		if (el != null) {
			goodsObj.ecCd = el.text();
		}

		// カラー
		Elements els = doc.getElementsByTag("li");
		if (!els.isEmpty()) {
			for (Element e : els) {
				String id = e.attr("id");
				if (id.startsWith("COLOR_FRAME_color_")) {
					String colorName = id.replace("COLOR_FRAME_color_", "");
					goodsObj.colorNameList.add(colorName);
					webDriver.findElement(By.id(id)).click();
					Document doc1 = Jsoup.parse(webDriver.getPageSource());
					// サイズ
					els = doc1.getElementsByTag("a");
					if (!els.isEmpty()) {
						for (Element e1 : els) {
							String id1 = e1.attr("id");
							if (id1.startsWith("size_")) {
								StockObject stockObj = new StockObject();
								stockObj.colorName = colorName;
								stockObj.sizeName = toSizeName(e1.attr("title"));
								stockObj.isStock = !e1.attr("class").equals(
										"nostock");
								goodsObj.stockList.add(stockObj);
							}
						}
					}
				}
			}
		}
		// サイズ
		els = doc.getElementsByTag("a");
		if (!els.isEmpty()) {
			for (Element e : els) {
				String id = e.attr("id");
				if (id.startsWith("size_")) {
					goodsObj.sizeNameList.add(toSizeName(e.attr("title")));
				}
			}
		}
		els = doc.getElementsByTag("img");
		if (!els.isEmpty()) {
			for (Element e : els) {
				String id = e.attr("id");
				String imgSrc = e.attr("src");
				int idx = imgSrc.indexOf("?");
				if (idx != -1) {
					imgSrc = imgSrc.substring(0, idx);
				}
				if (id.startsWith("GOODS_IMG")
						&& !goodsObj.pictureUrlList.contains(imgSrc)) {
					goodsObj.pictureUrlList.add(imgSrc);
				}
			}
		}

		return goodsObj;
	}

	private String toSizeName(String str) {
		String n = str.replace("ｲﾝﾁ", "");
		return n.trim();
	}

	private int toPrice(String str) {
		String n = str.replace("¥", "");
		n = n.replace(",", "");
		return Integer.parseInt(n.trim());
	}

//	private String findGoodsNo(String str) {
//
//		int idx = str.indexOf("GOODS_NO=");
//		String subStr = str.substring(idx);
//		int idx2 = subStr.indexOf("&");
//		if (idx2 == 1) {
//			idx2 = subStr.length();
//		}
//		return subStr.substring("GOODS_NO=".length(), idx2);
//	}
}
