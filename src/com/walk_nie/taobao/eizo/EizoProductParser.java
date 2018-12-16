package com.walk_nie.taobao.eizo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Lists;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.WebDriverUtil;

public class EizoProductParser extends BaseBaobeiParser {

	public List<EizoProductObject> scanItemByProductUrlList(List<String> urls) throws IOException {
		List<EizoProductObject> goodsList = new ArrayList<EizoProductObject>();
		for (String url : urls) {
			EizoProductObject obj = scanSingleItem(url);
			goodsList.add(obj);
		}
		return goodsList;
	}

	public EizoProductObject scanSingleItem(String url) throws IOException {
		System.out.println("[INFO][URL]" + url);
		EizoProductObject obj = new EizoProductObject();
		obj.productUrl = url;
		scanSingleItem(obj);
		return obj;
	}

	public void scanSingleItem(EizoProductObject goodsObj) throws IOException {
		String url = goodsObj.productUrl;

		String[] sp = url.split("/");
		goodsObj.kataban = sp[sp.length - 1];
		goodsObj.categoryName = sp[sp.length - 2];

		WebDriver webDriver = WebDriverUtil.getWebDriver(url);
		WebElement weRoot = webDriver.findElement(By.cssSelector("div[id=\"content\"]"));

		try {
			WebElement we = weRoot.findElement(By.cssSelector("ul[id=\"direct\"]"));
			goodsObj.storeUrl = we.findElements(By.tagName("a")).get(0).getAttribute("href");
		} catch (Exception e) {

		}
		if (StringUtil.isBlank(goodsObj.storeUrl)) {
			return;
		}
		goodsObj.productGalaryPicUrlList.add(
				"https://www.eizo.co.jp/products/" + goodsObj.categoryName + "/" + goodsObj.kataban + "/photo_big.jpg");

		WebElement we = weRoot.findElement(By.cssSelector("div[id=\"product_photo\"]"));
		List<WebElement> wes = we.findElements(By.cssSelector("img[class=\"transparent\"]"));
		for (WebElement wetemp : wes) {
			String href = wetemp.getAttribute("src");
			if (!goodsObj.productGalaryPicUrlList.contains(href)) {
				goodsObj.productGalaryPicUrlList.add(href);
			}
		}
		we = weRoot.findElement(By.cssSelector("div[id=\"spec_area\"]"));
		wes = we.findElements(By.cssSelector("div[class=\"title\"]"));
		if(!wes.isEmpty()){
			for (WebElement wetemp : wes) {
				WebElement w = wetemp.findElement(By.tagName("img"));
				goodsObj.productName = w.getAttribute("alt");
			}
		}
		
		we = weRoot.findElement(By.cssSelector("div[id=\"tab01_content\"]"));
		screenshotProductDetalDesp(webDriver, goodsObj, we);

		webDriver.get(goodsObj.storeUrl);
		
		weRoot = webDriver.findElement(By.cssSelector("div[class=\"detailsArea\"]"));

		we = weRoot.findElement(By.tagName("table"));
		List<WebElement> wesTr = we.findElements(By.tagName("tr"));
		for (WebElement wetemp : wesTr) {
			List<WebElement> wesTd = wetemp.findElements(By.tagName("td"));
			if (wesTd.get(0).getText().indexOf("販売価格（税込）") != -1) {
				goodsObj.priceOrg = wesTd.get(1).getText();
				toRealPrice(goodsObj);
				break;
			}
		}
		we = webDriver.findElement(By.cssSelector("div[id=\"spec\"]"));
		wesTr = we.findElements(By.tagName("tr"));
		for (WebElement wetemp : wesTr) {
			WebElement wesTh = null;
			try {
				wesTh = wetemp.findElement(By.tagName("th"));
			} catch (Exception e) {
				wesTh = wetemp.findElement(By.tagName("td"));
			}
			WebElement wesTd = wetemp.findElement(By.tagName("td"));
			if (wesTh.getText().equals("質量")) {
				goodsObj.weight = toWeiget(wesTd.getText());
				toExtraWeight(goodsObj);
				break;
			}
		}
		
		screenshotProductSpecDesp(webDriver, goodsObj, we);
	}

