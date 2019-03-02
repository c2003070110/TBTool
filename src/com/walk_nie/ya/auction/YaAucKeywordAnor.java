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


public class YaAucKeywordAnor {

	private  String searchUrlFmt = "https://auctions.yahoo.co.jp/search/search?n=100&mode=2&p=";

	public static void main(String[] args) throws IOException {
		YaAucKeywordAnor anor = new YaAucKeywordAnor();
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
		String fileNameFmt = "%s-%s.txt";
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
			String fmt = "%s\t%s\t%s\t%s";
			lines.add(String.format(fmt, "title", "seller", "price1",
					"price2"));
			
			WebElement weTbl = driver.findElement(By.cssSelector("#list01 > table"));
			List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
			for (WebElement trWe : trWeList) {
				try {
					WebElement a1We = trWe.findElement(By.cssSelector("td[class=\"a1\"]"));
					String title = a1We.findElement(By.tagName("h3")).getText();
					String txt = trWe.findElement(By.cssSelector("div[class=\"sinfwrp\"]")).getText();
					String seller = "";
					String category = "";
					if(txt.indexOf("カテゴリ") != -1){
						seller = txt.substring(0,txt.indexOf("カテゴリ"));
						category = txt.substring(txt.indexOf("カテゴリ"));
					}
					if(seller.indexOf("（") != -1){
						seller = seller.substring(0, txt.indexOf("（"));
					}
					seller = seller.replace("この出品者の商品を非表示にする", "");
					seller = seller.replace("出品者", "");
					seller = seller.replaceAll(" ", "");
					WebElement pr1We = trWe.findElement(By.cssSelector("td[class=\"pr1\"]"));
					txt = pr1We.getText();
					String price1 = txt.split("\n")[0];
					price1 = price1.replace("円", "");
					WebElement pr2We = trWe.findElement(By.cssSelector("td[class=\"pr2\"]"));
					String price2 = pr2We.getText();
					price2 = price2.replaceAll("\n", ":");
					price2 = price2.replaceAll(" ", "");
					price2 = price2.replace("円", "");
					//WebElement biWe = trWe.findElement(By.cssSelector("td[class=\"bi\"]"));
					//String biCnt = biWe.getText();
					//WebElement tiWe = trWe.findElement(By.cssSelector("td[class=\"ti\"]"));
					String line = String.format(fmt, title,seller,price1,price2);
					System.out.println(line);
					lines.add(line);
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
			String fileNm = String.format(fileNameFmt, yyyyMMdd, k);
			FileUtils.writeLines(new File(root,fileNm), lines, true);
		}
	}

}
