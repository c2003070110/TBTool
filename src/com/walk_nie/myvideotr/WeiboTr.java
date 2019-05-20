package com.walk_nie.myvideotr;

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
	String myfavURL = "https://www.weibo.com/like/outbox?leftnav=1";
	String myhomeURL = "https://www.weibo.com/u/1449729883/home";
	
	public static void main(String[] args) throws IOException {
		WeiboTr weibo = new WeiboTr();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		weibo.scan(driver);
	}
	
//	private void openUploadDialog(WebDriver driver){
//
//		List<WebElement> elesGNList = driver.findElements(By.cssSelector("div[class=\"gn_set_list\"]"));
//		for (WebElement ele : elesGNList) {
//			List<WebElement> eles1 = ele.findElements(By.tagName("a"));
//			boolean breakFlag = false;
//			for (WebElement ele1 : eles1) {
//				String att = "";
//				try{
//					att = ele1.getAttribute("node-type");
//				}catch(Exception e){}
//				if("publish".equals(att)){
//					ele1.click();
//					breakFlag = true;
//					break;
//				}
//			}
//			if(breakFlag)break;
//		}
//	}
//	private void closeUploadDialog(WebDriver driver) {
//		List<WebElement> elesGNList = driver.findElements(By.cssSelector("div[class=\"gn_set_list\"]"));
//		for (WebElement ele : elesGNList) {
//			List<WebElement> eles1 = ele.findElements(By.tagName("a"));
//			for (WebElement ele1 : eles1) {
//				String txt = ele1.getText();
//				String nodeType = ele1.getAttribute("node-type");
//				if(!StringUtil.isBlank(txt) &&txt.indexOf("X") != -1 && !StringUtil.isBlank(nodeType) &&nodeType.equals("close")){
//					ele1.click();
//				}
//			}
//		}
//	}
	public boolean publish(WebDriver driver, MyVideoObject uploadObj) throws IOException {

		List<File> multimediaContext = getToPublishFile(uploadObj);
		if(multimediaContext.isEmpty()){
			System.out.println("[INFO][publish]NONE File to publish");
			return true;
		}
		driver.get(myhomeURL);
		logonWeibo(driver);
		
		// open
//		WebDriverWait wait1 = new WebDriverWait(driver, 120);
//		wait1.until(new ExpectedCondition<Boolean>(){
//			@Override
//			public Boolean apply(WebDriver driver) {
//				try {
//					openUploadDialog(driver);
//					NieUtil.mySleepBySecond(2);
//					WebElement rootEl = findRootLayer(driver);
//					if (rootEl == null) {
//						return Boolean.FALSE;
//					} else {
//						//MyVideoTrUtil.stopBrowser(driver);
//						return Boolean.TRUE;
//					}
//				} catch (Exception e) {
//				}
//				return Boolean.FALSE;
//			}
//		});
		boolean rsltFlag = false;
		if(isVideoFile(multimediaContext.get(0))){
			rsltFlag = uploadVideo(driver, uploadObj, multimediaContext.get(0));
		}else{
			rsltFlag = uploadPhoto(driver, uploadObj, multimediaContext);
		}
//		try {
//			closeUploadDialog(driver);
//		} catch (Exception e) {
//		}
		System.out.println("[INFO][publish][Finish][uper]" + uploadObj.uper + "[uper]" + uploadObj.title);
		return rsltFlag;
		
	}

	private boolean uploadPhoto(WebDriver driver, MyVideoObject uploadObj,  List<File> files) throws IOException {

		WebElement rootEl = findRootLayer(driver);
		if(rootEl == null) {
			NieUtil.mySleepBySecond(3);
			rootEl = findRootLayer(driver);
		}
		
		WebElement weFile = findFileInputWebElement(driver);
		if(weFile == null) {
			System.out.println("[ERROR][uploadPhoto]It can NOT find file input");
			return false;
		}
		for (int i = 0; i < files.size(); i++) {
			weFile.sendKeys(files.get(i).getCanonicalPath());
			NieUtil.mySleepBySecond(3);
		}
		
		WebDriverWait wait1 = new WebDriverWait(driver,120);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					WebElement rootEl = findRootLayerPop(driver);
					if(rootEl == null) return Boolean.FALSE;
					List<WebElement> elspans = rootEl.findElements(By.className("layer_pic_list"));
					if(elspans == null) return Boolean.FALSE;
					List<WebElement> eles = elspans.get(0).findElements(By.tagName("li"));
					for (WebElement el : eles) {
						String txt = el.getAttribute("class").toLowerCase();
						if(!StringUtil.isBlank(txt) && txt.indexOf("loading") != -1){
							return Boolean.FALSE;
						}
					}
					return Boolean.TRUE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});

