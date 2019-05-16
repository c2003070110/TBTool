package com.walk_nie.myvideotr;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Maps;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class MyVideoTrDeamon {

	private File logFile = null;
	YoutubeTr youtube = new YoutubeTr();
	WeiboTr weibo = new WeiboTr();
	TwitterTr tw = new TwitterTr();
	TiktokTr tk = new TiktokTr();
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		MyVideoTrDeamon main = new MyVideoTrDeamon();
		main.init();
		//WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		//main.processByWebService(driver);
		//main.processByScanWeibo(driver);
		
		main.execute();
	}
	
	public void execute() throws IOException {
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		int interval = Integer.parseInt(NieConfig.getConfig("myvideotr.interval"));// second
		while (true) {
			try {
				long t1 = System.currentTimeMillis();
				execute(driver);
				long t2 = System.currentTimeMillis();
				long dif = t2 - t1;
				if (dif < interval * 1000) {
					NieUtil.log(logFile, "[SLEEP]zzzZZZzzz...");
					NieUtil.mySleepBySecond((new Long(interval - dif / 1000)).intValue());
				}
			}catch(org.openqa.selenium.UnhandledAlertException e1){
				e1.printStackTrace();
				driver.switchTo().alert().accept();
			}catch(org.openqa.selenium.WebDriverException e){
				e.printStackTrace();
				driver.close();
				NieUtil.mySleepBySecond(3);
				driver = WebDriverUtil.getFirefoxWebDriver();
			} catch (Exception ex) {
				ex.printStackTrace();
				NieUtil.log(logFile, ex);
			}
		}
	}

	public void execute(WebDriver driver) throws IOException,
			MessagingException {

		processByWebService(driver);
		processByScanWeibo(driver);
		processByScanTwitter(driver);
	}
	
	private void processByScanTwitter(WebDriver driver) {
		tw.scan(driver);
		//List<MyVideoObject> videoObjs = tw.scan(driver);
		//for (MyVideoObject videoObj : videoObjs) {
			// searchYT(driver, videoObj);
			//insertVideo(videoObj);
		//}
		//tw.removeFromTwitter(driver, videoObjs);
	}
	
	private void processByScanWeibo(WebDriver driver) {
		weibo.scan(driver);
		//List<MyVideoObject> videoObjs = weibo.scan(driver);
		//for (MyVideoObject videoObj : videoObjs) {
			// searchYT(driver, videoObj);
			//insertVideo(videoObj);
			//weibo.removeFromFav(driver, videoObj);
		//}
	}
	
	private void processByWebService(WebDriver driver) {
		MyVideoObject noticeObj = getLastestVideo();
		if (noticeObj != null) {
			try {
				parseVideoUper(driver, noticeObj);
			} catch (Exception e) {
				e.printStackTrace();
				NieUtil.log(logFile,
						"[ERROR][Service:updateByVideoUper]" + e.getMessage());
				NieUtil.log(logFile, e);
				updateVideoStatus(noticeObj.uid, "parsefailure");
			}
		}
		MyVideoObject downloadObj = getToDownloadVideo();
		if (downloadObj != null) {
			downloadVideo(driver, downloadObj);
		}
		MyVideoObject uploadloadObj = getToUploadVideo();
		if (uploadloadObj != null) {
			publish(driver, uploadloadObj);
		}
	}

	private void publish(WebDriver driver, MyVideoObject uploadObj) {
		try {
			boolean successfulFlag = false;
			if ("toWeibo".equals(uploadObj.toType)) {
				successfulFlag = weibo.publish(driver, uploadObj);
			} else if ("toYoutube".equals(uploadObj.toType)) {
				successfulFlag = youtube.publish(driver, uploadObj);
			} else if ("toTwitter".equals(uploadObj.toType)) {
				successfulFlag = tw.publish(driver, uploadObj);
			}
			if (successfulFlag) {
				File uploadFoldFolder = MyVideoTrUtil.getSaveFolder(uploadObj);
				FileUtils.deleteDirectory(uploadFoldFolder);
				updateVideoStatus(uploadObj.uid, "uled");
			} else {
				updateVideoStatus(uploadObj.uid, "ulfailure");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			NieUtil.log(logFile, "[ERROR][publish]" + ex.getMessage());
			NieUtil.log(logFile, ex);
			updateVideoStatus(uploadObj.uid, "ulfailure");
		}
	}

	private MyVideoObject getToUploadVideo() {
		MyVideoObject toULObj = getVideoObjectByExecuteServiceCommand("getByTouploadOne");
		return toULObj;
	}

	private void downloadVideo(WebDriver driver, MyVideoObject downloadObj) {
		try {
			String fromType = downloadObj.fromType;
			
			boolean rsltFlag = false;
			if ("fromWeibo".equals(fromType)) {
				rsltFlag = weibo.downloadVideo(driver, downloadObj);
			} else if ("fromTwitter".equals(fromType)) {
				rsltFlag = tw.downloadVideo(driver, downloadObj);
			} else if ("fromTiktokJP".equals(fromType) || "fromTiktokCN".equals(fromType)) {
				rsltFlag = tk.downloadVideo(driver, downloadObj);
			}else{
				NieUtil.log(logFile, "[ERROR][fromType is WRONG]" +fromType);
				updateVideoStatus(downloadObj.uid, "dlfailure");
				return;
			}
			if (rsltFlag) {
				updateVideoStatus(downloadObj.uid, "dled");
			} else {
				updateVideoStatus(downloadObj.uid, "dlfailure");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			NieUtil.log(logFile, "[ERROR][downloadVideo]" + ex.getMessage());
			NieUtil.log(logFile, ex);
			updateVideoStatus(downloadObj.uid, "dlfailure");
		}
	}

	private MyVideoObject getToDownloadVideo() {
		MyVideoObject toDLObj = getVideoObjectByExecuteServiceCommand("getByTodownloadOne");
		return toDLObj;
	}
	
	private MyVideoObject getVideoObjectByExecuteServiceCommand(String action){

		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", action);
			
			String rslt = NieUtil.httpGet(NieConfig.getConfig("myvideotr.service.url"), param);
			//NieUtil.log(logFile, "[INFO][Service:" + action + "][RESULT]" + rslt);
			System.out.println("[INFO][Service:" + action + "][RESULT]" + rslt);
			if (StringUtil.isBlank(rslt)) {
				return null;
			}
			MyVideoObject obj  = new  MyVideoObject();
			Json j = new Json();
			Map<String, Object> objMap = null;
			objMap = j.toType(rslt, Map.class);
			obj.uid = (String) objMap.get("uid");
			obj.url = (String) objMap.get("url");
			obj.title = (String) objMap.get("title");
			obj.uper = (String) objMap.get("uper");
			obj.videoUrl = (String) objMap.get("videoUrl");
			obj.toType = (String) objMap.get("toType");
			obj.fromType = (String) objMap.get("fromType");
			obj.trid = (String) objMap.get("trid");
			if (StringUtil.isBlank(obj.url)) {
				NieUtil.log(logFile, "[ERROR][executeServiceCommand]URL is NULL!!");
				return null;
			}
			obj.groupUid =  (String) objMap.get("groupUid");
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:" + action + "]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
		return null;
	}

	private void updateVideoStatus(String uid, String status) {
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "updateStatus");
			param.put("uid", uid);
			param.put("status", status);
			NieUtil.log(logFile, "[INFO][Service:updateStatus][Param]" + "[uid]" + uid + "[status]" + status);

			String rslt = NieUtil.httpGet(NieConfig.getConfig("myvideotr.service.url"), param);

			NieUtil.log(logFile, "[INFO][Service:updateStatus][RESULT]" + rslt);
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:updateStatus]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
	}

	private MyVideoObject getLastestVideo() {
		MyVideoObject obj = getVideoObjectByExecuteServiceCommand("getLastestVideoOne");
		return obj;
	}

	public void parseVideoUper(WebDriver driver ,MyVideoObject videoObj) {
	
		driver.get(videoObj.url);
		
		String urlTrue = driver.getCurrentUrl();
		videoObj.videoUrl = urlTrue;
		if(urlTrue.indexOf("365yg.com") != -1){
			//阳光宽频网·toutiao
			parseFor365yg(driver, videoObj);
			searchYT(driver, videoObj);
			updateVideoUper(videoObj);
		}else if(urlTrue.indexOf("bilibili.com") != -1){
			parseForBilibili(driver, videoObj);
			searchYT(driver, videoObj);
			updateVideoUper(videoObj);
		}else if(urlTrue.indexOf("tiktok.com") != -1){
			parseForTiktok(driver, videoObj);
			updateVideoUper(videoObj);
		}
	}

	private void searchYT(WebDriver driver, MyVideoObject videoObj) {

		if (StringUtil.isNotBlank(videoObj.title)) {
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
		}

		if (StringUtil.isNotBlank(videoObj.title)) {
			driver.get("https://www.youtube.com/");
			WebElement we = driver.findElement(By
					.cssSelector("input[id=\"search\"]"));
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

	private void updateVideoUper(MyVideoObject videoObj) {
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "updateByVideoUper");
			param.put("uid", videoObj.uid);
			param.put("uper", videoObj.uper);
			param.put("title", videoObj.title);
			param.put("ytSearchRslt", videoObj.ytSearchRslt);
			param.put("videoUrl", videoObj.videoUrl);
			param.put("fromType", videoObj.fromType);
			param.put("toType", videoObj.toType);
			NieUtil.log(logFile, "[INFO][Service:updateByVideoUper][Param]" + "[uid]" + videoObj.uid + "[uper]" + videoObj.uper + "[title]" + videoObj.title);

			String rslt = NieUtil.httpGet(NieConfig.getConfig("myvideotr.service.url"), param);

			NieUtil.log(logFile, "[INFO][Service:updateByVideoUper][RESULT]" + rslt);
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:updateByVideoUper]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
		
	}

	private void parseFor365yg(WebDriver driver, MyVideoObject videoObj) {
		videoObj.fromType = "toutiao";
		videoObj.toType = "toYoutube";
		List<WebElement> wes = driver.findElements(By.cssSelector("h2[class=\"title\"]"));
		if(!wes.isEmpty()){
			videoObj.title = wes.get(0).getText();
		}
		wes = driver.findElements(By.cssSelector("a[class=\"media-user\"]"));
		if(!wes.isEmpty()){
			videoObj.uper = wes.get(0).getText();
		}
	}

	private void parseForTiktok(WebDriver driver, MyVideoObject videoObj) {
		// TODO
		videoObj.videoUrl = videoObj.url;
		boolean isTiktokJP = false;
		boolean isTiktokCN = false;
		if(isTiktokJP){
			videoObj.fromType = "fromTiktokJP";
			videoObj.toType = "toWeibo";
			videoObj.uper = "";
			videoObj.uper = "";
		}else if(isTiktokCN){
			videoObj.fromType = "fromTiktokCN";
			videoObj.toType = "toTwitter";
			videoObj.uper = "";
			videoObj.uper = "";
		}
	}

	private void parseForBilibili(WebDriver driver, MyVideoObject videoObj) {

		videoObj.fromType = "toutiao";
		videoObj.toType = "toYoutube";
		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"viewbox_report\"]"));
		List<WebElement> wes = el1.findElements(By.cssSelector("h1[class=\"video-title\"]"));
		if(!wes.isEmpty()){
			videoObj.title = wes.get(0).getText();
		}
		el1 = driver.findElement(By.cssSelector("div[id=\"v_upinfo\"]"));
		wes = driver.findElements(By.cssSelector("a[class=\"username\"]"));
		if(!wes.isEmpty()){
			videoObj.uper = wes.get(0).getText();
		}
		videoObj.videoUrl = videoObj.url;
	}

	protected void parseForWeibo(WebDriver driver, MyVideoObject videoObj) {

		driver.get("https://www.weibo.com/1449729883/profile?rightmod=1&wvr=6&mod=personnumber&is_all=1");
		List<WebElement> wes = driver.findElements(By.cssSelector("h2[class=\"title\"]"));
		if(!wes.isEmpty()){
			videoObj.title = wes.get(0).getText();
		}
		wes = driver.findElements(By.cssSelector("a[class=\"media-user\"]"));
		if(!wes.isEmpty()){
			videoObj.uper = wes.get(0).getText();
		}
		videoObj.videoUrl = videoObj.url;
	}

	public void init() throws IOException {

		logFile = new File(NieConfig.getConfig("myvideotr.log.file"));

		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
	}

}
