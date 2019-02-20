package com.walk_nie.taobao;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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


public class TaobaoSuggestAnor {

	private  String taobaoUrl = "https://suggest.taobao.com/sug?area=c2c&code=utf-8&q=%s";

	public static void main(String[] args) throws 
			IOException {
		TaobaoSuggestAnor anor = new TaobaoSuggestAnor();
		anor.anorForkeyword();
		System.exit(0);
	}
	public void anorForkeyword() throws IOException {
		String outputPath = NieConfig.getConfig("taobao.work.folder") +  "/dianAnor";
		File keywordFile = new File(NieConfig.getConfig("taobao.dianAnor.keyword.infile"));
		List<String> keys = Files.readLines(keywordFile, Charset.forName("UTF-8"));
		
		if(keys == null || keys.isEmpty()){
			System.err.println("[anorForkeyword] NONE record in File(" + keywordFile.getAbsolutePath() +")");
			return;
		}

		File root = new File(outputPath);
		if (!root.exists()) {
			root.mkdir();
		}
		String yyyyMMdd = DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyyMMdd");
		String fileNameFmt = "%s-%s.png";
		for (String k : keys) {
			String searchUrl = String.format(taobaoUrl, k);
	        URL url = new URL(searchUrl);

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
