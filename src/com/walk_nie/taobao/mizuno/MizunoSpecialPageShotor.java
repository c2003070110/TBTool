package com.walk_nie.taobao.mizuno;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.walk_nie.taobao.util.WebDriverUtil;

public class MizunoSpecialPageShotor {
	String serieListFileName = "serieList.txt";
	String outputPath = "out/mizuno/specialPage/";

	public static void main(String[] args) throws IOException {
		String url = "https://www.mizuno.jp/football/rebula/";

		MizunoSpecialPageShotor db = new MizunoSpecialPageShotor();
		db.addUrl(url).process();
	}

	private void process() throws IOException {
		File file = new File(outputPath,
				serieListFileName);
		List<String> serielList = Lists.newArrayList();
		if (!file.exists()) {
			serielList = Files.readLines(file, Charset.forName("UTF-8"));
		}
		String fileNameFmt = "%s_specialPage.jpg";
		for (String url : urlList) {
			if (url.endsWith("/")) {
				url = url.substring(0, url.length() - 1);
			}
			String serialName = url.substring(url.lastIndexOf("/"));
			String fileName = String.format(fileNameFmt, serialName);
			File saveTo = new File(outputPath, fileName);
			if(saveTo.exists())continue;
			
			WebDriver webDriver = WebDriverUtil.getWebDriver(url);
			List<WebElement> elements = webDriver.findElements(By.tagName("body"));
			WebDriverUtil.screenShotV2(webDriver, elements, saveTo.getAbsolutePath(), null);
			String oldLine = null;
			for(String s:serielList){
				String[] spl = s.split("\t");
				if(spl[0].equals(serialName)){
					oldLine = s;
					break;
				}
			}
			if(oldLine != null){
				serielList.remove(oldLine);
			}
			String newLine = serialName + "\t" + saveTo.getAbsolutePath();
			serielList.add(newLine);
			System.out.println(newLine);
		}
		FileUtils.writeLines(file, "UTF-8", serielList);
	}

//	String outputPath;
//
//	private MizunoSpecialPageShotor setOutputFile(String outputPath) {
//		this.outputPath = outputPath;
//		return this;
//	}

	List<String> urlList = Lists.newArrayList();

	private MizunoSpecialPageShotor addUrl(String url) {
		urlList.add(url);
		return this;
	}
}
