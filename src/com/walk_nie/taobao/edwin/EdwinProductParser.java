package com.walk_nie.taobao.edwin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.jetty.util.StringUtil;
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
	private List<String> sanned_as_goods_no_list = Lists.newArrayList();
	private WebDriver webDriver = null;
	private String brandCd = "";
	private String sexVal = "";

	public EdwinProductParser() {
	}

	public EdwinProductParser(String brandCd, String sexVal) {
		this.brandCd = brandCd;
		this.sexVal = sexVal;
	}

	public List<GoodsObject> parseByCategoryNames(List<String> scanSeriesNames) {
		List<GoodsObject> goodsObjList = Lists.newArrayList();
		// brandCd,sexVal are Required!!
		for (String serial : scanSeriesNames) {
			String url = String.format(EdwinUtil.categoryURLFmt, brandCd,
					sexVal, serial);
			List<GoodsObject> goodsList = parseByCategory(url);
			for (GoodsObject goods : goodsList) {
				goods.brandCd = this.brandCd;
				goods.cateCd = serial;
				goods.sexVal = sexVal;
			}
			goodsObjList.addAll(goodsList);
		}
		List<GoodsObject> filteredProdList = filter(goodsObjList);
		saveSizeTip(filteredProdList);
		return filteredProdList;
	}

	public List<GoodsObject> parseByCategorys(List<String> urls) {
		List<GoodsObject> goodsObjList = Lists.newArrayList();
		for (String url : urls) {
			List<GoodsObject> goodsList = parseByCategory(url);
			goodsObjList.addAll(goodsList);
		}
		List<GoodsObject> filteredProdList = filter(goodsObjList);
		saveSizeTip(filteredProdList);
		return filteredProdList;
	}

	private List<GoodsObject> filter(List<GoodsObject> goodsObjList) {

		List<GoodsObject> filterdList = Lists.newArrayList();
		List<String> productId = Lists.newArrayList();
		for (GoodsObject prod : goodsObjList) {
			if (!productId.contains(prod.goods_no)) {
				productId.add(prod.goods_no);
				filterdList.add(prod);
			}
		}
		return filterdList;
	}

	private void saveSizeTip(List<GoodsObject> goodsObjList) {
		for (GoodsObject prod : goodsObjList) {
			prod.sizeTipFileName = getSizeTable(prod.goods_no);
		}

	}

	public List<GoodsObject> parseByCategory(String url) {
		List<GoodsObject> goodsObjList = Lists.newArrayList();
		WebDriver webDriver = getWebDriver();
		webDriver.get(url);
		try {
			WebElement weSearchRslt = webDriver.findElement(By
					.cssSelector("ul#searchResultList"));
			List<WebElement> weListItem = weSearchRslt.findElements(By
					.tagName("li"));
			List<String> urls = Lists.newArrayList();
			for (WebElement weLi : weListItem) {
				WebElement a = weLi.findElement(By.cssSelector("p.thumb"))
						.findElement(By.tagName("a"));
				if (a != null) {
					String href = a.getAttribute("href");
					if (!urls.contains(href)) {
						urls.add(href);
					}
				}
			}
			for (String purl : urls) {
				GoodsObject goods = parseByItemURL(purl);
				if (goods != null)
					goodsObjList.add(goods);
			}
		} catch (Exception ignore) {
			System.out.println("[ERROR]URL=" + url);
		}
		return goodsObjList;
	}

	private WebDriver getWebDriver() {
		if (webDriver != null)
			return webDriver;
		webDriver = WebDriverUtil.getFirefoxWebDriver();
		return webDriver;
	}

	public GoodsObject parseByItemURL(String url) {
		GoodsObject goodsObj = new GoodsObject();

		WebDriver webDriver = getWebDriver();
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
			String as_goods_no = el.text();
			if (sanned_as_goods_no_list.contains(as_goods_no)) {
				return null;
			}
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
					goodsObj.colorPictureUrlList.add(String.format(EdwinUtil.colorUrlFmt, goodsObj.ecCd,colorName));
					webDriver.findElement(By.id(id)).click();
					Document doc1 = Jsoup.parse(webDriver.getPageSource());
					// 商品番号
					el = doc1.getElementById("AS_GOODS_NO_TXT");
					if (el != null) {
						String as_goods_no = el.text();
						goodsObj.as_goods_no.add(as_goods_no);
						sanned_as_goods_no_list.add(as_goods_no);
					}

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
						&& !goodsObj.bodyPictureUrlList.contains(imgSrc)) {
					goodsObj.bodyPictureUrlList.add(imgSrc);
				}
			}
		}

		// 模特
		el = doc.getElementById("DTL_EXP_TXT");
		if (el != null) {
			String mdl = el.text();
			mdl = mdl.replaceAll("モデル", "模特");
			mdl = mdl.replaceAll("身長", "身高");
			mdl = mdl.replaceAll("胸囲", "胸围");
			mdl = mdl.replaceAll("ウエスト", "腰围");
			mdl = mdl.replaceAll("ヒップ", "臀围");
			mdl = mdl.replaceAll("サイズ", "码");
			mdl = mdl.replaceAll("インチ", "Inch");
			goodsObj.modelTipInfo = mdl;
		}

		return goodsObj;
	}

	// public void getSizeTable(GoodsObject goodsObj) {
	public String getSizeTable(String goodsNo) {
		String fileNameFmt = "sizetable_%s.png";
		String fileName = String.format(fileNameFmt, goodsNo);
        String outputRoot = EdwinUtil.rootPathName;
        if(!StringUtil.isBlank(brandCd)){
        	outputRoot += "/" + brandCd;
        }
        if(!StringUtil.isBlank(sexVal)){
        	outputRoot += "/" + sexVal;
        }
        		
		File despFile = new File(outputRoot, fileName);
		if (!despFile.getParentFile().exists()) {
			despFile.getParentFile().mkdirs();
		}
		if (despFile.exists()) {
			return despFile.getAbsolutePath();
		}
		String sizeTblUrl1 = String.format(EdwinUtil.sizeTblUrl, goodsNo);
		WebDriver webDriver = WebDriverUtil.getWebDriver(sizeTblUrl1);
		WebElement eleMain = webDriver.findElement(By.id("main"));
		List<WebElement> toPicElements = Lists.newArrayList();
		toPicElements.add(eleMain.findElement(By.id("size_detail")));
		toPicElements.add(eleMain.findElement(By
				.cssSelector("table.tableBlock01")));
		findSectionBlock(toPicElements, eleMain);
		if (!toPicElements.isEmpty()) {
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
		return despFile.getAbsolutePath();
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
		String n = str.replace("ｲﾝﾁ", "Inch");
		n = str.replace("インチ", "Inch");
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
