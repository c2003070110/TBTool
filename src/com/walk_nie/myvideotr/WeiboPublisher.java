package com.walk_nie.myvideotr;

import java.io.File;
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

public class WeiboPublisher {
	public static void main(String[] args) throws IOException {
		WeiboPublisher weibo = new WeiboPublisher();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		weibo.processByScanWeibo(driver);
	}
	public void publish(WebDriver driver, MyVideoObject uploadObj,File uploadFile) {
		logonWeibo(driver);
	}

	public List<MyVideoObject> processByScanWeibo(WebDriver driver) {
		String visitUrl = "https://www.weibo.com/like/outbox?leftnav=1";
		driver.get(visitUrl);
		logonWeibo(driver);
		//driver.get(visitUrl);

//		((JavascriptExecutor) driver)
//				.executeScript("window.scrollTo(0, document.body.scrollHeight)");
//		NieUtil.mySleepBySecond(4);
		
		List<MyVideoObject> videoObjs = Lists.newArrayList();
		List<WebElement> wes = driver.findElements(By.cssSelector("div[action-type=\"feed_list_item\"]"));
		for(WebElement we :wes){
			List<WebElement> wes1 = we.findElements(By.tagName("video"));
			if(wes1.isEmpty())continue;

			String videoUrl = findVideoUrl(we);
			if(StringUtil.isBlank(videoUrl))continue;

			MyVideoObject videoObj = new MyVideoObject();
			videoObj.url = videoUrl;
			videoObj.toType = "toYoutube";
			videoObj.videoUrl = videoObj.url;
		
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

			wes1 = we.findElements(By.cssSelector("div[node-type=\"feed_list_content\"]"));
			if(!wes1.isEmpty()){
				videoObj.title = wes1.get(0).getText();
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
			NieUtil.mySleepBySecond(2);
		}
		return videoObjs;
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
						videoUrl = s.substring("short_url=".length());
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
						videoUrl = s.substring("short_url=".length());
						return videoUrl;
					}
				}
			}
		}
		return videoUrl;
	}
	public void logonWeibo(WebDriver driver) {

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

		try {
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
		} catch (Exception ex) {
			return;
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
		
		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"plc_top\"]"));
		List<WebElement> eles = el1.findElements(By.cssSelector("em[class=\"S_txt1\"]"));
		for(WebElement ele:eles){
			String txt = ele.getText();
			if(txt.indexOf("次郎花子") != -1){
				return;
			}
		}
		
		NieUtil.readLineFromSystemIn("Weibo login is finished? ANY KEY For already");
	}

	/*
	public void publish(File srcFolder) {
		//
		List<PublishObject> objList = parsePublishInfoFromFolder(srcFolder);
		weiboLogon();
		for (PublishObject obj : objList) {
			System.out.println("[Publish File]" + obj.file.getAbsolutePath());
			try {
				publishToWeibo(obj);
			} catch (Exception e) {
			}
			NieUtil.mySleepBySecond(60);
		}
	}
	private List<PublishObject> parsePublishInfoFromFolder(File srcFolder) {
		
		List<PublishObject> objList = Lists.newArrayList();
		File[] files = srcFolder.listFiles(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				if(name.endsWith(".txt"))return true;
				if(name.endsWith(".mp4"))return true;
				if(name.endsWith(".flv"))return true;
				if(name.endsWith(".png"))return true;
				if(name.endsWith(".jpg"))return true;
				if(name.endsWith(".jpeg"))return true;
				// FIXME more type...
				return false;
			}});
		for(File file:files){
			String ext = getFileExtention(file);
			if(!ext.endsWith("txt")){
				continue;
			}
			PublishObject obj = new PublishObject();
			obj.file = file;
			String name = getFileWithoutExtention(file);
			List<String> lines = Lists.newArrayList();;
			try {
				lines = FileUtils.readLines(file, "UTF-8");
			} catch (IOException e) {
			}
			StringBuffer sb = new StringBuffer();
			for(String line :lines){
				sb.append(line).append("\n");
			}
			obj.txtContent = sb.toString();
			for(File file1:files){
				String name1 = getFileWithoutExtention(file1);
				String ext1 = getFileExtention(file1);
				if(name1.startsWith(name) && !ext1.endsWith("txt")){
					obj.multimediaContext.add(file1);
				}
			}
			
			objList.add(obj);
		}
		return objList;
	}
	protected void publishToWeibo(PublishObject obj) throws Exception {
		
		// upload picture or video
		for(File f:obj.multimediaContext){
			if(isVideoFile(f)){
				List<WebElement> elInputs = driver.findElements(By.tagName("input"));
				for (WebElement el : elInputs) {
					if (el.getAttribute("id").startsWith("publisher_upvideo")) {
						el.sendKeys(f.getAbsolutePath());
						break;
					}
				}

				WebElement elMain = driver.findElement(By.id("plc_main"));
				while (true) {
						boolean uploaded = false;
						try {
							List<WebElement> elspans = elMain.findElements(By
									.tagName("dl"));
							for (WebElement el : elspans) {
								String attr = el.getAttribute("node-type");
								if (attr != null && attr.equals("uploading")) {
									String attr1 = el.getAttribute("style");
									if (attr1 != null && attr1.indexOf("block") == -1)
										uploaded = true;
									break;
								}
							}
						} catch (Exception ex) {

						}
						if (uploaded) {
							break;
						}
						NieUtil.mySleepBySecond(3);
				}
			}
		}
		WebElement elMain = driver.findElement(By.id("plc_main"));
		// fill txt
		//String despTxt = "#日本# #抖音#";
		String despTxt = NieConfig.getConfig("weibo.douyin.keywords");
		if(StringUtils.isNotEmpty(obj.txtContent)){
			// FIXME 
			//despTxt += obj.txtContent;
		}
		List<WebElement> elInputs1 = elMain.findElements(By.tagName("input"));
		for (WebElement el : elInputs1) {
			String attr = el.getAttribute("action-type");
			if (attr != null && attr.equals("inputTitle")) {
				el.sendKeys(despTxt);
				break;
			}
		}
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

	private boolean hadLogon() {
		try{
			List<WebElement> es = driver.findElements(By.className("gn_name"));
			if(es == null || es.isEmpty()){
				return false;
			}
			// 
			for(WebElement e:es){
				if(!e.getTagName().toLowerCase().equals("a")){
					continue;
				}
				List<WebElement> es1 = e.findElements(By.className("S_txt1"));
				for(WebElement e1:es1){
					// FIXME
					if(e1.getText().toLowerCase().equals(NieConfig.getConfig("weibo.user.name"))){
						return true;
					}
				}
			}
			return true;
		}catch(Exception e){
		}
		return false;
	}
	private boolean isVideoFile(File f) {
		String exd = getFileExtention(f);
		if ("mp4".equalsIgnoreCase(exd) || "flv".equalsIgnoreCase(exd)) {
			return true;
		}
		return false;
	}

	private String getFileExtention(File f) {
		String fileName = f.getName();
		int dotPox = fileName.lastIndexOf(".");
		return fileName.substring(dotPox + 1, fileName.length());
	}

	private String getFileWithoutExtention(File f) {
		String fileName = f.getName();
		int dotPox = fileName.lastIndexOf(".");
		return fileName.substring(0, dotPox);
	}
	*/
	class PublishObject{
		String txtContent ="";
		List<File> multimediaContext = Lists.newArrayList();
		File file ;
	}

}
