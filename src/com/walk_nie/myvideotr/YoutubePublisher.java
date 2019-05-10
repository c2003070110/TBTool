package com.walk_nie.myvideotr;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.walk_nie.taobao.util.WebDriverUtil;

public class YoutubePublisher {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		YoutubePublisher main = new YoutubePublisher();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		MyVideoObject uploadObj = new MyVideoObject();
		File uploadFile = new File("");
		main.publish(driver, uploadObj, uploadFile);
	}
	

	public void publish(WebDriver driver, MyVideoObject uploadObj, File uploadFile) {
		List<WebElement> wes = driver.findElements(By.cssSelector("button[id=\"button\"]"));
		for (WebElement we : wes) {
			if ("動画または投稿を作成".equals(we.getAttribute("aria-label"))) {
				we.click();
				break;
			}
		}
		wes = driver.findElements(By.cssSelector("yt-formatted-string[id=\"label\"]"));
		for (WebElement we : wes) {
			if ("動画をアップロード".equals(we.getText())) {
				we.click();
				break;
			}
		}
		WebElement we = driver.findElement(By.cssSelector("button[id=\"upload-privacy-selector\"]"));
		we.click();

		// wes = driver.findElements(By.tagName("span"));
		// for (WebElement we1 : wes) {
		// if ("非公開".equals(we1.getText())) {
		// we1.click();
		// break;
		// }
		// }
		wes = driver.findElements(By.cssSelector("input[type=\"file\"]"));

		wes.get(0).sendKeys(uploadFile.getAbsolutePath());
		// savedFile.delete();

		WebElement weRoot = driver.findElement(By.cssSelector("form[name=\"mdeform\"]"));
		we = weRoot.findElement(By.cssSelector("input[name=\"title\"]"));
		we.clear();
		we.sendKeys(uploadObj.title);
		// set description for bilibili
		// we =
		// weRoot.findElement(By.cssSelector("input[name=\"description\"]"));
		// we.clear();
		// we.sendKeys("");

		WebDriverWait wait1 = new WebDriverWait(driver, 60);
		wait1.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					WebElement weRoot = driver.findElement(By.cssSelector("div[id=\"active-uploads-contain\"]"));
					List<WebElement> eles = weRoot.findElements(By.tagName("button"));
					for (WebElement ele : eles) {
						String title = ele.getAttribute("title");
						if (title.indexOf("この動画を今すぐ公開します") != -1) {
							ele.click();
							return Boolean.TRUE;
						}
					}
					return Boolean.FALSE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
	}

	public void searchYT(WebDriver driver, MyVideoObject videoObj) {
		driver.get("https://www.youtube.com/");

		WebElement we = driver.findElement(By
				.cssSelector("input[id=\"search\"]"));
		we.clear();
		we.sendKeys(videoObj.title);
		we = driver.findElement(By
				.cssSelector("button[id=\"search-icon-legacy\"]"));
		we.click();

		try {
			we = driver.findElement(By.cssSelector("div[id=\"contents\""));
			List<WebElement> wes = driver.findElements(By
					.tagName("ytd-channel-renderer"));
			if (!wes.isEmpty()) {
				we = wes.get(0).findElement(
						By.cssSelector("h3[id=\"channel-title\""));
				videoObj.ytSearchRslt += we.getText();
			}
			wes = driver.findElements(By.tagName("ytd-video-renderer"));
			if (!wes.isEmpty()) {
				we = wes.get(0).findElement(
						By.cssSelector("div[id=\"title-wrapper\""));
				videoObj.ytSearchRslt += we.getText();
			}
		} catch (Exception e) {

		}

		driver.get("https://www.youtube.com/");
		we = driver.findElement(By.cssSelector("input[id=\"search\"]"));
		we.clear();
		we.sendKeys(videoObj.uper);
		we = driver.findElement(By
				.cssSelector("button[id=\"search-icon-legacy\""));
		we.click();
		try {
			we = driver.findElement(By.cssSelector("div[id=\"contents\""));

			List<WebElement> wes = driver.findElements(By
					.tagName("ytd-channel-renderer"));
			if (!wes.isEmpty()) {
				we = wes.get(0).findElement(
						By.cssSelector("h3[id=\"channel-title\""));
				videoObj.ytSearchRslt += we.getText();
			}
			wes = driver.findElements(By.tagName("ytd-video-renderer"));
			if (!wes.isEmpty()) {
				we = wes.get(0).findElement(
						By.cssSelector("div[id=\"title-wrapper\""));
				videoObj.ytSearchRslt += we.getText();
			}
		} catch (Exception e) {

		}
	}
 
}
