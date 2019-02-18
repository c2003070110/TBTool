package com.walk_nie.taobao.amazon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.helper.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.taobao.object.StockObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

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

	public AmazonGoodsObject scanItemUrl(String url) throws IOException {
		AmazonGoodsObject obj = new AmazonGoodsObject();
		obj.url = url;
		String[] urlSplits = url.split("/");
		for(int i=0;i<urlSplits.length;i++){
			if(urlSplits[i].equals("dp") && i<urlSplits.length){
				obj.asin = urlSplits[i+1];
				break;
			}
		}
		
		getWebDriver(url);
		
		WebElement topLvlWE = webDriver.findElement(By.cssSelector("div[id=\"dp-container\"]"));
		
		// parse first!!
		parseASIN(topLvlWE,obj);

		// title price 
		parseCenterCol(topLvlWE,obj);
		
		// screen shot description
		// TODO
		//parseProductDescription(topLvlWE,obj);
		
		// video picture
		parseLeftCol(topLvlWE,obj);
		
		return obj;
	}

	private void parseLeftCol(WebElement topLvlWE, AmazonGoodsObject obj) {
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
//		ivLiWE = ivTopWE.findElement(By.cssSelector("li[id=\"iv360TabHeading\"]"));
//		if (ivLiWE != null) {
//			ivLiWE.click();
//		}
		ivLiWE = ivTopWE.findElement(By.cssSelector("li[id=\"ivImagesTabHeading\"]"));
		if (ivLiWE != null) {
			ivLiWE.click();
			WebElement ivTabTopWE = ivTopWE.findElement(By.cssSelector("div[id=\"ivImagesTab\"]"));
			WebElement thumbColsWE = ivTabTopWE.findElement(By.cssSelector("div[id=\"ivThumbColumn\"]"));
			List<WebElement> thumbColsWES = thumbColsWE.findElements(By.tagName("div"));
			for(WebElement wet : thumbColsWES){
				String id = wet.getAttribute("id");
				if(StringUtil.isBlank(id) || !id.startsWith("ivImage_")){
					continue;
				}
					
				wet.click();
				NieUtil.mySleepBySecond(1);
				WebElement ivTopWET = webDriver.findElement(By.cssSelector("div[id=\"iv-tab-view-container\"]"));
				WebElement ivTabTopWET = ivTopWET.findElement(By.cssSelector("div[id=\"ivImagesTab\"]"));
				WebElement ivMWET = ivTabTopWET.findElement(By.cssSelector("div[id=\"ivMain\"]"));
				WebElement aWE = ivMWET.findElement(By.tagName("img"));
				obj.googsPicUrlList.add(aWE.getAttribute("src"));
			}
		}
	}

	private void parseProductDescription(WebElement topLvlWE, AmazonGoodsObject obj) {
		
		getWebDriverForFullScreenShot(obj.url);
		File screenshot = ((TakesScreenshot) webDriverForFullScreenShot).getScreenshotAs(OutputType.FILE);
		topLvlWE = webDriverForFullScreenShot.findElement(By.cssSelector("div[id=\"dp-container\"]"));
		
		int i=2;
		try {
			WebElement aplusWE = topLvlWE
					.findElement(By.cssSelector("div[id=\"descriptionAndDetails\"]"));
			snapShotDetailDisp(obj, screenshot, i++, aplusWE);
		} catch (Exception ignore) {}
		try {
			WebElement aplusWE = topLvlWE
					.findElement(By.cssSelector("div[id=\"dpx-aplus-product-description_feature_div\"]"));
			snapShotDetailDisp(obj, screenshot, i++, aplusWE);
		} catch (Exception ignore) {}
		try {
			WebElement aplusWE = topLvlWE
					.findElement(By.cssSelector("div[id=\"aplus_feature_div\"]"));
			snapShotDetailDisp(obj, screenshot, i++, aplusWE);
		} catch (Exception ignore) {}
	}

	private void parseCenterCol(WebElement topLvlWE, AmazonGoodsObject obj) throws IOException {

		WebElement centerWE = topLvlWE.findElement(By.cssSelector("div[id=\"centerCol\"]"));
		
		obj.titleOrg = centerWE.findElement(By.cssSelector("span[id=\"productTitle\"]")).getText();
		obj.titleJP = obj.titleOrg ;
		obj.priceOrg = centerWE.findElement(By.cssSelector("span[id=\"priceblock_ourprice\"]")).getText();
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
		// TODO
		//snapShotDetailDisp(obj, 1, featurebulletsWE);
	}
	
	private void snapShotDetailDisp(AmazonGoodsObject obj, File screenshot, int pos, WebElement we) throws IOException {

		String fileNameFmt = "%s_detail_%d.png";
		String fileName = String.format(fileNameFmt, obj.asin, pos);

		String pictureOutFolder = NieConfig.getConfig("amazon.root.out.picture.folder");
		File despFile = new File(pictureOutFolder, fileName);
		WebDriverUtil.screenShotV2(screenshot, we, despFile.getAbsolutePath(), null);
		obj.detailScreenShotPicFile.add(despFile.getAbsolutePath());
	}
 
	
	private void snapShotDetailDisp(AmazonGoodsObject obj, int pos, WebElement we) throws IOException {

		String fileNameFmt = "%s_detail_%d.png";
		String fileName = String.format(fileNameFmt, obj.asin, pos);
		
		String pictureOutFolder = NieConfig.getConfig("amazon.root.out.picture.folder");
		File despFile = new File(pictureOutFolder, fileName);
		WebDriverUtil.screenShotV2(webDriver, we, despFile.getAbsolutePath(), null);
		obj.detailScreenShotPicFile.add(despFile.getAbsolutePath());
	}
 

	private void parseASIN(WebElement topLvlWE, AmazonGoodsObject obj) {

		WebElement dtlGridFeatherWE = getWebElementByCssSelectors(topLvlWE,
				"div[id=\"product-details-grid_feature_div\"]", 
				"div[id=\"detail-bullets_feature_div\"]",
				"div[id=\"prodDetails\"]");

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
		if (obj.weightItem != 0 && obj.weightShipment != 0) {
			// nothing.
		}else if (obj.weightItem == 0 && obj.weightShipment != 0) {
			obj.weightItem = 200;
		} else if (obj.weightItem != 0 && obj.weightShipment == 0) {
			obj.weightShipment = obj.weightItem;
		}else {
			obj.weightShipment = 0;
			obj.weightItem = 0;
		}
	}

	private WebElement getWebElementByCssSelectors(WebElement topLvlWE, String... cssSelectors) {
		for(String cssSelector:cssSelectors){
			try{
				return topLvlWE.findElement(By.cssSelector(cssSelector));
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}

	private int toWeight(String text) {
		String str = text.replaceAll(" ", "");
		str = str.toLowerCase();
		int b = 1;
		if(str.endsWith("kg")){
			b = 1000;
		}
		str = str.replace("kg", "");
		str = str.replace("g", "");
		return (int)Double.parseDouble(str) * b;
	}

	private WebDriver webDriver = null;
	private WebDriver getWebDriver(String url) {
		if(!testWebDriver()){
			webDriver = openWebDriver();
		}
		webDriver.get(url);
		return webDriver;
	}

	private boolean testWebDriver() {
		if(webDriver == null){
			return false;
		}
		try{
			webDriver.get("https://www.amazon.co.jp");
		}catch(Exception e){
			try{
				webDriver.close();
			}catch(Exception ex){
			}
			return false;
		}
		return true;
	}

	private WebDriver openWebDriver() {
		return WebDriverUtil.getFirefoxWebDriver();
	}
	
	private WebDriver webDriverForFullScreenShot = null;
	private WebDriver getWebDriverForFullScreenShot(String url) {
		if(!testWebDriverForFullScreenShot()){
			webDriverForFullScreenShot = openWebDriverForFullScreenShot();
		}
		webDriverForFullScreenShot.get(url);
		return webDriverForFullScreenShot;
	}

	private boolean testWebDriverForFullScreenShot() {
		if(webDriverForFullScreenShot == null){
			return false;
		}
		try{
			webDriverForFullScreenShot.get("https://www.amazon.co.jp");
		}catch(Exception e){
			try{
				webDriverForFullScreenShot.close();
			}catch(Exception ex){
			}
			return false;
		}
		return true;
	}

	private WebDriver openWebDriverForFullScreenShot() {
		return WebDriverUtil.getIEWebDriver();
	}

}
