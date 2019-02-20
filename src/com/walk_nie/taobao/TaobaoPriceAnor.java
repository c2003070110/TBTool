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
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.WebDriverSingleton;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;


public class TaobaoPriceAnor {

	private  String taobaoUrl = "https://s.taobao.com/search?imgfile=&commend=all&ssid=s5-e&search_type=item&sourceId=tb.index&ie=utf8&q=%s";

	public static void main(String[] args) throws 
			IOException {
		TaobaoPriceAnor anor = new TaobaoPriceAnor();
		anor.anorForkeyword();
		anor.anorForMontbell();
		System.exit(0);
	}
	public void anorForkeyword() throws IOException {
		String outputPath = NieConfig.getConfig("taobao.work.folder") +  "/priceAnor";
		File keywordFile = new File(NieConfig.getConfig("taobao.priceAnor.keyword.infile"));
		List<String> keys = Files.readLines(keywordFile,
				Charset.forName("UTF-8"));
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
			WebDriver webDriver = WebDriverSingleton.getWebDriver(searchUrl);
			
			NieUtil.mySleepBySecond(4);
			
			File screenshot = ((TakesScreenshot)  webDriver)
					.getScreenshotAs(OutputType.FILE);
			
			String fileNm = String.format(fileNameFmt, yyyyMMdd, k);
			File saveTo = new File(root, fileNm);
			FileUtils.copyFile(screenshot, saveTo);
		}
	}

	public void anorForMontbell() throws  IOException {
		String outputPath = NieConfig.getConfig("taobao.work.folder") +  "/priceAnor/montbell";
		String publishedBaobeiFile = NieConfig.getConfig("taobao.baobei.priceAnor.montbell.infile");
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		if(baobeiList == null || baobeiList.isEmpty()){
			System.err.println("[anorForMontbell] NONE record in File(" + file.getAbsolutePath() +")");
			return;
		}

		File root = new File(outputPath);
		if (!root.exists()) {
			root.mkdir();
		}
		String fileNameFmt = "%s-%s.png";
		for (BaobeiPublishObject baobeiObj : baobeiList) {
			String publisedProductId = "";
			String outer_id = baobeiObj.outer_id.replace("\"", "");
			if (outer_id.startsWith("MTBL_")) {
				String[] split = outer_id.split("-");
				publisedProductId = split[split.length - 1];
			} else {
				publisedProductId = outer_id;
			}
			String fileNm = String.format(fileNameFmt, publisedProductId,
					baobeiObj.price);
			String searchUrl = String.format(taobaoUrl, publisedProductId);
			WebDriver webDriver = WebDriverSingleton.getWebDriver(searchUrl);
			
			NieUtil.mySleepBySecond(4);
			
			File screenshot = ((TakesScreenshot)  webDriver)
					.getScreenshotAs(OutputType.FILE);
			File saveTo = new File(root, fileNm);
			FileUtils.copyFile(screenshot, saveTo);
		}
	}
}
