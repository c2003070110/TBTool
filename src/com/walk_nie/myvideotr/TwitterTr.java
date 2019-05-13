package com.walk_nie.myvideotr;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.util.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class TwitterTr {
	String favlog_url = "https://favolog.org/niehpjp";
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		TwitterTr main = new TwitterTr();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		main.scan(driver);
	}

	public List<MyVideoObject> scan(WebDriver driver) {
		
		List<MyVideoObject> videoObjs = parseFavolog(driver);
		
		return videoObjs;
	}
	public void removeFromFavolog(WebDriver driver, MyVideoObject tw) {
		driver.get(favlog_url);
		logonFavolog(driver);

		WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"main\"]"));
		List<WebElement> eles = rootWe.findElements(By.cssSelector("div[class=\"tl-tweets\"]"));
		for (WebElement ele : eles) {
			List<WebElement> eles1 = ele.findElements(By.className("tl-tweet"));
			for (WebElement ele1 : eles1) {
				List<WebElement> eles2 = ele1.findElements(By.tagName("a"));
				if(eles2.isEmpty())continue;
				for (WebElement ele2 : eles2) {
					String txt = ele2.getText();
					String onclick = ele2.getAttribute("onclick");
					if(txt.indexOf("削除") == -1)continue;
					if(onclick.indexOf(tw.trid) != -1){
						ele2.click();
						break;
					}

				}
			}
		}
	}

	private List<MyVideoObject> parseFavolog(WebDriver driver) {
		driver.get(favlog_url);
		logonFavolog(driver);
		
		WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"main\"]"));
		WebElement el1 = rootWe.findElement(By.cssSelector("div[id=\"update\"]"));
		el1.click();
		NieUtil.mySleepBySecond(2);
		rootWe = driver.findElement(By.cssSelector("div[id=\"main\"]"));
		List<WebElement> eles = rootWe.findElements(By.cssSelector("div[class=\"tl-tweets\"]"));
		
		List<MyVideoObject> twList = Lists.newArrayList();
		for (WebElement ele : eles) {
			List<WebElement> eles1 = ele.findElements(By.className("tl-tweet"));
			for (WebElement ele1 : eles1) {
				String id = ele1.getAttribute("id");// tw1127569675770974208
				String href = "";
				String uper = "";// TODO
				String title = "";// TODO
				if(id.startsWith("tw")){
					id = id.substring("tw".length());
				}
				List<WebElement> eles2 = ele1.findElements(By.className("tl-text"));
				if(eles2.isEmpty())continue;
				eles2 = ele1.findElements(By.tagName("a"));
				if(eles2.isEmpty())continue;
				for (WebElement ele2 : eles2) {
					String text = ele2.getText();
					if(text.toLowerCase().startsWith("pic.twitter")){
						 href = ele2.getAttribute("href");
						break;
					}
				}
				if(!StringUtil.isBlank(href)){
					MyVideoObject obj = new MyVideoObject();
					obj.trid = id;
					obj.url = href;
					obj.uper = uper;
					obj.title = title;
					obj.toType = "toWeibo";
					obj.fromType = "fromTwitter";
					twList.add(obj);
				}
			}
		}
		return twList;
	}

	private void logonFavolog(WebDriver driver) {

		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"header-r\"]"));
		String txt = el1.getText();
		if(txt.indexOf("ログイン") == -1){
			return ;
		}

		List<WebElement> eles = el1.findElements(By.tagName("a"));
		for (WebElement ele : eles) {
			String txt1 = ele.getText();
			if (txt1.indexOf("ログイン") != -1) {
				ele.click();break;
			}
		}
		NieUtil.mySleepBySecond(2);
		
		el1 = driver.findElement(By.cssSelector("form[id=\"oauth_form\"]"));
		
		WebElement el2 = el1.findElement(By.cssSelector("input[id=\"username_or_email\"]"));
		el2.clear();
		el2.sendKeys(NieConfig.getConfig("myvideotr.twitter.user.name"));
		
		el2 = el1.findElement(By.cssSelector("input[id=\"password\"]"));
		el2.clear();
		el2.sendKeys(NieConfig.getConfig("myvideotr.twitter.user.password"));
		
		el2 = el1.findElement(By.cssSelector("input[id=\"allow\"]"));
		el2.click();
		
		NieUtil.mySleepBySecond(2);
	}

	public boolean downloadVideo(WebDriver driver, MyVideoObject downloadObj) throws IOException {
		driver.get(downloadObj.url);
		NieUtil.mySleepBySecond(2);

		File outFolder = MyVideoTrUtil.getVideoSaveFolder(downloadObj);
		
		String url = driver.getCurrentUrl();
		if(url.indexOf("video") != -1){
			downloadObj.videoUrl = downloadObj.url;
			String videoDownloadUrl = MyVideoTrUtil.getVideoDownloadUrl(driver, downloadObj);
			if (StringUtil.isBlank(videoDownloadUrl)) {
				return false;
			}
			File saveFile = new File(outFolder, downloadObj.uid + ".mp4");
			MyVideoTrUtil.downLoadVideoFromUrl(videoDownloadUrl, saveFile);
			removeFromFavolog(driver, downloadObj);
			return true;
		}else if(url.indexOf("photo") != -1){
			WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"permalink-overlay\"]"));
			List<String> picUrlList = Lists.newArrayList();
			List<WebElement> eles = rootWe.findElements(By.cssSelector("div[class=\"halfWidthPhoto\"]"));
			for (WebElement ele : eles) {
				List<WebElement> eles1 = ele.findElements(By.className("AdaptiveMedia-photoContainer"));
				for (WebElement ele1 : eles1) {
					String dataUrl = ele1.getAttribute("data-image-url");
					if(StringUtil.isBlank(dataUrl)){
						continue;
					}
				}
			}
			for (int i = 0; i < picUrlList.size(); i++) {
				String urlpic = picUrlList.get(i);
				try {
					Files.copy(new File(urlpic), new File(outFolder, i + ".jpg"));
				} catch (IOException e) {
				}
			}
			return true;
		}
		return false;
	}
}
