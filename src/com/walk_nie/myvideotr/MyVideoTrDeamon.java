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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.json.Json;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Maps;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class MyVideoTrDeamon {

	private File logFile = null;
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		MyVideoTrDeamon main = new MyVideoTrDeamon();
		//main.init();
		main.execute();
		
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
		// TODO wo de zai list!
		driver.get("XXX");
		
		/*
		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"pl_topic_header\"]"));
		List<WebElement> eles = el1.findElements(By.tagName("a"));
		for (WebElement ele : eles) {
			String txt = ele.getText();
			if (txt.indexOf("视频") != -1) {
				ele.click();
				break;
			}
		}
		*/
		List<WebElement> wes = driver.findElements(By.cssSelector("div[action-type=\"feed_list_item\"]"));
		for(WebElement we :wes){
			List<WebElement> wes1 = we.findElements(By.tagName("video"));
			if(wes1.isEmpty())continue;
			
			wes1 = we.findElements(By.cssSelector("div[node-type=\"fl_h5_video_disp\"]"));
			if(wes1.isEmpty())continue;
			
			Actions actions = new Actions(driver);
			actions.doubleClick(wes1.get(0)).perform();
			
			wes1 = we.findElements(By.cssSelector("li[class=\"wbv-menu-item\"]"));
			if(wes1.isEmpty())continue;
			for (WebElement ele : wes1) {
				String txt = ele.getText();
				if (txt.indexOf("视频地址") != -1) {
					ele.click();
					break;
				}
			}
			
			MyVideoObject videoObj = new MyVideoObject();
			
			wes1 = we.findElements(By.cssSelector("input[name=\"CopyUrlInput\"]"));
			if(wes1.isEmpty())continue;
			videoObj.url = wes1.get(0).getAttribute("value");
			videoObj.videoUrl = videoObj.url;
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
			// TODO
			String title = "";
			
			videoObj.title = title;
	
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
//		List<MyVideoObject> uploadloadObjList = getToMergeVideo();
//		if (uploadloadObjList != null && !uploadloadObjList.isEmpty()) {
//			mergeAndUploadVideo(driver, uploadloadObjList);
//		}
	}

//	private void mergeAndUploadVideo(WebDriver driver, List<MyVideoObject> uploadloadObjList) {
//		MyVideoObject uploadloadObj = mergeVideo(uploadloadObjList);
//		uploadVideo(driver, uploadloadObj);
//	}

//	private MyVideoObject mergeVideo(List<MyVideoObject> uploadloadObjList) {
//		try {
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			NieUtil.log(logFile, e);
//			for (MyVideoObject obj : uploadloadObjList) {
//				updateStatus(obj.uid, "mgfailure");
//			}
//		}
//		return null;
//	}

//	private List<MyVideoObject> getToMergeVideo() {
//
//		MyVideoObject obj = getVideoObjectByExecuteServiceCommand("getByTomergeOne");
//		if(obj == null || StringUtil.isBlank(obj.groupUid)){
//			return null;
//		}
//
//		String action ="listByGroupUid";
//		try {
//			Map<String, String> param = Maps.newHashMap();
//			param.put("action", action);
//			param.put("groupUid", obj.groupUid);
//			
//			String rslt = NieUtil.httpGet(NieConfig.getConfig("myvideotr.service.url"), param);
//			if (StringUtil.isBlank(rslt)) {
//				return null;
//			}
//			NieUtil.log(logFile, "[INFO][Service:" + action + "][RESULT]" + rslt);
//			
//			List<MyVideoObject> objList = Lists.newArrayList();
//			Json j = new Json();
//			List<Map<String, Object>> objMapList = j.toType(rslt, List.class);
//			for (Map<String, Object> objMap : objMapList) {
//				obj.uid = (String) objMap.get("uid");
//				obj.url = (String) objMap.get("url");
//				obj.videoUrl = (String) objMap.get("videoUrl");
//				obj.groupUid =  (String) objMap.get("groupUid");
//				objList.add(obj);
//			}
//			return objList;
//		} catch (Exception e) {
//			e.printStackTrace();
//			NieUtil.log(logFile, "[ERROR][Service:" + action + "]" + e.getMessage());
//			NieUtil.log(logFile, e);
//		}
//		return null;
//	}

	private void uploadVideo(WebDriver driver, MyVideoObject uploadloadObj) {
		logonYoutube(driver);
		try {
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

//			wes = driver.findElements(By.tagName("span"));
//			for (WebElement we1 : wes) {
//				if ("非公開".equals(we1.getText())) {
//					we1.click();
//					break;
//				}
//			}
			wes = driver.findElements(By.cssSelector("input[type=\"file\"]"));
			File savedFile = getVideoSaveFile(uploadloadObj);
			wes.get(0).sendKeys(savedFile.getAbsolutePath());
			
			// TODO set title...

			updateVideoStatus(uploadloadObj.uid, "uled");
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, e);
			updateVideoStatus(uploadloadObj.uid, "ulfailure");
		}
	}

	private MyVideoObject getToUploadVideo() {
		MyVideoObject toULObj = getVideoObjectByExecuteServiceCommand("getByTouploadOne");
		return toULObj;
	}

	private void downloadVideo(WebDriver driver, MyVideoObject downloadObj) {
		String videoDownloadUrl = getVideoDownloadUrl(driver,downloadObj);
		if(StringUtil.isBlank(videoDownloadUrl)){
			NieUtil.log(logFile,"[ERROR][Video][Download]url=" + downloadObj.videoUrl);
			updateVideoStatus(downloadObj.uid , "dlfailure");
			return;
		}
		File saveFile = getVideoSaveFile(downloadObj);
		try {
			downLoadVideoFromUrl(videoDownloadUrl,saveFile);
			downloadObj.dlVideoPath = saveFile.getAbsolutePath();
			updateVideoStatus(downloadObj.uid , "dled");
		} catch (IOException e) {
			e.printStackTrace();
			updateVideoStatus(downloadObj.uid , "dlfailure");
		}
	}

	private File getVideoSaveFile(MyVideoObject downloadObj) {
		String outFolder = NieConfig.getConfig("myvideotr.root.folder") ;
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

	private String getVideoDownloadUrl(WebDriver driver, MyVideoObject downloadObj) {
		driver.get("https://www.parsevideo.com/");
		WebElement we = driver.findElement(By
				.cssSelector("input[id=\"url_input\"]"));
		we.clear();
		we.sendKeys(downloadObj.videoUrl);
		we = driver.findElement(By
				.cssSelector("button[id=\"url_submit_button\"]"));
		we.click();

		we = driver.findElement(By.cssSelector("div[id=\"video\"]"));
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
//		}else if(urlTrue.indexOf("weibo.com") != -1){
//			// weibo 
//			if(isWeiboTag(driver)){
//				// weibo #
//				parseForWeiboTag(driver, videoObj);
//			}else{
//				parseForWeibo(driver, videoObj);
//			}
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
		// TODO 
		return false;
	}

	protected void parseForWeiboTag(WebDriver driver, MyVideoObject videoObj) {
		// TODO 
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

		String rootUrl = "https://www.weibo.com/";
		driver.get(rootUrl);
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
		
		driver.get(rootUrl);
		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"pl_unlogin_home_login\"]"));
		WebElement el2 = el1.findElement(By.cssSelector("a[node-type=\"normal_tab\"]"));
		el2.click();
		
		el2 = el1.findElement(By.cssSelector("input[id=\"loginname\"]"));
		el2.sendKeys(NieConfig.getConfig("myvideotr.weibo.user.name"));
		
		el2 = el1.findElement(By.cssSelector("input[name=\"password\"]"));
		el2.sendKeys(NieConfig.getConfig("myvideotr.weibo.user.password"));
		
		
		List<WebElement> wes = el1.findElements(By.tagName("span"));
		for(WebElement we :wes){
			if("登录".equals(we.getText())){
				we.click();
				break;
			}
		}

		NieUtil.mySleepBySecond(2);
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