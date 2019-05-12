package com.walk_nie.myvideotr;

import java.io.IOException;
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
		
		List<TwitterObject> twList = parseFavolog(driver);
		
		List<MyVideoObject> videoObjs = parseTwitter(driver,twList);
		
		removeFromFavolog(driver, twList);
		
		return videoObjs;
	}

	private void removeFromFavolog(WebDriver driver, List<TwitterObject> twList) {
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
					if(txt.indexOf("削除") != -1){
						boolean found = false;
						for (TwitterObject tw : twList) {
							if(onclick.indexOf(tw.twid) != -1){
								found = true;
								break;
							}
						}
						if(found){
							ele2.click();
							break;
						}
					}
				}
			}
		}
		
	}

	private List<MyVideoObject> parseTwitter(WebDriver driver,
			List<TwitterObject> twList) {
		List<MyVideoObject> trList = Lists.newArrayList();
		for (TwitterObject tw : twList) {
			driver.get(tw.twurl);
			NieUtil.mySleepBySecond(2);
			
			String url = driver.getCurrentUrl();
			if(url.indexOf("video") != -1){
				
			}else if(url.indexOf("photo") != -1){
				WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"permalink-overlay\"]"));
				List<WebElement> eles = rootWe.findElements(By.cssSelector("div[class=\"halfWidthPhoto\"]"));
				for (WebElement ele : eles) {
					List<WebElement> eles1 = ele.findElements(By.className("AdaptiveMedia-photoContainer"));
					for (WebElement ele1 : eles1) {
						String dataUrl = ele1.getAttribute("data-image-url");
						if(!StringUtil.isBlank(dataUrl)){
							// TODO
						}
					}
				}
			}
		}
		return trList;
	}

	private List<TwitterObject> parseFavolog(WebDriver driver) {
		driver.get(favlog_url);
		logonFavolog(driver);
		
		WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"main\"]"));
		WebElement el1 = rootWe.findElement(By.cssSelector("div[id=\"update\"]"));
		el1.click();
		NieUtil.mySleepBySecond(2);
		rootWe = driver.findElement(By.cssSelector("div[id=\"main\"]"));
		List<WebElement> eles = rootWe.findElements(By.cssSelector("div[class=\"tl-tweets\"]"));
		
		List<TwitterObject> twList = Lists.newArrayList();
		for (WebElement ele : eles) {
			List<WebElement> eles1 = ele.findElements(By.className("tl-tweet"));
			for (WebElement ele1 : eles1) {
				String id = ele1.getAttribute("id");// tw1127569675770974208
				if(id.startsWith("tw")){
					id = id.substring("tw".length());
				}
				String href = "";
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
					TwitterObject obj = new TwitterObject();
					obj.twid = id;
					obj.twurl = href;
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
	
	class TwitterObject{
		String twurl ="";
		String twid = "";
	}
}
