package com.walk_nie.taobao.wacom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.WebDriverUtil;

public class WacomProductParser extends BaseBaobeiParser {

	public List<WacomProductObject> scanItemByProductInfoList(List<WacomProductObject> productInfos) throws IOException {
		List<WacomProductObject> goodsList = new ArrayList<WacomProductObject>();
		for (WacomProductObject productInfo : productInfos) {
			WacomProductObject obj = scanSingleItem(productInfo.productUrl);
			obj.categoryId = productInfo.categoryId;
			if (!isPublished(obj)) {
				goodsList.add(obj);
			}
		}
		return goodsList;
	}

	public List<WacomProductObject> scanItemByProductUrlList(List<String> urls) throws IOException {
		List<WacomProductObject> goodsList = new ArrayList<WacomProductObject>();
		for (String url : urls) {
			WacomProductObject obj = scanSingleItem(url);
			if (!isPublished(obj)) {
				goodsList.add(obj);
			}
		}
		return goodsList;
	}

	private boolean isPublished(WacomProductObject obj) {
		if (publishedbaobeiList == null) {
			return false;
		}
		if (publishedbaobeiList.isEmpty()) {
			return false;
		}
		for (BaobeiPublishObject baobeiObj : publishedbaobeiList) {
			String publisedProductId = "";
			String outer_id = baobeiObj.outer_id.replace("\"", "");
			if (outer_id.startsWith("WACOM_")) {
				String[] split = outer_id.split("-");
				publisedProductId = split[split.length - 1];
			}
			if (publisedProductId.equals(obj.productId)) {
				return true;
			}
		}
		return false;
	}

	public WacomProductObject scanSingleItem(String url) throws IOException {
		System.out.println("[INFO][URL]" + url);
		WacomProductObject obj = new WacomProductObject();
		obj.productUrl = url;
		try {
			scanSingleItem(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public void scanSingleItem(WacomProductObject goodsObj) throws IOException {
		String url = goodsObj.productUrl;

		String query = url.substring(goodsObj.productUrl.lastIndexOf("?")+1);
		String[] sp = query.split("=");
		for (int i = 0; i < sp.length; i++) {
			if (sp[i].equals("product_id")) {
				goodsObj.productId = sp[i + 1];
				break;
			}
		}
		WebDriver webDriver = WebDriverUtil.getWebDriver(url);
		WebElement weRoot = webDriver.findElement(By.cssSelector("div[id=\"undercolumn\"]"));
System.out.println("[DEBUG][1]" + DateUtils.formatDate(Calendar.getInstance().getTime(),"yyyy-MM-dd HH:mm:ss.SSS"));

		WebElement we = weRoot.findElement(By.cssSelector("div[id=\"detailrightbloc\"]"));
		goodsObj.productName = we.findElement(By.tagName("h1")).getText();
		goodsObj.priceOrg = we.findElement(By.cssSelector("dl[class=\"sale_price\"]")).getText();
		toRealPrice(goodsObj);
		goodsObj.kataban = we.findElement(By.cssSelector("span[id=\"product_code_default\"]")).getText();
System.out.println("[DEBUG][2]" + DateUtils.formatDate(Calendar.getInstance().getTime(),"yyyy-MM-dd HH:mm:ss.SSS"));

		we = weRoot.findElement(By.cssSelector("div[id=\"detailphotobloc\"][class=\"pc\"]"));
		List<WebElement> wes = we.findElements(By.cssSelector("a[rel=\"expansion\"]"));
		for (WebElement wetemp : wes) {
			String href = wetemp.getAttribute("href");
			if (!goodsObj.productGalaryPicUrlList.contains(href)) {
				goodsObj.productGalaryPicUrlList.add(href);
			}
		}

System.out.println("[DEBUG][3]" + DateUtils.formatDate(Calendar.getInstance().getTime(),"yyyy-MM-dd HH:mm:ss.SSS"));
		we = weRoot.findElement(By.cssSelector("table[class=\"new_spec\"]"));
		List<WebElement> wesTr = we.findElements(By.tagName("tr"));
		for (WebElement wetemp : wesTr) {
			int i= 0;
			WebElement wesTh = null;
			try {
				wesTh = wetemp.findElement(By.tagName("th"));
			} catch (Exception e) {
				wesTh = wetemp.findElement(By.tagName("td"));
				i++;
			}
			if (wesTh.getText().equals("質量")) {
				WebElement wesTd = wetemp.findElements(By.tagName("td")).get(i);
				goodsObj.weight = toWeiget(wesTd.getText());
				toExtraWeight(goodsObj);
				break;
			}
		}
System.out.println("[DEBUG][4]" + DateUtils.formatDate(Calendar.getInstance().getTime(),"yyyy-MM-dd HH:mm:ss.SSS"));
		screenshotProductSpecDesp(webDriver, goodsObj, we);
System.out.println("[DEBUG][5]" + DateUtils.formatDate(Calendar.getInstance().getTime(),"yyyy-MM-dd HH:mm:ss.SSS"));
	}

	private void toExtraWeight(WacomProductObject goodsObj) {
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
			String str = text.replace(" ", "");
			str = str.replace("(本体)", "");
			str = str.replace("(スタンド)", "");
			str = str.replace("本体", "");
			str = str.replace("スタンド", "");
			str = str.replaceAll("約", "");
			if (str.indexOf("/") > 0) {
				String[] ww = str.split("/");
				int wi = 0;
				for (String w : ww) {
					if(!w.toLowerCase().endsWith("kg") || !w.toLowerCase().endsWith("g")){
						w = w.substring(0, w.toLowerCase().lastIndexOf("g")+1);
					}
					if (w.toLowerCase().endsWith("kg")) {
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
				if(!str.toLowerCase().endsWith("kg") || !str.toLowerCase().endsWith("g")){
					str = str.substring(0, str.toLowerCase().lastIndexOf("g")+1);
				}
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
		System.out.println("[ERROR][WEIGHT][STR]" + text);
		return 99999;
	}

	private void screenshotProductSpecDesp(WebDriver webDriver, WacomProductObject goodsObj, WebElement weSpec)
			throws IOException {

		String fileNameFmt = "spec_%s.png";
		String fileName = String.format(fileNameFmt, goodsObj.productId);
		File despFile = new File(WacomUtil.getPictureRootFolder(), fileName);
		if (!despFile.exists()) {
			List<WebElement> ele = Lists.newArrayList();
			ele.add(weSpec);
			WebDriverUtil.screenShotV2(webDriver, ele, despFile.getAbsolutePath(), null);
		}
		goodsObj.specScreenShotPicFile = despFile.getAbsolutePath();
	}

	private void toRealPrice(WacomProductObject goodsObj) {
		String price = goodsObj.priceOrg.replace("￥", "");
		price = price.replace("(税込)", "");
		price = price.replace(",", "");
		if (price.indexOf("～") > 0) {
			price = price.substring(price.indexOf("～") + 1);
		}
		goodsObj.priceJPY = price.trim();
	}

}
