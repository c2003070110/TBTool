package com.walk_nie.myvideotr;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.util.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class YoutubeTr {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		YoutubeTr main = new YoutubeTr();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		MyVideoObject uploadObj = new MyVideoObject();
		main.publish(driver, uploadObj);
	}
	
	public boolean publish(WebDriver driver, MyVideoObject uploadObj) {
		logonYoutube(driver);
		File uploadFold = MyVideoTrUtil.getSaveFolder(uploadObj);
		// all of folder to upload
		File[] files = uploadFold.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".mp4"))return true;
				return false;
			}
		});
		for (File uploadFile : files) {
			publishFile(driver, uploadObj, uploadFile);
		}
		return true;
	}

	private void publishFile(WebDriver driver, MyVideoObject uploadObj, File uploadFile) {
		List<WebElement> wes = driver.findElements(By.cssSelector("button[id=\"button\"]"));
		for (WebElement we : wes) {
			String txt = we.getAttribute("aria-label");
			if(StringUtil.isBlank(txt)) continue;
			if ("動画または投稿を作成".equals(txt)
					|| txt.equalsIgnoreCase("Create a video or post")) {
				we.click();
				break;
			}
		}
		//logonYoutube(driver);
		NieUtil.mySleepBySecond(1);
		wes = driver.findElements(By.cssSelector("yt-formatted-string[id=\"label\"]"));
		for (WebElement we : wes) {
			String txt = we.getText();
			if ("動画をアップロード".equals(txt)
					|| txt.equalsIgnoreCase("Upload video")) {
				we.click();
				break;
			}
		}
		WebDriverWait wait1 = new WebDriverWait(driver,30);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					WebElement we = driver.findElement(By.cssSelector("button[id=\"upload-privacy-selector\"]"));
					we.click();
					return Boolean.TRUE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
	
		NieUtil.mySleepBySecond(2);

		wes = driver.findElements(By.cssSelector("input[type=\"file\"]"));
		wes.get(0).sendKeys(uploadFile.getAbsolutePath());
		NieUtil.mySleepBySecond(1);

		WebElement weRoot = driver.findElement(By.cssSelector("form[name=\"mdeform\"]"));
		WebElement we = weRoot.findElement(By.cssSelector("input[name=\"title\"]"));
		we.clear();
		we.sendKeys(uploadObj.title);
		// set description for bilibili
		// we =
		// weRoot.findElement(By.cssSelector("input[name=\"description\"]"));
		// we.clear();
		// we.sendKeys("");
		wait1 = new WebDriverWait(driver,600);
		wait1.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					List<WebElement> eles = driver.findElements(By.cssSelector("div[class=\"progress-bar-processing\"]"));
					for (WebElement ele : eles) {
						String text = ele.getText();
						if (text.indexOf("処理が完了しました") != -1
								|| text.toLowerCase().indexOf("Processing Done".toLowerCase()) != -1) {
							return Boolean.TRUE; 
						}
					}
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		weRoot = driver.findElement(By.cssSelector("div[class=\"metadata-save-button\"]"));
		List<WebElement>  eles1 = weRoot.findElements(By.tagName("button"));
		for (WebElement ele1 : eles1) {
			String title = ele1.getAttribute("title");
			System.out.println(title);
			ele1.click();
			try {
				ele1.click();
			} catch (Exception e) {
			}
			break;
		}

		wait1 = new WebDriverWait(driver,600);
		wait1.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					List<WebElement> eles = driver.findElements(By.cssSelector("input[name=\"share_url\"]"));
					for (WebElement ele : eles) {
						String text = ele.getAttribute("title");
						if (text.indexOf("リンクを共有") != -1
								|| text.toLowerCase().indexOf("Share link".toLowerCase()) != -1) {
							return Boolean.TRUE; 
						}
					}
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
	private void logonYoutube(WebDriver driver) {

		String rootUrl = "https://www.youtube.com/";

		driver.get(rootUrl);
		NieUtil.mySleepBySecond(5);
		List<WebElement> wes = driver.findElements(By.tagName("yt-formatted-string"));
		boolean islogon = true;
		for (WebElement we : wes) {
			String txt = we.getText();
			if(StringUtil.isBlank(txt)) continue;
			System.out.println("[logonYoutube]" + txt);
			if ("ログイン".equalsIgnoreCase(txt)||"Sign in".equalsIgnoreCase(txt)) {
				we.click();
				islogon = false;
				break;
			}
		}
		if(islogon)return;
		
		try {
			WebElement el1 = driver.findElement(By
					.cssSelector("input[id=\"identifierId\"]"));
			NieUtil.mySleepBySecond(1);
			el1.sendKeys(NieConfig.getConfig("myvideotr.youtube.user.name"));
			List<WebElement> wes1 = driver.findElements(By.tagName("span"));
			for (WebElement we1 : wes1) {
				String txt = we1.getText();
				if(StringUtil.isBlank(txt)) continue;
				if ("次へ".equalsIgnoreCase(txt)
						|| "Next".equalsIgnoreCase(txt)) {
					we1.click();
					break;
				}
			}
		} catch (Exception e) {
		}

		try {
			WebElement el2 = driver.findElement(By
					.cssSelector("input[name=\"password\"]"));
			NieUtil.mySleepBySecond(1);
			el2.sendKeys(NieConfig.getConfig("myvideotr.youtube.user.password"));

			List<WebElement> wes1 = driver.findElements(By.tagName("span"));
			for (WebElement we1 : wes1) {
				String txt = we1.getText();
				if(StringUtil.isBlank(txt)) continue;
				if ("次へ".equalsIgnoreCase(txt)
						|| "Next".equalsIgnoreCase(txt)) {
					we1.click();
					break;
				}
			}
			NieUtil.mySleepBySecond(2);
		} catch (Exception e) {
		}
		
		List<WebElement> eles = driver.findElements(By.cssSelector("button[id=\"avatar-btn\"]"));
		for(WebElement ele:eles){
			String txt = ele.getAttribute("aria-label");
			if(txt.indexOf("アカウントのプロフィール写真。クリックすると、他のアカウントのリストが表示されます") != -1
					|| txt.toLowerCase().indexOf("Account profile photo that opens list of".toLowerCase()) != -1){
				return;
			}
		}
		
		NieUtil.readLineFromSystemIn("youtube need to login manually!!!login manually and press ANY KEY to continue");
	}
 
}
