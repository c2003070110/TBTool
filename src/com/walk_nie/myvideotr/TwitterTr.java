package com.walk_nie.myvideotr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.jetty.util.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
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
	public void removeFromFav(WebDriver driver, List<MyVideoObject> videoObjs) {
		if(videoObjs.isEmpty()) return;
		
		driver.get(favlog_url);
		logonFavolog(driver);
		
		WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"main\"]"));
		List<WebElement> eles = rootWe.findElements(By.cssSelector("div[class=\"tl-tweets\"]"));
		for (WebElement ele : eles) {
			List<WebElement> eles1 = ele.findElements(By.className("tl-tweet"));
			for (WebElement ele1 : eles1) {
				List<WebElement> eles2 = ele1.findElements(By.tagName("a"));
				if(eles2.isEmpty())continue;
				boolean breakF = false;
				for (WebElement ele2 : eles2) {
					String txt = ele2.getText();
					String onclick = ele2.getAttribute("onclick");
					if (txt.indexOf("削除") == -1)
						continue;
					for (MyVideoObject tw : videoObjs) {
						if (onclick.indexOf(tw.trid) != -1) {
							ele2.click();
							NieUtil.mySleepBySecond(1);
							try {
								driver.switchTo().alert().accept();
								NieUtil.mySleepBySecond(1);
							} catch (Exception e) {
							}
							breakF = true;
							break;
						}
					}
					if(breakF)break;
				}
			}
		}
	}

	private List<MyVideoObject> parseFavolog(WebDriver driver) {
		driver.get(favlog_url);
		logonFavolog(driver);
		List<MyVideoObject> twList = Lists.newArrayList();
		try {
			WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"main\"]"));
			WebElement el1 = rootWe.findElement(By.cssSelector("div[id=\"update\"]"));
			el1.click();
			NieUtil.mySleepBySecond(2);
		} catch (Exception e) {
			return twList;
		}
		WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"main\"]"));
		List<WebElement> eles = rootWe.findElements(By.cssSelector("div[class=\"tl-tweets\"]"));
		
		if(eles.isEmpty()) return twList;
		
		for (WebElement ele : eles) {
			List<WebElement> eles1 = ele.findElements(By.className("tl-tweet"));
			for (WebElement ele1 : eles1) {
				String id = ele1.getAttribute("id");// tw1127569675770974208
				String href = "";
				String uper = "";
				String title = "";
				if(id.startsWith("tw")){
					id = id.substring("tw".length());
				}
				List<WebElement> eles2 = ele1.findElements(By.className("tl-name"));
				if(eles2.isEmpty())continue;
				uper = eles2.get(0).getText();
				
				eles2 = ele1.findElements(By.className("tl-text"));
				if(eles2.isEmpty())continue;
				title = eles2.get(0).getText();
				List<WebElement> eles3 = eles2.get(0).findElements(By.tagName("a"));
				if(eles3.isEmpty())continue;
				for (WebElement ele3 : eles3) {
					String text = ele3.getText();
					if(text.toLowerCase().startsWith("pic.twitter")){
						 href = ele3.getAttribute("href");
						break;
					}
				}
				if(!StringUtil.isBlank(href)){
					MyVideoObject obj = new MyVideoObject();
					obj.trid = id;
					obj.videoUrl = href;
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

		File outFolder = MyVideoTrUtil.getSaveFolder(downloadObj);
		
		String url = driver.getCurrentUrl();
		if(url.indexOf("video") != -1){
			downloadObj.videoUrl = url;
			String videoDownloadUrl = MyVideoTrUtil.getVideoDownloadUrlByParsevideo(driver, downloadObj);
			if (StringUtil.isBlank(videoDownloadUrl)) {
				return false;
			}
			File saveFile = new File(outFolder, downloadObj.uid + ".mp4");
			MyVideoTrUtil.downLoadVideoFromUrl(videoDownloadUrl, saveFile);
			
			return true;
		}else if(url.indexOf("photo") != -1){
			WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"permalink-overlay\"]"));
			List<WebElement> eles = rootWe.findElements(By.className("permalink-tweet-container"));
			if(eles.isEmpty()) return false;
			List<WebElement> eles1 = eles.get(0).findElements(By.tagName("div"));
			if(eles1.isEmpty()) return false;
			List<String> picUrlList = Lists.newArrayList();

			for (WebElement ele1 : eles1) {
					String dataUrl = ele1.getAttribute("data-image-url");
					if(StringUtil.isBlank(dataUrl)){
						continue;
					}
					picUrlList.add(dataUrl);
			}
			for (int i = 0; i < picUrlList.size(); i++) {
				String urlpic = picUrlList.get(i);
				try(InputStream in = new URL(urlpic).openStream()){
					File toFile = new File(outFolder, i + ".jpg");
				    Files.copy(in, Paths.get(toFile.getCanonicalPath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}
	public void publish(WebDriver driver, MyVideoObject uploadObj) throws IOException {
		// TODO
	}	
}
