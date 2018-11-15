package com.walk_nie.douyin;
 
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieUtil;
 
/**
 * 
 */
public class DouYinDownloader {
	private static WebDriver driver = null;
	
	public void downloadByFile(File file, File outFolder) throws IOException {
		// video urls is stored in file

		List<String> videoSrcLinks = parseVideoSrcLinksFile(file, outFolder);

		try {
			downloadVideo(videoSrcLinks, outFolder);
		} catch (Exception e1) {
			DouYinUtil.recordFailureURL(videoSrcLinks);
			e1.printStackTrace();
		}
	}

	public void downloadByURL(String urlStr, File outFolder) {

		List<String> videoSrcLinks = Lists.newArrayList();
		videoSrcLinks.add(urlStr);
		try {
			downloadVideo(videoSrcLinks, outFolder);
		} catch (Exception e) {
			NieUtil.mySleepBySecond(60);
			try {
				downloadVideo(videoSrcLinks, outFolder);
			} catch (Exception e1) {
				DouYinUtil.recordFailureURL(urlStr);
				e1.printStackTrace();
			}
		}
	}
	
	private List<String> downloadVideo(List<String> videoSrcLinks, File outFolder) throws IOException {
		List<String> rslt = Lists.newArrayList();
		
		activateDriver("http://douyin.iiilab.com/");
		
		for (int i = 0; i < videoSrcLinks.size(); i++) {
			String line = videoSrcLinks.get(i);
			driver.findElement(By.cssSelector("input.form-control.link-input")).clear();
			driver.findElement(By.cssSelector("input.form-control.link-input")).sendKeys(line);
			driver.findElement(By.cssSelector("button.btn.btn-default")).click();
			NieUtil.mySleepBySecond(4);
			String url = driver.findElement(By.cssSelector("a.btn.btn-success")).getAttribute("href").toString();
			rslt.add(url);
			downLoadFromUrl(url, new File(outFolder, i + ".mp4"));
			NieUtil.mySleepBySecond(3);
		}
		for (int i = 0; i < videoSrcLinks.size(); i++) {
			String line1 = rslt.get(i);
			FileUtils.write(new File(outFolder, i + ".txt"), line1, "UTF-8");
		}

		return rslt;
	}

	private void activateDriver(String url) {
		try {
			driver.get(url);
		} catch (Exception e) {
			try {
				driver.close();
			} catch (Exception e1) {
			}
			NieUtil.mySleepBySecond(5);
			driver = WebDriverUtil.getFirefoxWebDriver();
			driver.get(url);
		}
	}
	
	private List<String> parseVideoSrcLinksFile(File file, File outFile) throws IOException {
		List<String> rslt = Lists.newArrayList();
		List<String> lines = FileUtils.readLines(file, "UTF-8");
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (!StringUtils.isEmpty(line)) {
				rslt.add(line);
			}
		}
		return rslt;
	}
	private  void downLoadFromUrl(String urlStr, File saveFile) throws IOException {
		System.out.println("[Downloading]" + urlStr);
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
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
 
	private  byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}
 
}