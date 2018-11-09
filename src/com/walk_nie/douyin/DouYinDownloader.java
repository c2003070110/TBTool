package com.walk_nie.douyin;
 
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieUtil;
 
/**
 * 
 * 
 * @author lenovo
 *
 */
public class DouYinDownloader {
	
	public void downloadByFile(File file) throws IOException{
		// video urls is stored in file
		List<String> videoSrcLinks = parseVideoSrcLinksFile(file);
		List<String> videoUrlLinks = parseVideoUrlLinks(videoSrcLinks);
		downloadVideo(videoSrcLinks,videoUrlLinks);
	}

	private void downloadVideo(List<String> videoSrcLinks, List<String> videoUrlLinks) throws IOException {
		String outputFile = "out/douyin/v_%s.csv";
		String outFilePath = String.format(outputFile,
				DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyy_MM_dd_HH_mm_ss"));
		for (int i = 0; i < videoUrlLinks.size(); i++) {
			String line1 = videoSrcLinks.get(i);
			String line2 = videoUrlLinks.get(i);
			downLoadFromUrl(line2, new File(outFilePath, i + ".mp4"));
			FileUtils.write(new File(outFilePath, i + ".txt"), line1, "UTF-8");
		}
	}

	private List<String> parseVideoUrlLinks(List<String> videoSrcLinks) {
		List<String> rslt = Lists.newArrayList();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();

		driver.get("http://douyin.iiilab.com/");
		
		for (int i = 0; i < videoSrcLinks.size(); i++) {
			String line = videoSrcLinks.get(i);
			driver.findElement(By.cssSelector("input.form-control.link-input")).clear();
			driver.findElement(By.cssSelector("input.form-control.link-input")).sendKeys(line);
			driver.findElement(By.cssSelector("button.btn.btn-default")).click();
			NieUtil.mySleepBySecond(4);
			rslt.add(driver.findElement(By.cssSelector("a.btn.btn-success")).getAttribute("href").toString());
		}
		driver.close();
		driver.quit();
		return rslt;
	}

	private List<String> parseVideoSrcLinksFile(File file) throws IOException {
		List<String> rslt = Lists.newArrayList();
		List<String> lines = FileUtils.readLines(file, "UTF-8");
		String tmpStr = "";
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (StringUtils.isEmpty(line)) {
				if (!StringUtils.isEmpty(tmpStr)) {
					rslt.add(tmpStr);
					tmpStr = "";
				}
				continue;
			}
			tmpStr = tmpStr + "\n" + line;
		}
		if (!StringUtils.isEmpty(tmpStr)) {
			rslt.add(tmpStr);
		}
		return rslt;
	}
	public  void downLoadFromUrl(String urlStr, File saveFile) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		InputStream inputStream = conn.getInputStream();
		byte[] getData = readInputStream(inputStream);
		if (!saveFile.getParentFile().exists()) {
			saveFile.mkdirs();
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
 
	public  byte[] readInputStream(InputStream inputStream) throws IOException {
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