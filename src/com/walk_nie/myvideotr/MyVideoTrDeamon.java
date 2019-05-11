package com.walk_nie.myvideotr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		//WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		main.init();
		//main.processByWebService(driver);
		//main.processByScanWeibo(driver);
		
		//AmznGiftCardObject noticeObj = main.getLastestNotice();
		//main.finishAmazonNoticeForAddCode(noticeObj.uid, main.mailAddress);
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
	}
	
	private void processByScanWeibo(WebDriver driver) {
	
		List<MyVideoObject> videoObjs = weibo.processByScanWeibo(driver);
		for(MyVideoObject videoObj:videoObjs){
			//searchYT(driver, videoObj);
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
				weibo.publish(driver, uploadObj, savedFile);
			} else if ("toYoutube".equals(uploadObj.toType)) {
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
				URLCodec codec = new URLCodec("UTF-8");
				String url = codec.decode(downloadObj.videoUrl);
				NieUtil.log(logFile, "[ERROR][Video][Download]url=" + url);
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
		String url = NieUtil.decode(downloadObj.videoUrl);
		we.sendKeys(url);
		we = driver.findElement(By
				.cssSelector("button[id=\"url_submit_button\"]"));
		we.click();
		
		NieUtil.mySleepBySecond(5);

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
		List<String> videoUrlList = Lists.newArrayList();
		we = driver.findElement(By.cssSelector("div[id=\"video\"]"));
		
		List<WebElement> eles = we.findElements(By.cssSelector("li[class=\"list-group-item\"]"));
		for(WebElement we1 : eles){
			List<WebElement> eles2 = we1.findElements(By.tagName("input"));
			if(eles2.isEmpty())continue;
			String vurl = eles2.get(0).getAttribute("value");
			if(vurl.toLowerCase().indexOf(".mp4") == -1) continue;
			videoUrlList.add(vurl);
		}
		Collections.sort(videoUrlList, new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {

				Pattern p = Pattern.compile("\\d+{3,4}x{1}\\d+{3,4}");
				Matcher m = p.matcher(arg0); 
				String str0 = null;
				while (m.find()) {
					str0 = m.group();
				}
				m = p.matcher(arg1); 
				String str1 = null;
				while (m.find()) {
					str1 = m.group();
				}
				
				return str1.compareTo(str0);
			}
		});
		
		return videoUrlList.get(0);
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
			//NieUtil.log(logFile, "[INFO][Service:insertVideo][Param]" + "[url]" + obj.url + "[title]" + obj.title + "[uper]" + obj.uper);

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

}
