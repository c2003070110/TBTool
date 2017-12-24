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

public class YaAutoGetWon {
	private String ooutFileName = "./ya/won-out.txt";

	protected BufferedReader stdReader = null;

	public void execute(WebDriver driver) throws IOException {
		String aucUrl = "https://auctions.yahoo.co.jp/closeduser/jp/show/mystatus?select=won";
		driver.get(aucUrl);
		// WebElement rootTbl = driver.findElement(By
		// .cssSelector("table[bgcolor=\"#dcdcdc\"]"));
		String fmt = "%s\t216\t%s\t%s\t%s\t%s\t%s\t%s\n";
		List<String> sb = Lists.newArrayList();
		for (int i = 1; i <= 10; i++) {
			WebElement trWe = driver.findElement(
					By.cssSelector("table[bgcolor=\"#dcdcdc\"]")).findElement(
					By.cssSelector("tr:nth-child(" + (i + 1) + ")"));
			WebElement tdWe = trWe.findElement(By
					.cssSelector("td:nth-child(2)"));
			String auctionId = tdWe.getText();
			tdWe = trWe.findElement(By.cssSelector("td:nth-child(8)"));
			// System.out.println(tdWe.getAttribute("outerHTML"));
			if (!tdWe.findElements(By.tagName("a")).isEmpty()) {
				tdWe.findElements(By.tagName("a")).get(0).click();
				try {

					if (driver.findElements(
							By.cssSelector("dd[class=\"decItmName\"]"))
							.isEmpty()) {
						System.out.println("[INFO] 取得失敗。ヤフーオクID＝" + auctionId);
						driver.navigate().back();
						continue;
					}
					String title = driver.findElement(
							By.cssSelector("dd[class=\"decItmName\"]"))
							.getText();
					String price = driver.findElement(
							By.cssSelector("dd[class=\"decPrice\"]")).getText();
					price = price.substring(price.indexOf("：") + 1,
							price.length()).trim();
					price = price.substring(price.indexOf("落札価格：") + 5,
							price.length()).trim();
					price = price.replace("円", "");
					price = price.replaceAll(",", "");
					String decMDT = driver.findElement(
							By.cssSelector("dd[class=\"decMDT\"]")).getText();
					decMDT = decMDT.substring(decMDT.indexOf("：") + 1,
							decMDT.length()).trim();

					// String auctionId =
					// driver.findElement(By.cssSelector("dd[class=\"decItmID\"]")).getText();
					// auctionId = auctionId.substring(auctionId.indexOf("：")+1,
					// auctionId.length()).trim();
					String decSellerID = driver.findElement(
							By.cssSelector("dd[class=\"decSellerID\"]"))
							.getText();
					decSellerID = decSellerID.substring(
							decSellerID.indexOf("：") + 1, decSellerID.length())
							.trim();
					decSellerID = decSellerID.substring(0,
							decSellerID.indexOf("（"));
					driver.findElement(
							By.cssSelector("div[class=\"libLeadText\"]"))
							.click();
					List<WebElement> tradeInfWb = driver.findElements(By
							.cssSelector("div[class=\"libTableCnfTop\"]"));
					WebElement payWb = tradeInfWb.get(0);
					String payment = "";
					List<WebElement> wbs = payWb.findElements(By
							.cssSelector("div[class=\"decCnfWr\"]"));
					if (wbs.isEmpty()) {
						payment = "0";
					} else if (wbs.size() == 1) {
						payment = wbs.get(0).getText();
					} else {
						payment = wbs.get(1).getText();
					}
					String transFee = payment.substring(
							payment.indexOf("送料：") + 3, payment.length() - 1);
					transFee = transFee.replace("円", "");
					transFee = transFee.replaceAll(",", "");

					WebElement sellerWb = tradeInfWb.get(2);
					List<WebElement> s = sellerWb
							.findElements(By.tagName("td"));
					String sellerName = s.get(1).getText();
					String sellerAddr = s.get(2).getText();
					sellerAddr = sellerAddr.replaceAll("\n", " ");
					sellerAddr = sellerAddr.replaceAll("\\*", "").trim();
					sellerName = sellerName + " " + sellerAddr;
					// System.out.println(tradeInfWb.get(0).getAttribute("outerHTML"));
					sb.add(
							String.format(fmt, transFee, auctionId, title,
									price, decMDT, decSellerID,sellerName));
					driver.navigate().back();
				} catch (Exception ex) {

					System.out.println("[INFO] 取得失敗。ヤフーオクID＝" + auctionId
							+ "[ERROR]" + ex.getMessage());
					driver.navigate().back();
				}
			}
		}
		File oFile = new File(ooutFileName);
		String today = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy-MM-dd");
		FileUtils.write(oFile, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		for (String str : sb) {
			FileUtils.write(oFile, str, Charset.forName("UTF-8"), true);
		}

	}

}
