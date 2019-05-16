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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.seleniumhq.jetty9.util.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class MyVideoTrUtil {

	public static void downLoadVideoFromUrl(String urlStr, File saveFile)
			throws IOException {
		System.out.println("[INFO][Video][Downloading]" + urlStr);
		System.out.println("[INFO][Video][Save File  ]" + saveFile.getCanonicalPath());
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

	private static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

	public static String getVideoDownloadUrlByVblogDownload(WebDriver driver, MyVideoObject downloadObj) {
		driver.get("https://www.vlogdownloader.com/");
		// TODO
		WebElement rootWE = driver.findElement(By.cssSelector("form[id=\"vlog\"]"));
		WebElement we = rootWE.findElement(By.cssSelector("input[name=\"url\"]"));
		we.clear();
		we.sendKeys(downloadObj.videoUrl);
		we = rootWE.findElement(By.cssSelector("button[type=\"submit\"]"));
		we.click();
		
		NieUtil.mySleepBySecond(5);

		WebDriverWait wait1 = new WebDriverWait(driver, 300);
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
			System.out.println("[ERROR][Video][Download]" + el1.getText());
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

	public static String getVideoDownloadUrlByParsevideo(WebDriver driver, MyVideoObject downloadObj) {
		driver.get("https://www.parsevideo.com/");
		WebElement we = driver.findElement(By
				.cssSelector("input[id=\"url_input\"]"));
		we.clear();
		MyVideoTrUtil.stopBrowser(driver);
		String url = NieUtil.decode(downloadObj.videoUrl);
		we.sendKeys(url);
		we = driver.findElement(By
				.cssSelector("button[id=\"url_submit_button\"]"));
		we.click();
		
		NieUtil.mySleepBySecond(5);

		WebDriverWait wait1 = new WebDriverWait(driver, 300);
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
			System.out.println("[ERROR][Video][Download]" + el1.getText());
			System.out.println("[ERROR][Video][Download]" + url);
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


	public static File getSaveFolder(MyVideoObject obj) {
		String outFolder = NieConfig.getConfig("myvideotr.video.folder") ;
		File saveFile = new File(outFolder,obj.uid);
		if(!saveFile.exists()){
			saveFile.mkdirs();
		}
		return saveFile;
	}

	public static String getFileExtention(File f) {
		String fileName = f.getName();
		int dotPox = fileName.lastIndexOf(".");
		return fileName.substring(dotPox + 1, fileName.length());
	}

	public static void insertVideo(MyVideoObject obj) {
		
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "insertVideo");
			param.put("url", obj.url);
			param.put("videoUrl", obj.videoUrl);
			param.put("toType", obj.toType);
			param.put("fromType", obj.fromType);
			param.put("trid", obj.trid);
			param.put("title", obj.title);
			param.put("uper", obj.uper);
			param.put("groupUid", obj.groupUid);
			param.put("ytSearchRslt", obj.ytSearchRslt);
			//NieUtil.log(logFile, "[INFO][Service:insertVideo][Param]" + "[url]" + obj.url + "[title]" + obj.title + "[uper]" + obj.uper);

			String rslt = NieUtil.httpGet(NieConfig.getConfig("myvideotr.service.url"), param);

			if (!StringUtil.isBlank(rslt)) {
				System.out.println("[INFO][Service:insertVideo][RESULT]" + rslt);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[ERROR][Service:insertVideo]" + e.getMessage());
		}
	}
	public static void stopBrowser(WebDriver driver){
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("return window.stop");
	}
}
