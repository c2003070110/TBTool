package com.walk_nie.ya.auction;

import java.io.BufferedReader;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class YaAutoGetSold {

	protected BufferedReader stdReader = null;

	public void execute(WebDriver driver) throws IOException {
		String aucUrl = "https://auctions.yahoo.co.jp/closeduser/jp/show/mystatus?select=closed&hasWinner=1";
		driver.get(aucUrl);
		// WebElement rootTbl = driver.findElement(By
		// .cssSelector("table[bgcolor=\"#dcdcdc\"]"));
		String fmt = "%s\t%s\t%s\t%s\t%s\t%s\n";
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= 50; i++) {
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
			sb.append(String.format(fmt, auctionId, title,
					price, latestTime, obider,msg));
		}
		System.out.println(sb);

	}

}
