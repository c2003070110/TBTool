package com.walk_nie.taobao.amazon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.helper.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.taobao.object.StockObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;

public class AmazonGoodsPageParser extends BaseBaobeiParser {

	public List<AmazonGoodsObject> scanItemUrls(List<String> urls)
			throws IOException {
		List<AmazonGoodsObject> goodsList = new ArrayList<AmazonGoodsObject>();
		for (String url : urls) {
			AmazonGoodsObject obj = scanItemUrl(url);
			if(obj != null){
				goodsList.add(obj);
				for (StockObject st : obj.stockList) {
					if (!StringUtil.isBlank(st.sizeName) && !obj.sizeNameList.contains(st.sizeName)) {
						obj.sizeNameList.add(st.sizeName);
					}
					if (!StringUtil.isBlank(st.colorName) && !obj.colorNameList.contains(st.colorName)) {
						obj.colorNameList.add(st.colorName);
					}
				}
			}
		}
		return goodsList;
	}

	public AmazonGoodsObject scanItemUrl(String url) {
		AmazonGoodsObject obj = new AmazonGoodsObject();
		String[] urlSplits = url.split("/");
		for(int i=0;i<urlSplits.length;i++){
			if(urlSplits[i].equals("dp") && i<urlSplits.length){
				obj.asin = urlSplits[i+1];
				break;
			}
		}
		WebDriver webDriver = getWebDriver(url);
		WebElement topLvlWE = webDriver.findElement(By.cssSelector("div[id=\"dp-container\"]"));
		
		// parse first!!
		parseASIN(webDriver,topLvlWE,obj);

		// title price 
		parseCenterCol(webDriver,topLvlWE,obj);
		
		// screen shot description
		parseProductDescription(webDriver,topLvlWE,obj);
		
		// video picture
		parseLeftCol(webDriver,topLvlWE,obj);
		
		return obj;
	}

	private void parseLeftCol(WebDriver webDriver, WebElement topLvlWE, AmazonGoodsObject obj) {
		WebElement topWE = topLvlWE.findElement(By.cssSelector("div[id=\"leftCol\"]"));
		WebElement we = topWE.findElement(By.cssSelector("div[id=\"main-image-container\"]"));
		we.click();
		
		WebElement ivTopWE = webDriver.findElement(By.cssSelector("div[id=\"iv-tab-view-container\"]"));
		WebElement ivLiWE = ivTopWE.findElement(By.cssSelector("li[id=\"ivVideosTabHeading\"]"));
		if (ivLiWE != null) {
			ivLiWE.click();
			WebElement ivTabTopWE = ivTopWE.findElement(By.cssSelector("div[id=\"ivVideosTab\"]"));
			we = ivTabTopWE.findElement(By.tagName("video"));
			if (we != null) {
				obj.googsVideoUrlList.add(we.getAttribute("src"));
			}
		}
		ivLiWE = ivTopWE.findElement(By.cssSelector("li[id=\"iv360TabHeading\"]"));
		if (ivLiWE != null) {
			ivLiWE.click();
		}
		ivLiWE = ivTopWE.findElement(By.cssSelector("li[id=\"ivImagesTabHeading\"]"));
		if (ivLiWE != null) {
			ivLiWE.click();
			WebElement ivTabTopWE = ivTopWE.findElement(By.cssSelector("div[id=\"ivImagesTab\"]"));
			List<WebElement> thumbColsWES = ivTabTopWE.findElements(By.cssSelector("li[id=\"ivThumbColumn\"]"));
			for(WebElement wet : thumbColsWES){
				wet.click();
				WebElement ivTopWET = webDriver.findElement(By.cssSelector("div[id=\"iv-tab-view-container\"]"));
				WebElement ivTabTopWET = ivTopWET.findElement(By.cssSelector("div[id=\"ivImagesTab\"]"));
				WebElement aWE = ivTabTopWET.findElement(By.cssSelector("div[id=\"ivMain\"]")).findElement(By.tagName("a"));
				obj.googsPicUrlList.add(aWE.getAttribute("src"));
			}
		}
	}

