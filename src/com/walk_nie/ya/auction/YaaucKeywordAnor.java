package com.walk_nie.ya.auction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.util.WebDriverSingleton;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;


public class YaaucKeywordAnor {

	private  String searchUrlFmt = "https://auctions.yahoo.co.jp/search/search?n=50&p=";

	public static void main(String[] args) throws IOException {
		YaaucKeywordAnor anor = new YaaucKeywordAnor();
		anor.anorForkeyword();
	}
	
	public void anorForkeyword() throws IOException {
		String outputPath = NieConfig.getConfig("yahoo.auction.work.folder") +  "/keywordAnor";
		File root = new File(outputPath);
		if (!root.exists()) {
			root.mkdir();
		}
		WebDriver driver = WebDriverSingleton.getWebDriver();
		File keywordFile = new File(NieConfig.getConfig("yahoo.auction.keywordAnor.keyword.infile"));
		System.out.println("[waiting for typing keyword]"
				+ keywordFile.getAbsolutePath());
		long updateTime = System.currentTimeMillis();
		while (true) {
			if (updateTime < keywordFile.lastModified()) {
				updateTime = keywordFile.lastModified();
				try{
					doAnor(driver,root,keywordFile);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				System.out.println("[waiting for typing keyword]"
						+ keywordFile.getAbsolutePath());
			}
		}
	}
	private void doAnor(WebDriver driver, File root, File keywordFile) throws IOException {

		List<String> keys = Files.readLines(keywordFile, Charset.forName("UTF-8"));
		
		if(keys == null || keys.isEmpty()){
			System.err.println("[anorForkeyword] NONE record in File(" + keywordFile.getAbsolutePath() +")");
			return;
		}

		String yyyyMMdd = DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyyMMdd");
		String fileNameFmt = "%s-%s.png";
		for (String key : keys) {
			if(key.startsWith("#"))continue;
			String[] spl = key.split("\t");
			String k = spl[0];
			String searchUrl = searchUrlFmt + k.replaceAll(" ", "+");
			driver.get(searchUrl);
			String resp = NieUtil.readLineFromSystemIn("capture this page? Y/N");
			if(resp.equalsIgnoreCase("N")){
				continue;
			}

			List<String> lines = Lists.newArrayList();
			String fmt = "[title]%s[solder]%s[price]%s";
			
			WebElement weTbl = driver.findElement(By.cssSelector("#list01 > table"));
			List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
			for (WebElement trWe : trWeList) {
				try {
					WebElement a1We = trWe.findElement(By.cssSelector("td[class=\"a1\"]"));
					String title = a1We.findElement(By.tagName("h3")).getText();
					String txt = trWe.findElement(By.cssSelector("div[class=\"sinfwrp\"]")).getText();
					WebElement pr1We = trWe.findElement(By.cssSelector("td[class=\"pr1\"]"));
					String price1 = pr1We.getText();
					//WebElement pr2We = trWe.findElement(By.cssSelector("td[class=\"pr2\"]"));
					//String price2 = pr2We.getText();
					//WebElement biWe = trWe.findElement(By.cssSelector("td[class=\"bi\"]"));
					//String biCnt = biWe.getText();
					//WebElement tiWe = trWe.findElement(By.cssSelector("td[class=\"ti\"]"));
					String line = String.format(fmt, title,txt,price1);
					System.out.println(line);
					lines.add(line);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String fileNm = String.format(fileNameFmt, yyyyMMdd, k);
			FileUtils.writeLines(new File(root,String.format(fileNameFmt, fileNm)), lines, true);
		}
	}

}
