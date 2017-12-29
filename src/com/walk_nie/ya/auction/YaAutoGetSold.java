package com.walk_nie.ya.auction;

import java.io.BufferedReader;
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

import com.beust.jcommander.internal.Lists;

public class YaAutoGetSold {
	private String ooutFileName = "./ya/sold-out.txt";
	private String ooutFileNameMsg = "./ya/sold-msg.txt";

	protected BufferedReader stdReader = null;

	public void execute(WebDriver driver) throws IOException {
		String aucUrl = "https://auctions.yahoo.co.jp/closeduser/jp/show/mystatus?select=closed&hasWinner=1";
		driver.get(aucUrl);
		// WebElement rootTbl = driver.findElement(By
		// .cssSelector("table[bgcolor=\"#dcdcdc\"]"));
		String fmt = "%s\t%s\t%s\t%s\t%s\t%s\n";
		List<String> sb = Lists.newArrayList();
		List<String> auctionIds = Lists.newArrayList();
		for (int i = 1; i <= 40; i++) {
			WebElement trWe = driver.findElement(
					By.cssSelector("table[bgcolor=\"#dcdcdc\"]")).findElement(
					By.cssSelector("tr:nth-child(" + (i + 1) + ")"));
			WebElement tdWe = trWe.findElement(By
					.cssSelector("td:nth-child(2)"));
			String auctionId = tdWe.getText(); 
			tdWe = trWe.findElement(By
					.cssSelector("td:nth-child(3)"));
			String title = tdWe.getText();
			tdWe = trWe.findElement(By
					.cssSelector("td:nth-child(4)"));
			String price = tdWe.getText();
			price = price.replaceAll("円", "");
			price = price.trim();
			tdWe = trWe.findElement(By
					.cssSelector("td:nth-child(5)"));
			String latestTime = tdWe.getText();
			tdWe = trWe.findElement(By
					.cssSelector("td:nth-child(6)"));
			String obider = tdWe.getText();
			tdWe = trWe.findElement(By
					.cssSelector("td:nth-child(7)"));
			String msg = tdWe.getText();
			if(msg.equals("商品を受け取りました")){
				msg="完了";
			}
			auctionIds.add(auctionId);
			sb.add(String.format(fmt, auctionId, title,
					price, latestTime, obider,msg));
		}
		File oFile = new File(ooutFileName);
		String today = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy-MM-dd");
		FileUtils.write(oFile, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		for (String str : sb) {
			FileUtils.write(oFile, str, Charset.forName("UTF-8"), true);
		}
		
		String urlFmt ="https://contact.auctions.yahoo.co.jp/seller/top?aid=";
		List<String> msgList = Lists.newArrayList();
		for (String auctionId:auctionIds) {
			String url = urlFmt +  auctionId;
			msgList.add("--------Auction ID=" + auctionId +"--------");
			driver.get(url);
			WebElement messagelistWe = null;
			try{
			 messagelistWe = driver.findElement(By.id("messagelist"));
			}catch(Exception e){

				continue;
			}
			if(messagelistWe == null){
				continue;
			}
			List<WebElement> ddWes = messagelistWe.findElements(By.tagName("dd"));
			for(WebElement ddWe :ddWes){
				msgList.add(ddWe.getText());
			}
		}
		File oFileMsg = new File(ooutFileNameMsg);
		FileUtils.write(oFile, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		for (String str : msgList) {
			FileUtils.write(oFileMsg, str + "\n", Charset.forName("UTF-8"), true);
		}
	}

}
