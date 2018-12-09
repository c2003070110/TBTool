package com.walk_nie.douyin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class WeiboPublisher {
	private static WebDriver driver = null;

	public void publish(File srcFolder) {
		//
		List<PublishObject> objList = parsePublishInfoFromFolder(srcFolder);
		weiboLogon();
		for (PublishObject obj : objList) {
			System.out.println("[Publish File]" + obj.file.getAbsolutePath());
			try {
				publishToWeibo(obj);
			} catch (Exception e) {
				DouYinUtil.recordFailureVideo(obj.file.getAbsolutePath());
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

	private WebDriver weiboLogon() {

		if(driver != null && hadLogon()){
			return driver;
		}
		String rootUrl = "https://www.weibo.com/";
		// WebDriver driver = new ChromeDriver();
		driver = WebDriverUtil.getFirefoxWebDriver();
		driver.get(rootUrl);
		driver.manage().window().setSize(new Dimension(920, 960));
		driver.manage().window().setPosition(new Point(10, 10));


		NieUtil.mySleepBySecond(10);
		
		WebElement elLogin = driver.findElement(By.id("pl_login_form"));
		List<WebElement> elInputs = elLogin.findElements(By.tagName("input"));
		for (WebElement el : elInputs) {
			if ("loginname".equals(el.getAttribute("id"))) {
				el.clear();
				el.sendKeys(NieConfig.getConfig("weibo.user.id"));
			}
			if ("password".equals(el.getAttribute("type"))) {
				el.clear();
				el.sendKeys(NieConfig.getConfig("weibo.user.password"));
			}
		}
		List<WebElement> elas = elLogin.findElements(By.tagName("a"));
		for (WebElement el : elas) {
			if ("登录".equals(el.getText())) {
				el.click();
			}
		}

		NieUtil.readLineFromSystemIn("Weibo login is finished? ENTER For already");
		return driver;
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
	
	class PublishObject{
		String txtContent ="";
		List<File> multimediaContext = Lists.newArrayList();
		File file ;
	}

}