//		rootEl = findRootLayerPop(driver);
//		List<WebElement> eles = rootEl.findElements(By.cssSelector("a"));
//		for (WebElement el : eles) {
//			String txt = el.getText();
//			String nodeType = el.getAttribute("node-type");
//			if(txt.indexOf("X") != -1 && nodeType.equals("close")){
//				el.click();break;
//			}
//		}

		rootEl = findRootLayer(driver);
		List<WebElement> eles = rootEl.findElements(By.tagName("textarea"));
		String uper = uploadObj.uper;
		if(uper.length() < 6){
			uper += "----" + uper;
		}
		eles.get(0).sendKeys(uper);

		eles = rootEl.findElements(By.cssSelector("a[node-type=\"submit\"]"));
		for (WebElement el : eles) {
			String txt = el.getText();
			if(StringUtil.isBlank(txt))continue;
			String href = el.getAttribute("href");
			if(StringUtil.isBlank(href))continue;
			if(txt.indexOf("发布") != -1 && href.indexOf("javascript:void(0)") != -1){
				el.click();break;
			}
		}
		NieUtil.mySleepBySecond(3);
		return true;
	}
	
	private WebElement findFileInputWebElement(WebDriver driver) {
		WebDriverWait wait1 = new WebDriverWait(driver,120);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					WebElement rootEl = findRootLayer(driver);
					List<WebElement> elInputs = rootEl.findElements(By.tagName("input"));
					for (WebElement el : elInputs) {
						if (el.getAttribute("id").startsWith("swf_upbtn_")) {
							return Boolean.TRUE;
						}
					}
					//closeUploadDialog(driver);
					NieUtil.mySleepBySecond(3);
					//openUploadDialog(driver);
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		
		WebElement rootEl = findRootLayer(driver);
		List<WebElement> elInputs = rootEl.findElements(By.tagName("input"));
		WebElement weFileInput = null;
		for (WebElement el : elInputs) {
			if (el.getAttribute("id").startsWith("swf_upbtn_")) {
				weFileInput = el;
			}
		}
		return weFileInput;
	}

	private boolean uploadVideo(WebDriver driver, MyVideoObject uploadObj, File f) throws IOException {

		WebElement rootEl = findRootLayer(driver);
		if(rootEl == null) {
			NieUtil.mySleepBySecond(3);
			rootEl = findRootLayer(driver);
		}

		WebElement weFile = findVideoInputWebElement(driver);
		if(weFile == null) {
			System.out.println("[ERROR][uploadVideo]It can NOT find file input");
			return false;
		}
		weFile.sendKeys(f.getCanonicalPath());
		NieUtil.mySleepBySecond(3);
		try{
			rootEl = findRootLayerPop(driver);
			List<WebElement> eles = driver.findElements(By.tagName("a"));
			for (WebElement el : eles) {
				String cz = el.getAttribute("class");
				String txt = el.getText();
				if("W_btn_a".equals(cz) && txt.indexOf("知道了") != -1){
					el.click();break;
				}
			}
		}catch(Exception e){}

		WebDriverWait wait1 = new WebDriverWait(driver, 600);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					WebElement rootEl = findRootLayerPop(driver);
					if(rootEl == null) return Boolean.FALSE;
					List<WebElement> elspans = rootEl.findElements(By.tagName("dl"));
					for (WebElement el : elspans) {
						String nodeType = el.getAttribute("node-type");
						String style = el.getAttribute("style");
						if(nodeType.equals("uploading") && style.indexOf("none;") != -1){
							return Boolean.TRUE;
						}
					}
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		rootEl = findRootLayerPop(driver);
		List<WebElement> eles = rootEl.findElements(By.cssSelector("input[action-type=\"inputTitle\"]"));
		eles.get(0).clear();
		String uper = uploadObj.uper;
		if(uper.length() < 6){
			uper += "----" + uper;
		}
		eles.get(0).sendKeys(uper);
		NieUtil.mySleepBySecond(1);
		
		eles = rootEl.findElements(By.tagName("a"));
		for (WebElement el : eles) {
			String txt = el.getText();
			String nodeType = el.getAttribute("node-type");
			if(txt.indexOf("完成") != -1 && nodeType.equals("completeBtn")){
				el.click();break;
			}
		}
		NieUtil.mySleepBySecond(2);

		eles = driver.findElements(By.cssSelector("a[node-type=\"submit\"]"));
		for (WebElement el : eles) {
			String txt = el.getText();
			if(StringUtil.isBlank(txt))continue;
			String href = el.getAttribute("href");
			if(StringUtil.isBlank(href))continue;
			if(txt.indexOf("发布") != -1 && href.indexOf("javascript:void(0)") != -1){
				el.click();break;
			}
		}
		NieUtil.mySleepBySecond(10);
		return true;
	}
	
	private WebElement findVideoInputWebElement(WebDriver driver) {
		WebDriverWait wait1 = new WebDriverWait(driver,120);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					WebElement rootEl = findRootLayer(driver);
					List<WebElement> elInputs = rootEl.findElements(By.tagName("input"));
					for (WebElement el : elInputs) {
						if (el.getAttribute("id").startsWith("publisher_upvideo")) {
							return Boolean.TRUE;
						}
					}
					//closeUploadDialog(driver);
					NieUtil.mySleepBySecond(3);
					//openUploadDialog(driver);
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		
		WebElement rootEl = findRootLayer(driver);
		List<WebElement> elInputs = rootEl.findElements(By.tagName("input"));
		WebElement weFileInput = null;
		for (WebElement el : elInputs) {
			if (el.getAttribute("id").startsWith("publisher_upvideo")) {
				weFileInput = el;
			}
		}
		return weFileInput;
	}

	
	private WebElement findRootLayer(WebDriver driver){

		List<WebElement> eles = driver.findElements(By.cssSelector("div[id=\"v6_pl_content_publishertop\"]"));
		if(eles.isEmpty()) return null;
		return eles.get(0);
//		List<WebElement> eles = driver.findElements(By.className("W_layer"));
//		if(eles.isEmpty()) return null;
//		for (WebElement ele : eles) {
//			String id = ele.getAttribute("id");
//			if(!StringUtil.isBlank(id) && id.startsWith("layer_")){
//				return ele;
//			}
//		}
//		return null;
	}
	
	private WebElement findRootLayerPop(WebDriver driver){

		List<WebElement> eles = driver.findElements(By.className("W_layer"));
		if(eles.isEmpty()) return null;
		for (WebElement ele : eles) {
			String id = ele.getAttribute("id");
			String clzz = ele.getAttribute("class");
			if(!StringUtil.isBlank(id) && id.startsWith("layer_") && clzz.indexOf("W_layer_pop") != -1){
				return ele;
			}
		}
		return null;
	}
	private List<File> getToPublishFile(MyVideoObject uploadObj) {
		File uploadFoldFolder = MyVideoTrUtil.getSaveFolder(uploadObj);
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
		return false;
	}
	private boolean isPhotoFile(File f) {
		String exd = MyVideoTrUtil.getFileExtention(f);
		if(exd.equalsIgnoreCase("png"))return true;
		if(exd.equalsIgnoreCase("jpg"))return true;
		if(exd.equalsIgnoreCase("jpeg"))return true;
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

//		try {
//			Robot robot = new Robot();
//			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
//			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
//			parseWeibo(driver, videoObjs);
//		} catch (AWTException e) {
//		}
//		try {
//			Robot robot = new Robot();
//			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
//			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
//			parseWeibo(driver, videoObjs);
//		} catch (AWTException e) {
//		}
//		try {
//			Robot robot = new Robot();
//			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
//			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
//			parseWeibo(driver, videoObjs);
//		} catch (AWTException e) {
//		}
			

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

			videoObj.trid = mid;
			videoObj.url = videoUrl;
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
			MyVideoTrUtil.insertVideo(videoObj);
			removeFromFav(we, videoObj);
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
		WebElement el1 = driver.findElement(By.cssSelector("ul[class=\"gn_nav_list\"]"));
		List<WebElement> eles = el1.findElements(By.cssSelector("em[class=\"S_txt1\"]"));
		for (WebElement ele : eles) {
			String txt = ele.getText();
			if (txt.indexOf("次郎花子") != -1) {
				return;
			}
		}

		System.out.println("[INFO][logonWeibo]start");
		wait1.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElement(By.cssSelector("div[id=\"pl_unlogin_home_login\"]"));
					MyVideoTrUtil.stopBrowser(driver);
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

		wait1 = new WebDriverWait(driver,120);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					if(isNeedLoginVertify(driver)){
						return Boolean.TRUE;
					}

					WebElement el1 = driver.findElement(By.cssSelector("div[id=\"plc_top\"]"));
					List<WebElement> eles = el1.findElements(By.cssSelector("em[class=\"S_txt1\"]"));
					for(WebElement ele:eles){
						String txt = ele.getText();
						if(txt.indexOf("次郎花子") != -1){
							MyVideoTrUtil.stopBrowser(driver);
							return Boolean.TRUE;
						}
					}
					return Boolean.FALSE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		System.out.println("[INFO][logonWeibo]End");

		if(isNeedLoginVertify(driver)){
			NieUtil.readLineFromSystemIn("weibo need to login manually!!!login manually and press ANY KEY to continue");
		}
	}
	
	private boolean isNeedLoginVertify(WebDriver driver){
		List<WebElement> els = driver.findElements(By.cssSelector("input[name=\"verifycode\"]"));
		if(!els.isEmpty()){
			return Boolean.TRUE;
		}
		return false;
		
	}

	public boolean downloadVideo(WebDriver driver, MyVideoObject downloadObj) throws IOException  {

		String videoDownloadUrl = MyVideoTrUtil.getVideoDownloadUrlByParsevideo(driver, downloadObj);
		if (StringUtil.isBlank(videoDownloadUrl)) {
			return false;
		}
		File saveFile = getVideoSaveFile(downloadObj);
		MyVideoTrUtil.downLoadVideoFromUrl(videoDownloadUrl, saveFile);
		System.out.println("[INFO][download][Finish][uper]" + downloadObj.uper + "[uper]" + downloadObj.title);
		return true;
	}
	
	private File getVideoSaveFile(MyVideoObject downloadObj) {
		File outFolder = MyVideoTrUtil.getSaveFolder(downloadObj);
		File saveFile = new File(outFolder, downloadObj.uid + ".mp4");
		return saveFile;
	}

	public void removeFromFav(WebDriver driver, MyVideoObject videoObj) {
		//driver.get(myfavURL);
		//logonWeibo(driver);

		List<WebElement> wes = driver.findElements(By.cssSelector("div[action-type=\"feed_list_item\"]"));
		for(WebElement we :wes){
			removeFromFav(we, videoObj);
		}
	}
	public void removeFromFav(WebElement we, MyVideoObject videoObj) {

		List<WebElement> wes1 = we.findElements(By.cssSelector("div[class=\"WB_feed_handle\"]"));
		if(wes1.isEmpty())return;
		List<WebElement> wes2 = wes1.get(0).findElements(By.tagName("a"));
		if(wes2.isEmpty())return;
		for(WebElement we2:wes2){
			String title = we2.getAttribute("title");
			String actionData = we2.getAttribute("action-data");
			if(title.equals("取消赞") && actionData.indexOf(videoObj.trid) != -1){
				we2.click();
				NieUtil.mySleepBySecond(2);
				break;
			}
		}
	}
}