	private void parseProductDescription(WebDriver webDriver, WebElement topLvlWE, AmazonGoodsObject obj) {

		try {
			WebElement aplusWE = topLvlWE
					.findElement(By.cssSelector("div[id=\"dpx-aplus-product-description_feature_div\"]"));

			String fileNameFmt = "%s_detail_2.png";
			String fileName = String.format(fileNameFmt, obj.asin);
			snapShotDetailDisp(obj, webDriver, fileName, aplusWE);
		} catch (Exception ignore) {

		}
	}

	private void parseCenterCol(WebDriver webDriver, WebElement topLvlWE, AmazonGoodsObject obj) {

		WebElement centerWE = topLvlWE.findElement(By.cssSelector("div[id=\"centerCol\"]"));
		
		obj.titleOrg = centerWE.findElement(By.cssSelector("div[id=\"productTitle\"]")).getText();
		
		obj.priceOrg = centerWE.findElement(By.cssSelector("div[id=\"priceblock_ourprice\"]")).getText();
		String price = obj.priceOrg.replace("税", "");
		price = price.replaceAll("￥", "");
		price = price.replaceAll(",", "");
		price = price.replaceAll(" ", "");
		obj.priceJPY = Long.parseLong(price);
		
		WebElement featurebulletsWE = centerWE.findElement(By.cssSelector("div[id=\"featurebullets_feature_div\"]"));
		try {
			WebElement aWE = featurebulletsWE.findElement(By.cssSelector("a[data-action=\"a-expander-toggle\"]"));
			if (aWE.getText().equals("表示件数を増やす")) {
				aWE.click();
			}
		} catch (Exception ignore) {

		}
		//
		String fileNameFmt = "%s_detail_1.png";
		String fileName = String.format(fileNameFmt, obj.asin);
		snapShotDetailDisp(obj,webDriver,fileName, featurebulletsWE);
	}
	
	private void snapShotDetailDisp(AmazonGoodsObject obj, WebDriver webDriver, String fileName, WebElement we) {

		String pictureOutFolder = NieConfig.getConfig("amazon.root.out.picture.folder");
		File despFile = new File(pictureOutFolder, fileName);
		try {
			WebDriverUtil.screenShotV2(webDriver, we, despFile.getAbsolutePath(), WebDriverUtil.watermark_montbell);
			obj.detailScreenShotPicFile.add(despFile.getAbsolutePath());
		} catch (IOException ignore) {
		}
	}
 

	private void parseASIN(WebDriver webDriver, WebElement topLvlWE, AmazonGoodsObject obj) {

		WebElement dtlGridFeatherWE = topLvlWE.findElement(By.cssSelector("div[id=\"product-details-grid_feature_div\"]"));

		List<WebElement> trWES = dtlGridFeatherWE.findElements(By.tagName("tr"));
		for (WebElement we : trWES) {
			List<WebElement> tdWES = we.findElements(By.tagName("td"));
			if (tdWES.isEmpty() || tdWES.size() != 2) {
				continue;
			}
			if ("ASIN".equals(tdWES.get(0).getText())) {
				obj.asin = tdWES.get(1).getText();
			}
			if ("発送重量".equals(tdWES.get(0).getText())) {
				obj.weightShipment = toWeight(tdWES.get(1).getText());
			}
		}
		if (obj.weightItem == 0) {
			obj.weightItem = obj.weightShipment;
		}
		if (obj.weightShipment == 0) {
			obj.weightShipment = obj.weightItem;
		}
	}

	private int toWeight(String text) {
		String str = text.replaceAll(" ", "");
		str = str.toLowerCase();
		int b = 1;
		if(str.endsWith("kg")){
			b = 1000;
		}
		str = str.replace("g", "");
		str = str.replace("kg", "");
		return Integer.parseInt(str) * b;
	}

	private WebDriver getWebDriver(String url) {
		if(!testWebDriver()){
			openWebDriver();
		}
		driver.get(url);
		return driver;
	}

	private boolean testWebDriver() {
		if(driver == null){
			return false;
		}
		try{
			driver.get("https://www.amazon.co.jp");
		}catch(Exception e){
			try{
				driver.close();
			}catch(Exception ex){
			}
			return false;
		}
		return true;
	}

	private WebDriver openWebDriver() {
		return WebDriverUtil.getFirefoxWebDriver();
	}
	private WebDriver driver = null;

}
