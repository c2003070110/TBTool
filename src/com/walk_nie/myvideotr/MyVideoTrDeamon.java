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
			videoObj.videoUrl = videoObj.url;
			wes1 = we.findElements(By.cssSelector("div[node-type=\"feed_list_content\"]"));
			if(!wes1.isEmpty()){
				videoObj.title = wes1.get(0).getText();
			}
			
//			Actions actions = new Actions(driver);
//			//actions.contextClick(target)
//			actions.doubleClick(wes1.get(0)).perform();
//			actions.doubleClick(wes1.get(0)).perform();
			
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
/*
<div id="active-uploads-contain" class="upload-active-widget"><div class="upload-item yt-card branded-page-box-padding accordion-expanded upload-item-finished" id="upload-item-0">
    <div class="upload-failure hid" tabindex="0"></div>

    <div class="upload-item-sidebar">
        <div class="upload-thumb upload-cursor-pointer">
    <div class="upload-thumb-container">
        <span class="video-thumb  yt-thumb yt-thumb-194">
    <span class="yt-thumb-default">
      <span class="yt-thumb-clip">
        
  <img data-ytimg="1" class="upload-thumb-img" alt="" onload=";window.__ytRIL &amp;&amp; __ytRIL(this)" src="https://www.youtube.com/upload_thumbnail?v=A7nY0gHIR_Q&amp;t=hqdefault&amp;ts=1557418823459" width="194">

        <span class="vertical-align"></span>
      </span>
    </span>
  </span>

    </div>

    <div class="upload-spinner-container hid" style="display: none;">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    </div>
  </div>

  <div class="upload-item-sidebar-text">
    <h3>アップロード ステータス</h3>

    <div class="upload-status-text">アップロードが完了しました。</div>
    <div class="watch-page-link">動画は次の URL で視聴できるようになります: <a target="_blank" href="https://youtu.be/A7nY0gHIR_Q">https://youtu.be/A7nY0gHIR_Q</a></div>


    <div class="notification-area">
        <div class="alert-multi hid">
    <h3>画質 / 音質:</h3>
    <div class="yt-alert yt-alert-naked yt-alert-warn  ">  <div class="yt-alert-icon">
    <span class="icon master-sprite yt-sprite"></span>
  </div>
<div class="yt-alert-content" role="alert">    <div class="yt-alert-message" tabindex="0">
            No message
    </div>
</div></div>

    <div>
        <div class="alert-action-buttons">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default multialert-action hid" type="button" onclick=";return false;"><span class="yt-uix-button-content">はい</span></button>
      <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-close multialert-close close hid" type="button" onclick=";return false;"><span class="yt-uix-button-content">閉じる</span></button>
  </div>

  <div class="alert-prevnext-buttons">
    <span class="multialert-message-counter"></span>
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default multialert-next" type="button" onclick=";return false;"><span class="yt-uix-button-content">&gt;</span></button>
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default multialert-prev" type="button" onclick=";return false;"><span class="yt-uix-button-content">&lt;</span></button>
  </div>

    </div>
  </div>

    </div>

  </div>

    </div>

    <div class="upload-item-main">
        <div class="upload-state-bar">
    <div class="metadata-actions">
      <div class="metadata-save-button" data-position="topright" data-click-outside-persists="true" data-orientation="vertical">
          <div class="save-cancel-buttons">

    <button class="yt-uix-button yt-uix-button-size-default save-changes-button yt-uix-tooltip yt-uix-button-primary" type="button" onclick=";return false;" title="この動画を今すぐ公開します" data-tooltip-text="この動画を今すぐ公開します" aria-labelledby="yt-uix-tooltip14-arialabel"><span class="yt-uix-button-content">公開</span></button>
  </div>

      </div>
    </div>

    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default return-to-editing-button" type="button" onclick=";return false;"><span class="yt-uix-button-content">編集に戻る</span></button>
        <div class="progress-bars">
      <div class="progress-bar-background"></div>
        <span class="progress-bar-text-upload-queued">
アップロード待ち...
        </span>

      <div class="inner-progress-bars" tabindex="0">
          <div class="progress-bar-uploading" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="100">
    <span class="progress-bar-text hid" style="display: none;"><span class="progress-bar-percentage">100%</span> アップロード済み
</span>
    <div class="progress-bar-progress" style="width: 100%;"></div>
  </div>

          <div class="progress-bar-processing" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="" aria-valuenow="100">
    <span class="progress-bar-text"><span class="progress-bar-percentage">100%</span> 処理済み
</span>
      <span class="progress-bar-text-done">処理が完了しました
</span>
    <div class="progress-bar-progress" style="width: 100%;"></div>
  </div>

        <span class="upload-time-remaining hid" style="display: none;"></span>
      </div>
        <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default yt-uix-button-empty yt-uix-button-has-icon yt-uix-tooltip item-cancel yt-uix-tooltip" type="button" onclick=";return false;" alt="アップロードをキャンセル" title="アップロードをキャンセル" style="visibility: visible;"><span class="yt-uix-button-icon-wrapper"><span class="yt-uix-button-icon yt-uix-button-icon-upload-cancel yt-sprite"></span></span></button>
    </div>

    <div class="item-title upload-cursor-pointer">2</div>
    <div class="save-error-message">ドラフトが保存されました。</div>
    <div class="yt-alert yt-alert-naked yt-alert-info  upload-item-alert" style="">  <div class="yt-alert-icon">
    <span class="icon master-sprite yt-sprite"></span>
  </div>
<div class="yt-alert-content" role="alert">    <div class="yt-alert-message" tabindex="0">[公開] をクリックして、動画を公開してください。</div>
</div></div>
    <div class="yt-alert yt-alert-naked yt-alert-success  upload-complete-alert" style="display: none;">  <div class="yt-alert-icon">
    <span class="icon master-sprite yt-sprite"></span>
  </div>
<div class="yt-alert-content" role="alert">    <div class="yt-alert-message" tabindex="0">
            No message
    </div>
</div></div>
    <div class="yt-alert yt-alert-naked yt-alert-error hid upload-monetization-errors-alert" style="display: none;">  <div class="yt-alert-icon">
    <span class="icon master-sprite yt-sprite"></span>
  </div>
<div class="yt-alert-content" role="alert">    <div class="yt-alert-message" tabindex="0">
            広告を使って動画から収益を上げるには、情報を追加する必要があります。
    </div>
</div></div>
    <div class="upload-share-panel"></div>
  </div>


      <div style="overflow: hidden;"><div class="sub-item-exp-zippy">
          <div class="sub-item-exp">
    <div data-metadata-editor="" class="metadata-editor-container">
              <script id="adformats.html" type="text/ng-template">
    
    <div class="display-ads ad-option-card narrow">
      <img src="/yts/img/videomanager/adoptions/thumbnails/xhdpi/display-ads-vflqgfIf0.png" alt="?"><br>
      <label>
        <span class="ad-option-description">
ディスプレイ広告
        </span>
      </label>
      <span>
パソコンのみ（必須）
      </span>
    </div>

  <div class="overlay-ads ad-option-card narrow" data-ng-show="formats.can_enable_overlay_ads">
    <img src="/yts/img/videomanager/adoptions/thumbnails/xhdpi/overlay-ads-vflzYcppi.png" data-ng-show="formats.has_overlay_ads" alt="?">
    <img src="/yts/img/videomanager/adoptions/thumbnails/xhdpi/disabled-overlay-ads-vflym4j98.png" data-ng-hide="formats.has_overlay_ads" alt="?">
    <label>
      <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="formats.has_overlay_ads"><span class="yt-uix-form-input-checkbox-element"></span></span>
      <span class="ad-option-description">
オーバーレイ広告
      </span>
    </label>
    <span>
パソコンのみ
    </span>
  </div>

  <div class="product-listing-ads ad-option-card narrow" data-ng-show="formats.can_enable_product_listing_ads">
    <img src="/yts/img/videomanager/adoptions/thumbnails/xhdpi/product-listing-ads-vflAUsJZl.png" data-ng-show="formats.has_product_listing_ads" alt="?">
    <img src="/yts/img/videomanager/adoptions/thumbnails/xhdpi/disabled-product-listing-ads-vflMcCrCx.png" data-ng-hide="formats.has_product_listing_ads" alt="?">
    <label>
      <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="formats.has_product_listing_ads"><span class="yt-uix-form-input-checkbox-element"></span></span>
      <span class="ad-option-description">
スポンサー カード
      </span>
    </label>
    <span>
すべてのデバイス
    </span>
  </div>

  <div class="skippable-ads ad-option-card narrow" data-ng-show="formats.can_enable_skippable_video_ads">
    <img src="/yts/img/videomanager/adoptions/thumbnails/xhdpi/skippable-ads-vfl6Rh3vA.png" data-ng-show="formats.has_skippable_video_ads" alt="?">
    <img src="/yts/img/videomanager/adoptions/thumbnails/xhdpi/disabled-skippable-ads-vflRW5X1U.png" data-ng-hide="formats.has_skippable_video_ads" alt="?">
    <label>
      <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="formats.has_skippable_video_ads"><span class="yt-uix-form-input-checkbox-element"></span></span>
      <span class="ad-option-description">
スキップ可能な動画広告
      </span>
    </label>
    <span>
すべてのデバイス
    </span>
  </div>

  <div class="non-skippable-ads ad-option-card narrow" data-ng-show="formats.can_enable_non_skippable_video_ads">
    <img src="/yts/img/videomanager/adoptions/thumbnails/xhdpi/non-skippable-ads-vflFR_6DG.png" data-ng-show="formats.has_non_skippable_video_ads" alt="?">
    <img src="/yts/img/videomanager/adoptions/thumbnails/xhdpi/disabled-non-skippable-ads-vflD_sSmN.png" data-ng-hide="formats.has_non_skippable_video_ads" alt="?">
    <label>
      <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="formats.has_non_skippable_video_ads"><span class="yt-uix-form-input-checkbox-element"></span></span>
      <span class="ad-option-description">
スキップ不可の動画広告
      </span>
    </label>
    <span>
すべてのデバイス
    </span>
  </div>

  </script>


      <script id="productplacements.html" type="text/ng-template">
      <div class="product_placement advanced-tab-option paid-product-placement-widget">
    <label>
      <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-click="onHasPppClick()" data-ng-model="product.has_paid_product_placement"><span class="yt-uix-form-input-checkbox-element"></span></span>
この動画には、有料プロモーション（制作のスポンサー支援、スポンサーから対価を得て製品やサービスを紹介したりすすめたりすることなど）が含まれています。
    </label>

      
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細" aria-describedby="-4510307206842045335">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-4510307206842045335">クリエーターは、自身のコンテンツに第三者のためのプロモーションを含めてそれにより対価を受け取ることができます（「有料プロモーション」）。すべての有料のプロモーションは、YouTube の広告ポリシーおよび適用される法律を遵守しなければなりません。
        <a target="_new" href="//support.google.com/youtube/?p=paid_product_placement&amp;hl=ja">
詳細
        </a>
      </span>
    </span>
  </span>



    <div data-ng-show="product.has_paid_product_placement">
      <label>
        <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="product.show_paid_product_placement_overlay"><span class="yt-uix-form-input-checkbox-element"></span></span>

この動画に有料プロモーションについての情報を追加して視聴者に開示します。適用される法律によって開示情報の追加を求められる場合があります。
      </label>

        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細" aria-describedby="-7937292768905987434">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-7937292768905987434">動画に「有料プロモーションを含みます」という情報開示テキストが表示されます。適用される法律によって、有料プロモーション コンテンツ（プロダクト プレースメントやおすすめ情報など）について音声またはテキストでさらに開示するよう求められる場合があります。
        <a target="_new" href="//support.google.com/youtube/?p=paid_product_placement&amp;hl=ja">
詳細
        </a>
      </span>
    </span>
  </span>


    </div>
  </div>

  </script>


      <script id="thirdparty.html" type="text/ng-template">
      <div class="third-party-ads-settings">
    <div class="third-party-ads-with-id">
      <label>
        <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="thirdParty.has_third_party_ads"><span class="yt-uix-form-input-checkbox-element"></span></span>
この動画での第三者広告を許可する
      </label>

        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="第三者広告の詳細" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="第三者広告の詳細" aria-describedby="5102222208627433172">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="5102222208627433172">チェックボックスをオンにすると、この動画でお使いの第三者広告システムが有効になります。動画 ID は第三者広告システムの ID（例: FreeWheel ID）とする必要があります。それがこの動画の第三者動画 ID となります。
      </span>
    </span>
  </span>


    </div>
    <div class="third-party-ads-video-id">
      <label>
動画 ID
      </label>
      <span class=" yt-uix-form-input-container yt-uix-form-input-text-container "><input class="yt-uix-form-input-text third-party-ads-video-id-input" data-ng-model="thirdParty.third_party_ads_video_id" data-ng-disabled="!thirdParty.has_third_party_ads"></span>
    </div>
  </div>

  </script>


      <script id="playhead.html" type="text/ng-template">
      <div
    data-ng-if="playheadCtrl.isSnapped()"
    data-ng-click="playheadCtrl.onAdMarkerSectionClick()"
    data-ng-dblclick="playheadCtrl.onAdMarkerSectionDoubleclick()"
    class="timeline-ad-marker-section">    <span data-ng-hide="playheadCtrl.isEditing()">{{playheadCtrl.getCurrentAdMarkerLabel()}}</span>    <input
      data-ng-show="playheadCtrl.isEditing()"
      data-ng-model="playheadCtrl.inputText"
      data-ng-keypress="playheadCtrl.onInputTextKeypress($event)"
      class="timeline-playhead-input"
      type="text">    <div class="timeline-remove-marker-btn" data-ng-click="playheadCtrl.onRemoveAdClick()">&times;</div>  </div>  <div class="timeline-playhead-line"></div>  <div class="timeline-playhead-thumb-container"></div>  <div class="timeline-playhead-time" data-ng-mousedown="playheadCtrl.onTimeboxMousedown()">0:00</div>
  </script>


      <script id="admarker.html" type="text/ng-template">
      <div class="ad-marker" data-ng-hide="adMarkerCtrl.isSelected()">
    <div class="ad-marker-box" data-ng-click="adMarkerCtrl.onAdMarkerClick()">{{adMarkerCtrl.getTimeLabel()}}</div>
    <div class="ad-marker-line"></div>
  </div>

  </script>


      <script id="bulk-insert-dialog.html" type="text/ng-template">
        <div class="yt-dialog hid bulk-insert-dialog" data-ng-class="adBreaksCtrl.getBulkInsertError() == bulkInsertErrorType.NONE ? &#39;&#39; : &#39;bulk-insert-error&#39;">
    <div class="yt-dialog-base">
      <span class="yt-dialog-align"></span>
      <div class="yt-dialog-fg" role="dialog">
        <div class="yt-dialog-fg-content">
            <div class="yt-dialog-header">
                  <h2 class="yt-dialog-title" role="alert">
      {{adBreaksCtrl.hasAdBreaksInTimeline() ? &#39;ミッドロール挿入点を一括編集する&#39; : &#39;ミッドロール挿入点を一括挿入する&#39;}}
  </h2>

            </div>
          <div class="yt-dialog-loading">
              <div class="yt-dialog-waiting-content">
      <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
読み込んでいます...
    </span>
  </p>

  </div>

          </div>
          <div class="yt-dialog-content">
              <span class="yt-uix-form-input-container yt-uix-form-input-textarea-container "><textarea class="yt-uix-form-input-textarea bulk-insert-text-area" name="bulk-insert" placeholder="例: 2:30、6:18" data-ng-change="adBreaksCtrl.onBulkDialogChange()" data-ng-list=", " data-ng-model="adBreaksCtrl.bulkInsertTimesModel" rows="6" aria-label="例: 2:30、6:18"></textarea></span>
  <label class="bulk-insert-time-format-error" data-ng-if="adBreaksCtrl.getBulkInsertError() == bulkInsertErrorType.TIME_FORMAT_ERROR">
ミッドロール挿入点のフォーマットを確認してください（例: 1:00、1:30）
  </label>
  <label class="bulk-insert-time-range-error" data-ng-if="adBreaksCtrl.getBulkInsertError() == bulkInsertErrorType.TIME_RANGE_ERROR">
すべての時間が許容範囲内（0:02～動画の中で広告を挿入できる最後の時間）にあるわけではありません{{adBreaksCtrl.getMaxAdBreakTimeLabel()}})
  </label>
  <div class="yt-uix-overlay-actions">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default cancel-bulk-insert" type="button" onclick=";return false;" data-ng-click="dialogCtrl.cancel()"><span class="yt-uix-button-content">キャンセル</span></button>
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-primary accept-bulk-insert" type="button" onclick=";return false;" data-ng-click="adBreaksCtrl.onBulkInsertAcceptClick(dialogCtrl, $event)"><span class="yt-uix-button-content">{{adBreaksCtrl.hasAdBreaksInTimeline() ? &#39;広告を編集&#39; : &#39;広告を挿入&#39;}}</span></button>
  </div>

          </div>
          <div class="yt-dialog-working">
              <div class="yt-dialog-working-overlay"></div>
  <div class="yt-dialog-working-bubble">
    <div class="yt-dialog-waiting-content">
        <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
        処理しています...
    </span>
  </p>

      </div>
  </div>

          </div>
        </div>
        <div class="yt-dialog-focus-trap" tabindex="0"></div>
      </div>
    </div>
  </div>


  </script>


      <script id="suggest-placements-dialog.html" type="text/ng-template">
        <div class="yt-dialog hid ">
    <div class="yt-dialog-base">
      <span class="yt-dialog-align"></span>
      <div class="yt-dialog-fg" role="dialog">
        <div class="yt-dialog-fg-content">
            <div class="yt-dialog-header">
                  <h2 class="yt-dialog-title" role="alert">
      既存のミッドロール挿入点を削除しますか？
  </h2>

            </div>
          <div class="yt-dialog-loading">
              <div class="yt-dialog-waiting-content">
      <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
読み込んでいます...
    </span>
  </p>

  </div>

          </div>
          <div class="yt-dialog-content">
              <p class="suggest-placements-text">
動画にミッドロール挿入点が既に設定されています。続行すると既存のミッドロール挿入点が削除され、新しいミッドロール挿入点が追加されます。
  </p>
  <div class="yt-uix-overlay-actions">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default cancel-suggestions" type="button" onclick=";return false;" data-ng-click="dialogCtrl.cancel()"><span class="yt-uix-button-content">キャンセル</span></button>
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-primary accept-suggestions" type="button" onclick=";return false;" data-ng-click="dialogCtrl.accept()"><span class="yt-uix-button-content">はい、続行します</span></button>
  </div>

          </div>
          <div class="yt-dialog-working">
              <div class="yt-dialog-working-overlay"></div>
  <div class="yt-dialog-working-bubble">
    <div class="yt-dialog-waiting-content">
        <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
        処理しています...
    </span>
  </p>

      </div>
  </div>

          </div>
        </div>
        <div class="yt-dialog-focus-trap" tabindex="0"></div>
      </div>
    </div>
  </div>


  </script>


      <script id="v2-adbreaks.html" type="text/ng-template">
      <div class="ad-breaks-controls">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-primary ad-break-insert" type="button" onclick=";return false;" data-ng-class="adBreaksCtrl.isPreviewEnabled() ? &#39;yt-uix-button-default&#39; : &#39;yt-uix-button-primary&#39;" data-ng-click="adBreaksCtrl.onInsertAdClick()" data-ng-disabled="adBreaksCtrl.isPreviewEnabled() || !adBreaksCtrl.isInitialized()"><span class="yt-uix-button-content">広告を挿入</span></button>
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default ad-break-preview" type="button" onclick=";return false;" data-ng-class="adBreaksCtrl.isPreviewEnabled() &amp;&amp; !adBreaksCtrl.isPreloading() ? &#39;yt-uix-button-primary&#39; : &#39;yt-uix-button-default&#39;" data-ng-click="adBreaksCtrl.onPreviewAdClick()" data-ng-disabled="adBreaksCtrl.isPreloading() || !adBreaksCtrl.isPreviewEnabled() || !adBreaksCtrl.isInitialized()"><span class="yt-uix-button-content">プレビュー</span></button>
  </div>
  <div class="ad-breaks-bar" data-ng-show="adBreaksCtrl.isInitialized()">
    <div class="ad-breaks-player" data-ng-hide="adBreaksCtrl.isPreloading() || adBreaksCtrl.isPreviewingAd()"></div>
    <div class="ad-breaks-play-icon" data-ng-class="adBreaksCtrl.isPreloading() || adBreaksCtrl.isPreviewEnabled() || adBreaksCtrl.isPreviewingAd() || adBreaksCtrl.isPlayingVideo() ? 'hide-play-icon' : ''"></div>
    <div class="ad-breaks-preload" data-ng-show="adBreaksCtrl.isPreloading()"></div>
    <div class="ad-breaks-preview" data-ng-show="adBreaksCtrl.isPreviewingAd()">
      <span>ミッドロール挿入点</span>
    </div>
    <div class="ad-breaks-checkbox preroll">
      <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="adBreaksCtrl.adBreaksModel.has_preroll"><span class="yt-uix-form-input-checkbox-element"></span></span>
      <span>動画の前</span>
    </div>
      <div class="ad-breaks-timeline">    <div class="tetris-timeline">      <div class="timeline-content">        <div class="timeline-stack-track"></div>      </div>      <div class="timeline-overlay" data-ng-click="adBreaksCtrl.onTimelineOverlayClick($event)">        <div class="timeline-pause-track"></div>          <div class="timeline-ruler"></div>

        <div data-playhead
          class="timeline-playhead"
          tabindex="0"
          data-ng-class="playheadCtrl.getStatus()"
          data-ng-click="adBreaksCtrl.onPlayheadClick($event)"
          data-ng-mousedown="adBreaksCtrl.onPlayheadMousedown()"
          data-ng-keyup="playheadCtrl.onPlayheadKeyup($event)"></div>        <div class="timeline-overlay-center">          <span class="midroll-instruction" data-ng-hide="adBreaksCtrl.hasAdBreaksInTimeline()">            青いバーをドラッグし、[広告を挿入] をクリックします          </span>        </div>      </div>    </div>  </div>
    <div class="ad-breaks-checkbox postroll">
      <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="adBreaksCtrl.adBreaksModel.has_postroll"><span class="yt-uix-form-input-checkbox-element"></span></span>
      <span>動画の後</span>
    </div>
  </div>
  <div class="ad-breaks-loading" data-ng-hide="adBreaksCtrl.isInitialized()">
      <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
        ミッドロール挿入エディタを読み込んでいます
    </span>
  </p>

  </div>
  <div class="ad-breaks-additional-controls">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default ad-break-bulk-insert" type="button" onclick=";return false;" data-ng-click="adBreaksCtrl.onBulkInsertClick()" data-ng-disabled="!adBreaksCtrl.isInitialized()"><span class="yt-uix-button-content">{{adBreaksCtrl.hasAdBreaksInTimeline() ? &#39;ミッドロール挿入点を一括編集する&#39; : &#39;ミッドロール挿入点を一括挿入する&#39;}}</span></button>
      <div class="yt-uix-hovercard">
    <span class="yt-uix-hovercard-target" data-position="topright" data-orientation="vertical">
      <div class="suggest-hover-container">
        <div data-ng-if="!adBreaksCtrl.hasSuggestedPlacements()" class="suggest-hover-overlay"></div>
        <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default ad-break-suggest" type="button" onclick=";return false;" data-ng-click="adBreaksCtrl.onSuggestClick()" data-ng-disabled="!adBreaksCtrl.hasSuggestedPlacements()"><span class="yt-uix-button-content">プレースメントを提案</span></button>
      </div>
    </span>
    <div class="yt-uix-hovercard-content">
      <div data-ng-if="adBreaksCtrl.getSuggestTooltipType() == suggestTooltipType.DEFAULT">
        <p>YouTube は、動画のコンテンツに基づいて広告の配置を提案します。</p>
        <p>提案機能を使用するには、動画を公開してインストリーム広告を有効にする必要があります。</p>
      </div>
      <div data-ng-if="adBreaksCtrl.getSuggestTooltipType() == suggestTooltipType.AUTO_BREAK_EMPTY">
        <p>動画のコンテンツに基づいて、広告の配置に適した場所を検出することができませんでした。</p>
        <p>[挿入] をクリックして手動で動画に広告を配置してください。</p>
      </div>
      <div data-ng-if="adBreaksCtrl.getSuggestTooltipType() == suggestTooltipType.AUTO_BREAK_PROCESSING">
        <p>YouTube は、動画のコンテンツに基づいて広告の配置を提案します。</p>
        <p>このオプションが表示されるまで、動画がアップロードされてから最大で 2 時間かかることがあります。</p>
      </div>
    </div>  </div>
    <div class="suggest-placements-enabled" data-ng-if="!adBreaksCtrl.isManualMidrollEnabled()">
      現在、おすすめのミッドロール挿入点が使用されています。</span>
    </div>
  </div>

  </script>



        <script id="trailer.html" type="text/ng-template">
      <div data-ng-if="ctrl.errorText" class="trailer-error-text">
    {{ctrl.errorText}}
  </div>

  <div data-ng-show="ctrl.loadingTrailer">
      <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
読み込んでいます...
    </span>
  </p>

  </div>

  <div data-ng-show="!ctrl.loadingTrailer">
      <div data-ng-show="ctrl.trailerEncVideoId" class="trailer-video-lockup">
    
  </div>


    <div class="trailer-change-buttons">
      <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default trailer-select-button" type="button" onclick=";return false;" data-ng-show="!ctrl.trailerEncVideoId" data-ng-click="ctrl.selectOrChangeTrailer()"><span class="yt-uix-button-content">予告編を選択</span></button>

      <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default trailer-change-button" type="button" onclick=";return false;" data-ng-show="ctrl.trailerEncVideoId" data-ng-click="ctrl.selectOrChangeTrailer()"><span class="yt-uix-button-content">変更</span></button>

      <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default trailer-remove-button" type="button" onclick=";return false;" data-ng-show="ctrl.trailerEncVideoId" data-ng-click="ctrl.removeTrailer()"><span class="yt-uix-button-content">削除</span></button>
    </div>
  </div>

      <div class="yt-video-picker trailer-video-picker" data-show-button-id=""  data-hide-dialog-after-save="True">
    <input type="hidden" name="video_url" class="yt-video-picker-url-hidden-input">
    <input type="hidden" name="video_item_id" class="yt-video-picker-item-id-hidden-input">
    <input type="hidden" name="playlist_item_id" class="yt-video-picker-playlist-item-id-hidden-input">
    <div class="yt-video-picker-error-template">
      <!--
        <div class="yt-alert yt-alert-default yt-alert-error  ">  <div class="yt-alert-icon">
    <span class="icon master-sprite yt-sprite"></span>
  </div>
<div class="yt-alert-content" role="alert">    <div class="yt-alert-message" tabindex="0">
            __error__
    </div>
</div><div class="yt-alert-buttons"><button class="yt-uix-button yt-uix-button-size-default yt-uix-button-close close yt-uix-close" type="button" onclick=";return false;" aria-label="閉じる" data-close-parent-class="yt-alert"><span class="yt-uix-button-content">閉じる</span></button></div></div>
        -->
    </div>
    <div class="yt-video-picker-dialog-template">
      <!--
        <form action="" method="GET" class="yt-video-picker-form trailer-video-picker-form">
            <div class="yt-dialog hid ">
    <div class="yt-dialog-base">
      <span class="yt-dialog-align"></span>
      <div class="yt-dialog-fg" role="dialog">
        <div class="yt-dialog-fg-content">
            <div class="yt-dialog-header">
                  <h2 class="yt-dialog-title" role="alert">
      予告編の選択
  </h2>

            </div>
          <div class="yt-dialog-loading">
              <div class="yt-dialog-waiting-content">
      <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
読み込んでいます...
    </span>
  </p>

  </div>

          </div>
          <div class="yt-dialog-content">
              <div class="yt-video-picker-modal">
    <div class="yt-video-picker-errors">
    </div>
    <input type="hidden" name="show_warnings" class="yt-video-picker-show-warnings" value="False">




    <h3>
      
    </h3>
      <button onclick=";return false;" class="yt-video-picker-sort-options flip yt-uix-button yt-uix-button-default yt-uix-button-size-default" type="button" aria-expanded="false" aria-haspopup="true" data-button-menu-indicate-selected="true" data-button-has-sibling-menu="true"><span class="yt-uix-button-content">新しい順</span><span class="yt-uix-button-arrow yt-sprite"></span><div class=" yt-uix-button-menu yt-uix-button-menu-default hid">  <ul>
        <li class="yt-uix-load-more " data-uix-load-more-href="/video_picker_ajax?action_get_public_user_videos=1&amp;grid_css_id=trailer-video-picker-grid&amp;ar=2&amp;sort=most-viewed" data-uix-load-more-target-id="trailer-video-picker-grid" data-uix-load-more-replace-content="True">
    <span class="yt-uix-button-menu-item">
      再生回数順
    </span>
  </li>


        <li class="yt-uix-load-more yt-uix-button-menu-item-selected" data-uix-load-more-href="/video_picker_ajax?action_get_public_user_videos=1&amp;grid_css_id=trailer-video-picker-grid&amp;ar=2&amp;sort=newest" data-uix-load-more-target-id="trailer-video-picker-grid" data-uix-load-more-replace-content="True">
    <span class="yt-uix-button-menu-item">
      新しい順
    </span>
  </li>


        <li class="yt-uix-load-more " data-uix-load-more-href="/video_picker_ajax?action_get_public_user_videos=1&amp;grid_css_id=trailer-video-picker-grid&amp;ar=2&amp;sort=shortest" data-uix-load-more-target-id="trailer-video-picker-grid" data-uix-load-more-replace-content="True">
    <span class="yt-uix-button-menu-item">
      2 分未満の動画
    </span>
  </li>


  </ul>
</div></button>

    <div class="yt-video-picker-videos-container">
      <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-epic-nav-item yt-video-picker-label yt-video-picker-videos-label selected" type="button" onclick=";return false;"><span class="yt-uix-button-content">アップロード済み</span></button>
      <div class="yt-video-picker-scroll-container">
        <ul class="yt-video-picker-grid yt-video-picker-videos clearfix"  id="trailer-video-picker-grid">
          
        </ul>
      </div>
    </div>
    <span class="yt-video-picker-url-container yt-uix-form-input-container yt-uix-form-input-text-container  yt-uix-form-input-fluid-container"><span class=" yt-uix-form-input-fluid"><input class="yt-uix-form-input-text yt-video-picker-url" name="video_url" placeholder="または YouTube 動画の URL を入力"></span></span>
  </div>

          </div>
          <div class="yt-dialog-working">
              <div class="yt-dialog-working-overlay"></div>
  <div class="yt-dialog-working-bubble">
    <div class="yt-dialog-waiting-content">
        <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
        処理しています...
    </span>
  </p>

      </div>
  </div>

          </div>
<div class="yt-dialog-footer">  <div class="yt-uix-overlay-actions">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default   yt-dialog-dismiss" type="button" onclick=";return false;" data-action="cancel"><span class="yt-uix-button-content">キャンセル</span></button>
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-primary" type="submit" onclick=";return true;"><span class="yt-uix-button-content">保存</span></button>
  </div>
</div>        </div>
        <div class="yt-dialog-focus-trap" tabindex="0"></div>
      </div>
    </div>
  </div>

        </form>
      -->
    </div>
  </div>



  </script>


      <script id="category.html" type="text/ng-template">
      <span class="yt-uix-form-input-select "><span class="yt-uix-form-input-select-content"><span class="yt-uix-form-input-select-arrow yt-sprite"></span><span class="yt-uix-form-input-select-value"></span></span><select class="yt-uix-form-input-select-element " data-ng-options="value as label for (value, label) in options" data-ng-model="category"></select></span>

  </script>


      <script id="game_title.html" type="text/ng-template">
      <span class=" yt-uix-form-input-container yt-uix-form-input-text-container "><input class="yt-uix-form-input-text game-title-widget" placeholder="ゲームタイトル（推奨）
" maxlength="100" data-ng-change="ctrl.clearMid()" data-jfk-on-dismiss="ctrl.onBlur()" data-jfk-on-update="ctrl.addTitleInfo($event)" data-ng-model="gameTitleInfo.title" data-url="&#39;/game_suggestions_ajax?action_get_game_suggestions=1&#39;" data-jfk-autocomplete="" data-custom-renderer="ctrl.autoCompleteCustomRenderer"></span>

  </script>



      <div class="subnav clearfix">
          <ul class="tabs" role="tablist">
        <li class="tab-header epic-nav-item selected" role="tab" data-tab-id="basic-info">
    <div class="tab-header-title">
      <a href="">
        基本情報
        <sub>
        </sub>
      </a>
    </div>
    <div class="tab-header-icon-container">
      <span class="tab-header-icon yt-sprite"></span>
    </div>
  </li>


          <li class="tab-header epic-nav-item" role="tab" data-tab-id="translations">
    <div class="tab-header-title">
      <a href="">
        翻訳
        <sub>
        </sub>
      </a>
    </div>
    <div class="tab-header-icon-container">
      <span class="tab-header-icon yt-sprite"></span>
    </div>
  </li>


      
          <li class="tab-header epic-nav-item" role="tab" data-tab-id="advanced-settings" id="advanced-settings">
    <div class="tab-header-title">
      <a href="">
        詳細設定
        <sub>
        </sub>
      </a>
    </div>
    <div class="tab-header-icon-container">
      <span class="tab-header-icon yt-sprite"></span>
    </div>
  </li>


    
  </ul>

        <div class="metadata-editor-it-container"></div>
      </div>

        <div class="metadata-popups">
     <div class="location-map" style="display: none;">
       <div class="map-canvas"></div>
     </div>
  </div>

        <div class="metadata-container" role="tabpanel">
    <form name="mdeform" class="video-settings-form ng-valid ng-valid-parse ng-valid-maxlength ng-pristine" action="/metadata_ajax">
        <div class="metadata-tab basic-info-tab" data-tab-id="basic-info" role="tabpanel" style="">
    <fieldset class="metadata-two-column">
      <div ng-non-bindable="">
          <label class="basic-info-form-input">
      <span class="yt-uix-form-input-container yt-uix-form-input-text-container yt-uix-form-input-non-empty"><input class="yt-uix-form-input-text video-settings-title" name="title" value="" placeholder="タイトル" data-initial-value="" data-ita-enable="true"></span>

  </label>

  <label class="basic-info-form-input">
      <span class="yt-uix-form-input-container yt-uix-form-input-textarea-container "><textarea class="yt-uix-form-input-textarea video-settings-description" name="description" placeholder="説明" data-initial-value="" data-ita-enable="true" rows="6" aria-label="説明"></textarea></span>

  </label>

  <div class="basic-info-form-input">
    <span class="yt-uix-form-input-container yt-uix-form-input-textarea-container">
      <div class="video-settings-tag-chips-container yt-uix-form-input-textarea" role="list" aria-label="追加したタグ">
        <input type="hidden" name="keywords" class="video-settings-tags" value="" data-initial-value="">
        <span class="yt-uix-form-input-placeholder-container">
          <input class="video-settings-add-tag" spellcheck="false" autocomplete="false" aria-haspopup="true" placeholder="タグ（例: アルバート・アインシュタイン、空飛ぶ豚、マッシュアップ）" data-ita-enable="true" aria-label="タグがあると、動画とチャンネルを見つけてもらいやすくなります。タグを追加しますか？">
           <span class="yt-uix-form-input-placeholder">
             タグ（例: アルバート・アインシュタイン、空飛ぶ豚、マッシュアップ）
           </span>
        </span>
      <input class="yt-focusable-invisible-input" aria-hidden="true"></div>
    </span>
  </div>

      </div>
    </fieldset>
    <fieldset class="metadata-column" data-ng-non-bindable="">
      
  <div aria-live="polite" class="metadata-field metadata-privacy-field">

        <div class="live-premiere-info-container">
    <img class="live-premiere-info-banner" src="/yts/img/upload/premiere_promo-vflfX7MnK.png" alt="">
    <p class="live-premiere-info-title">
New! プレミア公開機能
    </p>
    <p class="live-premiere-info-description">
動画の配信を一大イベントに。新しい動画に対するファンの期待を盛り上げましょう。
    </p>
    <ul class="live-premiere-info-description live-premiere-info-description-list">
      <li>プレミア公開のスケジュール設定</li>
      <li>動画再生ページの URL をファンと共有しましょう</li>
      <li>プレミア公開前や公開中に視聴者とチャット</li>
      <li>視聴者と一緒にリアルタイムでプレミア公開を視聴</li>
    </ul>
    <p class="live-premiere-info-description">
      <a href="//support.google.com/youtube/answer/9080341?hl=ja" target="_blank">詳細</a>
    </p>
  </div>
  <div class="live-premiere-controls">
    <label>
プレミア公開
        <span class="yt-uix-checkbox-on-off ">
<input id="live-premiere-toggle" class="live-premiere-toggle-input mde-checkbox" type="checkbox" name="live_premiere"><label for="live-premiere-toggle" id="live-premiere-toggle-label"><span class="checked"></span><span class="toggle"></span><span class="unchecked"></span></label>  </span>

    </label>
    <span class="live-premiere-clickcard-private hid" style="display: none;">
        <span class="yt-uix-clickcard">
      <button class="yt-uix-clickcard-target" aria-label="詳細を確認" aria-haspopup="true" data-position="topleft">
        <span class="yt-help-icon yt-uix-clickcard-target yt-sprite"></span>
      </button>
<span class="yt-uix-clickcard-content"><button class="yt-uix-clickcard-close" aria-label="ヘルプテキストを閉じる"></button><span>非公開の動画には、プレミア公開機能をご利用いただけません</span></span>  </span>

    </span>
    <span class="live-premiere-clickcard-unlisted hid" style="display: none;">
        <span class="yt-uix-clickcard">
      <button class="yt-uix-clickcard-target" aria-label="詳細を確認" aria-haspopup="true" data-position="topleft">
        <span class="yt-help-icon yt-uix-clickcard-target yt-sprite"></span>
      </button>
<span class="yt-uix-clickcard-content"><button class="yt-uix-clickcard-close" aria-label="ヘルプテキストを閉じる"></button><span>限定公開の動画には、プレミア公開機能をご利用いただけません</span></span>  </span>

    </span>
  </div>


      <span class="yt-uix-form-input-select privacy-select"><span class="yt-uix-form-input-select-content"><span class="yt-uix-form-input-select-arrow yt-sprite"></span><span class="yt-uix-form-input-select-value">
    公開

  </span></span><select class="yt-uix-form-input-select-element metadata-privacy-input" name="privacy" data-initial-value="public">
        <option value="public" selected="true">
    公開

  </option>


        <option value="unlisted">
    限定公開

  </option>


        <option value="private">
    非公開

  </option>


        <option value="scheduled">
    スケジュール設定済み

  </option>

</select></span>



      <div class="metadata-privacy-settings metadata-private-settings metadata-share-render clearfix hid" style="display: none;">
            <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default metadata-share-button" type="button" onclick=";return false;"><span class="yt-uix-button-content">共有</span></button>
  <div class="metadata-share-text">
            本人のみ視聴可能

</div>

      </div>
      <div class="metadata-private-sharing-dialog">
          <div class="yt-dialog hid metadata-sharing-dialog">
    <div class="yt-dialog-base">
      <span class="yt-dialog-align"></span>
      <div class="yt-dialog-fg" role="dialog" tabindex="-1">
        <div class="yt-dialog-fg-content yt-dialog-show-content">
          <div class="yt-dialog-loading">
              <div class="yt-dialog-waiting-content">
      <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
読み込んでいます...
    </span>
  </p>

  </div>

          </div>
          <div class="yt-dialog-content">
              <div class="sharing-dialog-top-dog-container">
    <div class="metadata-sharing-dialog-content">
      <div class="sharing-dialog-regular-content">
        <div class="sharing-dialog-content-container">
          <div class="sharing-dialog-top-section">
              <div class="sharing-dialog-section extra-section-spacing">他のユーザーと共有</div>
          </div>
        </div>
        <div class="sharing-dialog-invalid-email">「<span class="invalid-email-text"></span>」は有効なメールではありません</div>
        <span class="yt-uix-form-input-container yt-uix-form-input-textarea-container "><textarea class="yt-uix-form-input-textarea metadata-share-contacts" placeholder="メールアドレスを入力してください" rows="6"></textarea></span>
        <label>
          <span class="yt-uix-form-input-checkbox-container  checked"><input type="checkbox" class="yt-uix-form-input-checkbox notify-via-email" name="notify_via_email" checked="checked" value="true"><span class="yt-uix-form-input-checkbox-element"></span></span>
          <span class="notify-via-email-text">メールで通知する</span>
        </label>
      </div>

      <div class="sharing-dialog-server-error-content">
        <div class="error-from-server-title"></div>
        <div class="error-from-server-text"></div>
      </div>
    </div>
    <div class="sharing-dialog-footer clearfix">
      <div class="sharing-dialog-regular-buttons">
        <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-primary sharing-dialog-button sharing-dialog-ok" type="button" onclick=";return false;" disabled="True"><span class="yt-uix-button-content">OK</span></button>
        <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default   yt-dialog-dismiss yt-dialog-cancel sharing-dialog-button sharing-dialog-cancel" type="button" onclick=";return false;" data-action="cancel"><span class="yt-uix-button-content">キャンセル</span></button>
      </div>
      <div class="sharing-dialog-error-buttons">
        <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-primary sharing-dialog-button sharing-dialog-back" type="button" onclick=";return false;"><span class="yt-uix-button-content">戻る</span></button>
      </div>
    </div>
    <input type="hidden" name="share_emails" class="share-emails">
    <input type="hidden" name="deleted_ogids" class="delete-share-ogids">
    <input type="hidden" name="deleted_circle_ids" class="delete-share-circle-ids">
    <input type="hidden" name="deleted_emails" class="delete-share-emails">
    <input type="hidden" name="dasher_change" class="dasher-change" disabled="">
  </div>

          </div>
          <div class="yt-dialog-working">
              <div class="yt-dialog-working-overlay"></div>
  <div class="yt-dialog-working-bubble">
    <div class="yt-dialog-waiting-content">
        <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
        処理しています...
    </span>
  </p>

      </div>
  </div>

          </div>
        </div>
        <div class="yt-dialog-focus-trap" tabindex="0"></div>
      </div>
    </div>
  </div>

      </div>


    <input type="hidden" name="playlists_values" class="playlists-values hid" value="InvalidValue">
    <input type="hidden" name="privacy_draft" class="privacy_draft" value="public">

      <div class="metadata-privacy-settings metadata-scheduled-settings hid" style="display: none;">
        <div class="delayed-publishing-controls hid" style="display: none;">
            <div class="publish-date-info hid" style="display: none;">
    <span class=" yt-uix-form-input-container yt-uix-form-input-text-container "><input class="yt-uix-form-input-text publish-date-formatted" placeholder=""></span>
    <span class="yt-uix-form-input-select"><span class="yt-uix-form-input-select-content"><span class="yt-uix-form-input-select-arrow yt-sprite"></span><span class="yt-uix-form-input-select-value">1:00</span></span><select class="yt-uix-form-input-select-element publish-time-formatted">

    <option value="0">0:00</option>
    <option value="15">0:15</option>
    <option value="30">0:30</option>
    <option value="45">0:45</option>
    <option value="60">1:00</option>
    <option value="75">1:15</option>
    <option value="90">1:30</option>
    <option value="105">1:45</option>
    <option value="120">2:00</option>
    <option value="135">2:15</option>
    <option value="150">2:30</option>
    <option value="165">2:45</option>
    <option value="180">3:00</option>
    <option value="195">3:15</option>
    <option value="210">3:30</option>
    <option value="225">3:45</option>
    <option value="240">4:00</option>
    <option value="255">4:15</option>
    <option value="270">4:30</option>
    <option value="285">4:45</option>
    <option value="300">5:00</option>
    <option value="315">5:15</option>
    <option value="330">5:30</option>
    <option value="345">5:45</option>
    <option value="360">6:00</option>
    <option value="375">6:15</option>
    <option value="390">6:30</option>
    <option value="405">6:45</option>
    <option value="420">7:00</option>
    <option value="435">7:15</option>
    <option value="450">7:30</option>
    <option value="465">7:45</option>
    <option value="480">8:00</option>
    <option value="495">8:15</option>
    <option value="510">8:30</option>
    <option value="525">8:45</option>
    <option value="540">9:00</option>
    <option value="555">9:15</option>
    <option value="570">9:30</option>
    <option value="585">9:45</option>
    <option value="600">10:00</option>
    <option value="615">10:15</option>
    <option value="630">10:30</option>
    <option value="645">10:45</option>
    <option value="660">11:00</option>
    <option value="675">11:15</option>
    <option value="690">11:30</option>
    <option value="705">11:45</option>
    <option value="720" selected="">12:00</option>
    <option value="735">12:15</option>
    <option value="750">12:30</option>
    <option value="765">12:45</option>
    <option value="780">13:00</option>
    <option value="795">13:15</option>
    <option value="810">13:30</option>
    <option value="825">13:45</option>
    <option value="840">14:00</option>
    <option value="855">14:15</option>
    <option value="870">14:30</option>
    <option value="885">14:45</option>
    <option value="900">15:00</option>
    <option value="915">15:15</option>
    <option value="930">15:30</option>
    <option value="945">15:45</option>
    <option value="960">16:00</option>
    <option value="975">16:15</option>
    <option value="990">16:30</option>
    <option value="1005">16:45</option>
    <option value="1020">17:00</option>
    <option value="1035">17:15</option>
    <option value="1050">17:30</option>
    <option value="1065">17:45</option>
    <option value="1080">18:00</option>
    <option value="1095">18:15</option>
    <option value="1110">18:30</option>
    <option value="1125">18:45</option>
    <option value="1140">19:00</option>
    <option value="1155">19:15</option>
    <option value="1170">19:30</option>
    <option value="1185">19:45</option>
    <option value="1200">20:00</option>
    <option value="1215">20:15</option>
    <option value="1230">20:30</option>
    <option value="1245">20:45</option>
    <option value="1260">21:00</option>
    <option value="1275">21:15</option>
    <option value="1290">21:30</option>
    <option value="1305">21:45</option>
    <option value="1320">22:00</option>
    <option value="1335">22:15</option>
    <option value="1350">22:30</option>
    <option value="1365">22:45</option>
    <option value="1380">23:00</option>
    <option value="1395">23:15</option>
    <option value="1410">23:30</option>
    <option value="1425">23:45</option>
</select></span>
      <span class="yt-uix-form-input-select"><span class="yt-uix-form-input-select-content"><span class="yt-uix-form-input-select-arrow yt-sprite"></span><span class="yt-uix-form-input-select-value">(GMT+0900) 現地時間</span></span><select class="yt-uix-form-input-select-element publish-timezone-formatted">  <option value="Etc/Unknown" class="publish-local-timezone" selected="">(GMT+0900) 現地時間</option>
    <option value="Pacific/Honolulu">(GMT-1000) ホノルル</option>
    <option value="America/Anchorage">(GMT-0800) アンカレッジ</option>
    <option value="America/Juneau">(GMT-0800) ジュノー</option>
    <option value="America/Hermosillo">(GMT-0700) エルモシヨ</option>
    <option value="America/Tijuana">(GMT-0700) ティフアナ</option>
    <option value="America/Vancouver">(GMT-0700) バンクーバー</option>
    <option value="America/Phoenix">(GMT-0700) フェニックス</option>
    <option value="America/Los_Angeles">(GMT-0700) ロサンゼルス</option>
    <option value="Pacific/Easter">(GMT-0600) イースター島</option>
    <option value="America/Edmonton">(GMT-0600) エドモントン</option>
    <option value="America/Denver">(GMT-0600) デンバー</option>
    <option value="America/Winnipeg">(GMT-0500) ウィニペグ</option>
    <option value="America/Chicago">(GMT-0500) シカゴ</option>
    <option value="America/Bogota">(GMT-0500) ボゴタ</option>
    <option value="America/Mexico_City">(GMT-0500) メキシコシティー</option>
    <option value="America/Rio_Branco">(GMT-0500) リオブランコ</option>
    <option value="America/Montreal">(GMT-0400) Montreal</option>
    <option value="America/Santiago">(GMT-0400) サンチアゴ</option>
    <option value="America/Detroit">(GMT-0400) デトロイト</option>
    <option value="America/Toronto">(GMT-0400) トロント</option>
    <option value="America/New_York">(GMT-0400) ニューヨーク</option>
    <option value="America/Manaus">(GMT-0400) マナウス</option>
    <option value="America/Sao_Paulo">(GMT-0300) サンパウロ</option>
    <option value="America/Halifax">(GMT-0300) ハリファクス</option>
    <option value="America/Bahia">(GMT-0300) バイーア</option>
    <option value="America/Buenos_Aires">(GMT-0300) ブエノスアイレス</option>
    <option value="America/Belem">(GMT-0300) ベレン</option>
    <option value="America/Recife">(GMT-0300) レシフェ</option>
    <option value="America/St_Johns">(GMT-0230) セントジョンズ</option>
    <option value="America/Noronha">(GMT-0200) ノローニャ</option>
    <option value="Africa/Casablanca">(GMT+0000) カサブランカ</option>
    <option value="Africa/Algiers">(GMT+0100) アルジェ</option>
    <option value="Atlantic/Canary">(GMT+0100) カナリア</option>
    <option value="Europe/Dublin">(GMT+0100) ダブリン</option>
    <option value="Africa/Tunis">(GMT+0100) チュニス</option>
    <option value="Africa/Lagos">(GMT+0100) ラゴス</option>
    <option value="Europe/London">(GMT+0100) ロンドン</option>
    <option value="Europe/Amsterdam">(GMT+0200) アムステルダム</option>
    <option value="Africa/Cairo">(GMT+0200) カイロ</option>
    <option value="Europe/Kaliningrad">(GMT+0200) カリーニングラード</option>
    <option value="Europe/Stockholm">(GMT+0200) ストックホルム</option>
    <option value="Europe/Paris">(GMT+0200) パリ</option>
    <option value="Europe/Budapest">(GMT+0200) ブダペスト</option>
    <option value="Europe/Brussels">(GMT+0200) ブリュッセル</option>
    <option value="Europe/Prague">(GMT+0200) プラハ</option>
    <option value="Europe/Berlin">(GMT+0200) ベルリン</option>
    <option value="Europe/Madrid">(GMT+0200) マドリード</option>
    <option value="Africa/Johannesburg">(GMT+0200) ヨハネスブルグ</option>
    <option value="Europe/Rome">(GMT+0200) ローマ</option>
    <option value="Europe/Warsaw">(GMT+0200) ワルシャワ</option>
    <option value="Asia/Aden">(GMT+0300) アデン</option>
    <option value="Asia/Amman">(GMT+0300) アンマン</option>
    <option value="Asia/Jerusalem">(GMT+0300) エルサレム</option>
    <option value="Africa/Kampala">(GMT+0300) カンパラ</option>
    <option value="Africa/Nairobi">(GMT+0300) ナイロビ</option>
    <option value="Europe/Moscow">(GMT+0300) モスクワ</option>
    <option value="Asia/Riyadh">(GMT+0300) リヤド</option>
    <option value="Europe/Volgograd">(GMT+0400) ボルゴグラード</option>
    <option value="Asia/Yekaterinburg">(GMT+0500) エカテリンブルグ</option>
    <option value="Asia/Kolkata">(GMT+0530) Kolkata</option>
    <option value="Asia/Omsk">(GMT+0600) オムスク</option>
    <option value="Asia/Krasnoyarsk">(GMT+0700) クラスノヤルスク</option>
    <option value="Asia/Novosibirsk">(GMT+0700) ノヴォシビルスク</option>
    <option value="Asia/Irkutsk">(GMT+0800) イルクーツク</option>
    <option value="Asia/Singapore">(GMT+0800) シンガポール</option>
    <option value="Australia/Perth">(GMT+0800) パース</option>
    <option value="Asia/Manila">(GMT+0800) マニラ</option>
    <option value="Asia/Taipei">(GMT+0800) 台北</option>
    <option value="Asia/Hong_Kong">(GMT+0800) 香港</option>
    <option value="Australia/Eucla">(GMT+0845) ユークラ</option>
    <option value="Asia/Seoul">(GMT+0900) ソウル</option>
    <option value="Asia/Yakutsk">(GMT+0900) ヤクーツク</option>
    <option value="Asia/Tokyo">(GMT+0900) 東京</option>
    <option value="Australia/Adelaide">(GMT+0930) アデレード</option>
    <option value="Australia/Darwin">(GMT+0930) ダーウィン</option>
    <option value="Asia/Vladivostok">(GMT+1000) ウラジオストク</option>
    <option value="Australia/Sydney">(GMT+1000) シドニー</option>
    <option value="Australia/Brisbane">(GMT+1000) ブリスベン</option>
    <option value="Australia/Hobart">(GMT+1000) ホバート</option>
    <option value="Australia/Melbourne">(GMT+1000) メルボルン</option>
    <option value="Asia/Sakhalin">(GMT+1100) サハリン</option>
    <option value="Pacific/Auckland">(GMT+1200) オークランド</option>
    <option value="Asia/Kamchatka">(GMT+1200) カムチャッカ</option>
    <option value="Pacific/Chatham">(GMT+1245) チャタム</option>
</select></span>

    <div class="publish-date-error hid">
      <div class="yt-alert yt-alert-default yt-alert-info  ">  <div class="yt-alert-icon">
    <span class="icon master-sprite yt-sprite"></span>
  </div>
<div class="yt-alert-content" role="alert">    <div class="yt-alert-message" tabindex="0">
            過去の日時が設定されています。動画はすぐに公開されます。
    </div>
</div><div class="yt-alert-buttons"></div></div>
    </div>


    <input type="hidden" value="2019-05-10T16:00" name="publish_time" class="publish_time_value">
    <input type="hidden" value="UTC" name="publish_timezone" class="publish_timezone_value">
  </div>

            
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="1687762251652441373">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="1687762251652441373">YouTube で動画を公開すると動画再生ページの下に表示される「～に公開」という日付は、太平洋標準時（PST）に基づいています。
        <a target="_new" href="//support.google.com/youtube/bin/answer.py?answer=1270709&amp;hl=ja">
詳細
        </a>
      </span>
    </span>
  </span>


        </div>
      </div>


  </div>
  <div class="metadata-privacy-disabled hid" style="display: none;">
    <p class="metadata-privacy-disabled-rental">
この動画は非公開となり、レンタルが承認されると自動的に公開されます
    </p>
  </div>

        <span class="yt-uix-clickcard metadata-add-to-clickcard" data-card-class="add-to-widget-clickcard metadata-add-to-clickcard-card">
    <span class="yt-uix-clickcard-target" data-position="bottomright" data-orientation="vertical">
      <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default addto-button metadata-add-to-playlist-button" type="button" onclick=";return false;" aria-pressed="false" aria-haspopup="true" data-feature="upload"><span class="yt-uix-button-content">    <span class="playlist-icon yt-sprite hid" style="display: none;"></span>
    <div class="playlist-label">+ 再生リストに追加</div>
</span></button>
    </span>
    <span class="yt-uix-clickcard-content add-to-widget">
        <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
読み込んでいます...
    </span>
  </p>

    </span>
  </span>

        <div class="guidelines-reminder-container">
    <div class="guidelines-reminder">
      <img class="guidelines-reminder-icon" alt="" src="/yts/img/child_safety_icon-vflR4uq1h.png">
      <div>
        <p class="guidelines-reminder-title">
この動画に未成年者は登場しますか？
        </p>
        <p class="guidelines-reminder-description">
YouTube の子供の安全に関するポリシーに従っていることと、適用される可能性のある労働法の義務に準拠していることをご確認ください。<a href="//support.google.com/youtube/answer/2801999?hl=ja" target="_blank">詳細</a>
        </p>
      </div>
    </div>

    <div class="guidelines-reminder">
      <img class="guidelines-reminder-icon" alt="" src="/yts/img/educator_resources_icon-vflp8y7TL.png">
      <div>
        <p class="guidelines-reminder-title">
コンテンツに関する全体的なガイダンスをお探しですか？
        </p>
        <p class="guidelines-reminder-description">
YouTube のコミュニティ ガイドラインは、トラブルを回避し、YouTube をクリエイター、広告主、視聴者が成功を収められる場所として維持するうえで役立ちます。<a href="//www.youtube.com/yt/policyandsafety/ja/" target="_blank">詳細</a>
        </p>
      </div>
    </div>
  </div>

    </fieldset>
      <fieldset class="metadata-column-footer" data-ng-non-bindable="">
      
  <div class="metadata-thumbnail-chooser horizontal-thumbnail-chooser">
      <h3>
動画のサムネイル
          
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="-608481742136678228">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-608481742136678228">ご自分の動画に最適なサムネイルを選択してください。カスタム サムネイルを追加するには、お使いのアカウントが<a href="/verify_phone_number">確認済み</a>で、良好な状態である必要があります。<a href="//support.google.com/youtube/answer/72431?hl=ja">詳細</a>

      </span>
    </span>
  </span>


      </h3>

    <span class="before-standard-thumbs-available hid" style="display: none;">
      <span class="thumb-placeholder thumb_dimensions">
動画の処理が終わると、サムネイルを選択できます。
      </span>
    </span>

    <span class="standard-thumbs-available">

        <div class="selectable-thumb standard-thumb thumb_dimensions " role="button" tabindex="0" aria-label="サムネイル 1 を選択
">
            <span class="video-thumb  yt-thumb yt-thumb-120">
    <span class="yt-thumb-default">
      <span class="yt-thumb-clip">
        
  <img data-ytimg="1" aria-hidden="true" alt="" onload=";window.__ytRIL &amp;&amp; __ytRIL(this)" src="https://www.youtube.com/upload_thumbnail?v=A7nY0gHIR_Q&amp;t=1&amp;ts=1557418823460" width="120" data-still-image-index="1">

        <span class="vertical-align"></span>
      </span>
    </span>
  </span>

          <span class="thumbnail-overlay hid overlay-standard thumb_dimensions">
            <span class="thumbnail-overlay-inner-border">
              <div class="thumbnail-overlay-content">
                サムネイルとして設定
              </div>
            </span>
          </span>
        </div>

        <div class="selectable-thumb standard-thumb thumb_dimensions " role="button" tabindex="0" aria-label="サムネイル 2 を選択
">
            <span class="video-thumb  yt-thumb yt-thumb-120">
    <span class="yt-thumb-default">
      <span class="yt-thumb-clip">
        
  <img data-ytimg="1" aria-hidden="true" alt="" onload=";window.__ytRIL &amp;&amp; __ytRIL(this)" src="https://www.youtube.com/upload_thumbnail?v=A7nY0gHIR_Q&amp;t=2&amp;ts=1557418823460" width="120" data-still-image-index="2">

        <span class="vertical-align"></span>
      </span>
    </span>
  </span>

          <span class="thumbnail-overlay hid overlay-standard thumb_dimensions">
            <span class="thumbnail-overlay-inner-border">
              <div class="thumbnail-overlay-content">
                サムネイルとして設定
              </div>
            </span>
          </span>
        </div>

        <div class="selectable-thumb standard-thumb thumb_dimensions " role="button" tabindex="0" aria-label="サムネイル 3 を選択
">
            <span class="video-thumb  yt-thumb yt-thumb-120">
    <span class="yt-thumb-default">
      <span class="yt-thumb-clip">
        
  <img data-ytimg="1" aria-hidden="true" alt="" onload=";window.__ytRIL &amp;&amp; __ytRIL(this)" src="https://www.youtube.com/upload_thumbnail?v=A7nY0gHIR_Q&amp;t=3&amp;ts=1557418823460" width="120" data-still-image-index="3">

        <span class="vertical-align"></span>
      </span>
    </span>
  </span>

          <span class="thumbnail-overlay hid overlay-standard thumb_dimensions">
            <span class="thumbnail-overlay-inner-border">
              <div class="thumbnail-overlay-content">
                サムネイルとして設定
              </div>
            </span>
          </span>
        </div>
    </span>

  </div>

    <input type="hidden" class="still-id" name="still_id" value="2">

    <input type="hidden" class="still-id-custom-thumb-version" name="thumbnail_preview_version" value="">
  </fieldset>

  </div>

        <div class="metadata-tab translation-tab fake-form-field-translations hid" data-tab-id="translations" role="tabpanel" style="display: none;">
    <div class="clearfix">
            <script id="language_picker.html" type="text/ng-template">
    
  <div class="yt-languagepicker">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default yt-languagepicker-button" type="button" onclick=";return false;" data-button-menu-id="yt-languagepicker-menu-{{menuId}}" data-default-button-text="言語を選択" data-button-menu-indicate-selected="True"><span class="yt-uix-button-content">言語を選択</span><span class="yt-uix-button-arrow yt-sprite"></span></button>
    <input type="hidden" id="yt-languagepicker-input-hidden-input-{{menuId}}" name="hidden-input-{{menuId}}" class="yt-uix-languagepicker-hidden-input">
    <div class="yt-uix-button-menu yt-uix-languagepicker yt-uix-languagepicker-menu hid loading" tabindex="0" id="yt-languagepicker-menu-{{menuId}}" data-languagepicker-input-id=yt-languagepicker-input-hidden-input-{{menuId}} data-fallback-option="und">
      <ul class="yt-uix-languagepicker-language-list yt-uix-languagepicker-suggestions" role="menu">
        <li data-ng-repeat="language in suggestedValues" class="yt-uix-languagepicker-menu-item yt-uix-languagepicker-menu-item-{{language.code}}" data-value="{{language.code}}" data-ng-click="select(language)" role="menuitem">
          <span class="yt-uix-button-menu-item">{{language.name}}</span>
        </li>
      </ul>
      <div data-ng-if="allowedValues.length > 0" class="yt-uix-languagepicker-search-container">
        <div class="yt-uix-languagepicker-search-input-container">
          <span class=" yt-uix-form-input-container yt-uix-form-input-text-container  yt-uix-form-input-fluid-container"><span class=" yt-uix-form-input-fluid"><input class="yt-uix-form-input-text yt-uix-languagepicker-search-input mde-ignore-change" placeholder="他の言語を検索"></span></span>
        </div>
        <ul class="yt-uix-languagepicker-language-list yt-uix-languagepicker-search-result hid" role="menu">
          <li data-ng-repeat="language in allowedValues" class="yt-uix-languagepicker-menu-item yt-uix-languagepicker-menu-item-{{language.code}}" data-value="{{language.code}}" data-ng-click="select(language)" role="menuitem">
            <span class="yt-uix-button-menu-item">{{language.name}}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>

  </script>


    <script id="translation_editor.html" type="text/ng-template">
      <div data-ng-if="transEditor.errors" class="translation-editor-error">
    <div class="yt-alert yt-alert-naked yt-alert-error  inline-error">  <div class="yt-alert-icon">
    <span class="icon master-sprite yt-sprite"></span>
  </div>
<div class="yt-alert-content" role="alert">    <div class="yt-alert-message" tabindex="0">
            次の翻訳を修正してください: {{transEditor.getLanguagesWithErrorsAsString()}}

    </div>
</div></div>
  </div>
    <div class="translation-editor-header translation-editor-row">
    <div class="translation-editor-column-half">
      <h4>
元の言語
      </h4>
      <div class="translation-editor-menu-bar">
          <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default translation-editor-original-language yt-uix-tooltip" type="button" onclick=";return false;" title="元の言語を変更" data-ng-click="transEditor.onClickOriginalLanguage()"><span class="yt-uix-button-content">{{transEditor.getOriginalLanguageName() || "言語を選択"}}</span></button>

      </div>
    </div>

    <div class="translation-editor-column-divider"></div>


    <div class="translation-editor-column-half">
      <h4>
翻訳先の言語
        <span class="translation-editor-translation-count">
          ({{transEditor.translatedLanguageList.length | number}})
        </span>
      </h4>
      <div class="translation-editor-menu-bar">
          <span data-translation-menu data-ng-model="transEditor.language" data-default-text="言語を選択" data-language-list="transEditor.translatedLanguageList" data-errors="transEditor.errors" data-on-add-translation="transEditor.onClickAddTranslation()"></span>

  <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default yt-uix-button-empty yt-uix-button-has-icon translation-editor-action-button translation-editor-delete-button yt-uix-tooltip" type="button" onclick=";return false;" title="言語を削除" data-ng-click="transEditor.deleteTranslation()" data-ng-disabled="transEditor.language == null"><span class="yt-uix-button-icon-wrapper"><span class="yt-uix-button-icon yt-uix-button-icon-translation-editor-delete-icon yt-sprite"></span></span></button>

      </div>
    </div>
  </div>


  </script>

    <script id="translation_menu.html" type="text/ng-template">
      <div class="yt-uix-menu yt-uix-menu-checked" >  <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default translation-editor-translation-menu-button yt-uix-menu-trigger" type="button" onclick=";return false;" data-ng-class="{&#39;field-with-error&#39;: errors}" aria-pressed="false" aria-haspopup="true" role="button" aria-label="翻訳先の言語: {{selectedText}}"><span class="yt-uix-button-content">{{selectedText}}</span><span class="yt-uix-button-arrow yt-sprite"></span></button>
<div class="yt-uix-menu-content yt-ui-menu-content yt-uix-menu-content-hidden" role="menu">  <ul class="translation-editor-translation-menu-content">
    <li data-ng-repeat="language in languageList" data-value="{{language.code}}">
        <button type="button" class="yt-ui-menu-item has-icon yt-uix-menu-close-on-select  yt-ui-menu-item-checked-hid"
 data-ng-class="{&#39;translation-editor-error&#39;: errors[language.code]}">
    <span class="yt-ui-menu-item-label">{{language.name}}</span>
  </button>

    </li>

    <li data-ng-if="languageList.length == 0" class="translation-editor-no-translations-item">
      翻訳はまだありません
    </li>

    <li class="yt-ui-menu-new-section-separator">
        <button type="button" class="yt-ui-menu-item yt-uix-menu-close-on-select translation-editor-add-translation-button"
 data-ng-click="onAddTranslation()">
    <span class="yt-ui-menu-item-label">新しい言語を追加</span>
  </button>

    </li>
  </ul>
</div></div>

  </script>

    <script id="set_original_language_dialog.html" type="text/ng-template">
        <div class="yt-dialog hid translation-editor-change-original-dialog">
    <div class="yt-dialog-base">
      <span class="yt-dialog-align"></span>
      <div class="yt-dialog-fg" role="dialog">
        <div class="yt-dialog-fg-content">
            <div class="yt-dialog-header">
                  <h2 class="yt-dialog-title" role="alert">
      元の言語の変更
  </h2>

            </div>
          <div class="yt-dialog-loading">
              <div class="yt-dialog-waiting-content">
      <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
読み込んでいます...
    </span>
  </p>

  </div>

          </div>
          <div class="yt-dialog-content">
              <div class="translation-editor-overlay">
      <p>
元のテキストの言語
  </p>

  <div class="translation-editor-overlay-setting-group clearfix">
    <div data-language-picker data-ng-model="dialogCtrl.originalLanguage" data-suggested-values="dialogCtrl.languagePickerSuggestions" data-allowed-values="dialogCtrl.languagePickerAllowedValues" class="translation-editor-original-picker"></div>

    <label class="default-for-new-uploads" data-ng-if="dialogCtrl.showDefaultLanguageForNewUploadsCheckbox">
      <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="dialogCtrl.defaultForNewUploads"><span class="yt-uix-form-input-checkbox-element"></span></span>
新しいアップロード動画のデフォルト設定
    </label>
  </div>

    <p data-ng-if="dialogCtrl.languageCollidesWithTranslation()" class="translation-editor-error">
選択した言語の翻訳は既に存在しています。続行すると、この翻訳は上書きされます。
    </p>
  </div>
  <div class="yt-uix-overlay-actions">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default" type="button" onclick="return false;" data-ng-click="dialogCtrl.cancel()"><span class="yt-uix-button-content">キャンセル</span></button>
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-primary" type="button" onclick="return false;" data-ng-click="dialogCtrl.save()" data-ng-disabled="dialogCtrl.originalLanguage == null"><span class="yt-uix-button-content">言語を設定</span></button>
  </div>

          </div>
          <div class="yt-dialog-working">
              <div class="yt-dialog-working-overlay"></div>
  <div class="yt-dialog-working-bubble">
    <div class="yt-dialog-waiting-content">
        <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
        処理しています...
    </span>
  </p>

      </div>
  </div>

          </div>
        </div>
        <div class="yt-dialog-focus-trap" tabindex="0"></div>
      </div>
    </div>
  </div>


  </script>

    <script id="add_translation_dialog.html" type="text/ng-template">
        <div class="yt-dialog hid translation-editor-add-new-translation-dialog">
    <div class="yt-dialog-base">
      <span class="yt-dialog-align"></span>
      <div class="yt-dialog-fg" role="dialog">
        <div class="yt-dialog-fg-content">
            <div class="yt-dialog-header">
                  <h2 class="yt-dialog-title" role="alert">
      新しい翻訳言語の追加
  </h2>

            </div>
          <div class="yt-dialog-loading">
              <div class="yt-dialog-waiting-content">
      <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
読み込んでいます...
    </span>
  </p>

  </div>

          </div>
          <div class="yt-dialog-content">
              <div class="translation-editor-overlay">
      <p>
翻訳先の言語
  </p>
  <div class="translation-editor-overlay-setting-group clearfix">
    <div data-language-picker data-ng-model="dialogCtrl.translationLanguage" data-suggested-values="dialogCtrl.languagePickerSuggestions" data-allowed-values="dialogCtrl.languagePickerAllowedValues" class="translation-editor-translation-picker"></div>
  </div>


    <p data-ng-if="dialogCtrl.originalLanguage == dialogCtrl.translationLanguage" class="translation-editor-error">
元の言語と同じ言語の翻訳を追加することはできません。
    </p>
  </div>
  <div class="yt-uix-overlay-actions">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default" type="button" onclick="return false;" data-ng-click="dialogCtrl.cancel()"><span class="yt-uix-button-content">キャンセル</span></button>
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-primary" type="button" onclick="return false;" data-ng-click="dialogCtrl.save()" data-ng-disabled="dialogCtrl.translationLanguage == null || dialogCtrl.translationLanguage == dialogCtrl.originalLanguage"><span class="yt-uix-button-content">言語を追加</span></button>
  </div>

          </div>
          <div class="yt-dialog-working">
              <div class="yt-dialog-working-overlay"></div>
  <div class="yt-dialog-working-bubble">
    <div class="yt-dialog-waiting-content">
        <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
        処理しています...
    </span>
  </p>

      </div>
  </div>

          </div>
        </div>
        <div class="yt-dialog-focus-trap" tabindex="0"></div>
      </div>
    </div>
  </div>


  </script>

    <script id="set_original_and_add_translation_dialog.html" type="text/ng-template">
        <div class="yt-dialog hid translation-editor-set-original-and-add-translation-dialog">
    <div class="yt-dialog-base">
      <span class="yt-dialog-align"></span>
      <div class="yt-dialog-fg" role="dialog">
        <div class="yt-dialog-fg-content">
            <div class="yt-dialog-header">
                  <h2 class="yt-dialog-title" role="alert">
      タイトルと説明の翻訳
  </h2>

            </div>
          <div class="yt-dialog-loading">
              <div class="yt-dialog-waiting-content">
      <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
読み込んでいます...
    </span>
  </p>

  </div>

          </div>
          <div class="yt-dialog-content">
              <div class="translation-editor-overlay">
      <p>
元のテキストの言語
  </p>

  <div class="translation-editor-overlay-setting-group clearfix">
    <div data-language-picker data-ng-model="dialogCtrl.originalLanguage" data-suggested-values="dialogCtrl.languagePickerSuggestions" data-allowed-values="dialogCtrl.languagePickerAllowedValues" class="translation-editor-original-picker"></div>

    <label class="default-for-new-uploads" data-ng-if="dialogCtrl.showDefaultLanguageForNewUploadsCheckbox">
      <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="dialogCtrl.defaultForNewUploads"><span class="yt-uix-form-input-checkbox-element"></span></span>
新しいアップロード動画のデフォルト設定
    </label>
  </div>

      <p>
翻訳先の言語
  </p>
  <div class="translation-editor-overlay-setting-group clearfix">
    <div data-language-picker data-ng-model="dialogCtrl.translationLanguage" data-suggested-values="dialogCtrl.languagePickerSuggestions" data-allowed-values="dialogCtrl.languagePickerAllowedValues" class="translation-editor-translation-picker"></div>
  </div>


    <p data-ng-if="dialogCtrl.originalLanguage != null && dialogCtrl.originalLanguage == dialogCtrl.translationLanguage" class="translation-editor-error">
2 つの異なる言語を選択する必要があります。
    </p>
  </div>
  <div class="yt-uix-overlay-actions">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default" type="button" onclick="return false;" data-ng-click="dialogCtrl.cancel()"><span class="yt-uix-button-content">キャンセル</span></button>
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-primary" type="button" onclick="return false;" data-ng-click="dialogCtrl.save()" data-ng-disabled="!dialogCtrl.canSave()"><span class="yt-uix-button-content">言語を追加</span></button>
  </div>

          </div>
          <div class="yt-dialog-working">
              <div class="yt-dialog-working-overlay"></div>
  <div class="yt-dialog-working-bubble">
    <div class="yt-dialog-waiting-content">
        <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
        処理しています...
    </span>
  </p>

      </div>
  </div>

          </div>
        </div>
        <div class="yt-dialog-focus-trap" tabindex="0"></div>
      </div>
    </div>
  </div>


  </script>


      <div class="translation-editor-container ng-untouched ng-valid ng-not-empty ng-valid-parse ng-pristine" data-ng-form="translation_editor" data-translation-editor="" data-ng-model="metadata.translation_editor" data-show-default-language-for-new-uploads-checkbox="true" data-init-from-base64-json="eyJvcmlnaW5hbCI6eyJsYW5ndWFnZSI6InpoIn0sInN1Z2dlc3RlZF9sYW5ndWFnZSI6W3siY29k
ZSI6ImphIiwibmFtZSI6IuaXpeacrOiqniJ9XSwiYWxsb3dlZF9sYW5ndWFnZSI6W3siY29kZSI6
InNkcCIsIm5hbWUiOiJTaGVyZHVrcGVuIn0seyJjb2RlIjoiaXMiLCJuYW1lIjoi44Ki44Kk44K5
44Op44Oz44OJ6KqeIn0seyJjb2RlIjoiYXkiLCJuYW1lIjoi44Ki44Kk44Oe44Op6KqeIn0seyJj
b2RlIjoiZ2EiLCJuYW1lIjoi44Ki44Kk44Or44Op44Oz44OJ6KqeIn0seyJjb2RlIjoiYXoiLCJu
YW1lIjoi44Ki44K844Or44OQ44Kk44K444Oj44Oz6KqeIn0seyJjb2RlIjoiYXMiLCJuYW1lIjoi
44Ki44OD44K144Og6KqeIn0seyJjb2RlIjoiYWEiLCJuYW1lIjoi44Ki44OV44Kh44Or6KqeIn0s
eyJjb2RlIjoiYWIiLCJuYW1lIjoi44Ki44OW44OP44K66KqeIn0seyJjb2RlIjoiYWYiLCJuYW1l
Ijoi44Ki44OV44Oq44Kr44O844Oz44K56KqeIn0seyJjb2RlIjoiYW0iLCJuYW1lIjoi44Ki44Og
44OP44Op6KqeIn0seyJjb2RlIjoiYXNlIiwibmFtZSI6IuOCouODoeODquOCq+aJi+ipsSJ9LHsi
Y29kZSI6ImFyIiwibmFtZSI6IuOCouODqeODk+OCouiqniJ9LHsiY29kZSI6ImFyYyIsIm5hbWUi
OiLjgqLjg6njg6Doqp4ifSx7ImNvZGUiOiJzcSIsIm5hbWUiOiLjgqLjg6vjg5Djg4vjgqLoqp4i
fSx7ImNvZGUiOiJoeSIsIm5hbWUiOiLjgqLjg6vjg6Hjg4vjgqLoqp4ifSx7ImNvZGUiOiJpdCIs
Im5hbWUiOiLjgqTjgr/jg6rjgqLoqp4ifSx7ImNvZGUiOiJ5aSIsIm5hbWUiOiLjgqTjg4fjgqPj
g4Pjgrfjg6Xoqp4ifSx7ImNvZGUiOiJpdSIsIm5hbWUiOiLjgqTjg4zjgq/jg4bjgqPjg4jjg4Pj
g4joqp4ifSx7ImNvZGUiOiJpayIsIm5hbWUiOiLjgqTjg4zjg5TjgqLjg4Pjgq/oqp4ifSx7ImNv
ZGUiOiJpZyIsIm5hbWUiOiLjgqTjg5zoqp4ifSx7ImNvZGUiOiJpZSIsIm5hbWUiOiLjgqTjg7Pj
gr/jg7zjg6rjg7PjgrAifSx7ImNvZGUiOiJpYSIsIm5hbWUiOiLjgqTjg7Pjgr/jg7zjg6rjg7Pj
grDjgqIifSx7ImNvZGUiOiJpZCIsIm5hbWUiOiLjgqTjg7Pjg4njg43jgrfjgqLoqp4ifSx7ImNv
ZGUiOiJjeSIsIm5hbWUiOiLjgqbjgqfjg7zjg6vjgrroqp4ifSx7ImNvZGUiOiJ2byIsIm5hbWUi
OiLjg7Tjgqnjg6njg5Tjg6Xjgq/oqp4ifSx7ImNvZGUiOiJ3byIsIm5hbWUiOiLjgqbjgqnjg63j
g5Xoqp4ifSx7ImNvZGUiOiJ1ayIsIm5hbWUiOiLjgqbjgq/jg6njgqTjg4roqp4ifSx7ImNvZGUi
OiJ1eiIsIm5hbWUiOiLjgqbjgrrjg5njgq/oqp4ifSx7ImNvZGUiOiJ1ciIsIm5hbWUiOiLjgqbj
g6vjg4njgqXjg7zoqp4ifSx7ImNvZGUiOiJldCIsIm5hbWUiOiLjgqjjgrnjg4jjg4vjgqLoqp4i
fSx7ImNvZGUiOiJlbyIsIm5hbWUiOiLjgqjjgrnjg5rjg6njg7Pjg4joqp4ifSx7ImNvZGUiOiJv
YyIsIm5hbWUiOiLjgqrjg4Pjgq/oqp4ifSx7ImNvZGUiOiJvciIsIm5hbWUiOiLjgqrjg4fjgqPj
gqLoqp4ifSx7ImNvZGUiOiJubCIsIm5hbWUiOiLjgqrjg6njg7Pjg4Doqp4ifSx7ImNvZGUiOiJu
bC1OTCIsIm5hbWUiOiLjgqrjg6njg7Pjg4Doqp4gKOOCquODqeODs+ODgCkifSx7ImNvZGUiOiJu
bC1CRSIsIm5hbWUiOiLjgqrjg6njg7Pjg4Doqp4gKOODmeODq+OCruODvCkifSx7ImNvZGUiOiJv
bSIsIm5hbWUiOiLjgqrjg63jg6Loqp4ifSx7ImNvZGUiOiJrayIsIm5hbWUiOiLjgqvjgrbjg5Xo
qp4ifSx7ImNvZGUiOiJrcyIsIm5hbWUiOiLjgqvjgrfjg5/jg7zjg6voqp4ifSx7ImNvZGUiOiJj
YSIsIm5hbWUiOiLjgqvjgr/jg63jg4vjgqLoqp4ifSx7ImNvZGUiOiJnbCIsIm5hbWUiOiLjgqzj
g6rjgrfjgqLoqp4ifSx7ImNvZGUiOiJrbiIsIm5hbWUiOiLjgqvjg7Pjg4rjg4Doqp4ifSx7ImNv
ZGUiOiJydyIsIm5hbWUiOiLjgq3jg4vjgqLjg6vjg6/jg7Pjg4Doqp4ifSx7ImNvZGUiOiJlbCIs
Im5hbWUiOiLjgq7jg6rjgrfjg6Poqp4ifSx7ImNvZGUiOiJreSIsIm5hbWUiOiLjgq3jg6vjgq7j
grnoqp4ifSx7ImNvZGUiOiJnbiIsIm5hbWUiOiLjgrDjgqLjg6njg4vjg7zoqp4ifSx7ImNvZGUi
OiJndSIsIm5hbWUiOiLjgrDjgrjjg6Pjg6njg7zjg4joqp4ifSx7ImNvZGUiOiJrbSIsIm5hbWUi
OiLjgq/jg6Hjg7zjg6voqp4ifSx7ImNvZGUiOiJrbCIsIm5hbWUiOiLjgrDjg6rjg7zjg7Pjg6nj
g7Pjg4noqp7vvIjjgqvjg6njg7zjg6rjg4Pjg4joqp7vvIkifSx7ImNvZGUiOiJ0bGgiLCJuYW1l
Ijoi44Kv44Oq44Oz44K044Oz6KqeIn0seyJjb2RlIjoia3UiLCJuYW1lIjoi44Kv44Or44OJ6Kqe
In0seyJjb2RlIjoiaHIiLCJuYW1lIjoi44Kv44Ot44Ki44OB44Ki6KqeIn0seyJjb2RlIjoicXUi
LCJuYW1lIjoi44Kx44OB44Ol44Ki6KqeIn0seyJjb2RlIjoieGgiLCJuYW1lIjoi44Kz44K16Kqe
In0seyJjb2RlIjoiY28iLCJuYW1lIjoi44Kz44Or44K344Kr6KqeIn0seyJjb2RlIjoic20iLCJu
YW1lIjoi44K144Oi44Ki6KqeIn0seyJjb2RlIjoic2MiLCJuYW1lIjoi44K144Or44OH44O844OL
44Oj6KqeIn0seyJjb2RlIjoic2ciLCJuYW1lIjoi44K144Oz44K06KqeIn0seyJjb2RlIjoic2Ei
LCJuYW1lIjoi44K144Oz44K544Kv44Oq44OD44OI6KqeIn0seyJjb2RlIjoic2NuIiwibmFtZSI6
IuOCt+ODgeODquOCouiqniJ9LHsiY29kZSI6Imp2IiwibmFtZSI6IuOCuOODo+ODr+iqniJ9LHsi
Y29kZSI6ImthIiwibmFtZSI6IuOCuOODp+ODvOOCuOOCouiqniJ9LHsiY29kZSI6InNuIiwibmFt
ZSI6IuOCt+ODp+ODiuiqniJ9LHsiY29kZSI6InNkIiwibmFtZSI6IuOCt+ODs+ODieiqniJ9LHsi
Y29kZSI6InNpIiwibmFtZSI6IuOCt+ODs+ODj+ODqeiqniJ9LHsiY29kZSI6InN2IiwibmFtZSI6
IuOCueOCpuOCp+ODvOODh+ODs+iqniJ9LHsiY29kZSI6Inp1IiwibmFtZSI6IuOCuuODvOODq+OD
vOiqniJ9LHsiY29kZSI6ImdkIiwibmFtZSI6IuOCueOCs+ODg+ODiOODqeODs+ODieODu+OCsuOD
vOODq+iqniJ9LHsiY29kZSI6ImVzIiwibmFtZSI6IuOCueODmuOCpOODs+iqniJ9LHsiY29kZSI6
ImVzLVVTIiwibmFtZSI6IuOCueODmuOCpOODs+iqniAo44Ki44Oh44Oq44Kr5ZCI6KGG5Zu9KSJ9
LHsiY29kZSI6ImVzLUVTIiwibmFtZSI6IuOCueODmuOCpOODs+iqniAo44K544Oa44Kk44OzKSJ9
LHsiY29kZSI6ImVzLU1YIiwibmFtZSI6IuOCueODmuOCpOODs+iqniAo44Oh44Kt44K344KzKSJ9
LHsiY29kZSI6ImVzLTQxOSIsIm5hbWUiOiLjgrnjg5rjgqTjg7Poqp4gKOODqeODhuODs+OCouOD
oeODquOCqykifSx7ImNvZGUiOiJzayIsIm5hbWUiOiLjgrnjg63jg5Djgq3jgqLoqp4ifSx7ImNv
ZGUiOiJzbCIsIm5hbWUiOiLjgrnjg63jg5njg4vjgqLoqp4ifSx7ImNvZGUiOiJzcyIsIm5hbWUi
OiLjgrnjg6/jgrjoqp4ifSx7ImNvZGUiOiJzdyIsIm5hbWUiOiLjgrnjg6/jg5Ljg6roqp4ifSx7
ImNvZGUiOiJzdSIsIm5hbWUiOiLjgrnjg7Pjg4Doqp4ifSx7ImNvZGUiOiJzciIsIm5hbWUiOiLj
grvjg6vjg5PjgqLoqp4ifSx7ImNvZGUiOiJzci1DeXJsIiwibmFtZSI6IuOCu+ODq+ODk+OCouiq
niAo44Kt44Oq44Or5paH5a2XKSJ9LHsiY29kZSI6InNyLUxhdG4iLCJuYW1lIjoi44K744Or44OT
44Ki6KqeICjjg6njg4bjg7PmloflrZcpIn0seyJjb2RlIjoic2giLCJuYW1lIjoi44K744Or44Oc
44O744Kv44Ot44Ki44OB44Ki6KqeIn0seyJjb2RlIjoic28iLCJuYW1lIjoi44K944Oe44Oq6Kqe
In0seyJjb2RlIjoiZHoiLCJuYW1lIjoi44K+44Oz44Kr6KqeIn0seyJjb2RlIjoidGgiLCJuYW1l
Ijoi44K/44Kk6KqeIn0seyJjb2RlIjoidGwiLCJuYW1lIjoi44K/44Ks44Ot44Kw6KqeIn0seyJj
b2RlIjoidGciLCJuYW1lIjoi44K/44K444Kv6KqeIn0seyJjb2RlIjoidHQiLCJuYW1lIjoi44K/
44K/44O844Or6KqeIn0seyJjb2RlIjoidGEiLCJuYW1lIjoi44K/44Of44Or6KqeIn0seyJjb2Rl
IjoiY3MiLCJuYW1lIjoi44OB44Kn44Kz6KqeIn0seyJjb2RlIjoiY2hyIiwibmFtZSI6IuODgeOC
p+ODreOCreODvOiqniJ9LHsiY29kZSI6ImJvIiwibmFtZSI6IuODgeODmeODg+ODiOiqniJ9LHsi
Y29kZSI6ImNobyIsIm5hbWUiOiLjg4Hjg6fjgq/jg4jjg7zoqp4ifSx7ImNvZGUiOiJ0cyIsIm5h
bWUiOiLjg4Tjgqnjg7Pjgqzoqp4ifSx7ImNvZGUiOiJ0biIsIm5hbWUiOiLjg4Tjg6/jg4roqp4i
fSx7ImNvZGUiOiJ0aSIsIm5hbWUiOiLjg4bjgqPjgrDjg6rjg4vjgqLoqp4ifSx7ImNvZGUiOiJ0
ZSIsIm5hbWUiOiLjg4bjg6vjgrDoqp4ifSx7ImNvZGUiOiJkYSIsIm5hbWUiOiLjg4fjg7Pjg57j
g7zjgq/oqp4ifSx7ImNvZGUiOiJkZSIsIm5hbWUiOiLjg4njgqTjg4Toqp4ifSx7ImNvZGUiOiJk
ZS1BVCIsIm5hbWUiOiLjg4njgqTjg4Toqp4gKOOCquODvOOCueODiOODquOCoikifSx7ImNvZGUi
OiJkZS1DSCIsIm5hbWUiOiLjg4njgqTjg4Toqp4gKOOCueOCpOOCuSkifSx7ImNvZGUiOiJkZS1E
RSIsIm5hbWUiOiLjg4njgqTjg4Toqp4gKOODieOCpOODhCkifSx7ImNvZGUiOiJ0dyIsIm5hbWUi
OiLjg4jjgqbjgqPoqp4ifSx7ImNvZGUiOiJ0ayIsIm5hbWUiOiLjg4jjg6vjgq/jg6Hjg7Poqp4i
fSx7ImNvZGUiOiJ0ciIsIm5hbWUiOiLjg4jjg6vjgrPoqp4ifSx7ImNvZGUiOiJ0byIsIm5hbWUi
OiLjg4jjg7Pjgqzoqp4ifSx7ImNvZGUiOiJuYSIsIm5hbWUiOiLjg4rjgqbjg6voqp4ifSx7ImNv
ZGUiOiJudiIsIm5hbWUiOiLjg4rjg5Djg5voqp4ifSx7ImNvZGUiOiJuZSIsIm5hbWUiOiLjg43j
g5Hjg7zjg6voqp4ifSx7ImNvZGUiOiJubyIsIm5hbWUiOiLjg47jg6vjgqbjgqfjgqToqp4ifSx7
ImNvZGUiOiJoYSIsIm5hbWUiOiLjg4/jgqbjgrXoqp4ifSx7ImNvZGUiOiJiYSIsIm5hbWUiOiLj
g5Djgrfjgq3jg7zjg6voqp4ifSx7ImNvZGUiOiJwcyIsIm5hbWUiOiLjg5Hjgrfjg6Xjg4jjgqXj
g7zoqp4ifSx7ImNvZGUiOiJldSIsIm5hbWUiOiLjg5Djgrnjgq/oqp4ifSx7ImNvZGUiOiJodSIs
Im5hbWUiOiLjg4/jg7Pjgqzjg6rjg7zoqp4ifSx7ImNvZGUiOiJwYSIsIm5hbWUiOiLjg5Hjg7Pj
grjjg6Pjg5boqp4ifSx7ImNvZGUiOiJiaSIsIm5hbWUiOiLjg5Pjgrnjg6njg57oqp4ifSx7ImNv
ZGUiOiJiaCIsIm5hbWUiOiLjg5Pjg4/jg7zjg6voqp4ifSx7ImNvZGUiOiJoaSIsIm5hbWUiOiLj
g5Ljg7Pjg4fjgqPjg7zoqp4ifSx7ImNvZGUiOiJoaS1MYXRuIiwibmFtZSI6IuODkuODs+ODh+OC
o+ODvOiqnu+8iOihqOmfs++8iSJ9LHsiY29kZSI6ImZqIiwibmFtZSI6IuODleOCo+OCuOODvOiq
niJ9LHsiY29kZSI6ImZpbCIsIm5hbWUiOiLjg5XjgqPjg6rjg5Tjg47oqp4ifSx7ImNvZGUiOiJm
aSIsIm5hbWUiOiLjg5XjgqPjg7Pjg6njg7Pjg4noqp4ifSx7ImNvZGUiOiJmbyIsIm5hbWUiOiLj
g5Xjgqfjg63jg7zoqp4ifSx7ImNvZGUiOiJmciIsIm5hbWUiOiLjg5Xjg6njg7Pjgrnoqp4ifSx7
ImNvZGUiOiJmci1DQSIsIm5hbWUiOiLjg5Xjg6njg7Pjgrnoqp4gKOOCq+ODiuODgCkifSx7ImNv
ZGUiOiJmci1DSCIsIm5hbWUiOiLjg5Xjg6njg7Pjgrnoqp4gKOOCueOCpOOCuSkifSx7ImNvZGUi
OiJmci1GUiIsIm5hbWUiOiLjg5Xjg6njg7Pjgrnoqp4gKOODleODqeODs+OCuSkifSx7ImNvZGUi
OiJmci1CRSIsIm5hbWUiOiLjg5Xjg6njg7Pjgrnoqp4gKOODmeODq+OCruODvCkifSx7ImNvZGUi
OiJmZiIsIm5hbWUiOiLjg5Xjg6noqp4ifSx7ImNvZGUiOiJiZyIsIm5hbWUiOiLjg5bjg6vjgqzj
g6rjgqLoqp4ifSx7ImNvZGUiOiJiciIsIm5hbWUiOiLjg5bjg6vjg4jjg7Poqp4ifSx7ImNvZGUi
OiJ2aSIsIm5hbWUiOiLjg5njg4jjg4rjg6Doqp4ifSx7ImNvZGUiOiJpdyIsIm5hbWUiOiLjg5jj
g5bjg6njgqToqp4ifSx7ImNvZGUiOiJiZSIsIm5hbWUiOiLjg5njg6njg6vjg7zjgrfoqp4ifSx7
ImNvZGUiOiJmYSIsIm5hbWUiOiLjg5rjg6vjgrfjgqLoqp4ifSx7ImNvZGUiOiJmYS1BRiIsIm5h
bWUiOiLjg5rjg6vjgrfjgqLoqp4gKOOCouODleOCrOODi+OCueOCv+ODsykifSx7ImNvZGUiOiJm
YS1JUiIsIm5hbWUiOiLjg5rjg6vjgrfjgqLoqp4gKOOCpOODqeODsykifSx7ImNvZGUiOiJibiIs
Im5hbWUiOiLjg5njg7Pjgqzjg6voqp4ifSx7ImNvZGUiOiJwbCIsIm5hbWUiOiLjg53jg7zjg6nj
g7Pjg4noqp4ifSx7ImNvZGUiOiJicyIsIm5hbWUiOiLjg5zjgrnjg4vjgqLoqp4ifSx7ImNvZGUi
OiJwdCIsIm5hbWUiOiLjg53jg6vjg4jjgqzjg6voqp4ifSx7ImNvZGUiOiJwdC1CUiIsIm5hbWUi
OiLjg53jg6vjg4jjgqzjg6voqp4gKOODluODqeOCuOODqykifSx7ImNvZGUiOiJwdC1QVCIsIm5h
bWUiOiLjg53jg6vjg4jjgqzjg6voqp4gKOODneODq+ODiOOCrOODqykifSx7ImNvZGUiOiJtaSIs
Im5hbWUiOiLjg57jgqrjg6roqp4ifSx7ImNvZGUiOiJtayIsIm5hbWUiOiLjg57jgrHjg4njg4vj
gqLoqp4ifSx7ImNvZGUiOiJtYXMiLCJuYW1lIjoi44Oe44K144Kk6KqeIn0seyJjb2RlIjoibWci
LCJuYW1lIjoi44Oe44OA44Ks44K544Kr44Or6KqeIn0seyJjb2RlIjoibW5pIiwibmFtZSI6IuOD
nuODi+ODl+ODquiqniJ9LHsiY29kZSI6Im1yIiwibmFtZSI6IuODnuODqeODvOODhuOCo+ODvOiq
niJ9LHsiY29kZSI6Im1sIiwibmFtZSI6IuODnuODqeODpOODvOODqeODoOiqniJ9LHsiY29kZSI6
Im10IiwibmFtZSI6IuODnuODq+OCv+iqniJ9LHsiY29kZSI6Im1zIiwibmFtZSI6IuODnuODrOOD
vOiqniJ9LHsiY29kZSI6Imx1cyIsIm5hbWUiOiLjg5/jgr7oqp4ifSx7ImNvZGUiOiJteSIsIm5h
bWUiOiLjg5/jg6Pjg7Pjg57jg7zvvIjjg5Pjg6vjg57vvInoqp4ifSx7ImNvZGUiOiJtbyIsIm5h
bWUiOiLjg6Ljg6vjg4Djg5PjgqLoqp4ifSx7ImNvZGUiOiJtbiIsIm5hbWUiOiLjg6Ljg7PjgrTj
g6voqp4ifSx7ImNvZGUiOiJ5byIsIm5hbWUiOiLjg6jjg6vjg5Doqp4ifSx7ImNvZGUiOiJsbyIs
Im5hbWUiOiLjg6njgqroqp4ifSx7ImNvZGUiOiJsYSIsIm5hbWUiOiLjg6njg4bjg7Poqp4ifSx7
ImNvZGUiOiJsdiIsIm5hbWUiOiLjg6njg4jjg5PjgqLoqp4ifSx7ImNvZGUiOiJsdCIsIm5hbWUi
OiLjg6rjg4jjgqLjg4vjgqLoqp4ifSx7ImNvZGUiOiJsbiIsIm5hbWUiOiLjg6rjg7Pjgqzjg6no
qp4ifSx7ImNvZGUiOiJybyIsIm5hbWUiOiLjg6vjg7zjg57jg4vjgqLoqp4ifSx7ImNvZGUiOiJs
YiIsIm5hbWUiOiLjg6vjgq/jgrvjg7Pjg5bjg6vjgq/oqp4ifSx7ImNvZGUiOiJybiIsIm5hbWUi
OiLjg6vjg7Pjg4fjgqPoqp4ifSx7ImNvZGUiOiJydSIsIm5hbWUiOiLjg63jgrfjgqLoqp4ifSx7
ImNvZGUiOiJydS1MYXRuIiwibmFtZSI6IuODreOCt+OCouiqnu+8iOihqOmfs++8iSJ9LHsiY29k
ZSI6InJtIiwibmFtZSI6IuODreODnuODs+OCt+ODpeiqniJ9LHsiY29kZSI6ImVuIiwibmFtZSI6
IuiLseiqniJ9LHsiY29kZSI6ImVuLUlFIiwibmFtZSI6IuiLseiqniAo44Ki44Kk44Or44Op44Oz
44OJKSJ9LHsiY29kZSI6ImVuLVVTIiwibmFtZSI6IuiLseiqniAo44Ki44Oh44Oq44Kr5ZCI6KGG
5Zu9KSJ9LHsiY29kZSI6ImVuLUdCIiwibmFtZSI6IuiLseiqniAo44Kk44Ku44Oq44K5KSJ9LHsi
Y29kZSI6ImVuLUNBIiwibmFtZSI6IuiLseiqniAo44Kr44OK44OAKSJ9LHsiY29kZSI6ImtvIiwi
bmFtZSI6Iumfk+WbveiqniJ9LHsiY29kZSI6ImhhayIsIm5hbWUiOiLlrqLlrrboqp4ifSx7ImNv
ZGUiOiJoYWstVFciLCJuYW1lIjoi5a6i5a626KqeICjlj7Dmub4pIn0seyJjb2RlIjoieXVlIiwi
bmFtZSI6IuW6g+adseiqniJ9LHsiY29kZSI6Inl1ZS1ISyIsIm5hbWUiOiLluoPmnbHoqp4gKOmm
mea4rykifSx7ImNvZGUiOiJmeSIsIm5hbWUiOiLopb/jg5Xjg6rjgrjjgqLoqp4ifSx7ImNvZGUi
OiJ6aCIsIm5hbWUiOiLkuK3lm73oqp4ifSx7ImNvZGUiOiJ6aC1TRyIsIm5hbWUiOiLkuK3lm73o
qp4gKOOCt+ODs+OCrOODneODvOODqykifSx7ImNvZGUiOiJ6aC1IYW5zIiwibmFtZSI6IuS4reWb
veiqniAo57Ch5L2T5a2XKSJ9LHsiY29kZSI6InpoLUhLIiwibmFtZSI6IuS4reWbveiqniAo6aaZ
5rivKSJ9LHsiY29kZSI6InpoLVRXIiwibmFtZSI6IuS4reWbveiqniAo5Y+w5rm+KSJ9LHsiY29k
ZSI6InpoLUNOIiwibmFtZSI6IuS4reWbveiqniAo5Lit5Zu9KSJ9LHsiY29kZSI6InpoLUhhbnQi
LCJuYW1lIjoi5Lit5Zu96KqeICjnuYHkvZPlrZcpIn0seyJjb2RlIjoic3QiLCJuYW1lIjoi5Y2X
6YOo44K944OI6KqeIn0seyJjb2RlIjoiamEiLCJuYW1lIjoi5pel5pys6KqeIn0seyJjb2RlIjoi
bmFuIiwibmFtZSI6IumWqeWNl+iqniJ9LHsiY29kZSI6Im5hbi1UVyIsIm5hbWUiOiLplqnljZfo
qp4gKOWPsOa5vikifV19
">
      <!---->
    <div class="translation-editor-header translation-editor-row">
    <div class="translation-editor-column-half">
      <h4>
元の言語
      </h4>
      <div class="translation-editor-menu-bar">
          <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default translation-editor-original-language yt-uix-tooltip" type="button" onclick=";return false;" title="元の言語を変更" data-ng-click="transEditor.onClickOriginalLanguage()"><span class="yt-uix-button-content">中国語</span></button>

      </div>
    </div>

    <div class="translation-editor-column-divider"></div>


    <div class="translation-editor-column-half">
      <h4>
翻訳先の言語
        <span class="translation-editor-translation-count">
          (0)
        </span>
      </h4>
      <div class="translation-editor-menu-bar">
          <span data-translation-menu="" data-ng-model="transEditor.language" data-default-text="言語を選択" data-language-list="transEditor.translatedLanguageList" data-errors="transEditor.errors" data-on-add-translation="transEditor.onClickAddTranslation()" class="ng-pristine ng-untouched ng-valid ng-empty">
      <div class="yt-uix-menu yt-uix-menu-checked">  <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default translation-editor-translation-menu-button yt-uix-menu-trigger" type="button" onclick=";return false;" data-ng-class="{'field-with-error': errors}" aria-pressed="false" aria-haspopup="true" role="button" aria-label="翻訳先の言語: 言語を選択"><span class="yt-uix-button-content">言語を選択</span><span class="yt-uix-button-arrow yt-sprite"></span></button>
<div class="yt-uix-menu-content yt-ui-menu-content yt-uix-menu-content-hidden" role="menu">  <ul class="translation-editor-translation-menu-content">
    <!---->

    <!----><li data-ng-if="languageList.length == 0" class="translation-editor-no-translations-item">
      翻訳はまだありません
    </li><!---->

    <li class="yt-ui-menu-new-section-separator">
        <button type="button" class="yt-ui-menu-item yt-uix-menu-close-on-select translation-editor-add-translation-button" data-ng-click="onAddTranslation()">
    <span class="yt-ui-menu-item-label">新しい言語を追加</span>
  </button>

    </li>
  </ul>
</div></div>

  </span>

  <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default yt-uix-button-empty yt-uix-button-has-icon translation-editor-action-button translation-editor-delete-button yt-uix-tooltip" type="button" onclick=";return false;" title="言語を削除" data-ng-click="transEditor.deleteTranslation()" data-ng-disabled="transEditor.language == null" disabled="disabled"><span class="yt-uix-button-icon-wrapper"><span class="yt-uix-button-icon yt-uix-button-icon-translation-editor-delete-icon yt-sprite"></span></span></button>

      </div>
    </div>
  </div>


  
          

    <div class="translation-editor-metadata-input-group translation-editor-row">
    <div class="translation-editor-column-half">
      <span class=" yt-uix-form-input-container yt-uix-form-input-text-container  yt-uix-form-input-fluid-container"><span class=" yt-uix-form-input-fluid"><input class="yt-uix-form-input-text translation-editor-original-input translation-editor-original-title ng-pristine ng-untouched ng-valid ng-not-empty" placeholder="タイトル" readonly="" data-ng-model="transEditor.original.title" tabindex="-1"></span></span>
    </div>

    <div class="translation-editor-column-divider">
      <span class="translation-editor-arrow yt-sprite"></span>
    </div>

    <div class="translation-editor-column-half">
      <span class=" yt-uix-form-input-container yt-uix-form-input-text-container  yt-uix-form-input-fluid-container"><span class=" yt-uix-form-input-fluid"><input class="yt-uix-form-input-text translation-editor-translated-title ng-pristine ng-untouched ng-valid ng-empty" placeholder="翻訳したタイトルを入力してください" data-ng-model="transEditor.translations[transEditor.language].title" data-ng-disabled="transEditor.language == null" disabled="disabled"></span></span>  <!---->

    </div>
  </div>



          

    <div class="translation-editor-metadata-input-group translation-editor-row">
    <div class="translation-editor-column-half">
      <span class="yt-uix-form-input-container yt-uix-form-input-textarea-container  yt-uix-form-input-fluid-container"><span class=" yt-uix-form-input-fluid"><textarea class="yt-uix-form-input-textarea translation-editor-original-input translation-editor-original-description ng-pristine ng-untouched ng-valid ng-empty" placeholder="説明" readonly="" data-ng-model="transEditor.original.description" rows="6" tabindex="-1" aria-label="説明"></textarea></span></span>
    </div>

    <div class="translation-editor-column-divider">
      <span class="translation-editor-arrow yt-sprite"></span>
    </div>

    <div class="translation-editor-column-half">
      <span class="yt-uix-form-input-container yt-uix-form-input-textarea-container  yt-uix-form-input-fluid-container"><span class=" yt-uix-form-input-fluid"><textarea class="yt-uix-form-input-textarea translation-editor-translated-description ng-pristine ng-untouched ng-valid ng-empty" placeholder="翻訳した説明を入力してください" data-ng-model="transEditor.translations[transEditor.language].description" data-ng-disabled="transEditor.language == null" rows="6" aria-label="翻訳した説明を入力してください" disabled="disabled"></textarea></span></span>  <!---->

    </div>
  </div>



          <div class="translation-editor-row">
    <div class="translation-editor-column-half"></div>
    <div class="translation-editor-column-divider"></div>
    <div class="translation-editor-column-half ng-hide" data-ng-show="transEditor.translations[transEditor.language].source">
      <span class="translation-editor-source">
        翻訳元:
        
      </span>
      <span class="translation-editor-source ng-hide" data-ng-show="transEditor.getSourceWarningVisibility()">
        編集内容を保存すると、この翻訳のソースが次の言語に変更されます:
        
      </span>
    </div>
  </div>

      </div>
    </div>
  </div>

        <div class="metadata-tab hid" data-tab-id="monetization" role="tabpanel" style="display: none;">

  </div>

          <div class="metadata-tab hid" data-tab-id="advanced-settings" role="tabpanel" style="display: none;">
      <div class="clearfix">
          <div class="metadata-column-half">
    <div data-ng-non-bindable="">
        <h3 class="comments-options-header">コメント</h3>

  <div class="advanced-tab-option">
    <label>
          <span class="yt-uix-form-input-checkbox-container  checked"><input type="checkbox" class="yt-uix-form-input-checkbox video-settings-allow-comments mde-checkbox" name="allow_comments" checked="checked" data-initial-value="yes"><span class="yt-uix-form-input-checkbox-element"></span></span>


コメントを許可する。
      <a target="_blank" href="//support.google.com/youtube/bin/answer.py?answer=111870&amp;hl=ja">
詳細
      </a>
    </label>

    <div class="setting-two-column">
      <label>
表示
      </label>
      <span class="yt-uix-form-input-select inline-select video-settings-allow-comments-detail"><span class="yt-uix-form-input-select-content"><span class="yt-uix-form-input-select-arrow yt-sprite"></span><span class="yt-uix-form-input-select-value">すべて</span></span><select class="yt-uix-form-input-select-element " name="allow_comments_detail" aria-label="コメントの種類の選択"><option value="all" selected="">すべて</option><option value="auto">不適切な可能性があるコメントを除くすべて</option><option value="approval">承認制</option></select></span>
    </div>

    <div class="setting-two-column">
      <label>
並べ替え
      </label>
      <span class="yt-uix-form-input-select inline-select video-settings-allow-comments-sort-order"><span class="yt-uix-form-input-select-content"><span class="yt-uix-form-input-select-arrow yt-sprite"></span><span class="yt-uix-form-input-select-value">評価順</span></span><select class="yt-uix-form-input-select-element " name="allow_comments_sort_order" aria-label="コメントの並べ替え条件"><option value="top_comments" selected="">評価順</option><option value="latest_first">新しい順</option></select></span>
    </div>
  </div>

  <div class="advanced-tab-option">
    <label>
          <span class="yt-uix-form-input-checkbox-container  checked"><input type="checkbox" class="yt-uix-form-input-checkbox video-settings-allow-ratings mde-checkbox" name="allow_ratings" checked="checked" data-initial-value="yes"><span class="yt-uix-form-input-checkbox-element"></span></span>


この動画の評価をユーザーに表示する
    </label>
  </div>



      <div class="advanced-tab-option">
           <div class="license-selection single-field">
     <h3 class="license-header">
所有するライセンスと権利
           
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="-2661166545095825609">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-2661166545095825609">標準の YouTube ライセンスについては、<a href="/t/terms">YouTube 利用規約</a>の第 6 条をご覧ください。また、クリエイティブ・コモンズ表示ライセンスもご利用いただけます。<a href="/t/creative_commons">詳細</a>
      </span>
    </span>
  </span>



     </h3>
     <span class="yt-uix-form-input-select license-selection-select"><span class="yt-uix-form-input-select-content"><span class="yt-uix-form-input-select-arrow yt-sprite"></span><span class="yt-uix-form-input-select-value">
標準の YouTube ライセンス
   </span></span><select class="yt-uix-form-input-select-element license-input" name="reuse">   <option value="all_rights_reserved" selected="">
標準の YouTube ライセンス
   </option>
   <option value="creative_commons">
クリエイティブ・コモンズ（表示）
   </option>
</select></span>
   </div>

      </div>
    </div>

        <div id="live-chat-settings" class="hid" style="display: none;">
    <h3 class="chat-options-header">チャット</h3>

    <div data-ng-non-bindable="">
          <div class="advanced-tab-option ">
    <label>
          <span class="yt-uix-form-input-checkbox-container  checked"><input type="checkbox" class="yt-uix-form-input-checkbox video-settings-user_enabled_live_chat_replay mde-checkbox" name="user_enabled_live_chat_replay" checked="checked" data-initial-value="no"><span class="yt-uix-form-input-checkbox-element"></span></span>


このプレミア公開に対するチャットでの返信を許可する
        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="-8298936632172878478">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-8298936632172878478">これにより、視聴者が後でライブ配信を再生したときに、チャットがライブ時と同じように表示されます。
      </span>
    </span>
  </span>


    </label>
  </div>

    </div>



        <script id="chat_options.html" type="text/ng-template">
          <label class="chat-options-checkbox-label">
      <span class="yt-uix-form-input-checkbox-container slow-mode-enabled-checkbox"><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="chatOptions.slowModeEnabled"><span class="yt-uix-form-input-checkbox-element"></span></span>
      <span class="slow-mode-enabled-label">
        低速モードを有効にする
      </span>
        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="-4537410281162749609">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-4537410281162749609">低速モードにすると、チャット参加者がメッセージを投稿できる間隔を指定できます。
      </span>
    </span>
  </span>


    </label>
      <label class="slow-mode-duration-label" ng-class="{'metadata-disabled': !chatOptions.slowModeEnabled}">
チャットを投稿できる間隔を 1 人あたり   <span class="yt-uix-form-input-container yt-uix-form-input-text-container" ng-class="{'yt-uix-form-error': !chatOptions.isSlowModeDurationValid()}">
    <input class="yt-uix-form-input-text slow-mode-timeout-duration" maxlength="3" ng-disabled="!chatOptions.slowModeEnabled" ng-model="chatOptions.slowModeTimeoutDurationSec">
  </span>
 秒に制限する
    <span class="yt-uix-form-error" ng-show="!chatOptions.isSlowModeDurationValid()">
       <span class="yt-uix-form-error-message" role="alert">
最小値: 1 秒。最大値: 300 秒。
      </span>
    </span>
  </label>

  <label class="chat-options-checkbox-label">
    <span class="yt-uix-form-input-checkbox-container block-spam-checkbox"><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="chatOptions.blockSpam"><span class="yt-uix-form-input-checkbox-element"></span></span>
    <span class="block-spam-label">
      スパム メッセージを自動的にブロック
    </span>
      
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="-5052646796119995325">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-5052646796119995325">すべて大文字で書かれた長文のメッセージや、同じユーザーから同じメッセージが繰り返し送られるなどの単純なスパムが自動的にブロックされます。
      </span>
    </span>
  </span>


  </label>


  </script>


    
  <ng-form name="chat_options" data-chat-options="" data-ng-model="metadata.chat_options" data-initial-chat-options="{&quot;super_chat_for_good_npo_id&quot;: null, &quot;invite_only_mode_enabled&quot;: false, &quot;slow_mode_min_duration_sec&quot;: 1, &quot;chat_members_only_enabled&quot;: false, &quot;slow_mode_max_duration_sec&quot;: 300, &quot;super_chat_for_good_enabled&quot;: false, &quot;slow_mode_enabled&quot;: false, &quot;slow_mode_timeout_duration_sec&quot;: 60, &quot;block_spam&quot;: true}" class="ng-untouched ng-valid ng-not-empty ng-valid-parse ng-valid-maxlength ng-pristine">
          <label class="chat-options-checkbox-label">
      <span class="yt-uix-form-input-checkbox-container slow-mode-enabled-checkbox"><input type="checkbox" class="yt-uix-form-input-checkbox ng-pristine ng-untouched ng-valid ng-empty" data-ng-model="chatOptions.slowModeEnabled"><span class="yt-uix-form-input-checkbox-element"></span></span>
      <span class="slow-mode-enabled-label">
        低速モードを有効にする
      </span>
        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="-4537410281162749609">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-4537410281162749609">低速モードにすると、チャット参加者がメッセージを投稿できる間隔を指定できます。
      </span>
    </span>
  </span>


    </label>
      <label class="slow-mode-duration-label metadata-disabled" ng-class="{'metadata-disabled': !chatOptions.slowModeEnabled}">
チャットを投稿できる間隔を 1 人あたり   <span class="yt-uix-form-input-container yt-uix-form-input-text-container" ng-class="{'yt-uix-form-error': !chatOptions.isSlowModeDurationValid()}">
    <input class="yt-uix-form-input-text slow-mode-timeout-duration ng-pristine ng-untouched ng-valid ng-not-empty ng-valid-maxlength" maxlength="3" ng-disabled="!chatOptions.slowModeEnabled" ng-model="chatOptions.slowModeTimeoutDurationSec" disabled="disabled">
  </span>
 秒に制限する
    <span class="yt-uix-form-error ng-hide" ng-show="!chatOptions.isSlowModeDurationValid()">
       <span class="yt-uix-form-error-message" role="alert">
最小値: 1 秒。最大値: 300 秒。
      </span>
    </span>
  </label>

  <label class="chat-options-checkbox-label">
    <span class="yt-uix-form-input-checkbox-container block-spam-checkbox checked"><input type="checkbox" class="yt-uix-form-input-checkbox ng-pristine ng-untouched ng-valid ng-not-empty" data-ng-model="chatOptions.blockSpam"><span class="yt-uix-form-input-checkbox-element"></span></span>
    <span class="block-spam-label">
      スパム メッセージを自動的にブロック
    </span>
      
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="-5052646796119995325">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-5052646796119995325">すべて大文字で書かれた長文のメッセージや、同じユーザーから同じメッセージが繰り返し送られるなどの単純なスパムが自動的にブロックされます。
      </span>
    </span>
  </span>


  </label>


  </ng-form>

  </div>




    <div data-ng-non-bindable="">
      


  <div class="metadata-syndication-settings metadata-syndication-disabled">

    <h3 class="metadata-syndication-disabled syndication-header">シンジケーション</h3>

    <label class="single-option metadata-syndication-label">
          <span class="yt-uix-form-input-radio-container all-platforms checked"><input type="radio" class="yt-uix-form-input-radio" name="syndication" checked="checked" value="everywhere" disabled=""><span class="yt-uix-form-input-radio-element"></span></span>


すべてのプラットフォーム
    </label>
    <span class="metadata-syndication-option-desc">
      <ul>
        <li>この動画をすべてのプラットフォームで再生できるようにする</li>
      </ul>
    </span>

      <label class="single-option metadata-syndication-label">
            <span class="yt-uix-form-input-radio-container monetized-platform"><input type="radio" class="yt-uix-form-input-radio" name="syndication" value="monetized" disabled=""><span class="yt-uix-form-input-radio-element"></span></span>


収益化対象のプラットフォーム
      </label>
      <span class="metadata-syndication-option-desc">
この動画を収益化対象のプラットフォームでのみ再生できるようにする
          
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="-5771535832877673377">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-5771535832877673377">収益化対象のプラットフォームには、youtube.com や Android 上の YouTube アプリなどがあります。
        <a target="_new" href="//support.google.com/youtube/bin/answer.py?answer=2453875&amp;hl=ja">
詳細
        </a>
      </span>
    </span>
  </span>


      </span>

  </div>


      <div class="advanced-tab-option">
  <h3>
    <label for="captions_certificate_reason">
      字幕の認定

    </label>
        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="3564922981354075145">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="3564922981354075145">米国のテレビで放送されたコンテンツは、字幕に関する FCC 規則の対象となることがあります。必要に応じてここで字幕の認定を行います。
      </span>
    </span>
  </span>



  </h3>
  <span class="yt-uix-form-input-select captions-certification-select"><span class="yt-uix-form-input-select-content"><span class="yt-uix-form-input-select-arrow yt-sprite"></span><span class="yt-uix-form-input-select-value">いずれかを選択
</span></span><select class="yt-uix-form-input-select-element " name="captions_certificate_reason" id="captions_certificate_reason">
    <option value="" arg="" selected="">いずれかを選択
</option>
      <option value="1">このコンテンツは米国内のテレビで放送されたことはありません。
</option>
      <option value="2">このコンテンツは米国内のテレビで字幕なしでのみ放送されました。
</option>
      <option value="3">このコンテンツは 2012 年 9 月 30 日以降米国内のテレビで字幕付きでは放送されていません。
</option>
      <option value="4">このコンテンツは動画番組全体を収録したものではありません。
</option>
      <option value="5">このコンテンツは、FCC の規則（47 C.F.R. § 79）で定められた、現在字幕が必要なオンライン プログラムのカテゴリに含まれません。
</option>
      <option value="6">FCC または米国議会で、このコンテンツは字幕要件の例外と認められています。
</option>
</select></span>
</div>


        <h3 class="embedding-header">
配信オプション
  </h3>

    <div class=" advanced-tab-option">
    <label>
        <span class="yt-uix-form-input-checkbox-container  checked"><input type="checkbox" class="yt-uix-form-input-checkbox video-settings-allow-embedding mde-checkbox" name="allow_embedding" checked="checked"><span class="yt-uix-form-input-checkbox-element"></span></span>

埋め込みを許可
    </label>
        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="7198689480108513356">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="7198689480108513356">他のサイトへの動画の埋め込みを許可します。
        <a target="_new" href="//support.google.com/youtube/bin/answer.py?answer=171780&amp;hl=ja">
詳細
        </a>
      </span>
    </span>
  </span>



  </div>



      <div class="advanced-tab-option">
    <label>
        <span class="yt-uix-form-input-checkbox-container  checked"><input type="checkbox" class="yt-uix-form-input-checkbox sharing-checkbox-feeds mde-checkbox" name="creator_share_feeds" checked="checked"><span class="yt-uix-form-input-checkbox-element"></span></span>

[登録チャンネル] フィードに公開してチャンネル登録者に通知
    </label>
      
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topright" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="1687721873072532490">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="1687721873072532490">チャンネル登録者の [登録チャンネル] フィードにこの動画が表示され、通知を受信するよう選択している場合は通知が届きます。
      </span>
    </span>
  </span>


  </div>



      
    <h3 class="age-restrictions-header">
年齢制限
  </h3>

  <div class="advanced-tab-option">
    <label>
        <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox video-settings-self-racy mde-checkbox" name="self_racy" data-initial-value="no"><span class="yt-uix-form-input-checkbox-element"></span></span>


年齢制限を有効にする
    </label>
        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="3804618781795217027">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="3804618781795217027">未成年者はこの動画を視聴できなくなります。また、収益の受け取りや、さまざまな広告フォーマットでの動画の宣伝もできなくなります。
        <a target="_new" href="//support.google.com/youtube/go/age_restrictions?hl=ja">
詳細
        </a>
      </span>
    </span>
  </span>


  </div>



    </div>
  </div>

    <div class="metadata-column-half">

    <label class="single-field">
      カテゴリ

      <div>
          <ng-form name="category" data-category="" data-ng-model="metadata.category" data-initial-category="{&quot;options&quot;: {&quot;1&quot;: &quot;\u6620\u753b\u3068\u30a2\u30cb\u30e1&quot;, &quot;2&quot;: &quot;\u81ea\u52d5\u8eca\u3068\u4e57\u308a\u7269&quot;, &quot;10&quot;: &quot;\u97f3\u697d&quot;, &quot;15&quot;: &quot;\u30da\u30c3\u30c8\u3068\u52d5\u7269&quot;, &quot;17&quot;: &quot;\u30b9\u30dd\u30fc\u30c4&quot;, &quot;19&quot;: &quot;\u65c5\u884c\u3068\u30a4\u30d9\u30f3\u30c8&quot;, &quot;20&quot;: &quot;\u30b2\u30fc\u30e0&quot;, &quot;22&quot;: &quot;\u30d6\u30ed\u30b0&quot;, &quot;23&quot;: &quot;\u30b3\u30e1\u30c7\u30a3\u30fc&quot;, &quot;24&quot;: &quot;\u30a8\u30f3\u30bf\u30fc\u30c6\u30a4\u30e1\u30f3\u30c8&quot;, &quot;25&quot;: &quot;\u30cb\u30e5\u30fc\u30b9\u3068\u653f\u6cbb&quot;, &quot;26&quot;: &quot;\u30cf\u30a6\u30c4\u30fc\u3068\u30b9\u30bf\u30a4\u30eb&quot;, &quot;27&quot;: &quot;\u6559\u80b2&quot;, &quot;28&quot;: &quot;\u79d1\u5b66\u3068\u6280\u8853&quot;, &quot;29&quot;: &quot;\u975e\u55b6\u5229\u56e3\u4f53\u3068\u793e\u4f1a\u6d3b\u52d5&quot;}, &quot;category&quot;: &quot;22&quot;}" class="ng-untouched ng-valid ng-not-empty ng-valid-parse ng-pristine">
      <span class="yt-uix-form-input-select"><span class="yt-uix-form-input-select-content"><span class="yt-uix-form-input-select-arrow yt-sprite"></span><span class="yt-uix-form-input-select-value">ブログ</span></span><select class="yt-uix-form-input-select-element  ng-pristine ng-untouched ng-valid ng-not-empty" data-ng-options="value as label for (value, label) in options" data-ng-model="category"><option label="映画とアニメ" value="string:1">映画とアニメ</option><option label="自動車と乗り物" value="string:2">自動車と乗り物</option><option label="音楽" value="string:10">音楽</option><option label="ペットと動物" value="string:15">ペットと動物</option><option label="スポーツ" value="string:17">スポーツ</option><option label="旅行とイベント" value="string:19">旅行とイベント</option><option label="ゲーム" value="string:20">ゲーム</option><option label="ブログ" value="string:22" selected="selected">ブログ</option><option label="コメディー" value="string:23">コメディー</option><option label="エンターテイメント" value="string:24">エンターテイメント</option><option label="ニュースと政治" value="string:25">ニュースと政治</option><option label="ハウツーとスタイル" value="string:26">ハウツーとスタイル</option><option label="教育" value="string:27">教育</option><option label="科学と技術" value="string:28">科学と技術</option><option label="非営利団体と社会活動" value="string:29">非営利団体と社会活動</option></select></span>

  </ng-form>

          <ng-form name="game_title" data-game-title="" class="game-title-picker ng-untouched ng-valid ng-not-empty ng-valid-parse ng-hide ng-valid-maxlength ng-pristine" data-ng-model="metadata.game_title" data-ng-show="metadata.category == '20'" data-initial-encoded-title="">
      <span class=" yt-uix-form-input-container yt-uix-form-input-text-container "><input class="yt-uix-form-input-text game-title-widget ng-pristine ng-untouched ng-valid jfk-textinput ng-empty ng-valid-maxlength" placeholder="ゲームタイトル（推奨）
" maxlength="100" data-ng-change="ctrl.clearMid()" data-jfk-on-dismiss="ctrl.onBlur()" data-jfk-on-update="ctrl.addTitleInfo($event)" data-ng-model="gameTitleInfo.title" data-url="'/game_suggestions_ajax?action_get_game_suggestions=1'" data-jfk-autocomplete="" data-custom-renderer="ctrl.autoCompleteCustomRenderer" autocomplete="off" role="combobox" aria-autocomplete="list"></span>

  </ng-form>

      </div>
    </label>
    <div data-ng-non-bindable="">
        <label>
          <h3 class="audio-language-header">動画の言語</h3>
            



  <div class="yt-languagepicker ">
    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default yt-languagepicker-button" type="button" onclick=";return false;" data-button-menu-id="yt-languagepicker-menu-audio_language" data-default-button-text="言語を選択" data-button-menu-indicate-selected="true"><span class="yt-uix-button-content">言語を選択</span><span class="yt-uix-button-arrow yt-sprite"></span></button>
    <input type="text" id="yt-languagepicker-input-audio_language" name="audio_language" value="" class="hid">
      <div class="yt-uix-button-menu yt-uix-languagepicker yt-uix-languagepicker-menu hid loading" tabindex="0" id="yt-languagepicker-menu-audio_language" data-fallback-option="und" data-languagepicker-input-id="yt-languagepicker-input-audio_language">
      <ul class="yt-uix-languagepicker-language-list yt-uix-languagepicker-suggestions" role="menu">
        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ja">
    <span class="yt-uix-button-menu-item">日本語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="zxx">
    <span class="yt-uix-button-menu-item">該当なし</span>
  </li>

  </ul>



      <div class="yt-uix-languagepicker-search-container">
        <div class="yt-uix-languagepicker-search-input-container">
          <span class=" yt-uix-form-input-container yt-uix-form-input-text-container  yt-uix-form-input-fluid-container"><span class=" yt-uix-form-input-fluid"><input class="yt-uix-form-input-text yt-uix-languagepicker-search-input mde-ignore-change" placeholder="他の 192 件の言語を検索"></span></span>
        </div>

          <ul class="yt-uix-languagepicker-language-list yt-uix-languagepicker-search-result hid" role="menu">
        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sdp">
    <span class="yt-uix-button-menu-item">Sherdukpen</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="is">
    <span class="yt-uix-button-menu-item">アイスランド語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ay">
    <span class="yt-uix-button-menu-item">アイマラ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ga">
    <span class="yt-uix-button-menu-item">アイルランド語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="az">
    <span class="yt-uix-button-menu-item">アゼルバイジャン語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="as">
    <span class="yt-uix-button-menu-item">アッサム語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="aa">
    <span class="yt-uix-button-menu-item">アファル語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ab">
    <span class="yt-uix-button-menu-item">アブハズ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="af">
    <span class="yt-uix-button-menu-item">アフリカーンス語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="am">
    <span class="yt-uix-button-menu-item">アムハラ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ase">
    <span class="yt-uix-button-menu-item">アメリカ手話</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ar">
    <span class="yt-uix-button-menu-item">アラビア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="arc">
    <span class="yt-uix-button-menu-item">アラム語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sq">
    <span class="yt-uix-button-menu-item">アルバニア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="hy">
    <span class="yt-uix-button-menu-item">アルメニア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="it">
    <span class="yt-uix-button-menu-item">イタリア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="yi">
    <span class="yt-uix-button-menu-item">イディッシュ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="iu">
    <span class="yt-uix-button-menu-item">イヌクティトット語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ik">
    <span class="yt-uix-button-menu-item">イヌピアック語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ig">
    <span class="yt-uix-button-menu-item">イボ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ie">
    <span class="yt-uix-button-menu-item">インターリング</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ia">
    <span class="yt-uix-button-menu-item">インターリングア</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="id">
    <span class="yt-uix-button-menu-item">インドネシア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="cy">
    <span class="yt-uix-button-menu-item">ウェールズ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="vo">
    <span class="yt-uix-button-menu-item">ヴォラピュク語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="wo">
    <span class="yt-uix-button-menu-item">ウォロフ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="uk">
    <span class="yt-uix-button-menu-item">ウクライナ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="uz">
    <span class="yt-uix-button-menu-item">ウズベク語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ur">
    <span class="yt-uix-button-menu-item">ウルドゥー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="et">
    <span class="yt-uix-button-menu-item">エストニア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="eo">
    <span class="yt-uix-button-menu-item">エスペラント語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="oc">
    <span class="yt-uix-button-menu-item">オック語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="or">
    <span class="yt-uix-button-menu-item">オディア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="nl">
    <span class="yt-uix-button-menu-item">オランダ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="nl-NL">
    <span class="yt-uix-button-menu-item">オランダ語 (オランダ)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="nl-BE">
    <span class="yt-uix-button-menu-item">オランダ語 (ベルギー)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="om">
    <span class="yt-uix-button-menu-item">オロモ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="kk">
    <span class="yt-uix-button-menu-item">カザフ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ks">
    <span class="yt-uix-button-menu-item">カシミール語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ca">
    <span class="yt-uix-button-menu-item">カタロニア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="gl">
    <span class="yt-uix-button-menu-item">ガリシア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="kn">
    <span class="yt-uix-button-menu-item">カンナダ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="rw">
    <span class="yt-uix-button-menu-item">キニアルワンダ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="el">
    <span class="yt-uix-button-menu-item">ギリシャ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ky">
    <span class="yt-uix-button-menu-item">キルギス語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="gn">
    <span class="yt-uix-button-menu-item">グアラニー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="gu">
    <span class="yt-uix-button-menu-item">グジャラート語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="km">
    <span class="yt-uix-button-menu-item">クメール語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="kl">
    <span class="yt-uix-button-menu-item">グリーンランド語（カラーリット語）</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="tlh">
    <span class="yt-uix-button-menu-item">クリンゴン語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ku">
    <span class="yt-uix-button-menu-item">クルド語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="hr">
    <span class="yt-uix-button-menu-item">クロアチア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="qu">
    <span class="yt-uix-button-menu-item">ケチュア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="xh">
    <span class="yt-uix-button-menu-item">コサ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="co">
    <span class="yt-uix-button-menu-item">コルシカ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sm">
    <span class="yt-uix-button-menu-item">サモア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sc">
    <span class="yt-uix-button-menu-item">サルデーニャ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sg">
    <span class="yt-uix-button-menu-item">サンゴ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sa">
    <span class="yt-uix-button-menu-item">サンスクリット語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="scn">
    <span class="yt-uix-button-menu-item">シチリア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="jv">
    <span class="yt-uix-button-menu-item">ジャワ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ka">
    <span class="yt-uix-button-menu-item">ジョージア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sn">
    <span class="yt-uix-button-menu-item">ショナ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sd">
    <span class="yt-uix-button-menu-item">シンド語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="si">
    <span class="yt-uix-button-menu-item">シンハラ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sv">
    <span class="yt-uix-button-menu-item">スウェーデン語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="zu">
    <span class="yt-uix-button-menu-item">ズールー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="gd">
    <span class="yt-uix-button-menu-item">スコットランド・ゲール語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="es">
    <span class="yt-uix-button-menu-item">スペイン語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="es-US">
    <span class="yt-uix-button-menu-item">スペイン語 (アメリカ合衆国)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="es-ES">
    <span class="yt-uix-button-menu-item">スペイン語 (スペイン)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="es-MX">
    <span class="yt-uix-button-menu-item">スペイン語 (メキシコ)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="es-419">
    <span class="yt-uix-button-menu-item">スペイン語 (ラテンアメリカ)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sk">
    <span class="yt-uix-button-menu-item">スロバキア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sl">
    <span class="yt-uix-button-menu-item">スロベニア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ss">
    <span class="yt-uix-button-menu-item">スワジ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sw">
    <span class="yt-uix-button-menu-item">スワヒリ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="su">
    <span class="yt-uix-button-menu-item">スンダ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sr">
    <span class="yt-uix-button-menu-item">セルビア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sr-Cyrl">
    <span class="yt-uix-button-menu-item">セルビア語 (キリル文字)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sr-Latn">
    <span class="yt-uix-button-menu-item">セルビア語 (ラテン文字)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="sh">
    <span class="yt-uix-button-menu-item">セルボ・クロアチア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="so">
    <span class="yt-uix-button-menu-item">ソマリ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="dz">
    <span class="yt-uix-button-menu-item">ゾンカ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="th">
    <span class="yt-uix-button-menu-item">タイ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="tl">
    <span class="yt-uix-button-menu-item">タガログ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="tg">
    <span class="yt-uix-button-menu-item">タジク語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="tt">
    <span class="yt-uix-button-menu-item">タタール語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ta">
    <span class="yt-uix-button-menu-item">タミル語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="cs">
    <span class="yt-uix-button-menu-item">チェコ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="chr">
    <span class="yt-uix-button-menu-item">チェロキー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="bo">
    <span class="yt-uix-button-menu-item">チベット語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="cho">
    <span class="yt-uix-button-menu-item">チョクトー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ts">
    <span class="yt-uix-button-menu-item">ツォンガ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="tn">
    <span class="yt-uix-button-menu-item">ツワナ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ti">
    <span class="yt-uix-button-menu-item">ティグリニア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="te">
    <span class="yt-uix-button-menu-item">テルグ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="da">
    <span class="yt-uix-button-menu-item">デンマーク語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="de">
    <span class="yt-uix-button-menu-item">ドイツ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="de-AT">
    <span class="yt-uix-button-menu-item">ドイツ語 (オーストリア)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="de-CH">
    <span class="yt-uix-button-menu-item">ドイツ語 (スイス)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="de-DE">
    <span class="yt-uix-button-menu-item">ドイツ語 (ドイツ)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="tw">
    <span class="yt-uix-button-menu-item">トウィ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="tk">
    <span class="yt-uix-button-menu-item">トルクメン語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="tr">
    <span class="yt-uix-button-menu-item">トルコ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="to">
    <span class="yt-uix-button-menu-item">トンガ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="na">
    <span class="yt-uix-button-menu-item">ナウル語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="nv">
    <span class="yt-uix-button-menu-item">ナバホ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ne">
    <span class="yt-uix-button-menu-item">ネパール語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="no">
    <span class="yt-uix-button-menu-item">ノルウェイ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ha">
    <span class="yt-uix-button-menu-item">ハウサ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ba">
    <span class="yt-uix-button-menu-item">バシキール語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ps">
    <span class="yt-uix-button-menu-item">パシュトゥー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="eu">
    <span class="yt-uix-button-menu-item">バスク語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="hu">
    <span class="yt-uix-button-menu-item">ハンガリー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="pa">
    <span class="yt-uix-button-menu-item">パンジャブ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="bi">
    <span class="yt-uix-button-menu-item">ビスラマ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="bh">
    <span class="yt-uix-button-menu-item">ビハール語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="hi">
    <span class="yt-uix-button-menu-item">ヒンディー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="hi-Latn">
    <span class="yt-uix-button-menu-item">ヒンディー語（表音）</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fj">
    <span class="yt-uix-button-menu-item">フィジー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fil">
    <span class="yt-uix-button-menu-item">フィリピノ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fi">
    <span class="yt-uix-button-menu-item">フィンランド語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fo">
    <span class="yt-uix-button-menu-item">フェロー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fr">
    <span class="yt-uix-button-menu-item">フランス語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fr-CA">
    <span class="yt-uix-button-menu-item">フランス語 (カナダ)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fr-CH">
    <span class="yt-uix-button-menu-item">フランス語 (スイス)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fr-FR">
    <span class="yt-uix-button-menu-item">フランス語 (フランス)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fr-BE">
    <span class="yt-uix-button-menu-item">フランス語 (ベルギー)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ff">
    <span class="yt-uix-button-menu-item">フラ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="bg">
    <span class="yt-uix-button-menu-item">ブルガリア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="br">
    <span class="yt-uix-button-menu-item">ブルトン語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="vi">
    <span class="yt-uix-button-menu-item">ベトナム語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="iw">
    <span class="yt-uix-button-menu-item">ヘブライ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="be">
    <span class="yt-uix-button-menu-item">ベラルーシ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fa">
    <span class="yt-uix-button-menu-item">ペルシア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fa-AF">
    <span class="yt-uix-button-menu-item">ペルシア語 (アフガニスタン)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fa-IR">
    <span class="yt-uix-button-menu-item">ペルシア語 (イラン)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="bn">
    <span class="yt-uix-button-menu-item">ベンガル語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="pl">
    <span class="yt-uix-button-menu-item">ポーランド語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="bs">
    <span class="yt-uix-button-menu-item">ボスニア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="pt">
    <span class="yt-uix-button-menu-item">ポルトガル語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="pt-BR">
    <span class="yt-uix-button-menu-item">ポルトガル語 (ブラジル)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="pt-PT">
    <span class="yt-uix-button-menu-item">ポルトガル語 (ポルトガル)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="mi">
    <span class="yt-uix-button-menu-item">マオリ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="mk">
    <span class="yt-uix-button-menu-item">マケドニア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="mas">
    <span class="yt-uix-button-menu-item">マサイ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="mg">
    <span class="yt-uix-button-menu-item">マダガスカル語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="mni">
    <span class="yt-uix-button-menu-item">マニプリ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="mr">
    <span class="yt-uix-button-menu-item">マラーティー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ml">
    <span class="yt-uix-button-menu-item">マラヤーラム語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="mt">
    <span class="yt-uix-button-menu-item">マルタ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ms">
    <span class="yt-uix-button-menu-item">マレー語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="lus">
    <span class="yt-uix-button-menu-item">ミゾ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="my">
    <span class="yt-uix-button-menu-item">ミャンマー（ビルマ）語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="mo">
    <span class="yt-uix-button-menu-item">モルダビア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="mn">
    <span class="yt-uix-button-menu-item">モンゴル語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="yo">
    <span class="yt-uix-button-menu-item">ヨルバ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="lo">
    <span class="yt-uix-button-menu-item">ラオ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="la">
    <span class="yt-uix-button-menu-item">ラテン語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="lv">
    <span class="yt-uix-button-menu-item">ラトビア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="lt">
    <span class="yt-uix-button-menu-item">リトアニア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ln">
    <span class="yt-uix-button-menu-item">リンガラ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ro">
    <span class="yt-uix-button-menu-item">ルーマニア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="lb">
    <span class="yt-uix-button-menu-item">ルクセンブルク語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="rn">
    <span class="yt-uix-button-menu-item">ルンディ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ru">
    <span class="yt-uix-button-menu-item">ロシア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ru-Latn">
    <span class="yt-uix-button-menu-item">ロシア語（表音）</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="rm">
    <span class="yt-uix-button-menu-item">ロマンシュ語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="en">
    <span class="yt-uix-button-menu-item">英語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="en-IE">
    <span class="yt-uix-button-menu-item">英語 (アイルランド)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="en-US">
    <span class="yt-uix-button-menu-item">英語 (アメリカ合衆国)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="en-GB">
    <span class="yt-uix-button-menu-item">英語 (イギリス)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="en-CA">
    <span class="yt-uix-button-menu-item">英語 (カナダ)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ko">
    <span class="yt-uix-button-menu-item">韓国語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="hak">
    <span class="yt-uix-button-menu-item">客家語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="hak-TW">
    <span class="yt-uix-button-menu-item">客家語 (台湾)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="yue">
    <span class="yt-uix-button-menu-item">広東語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="yue-HK">
    <span class="yt-uix-button-menu-item">広東語 (香港)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="fy">
    <span class="yt-uix-button-menu-item">西フリジア語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="zh">
    <span class="yt-uix-button-menu-item">中国語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="zh-SG">
    <span class="yt-uix-button-menu-item">中国語 (シンガポール)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="zh-Hans">
    <span class="yt-uix-button-menu-item">中国語 (簡体字)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="zh-HK">
    <span class="yt-uix-button-menu-item">中国語 (香港)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="zh-TW">
    <span class="yt-uix-button-menu-item">中国語 (台湾)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="zh-CN">
    <span class="yt-uix-button-menu-item">中国語 (中国)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="zh-Hant">
    <span class="yt-uix-button-menu-item">中国語 (繁体字)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="st">
    <span class="yt-uix-button-menu-item">南部ソト語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="ja">
    <span class="yt-uix-button-menu-item">日本語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="nan">
    <span class="yt-uix-button-menu-item">閩南語</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="nan-TW">
    <span class="yt-uix-button-menu-item">閩南語 (台湾)</span>
  </li>

        <li class="yt-uix-languagepicker-menu-item " role="menuitem" data-value="und">
    <span class="yt-uix-button-menu-item">その他</span>
  </li>

  </ul>

      </div>
  </div>

  </div>


        </label>

          <div class="advanced-tab-option">
            <h3>視聴者への翻訳依頼</h3>
            <label>
                  <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox video-settings-captions_crowdsource mde-checkbox" name="captions_crowdsource" data-initial-value="no"><span class="yt-uix-form-input-checkbox-element"></span></span>


視聴者によって翻訳されたタイトル、説明、字幕の提供を許可する
            </label>
              
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="-4341174965751984748">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-4341174965751984748">YouTube ユーザーに動画の翻訳を呼びかけ、より多くの言語で視聴者に楽しんでもらいましょう。<a href="https://support.google.com/youtube/?p=subtitles&amp;hl=ja">詳細</a>
      </span>
    </span>
  </span>


          </div>

      <label>
        <h3 class="recording-date-header">録画日</h3>                          <span class="recorded-date-formatted yt-uix-form-input-container yt-uix-form-input-text-container "><input class="yt-uix-form-input-text recorded-date-formatted-input" value=""></span>
      </label>

      <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default recorded-date-today-button" type="button" onclick=";return false;"><span class="yt-uix-button-content">今日</span></button>
      <input name="recorded_date" type="hidden" class="recorded-date" value="">

        <h3 class="videostats-header">動画の統計情報</h3>

  <div class="advanced-tab-option">
    <label>
          <span class="yt-uix-form-input-checkbox-container  checked"><input type="checkbox" class="yt-uix-form-input-checkbox video-settings-allow-public-stats mde-checkbox" name="allow_public_stats" checked="checked" data-initial-value=""><span class="yt-uix-form-input-checkbox-element"></span></span>


動画再生ページでの動画統計情報を一般公開する
    </label>
        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細はこちら" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細はこちら" aria-describedby="4891350099188259652">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="4891350099188259652">動画再生ページでの動画統計情報にアクセスするには、動画の下のグラフ アイコンをクリックしてください。
      </span>
    </span>
  </span>



  </div>

    </div>

      <h3 class="threed-header">
3D 動画
      </h3>
        <p>
3D チェックボックスのサポートは終了しました。3D ファイルのアップロード用のメタデータを指定する方法については、<a href="//support.google.com/youtube/answer/7278886?hl=ja">こちらのヘルプセンター記事</a>をご覧ください。
  </p>


        <h3>コンテンツに関する申告</h3>
        <script id="productplacements.html" type="text/ng-template">
      <div class="product_placement advanced-tab-option paid-product-placement-widget">
    <label>
      <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-click="onHasPppClick()" data-ng-model="product.has_paid_product_placement"><span class="yt-uix-form-input-checkbox-element"></span></span>
この動画には、有料プロモーション（制作のスポンサー支援、スポンサーから対価を得て製品やサービスを紹介したりすすめたりすることなど）が含まれています。
    </label>

      
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細" aria-describedby="-4510307206842045335">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-4510307206842045335">クリエーターは、自身のコンテンツに第三者のためのプロモーションを含めてそれにより対価を受け取ることができます（「有料プロモーション」）。すべての有料のプロモーションは、YouTube の広告ポリシーおよび適用される法律を遵守しなければなりません。
        <a target="_new" href="//support.google.com/youtube/?p=paid_product_placement&amp;hl=ja">
詳細
        </a>
      </span>
    </span>
  </span>



    <div data-ng-show="product.has_paid_product_placement">
      <label>
        <span class="yt-uix-form-input-checkbox-container "><input type="checkbox" class="yt-uix-form-input-checkbox" data-ng-model="product.show_paid_product_placement_overlay"><span class="yt-uix-form-input-checkbox-element"></span></span>

この動画に有料プロモーションについての情報を追加して視聴者に開示します。適用される法律によって開示情報の追加を求められる場合があります。
      </label>

        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細" aria-describedby="-7937292768905987434">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-7937292768905987434">動画に「有料プロモーションを含みます」という情報開示テキストが表示されます。適用される法律によって、有料プロモーション コンテンツ（プロダクト プレースメントやおすすめ情報など）について音声またはテキストでさらに開示するよう求められる場合があります。
        <a target="_new" href="//support.google.com/youtube/?p=paid_product_placement&amp;hl=ja">
詳細
        </a>
      </span>
    </span>
  </span>


    </div>
  </div>

  </script>


      <ng-form name="product_placement" data-product-placements="" data-initial-paid-product="{&quot;show_paid_product_placement_overlay&quot;:false,&quot;has_paid_product_placement&quot;:false}" data-ng-model="metadata.product_placement" class="ng-untouched ng-valid ng-not-empty ng-valid-parse ng-pristine">
      <div class="product_placement advanced-tab-option paid-product-placement-widget">
    <label>
      <span class="yt-uix-form-input-checkbox-container"><input type="checkbox" class="yt-uix-form-input-checkbox ng-pristine ng-untouched ng-valid ng-empty" data-ng-click="onHasPppClick()" data-ng-model="product.has_paid_product_placement"><span class="yt-uix-form-input-checkbox-element"></span></span>
この動画には、有料プロモーション（制作のスポンサー支援、スポンサーから対価を得て製品やサービスを紹介したりすすめたりすることなど）が含まれています。
    </label>

      
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細" aria-describedby="-4510307206842045335">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-4510307206842045335">クリエーターは、自身のコンテンツに第三者のためのプロモーションを含めてそれにより対価を受け取ることができます（「有料プロモーション」）。すべての有料のプロモーションは、YouTube の広告ポリシーおよび適用される法律を遵守しなければなりません。
        <a target="_new" href="//support.google.com/youtube/?p=paid_product_placement&amp;hl=ja">
詳細
        </a>
      </span>
    </span>
  </span>



    <div data-ng-show="product.has_paid_product_placement" class="ng-hide">
      <label>
        <span class="yt-uix-form-input-checkbox-container"><input type="checkbox" class="yt-uix-form-input-checkbox ng-pristine ng-untouched ng-valid ng-empty" data-ng-model="product.show_paid_product_placement_overlay"><span class="yt-uix-form-input-checkbox-element"></span></span>

この動画に有料プロモーションについての情報を追加して視聴者に開示します。適用される法律によって開示情報の追加を求められる場合があります。
      </label>

        
  <span class="yt-uix-clickcard">
    <button class="yt-uix-clickcard-target" aria-label="詳細" aria-haspopup="true" data-position="topleft" type="button">
      <span class="yt-help-icon yt-sprite"></span>
    </button>
    <span class="yt-uix-clickcard-content" role="dialog" aria-label="詳細" aria-describedby="-7937292768905987434">
      <button class="yt-uix-clickcard-close" aria-label="詳細ダイアログを閉じる"></button>
      <span id="-7937292768905987434">動画に「有料プロモーションを含みます」という情報開示テキストが表示されます。適用される法律によって、有料プロモーション コンテンツ（プロダクト プレースメントやおすすめ情報など）について音声またはテキストでさらに開示するよう求められる場合があります。
        <a target="_new" href="//support.google.com/youtube/?p=paid_product_placement&amp;hl=ja">
詳細
        </a>
      </span>
    </span>
  </span>


    </div>
  </div>

  </ng-form>



    <div data-ng-non-bindable="">
      
    </div>
    
  </div>


      </div>
    </div>

    </form>
    

  </div>

    </div>

    <div data-ng-non-bindable="">
        <div class="metadata-actions">
      <div class="save-cancel-buttons">

    <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default save-changes-button yt-uix-tooltip" type="button" onclick=";return false;"><span class="yt-uix-button-content">変更を保存</span></button>
  </div>

    <span class="save-error-message" aria-live="assertive"></span>
    <span class="metadata-left-footer">
      
    </span>
  </div>

        <div id="expandable-error-detail" class="hid">
    <span class="yt-uix-expander yt-uix-expander-collapsed">
      <span class="yt-uix-expander-head yt-uix-button-blue-text">詳細</span>
      <div class="yt-uix-expander-body">
        <p class="error-header">このエラーが引き続き表示される場合は<a href="//support.google.com/youtube/answer/4347644?hl=ja">フィードバックをお送りください</a>。その際にはこちらのコードをお知らせください。</p>
        <pre>__details__</pre>
      </div>
    </span>
  </div>

  <div id="inline-error-msg" class="hid">
    <div class="yt-alert yt-alert-naked yt-alert-error  inline-error">  <div class="yt-alert-icon">
    <span class="icon master-sprite yt-sprite"></span>
  </div>
<div class="yt-alert-content" role="alert">    <div class="yt-alert-message" tabindex="0">
            __message__
    </div>
</div></div>
  </div>
  <div id="alert-card-template" class="hid">
    <div class="yt-uix-hovercard alert-card" data-card-class="suggestion-hovercard">
      <div class="alert-card-target" data-position="topleft" data-orientation="vertical">
        <span class="alert-card-icon yt-sprite"></span>
      </div>
      <div class="yt-uix-hovercard-content alert-card-content">
      </div>
    </div>
  </div>

    </div>
  </div>

      </div></div>

      <div class="custom-message-indicator hid" style="display: none;">
        <div class="sharing-balloon-user">
            <span class="video-thumb  yt-thumb yt-thumb-34">
    <span class="yt-thumb-square">
      <span class="yt-thumb-clip">
        
  <img data-ytimg="1" aria-hidden="true" alt="" height="34" onload=";window.__ytRIL &amp;&amp; __ytRIL(this)" src="https://yt3.ggpht.com/-OVD_2VA9p-g/AAAAAAAAAAI/AAAAAAAAAAA/o00V4m-5h2U/s34-c-k-c0x00ffffff-no-rj-mo/photo.jpg" width="34">

        <span class="vertical-align"></span>
      </span>
    </span>
  </span>

        </div>
        <div class="sharing-balloon custom-message-preview">
          <div class="custom-message-preview-text"></div>
        </div>
      </div>

      <div class="expand-link" style="display: none;">
        <div class="expand-button">
          <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-text expand-button-text" type="button" onclick=";return false;"><span class="yt-uix-button-content">もっと見る</span></button>
        </div>
        <div class="upload-item-main-footer">
          <hr class="yt-horizontal-rule ">
        </div>
      </div>
    </div>

    <div class="sharing-indicators">
        <div class="sharing-indicator hid" data-network-name="twitter" style="display: none;">
          <span class="twitter-icon yt-sprite" title="この動画は Twitter で共有されます"></span>
        </div>
        <div class="sharing-indicator hid" data-network-name="gplus" style="display: none;">
          <span class="gplus-icon yt-sprite" title="この動画は Google+ で共有されます"></span>
        </div>
    </div>

    <div class="confirm-cancel-dialog">
        <div class="yt-dialog hid ">
    <div class="yt-dialog-base">
      <span class="yt-dialog-align"></span>
      <div class="yt-dialog-fg" role="dialog" tabindex="-1">
        <div class="yt-dialog-fg-content yt-dialog-show-content">
          <div class="yt-dialog-loading">
              <div class="yt-dialog-waiting-content">
      <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
読み込んでいます...
    </span>
  </p>

  </div>

          </div>
          <div class="yt-dialog-content">
            このアップロードをキャンセルしてもよろしいですか？

          </div>
          <div class="yt-dialog-working">
              <div class="yt-dialog-working-overlay"></div>
  <div class="yt-dialog-working-bubble">
    <div class="yt-dialog-waiting-content">
        <p class="yt-spinner ">
        <span class="yt-spinner-img  yt-sprite" title="読み込み中のアイコン"></span>

    <span class="yt-spinner-message">
        処理しています...
    </span>
  </p>

      </div>
  </div>

          </div>
<div class="yt-dialog-footer">  <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-default   yt-dialog-dismiss" type="button" onclick=";return false;"><span class="yt-uix-button-content">キャンセルしない</span></button>
  <button class="yt-uix-button yt-uix-button-size-default yt-uix-button-primary   yt-dialog-dismiss" type="button" onclick=";return false;" data-action="save"><span class="yt-uix-button-content">アップロードをキャンセル</span></button>
</div>        </div>
        <div class="yt-dialog-focus-trap" tabindex="0"></div>
      </div>
    </div>
  </div>

    </div>

    <div class="upload-item-message-close">
      <button class="yt-uix-close yt-close item-close"><span class="yt-close-img yt-sprite" title="閉じる"></span></button>
    </div>
  </div></div>
 */
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
		try {
			String videoDownloadUrl = getVideoDownloadUrl(driver, downloadObj);
			if (StringUtil.isBlank(videoDownloadUrl)) {
				NieUtil.log(logFile, "[ERROR][Video][Download]url=" + downloadObj.videoUrl);
				updateVideoStatus(downloadObj.uid, "dlfailure");
				return;
			}
			File saveFile = getVideoSaveFile(downloadObj);
			downLoadVideoFromUrl(videoDownloadUrl, saveFile);
			downloadObj.dlVideoPath = saveFile.getAbsolutePath();
			updateVideoStatus(downloadObj.uid, "dled");
		} catch (Exception e) {
			e.printStackTrace();
			updateVideoStatus(downloadObj.uid, "dlfailure");
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

		WebDriverWait wait1 = new WebDriverWait(driver,10);
		wait1 = new WebDriverWait(driver, 60);
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
		// TODO
		/*
<div id="video" style=""><div class="panel panel-success"><div class="panel-heading"><h3 class="panel-title">视频列表：</h3></div><ul class="list-group"><li class="list-group-item"><p class="input-group"><input name="search" id="video0" class="form-control get-text" value="http://f.us.sinaimg.cn/001LcXXnlx07tkBq1d3G01041200fgyo0E010.mp4?label=mp4_ld&amp;template=480x360.28.0&amp;Expires=1557421332&amp;ssig=S3YS2uCtrb&amp;KID=unistore,video" type="text"><span class="input-group-btn"><a href="javascript:void(0);" class="btn btn-info btnCopy" data-clipboard-action="copy" data-clipboard-target="#video0" onclick="ga('send', 'event', '220.216.10.248', 'copy', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');"><span class="glyphicon glyphicon-duplicate"></span></a><a href="http://f.us.sinaimg.cn/001LcXXnlx07tkBq1d3G01041200fgyo0E010.mp4?label=mp4_ld&amp;template=480x360.28.0&amp;Expires=1557421332&amp;ssig=S3YS2uCtrb&amp;KID=unistore,video" class="btn btn-primary get-dowmload" download="title-time.mp4" role="button" title="下载" onclick="ga('send', 'event', '220.216.10.248', 'download', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');"><span class="glyphicon glyphicon-save"></span></a><a href="http://f.us.sinaimg.cn/001LcXXnlx07tkBq1d3G01041200fgyo0E010.mp4?label=mp4_ld&amp;template=480x360.28.0&amp;Expires=1557421332&amp;ssig=S3YS2uCtrb&amp;KID=unistore,video" class="btn btn-success" role="button" title="播放" onclick="ga('send', 'event', '220.216.10.248', 'play', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');" data-toggle="lightbox" data-title="" data-type="video" data-width="1024"><span class="glyphicon glyphicon-play"></span></a><a class="btn btn-default btn-warning hidden-xs" title="扫码看视频" onclick="ga('send', 'event', 'video', 'qrcode', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');$.fancybox.open('<div id=\'qrcode\'></div>');$('#qrcode').qrcode({text:'http://f.us.sinaimg.cn/001LcXXnlx07tkBq1d3G01041200fgyo0E010.mp4?label=mp4_ld&amp;template=480x360.28.0&amp;Expires=1557421332&amp;ssig=S3YS2uCtrb&amp;KID=unistore,video'});"><span class="fa fa-qrcode"></span></a></span></p><p class="get-desc">简介：STREAM_URL: 不要再叫我老公了的微博视频</p></li><li class="list-group-item"><p class="input-group"><input name="search" id="video1" class="form-control get-text" value="http://f.us.sinaimg.cn/003gE1nPlx07tkBq7YP601041200o6oB0E010.mp4?label=mp4_hd&amp;template=640x480.28.0&amp;Expires=1557421332&amp;ssig=se42QDPYpI&amp;KID=unistore,video" type="text"><span class="input-group-btn"><a href="javascript:void(0);" class="btn btn-info btnCopy" data-clipboard-action="copy" data-clipboard-target="#video1" onclick="ga('send', 'event', '220.216.10.248', 'copy', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');"><span class="glyphicon glyphicon-duplicate"></span></a><a href="http://f.us.sinaimg.cn/003gE1nPlx07tkBq7YP601041200o6oB0E010.mp4?label=mp4_hd&amp;template=640x480.28.0&amp;Expires=1557421332&amp;ssig=se42QDPYpI&amp;KID=unistore,video" class="btn btn-primary get-dowmload" download="title-time.mp4" role="button" title="下载" onclick="ga('send', 'event', '220.216.10.248', 'download', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');"><span class="glyphicon glyphicon-save"></span></a><a href="http://f.us.sinaimg.cn/003gE1nPlx07tkBq7YP601041200o6oB0E010.mp4?label=mp4_hd&amp;template=640x480.28.0&amp;Expires=1557421332&amp;ssig=se42QDPYpI&amp;KID=unistore,video" class="btn btn-success" role="button" title="播放" onclick="ga('send', 'event', '220.216.10.248', 'play', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');" data-toggle="lightbox" data-title="" data-type="video" data-width="1024"><span class="glyphicon glyphicon-play"></span></a><a class="btn btn-default btn-warning hidden-xs" title="扫码看视频" onclick="ga('send', 'event', 'video', 'qrcode', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');$.fancybox.open('<div id=\'qrcode\'></div>');$('#qrcode').qrcode({text:'http://f.us.sinaimg.cn/003gE1nPlx07tkBq7YP601041200o6oB0E010.mp4?label=mp4_hd&amp;template=640x480.28.0&amp;Expires=1557421332&amp;ssig=se42QDPYpI&amp;KID=unistore,video'});"><span class="fa fa-qrcode"></span></a></span></p><p class="get-desc">简介：STREAM_URL_HD: 不要再叫我老公了的微博视频</p></li><li class="list-group-item"><p class="input-group"><input name="search" id="video2" class="form-control get-text" value="http://f.us.sinaimg.cn/001LcXXnlx07tkBq1d3G01041200fgyo0E010.mp4?label=mp4_ld&amp;template=480x360.28.0&amp;Expires=1557421332&amp;ssig=S3YS2uCtrb&amp;KID=unistore,video" type="text"><span class="input-group-btn"><a href="javascript:void(0);" class="btn btn-info btnCopy" data-clipboard-action="copy" data-clipboard-target="#video2" onclick="ga('send', 'event', '220.216.10.248', 'copy', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');"><span class="glyphicon glyphicon-duplicate"></span></a><a href="http://f.us.sinaimg.cn/001LcXXnlx07tkBq1d3G01041200fgyo0E010.mp4?label=mp4_ld&amp;template=480x360.28.0&amp;Expires=1557421332&amp;ssig=S3YS2uCtrb&amp;KID=unistore,video" class="btn btn-primary get-dowmload" download="title-time.mp4" role="button" title="下载" onclick="ga('send', 'event', '220.216.10.248', 'download', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');"><span class="glyphicon glyphicon-save"></span></a><a href="http://f.us.sinaimg.cn/001LcXXnlx07tkBq1d3G01041200fgyo0E010.mp4?label=mp4_ld&amp;template=480x360.28.0&amp;Expires=1557421332&amp;ssig=S3YS2uCtrb&amp;KID=unistore,video" class="btn btn-success" role="button" title="播放" onclick="ga('send', 'event', '220.216.10.248', 'play', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');" data-toggle="lightbox" data-title="" data-type="video" data-width="1024"><span class="glyphicon glyphicon-play"></span></a><a class="btn btn-default btn-warning hidden-xs" title="扫码看视频" onclick="ga('send', 'event', 'video', 'qrcode', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');$.fancybox.open('<div id=\'qrcode\'></div>');$('#qrcode').qrcode({text:'http://f.us.sinaimg.cn/001LcXXnlx07tkBq1d3G01041200fgyo0E010.mp4?label=mp4_ld&amp;template=480x360.28.0&amp;Expires=1557421332&amp;ssig=S3YS2uCtrb&amp;KID=unistore,video'});"><span class="fa fa-qrcode"></span></a></span></p><p class="get-desc">简介：MP4_SD_URL: 不要再叫我老公了的微博视频</p></li><li class="list-group-item"><p class="input-group"><input name="search" id="video3" class="form-control get-text" value="http://f.us.sinaimg.cn/003gE1nPlx07tkBq7YP601041200o6oB0E010.mp4?label=mp4_hd&amp;template=640x480.28.0&amp;Expires=1557421332&amp;ssig=se42QDPYpI&amp;KID=unistore,video" type="text"><span class="input-group-btn"><a href="javascript:void(0);" class="btn btn-info btnCopy" data-clipboard-action="copy" data-clipboard-target="#video3" onclick="ga('send', 'event', '220.216.10.248', 'copy', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');"><span class="glyphicon glyphicon-duplicate"></span></a><a href="http://f.us.sinaimg.cn/003gE1nPlx07tkBq7YP601041200o6oB0E010.mp4?label=mp4_hd&amp;template=640x480.28.0&amp;Expires=1557421332&amp;ssig=se42QDPYpI&amp;KID=unistore,video" class="btn btn-primary get-dowmload" download="title-time.mp4" role="button" title="下载" onclick="ga('send', 'event', '220.216.10.248', 'download', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');"><span class="glyphicon glyphicon-save"></span></a><a href="http://f.us.sinaimg.cn/003gE1nPlx07tkBq7YP601041200o6oB0E010.mp4?label=mp4_hd&amp;template=640x480.28.0&amp;Expires=1557421332&amp;ssig=se42QDPYpI&amp;KID=unistore,video" class="btn btn-success" role="button" title="播放" onclick="ga('send', 'event', '220.216.10.248', 'play', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');" data-toggle="lightbox" data-title="" data-type="video" data-width="1024"><span class="glyphicon glyphicon-play"></span></a><a class="btn btn-default btn-warning hidden-xs" title="扫码看视频" onclick="ga('send', 'event', 'video', 'qrcode', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');$.fancybox.open('<div id=\'qrcode\'></div>');$('#qrcode').qrcode({text:'http://f.us.sinaimg.cn/003gE1nPlx07tkBq7YP601041200o6oB0E010.mp4?label=mp4_hd&amp;template=640x480.28.0&amp;Expires=1557421332&amp;ssig=se42QDPYpI&amp;KID=unistore,video'});"><span class="fa fa-qrcode"></span></a></span></p><p class="get-desc">简介：MP4_HD_URL: 不要再叫我老公了的微博视频</p></li><li class="list-group-item"><p class="input-group"><input name="search" id="video4" class="form-control get-text" value="http://f.us.sinaimg.cn/000IXfsigx07tkBHQzJ601041200jy2G0E010.mp4?label=hevc_mp4_hd&amp;template=640x480.32.0&amp;Expires=1557421332&amp;ssig=o9OTZ6lGut&amp;KID=unistore,video" type="text"><span class="input-group-btn"><a href="javascript:void(0);" class="btn btn-info btnCopy" data-clipboard-action="copy" data-clipboard-target="#video4" onclick="ga('send', 'event', '220.216.10.248', 'copy', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');"><span class="glyphicon glyphicon-duplicate"></span></a><a href="http://f.us.sinaimg.cn/000IXfsigx07tkBHQzJ601041200jy2G0E010.mp4?label=hevc_mp4_hd&amp;template=640x480.32.0&amp;Expires=1557421332&amp;ssig=o9OTZ6lGut&amp;KID=unistore,video" class="btn btn-primary get-dowmload" download="title-time.mp4" role="button" title="下载" onclick="ga('send', 'event', '220.216.10.248', 'download', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');"><span class="glyphicon glyphicon-save"></span></a><a href="http://f.us.sinaimg.cn/000IXfsigx07tkBHQzJ601041200jy2G0E010.mp4?label=hevc_mp4_hd&amp;template=640x480.32.0&amp;Expires=1557421332&amp;ssig=o9OTZ6lGut&amp;KID=unistore,video" class="btn btn-success" role="button" title="播放" onclick="ga('send', 'event', '220.216.10.248', 'play', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');" data-toggle="lightbox" data-title="" data-type="video" data-width="1024"><span class="glyphicon glyphicon-play"></span></a><a class="btn btn-default btn-warning hidden-xs" title="扫码看视频" onclick="ga('send', 'event', 'video', 'qrcode', 'http://t.cn/EaW2rDA?m=4364617055065323&amp;u=5371249259');$.fancybox.open('<div id=\'qrcode\'></div>');$('#qrcode').qrcode({text:'http://f.us.sinaimg.cn/000IXfsigx07tkBHQzJ601041200jy2G0E010.mp4?label=hevc_mp4_hd&amp;template=640x480.32.0&amp;Expires=1557421332&amp;ssig=o9OTZ6lGut&amp;KID=unistore,video'});"><span class="fa fa-qrcode"></span></a></span></p><p class="get-desc">简介：HEVC_MP4_HD: 不要再叫我老公了的微博视频</p></li></ul></div></div>
		 */
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
		
		WebDriverWait wait1 = new WebDriverWait(driver,10);
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
