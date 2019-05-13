package com.walk_nie.myvideotr;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.seleniumhq.jetty9.util.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class WeiboTr {
	public static void main(String[] args) throws IOException {
		WeiboTr weibo = new WeiboTr();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		weibo.scan(driver);
	}
	public void publish(WebDriver driver, MyVideoObject uploadObj) {
		String visitUrl = "https://www.weibo.com/like/outbox?leftnav=1";
		driver.get(visitUrl);
		logonWeibo(driver);

		List<File> multimediaContext = getToPublishFile(uploadObj);
		for(File f:multimediaContext){
			if(isVideoFile(f)){
				uploadVideo(driver, uploadObj, f);
			}else if(isVideoFile(f)){
				uploadPhoto(driver, uploadObj, f);
			}
		}

		WebElement elMain = driver.findElement(By.id("plc_main"));
		
		List<WebElement> elA = elMain.findElements(By.tagName("a"));
		for (WebElement el : elA) {
			if (el.getText().equals("完成")) {
				el.click();
				break;
			}
		}

		for (WebElement el : elA) {
			String attr = el.getAttribute("title");
			if (attr != null && attr.equals("发布微博按钮")) {
				el.click();
				break;
			}
		}
		
	}
	private void uploadPhoto(WebDriver driver, MyVideoObject uploadObj, File f) {
		// TODO
	}
	private void uploadVideo(WebDriver driver, MyVideoObject uploadObj, File f) {

		List<WebElement> elInputs = driver.findElements(By.tagName("input"));
		for (WebElement el : elInputs) {
			if (el.getAttribute("id").startsWith("publisher_upvideo")) {
				el.sendKeys(f.getAbsolutePath());
				break;
			}
		}
		WebElement elMain = driver.findElement(By.id("plc_main"));

		WebDriverWait wait1 = new WebDriverWait(driver,120);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					List<WebElement> elspans = elMain.findElements(By.tagName("dl"));
					for (WebElement el : elspans) {
						String attr = el.getAttribute("node-type");
						if (attr != null && attr.equals("uploading")) {
							String attr1 = el.getAttribute("style");
							if (attr1 != null && attr1.indexOf("block") == -1)
							break;
						}
					}
					return Boolean.TRUE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		
		NieUtil.mySleepBySecond(3);
	}
	private List<File> getToPublishFile(MyVideoObject uploadObj) {
		File uploadFoldFolder = MyVideoTrUtil.getVideoSaveFolder(uploadObj);
		File[] files = uploadFoldFolder.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				File f = new File(dir,name);
				if(isVideoFile(f))return true;
				if(isPhotoFile(f))return true;
				return false;
			}});
		List<File> multimediaContext = Lists.newArrayList();
		for(File file:files){
			multimediaContext.add(file);
		}
		return multimediaContext;
	}
	private boolean isVideoFile(File f) {
		String exd = MyVideoTrUtil.getFileExtention(f);
		if(exd.equalsIgnoreCase("mp4"))return true;
		if(exd.equalsIgnoreCase("flv"))return true;
		// FIXME more type...
		return false;
	}
	private boolean isPhotoFile(File f) {
		String exd = MyVideoTrUtil.getFileExtention(f);
		if(exd.equalsIgnoreCase("png"))return true;
		if(exd.equalsIgnoreCase("jpg"))return true;
		if(exd.equalsIgnoreCase("jpeg"))return true;
		// FIXME more type...
		return false;
	}

	public List<MyVideoObject> scan(WebDriver driver) {
		String visitUrl = "https://www.weibo.com/like/outbox?leftnav=1";
		driver.get(visitUrl);
		logonWeibo(driver);

		List<MyVideoObject> videoObjs = Lists.newArrayList();
		parseWeibo(driver, videoObjs);

//		((JavascriptExecutor) driver)
//				.executeScript("window.scrollTo(0, document.body.scrollHeight)");
//		NieUtil.mySleepBySecond(4);

		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
			parseWeibo(driver, videoObjs);
		} catch (AWTException e) {
		}
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
			parseWeibo(driver, videoObjs);
		} catch (AWTException e) {
		}
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
			parseWeibo(driver, videoObjs);
		} catch (AWTException e) {
		}
			

		return videoObjs;
	}

	private void parseWeibo(WebDriver driver, List<MyVideoObject> videoObjs) {
		List<WebElement> wes = driver.findElements(By.cssSelector("div[action-type=\"feed_list_item\"]"));
		for(WebElement we :wes){
			List<WebElement> wes1 = we.findElements(By.tagName("video"));
			if(wes1.isEmpty())continue;
			String mid = we.getAttribute("mid");
			MyVideoObject videoObj = new MyVideoObject();
			wes1 = we.findElements(By.cssSelector("div[node-type=\"feed_list_content\"]"));
			if(!wes1.isEmpty()){
				videoObj.title = wes1.get(0).getText();
			}

			String videoUrl = findVideoUrl(we);
			if(StringUtil.isBlank(videoUrl))continue;

			videoObj.url = StringUtil.isBlank(mid)?videoUrl:mid;
			videoObj.toType = "toYoutube";
			videoObj.fromType = "fromWeibo";
			videoObj.videoUrl = videoUrl;
		
			wes1 = we.findElements(By.cssSelector("div[class=\"WB_info\"]"));
			if(!wes1.isEmpty()){
				List<WebElement> wes2 = wes1.get(0).findElements(By.tagName("a"));
				for(WebElement we2:wes2){
					String nickName = we2.getAttribute("nick-name");
					if(!StringUtil.isBlank(nickName)){
						videoObj.uper = nickName.trim();break;
					}
				}
			}

			wes1 = we.findElements(By.cssSelector("div[class=\"W_fl\"]"));
			if(!wes1.isEmpty()){
				videoObj.fl = wes1.get(0).getText();
			}
			wes1 = we.findElements(By.cssSelector("div[class=\"W_fr\"]"));
			if(!wes1.isEmpty()){
				videoObj.fr = wes1.get(0).getText();
			}
			videoObjs.add(videoObj);
			//NieUtil.mySleepBySecond(1);
		}
	}
	
	private String findVideoUrl(WebElement we) {

		String videoUrl = "";
		List<WebElement> wes1 = we.findElements(By.cssSelector("li[action-type=\"feed_list_third_rend\"]"));
		if(!wes1.isEmpty()){
			for(WebElement we1 : wes1){
				String dataVal = we1.getAttribute("action-data");
				if(StringUtil.isBlank(dataVal)) continue;
				String[] sp = dataVal.split("&");
//				for(String s:sp){
//					try {
//						System.out.println(NieUtil.decode(s));
//					} catch (DecoderException e) {
//					}
//				}
				for(String s:sp){
					if(s.startsWith("short_url=")){
						videoUrl = NieUtil.decode(s.substring("short_url=".length()));
						return videoUrl;
					}
				}
			}
		}
		wes1 = we.findElements(By.cssSelector("li[action-type=\"feed_list_media_img\"]"));
		if(!wes1.isEmpty()){
			for(WebElement we1 : wes1){
				String dataVal = we1.getAttribute("action-data");
				if(StringUtil.isBlank(dataVal)) continue;
				String[] sp = dataVal.split("&");
//				for(String s:sp){
//					try {
//						System.out.println(NieUtil.decode(s));
//					} catch (DecoderException e) {
//					}
//				}
				for(String s:sp){
					if(s.startsWith("short_url=")){
						videoUrl = NieUtil.decode(s.substring("short_url=".length()));
						return videoUrl;
					}
				}
			}
		}
		return videoUrl;
	}
	public void logonWeibo(WebDriver driver) {

		WebDriverWait wait1 = new WebDriverWait(driver,60);
		wait1.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElement(By
							.cssSelector("ul[class=\"gn_nav_list\"]"));
					return Boolean.TRUE;
				} catch (Exception ex) {
				}
				return Boolean.FALSE;
			}
		});
		WebElement el1 = driver.findElement(By
				.cssSelector("ul[class=\"gn_nav_list\"]"));
		List<WebElement> eles = el1.findElements(By
				.cssSelector("em[class=\"S_txt1\"]"));
		for (WebElement ele : eles) {
			String txt = ele.getText();
			if (txt.indexOf("次郎花子") != -1) {
				return;
			}
		}

		wait1.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElement(By.cssSelector("div[id=\"pl_unlogin_home_login\"]"));
					return Boolean.TRUE;
				} catch (Exception ex) {
				}
				return Boolean.FALSE;
			}
		});
		el1 = driver.findElement(By.cssSelector("div[id=\"pl_unlogin_home_login\"]"));
		WebElement el2 = el1.findElement(By.cssSelector("a[node-type=\"normal_tab\"]"));
		el2.click();
		
		el2 = el1.findElement(By.cssSelector("input[id=\"loginname\"]"));
		el2.clear();
		el2.sendKeys(NieConfig.getConfig("myvideotr.weibo.user.name"));
		
		el2 = el1.findElement(By.cssSelector("input[name=\"password\"]"));
		el2.clear();
		el2.sendKeys(NieConfig.getConfig("myvideotr.weibo.user.password"));
		
		List<WebElement> wes = el1.findElements(By.tagName("span"));
		for(WebElement we :wes){
			if("登录".equals(we.getText())){
				we.click();
				break;
			}
		}

		NieUtil.mySleepBySecond(2);
		
		wait1 = new WebDriverWait(driver,60);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {

					WebElement el1 = driver.findElement(By.cssSelector("div[id=\"plc_top\"]"));
					List<WebElement> eles = el1.findElements(By.cssSelector("em[class=\"S_txt1\"]"));
					for(WebElement ele:eles){
						String txt = ele.getText();
						if(txt.indexOf("次郎花子") != -1){
							return Boolean.TRUE;
						}
					}
					return Boolean.FALSE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		
		//NieUtil.readLineFromSystemIn("Weibo login is finished? ANY KEY For already");
	}

	public boolean downloadVideo(WebDriver driver, MyVideoObject downloadObj) throws IOException  {

		String videoDownloadUrl = MyVideoTrUtil.getVideoDownloadUrl(driver, downloadObj);
		if (StringUtil.isBlank(videoDownloadUrl)) {
			return false;
		}
		File saveFile = getVideoSaveFile(downloadObj);
		MyVideoTrUtil.downLoadVideoFromUrl(videoDownloadUrl, saveFile);
		return true;
	}
	
	private File getVideoSaveFile(MyVideoObject downloadObj) {
		File outFolder = MyVideoTrUtil.getVideoSaveFolder(downloadObj);
		File saveFile = new File(outFolder, downloadObj.uid + ".mp4");
		return saveFile;
	}

}