	private void toExtraWeight(EizoProductObject goodsObj) {
		if (goodsObj.weight < 1000) {
			goodsObj.weightExtra = 400;
		} else if (goodsObj.weight < 2000) {
			goodsObj.weightExtra = 500;
		} else if (goodsObj.weight < 3000) {
			goodsObj.weightExtra = 600;
		} else if (goodsObj.weight < 4000) {
			goodsObj.weightExtra = 700;
		} else if (goodsObj.weight < 5000) {
			goodsObj.weightExtra = 800;
		} else if (goodsObj.weight < 7000) {
			goodsObj.weightExtra = 900;
		} else if (goodsObj.weight < 8000) {
			goodsObj.weightExtra = 1000;
		} else {
			goodsObj.weightExtra = 1100;
		}
	}

	private int toWeiget(String text) {
		try {
			String str = text.replaceAll(" ", "");
			str = str.replaceAll(" ", "");
			str = str.replace("本体", "");
			str = str.replace("スタンド", "");
			str = str.replace("約", "");
			if (str.indexOf("/") > 0) {
				String[] ww = str.split("/");
				int wi = 0;
				for (String w : ww) {
					if (ww[0].toLowerCase().endsWith("kg")) {
						String wq = w.replace("kg", "");
						wq = wq.replace("Kg", "");
						wq = wq.replace("KG", "");
						wi += (int) (Double.parseDouble(wq) * 1000);
						continue;
					}
					if (ww[0].toLowerCase().endsWith("g")) {
						String wq = w.replace("g", "");
						wq = wq.replace("G", "");
						wi += Integer.parseInt(wq);
						continue;
					}
				}
				return wi;
			} else {
				if (str.toLowerCase().endsWith("kg")) {
					String wq = str.replace("kg", "");
					wq = wq.replace("Kg", "");
					wq = wq.replace("KG", "");
					return (int) (Double.parseDouble(wq) * 1000);
				}
				if (str.toLowerCase().endsWith("g")) {
					String wq = str.replace("g", "");
					wq = wq.replace("G", "");
					return Integer.parseInt(wq);
				}
			}
		} catch (Exception e) {
			System.out.println("[ERROR][WEIGHT][STR]" + text);
		}
		return 99999;
	}

	private void screenshotProductDetalDesp(WebDriver webDriver, EizoProductObject goodsObj, WebElement we)
			throws IOException {

		String fileNameFmt = "detail_%s_%s.png";
		String fileName = String.format(fileNameFmt, goodsObj.categoryName, goodsObj.kataban);
		File despFile = new File(EizoUtil.getPictureRootFolder(), fileName);
		if (!despFile.exists()) {
			List<WebElement> ele = Lists.newArrayList();
			ele.add(we);
			WebDriverUtil.screenShotV2(webDriver, ele, despFile.getAbsolutePath(), null);
		}
		goodsObj.specScreenShotPicFile = despFile.getAbsolutePath();
	}

	private void screenshotProductSpecDesp(WebDriver webDriver, EizoProductObject goodsObj, WebElement we)
			throws IOException {

		String fileNameFmt = "spec_%s_%s.png";
		String fileName = String.format(fileNameFmt, goodsObj.categoryName, goodsObj.kataban);
		File despFile = new File(EizoUtil.getPictureRootFolder(), fileName);
		if (!despFile.exists()) {
			List<WebElement> ele = Lists.newArrayList();
			ele.add(we);
			WebDriverUtil.screenShotV2(webDriver, ele, despFile.getAbsolutePath(), null);
		}
		goodsObj.specScreenShotPicFile = despFile.getAbsolutePath();
	}

	private void toRealPrice(EizoProductObject goodsObj) {
		String price = goodsObj.priceOrg.replace("￥", "");
		price = price.replace("(税込)", "");
		price = price.replace(",", "");
		if (price.indexOf("～") > 0) {
			price = price.substring(price.indexOf("～") + 1);
		}
		goodsObj.priceJPY = price;
	}

}
