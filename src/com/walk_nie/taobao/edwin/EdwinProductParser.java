package com.walk_nie.taobao.edwin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
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
		// picture
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

	// public void getSizeTable(GoodsObject goodsObj) {
	public void getSizeTable(String goodsNo) {
		String sizeTblUrl1 = String.format(sizeTblUrl, goodsNo);
		WebDriver webDriver = WebDriverUtil.getWebDriver(sizeTblUrl1);
		WebElement eleMain = webDriver.findElement(By.id("main"));
		List<WebElement> toPicElements = Lists.newArrayList();
		toPicElements.add(eleMain.findElement(By.id("size_detail")));
		toPicElements.add(eleMain.findElement(By
				.cssSelector("table.tableBlock01")));
		findSectionBlock(toPicElements, eleMain);
		if (!toPicElements.isEmpty()) {
			String fileNameFmt = "detail_%s.png";
			String fileName = String.format(fileNameFmt, goodsNo);
			File despFile = new File("./out", fileName);
			try {
				WebDriverUtil.screenShotV2(webDriver, toPicElements,
						despFile.getAbsolutePath(),
						WebDriverUtil.watermark_common);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void findSectionBlock(List<WebElement> ele, WebElement eleMain) {
		List<WebElement> sectionBls = eleMain.findElements(By
				.className("sectionBlock"));
		for (WebElement section : sectionBls) {
			List<WebElement> pList = section.findElements(By
					.cssSelector("p.ttlType04"));
			for (WebElement p : pList) {
				if ("商品実寸法の測り方".equals(p.getText())) {
					ele.add(section);
					return;
				}
			}
		}
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

	// private String findGoodsNo(String str) {
	//
	// int idx = str.indexOf("GOODS_NO=");
	// String subStr = str.substring(idx);
	// int idx2 = subStr.indexOf("&");
	// if (idx2 == 1) {
	// idx2 = subStr.length();
	// }
	// return subStr.substring("GOODS_NO=".length(), idx2);
	// }
}
