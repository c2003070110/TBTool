package com.walk_nie.taobao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.io.Files;
import com.walk_nie.taobao.util.WebDriverSingleton;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;


public class TaobaoDianAnor {

	private  String taobaoUrl = "https://s.taobao.com/search?imgfile=&commend=all&ssid=s5-e&search_type=item&sourceId=tb.index&ie=utf8&q=%s";

	public static void main(String[] args) throws IOException {
		TaobaoDianAnor anor = new TaobaoDianAnor();
		anor.anorForkeyword();
		System.exit(0);
	}
	
	public void anorForkeyword() throws IOException {
		String outputPath = NieConfig.getConfig("taobao.work.folder") +  "/dianAnor";
		File root = new File(outputPath);
		if (!root.exists()) {
			root.mkdir();
		}
		File keywordFile = new File(NieConfig.getConfig("taobao.dianAnor.keyword.infile"));
		long updateTime = System.currentTimeMillis();
		while (true) {
			if (updateTime < keywordFile.lastModified()) {
				updateTime = keywordFile.lastModified();
				try{
					doAnor(root,keywordFile);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				System.out.println("[waiting for keyword in ]"
						+ keywordFile.getAbsolutePath());
			}
		}
	}
	private void doAnor(File root, File keywordFile) throws IOException {

		List<String> keys = Files.readLines(keywordFile, Charset.forName("UTF-8"));
		
		if(keys == null || keys.isEmpty()){
			System.err.println("[anorForkeyword] NONE record in File(" + keywordFile.getAbsolutePath() +")");
			return;
		}

		String yyyyMMdd = DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyyMMdd");
		String fileNameFmt = "%s-%s.png";
		for (String k : keys) {
			String searchUrl = String.format(taobaoUrl, k);
			WebDriver webDriver = WebDriverSingleton.getWebDriver(searchUrl);
			
			NieUtil.mySleepBySecond(4);
			
			File screenshot = ((TakesScreenshot)  webDriver)
					.getScreenshotAs(OutputType.FILE);
			
			String fileNm = String.format(fileNameFmt, yyyyMMdd, k);
			File saveTo = new File(root, fileNm);
			FileUtils.copyFile(screenshot, saveTo);
		}
	}

}
