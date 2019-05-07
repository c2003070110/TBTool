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
import org.openqa.selenium.json.Json;
import org.seleniumhq.jetty9.util.StringUtil;

import com.beust.jcommander.internal.Lists;
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
		// TODO
		driver.get("XXX");
		List<WebElement> wes = driver.findElements(By.cssSelector("XXXX[id=\"XXX\"]"));
		for(WebElement we :wes){
			String accessType = we.findElement(By.cssSelector("XXXX[id=\"XXX\"]")).getText();
			if(!"".equals(accessType))continue;
			// TODO
			String url = "";

			try {
				Map<String, String> param = Maps.newHashMap();
				param.put("action", "listVideoStatusByUrl");
				param.put("url", url);
				NieUtil.log(logFile, "[INFO][Service:listVideoStatusByUrl][Param]" + "[url]" + url);

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
			// TODO
			String title = "";
			String videoUrl = "";
			
			MyVideoObject videoObj = new MyVideoObject();
			videoObj.title = title;
			videoObj.videoUrl = videoUrl;
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
				updateStatus(noticeObj.uid, "parsefailure");
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
		List<MyVideoObject> uploadloadObjList = getToMergeVideo();
		if (uploadloadObjList != null && !uploadloadObjList.isEmpty()) {
			mergeAndUploadVideo(driver, uploadloadObjList);
		}
	}

	private void mergeAndUploadVideo(WebDriver driver, List<MyVideoObject> uploadloadObjList) {
		MyVideoObject uploadloadObj = mergeVideo(uploadloadObjList);
		uploadVideo(driver, uploadloadObj);
	}

	private MyVideoObject mergeVideo(List<MyVideoObject> uploadloadObjList) {
		// TODO 
		try {

		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, e);
			for (MyVideoObject obj : uploadloadObjList) {
				updateStatus(obj.uid, "mgfailure");
			}
		}
		return null;
	}

	private List<MyVideoObject> getToMergeVideo() {

		MyVideoObject obj = getVideoObjectByExecuteServiceCommand("getByTomergeOne");
		if(obj == null || StringUtil.isBlank(obj.groupUid)){
			return null;
		}

		String action ="listByGroupUid";
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", action);
			param.put("groupUid", obj.groupUid);
			
			String rslt = NieUtil.httpGet(NieConfig.getConfig("myvideotr.service.url"), param);
			if (StringUtil.isBlank(rslt)) {
				return null;
			}
			NieUtil.log(logFile, "[INFO][Service:" + action + "][RESULT]" + rslt);
			
			List<MyVideoObject> objList = Lists.newArrayList();
			Json j = new Json();
			List<Map<String, Object>> objMapList = j.toType(rslt, List.class);
			for (Map<String, Object> objMap : objMapList) {
				obj.uid = (String) objMap.get("uid");
				obj.url = (String) objMap.get("url");
				obj.videoUrl = (String) objMap.get("videoUrl");
				obj.groupUid =  (String) objMap.get("groupUid");
				objList.add(obj);
			}
			return objList;
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:" + action + "]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
		return null;
		
	}

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
			wes.get(0).sendKeys(uploadloadObj.dlVideoPath);
			
			// TODO

			updateStatus(uploadloadObj.uid, "uled");
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, e);
			updateStatus(uploadloadObj.uid, "ulfailure");
		}
	}

	private MyVideoObject getToUploadVideo() {
		MyVideoObject toULObj = getVideoObjectByExecuteServiceCommand("getByTouploadOne");
		return toULObj;
	}

	private void downloadVideo(WebDriver driver, MyVideoObject downloadObj) {
		String videoDownloadUrl = getVideoDownloadUrl(driver,downloadObj);
		String outFolder = NieConfig.getConfig("myvideotr.root.folder") ;
		File saveFile = new File(outFolder, downloadObj.uid + ".mp4");
		try {
			downLoadVideoFromUrl(videoDownloadUrl,saveFile);
			updateStatus(downloadObj.uid , "dled");
		} catch (IOException e) {
			e.printStackTrace();
			updateStatus(downloadObj.uid , "dlfailure");
		}
	}

	private void downLoadVideoFromUrl(String urlStr, File saveFile)
			throws IOException {
		System.out.println("[Downloading]" + urlStr);
		System.out.println("[Save File  ]" + saveFile.getCanonicalPath());
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

	private void updateStatus(String uid, String status) {
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
		}else if(urlTrue.indexOf("weibo.cn") != -1){
			// weibo 
			if(isWeiboTag(driver)){
				parseForWeiboTag(driver, videoObj);
			}else{
				parseForWeibo(driver, videoObj);
			}
			// weibo #
		}else if(urlTrue.indexOf("bilibili.com") != -1){
			parseForBilibili(driver, videoObj);
		}
		searchYT(driver, videoObj);
		updateVideoUper(videoObj);
	}

	private boolean isWeiboTag(WebDriver driver) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
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
	}

	private void parseForWeibo(WebDriver driver, MyVideoObject videoObj) {
		// TODO 
		driver.get("https://www.weibo.com/1449729883/profile?rightmod=1&wvr=6&mod=personnumber&is_all=1");
		/*
<div id="Pl_Official_MyProfileFeed__20" anchor="-50">                <div class="WB_feed WB_feed_v3 WB_feed_v4" pagenum="" node-type="feed_list" module-type="feed">
        <div style="position:relative;" node-type="feedconfig" data-queryfix="is_all=1">
            <div style="position:absolute;top:-110px;left:0;width:0;height:0;" id="feedtop" name="feedtop"></div>
        </div>
                    	        		    		    		    		    	        	<div minfo="ru=2035222637&amp;rm=4367403292782532" tbinfo="ouid=1449729883&amp;rouid=2035222637" action-type="feed_list_item" diss-data="" mid="4367592720196527" isforward="1" omid="4367403292782532" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
            <div class="WB_cardtitle_b S_line2">
            <div class="obj_name S_txt2"><h2 class="main_title"><span class="WB_type" title="自己可见微博"><i class="W_icon icon_type_self"></i>自己可见</span></h2></div>        </div>
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4367592720196527&amp;domain=&amp;is_ori=0&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4367592720196527" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                                            </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4367592720196527" target="_blank" href="/1449729883/Hsmow0P7N?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2019-05-02 20:05" date="1556798712000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4367592720196527:fromprofile"> 5月2日 20:05</a> 来自 <a class="S_txt2" target="_blank" href="http://vip.weibo.com/prividesc?priv=1006&amp;from=feed">iPhone客户端</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content">
                                                                                                                        转发微博                                            </div>
                                                                        <div class="WB_feed_expand">
                    <div class="W_arrow_bor W_arrow_bor_t"><i class="S_bg1_br"></i></div>
                    <div class="WB_expand S_bg1" node-type="feed_list_forwardContent">
                                                                            <div class="WB_info">
                                <a suda-uatrack="key=feed_headnick&amp;value=transuser_nick:4367403292782532" node-type="feed_list_originNick" class="W_fb S_txt1" nick-name="洋葱军事新闻" bpfilter="page_frame" href="/OnionMilitaryNews?refer_flag=1005055010_" title="洋葱军事新闻" usercard="id=2035222637&amp;refer_flag=1005055010_">@洋葱军事新闻</a><a target="_blank" href="http://verified.weibo.com/verify"><i title="微博个人认证 " class="W_icon icon_approve_gold"></i></a><a title="微博会员" target="_blank" href="http://vip.weibo.com/personal?from=main" action-type="ignore_list" suda-uatrack="key=profile_head&amp;value=member_guest"><em class="W_icon icon_member3"></em></a><a target="_blank" href="https://huodong.weibo.com/pai2019?ref=icon" title="随手拍"><img src="https://h5.sinaimg.cn/upload/1017/172/2019/04/15/feed_icon32x28_pai_default.png" alt="" class="W_icon_yystyle"></a><a target="_blank" href="https://weibo.com/ttwenda" title="微博问答答主"><img src="https://h5.sinaimg.cn/upload/1005/526/2019/04/04/icon_wenda_v3_v2.png" alt="" class="W_icon_yystyle"></a>                            </div>
                            <div class="WB_text" node-type="feed_list_reason">
                                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E5%88%9D%E4%B8%AD%E7%94%9F%E9%81%AD5%E5%90%8D%E5%90%8C%E5%AD%A6%E6%AE%B4%E6%89%93%E8%87%B4%E6%AD%BB%23">#初中生遭5名同学殴打致死#</a>小时候也经历过校园凌霸，在当年的东北还是很普遍的，普遍到感觉不到异常，直到听过那首《大学自习室》才想起。<br>校园凌霸对处于人格塑造阶段的学生来说，造成的阴影很严重。会扭曲孩子的价值观，人生观。如果校园凌霸没有及时控制，影响的不仅仅是受害者。<br> ​​​​...<a target="_blank" class="WB_text_opt" suda-uatrack="key=original_blog_unfold&amp;value=click_unfold:4367403292782532:2035222637" href="/2035222637/HshsZbFRy" action-type="fl_unfold" action-data="mid=4367403292782532">展开全文<i class="W_ficon ficon_arrow_down">c</i></a>                                                            </div>
                                                            <div class="WB_expand_media_box" style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 该模板和media.phtml如此之相像……有时间的话可以合并之，需注意ifForward="1"，其他几乎相同 -->
<!-- 模板V3 -->
<!-- 特型广告feed -->

                        <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
            <div class="media_box">
                                    <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                                                                                    
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg2 WB_video_mini WB_video_a WB_video_h5_v2" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4367403292782532:2017607-video:4367403292782532:2017607%3A4367121220593843:profile:2035222637:4367592720196527:1449729883:230442" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Fgslb.miaopai.com%252Fstream%252FNdXZzl300NayhDrxJrLUoiogLcRpKYvxWVCxjg__.mp4%253Fyx%253D%2526refer%253Dweibo_app%2526vend%253Dweibo%2526label%253Dmp4_hd%2526mpflag%253D8%2526Expires%253D1557243221%2526ssig%253DrKtaGTBimT%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Fgslb.miaopai.com%2Fstream%2FNdXZzl300NayhDrxJrLUoiogLcRpKYvxWVCxjg__.mp4%3Fyx%3D%26refer%3Dweibo_app%26vend%3Dweibo%26label%3Dmp4_hd%26mpflag%3D8%26Expires%3D1557243221%26ssig%3DrKtaGTBimT%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=2017607:4367121220593843&amp;keys=4367121223022881&amp;video_src=%2F%2Fgslb.miaopai.com%2Fstream%2FNdXZzl300NayhDrxJrLUoiogLcRpKYvxWVCxjg__.mp4%3Fyx%3D%26refer%3Dweibo_app%26vend%3Dweibo%26label%3Dmp4_hd%26mpflag%3D8%26Expires%3D1557243221%26ssig%3DrKtaGTBimT%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fimg3.miaopai.com%2Fimages%2FNdXZzl300NayhDrxJrLUoiogLcRpKYvxWVCxjg___CV_C_4.jpg&amp;card_height=720&amp;card_width=1280&amp;play_count=2125万&amp;duration=66&amp;short_url=http%3A%2F%2Ft.cn%2FESp7GNY%3Fm%3D4367403292782532%26u%3D2035222637&amp;encode_mode=&amp;bitrate=430&amp;biz_id=230442&amp;current_mid=4367403292782532&amp;video_orientation=horizontal" action-type="feed_list_third_rend" mediasize="1280:720" unique-video="H5_kkw6m_15572396550111530">
                <div node-type="fl_h5_video_pre" style="display: none;">
                    <img src="//img2.miaopai.com/images/NdXZzl300NayhDrxJrLUoiogLcRpKYvxWVCxjg___CV_C_4.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style=""><div disabletitle="true" tabindex="-1" class="WB_h5video_v2 wbv-controls-enabled wbv-deployable wbv-workinghover wbv-has-started wbv-paused wbv-ended wbv-user-active" id="H5_kkw6m_15572396550111530" role="region" aria-label="Video Player" style="width: 100%; height: 100%;"><video id="H5_kkw6m_15572396550111530_html5_api" class="wbv-tech" tabindex="-1" muted="muted" src="//gslb.miaopai.com/stream/NdXZzl300NayhDrxJrLUoiogLcRpKYvxWVCxjg__.mp4?yx=&amp;refer=weibo_app&amp;vend=weibo&amp;label=mp4_hd&amp;mpflag=8&amp;Expires=1557243221&amp;ssig=rKtaGTBimT&amp;KID=unistore,video"></video><div class="wbv-pop-layer wbv-hidden" aria-disabled="false"><h4><span>展开</span></h4></div><div class="wbv-poster" tabindex="-1" disabletitle="true" aria-disabled="false"><img src="//img3.miaopai.com/images/NdXZzl300NayhDrxJrLUoiogLcRpKYvxWVCxjg___CV_C_4.jpg"><div class="wbv-poster-info clearfix"><div class="W_fl">2125万次播放</div><div class="W_fr">1:06</div></div></div><div class="wbv-loading-spinner" disabletitle="true"><i class="W_loading_big"></i></div><button class="wbv-big-play-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">Play</span></button><div class="wbv-ending-layer" disabletitle="true"></div><div class="wbv-error-display wbv-modal-dialog wbv-hidden " disabletitle="true"><div class="box"><h3><i class="W_loading_big"></i></h3><h4><span>视频无法播放，请您稍后再试</span></h4></div></div><div class="wbv-control-bar" dir="ltr" disabletitle="true" role="group"><button class="wbv-play-control wbv-control wbv-button wbv-paused wbv-ended" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">重播</span></button><div class="wbv-progress-control wbv-control" disabletitle="true"><div class="wbv-progress-holder wbv-slider wbv-slider-horizontal" disabletitle="true" role="slider" aria-valuenow="100.00" aria-valuemin="0" aria-valuemax="100" aria-label="Progress Bar" aria-valuetext="1:06 of 1:06"><div class="wbv-load-progress" disabletitle="true" style="width: 98.2059%;"><span class="wbv-control-text"><span>Loaded</span>: 0%</span><div style="left: 0%; width: 100%;"></div></div><div class="wbv-play-progress wbv-slider-bar wbv-slider-handle" disabletitle="true" style="width: 100%;"><span class="wbv-control-text"><span>Progress</span>: 0%</span></div></div></div><div class="wbv-current-time wbv-time-control wbv-control" disabletitle="true"><div class="wbv-current-time-display"><span class="wbv-control-text">Current Time</span> 1:06</div></div><div class="wbv-time-control wbv-time-divider" disabletitle="true"><div><span>/</span></div></div><div class="wbv-duration wbv-time-control wbv-control" disabletitle="true"><div class="wbv-duration-display" aria-live="off"><span class="wbv-control-text">Duration Time</span> 1:06</div></div><button class="wbv-pop-control wbv-control wbv-button" disabletitle="false" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">弹层播放</span></button><button class="wbv-fullscreen-control wbv-control wbv-button wbv-hidden" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">进入全屏</span></button><div class="wbv-logo" disabletitle="true"></div><div class="wbv-volume-panel wbv-control wbv-volume-panel-vertical" disabletitle="true"><button class="wbv-mute-control wbv-control wbv-button wbv-vol-0" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">音量</span></button><div class="wbv-volume-control wbv-control wbv-volume-vertical wbv-hidden" disabletitle="true"><div class="wbv-volume-bar wbv-slider-bar wbv-slider wbv-slider-vertical" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Volume Level" aria-live="polite" aria-valuetext="0%"><div class="wbv-volume-level wbv-volume-handle" disabletitle="true"><span class="wbv-control-text"></span></div></div></div></div></div><div class="wbv-right-pop wbv-hidden" disabletitle="true"><ul class="wbv-menu-content" role="list"><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">播放</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">音量</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">视频地址</a></li></ul></div><div class="wbv-right-layer wbv-hidden" disabletitle="true"><div class="ipt"><input name="CopyUrlInput" type="text" readonly="readonly"><a class="V_ficon V_ficon_close" aria-disabled="false"></a></div></div></div></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                                                                                                </div>
        </div>
         
                                                                                                                    <div class="WB_func clearfix">
    <div class="WB_handle W_fr" mid="4367403292782532">
        <ul class="clearfix">
                                        <li><span class="line S_line1"><a bpfilter="page_frame" href="//weibo.com/2035222637/HshsZbFRy?type=repost" class="S_txt2" action-history="rec=1" suda-uatrack="key=feed_trans_weibo&amp;value=transfer:4367403292782532"><span><em class="W_ficon ficon_forward S_ficon"></em><em>549</em></span></a></span></li>
                        <li><span class="line S_line1"><a bpfilter="page_frame" href="//weibo.com/2035222637/HshsZbFRy" class="S_txt2" suda-uatrack="key=feed_trans_weibo&amp;value=comment:4367403292782532"><span><em class="W_ficon ficon_repeat S_ficon"></em><em> 1850</em></span></a></span></li>
            <li><span class="line S_line1"><a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4367403292782532" title="赞">
                                                                                                                     
                                                    <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>3914</em></span>                </a></span></li>
        </ul>
    </div>
    <div class="WB_from S_txt2">
                <a class="S_txt2" target="_blank" href="/2035222637/HshsZbFRy" title="2019-05-02 07:32" date="1556753549000" node-type="feed_list_item_date" suda-uatrack="key=feed_trans_weibo&amp;value=time:4367403292782532:fromprofile">5月2日 07:32</a>
            	    </div>
</div>
                                            </div>
                </div>
                                    <div class="WB_tag WB_tag_v2 clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4367592720196527"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r3 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读0次">阅读 0</i>&nbsp;
                                                                                <i class="S_txt2" title="此条微博无法使用推广功能">推广</i>
                                    </span></span>
                            </a>
                        			        </li>
                                                                <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4367592720196527"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4367592720196527&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div minfo="ru=1644114654&amp;rm=4346499800699547" tbinfo="ouid=1449729883&amp;rouid=1644114654" action-type="feed_list_item" diss-data="" mid="4346777237186112" isforward="1" omid="4346499800699547" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4346777237186112&amp;domain=&amp;is_ori=0&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4346777237186112" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4346777237186112" target="_blank" href="/1449729883/HjCT9u9r2?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2019-03-06 09:31" date="1551835914000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4346777237186112:fromprofile"> 3月6日 09:31</a> 来自 <a class="S_txt2" target="_blank" href="http://vip.weibo.com/prividesc?priv=1006&amp;from=feed">iPhone客户端</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content">
                                                                                                                        因为贪污，直接涉案金额超过1.3亿人民币(90年代的1.3亿 当时贪官是100万级的）被判无期徒刑，才坐三年牢。因为糖尿病，就保外了！结果活了91岁！ 褚是中国式的奇迹。活着侮辱法制，死了侮辱社会。                                            </div>
                                                                        <div class="WB_feed_expand">
                    <div class="W_arrow_bor W_arrow_bor_t"><i class="S_bg1_br"></i></div>
                    <div class="WB_expand S_bg1" node-type="feed_list_forwardContent">
                                                                            <div class="WB_info">
                                <a suda-uatrack="key=feed_headnick&amp;value=transuser_nick:4346499800699547" node-type="feed_list_originNick" class="W_fb S_txt1" nick-name="新京报" bpfilter="page_frame" href="/xjb?refer_flag=1005055010_" title="新京报" usercard="id=1644114654&amp;refer_flag=1005055010_">@新京报</a><a target="_blank" href="http://fuwu.biz.weibo.com"><i title="微博官方认证" class="W_icon icon_approve_co"></i></a><a title="微博会员" target="_blank" href="http://vip.weibo.com/personal?from=main" action-type="ignore_list" suda-uatrack="key=profile_head&amp;value=member_guest"><em class="W_icon icon_member6"></em></a>                            </div>
                            <div class="WB_text" node-type="feed_list_reason">
                                                                                                                                        【原云南红塔集团董事长、褚橙创始人<a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo/%23%E8%A4%9A%E6%97%B6%E5%81%A5%E5%8E%BB%E4%B8%96%23">#褚时健去世#</a>】3月5日，新京报记者从褚时健儿子褚一斌公司人员处获悉，原云南红塔集团有限公司和玉溪红塔烟草（集团）有限责任公司董事长、褚橙创始人褚时健去世，享年91岁。<a title="新京报App" href="http://t.cn/EINQX1e" alt="http://t.cn/EINQX1e" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_link">O</i>新京报App</a> ​ ​​​​                                                            </div>
                                                            <div class="WB_expand_media_box" style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 该模板和media.phtml如此之相像……有时间的话可以合并之，需注意ifForward="1"，其他几乎相同 -->
<!-- 模板V3 -->
<!-- 特型广告feed -->

                        <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
            <div class="media_box">
                                    <!--判断图片的个数，渲染图片-->
                                            <!--图片个数等于1，只显示图片-->
                        
				 		    <!--picture_count == 1-->
    <ul class="WB_media_a  WB_media_a_m1 clearfix" action-data="isPrivate=0&amp;relation=0&amp;clear_picSrc=%2F%2Fwx4.sinaimg.cn%2Fmw690%2F61ff32dely1g0rxridayvj20sg0izjvk.jpg">
                        <li class="WB_pic li_1 S_bg1 S_line2 bigcursor " action-data="isPrivate=0&amp;relation=0&amp;pid=61ff32dely1g0rxridayvj20sg0izjvk&amp;object_ids=1042018%3A24406ec343c4a1d9fc1224a999ef293b&amp;photo_tag_pids=&amp;uid=1644114654&amp;mid=4346499800699547&amp;pic_ids=61ff32dely1g0rxridayvj20sg0izjvk&amp;pic_objects=" action-type="feed_list_media_img" suda-uatrack="key=tblog_newimage_feed&amp;value=image_feed_unfold:4346499800699547:61ff32dely1g0rxridayvj20sg0izjvk:1644114654:0">
                           <img src="//wx4.sinaimg.cn/orj360/61ff32dely1g0rxridayvj20sg0izjvk.jpg">
          <i class="W_loading" style="display:none;"></i>
                     <i class="W_loading" style="display:none;"></i>
                    </li>
    </ul>
                                                </div>
        </div>
         
                                                                                                                    <div class="WB_func clearfix">
    <div class="WB_handle W_fr" mid="4346499800699547">
        <ul class="clearfix">
                                        <li><span class="line S_line1"><a bpfilter="page_frame" href="//weibo.com/1644114654/HjvFG2VZ1?type=repost" class="S_txt2" action-history="rec=1" suda-uatrack="key=feed_trans_weibo&amp;value=transfer:4346499800699547"><span><em class="W_ficon ficon_forward S_ficon"></em><em>33244</em></span></a></span></li>
                        <li><span class="line S_line1"><a bpfilter="page_frame" href="//weibo.com/1644114654/HjvFG2VZ1" class="S_txt2" suda-uatrack="key=feed_trans_weibo&amp;value=comment:4346499800699547"><span><em class="W_ficon ficon_repeat S_ficon"></em><em> 20951</em></span></a></span></li>
            <li><span class="line S_line1"><a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4346499800699547&amp;hide_multi_attitude=1&amp;liked=0" title="赞">
                                                                                                                     
                                                    <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>66950</em></span>                </a></span></li>
        </ul>
    </div>
    <div class="WB_from S_txt2">
                <a class="S_txt2" target="_blank" href="/1644114654/HjvFG2VZ1" title="2019-03-05 15:09" date="1551769769000" node-type="feed_list_item_date" suda-uatrack="key=feed_trans_weibo&amp;value=time:4346499800699547:fromprofile">3月5日 15:09</a>
         来自 <a class="S_txt2" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/6vtZb0" rel="nofollow">微博 weibo.com</a>    	                            <span title="可于本微博右侧下拉菜单查看编辑记录">已编辑</span>
                        </div>
</div>
                                            </div>
                </div>
                                    <div class="WB_tag WB_tag_v2 clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4346777237186112"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4346777237186112&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读129次">阅读 129</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;rootmid=4346499800699547&amp;rootname=新京报&amp;rootuid=1644114654&amp;rooturl=https://weibo.com/1644114654/HjvFG2VZ1&amp;url=https://weibo.com/1449729883/HjCT9u9r2&amp;mid=4346777237186112&amp;name=次郎花子&amp;uid=1449729883&amp;domain=xjb&amp;pid=61ff32dely1g0rxridayvj20sg0izjvk" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4346777237186112"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4346777237186112&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4320876985810535" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4320876985810535&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4320876985810535" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4320876985810535" target="_blank" href="/1449729883/H8L6yonAj?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-24 22:13" date="1545660814000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4320876985810535:fromprofile"> 2018-12-24 22:13</a> 来自 <a class="S_txt2" target="_blank" href="http://vip.weibo.com/prividesc?priv=1006&amp;from=feed">iPhone客户端</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        分享视频 <a suda-uatrack="key=tblog_card&amp;value=click_title:4320876985810535:1022-place:1022%3A1001018008100000000000044" title="日本·东京" href="http://t.cn/RD4KZQd" alt="http://t.cn/RD4KZQd" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_place">2</i>日本·东京</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4320876985810535:1034-video:1034%3A4320876972203627:page_100505_home:1449729883:4320876985810535:1449729883" title="次郎花子的微博视频" href="http://t.cn/E4mcI7q" alt="http://t.cn/E4mcI7q" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的微博视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a WB_video_h5_v2" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4320876985810535:1034-video:4320876985810535:1034%3A4320876972203627:profile:1449729883:4320876985810535:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F0008Yveqlx07qe5uXrIk0104020047Dc0E010.mp4%253Flabel%253Dmp4_hd%2526template%253D540x960.24.0%2526Expires%253D1557243244%2526ssig%253DYuFYN98rIN%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F0008Yveqlx07qe5uXrIk0104020047Dc0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DYuFYN98rIN%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4320876972203627&amp;keys=4320876978807671&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F0008Yveqlx07qe5uXrIk0104020047Dc0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DYuFYN98rIN%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx4.sinaimg.cn%2Forj480%2F56691f5bly1fyi6z8dl51j20f00qodh9.jpg&amp;card_height=960&amp;card_width=540&amp;play_count=38&amp;duration=7&amp;short_url=http%3A%2F%2Ft.cn%2FE4mcI7q%3Fm%3D4320876985810535%26u%3D1449729883&amp;encode_mode=&amp;bitrate=1033&amp;biz_id=230444&amp;current_mid=4320876985810535&amp;video_orientation=vertical" action-type="feed_list_third_rend" mediasize="540:960" unique-video="H5_cumbp_155723965501296081">
                <div node-type="fl_h5_video_pre" style="display: none;">
                    <img src="//wx4.sinaimg.cn/orj480/56691f5bly1fyi6z8dl51j20f00qodh9.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style=""><div disabletitle="true" tabindex="-1" class="WB_h5video_v2 wbv-controls-enabled wbv-deployable wbv-workinghover wbv-has-started wbv-paused wbv-ended wbv-user-active" id="H5_cumbp_155723965501296081" role="region" aria-label="Video Player" style="width: 100%; height: 100%;"><video id="H5_cumbp_155723965501296081_html5_api" class="wbv-tech" tabindex="-1" muted="muted" src="//f.us.sinaimg.cn/0008Yveqlx07qe5uXrIk0104020047Dc0E010.mp4?label=mp4_hd&amp;template=540x960.24.0&amp;Expires=1557243244&amp;ssig=YuFYN98rIN&amp;KID=unistore,video"></video><div class="wbv-pop-layer wbv-hidden" aria-disabled="false"><h4><span>展开</span></h4></div><div class="wbv-poster" tabindex="-1" disabletitle="true" aria-disabled="false"><img src="//wx4.sinaimg.cn/orj480/56691f5bly1fyi6z8dl51j20f00qodh9.jpg"><div class="wbv-poster-info clearfix"><div class="W_fl">38次播放</div><div class="W_fr">0:07</div></div></div><div class="wbv-loading-spinner" disabletitle="true"><i class="W_loading_big"></i></div><button class="wbv-big-play-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">Play</span></button><div class="wbv-ending-layer" disabletitle="true"></div><div class="wbv-error-display wbv-modal-dialog wbv-hidden " disabletitle="true"><div class="box"><h3><i class="W_loading_big"></i></h3><h4><span>视频无法播放，请您稍后再试</span></h4></div></div><div class="wbv-control-bar" dir="ltr" disabletitle="true" role="group"><button class="wbv-play-control wbv-control wbv-button wbv-paused wbv-ended" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">重播</span></button><div class="wbv-progress-control wbv-control" disabletitle="true"><div class="wbv-progress-holder wbv-slider wbv-slider-horizontal" disabletitle="true" role="slider" aria-valuenow="100.00" aria-valuemin="0" aria-valuemax="100" aria-label="Progress Bar" aria-valuetext="0:07 of 0:07"><div class="wbv-load-progress" disabletitle="true" style="width: 100%;"><span class="wbv-control-text"><span>Loaded</span>: 0%</span><div style="left: 0%; width: 100%;"></div></div><div class="wbv-play-progress wbv-slider-bar wbv-slider-handle" disabletitle="true" style="width: 100%;"><span class="wbv-control-text"><span>Progress</span>: 0%</span></div></div></div><div class="wbv-current-time wbv-time-control wbv-control" disabletitle="true"><div class="wbv-current-time-display"><span class="wbv-control-text">Current Time</span> 0:07</div></div><div class="wbv-time-control wbv-time-divider" disabletitle="true"><div><span>/</span></div></div><div class="wbv-duration wbv-time-control wbv-control" disabletitle="true"><div class="wbv-duration-display" aria-live="off"><span class="wbv-control-text">Duration Time</span> 0:07</div></div><button class="wbv-pop-control wbv-control wbv-button" disabletitle="false" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">弹层播放</span></button><button class="wbv-fullscreen-control wbv-control wbv-button wbv-hidden" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">进入全屏</span></button><div class="wbv-logo" disabletitle="true"></div><div class="wbv-volume-panel wbv-control wbv-volume-panel-vertical" disabletitle="true"><button class="wbv-mute-control wbv-control wbv-button wbv-vol-0" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">音量</span></button><div class="wbv-volume-control wbv-control wbv-volume-vertical wbv-hidden" disabletitle="true"><div class="wbv-volume-bar wbv-slider-bar wbv-slider wbv-slider-vertical" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Volume Level" aria-live="polite" aria-valuetext="0%"><div class="wbv-volume-level wbv-volume-handle" disabletitle="true"><span class="wbv-control-text"></span></div></div></div></div></div><div class="wbv-right-pop wbv-hidden" disabletitle="true"><ul class="wbv-menu-content" role="list"><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">播放</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">音量</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">视频地址</a></li></ul></div><div class="wbv-right-layer wbv-hidden" disabletitle="true"><div class="ipt"><input name="CopyUrlInput" type="text" readonly="readonly"><a class="V_ficon V_ficon_close" aria-disabled="false"></a></div></div></div></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4320876985810535"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4320876985810535&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读256次">阅读 256</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H8L6yonAj&amp;mid=4320876985810535&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4320876985810535"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4320876985810535&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4320876943589574" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4320876943589574&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4320876943589574" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4320876943589574" target="_blank" href="/1449729883/H8L6uf3Om?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-24 22:13" date="1545660804000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4320876943589574:fromprofile"> 2018-12-24 22:13</a> 来自 <a class="S_txt2" target="_blank" href="http://vip.weibo.com/prividesc?priv=1006&amp;from=feed">iPhone客户端</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        分享视频 <a suda-uatrack="key=tblog_card&amp;value=click_title:4320876943589574:1022-place:1022%3A1001018008100000000000044" title="日本·东京" href="http://t.cn/RD4KZQd" alt="http://t.cn/RD4KZQd" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_place">2</i>日本·东京</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4320876943589574:1034-video:1034%3A4320876922127100:page_100505_home:1449729883:4320876943589574:1449729883" title="次郎花子的微博视频" href="http://t.cn/E4mcqke" alt="http://t.cn/E4mcqke" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的微博视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a WB_video_h5_v2" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4320876943589574:1034-video:4320876943589574:1034%3A4320876922127100:profile:1449729883:4320876943589574:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F001pTzmDlx07qe5uzUQE0104020054BO0E010.mp4%253Flabel%253Dmp4_hd%2526template%253D576x1024.24.0%2526Expires%253D1557243244%2526ssig%253D%25252FhkHaT8GGI%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F001pTzmDlx07qe5uzUQE0104020054BO0E010.mp4%3Flabel%3Dmp4_hd%26template%3D576x1024.24.0%26Expires%3D1557243244%26ssig%3D%252FhkHaT8GGI%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4320876922127100&amp;keys=4320876924285446&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F001pTzmDlx07qe5uzUQE0104020054BO0E010.mp4%3Flabel%3Dmp4_hd%26template%3D576x1024.24.0%26Expires%3D1557243244%26ssig%3D%252FhkHaT8GGI%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx2.sinaimg.cn%2Forj480%2F56691f5bly1fyi6z0yc2oj20g00sgmxt.jpg&amp;card_height=1024&amp;card_width=576&amp;play_count=29&amp;duration=13&amp;short_url=http%3A%2F%2Ft.cn%2FE4mcqke%3Fm%3D4320876943589574%26u%3D1449729883&amp;encode_mode=&amp;bitrate=693&amp;biz_id=230444&amp;current_mid=4320876943589574&amp;video_orientation=vertical" action-type="feed_list_third_rend" mediasize="576:1024" unique-video="H5_xhikg_155723965501399182">
                <div node-type="fl_h5_video_pre" style="display: none;">
                    <img src="//wx2.sinaimg.cn/orj480/56691f5bly1fyi6z0yc2oj20g00sgmxt.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style=""><div disabletitle="true" tabindex="-1" class="WB_h5video_v2 wbv-controls-enabled wbv-deployable wbv-workinghover wbv-has-started wbv-paused wbv-user-active" id="H5_xhikg_155723965501399182" role="region" aria-label="Video Player" style="width: 100%; height: 100%;"><video id="H5_xhikg_155723965501399182_html5_api" class="wbv-tech" tabindex="-1" muted="muted" src="//f.us.sinaimg.cn/001pTzmDlx07qe5uzUQE0104020054BO0E010.mp4?label=mp4_hd&amp;template=576x1024.24.0&amp;Expires=1557243244&amp;ssig=%2FhkHaT8GGI&amp;KID=unistore,video"></video><div class="wbv-pop-layer wbv-hidden" aria-disabled="false"><h4><span>展开</span></h4></div><div class="wbv-poster" tabindex="-1" disabletitle="true" aria-disabled="false"><img src="//wx2.sinaimg.cn/orj480/56691f5bly1fyi6z0yc2oj20g00sgmxt.jpg"><div class="wbv-poster-info clearfix"><div class="W_fl">29次播放</div><div class="W_fr">0:13</div></div></div><div class="wbv-loading-spinner" disabletitle="true"><i class="W_loading_big"></i></div><button class="wbv-big-play-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">Play</span></button><div class="wbv-ending-layer" disabletitle="true"></div><div class="wbv-error-display wbv-modal-dialog wbv-hidden " disabletitle="true"><div class="box"><h3><i class="W_loading_big"></i></h3><h4><span>视频无法播放，请您稍后再试</span></h4></div></div><div class="wbv-control-bar" dir="ltr" disabletitle="true" role="group"><button class="wbv-play-control wbv-control wbv-button wbv-paused" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">播放</span></button><div class="wbv-progress-control wbv-control" disabletitle="true"><div class="wbv-progress-holder wbv-slider wbv-slider-horizontal" disabletitle="true" role="slider" aria-valuenow="13.17" aria-valuemin="0" aria-valuemax="100" aria-label="Progress Bar" aria-valuetext="0:01 of 0:13"><div class="wbv-load-progress" disabletitle="true" style="width: 100%;"><span class="wbv-control-text"><span>Loaded</span>: 0%</span><div style="left: 0%; width: 100%;"></div></div><div class="wbv-play-progress wbv-slider-bar wbv-slider-handle" disabletitle="true" style="width: 13.17%;"><span class="wbv-control-text"><span>Progress</span>: 0%</span></div></div></div><div class="wbv-current-time wbv-time-control wbv-control" disabletitle="true"><div class="wbv-current-time-display"><span class="wbv-control-text">Current Time</span> 0:01</div></div><div class="wbv-time-control wbv-time-divider" disabletitle="true"><div><span>/</span></div></div><div class="wbv-duration wbv-time-control wbv-control" disabletitle="true"><div class="wbv-duration-display" aria-live="off"><span class="wbv-control-text">Duration Time</span> 0:13</div></div><button class="wbv-pop-control wbv-control wbv-button" disabletitle="false" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">弹层播放</span></button><button class="wbv-fullscreen-control wbv-control wbv-button wbv-hidden" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">进入全屏</span></button><div class="wbv-logo" disabletitle="true"></div><div class="wbv-volume-panel wbv-control wbv-volume-panel-vertical" disabletitle="true"><button class="wbv-mute-control wbv-control wbv-button wbv-vol-0" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">音量</span></button><div class="wbv-volume-control wbv-control wbv-volume-vertical wbv-hidden" disabletitle="true"><div class="wbv-volume-bar wbv-slider-bar wbv-slider wbv-slider-vertical" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Volume Level" aria-live="polite" aria-valuetext="0%"><div class="wbv-volume-level wbv-volume-handle" disabletitle="true"><span class="wbv-control-text"></span></div></div></div></div></div><div class="wbv-right-pop wbv-hidden" disabletitle="true"><ul class="wbv-menu-content" role="list"><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">播放</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">音量</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">视频地址</a></li></ul></div><div class="wbv-right-layer wbv-hidden" disabletitle="true"><div class="ipt"><input name="CopyUrlInput" type="text" readonly="readonly"><a class="V_ficon V_ficon_close" aria-disabled="false"></a></div></div></div></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4320876943589574"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4320876943589574&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读270次">阅读 270</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H8L6uf3Om&amp;mid=4320876943589574&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4320876943589574"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4320876943589574&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4315444561501975" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4315444561501975&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4315444561501975" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4315444561501975" target="_blank" href="/1449729883/H6tMA6iJp?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-09 22:27" date="1544365622000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4315444561501975:fromprofile"> 2018-12-9 22:27</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/4fw5aJ" rel="nofollow">秒拍网页版</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4315444561501975:1034-video:1034%3A4315444522211939:page_100505_home:1449729883:4315444561501975:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/Eygsveg" alt="http://t.cn/Eygsveg" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a WB_video_h5_v2" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4315444561501975:1034-video:4315444561501975:1034%3A4315444522211939:profile:1449729883:4315444561501975:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F003MKm56lx07pQbaUcg001040200aIpz0E010.mp4%253Flabel%253Dmp4_720p%2526template%253D1280x720.20.0%2526Expires%253D1557243244%2526ssig%253D4ayr68Rx5X%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F004qJ69xlx07pQb9GoTe0104020036h30E010.mp4%3Flabel%3Dmp4_hd%26template%3D852x480.28.0%26Expires%3D1557243244%26ssig%3DIxKghND41i%26KID%3Dunistore%2Cvideo&amp;720=http%3A%2F%2Ff.us.sinaimg.cn%2F003MKm56lx07pQbaUcg001040200aIpz0E010.mp4%3Flabel%3Dmp4_720p%26template%3D1280x720.20.0%26Expires%3D1557243244%26ssig%3D4ayr68Rx5X%26KID%3Dunistore%2Cvideo&amp;qType=720" action-data="type=feedvideo&amp;objectid=1034:4315444522211939&amp;keys=4315444524668075&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F003MKm56lx07pQbaUcg001040200aIpz0E010.mp4%3Flabel%3Dmp4_720p%26template%3D1280x720.20.0%26Expires%3D1557243244%26ssig%3D4ayr68Rx5X%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx1.sinaimg.cn%2Forj480%2F56691f5bly1fy0v2m30cvj20zk0k0q3o.jpg&amp;card_height=480&amp;card_width=852&amp;play_count=9&amp;duration=14&amp;short_url=http%3A%2F%2Ft.cn%2FEygsveg%3Fm%3D4315444561501975%26u%3D1449729883&amp;encode_mode=&amp;bitrate=398&amp;biz_id=230444&amp;current_mid=4315444561501975&amp;video_orientation=horizontal" action-type="feed_list_third_rend" mediasize="852:480" unique-video="H5_ymlvo_155723965501482853">
                <div node-type="fl_h5_video_pre" style="display: none;">
                    <img src="//wx1.sinaimg.cn/orj480/56691f5bly1fy0v2m30cvj20zk0k0q3o.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style=""><div disabletitle="true" tabindex="-1" class="WB_h5video_v2 wbv-paused wbv-controls-enabled wbv-deployable wbv-workinghover wbv-user-inactive" id="H5_ymlvo_155723965501482853" role="region" aria-label="Video Player" style="width: 100%; height: 100%;"><video id="H5_ymlvo_155723965501482853_html5_api" class="wbv-tech" tabindex="-1" muted="muted" src="//f.us.sinaimg.cn/003MKm56lx07pQbaUcg001040200aIpz0E010.mp4?label=mp4_720p&amp;template=1280x720.20.0&amp;Expires=1557243244&amp;ssig=4ayr68Rx5X&amp;KID=unistore,video"></video><div class="wbv-pop-layer wbv-hidden" aria-disabled="false"><h4><span>展开</span></h4></div><div class="wbv-poster" tabindex="-1" disabletitle="true" aria-disabled="false"><img src="//wx1.sinaimg.cn/orj480/56691f5bly1fy0v2m30cvj20zk0k0q3o.jpg"><div class="wbv-poster-info clearfix"><div class="W_fl">9次播放</div><div class="W_fr">0:14</div></div></div><div class="wbv-loading-spinner" disabletitle="true"><i class="W_loading_big"></i></div><button class="wbv-big-play-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">Play</span></button><div class="wbv-ending-layer" disabletitle="true"></div><div class="wbv-error-display wbv-modal-dialog wbv-hidden " disabletitle="true"><div class="box"><h3><i class="W_loading_big"></i></h3><h4><span>视频无法播放，请您稍后再试</span></h4></div></div><div class="wbv-control-bar" dir="ltr" disabletitle="true" role="group"><button class="wbv-play-control wbv-control wbv-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">播放</span></button><div class="wbv-progress-control wbv-control" disabletitle="true"><div class="wbv-progress-holder wbv-slider wbv-slider-horizontal" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Progress Bar"><div class="wbv-load-progress" disabletitle="true" style="width: 18.4478%;"><span class="wbv-control-text"><span>Loaded</span>: 0%</span><div style="left: 0%; width: 100%;"></div></div><div class="wbv-play-progress wbv-slider-bar wbv-slider-handle" disabletitle="true"><span class="wbv-control-text"><span>Progress</span>: 0%</span></div></div></div><div class="wbv-current-time wbv-time-control wbv-control" disabletitle="true"><div class="wbv-current-time-display"><span class="wbv-control-text">Current Time</span> 0:00</div></div><div class="wbv-time-control wbv-time-divider" disabletitle="true"><div><span>/</span></div></div><div class="wbv-duration wbv-time-control wbv-control" disabletitle="true"><div class="wbv-duration-display" aria-live="off"><span class="wbv-control-text">Duration Time</span> 0:14</div></div><div class="wbv-quality-panel wbv-menu-button wbv-menu-button-popup wbv-control wbv-button wbv-quality-720" disabletitle="true"><button class="wbv-quality-button wbv-button wbv-control" disabletitle="true" type="button" aria-live="polite" aria-disabled="false" aria-haspopup="true" aria-expanded="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text"></span></button><div class="wbv-menu" disabletitle="true"><ul class="wbv-menu-content" role="menu"><li class="wbv-menu-item wbv-selected" tabindex="-1" disabletitle="true" role="menuitem" aria-live="polite" suda-uatrack="key=wb_PC_H5_player&amp;value=quality_720p" data-type="720" aria-disabled="false" aria-checked="true"><span class="wbv-menu-item-text">高清</span><span class="wbv-control-text">, selected</span></li><li class="wbv-menu-item" tabindex="-1" disabletitle="true" role="menuitem" aria-live="polite" suda-uatrack="key=wb_PC_H5_player&amp;value=quality_480p" data-type="480" aria-disabled="false" aria-checked="false"><span class="wbv-menu-item-text">标清</span><span class="wbv-control-text"> </span></li></ul></div></div><button class="wbv-pop-control wbv-control wbv-button" disabletitle="false" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">弹层播放</span></button><button class="wbv-fullscreen-control wbv-control wbv-button wbv-hidden" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">进入全屏</span></button><div class="wbv-logo" disabletitle="true"></div><div class="wbv-volume-panel wbv-control wbv-volume-panel-vertical" disabletitle="true"><button class="wbv-mute-control wbv-control wbv-button wbv-vol-0" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">音量</span></button><div class="wbv-volume-control wbv-control wbv-volume-vertical wbv-hidden" disabletitle="true"><div class="wbv-volume-bar wbv-slider-bar wbv-slider wbv-slider-vertical" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Volume Level" aria-live="polite" aria-valuetext="0%"><div class="wbv-volume-level wbv-volume-handle" disabletitle="true"><span class="wbv-control-text"></span></div></div></div></div></div><div class="wbv-right-pop wbv-hidden" disabletitle="true"><ul class="wbv-menu-content" role="list"><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);"></a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">音量</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">视频地址</a></li></ul></div><div class="wbv-right-layer wbv-hidden" disabletitle="true"><div class="ipt"><input name="CopyUrlInput" type="text" readonly="readonly"><a class="V_ficon V_ficon_close" aria-disabled="false"></a></div></div></div></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4315444561501975"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4315444561501975&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读307次">阅读 307</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H6tMA6iJp&amp;mid=4315444561501975&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4315444561501975"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4315444561501975&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4315444246035000" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4315444246035000&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4315444246035000" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4315444246035000" target="_blank" href="/1449729883/H6tM4pjYI?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-09 22:25" date="1544365548000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4315444246035000:fromprofile"> 2018-12-9 22:25</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/19DcdW" rel="nofollow">微博视频</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4315444246035000:1034-video:1034%3A4315444216025129:page_100505_home:1449729883:4315444246035000:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/EygFak2" alt="http://t.cn/EygFak2" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a WB_video_h5_v2" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4315444246035000:1034-video:4315444246035000:1034%3A4315444216025129:profile:1449729883:4315444246035000:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F000xyW4Zlx07pQb4cvb2010402004WoR0E010.mp4%253Flabel%253Dmp4_hd%2526template%253D540x960.24.0%2526Expires%253D1557243244%2526ssig%253DoAlKhG0TS1%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F000xyW4Zlx07pQb4cvb2010402004WoR0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DoAlKhG0TS1%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4315444216025129&amp;keys=4315444222664019&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F000xyW4Zlx07pQb4cvb2010402004WoR0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DoAlKhG0TS1%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx2.sinaimg.cn%2Forj480%2F56691f5bly1fy0v1cahm1j20f00qogmj.jpg&amp;card_height=960&amp;card_width=540&amp;play_count=8&amp;duration=10&amp;short_url=http%3A%2F%2Ft.cn%2FEygFak2%3Fm%3D4315444246035000%26u%3D1449729883&amp;encode_mode=&amp;bitrate=873&amp;biz_id=230444&amp;current_mid=4315444246035000&amp;video_orientation=vertical" action-type="feed_list_third_rend" mediasize="540:960" unique-video="H5_xxl7l_15572396550147514">
                <div node-type="fl_h5_video_pre" style="display: none;">
                    <img src="//wx2.sinaimg.cn/orj480/56691f5bly1fy0v1cahm1j20f00qogmj.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style=""><div disabletitle="true" tabindex="-1" class="WB_h5video_v2 wbv-controls-enabled wbv-deployable wbv-workinghover wbv-has-started wbv-paused wbv-user-active" id="H5_xxl7l_15572396550147514" role="region" aria-label="Video Player" style="width: 100%; height: 100%;"><video id="H5_xxl7l_15572396550147514_html5_api" class="wbv-tech" tabindex="-1" muted="muted" src="//f.us.sinaimg.cn/000xyW4Zlx07pQb4cvb2010402004WoR0E010.mp4?label=mp4_hd&amp;template=540x960.24.0&amp;Expires=1557243244&amp;ssig=oAlKhG0TS1&amp;KID=unistore,video"></video><div class="wbv-pop-layer wbv-hidden" aria-disabled="false"><h4><span>展开</span></h4></div><div class="wbv-poster" tabindex="-1" disabletitle="true" aria-disabled="false"><img src="//wx2.sinaimg.cn/orj480/56691f5bly1fy0v1cahm1j20f00qogmj.jpg"><div class="wbv-poster-info clearfix"><div class="W_fl">8次播放</div><div class="W_fr">0:10</div></div></div><div class="wbv-loading-spinner" disabletitle="true"><i class="W_loading_big"></i></div><button class="wbv-big-play-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">Play</span></button><div class="wbv-ending-layer" disabletitle="true"></div><div class="wbv-error-display wbv-modal-dialog wbv-hidden " disabletitle="true"><div class="box"><h3><i class="W_loading_big"></i></h3><h4><span>视频无法播放，请您稍后再试</span></h4></div></div><div class="wbv-control-bar" dir="ltr" disabletitle="true" role="group"><button class="wbv-play-control wbv-control wbv-button wbv-paused" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">播放</span></button><div class="wbv-progress-control wbv-control" disabletitle="true"><div class="wbv-progress-holder wbv-slider wbv-slider-horizontal" disabletitle="true" role="slider" aria-valuenow="15.73" aria-valuemin="0" aria-valuemax="100" aria-label="Progress Bar" aria-valuetext="0:01 of 0:10"><div class="wbv-load-progress" disabletitle="true" style="width: 100%;"><span class="wbv-control-text"><span>Loaded</span>: 0%</span><div style="left: 0%; width: 100%;"></div></div><div class="wbv-play-progress wbv-slider-bar wbv-slider-handle" disabletitle="true" style="width: 15.73%;"><span class="wbv-control-text"><span>Progress</span>: 0%</span></div></div></div><div class="wbv-current-time wbv-time-control wbv-control" disabletitle="true"><div class="wbv-current-time-display"><span class="wbv-control-text">Current Time</span> 0:01</div></div><div class="wbv-time-control wbv-time-divider" disabletitle="true"><div><span>/</span></div></div><div class="wbv-duration wbv-time-control wbv-control" disabletitle="true"><div class="wbv-duration-display" aria-live="off"><span class="wbv-control-text">Duration Time</span> 0:10</div></div><button class="wbv-pop-control wbv-control wbv-button" disabletitle="false" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">弹层播放</span></button><button class="wbv-fullscreen-control wbv-control wbv-button wbv-hidden" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">进入全屏</span></button><div class="wbv-logo" disabletitle="true"></div><div class="wbv-volume-panel wbv-control wbv-volume-panel-vertical" disabletitle="true"><button class="wbv-mute-control wbv-control wbv-button wbv-vol-0" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">音量</span></button><div class="wbv-volume-control wbv-control wbv-volume-vertical wbv-hidden" disabletitle="true"><div class="wbv-volume-bar wbv-slider-bar wbv-slider wbv-slider-vertical" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Volume Level" aria-live="polite" aria-valuetext="0%"><div class="wbv-volume-level wbv-volume-handle" disabletitle="true"><span class="wbv-control-text"></span></div></div></div></div></div><div class="wbv-right-pop wbv-hidden" disabletitle="true"><ul class="wbv-menu-content" role="list"><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">播放</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">音量</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">视频地址</a></li></ul></div><div class="wbv-right-layer wbv-hidden" disabletitle="true"><div class="ipt"><input name="CopyUrlInput" type="text" readonly="readonly"><a class="V_ficon V_ficon_close" aria-disabled="false"></a></div></div></div></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4315444246035000"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4315444246035000&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读323次">阅读 323</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H6tM4pjYI&amp;mid=4315444246035000&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4315444246035000"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4315444246035000&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4315443948828685" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4315443948828685&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4315443948828685" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4315443948828685" target="_blank" href="/1449729883/H6tLAB2K9?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-09 22:24" date="1544365477000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4315443948828685:fromprofile"> 2018-12-9 22:24</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/19DcdW" rel="nofollow">微博视频</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4315443948828685:1034-video:1034%3A4315443926901078:page_100505_home:1449729883:4315443948828685:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/EygkDGZ" alt="http://t.cn/EygkDGZ" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a WB_video_h5_v2" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4315443948828685:1034-video:4315443948828685:1034%3A4315443926901078:profile:1449729883:4315443948828685:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F004DfCS8lx07pQb08g2I010402004itm0E010.mp4%253Flabel%253Dmp4_hd%2526template%253D540x960.24.0%2526Expires%253D1557243244%2526ssig%253DQ21JXZZhXZ%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F004DfCS8lx07pQb08g2I010402004itm0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DQ21JXZZhXZ%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4315443926901078&amp;keys=4315443929051009&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F004DfCS8lx07pQb08g2I010402004itm0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DQ21JXZZhXZ%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx4.sinaimg.cn%2Forj480%2F56691f5bly1fy0v042xy5j20f00qogma.jpg&amp;card_height=960&amp;card_width=540&amp;play_count=6&amp;duration=10&amp;short_url=http%3A%2F%2Ft.cn%2FEygkDGZ%3Fm%3D4315443948828685%26u%3D1449729883&amp;encode_mode=&amp;bitrate=775&amp;biz_id=230444&amp;current_mid=4315443948828685&amp;video_orientation=vertical" action-type="feed_list_third_rend" mediasize="540:960" unique-video="H5_femp9_155723965501543935">
                <div node-type="fl_h5_video_pre" style="display: none;">
                    <img src="//wx4.sinaimg.cn/orj480/56691f5bly1fy0v042xy5j20f00qogma.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style=""><div disabletitle="true" tabindex="-1" class="WB_h5video_v2 wbv-paused wbv-controls-enabled wbv-deployable wbv-workinghover wbv-user-inactive" id="H5_femp9_155723965501543935" role="region" aria-label="Video Player" style="width: 100%; height: 100%;"><video id="H5_femp9_155723965501543935_html5_api" class="wbv-tech" tabindex="-1" muted="muted" src="//f.us.sinaimg.cn/004DfCS8lx07pQb08g2I010402004itm0E010.mp4?label=mp4_hd&amp;template=540x960.24.0&amp;Expires=1557243244&amp;ssig=Q21JXZZhXZ&amp;KID=unistore,video"></video><div class="wbv-pop-layer wbv-hidden" aria-disabled="false"><h4><span>展开</span></h4></div><div class="wbv-poster" tabindex="-1" disabletitle="true" aria-disabled="false"><img src="//wx4.sinaimg.cn/orj480/56691f5bly1fy0v042xy5j20f00qogma.jpg"><div class="wbv-poster-info clearfix"><div class="W_fl">6次播放</div><div class="W_fr">0:10</div></div></div><div class="wbv-loading-spinner" disabletitle="true"><i class="W_loading_big"></i></div><button class="wbv-big-play-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">Play</span></button><div class="wbv-ending-layer" disabletitle="true"></div><div class="wbv-error-display wbv-modal-dialog wbv-hidden " disabletitle="true"><div class="box"><h3><i class="W_loading_big"></i></h3><h4><span>视频无法播放，请您稍后再试</span></h4></div></div><div class="wbv-control-bar" dir="ltr" disabletitle="true" role="group"><button class="wbv-play-control wbv-control wbv-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">播放</span></button><div class="wbv-progress-control wbv-control" disabletitle="true"><div class="wbv-progress-holder wbv-slider wbv-slider-horizontal" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Progress Bar"><div class="wbv-load-progress" disabletitle="true" style="width: 32.2672%;"><span class="wbv-control-text"><span>Loaded</span>: 0%</span><div style="left: 0%; width: 100%;"></div></div><div class="wbv-play-progress wbv-slider-bar wbv-slider-handle" disabletitle="true"><span class="wbv-control-text"><span>Progress</span>: 0%</span></div></div></div><div class="wbv-current-time wbv-time-control wbv-control" disabletitle="true"><div class="wbv-current-time-display"><span class="wbv-control-text">Current Time</span> 0:00</div></div><div class="wbv-time-control wbv-time-divider" disabletitle="true"><div><span>/</span></div></div><div class="wbv-duration wbv-time-control wbv-control" disabletitle="true"><div class="wbv-duration-display" aria-live="off"><span class="wbv-control-text">Duration Time</span> 0:10</div></div><button class="wbv-pop-control wbv-control wbv-button" disabletitle="false" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">弹层播放</span></button><button class="wbv-fullscreen-control wbv-control wbv-button wbv-hidden" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">进入全屏</span></button><div class="wbv-logo" disabletitle="true"></div><div class="wbv-volume-panel wbv-control wbv-volume-panel-vertical" disabletitle="true"><button class="wbv-mute-control wbv-control wbv-button wbv-vol-0" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">音量</span></button><div class="wbv-volume-control wbv-control wbv-volume-vertical wbv-hidden" disabletitle="true"><div class="wbv-volume-bar wbv-slider-bar wbv-slider wbv-slider-vertical" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Volume Level" aria-live="polite" aria-valuetext="0%"><div class="wbv-volume-level wbv-volume-handle" disabletitle="true"><span class="wbv-control-text"></span></div></div></div></div></div><div class="wbv-right-pop wbv-hidden" disabletitle="true"><ul class="wbv-menu-content" role="list"><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);"></a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">音量</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">视频地址</a></li></ul></div><div class="wbv-right-layer wbv-hidden" disabletitle="true"><div class="ipt"><input name="CopyUrlInput" type="text" readonly="readonly"><a class="V_ficon V_ficon_close" aria-disabled="false"></a></div></div></div></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4315443948828685"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4315443948828685&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读327次">阅读 327</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H6tLAB2K9&amp;mid=4315443948828685&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4315443948828685"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4315443948828685&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4315443655226523" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4315443655226523&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4315443655226523" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4315443655226523" target="_blank" href="/1449729883/H6tL7lVEL?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-09 22:23" date="1544365407000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4315443655226523:fromprofile"> 2018-12-9 22:23</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/19DcdW" rel="nofollow">微博视频</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4315443655226523:1034-video:1034%3A4315443628817539:page_100505_home:1449729883:4315443655226523:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/Eygk60p" alt="http://t.cn/Eygk60p" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a WB_video_h5_v2" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4315443655226523:1034-video:4315443655226523:1034%3A4315443628817539:profile:1449729883:4315443655226523:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F003CE0OMlx07pQaV3ZwQ010402005WiU0E010.mp4%253Flabel%253Dmp4_hd%2526template%253D540x960.24.0%2526Expires%253D1557243244%2526ssig%253Dz0JRoY3aMI%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F003CE0OMlx07pQaV3ZwQ010402005WiU0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3Dz0JRoY3aMI%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4315443628817539&amp;keys=4315443631258330&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F003CE0OMlx07pQaV3ZwQ010402005WiU0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3Dz0JRoY3aMI%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx2.sinaimg.cn%2Forj480%2F56691f5bly1fy0uyvs0plj20f00qoq3f.jpg&amp;card_height=960&amp;card_width=540&amp;play_count=4&amp;duration=10&amp;short_url=http%3A%2F%2Ft.cn%2FEygk60p%3Fm%3D4315443655226523%26u%3D1449729883&amp;encode_mode=&amp;bitrate=1076&amp;biz_id=230444&amp;current_mid=4315443655226523&amp;video_orientation=vertical" action-type="feed_list_third_rend" mediasize="540:960" unique-video="H5_n6baj_155723965501572136">
                <div node-type="fl_h5_video_pre" style="display: none;">
                    <img src="//wx2.sinaimg.cn/orj480/56691f5bly1fy0uyvs0plj20f00qoq3f.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style=""><div disabletitle="true" tabindex="-1" class="WB_h5video_v2 wbv-paused wbv-controls-enabled wbv-deployable wbv-workinghover wbv-user-inactive" id="H5_n6baj_155723965501572136" role="region" aria-label="Video Player" style="width: 100%; height: 100%;"><video id="H5_n6baj_155723965501572136_html5_api" class="wbv-tech" tabindex="-1" muted="muted" src="//f.us.sinaimg.cn/003CE0OMlx07pQaV3ZwQ010402005WiU0E010.mp4?label=mp4_hd&amp;template=540x960.24.0&amp;Expires=1557243244&amp;ssig=z0JRoY3aMI&amp;KID=unistore,video"></video><div class="wbv-pop-layer wbv-hidden" aria-disabled="false"><h4><span>展开</span></h4></div><div class="wbv-poster" tabindex="-1" disabletitle="true" aria-disabled="false"><img src="//wx2.sinaimg.cn/orj480/56691f5bly1fy0uyvs0plj20f00qoq3f.jpg"><div class="wbv-poster-info clearfix"><div class="W_fl">4次播放</div><div class="W_fr">0:10</div></div></div><div class="wbv-loading-spinner" disabletitle="true"><i class="W_loading_big"></i></div><button class="wbv-big-play-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">Play</span></button><div class="wbv-ending-layer" disabletitle="true"></div><div class="wbv-error-display wbv-modal-dialog wbv-hidden " disabletitle="true"><div class="box"><h3><i class="W_loading_big"></i></h3><h4><span>视频无法播放，请您稍后再试</span></h4></div></div><div class="wbv-control-bar" dir="ltr" disabletitle="true" role="group"><button class="wbv-play-control wbv-control wbv-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">播放</span></button><div class="wbv-progress-control wbv-control" disabletitle="true"><div class="wbv-progress-holder wbv-slider wbv-slider-horizontal" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Progress Bar"><div class="wbv-load-progress" disabletitle="true" style="width: 29.127%;"><span class="wbv-control-text"><span>Loaded</span>: 0%</span><div style="left: 0%; width: 100%;"></div></div><div class="wbv-play-progress wbv-slider-bar wbv-slider-handle" disabletitle="true"><span class="wbv-control-text"><span>Progress</span>: 0%</span></div></div></div><div class="wbv-current-time wbv-time-control wbv-control" disabletitle="true"><div class="wbv-current-time-display"><span class="wbv-control-text">Current Time</span> 0:00</div></div><div class="wbv-time-control wbv-time-divider" disabletitle="true"><div><span>/</span></div></div><div class="wbv-duration wbv-time-control wbv-control" disabletitle="true"><div class="wbv-duration-display" aria-live="off"><span class="wbv-control-text">Duration Time</span> 0:10</div></div><button class="wbv-pop-control wbv-control wbv-button" disabletitle="false" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">弹层播放</span></button><button class="wbv-fullscreen-control wbv-control wbv-button wbv-hidden" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">进入全屏</span></button><div class="wbv-logo" disabletitle="true"></div><div class="wbv-volume-panel wbv-control wbv-volume-panel-vertical" disabletitle="true"><button class="wbv-mute-control wbv-control wbv-button wbv-vol-0" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">音量</span></button><div class="wbv-volume-control wbv-control wbv-volume-vertical wbv-hidden" disabletitle="true"><div class="wbv-volume-bar wbv-slider-bar wbv-slider wbv-slider-vertical" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Volume Level" aria-live="polite" aria-valuetext="0%"><div class="wbv-volume-level wbv-volume-handle" disabletitle="true"><span class="wbv-control-text"></span></div></div></div></div></div><div class="wbv-right-pop wbv-hidden" disabletitle="true"><ul class="wbv-menu-content" role="list"><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);"></a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">音量</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">视频地址</a></li></ul></div><div class="wbv-right-layer wbv-hidden" disabletitle="true"><div class="ipt"><input name="CopyUrlInput" type="text" readonly="readonly"><a class="V_ficon V_ficon_close" aria-disabled="false"></a></div></div></div></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4315443655226523"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4315443655226523&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读318次">阅读 318</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H6tL7lVEL&amp;mid=4315443655226523&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4315443655226523"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4315443655226523&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4315443357844616" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4315443357844616&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4315443357844616" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4315443357844616" target="_blank" href="/1449729883/H6tKDwUK4?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-09 22:22" date="1544365336000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4315443357844616:fromprofile"> 2018-12-9 22:22</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/19DcdW" rel="nofollow">微博视频</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4315443357844616:1034-video:1034%3A4315443331305430:page_100505_home:1449729883:4315443357844616:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/EygDdMO" alt="http://t.cn/EygDdMO" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a WB_video_h5_v2" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4315443357844616:1034-video:4315443357844616:1034%3A4315443331305430:profile:1449729883:4315443357844616:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F002F2Jt3lx07pQaPvWnK010402007xKs0E010.mp4%253Flabel%253Dmp4_hd%2526template%253D540x960.24.0%2526Expires%253D1557243244%2526ssig%253DUY22C2R3mM%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F002F2Jt3lx07pQaPvWnK010402007xKs0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DUY22C2R3mM%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4315443331305430&amp;keys=4315443333433603&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F002F2Jt3lx07pQaPvWnK010402007xKs0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DUY22C2R3mM%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx3.sinaimg.cn%2Forj480%2F56691f5bly1fy0uxo1mw1j20f00qomyj.jpg&amp;card_height=960&amp;card_width=540&amp;play_count=3&amp;duration=13&amp;short_url=http%3A%2F%2Ft.cn%2FEygDdMO%3Fm%3D4315443357844616%26u%3D1449729883&amp;encode_mode=&amp;bitrate=1012&amp;biz_id=230444&amp;current_mid=4315443357844616&amp;video_orientation=vertical" action-type="feed_list_third_rend" mediasize="540:960" unique-video="H5_x6ixl_155723965501573967">
                <div node-type="fl_h5_video_pre" style="display: none;">
                    <img src="//wx3.sinaimg.cn/orj480/56691f5bly1fy0uxo1mw1j20f00qomyj.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style=""><div disabletitle="true" tabindex="-1" class="WB_h5video_v2 wbv-paused wbv-controls-enabled wbv-deployable wbv-workinghover wbv-user-inactive" id="H5_x6ixl_155723965501573967" role="region" aria-label="Video Player" style="width: 100%; height: 100%;"><video id="H5_x6ixl_155723965501573967_html5_api" class="wbv-tech" tabindex="-1" muted="muted" src="//f.us.sinaimg.cn/002F2Jt3lx07pQaPvWnK010402007xKs0E010.mp4?label=mp4_hd&amp;template=540x960.24.0&amp;Expires=1557243244&amp;ssig=UY22C2R3mM&amp;KID=unistore,video"></video><div class="wbv-pop-layer wbv-hidden" aria-disabled="false"><h4><span>展开</span></h4></div><div class="wbv-poster" tabindex="-1" disabletitle="true" aria-disabled="false"><img src="//wx3.sinaimg.cn/orj480/56691f5bly1fy0uxo1mw1j20f00qomyj.jpg"><div class="wbv-poster-info clearfix"><div class="W_fl">3次播放</div><div class="W_fr">0:13</div></div></div><div class="wbv-loading-spinner" disabletitle="true"><i class="W_loading_big"></i></div><button class="wbv-big-play-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">Play</span></button><div class="wbv-ending-layer" disabletitle="true"></div><div class="wbv-error-display wbv-modal-dialog wbv-hidden " disabletitle="true"><div class="box"><h3><i class="W_loading_big"></i></h3><h4><span>视频无法播放，请您稍后再试</span></h4></div></div><div class="wbv-control-bar" dir="ltr" disabletitle="true" role="group"><button class="wbv-play-control wbv-control wbv-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">播放</span></button><div class="wbv-progress-control wbv-control" disabletitle="true"><div class="wbv-progress-holder wbv-slider wbv-slider-horizontal" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Progress Bar"><div class="wbv-load-progress" disabletitle="true" style="width: 27.3317%;"><span class="wbv-control-text"><span>Loaded</span>: 0%</span><div style="left: 0%; width: 100%;"></div></div><div class="wbv-play-progress wbv-slider-bar wbv-slider-handle" disabletitle="true"><span class="wbv-control-text"><span>Progress</span>: 0%</span></div></div></div><div class="wbv-current-time wbv-time-control wbv-control" disabletitle="true"><div class="wbv-current-time-display"><span class="wbv-control-text">Current Time</span> 0:00</div></div><div class="wbv-time-control wbv-time-divider" disabletitle="true"><div><span>/</span></div></div><div class="wbv-duration wbv-time-control wbv-control" disabletitle="true"><div class="wbv-duration-display" aria-live="off"><span class="wbv-control-text">Duration Time</span> 0:13</div></div><button class="wbv-pop-control wbv-control wbv-button" disabletitle="false" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">弹层播放</span></button><button class="wbv-fullscreen-control wbv-control wbv-button wbv-hidden" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">进入全屏</span></button><div class="wbv-logo" disabletitle="true"></div><div class="wbv-volume-panel wbv-control wbv-volume-panel-vertical" disabletitle="true"><button class="wbv-mute-control wbv-control wbv-button wbv-vol-0" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">音量</span></button><div class="wbv-volume-control wbv-control wbv-volume-vertical wbv-hidden" disabletitle="true"><div class="wbv-volume-bar wbv-slider-bar wbv-slider wbv-slider-vertical" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Volume Level" aria-live="polite" aria-valuetext="0%"><div class="wbv-volume-level wbv-volume-handle" disabletitle="true"><span class="wbv-control-text"></span></div></div></div></div></div><div class="wbv-right-pop wbv-hidden" disabletitle="true"><ul class="wbv-menu-content" role="list"><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);"></a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">音量</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">视频地址</a></li></ul></div><div class="wbv-right-layer wbv-hidden" disabletitle="true"><div class="ipt"><input name="CopyUrlInput" type="text" readonly="readonly"><a class="V_ficon V_ficon_close" aria-disabled="false"></a></div></div></div></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4315443357844616"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4315443357844616&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读302次">阅读 302</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H6tKDwUK4&amp;mid=4315443357844616&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4315443357844616"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4315443357844616&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4315442996858100" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4315442996858100&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4315442996858100" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4315442996858100" target="_blank" href="/1449729883/H6tK3sM6w?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-09 22:20" date="1544365250000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4315442996858100:fromprofile"> 2018-12-9 22:20</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/19DcdW" rel="nofollow">微博视频</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4315442996858100:1034-video:1034%3A4315442966398160:page_100505_home:1449729883:4315442996858100:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/EygDbfS" alt="http://t.cn/EygDbfS" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a WB_video_h5_v2" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4315442996858100:1034-video:4315442996858100:1034%3A4315442966398160:profile:1449729883:4315442996858100:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F002SDxKWlx07pQaKmv6M010402005On30E010.mp4%253Flabel%253Dmp4_hd%2526template%253D540x960.24.0%2526Expires%253D1557243244%2526ssig%253DqRQ0PW%25252F6ol%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F002SDxKWlx07pQaKmv6M010402005On30E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DqRQ0PW%252F6ol%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4315442966398160&amp;keys=4315442968578006&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F002SDxKWlx07pQaKmv6M010402005On30E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DqRQ0PW%252F6ol%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx4.sinaimg.cn%2Forj480%2F56691f5bly1fy0uw6jyzgj20f00qojs8.jpg&amp;card_height=960&amp;card_width=540&amp;play_count=1&amp;duration=11&amp;short_url=http%3A%2F%2Ft.cn%2FEygDbfS%3Fm%3D4315442996858100%26u%3D1449729883&amp;encode_mode=&amp;bitrate=963&amp;biz_id=230444&amp;current_mid=4315442996858100&amp;video_orientation=vertical" action-type="feed_list_third_rend" mediasize="540:960" unique-video="H5_2fr37_155723965501625608">
                <div node-type="fl_h5_video_pre" style="display: none;">
                    <img src="//wx4.sinaimg.cn/orj480/56691f5bly1fy0uw6jyzgj20f00qojs8.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style=""><div disabletitle="true" tabindex="-1" class="WB_h5video_v2 wbv-paused wbv-controls-enabled wbv-deployable wbv-workinghover wbv-user-inactive" id="H5_2fr37_155723965501625608" role="region" aria-label="Video Player" style="width: 100%; height: 100%;"><video id="H5_2fr37_155723965501625608_html5_api" class="wbv-tech" tabindex="-1" muted="muted" src="//f.us.sinaimg.cn/002SDxKWlx07pQaKmv6M010402005On30E010.mp4?label=mp4_hd&amp;template=540x960.24.0&amp;Expires=1557243244&amp;ssig=qRQ0PW%2F6ol&amp;KID=unistore,video"></video><div class="wbv-pop-layer wbv-hidden" aria-disabled="false"><h4><span>展开</span></h4></div><div class="wbv-poster" tabindex="-1" disabletitle="true" aria-disabled="false"><img src="//wx4.sinaimg.cn/orj480/56691f5bly1fy0uw6jyzgj20f00qojs8.jpg"><div class="wbv-poster-info clearfix"><div class="W_fl">1次播放</div><div class="W_fr">0:11</div></div></div><div class="wbv-loading-spinner" disabletitle="true"><i class="W_loading_big"></i></div><button class="wbv-big-play-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">Play</span></button><div class="wbv-ending-layer" disabletitle="true"></div><div class="wbv-error-display wbv-modal-dialog wbv-hidden " disabletitle="true"><div class="box"><h3><i class="W_loading_big"></i></h3><h4><span>视频无法播放，请您稍后再试</span></h4></div></div><div class="wbv-control-bar" dir="ltr" disabletitle="true" role="group"><button class="wbv-play-control wbv-control wbv-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">播放</span></button><div class="wbv-progress-control wbv-control" disabletitle="true"><div class="wbv-progress-holder wbv-slider wbv-slider-horizontal" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Progress Bar"><div class="wbv-load-progress" disabletitle="true" style="width: 32.1143%;"><span class="wbv-control-text"><span>Loaded</span>: 0%</span><div style="left: 0%; width: 100%;"></div></div><div class="wbv-play-progress wbv-slider-bar wbv-slider-handle" disabletitle="true"><span class="wbv-control-text"><span>Progress</span>: 0%</span></div></div></div><div class="wbv-current-time wbv-time-control wbv-control" disabletitle="true"><div class="wbv-current-time-display"><span class="wbv-control-text">Current Time</span> 0:00</div></div><div class="wbv-time-control wbv-time-divider" disabletitle="true"><div><span>/</span></div></div><div class="wbv-duration wbv-time-control wbv-control" disabletitle="true"><div class="wbv-duration-display" aria-live="off"><span class="wbv-control-text">Duration Time</span> 0:11</div></div><button class="wbv-pop-control wbv-control wbv-button" disabletitle="false" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">弹层播放</span></button><button class="wbv-fullscreen-control wbv-control wbv-button wbv-hidden" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">进入全屏</span></button><div class="wbv-logo" disabletitle="true"></div><div class="wbv-volume-panel wbv-control wbv-volume-panel-vertical" disabletitle="true"><button class="wbv-mute-control wbv-control wbv-button wbv-vol-0" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">音量</span></button><div class="wbv-volume-control wbv-control wbv-volume-vertical wbv-hidden" disabletitle="true"><div class="wbv-volume-bar wbv-slider-bar wbv-slider wbv-slider-vertical" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Volume Level" aria-live="polite" aria-valuetext="0%"><div class="wbv-volume-level wbv-volume-handle" disabletitle="true"><span class="wbv-control-text"></span></div></div></div></div></div><div class="wbv-right-pop wbv-hidden" disabletitle="true"><ul class="wbv-menu-content" role="list"><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);"></a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">音量</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">视频地址</a></li></ul></div><div class="wbv-right-layer wbv-hidden" disabletitle="true"><div class="ipt"><input name="CopyUrlInput" type="text" readonly="readonly"><a class="V_ficon V_ficon_close" aria-disabled="false"></a></div></div></div></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4315442996858100"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4315442996858100&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读315次">阅读 315</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H6tK3sM6w&amp;mid=4315442996858100&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4315442996858100"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4315442996858100&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4315442627051310" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4315442627051310&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4315442627051310" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4315442627051310" target="_blank" href="/1449729883/H6tJstAmO?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-09 22:19" date="1544365162000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4315442627051310:fromprofile"> 2018-12-9 22:19</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/4fw5aJ" rel="nofollow">秒拍网页版</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4315442627051310:1034-video:1034%3A4315442592815141:page_100505_home:1449729883:4315442627051310:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/EygeCus" alt="http://t.cn/EygeCus" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4315442627051310:1034-video:4315442627051310:1034%3A4315442592815141:profile:1449729883:4315442627051310:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F004c957Klx07pQaC2vHy010402001sSi0E010.mp4%253Flabel%253Dmp4_hd%2526template%253D540x480.28.0%2526Expires%253D1557243244%2526ssig%253DplbWf992xp%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F004c957Klx07pQaC2vHy010402001sSi0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x480.28.0%26Expires%3D1557243244%26ssig%3DplbWf992xp%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4315442592815141&amp;keys=4315442595234761&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F004c957Klx07pQaC2vHy010402001sSi0E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x480.28.0%26Expires%3D1557243244%26ssig%3DplbWf992xp%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx3.sinaimg.cn%2Forj480%2F56691f5bly1fy0uumsh9cj20k00hsjs9.jpg&amp;card_height=480&amp;card_width=540&amp;play_count=3&amp;duration=12&amp;short_url=http%3A%2F%2Ft.cn%2FEygeCus%3Fm%3D4315442627051310%26u%3D1449729883&amp;encode_mode=&amp;bitrate=219&amp;biz_id=230444&amp;current_mid=4315442627051310&amp;video_orientation=horizontal" action-type="feed_list_third_rend" mediasize="540:480" unique-video="H5_epncq_155723965501675959">
                <div node-type="fl_h5_video_pre">
                    <img src="//wx3.sinaimg.cn/orj480/56691f5bly1fy0uumsh9cj20k00hsjs9.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style="display:none"></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4315442627051310"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4315442627051310&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读301次">阅读 301</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H6tJstAmO&amp;mid=4315442627051310&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4315442627051310"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4315442627051310&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4315442317587483" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4315442317587483&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4315442317587483" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4315442317587483" target="_blank" href="/1449729883/H6tIXvPQL?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-09 22:18" date="1544365088000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4315442317587483:fromprofile"> 2018-12-9 22:18</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/19DcdW" rel="nofollow">微博视频</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4315442317587483:1034-video:1034%3A4315442282721426:page_100505_home:1449729883:4315442317587483:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/EyggeOI" alt="http://t.cn/EyggeOI" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4315442317587483:1034-video:4315442317587483:1034%3A4315442282721426:profile:1449729883:4315442317587483:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F004xFAE7lx07pQaypQSQ010402007sGu0E010.mp4%253Flabel%253Dmp4_hd%2526template%253D576x1024.24.0%2526Expires%253D1557243244%2526ssig%253D8V6gPYXlEk%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F004xFAE7lx07pQaypQSQ010402007sGu0E010.mp4%3Flabel%3Dmp4_hd%26template%3D576x1024.24.0%26Expires%3D1557243244%26ssig%3D8V6gPYXlEk%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4315442282721426&amp;keys=4315442284919102&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F004xFAE7lx07pQaypQSQ010402007sGu0E010.mp4%3Flabel%3Dmp4_hd%26template%3D576x1024.24.0%26Expires%3D1557243244%26ssig%3D8V6gPYXlEk%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx1.sinaimg.cn%2Forj480%2F56691f5bly1fy0utcnu98j20g00sgdh0.jpg&amp;card_height=1024&amp;card_width=576&amp;play_count=1&amp;duration=15&amp;short_url=http%3A%2F%2Ft.cn%2FEyggeOI%3Fm%3D4315442317587483%26u%3D1449729883&amp;encode_mode=&amp;bitrate=913&amp;biz_id=230444&amp;current_mid=4315442317587483&amp;video_orientation=vertical" action-type="feed_list_third_rend" mediasize="576:1024" unique-video="H5_b1g4q_1557239655017405610">
                <div node-type="fl_h5_video_pre">
                    <img src="//wx1.sinaimg.cn/orj480/56691f5bly1fy0utcnu98j20g00sgdh0.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style="display:none"></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4315442317587483"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4315442317587483&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读301次">阅读 301</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H6tIXvPQL&amp;mid=4315442317587483&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4315442317587483"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4315442317587483&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4315441939430890" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4315441939430890&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4315441939430890" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4315441939430890" target="_blank" href="/1449729883/H6tIlDzp8?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-12-09 22:16" date="1544364998000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4315441939430890:fromprofile"> 2018-12-9 22:16</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/19DcdW" rel="nofollow">微博视频</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4315441939430890:1034-video:1034%3A4315441913619720:page_100505_home:1449729883:4315441939430890:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/EyggGSZ" alt="http://t.cn/EyggGSZ" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4315441939430890:1034-video:4315441939430890:1034%3A4315441913619720:profile:1449729883:4315441939430890:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F0011rSaglx07pQaqFgne010402004yR70E010.mp4%253Flabel%253Dmp4_hd%2526template%253D540x960.24.0%2526Expires%253D1557243244%2526ssig%253DUR%25252BRQMP1LA%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F0011rSaglx07pQaqFgne010402004yR70E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DUR%252BRQMP1LA%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4315441913619720&amp;keys=4315441915788388&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F0011rSaglx07pQaqFgne010402004yR70E010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DUR%252BRQMP1LA%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx3.sinaimg.cn%2Forj480%2F56691f5bly1fy0urspfavj20f00qogmz.jpg&amp;card_height=960&amp;card_width=540&amp;play_count=3&amp;duration=7&amp;short_url=http%3A%2F%2Ft.cn%2FEyggGSZ%3Fm%3D4315441939430890%26u%3D1449729883&amp;encode_mode=&amp;bitrate=1057&amp;biz_id=230444&amp;current_mid=4315441939430890&amp;video_orientation=vertical" action-type="feed_list_third_rend" mediasize="540:960" unique-video="H5_irikk_1557239655017391911">
                <div node-type="fl_h5_video_pre">
                    <img src="//wx3.sinaimg.cn/orj480/56691f5bly1fy0urspfavj20f00qogmz.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style="display:none"></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4315441939430890"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4315441939430890&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读289次">阅读 289</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H6tIlDzp8&amp;mid=4315441939430890&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4315441939430890"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4315441939430890&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4308559996636587" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4308559996636587&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4308559996636587" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4308559996636587" target="_blank" href="/1449729883/H3AGrrQtJ?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-11-20 22:30" date="1542724215000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4308559996636587:fromprofile"> 2018-11-20 22:30</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/19DcdW" rel="nofollow">微博视频</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4308559996636587:1034-video:1034%3A4308559974887117:page_100505_home:1449729883:4308559996636587:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/E2YPHCL" alt="http://t.cn/E2YPHCL" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4308559996636587:1034-video:4308559996636587:1034%3A4308559974887117:profile:1449729883:4308559996636587:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F004yHtNnlx07plSelHiU010402009yrk0k010.mp4%253Flabel%253Dmp4_hd%2526template%253D540x960.24.0%2526Expires%253D1557243244%2526ssig%253DgfzTN2k6Ra%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F004yHtNnlx07plSelHiU010402009yrk0k010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DgfzTN2k6Ra%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4308559974887117&amp;keys=4308559977268842&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F004yHtNnlx07plSelHiU010402009yrk0k010.mp4%3Flabel%3Dmp4_hd%26template%3D540x960.24.0%26Expires%3D1557243244%26ssig%3DgfzTN2k6Ra%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx1.sinaimg.cn%2Forj480%2F56691f5bly1fxewe44bw3j20f00qogms.jpg&amp;card_height=960&amp;card_width=540&amp;play_count=1&amp;duration=13&amp;short_url=http%3A%2F%2Ft.cn%2FE2YPHCL%3Fm%3D4308559996636587%26u%3D1449729883&amp;encode_mode=&amp;bitrate=1278&amp;biz_id=230444&amp;current_mid=4308559996636587&amp;video_orientation=vertical" action-type="feed_list_third_rend" mediasize="540:960" unique-video="H5_wys10_155723965501868612">
                <div node-type="fl_h5_video_pre">
                    <img src="//wx1.sinaimg.cn/orj480/56691f5bly1fxewe44bw3j20f00qogms.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style="display:none"></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4308559996636587"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4308559996636587&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读346次">阅读 346</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H3AGrrQtJ&amp;mid=4308559996636587&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4308559996636587"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4308559996636587&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                	        		    		    		    		    	        	<div tbinfo="ouid=1449729883" action-type="feed_list_item" diss-data="" mid="4308559359576071" class="WB_cardwrap WB_feed_type S_bg2 WB_feed_like ">
        <div class="WB_feed_detail clearfix" node-type="feed_content">
                        <div class="WB_screen W_fr" node-type="fl_screen_box">
    <div class="screen_box"><a href="javascript:void(0);" action-type="fl_menu"><i class="W_ficon ficon_arrow_down S_ficon">c</i></a>
        <div class="layer_menu_list" style="display: none; position: absolute; z-index: 999;" node-type="fl_menu_right">
            <ul>
                                                    <li><a title="删除此条微博" href="javascript:void(0);" action-type="feed_list_delete" suda-uatrack="key=profile_feed&amp;value=delete_host">删除</a></li>
                                        <li><a action-type="fl_setToTop" href="javascript:void(0);" title="在微博区域置顶显示" suda-uatrack="key=profile_feed&amp;value=stickie_host">置顶</a></li>                                            <li><a href="javascript:void(0);" node-type="" action-type="fl_reEdit" action-data="isReEdit=1&amp;mid=4308559359576071&amp;domain=&amp;is_ori=1&amp;can_edit=0" suda-uatrack="">编辑微博</a></li>
                                                            
                                            <li><a href="javascript:void(0);" _taglist="" node-type="feed_list_addTag" action-type="fl_addTag" action-data="mid=4308559359576071" suda-uatrack="key=profile_feed&amp;value=addtag_host">加标签</a></li>
                                                                                    <li><a href="javascript:;" action-type="fl_friendVisible" action-data="visible=2" suda-uatrack="key=profile_feed&amp;value=friendsonly_host" title="转换为好友圈可见">转换为好友圈可见</a></li>
                                                <li><a href="javascript:;" action-type="fl_personalVisible" action-data="visible=1" suda-uatrack="key=profile_feed&amp;value=meonly_host" title="转换为仅自己可见">转换为仅自己可见</a></li>
                                                                    </ul>
        </div>
    </div>
</div>
        <div class="WB_face W_fl">
            <div class="face"><a target="_blank" class="W_face_radius" suda-uatrack="key=noload_singlepage&amp;value=user_pic" href="//weibo.com/u/1449729883?refer_flag=1005055010_" title="次郎花子"><img usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" src="//tva2.sinaimg.cn/crop.0.0.512.512.180/56691f5bjw8eytdetyrj6j20e80e8ab6.jpg" width="50" height="50" alt="次郎花子" class="W_face_radius"></a></div>
                    </div>

        <div class="WB_detail">
            <div class="WB_info">
                <a href="//weibo.com/u/1449729883?refer_flag=1005055010_" target="_blank" class="W_f14 W_fb S_txt1" usercard="id=1449729883&amp;type=0&amp;refer_flag=1005055010_" suda-uatrack="key=noload_singlepage&amp;value=user_name">次郎花子</a>
                                            </div>
            <div class="WB_from S_txt2">
                                <a name="4308559359576071" target="_blank" href="/1449729883/H3AFpEbaL?from=page_1005051449729883_profile&amp;wvr=6&amp;mod=weibotime" title="2018-11-20 22:27" date="1542724063000" class="S_txt2" node-type="feed_list_item_date" suda-data="key=tblog_home_new&amp;value=feed_time:4308559359576071:fromprofile"> 2018-11-20 22:27</a> 来自 <a class="S_txt2" suda-uatrack="key=profile_feed&amp;value=pubfrom_host" action-type="app_source" target="_blank" href="http://app.weibo.com/t/feed/4fw5aJ" rel="nofollow">秒拍网页版</a>            	            </div>

            <div class="PCD_user_b S_bg1" node-type="follow_recommend_box" style="display:none"></div>

            <div class="WB_text W_f14" node-type="feed_list_content" nick-name="次郎花子">
                                                                                                                        <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%97%A5%E6%9C%AC%23">#日本#</a> <a target="_blank" render="ext" suda-uatrack="key=topic_click&amp;value=click_topic" class="a_topic" extra-data="type=topic" href="http://s.weibo.com/weibo?q=%23%E6%8A%96%E9%9F%B3%23">#抖音#</a> <a suda-uatrack="key=tblog_card&amp;value=click_title:4308559359576071:1034-video:1034%3A4308559324765111:page_100505_home:1449729883:4308559359576071:1449729883" title="次郎花子的秒拍视频" href="http://t.cn/E2YvS8d" alt="http://t.cn/E2YvS8d" action-type="feed_list_url" target="_blank" rel="noopener noreferrer"><i class="W_ficon ficon_cd_video">L</i>次郎花子的秒拍视频</a> ​​​​                                            </div>
                                                                <div class="WB_expand_media_box " style="display: none;" node-type="feed_list_media_disp"></div>
                                                                    <!-- 引用文件时，必须对midia_info赋值 -->
<!-- 微博心情，独立于标准的ul节点 -->
                    <div class="WB_media_wrap clearfix" node-type="feed_list_media_prev">
        <div class="media_box">
            <!--判断图片的个数，渲染图片-->
                                                                                                                                                                                            
<!-- VideoCard -->
         

<!--  酷燃视频 -->
            <ul class="WB_media_a WB_media_a_m1 clearfix">
            <li class="WB_video  S_bg1 WB_video_mini WB_video_a" suda-uatrack="key=multimedia_bigplay&amp;value=bigplay_button:4308559359576071:1034-video:4308559359576071:1034%3A4308559324765111:profile:1449729883:4308559359576071:1449729883:230444" node-type="fl_h5_video" video-sources="fluency=http%253A%252F%252Ff.us.sinaimg.cn%252F000crkbZlx07plS3kXCU010402004vdO0k010.mp4%253Flabel%253Dmp4_hd%2526template%253D852x480.28.0%2526Expires%253D1557243244%2526ssig%253D3FoKQYwRWr%2526KID%253Dunistore%252Cvideo&amp;480=http%3A%2F%2Ff.us.sinaimg.cn%2F000crkbZlx07plS3kXCU010402004vdO0k010.mp4%3Flabel%3Dmp4_hd%26template%3D852x480.28.0%26Expires%3D1557243244%26ssig%3D3FoKQYwRWr%26KID%3Dunistore%2Cvideo&amp;720=&amp;qType=480" action-data="type=feedvideo&amp;objectid=1034:4308559324765111&amp;keys=4308559327181230&amp;video_src=%2F%2Ff.us.sinaimg.cn%2F000crkbZlx07plS3kXCU010402004vdO0k010.mp4%3Flabel%3Dmp4_hd%26template%3D852x480.28.0%26Expires%3D1557243244%26ssig%3D3FoKQYwRWr%26KID%3Dunistore%2Cvideo&amp;cover_img=%2F%2Fwx4.sinaimg.cn%2Forj480%2F56691f5bly1fxewbgg2xmj20qo0f0dh7.jpg&amp;card_height=480&amp;card_width=852&amp;play_count=3&amp;duration=11&amp;short_url=http%3A%2F%2Ft.cn%2FE2YvS8d%3Fm%3D4308559359576071%26u%3D1449729883&amp;encode_mode=&amp;bitrate=700&amp;biz_id=230444&amp;current_mid=4308559359576071&amp;video_orientation=horizontal" action-type="feed_list_third_rend" mediasize="852:480" unique-video="H5_zlwye_1557239655018759313">
                <div node-type="fl_h5_video_pre">
                    <img src="//wx4.sinaimg.cn/orj480/56691f5bly1fxewbgg2xmj20qo0f0dh7.jpg" width="120" height="120"><i class="W_icon icon_playvideo"></i>
                </div>
                <div node-type="fl_h5_video_disp" style="display:none"></div>
            </li>
        </ul>
    
<!-- /VideoCard -->
                                                                                                                                            </div>
    </div>
        <!-- super card-->

                                                                                    <div class="WB_tag  clearfix S_txt2" style="display: none">
                <span class="WB_tag_s" node-type="feed_list_tagList">
                                        <span class="b" id="q1" style="display:none;">
		                                </span>
                                            <span class="o"><a href="javascript:void(0);" title="编辑这条微博的标签" _taglist="" node-type="feed_list_editTag" action-type="fl_addTag" action-data="mid=4308559359576071"><i class="W_ficon ficon_edit S_ficon">7</i></a></span>
                                    </span>
            </div>
                        <!-- feed区 大数据tag -->
                    </div>
        <div class="WB_like" node-type="templeLike_ani" action-data="parise_id=p_0000" style="display:none;">
            <div class="anibox UI_ani" style="background-image:url(//img.t.sinajs.cn/t6/skin/public/like/p_0000_pc.png?version=d5dc5c8dd22e6c3c);"></div>
        </div>
    </div>
    <div class="WB_feed_handle" node-type="feed_list_options">
        <div class="WB_handle">
            <ul class="WB_row_line WB_row_r4 clearfix S_line2">
                                    <li>
                                                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_pop" action-data="mid=4308559359576071&amp;from=p_profile_pc_05" suda-uatrack="key=profile_feed&amp;value=popularize_host"><span class="pos"><span class="line S_line1">
                                                                                    <i class="S_txt2" title="此条微博已经被阅读344次">阅读 344</i>&nbsp;
                                                                                                                            推广                                                                            </span></span>
                            </a>
                        			        </li>
                                                                            <li>
                            <a action-data="allowForward=1&amp;url=https://weibo.com/1449729883/H3AFpEbaL&amp;mid=4308559359576071&amp;name=次郎花子&amp;uid=1449729883&amp;domain=1449729883" action-type="fl_forward" action-history="rec=1" href="javascript:void(0);" class="S_txt2" suda-uatrack="key=profile_feed&amp;value=transfer"><span class="pos"><span class="line S_line1" node-type="forward_btn_text"><span><em class="W_ficon ficon_forward S_ficon"></em><em>转发</em></span></span></span></a>
                            <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                        </li>
                                                    <li>
                                            <a href="javascript:void(0);" class="S_txt2" action-type="fl_comment" action-data="ouid=1449729883&amp;location=profile&amp;comment_type=0" suda-uatrack="key=profile_feed&amp;value=comment:4308559359576071"><span class="pos"><span class="line S_line1" node-type="comment_btn_text"><span><em class="W_ficon ficon_repeat S_ficon"></em><em>评论</em></span></span></span></a>
                                        <span class="arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
                <li class="">
                    <!--cuslike用于前端判断是否显示个性赞，1:显示-->
                    <a href="javascript:void(0);" class="S_txt2" action-type="fl_like" action-data="version=mini&amp;qid=heart&amp;mid=4308559359576071&amp;loc=profile&amp;cuslike=1" title="赞" suda-uatrack="key=profile_feed&amp;value=like"><span class="pos"><span class="line S_line1">
                                                                                                                                                                                                                                                                            <span node-type="like_status" class=""><em class="W_ficon ficon_praised S_txt2">ñ</em><em>赞</em></span>                        </span></span></a>
                    <span class="arrow" node-type="cmtarrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line1"></i><em class="S_bg1_br"></em></span></span>
                </li>
            </ul>
        </div>
    </div>
            <div node-type="feed_list_repeat" class="WB_feed_repeat S_bg1" style="display:none;"></div>
    </div>
	                                        <div class="WB_cardwrap S_bg2" node-type="lazyload">
            <div class="WB_empty WB_empty_narrow">
                <div class="WB_innerwrap">
                    <div class="empty_con clearfix">
                        <p class="text"><i class="W_loading"></i>正在加载中，请稍候...</p>
                    </div>
                </div>
            </div>
        </div>
           
    
    <!--翻页-->
                        
    <!--/翻页-->
</div>
    </div>
		 */
		List<WebElement> wes = driver.findElements(By.cssSelector("h2[class=\"title\"]"));
		if(!wes.isEmpty()){
			videoObj.title = wes.get(0).getText();
		}
		wes = driver.findElements(By.cssSelector("a[class=\"media-user\"]"));
		if(!wes.isEmpty()){
			videoObj.uper = wes.get(0).getText();
		}
		
	}

	private void parseForWeiboTag(WebDriver driver, MyVideoObject videoObj) {
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
		// TODO 
		el1 = driver.findElement(By.cssSelector("div[id=\"pl_feedlist_index\"]"));
		List<WebElement> wes = el1.findElements(By.cssSelector("div[class=\"card-wrap\"]"));
		/*
<div class="card">
        <div class="card-feed">
            <div class="avator">
                <a href="//weibo.com/1796445350?refer_flag=1001030103_" target="_blank" suda-data="key=tblog_search_weibo&amp;value=seqid:155724057076401672028|type:71|t:0|pos:1-0|q:%23%E8%B5%B5%E9%9B%A8%E6%80%9D%E7%94%B3%E8%AF%B7%E5%85%A5%E5%AD%A6%E8%B5%84%E6%96%99%E4%BD%9C%E5%81%87%E8%A2%AB%E9%80%80%E5%AD%A6%23|ext:cate:31,mpos:1,click:user_pic"><img src="//tvax1.sinaimg.cn/crop.5.5.1994.1994.50/6b1394a6ly8fsrwt5ygvpj21jk1jkgy9.jpg"></a>
            </div>
            <!--微博内容-->
            <div class="content" node-type="like">
                <div class="info">
                    <div class="menu s-fr">
                        <a href="javascript:void(0);" action-type="fl_menu"><i class="wbicon">c</i></a>
                        <ul node-type="fl_menu_right" style="display:none;">
                            <li><a href="javascript:void(0);" onclick="javascript:window.open('//service.account.weibo.com/reportspam?rid=4369416319879046&amp;type=1&amp;from=10501&amp;url=&amp;bottomnav=1&amp;wvr=6', 'newwindow', 'height=700, width=550, toolbar =yes, menubar=no, scrollbars=yes, resizable=yes, location=no, status=no');">投诉</a></li>
                                                            <!--主持人流干预-->

                                                    </ul>
                    </div>
                    <div>
                        <a href="//weibo.com/1796445350?refer_flag=1001030103_" class="name" target="_blank" nick-name="锋潮科技" suda-data="key=tblog_search_weibo&amp;value=seqid:155724057076401672028|type:71|t:0|pos:1-0|q:%23%E8%B5%B5%E9%9B%A8%E6%80%9D%E7%94%B3%E8%AF%B7%E5%85%A5%E5%AD%A6%E8%B5%84%E6%96%99%E4%BD%9C%E5%81%87%E8%A2%AB%E9%80%80%E5%AD%A6%23|ext:cate:31,mpos:1,click:user_name">锋潮科技</a>
                        <a href="//verified.weibo.com/verify" target="_blank" title="微博个人认证"><i class="icon-vip icon-vip-g"></i></a>
                        <!--广告微博加关注按钮 -->
                                            </div>
                </div>
                <p class="txt" node-type="feed_list_content" nick-name="锋潮科技">
                    近日富商花650万美元送女儿<em class="s-color-red">赵</em><em class="s-color-red">雨思</em>上斯坦福大学受到热议。<a href="http://s.weibo.com/weibo?q=%23%E6%96%AF%E5%9D%A6%E7%A6%8F%E5%9B%9E%E5%BA%94%E8%B5%B5%E9%9B%A8%E6%80%9D%E9%80%80%E5%AD%A6%E5%8E%9F%E5%9B%A0%23" target="_blank">#斯坦福回应<em class="s-color-red">赵</em><em class="s-color-red">雨思</em><em class="s-color-red">退学</em>原因#</a> ，他们称只在<em class="s-color-red">赵</em><em class="s-color-red">雨思</em><em class="s-color-red">入学</em>收到50万美元帆船项目捐款，没收到650万美元。实情是<a href="http://s.weibo.com/weibo?q=%23%E8%B5%B5%E9%9B%A8%E6%80%9D%E7%94%B3%E8%AF%B7%E5%85%A5%E5%AD%A6%E8%B5%84%E6%96%99%E4%BD%9C%E5%81%87%E8%A2%AB%E9%80%80%E5%AD%A6%23" target="_blank">#<em class="s-color-red">赵</em><em class="s-color-red">雨思</em><em class="s-color-red">申请</em><em class="s-color-red">入学</em><em class="s-color-red">资料</em><em class="s-color-red">作假</em><em class="s-color-red">被</em><em class="s-color-red">退学</em>#</a> 。<a href="http://t.cn/ES8uIqO" target="_blank"><i class="wbicon">L</i>老板联播的秒拍视频</a> ​                </p>
                                                <!--card解析-->
<!--linkcard 不能播放视频-->
        <div class="media media-video-a" node-type="feed_list_media_prev">
        <!--linkcard 不能播放视频-->
<div class="media media-video-a" node-type="feed_list_media_disp" style="display:none;"></div>
<!--视频card-->
<div class="thumbnail" style="height:auto;min-height:281px;">
    <a href="javascript:void(0);" class="WB_video_h5 WB_video_h5_v2" node-type="fl_h5_video" action-data="type=feedvideo&amp;objectid=2017607:4367562931114805&amp;keys=4367562933549299&amp;cover_img=%2F%2Fimgaliyuncdn.miaopai.com%2Fimages%2FZXhQeYG~EgoUGdQi1NzoDqagRzibYUusKNZpoA___daZQ_4.jpg&amp;card_height=360&amp;card_width=640&amp;play_count=7822346&amp;short_url=http%3A%2F%2Ft.cn%2FES8uIqO&amp;encode_mode=&amp;bitrate=296&amp;biz_id=230442&amp;current_mid=0&amp;key=tblog_card&amp;title=%E8%80%81%E6%9D%BF%E8%81%94%E6%92%AD%E7%9A%84%E7%A7%92%E6%8B%8D%E8%A7%86%E9%A2%91&amp;full_url=%2F%2Fmiaopai.com%2Fshow%2FZXhQeYG~EgoUGdQi1NzoDqagRzibYUusKNZpoA__.htm&amp;object_id=2017607:4367562931114805&amp;video_src=%2F%2Fgslb.miaopai.com%2Fstream%2FZXhQeYG~EgoUGdQi1NzoDqagRzibYUusKNZpoA__.mp4%3Fyx%3D%26refer%3Dweibo_app%26vend%3Dweibo%26label%3Dmp4_hd%26mpflag%3D8%26Expires%3D1557244140%26ssig%3DxqQXrQicIZ%26KID%3Dunistore%2Cvideo" action-type="feed_list_third_rend" mediasize="852:480" unique-video="H5_4fs5u_15572405794486150">
        <div node-type="fl_h5_video_pre" style="display: none;">
            <img src="//imgaliyuncdn.miaopai.com/images/ZXhQeYG~EgoUGdQi1NzoDqagRzibYUusKNZpoA___daZQ_4.jpg" alt="" style="display:block;height: 281px;">
            <i class="icon-media icon-play-b"></i>
        </div>
        <div node-type="fl_h5_video_disp" style="">
        <div disabletitle="true" tabindex="-1" class="WB_h5video_v2 wbv-controls-enabled wbv-deployable wbv-workinghover wbv-has-started wbv-paused wbv-ended wbv-user-active" id="H5_4fs5u_15572405794486150" role="region" aria-label="Video Player" style="width: 100%; height: 100%;"><video id="H5_4fs5u_15572405794486150_html5_api" class="wbv-tech" tabindex="-1" muted="muted" src="//gslb.miaopai.com/stream/ZXhQeYG~EgoUGdQi1NzoDqagRzibYUusKNZpoA__.mp4?yx=&amp;refer=weibo_app&amp;vend=weibo&amp;label=mp4_hd&amp;mpflag=8&amp;Expires=1557244140&amp;ssig=xqQXrQicIZ&amp;KID=unistore,video"></video><div class="wbv-poster" tabindex="-1" disabletitle="true" aria-disabled="false"><img src="//imgaliyuncdn.miaopai.com/images/ZXhQeYG~EgoUGdQi1NzoDqagRzibYUusKNZpoA___daZQ_4.jpg"></div><div class="wbv-loading-spinner" disabletitle="true"><i class="W_loading_big"></i></div><button class="wbv-big-play-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">Play</span></button><div class="wbv-ending-layer" disabletitle="true"></div><div class="wbv-error-display wbv-modal-dialog wbv-hidden " disabletitle="true"><div class="box"><h3><i class="W_loading_big"></i></h3><h4><span>视频无法播放，请您稍后再试</span></h4></div></div><div class="wbv-control-bar" dir="ltr" disabletitle="true" role="group"><button class="wbv-play-control wbv-control wbv-button wbv-paused wbv-ended" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">重播</span></button><div class="wbv-progress-control wbv-control" disabletitle="true"><div class="wbv-progress-holder wbv-slider wbv-slider-horizontal" disabletitle="true" role="slider" aria-valuenow="100.00" aria-valuemin="0" aria-valuemax="100" aria-label="Progress Bar" aria-valuetext="1:20 of 1:20"><div class="wbv-load-progress" disabletitle="true" style="width: 100%;"><span class="wbv-control-text"><span>Loaded</span>: 0%</span><div style="left: 0%; width: 100%;"></div></div><div class="wbv-play-progress wbv-slider-bar wbv-slider-handle" disabletitle="true" style="width: 100%;"><span class="wbv-control-text"><span>Progress</span>: 0%</span></div></div></div><div class="wbv-current-time wbv-time-control wbv-control" disabletitle="true"><div class="wbv-current-time-display"><span class="wbv-control-text">Current Time</span> 1:20</div></div><div class="wbv-time-control wbv-time-divider" disabletitle="true"><div><span>/</span></div></div><div class="wbv-duration wbv-time-control wbv-control" disabletitle="true"><div class="wbv-duration-display" aria-live="off"><span class="wbv-control-text">Duration Time</span> 1:20</div></div><button class="wbv-fullscreen-control wbv-control wbv-button" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">进入全屏</span></button><div class="wbv-logo" disabletitle="true"></div><div class="wbv-volume-panel wbv-control wbv-volume-panel-vertical" disabletitle="true"><button class="wbv-mute-control wbv-control wbv-button wbv-vol-0" disabletitle="true" type="button" aria-live="polite" aria-disabled="false"><span aria-hidden="true" class="wbv-icon-placeholder"></span><span class="wbv-control-text">音量</span></button><div class="wbv-volume-control wbv-control wbv-volume-vertical wbv-hidden" disabletitle="true"><div class="wbv-volume-bar wbv-slider-bar wbv-slider wbv-slider-vertical" disabletitle="true" role="slider" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" aria-label="Volume Level" aria-live="polite" aria-valuetext="0%"><div class="wbv-volume-level wbv-volume-handle" disabletitle="true"><span class="wbv-control-text"></span></div></div></div></div></div><div class="wbv-right-pop wbv-hidden" disabletitle="true"><ul class="wbv-menu-content" role="list"><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">播放</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">音量</a></li><li class="wbv-menu-item" disabletitle="true" aria-disabled="false"><a href="javascript:void(0);">视频地址</a></li></ul></div><div class="wbv-right-layer wbv-hidden" disabletitle="true"><div class="ipt"><input name="CopyUrlInput" type="text" readonly="readonly"><a class="V_ficon V_ficon_close" aria-disabled="false"></a></div></div></div></div>
    </a>
</div>
<!--/视频card-->

    </div>
    

<!--/card解析-->
                                <p class="from">
                                            <a href="//weibo.com/1796445350/Ht7PNFrZs?refer_flag=1001030103_" target="_blank" suda-data="key=tblog_search_weibo&amp;value=seqid:155724057076401672028|type:71|t:0|pos:1-0|q:%23%E8%B5%B5%E9%9B%A8%E6%80%9D%E7%94%B3%E8%AF%B7%E5%85%A5%E5%AD%A6%E8%B5%84%E6%96%99%E4%BD%9C%E5%81%87%E8%A2%AB%E9%80%80%E5%AD%A6%23|ext:cate:31,mpos:1,click:wb_time">
                        今天20:51
                        </a>
                                         来自 <a href="http://app.weibo.com/t/feed/6vtZb0" rel="nofollow">微博 weibo.com</a>                </p>
            </div>
            <!--/微博内容-->
        </div>
        <div class="card-act">
            <ul>
                <li><a href="javascript:void(0);" action-type="feed_list_favorite" suda-data="key=tblog_search_weibo&amp;value=seqid:155724057076401672028|type:71|t:0|pos:1-0|q:%23%E8%B5%B5%E9%9B%A8%E6%80%9D%E7%94%B3%E8%AF%B7%E5%85%A5%E5%AD%A6%E8%B5%84%E6%96%99%E4%BD%9C%E5%81%87%E8%A2%AB%E9%80%80%E5%AD%A6%23|ext:cate:31,mpos:1,click:fav">收藏</a></li>
                                                                <li><a href="javascript:void(0);" action-data="allowForward=1&amp;mid=4369416319879046&amp;name=锋潮科技&amp;uid=1796445350&amp;suda-data=key%3Dtblog_search_weibo%26value%3Dseqid%3A155724057076401672028%7Ctype%3A71%7Ct%3A0%7Cpos%3A1-0%7Cq%3A%2523%25E8%25B5%25B5%25E9%259B%25A8%25E6%2580%259D%25E7%2594%25B3%25E8%25AF%25B7%25E5%2585%25A5%25E5%25AD%25A6%25E8%25B5%2584%25E6%2596%2599%25E4%25BD%259C%25E5%2581%2587%25E8%25A2%25AB%25E9%2580%2580%25E5%25AD%25A6%2523%7Cext%3Acate%3A31%2Cclick:do_repost,mid:4369416319879046" action-type="feed_list_forward" suda-data="key=tblog_search_weibo&amp;value=seqid:155724057076401672028|type:71|t:0|pos:1-0|q:%23%E8%B5%B5%E9%9B%A8%E6%80%9D%E7%94%B3%E8%AF%B7%E5%85%A5%E5%AD%A6%E8%B5%84%E6%96%99%E4%BD%9C%E5%81%87%E8%A2%AB%E9%80%80%E5%AD%A6%23|ext:cate:31,mpos:1,click:repost,mid:4369416319879046"> 转发 265</a></li>
                                <li><a href="javascript:void(0);" action-data="pageid=weibo&amp;suda-data=key%3Dtblog_search_weibo%26value%3Dweibo_h_1_p_p" suda-data="key=tblog_search_weibo&amp;value=seqid:155724057076401672028|type:71|t:0|pos:1-0|q:%23%E8%B5%B5%E9%9B%A8%E6%80%9D%E7%94%B3%E8%AF%B7%E5%85%A5%E5%AD%A6%E8%B5%84%E6%96%99%E4%BD%9C%E5%81%87%E8%A2%AB%E9%80%80%E5%AD%A6%23|ext:cate:31,mpos:1,click:comment" action-type="feed_list_comment">评论 206</a></li>
                                                                <li><a title="赞" action-data="mid=4369416319879046" action-type="feed_list_like" href="javascript:void(0);" suda-data="key=tblog_search_weibo&amp;value=seqid:155724057076401672028|type:71|t:0|pos:1-0|q:%23%E8%B5%B5%E9%9B%A8%E6%80%9D%E7%94%B3%E8%AF%B7%E5%85%A5%E5%AD%A6%E8%B5%84%E6%96%99%E4%BD%9C%E5%81%87%E8%A2%AB%E9%80%80%E5%AD%A6%23|ext:cate:31,mpos:1,click:like,mid:4369416319879046,act:add"><i class="icon-act icon-act-praise"></i> <em>779</em></a></li>
            </ul>
        </div>
        <div node-type="feed_list_repeat"></div>
            </div>
		 */
		for(WebElement we :wes){
			String url = "";
			MyVideoObject newObj = new MyVideoObject();
			newObj.url = videoObj.url;
			newObj.videoUrl = url;
			newObj.title = videoObj.title;
			newObj.groupUid = videoObj.uid;
			insertVideo(newObj);
		}
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
		
		logonYoutube(driver);
		
		logonWeibo(driver);
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
