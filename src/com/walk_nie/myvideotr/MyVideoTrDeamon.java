package com.walk_nie.myvideotr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.seleniumhq.jetty9.util.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class MyVideoTrDeamon {

	private File logFile = null;
	YoutubePublisher youtube = new YoutubePublisher();
	WeiboPublisher weibo = new WeiboPublisher();
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		MyVideoTrDeamon main = new MyVideoTrDeamon();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		main.init();
		main.processByWebService(driver);
		//main.processByScanWeibo(driver);
		
		//AmznGiftCardObject noticeObj = main.getLastestNotice();
		//main.finishAmazonNoticeForAddCode(noticeObj.uid, main.mailAddress);
	}
	
	public void execute() throws IOException {
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		init(driver);
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
			} catch (Exception ex) {
				NieUtil.log(logFile, ex);
			}
		}
	}

	public void execute(WebDriver driver) throws IOException,
			MessagingException {

		processByWebService(driver);
		
		processByScanWeibo(driver);
	}
	
	private void processByScanWeibo(WebDriver driver) {
		logonWeibo(driver);
		
		driver.get("https://www.weibo.com/like/outbox?leftnav=1");
	
		List<MyVideoObject> videoObjs = Lists.newArrayList();
		List<WebElement> wes = driver.findElements(By.cssSelector("div[action-type=\"feed_list_item\"]"));
		for(WebElement we :wes){
			List<WebElement> wes1 = we.findElements(By.tagName("video"));
			if(wes1.isEmpty())continue;
			
			wes1 = we.findElements(By.cssSelector("li[action-type=\"feed_list_third_rend\"]"));
			if(wes1.isEmpty())continue;
			//String videoVal = wes1.get(0).getAttribute("video-sources");
			String dataVal = wes1.get(0).getAttribute("action-data");
			String[] sp = dataVal.split("&");
			String videoUrl = "";
			for(String s:sp){
				if(s.startsWith("short_url=")){
					videoUrl = s.substring("short_url=".length());
					break;
				}
			}
			if(StringUtil.isBlank(videoUrl))continue;

			MyVideoObject videoObj = new MyVideoObject();
			videoObj.url = videoUrl;
			
			videoObj.toType = "toYoutube";
			
			videoObj.videoUrl = videoObj.url;
			wes1 = we.findElements(By.cssSelector("div[node-type=\"feed_list_content\"]"));
			if(!wes1.isEmpty()){
				videoObj.title = wes1.get(0).getText();
			}
			
			try {
				Map<String, String> param = Maps.newHashMap();
				param.put("action", "listVideoStatusByUrl");
				param.put("url", videoObj.url);
				NieUtil.log(logFile, "[INFO][Service:listVideoStatusByUrl][Param]" + "[url]" + videoObj.url);

				String rslt = NieUtil.httpGet(NieConfig.getConfig("myvideotr.service.url"), param);

				if (!StringUtil.isBlank(rslt)) {
					NieUtil.log(logFile, "[INFO][Service:listVideoStatusByUrl][RESULT]" + rslt);
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
				NieUtil.log(logFile, "[ERROR][Service:listVideoStatusByUrl]" + e.getMessage());
				NieUtil.log(logFile, e);
				continue;
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
		}
		for(MyVideoObject videoObj:videoObjs){
			searchYT(driver, videoObj);
			insertVideo(videoObj);
		}
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
			uploadVideo(driver, uploadloadObj);
		}
	}

	private void uploadVideo(WebDriver driver, MyVideoObject uploadObj) {
		File savedFile = getVideoSaveFile(uploadObj);
		try {
			if ("toWeibo".equals(uploadObj.toType)) {
				logonWeibo(driver);
				weibo.publish(driver, uploadObj, savedFile);
			} else if ("toYoutube".equals(uploadObj.toType)) {
				logonYoutube(driver);
				youtube.publish(driver, uploadObj, savedFile);
			}

			updateVideoStatus(uploadObj.uid, "uled");
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, e);
			updateVideoStatus(uploadObj.uid, "ulfailure");
		}
	}

	private MyVideoObject getToUploadVideo() {
		MyVideoObject toULObj = getVideoObjectByExecuteServiceCommand("getByTouploadOne");
		return toULObj;
	}

	private void downloadVideo(WebDriver driver, MyVideoObject downloadObj) {
		try {
			String videoDownloadUrl = getVideoDownloadUrl(driver, downloadObj);
			if (StringUtil.isBlank(videoDownloadUrl)) {
				NieUtil.log(logFile, "[ERROR][Video][Download]url=" + downloadObj.videoUrl);
				updateVideoStatus(downloadObj.uid, "dlfailure");
				return;
			}
			File saveFile = getVideoSaveFile(downloadObj);
			downLoadVideoFromUrl(videoDownloadUrl, saveFile);
			//downloadObj.dlVideoPath = saveFile.getAbsolutePath();
			updateVideoStatus(downloadObj.uid, "dled");
		} catch (Exception e) {
			e.printStackTrace();
			updateVideoStatus(downloadObj.uid, "dlfailure");
		}
	}

	private File getVideoSaveFile(MyVideoObject downloadObj) {
		String outFolder = NieConfig.getConfig("myvideotr.video.folder") ;
		File saveFile = new File(outFolder, downloadObj.uid + ".mp4");
		return saveFile;
	}

	private void downLoadVideoFromUrl(String urlStr, File saveFile)
			throws IOException {
		NieUtil.log(logFile,"[INFO][Video][Downloading]" + urlStr);
		NieUtil.log(logFile,"[INFO][Video][Save File  ]" + saveFile.getCanonicalPath());
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setRequestProperty("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		InputStream inputStream = conn.getInputStream();
		byte[] getData = readInputStream(inputStream);
		if (!saveFile.getParentFile().exists()) {
			saveFile.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(saveFile);
		fos.write(getData);
		if (fos != null) {
			fos.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}
	}

	private byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

	private String getVideoDownloadUrl(WebDriver driver, MyVideoObject downloadObj) throws DecoderException {
		driver.get("https://www.parsevideo.com/");
		WebElement we = driver.findElement(By
				.cssSelector("input[id=\"url_input\"]"));
		we.clear();
		URLCodec codec = new URLCodec("UTF-8");
		String url = codec.decode(downloadObj.videoUrl);
		we.sendKeys(url);
		we = driver.findElement(By
				.cssSelector("button[id=\"url_submit_button\"]"));
		we.click();

		WebDriverWait wait1 = new WebDriverWait(driver,60);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					WebElement el1 = driver.findElement(By.cssSelector("div[id=\"message\"]"));
					if(!StringUtil.isBlank(el1.getText())){
						return Boolean.TRUE;
					}
					 el1 = driver.findElement(By.cssSelector("div[id=\"video\"]"));
					List<WebElement> eles = el1.findElements(By.cssSelector("li[class=\"list-group-item\"]"));
					if(!eles.isEmpty()){
						return Boolean.TRUE;
					}
					return Boolean.FALSE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"message\"]"));
		if(!StringUtil.isBlank(el1.getText())){
			// parse error!!
			return null;
		}
		
		we = driver.findElement(By.cssSelector("div[id=\"video\"]"));
		try{
			WebElement we1 = we.findElement(By.cssSelector("input[id=\"video5\"]"));
			return we1.getAttribute("value");
		}catch(Exception e){
		}
		try{
			WebElement we1 = we.findElement(By.cssSelector("input[id=\"video4\"]"));
			return we1.getAttribute("value");
		}catch(Exception e){
		}
		try{
			WebElement we1 = we.findElement(By.cssSelector("input[id=\"video3\"]"));
			return we1.getAttribute("value");
		}catch(Exception e){
		}
		try{
			WebElement we1 = we.findElement(By.cssSelector("input[id=\"video2\"]"));
			return we1.getAttribute("value");
		}catch(Exception e){
		}
		try{
			WebElement we1 = we.findElement(By.cssSelector("input[id=\"video1\"]"));
			return we1.getAttribute("value");
		}catch(Exception e){
		}
		try{
			WebElement we1 = we.findElement(By.cssSelector("input[id=\"video0\"]"));
			return we1.getAttribute("value");
		}catch(Exception e){
		}
		return null;
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
			NieUtil.log(logFile, "[INFO][Service:" + action + "][RESULT]" + rslt);
			if (StringUtil.isBlank(rslt)) {
				return null;
			}
			MyVideoObject obj  = new  MyVideoObject();
			Json j = new Json();
			Map<String, Object> objMap = null;
			objMap = j.toType(rslt, Map.class);
			obj.uid = (String) objMap.get("uid");
			obj.url = (String) objMap.get("url");
			obj.videoUrl = (String) objMap.get("videoUrl");
			obj.toType = (String) objMap.get("toType");
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
		}else if(urlTrue.indexOf("bilibili.com") != -1){
			parseForBilibili(driver, videoObj);
		}
		searchYT(driver, videoObj);
		updateVideoUper(videoObj);
	}

	private void searchYT(WebDriver driver, MyVideoObject videoObj) {
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

	private void updateVideoUper(MyVideoObject videoObj) {
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "updateByVideoUper");
			param.put("uid", videoObj.uid);
			param.put("uper", videoObj.uper);
			param.put("title", videoObj.title);
			param.put("ytSearchRslt", videoObj.ytSearchRslt);
			param.put("videoUrl", videoObj.videoUrl);
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
		
		List<WebElement> wes = driver.findElements(By.cssSelector("h2[class=\"title\"]"));
		if(!wes.isEmpty()){
			videoObj.title = wes.get(0).getText();
		}
		wes = driver.findElements(By.cssSelector("a[class=\"media-user\"]"));
		if(!wes.isEmpty()){
			videoObj.uper = wes.get(0).getText();
		}
	}

	private void parseForBilibili(WebDriver driver, MyVideoObject videoObj) {

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

	protected boolean isWeiboTag(WebDriver driver) {
	
		return false;
	}

	protected void parseForWeiboTag(WebDriver driver, MyVideoObject videoObj) {
	
		/*
		driver.get(videoObj.url);
		
		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"pl_topic_header\"]"));
		String title = el1.findElement(By.tagName("h1")).getText();
		title = title.replaceAll("#", "");
		videoObj.title = title;
		List<WebElement> eles = el1.findElements(By.tagName("a"));
		for (WebElement ele : eles) {
			String txt = ele.getText();
			if (txt.indexOf("视频") != -1) {
				ele.click();
				break;
			}
		}
		el1 = driver.findElement(By.cssSelector("div[id=\"pl_feedlist_index\"]"));
		List<WebElement> wes = el1.findElements(By.cssSelector("div[class=\"card-wrap\"]"));
		
		for(WebElement we :wes){
			String url = "";
			MyVideoObject newObj = new MyVideoObject();
			newObj.url = videoObj.url;
			newObj.videoUrl = url;
			newObj.title = videoObj.title;
			newObj.groupUid = videoObj.uid;
			insertVideo(newObj);
		}
		*/
	}

	private void insertVideo(MyVideoObject obj) {

		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "insertVideo");
			param.put("url", obj.url);
			param.put("videoUrl", obj.videoUrl);
			param.put("toType", obj.toType);
			param.put("title", obj.title);
			param.put("uper", obj.uper);
			param.put("groupUid", obj.groupUid);
			param.put("ytSearchRslt", obj.ytSearchRslt);
			NieUtil.log(logFile, "[INFO][Service:insertVideo][Param]" + "[url]" + obj.url + "[title]" + obj.title + "[uper]" + obj.uper);

			String rslt = NieUtil.httpGet(NieConfig.getConfig("myvideotr.service.url"), param);

			if (!StringUtil.isBlank(rslt)) {
				NieUtil.log(logFile, "[INFO][Service:insertVideo][RESULT]" + rslt);
			}
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:insertVideo]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
	}

	public void init() throws IOException {

		logFile = new File(NieConfig.getConfig("myvideotr.log.file"));

		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
		
	}

	public void init(WebDriver driver) throws IOException {
		init();
		
		//logonYoutube(driver);
		
		//logonWeibo(driver);
	}
	private void logonWeibo(WebDriver driver) {

		//driver.get(rootUrl);
		try {
			WebElement el1 = driver.findElement(By
					.cssSelector("div[id=\"plc_top\"]"));
			List<WebElement> eles = el1.findElements(By
					.cssSelector("em[class=\"S_txt1\"]"));
			for (WebElement ele : eles) {
				String txt = ele.getText();
				if (txt.indexOf("次郎花子") != -1) {
					return;
				}
			}
		} catch (Exception ex) {
		}

		String rootUrl = "https://www.weibo.com/";
		driver.get(rootUrl);
		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"pl_unlogin_home_login\"]"));
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
		
		WebDriverWait wait1 = new WebDriverWait(driver,60);
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
		
		el1 = driver.findElement(By.cssSelector("div[id=\"plc_top\"]"));
		List<WebElement> eles = el1.findElements(By.cssSelector("em[class=\"S_txt1\"]"));
		for(WebElement ele:eles){
			String txt = ele.getText();
			if(txt.indexOf("次郎花子") != -1){
				return;
			}
		}
		
		NieUtil.readLineFromSystemIn("Weibo login is finished? ANY KEY For already");
	}

	private void logonYoutube(WebDriver driver) {

		String rootUrl = "https://www.youtube.com/";

		driver.get(rootUrl);
		List<WebElement> wes = driver.findElements(By.cssSelector("yt-formatted-string[id=\"text\"]"));
		for(WebElement we :wes){
			if("ログイン".equals(we.getText())){
				return;
			}
		}
		
		WebElement el1 = driver.findElement(By.cssSelector("input[id=\"identifierId\"]"));
		el1.sendKeys(NieConfig.getConfig("myvideotr.youtube.user.name"));
		
		wes = driver.findElements(By.tagName("span"));
		for(WebElement we :wes){
			if("次へ".equals(we.getText())){
				we.click();
				break;
			}
		}
		NieUtil.mySleepBySecond(2);
		
		WebElement el2 = driver.findElement(By.cssSelector("input[name=\"password\"]"));
		el2.sendKeys(NieConfig.getConfig("myvideotr.youtube.user.password"));
		
		wes = driver.findElements(By.tagName("span"));
		for(WebElement we :wes){
			if("次へ".equals(we.getText())){
				we.click();
				break;
			}
		}

		NieUtil.mySleepBySecond(2);
		
		List<WebElement> eles = driver.findElements(By.cssSelector("button[id=\"avatar-btn\"]"));
		for(WebElement ele:eles){
			String txt = ele.getAttribute("aria-label");
			if(txt.indexOf("アカウントのプロフィール写真。クリックすると、他のアカウントのリストが表示されます") != -1){
				return;
			}
		}
		
		NieUtil.readLineFromSystemIn("YOUTUBE login is finished? ANY KEY For already");
	}
}
