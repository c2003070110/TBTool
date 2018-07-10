package com.walk_nie.ya.auction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
		String allwinnerUrl = "https://auctions.yahoo.co.jp/jp/show/allwinners?aID=";
		driver.get(aucUrl);
		WebElement weTbl = driver.findElement(By
				.cssSelector("table[bgcolor=\"#dcdcdc\"]"));
		List<String> llAuctionIds = Lists.newArrayList();
		List<YaSoldObject> soldObjList = Lists.newArrayList();
		for (int i = 1; i <= 5; i++) {
			YaSoldObject yaObj = new YaSoldObject();
			WebElement trWe = weTbl.findElement(By.cssSelector("tr:nth-child("
					+ (i + 1) + ")"));
			WebElement tdWe = trWe.findElement(By
					.cssSelector("td:nth-child(2)"));
			yaObj.auctionId = tdWe.getText();
			tdWe = trWe.findElement(By.cssSelector("td:nth-child(3)"));
			yaObj.title = tdWe.getText();
			tdWe = trWe.findElement(By.cssSelector("td:nth-child(4)"));
			String price = tdWe.getText();
			price = price.replaceAll("円", "");
			yaObj.price = price.trim();
			tdWe = trWe.findElement(By.cssSelector("td:nth-child(5)"));
			yaObj.latestTime = tdWe.getText().replaceAll(" ", "");
			tdWe = trWe.findElement(By.cssSelector("td:nth-child(6)"));
			yaObj.obider = tdWe.getText();
			if (yaObj.obider.equals("落札者一覧")) {
				llAuctionIds.add(yaObj.auctionId);
				continue;
			} else {
				tdWe = trWe.findElement(By.cssSelector("td:nth-child(7)"));
				String msg = tdWe.getText();
				if (msg.equals("商品を受け取りました")) {
					msg = "完了";
				}
				yaObj.statusMsg = msg;
				soldObjList.add(yaObj);
			}
		}
		// 落札者一覧
		for (String auctionid : llAuctionIds) {
			driver.get(allwinnerUrl + auctionid);
			WebElement weTbl1 = driver
					.findElement(By
							.cssSelector("div.modListTbl:nth-child(4) > table:nth-child(2)"));

			WebElement weTbl2 = driver
					.findElement(By
							.cssSelector("div.modListTbl:nth-child(5) > table:nth-child(3)"));
			List<WebElement> tblTrs = weTbl2.findElements(By.tagName("tr"));
			for (int j = 1; j < tblTrs.size(); j++) {
				YaSoldObject yaObj = new YaSoldObject();
				yaObj.auctionId = auctionid;
				yaObj.title = weTbl1.findElement(
						By.cssSelector("tr:nth-child(2) > td:nth-child(2)"))
						.getText();
				WebElement trWe2 = tblTrs.get(j);
				WebElement tdWe2 = trWe2.findElement(By
						.cssSelector("td:nth-child(1)"));
				yaObj.obider = tdWe2.getText();
				tdWe2 = trWe2.findElement(By.cssSelector("td:nth-child(2)"));
				String price = tdWe2.getText();
				price = price.replaceAll("円", "");
				yaObj.price = price.trim();
				tdWe2 = trWe2.findElement(By.cssSelector("td:nth-child(3)"));
				String msg = tdWe2.getText();
				if (msg.equals("商品を受け取りました")) {
					msg = "完了";
				}
				yaObj.statusMsg = msg;
				tdWe2 = trWe2.findElement(By.cssSelector("td:nth-child(4)"));
				yaObj.latestTime = tdWe2.getText().replaceAll(" ", "");

				soldObjList.add(yaObj);
			}
		}
		Collections.sort(soldObjList, new Comparator<YaSoldObject>() {
			@Override
			public int compare(YaSoldObject obj1, YaSoldObject obj2) {
				return obj2.latestTime.compareTo(obj1.latestTime);
			}
		});

		File oFile = new File(ooutFileName);
		String today = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy-MM-dd");
		FileUtils.write(oFile, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		String fmt = "%s\t%s\t%s\t%s\t%s\t%s\n";
		for (YaSoldObject yaObj : soldObjList) {
			String str = String.format(fmt, yaObj.auctionId, yaObj.title,
					yaObj.price, yaObj.latestTime, yaObj.obider,
					yaObj.statusMsg);
			FileUtils.write(oFile, str, Charset.forName("UTF-8"), true);
		}

		String urlFmt = "https://contact.auctions.yahoo.co.jp/seller/top?aid=%s&bid=%s";
		List<String> msgList = Lists.newArrayList();
		for (YaSoldObject yaObj : soldObjList) {
			String url = String.format(urlFmt, yaObj.auctionId, yaObj.obider);
			msgList.add("--------Auction ID=" + yaObj.auctionId + "--------");
			msgList.add("--------Obid ID=" + yaObj.obider + "--------");
			driver.get(url);
			WebElement messagelistWe = null;
			try {
				messagelistWe = driver.findElement(By.id("messagelist"));
			} catch (Exception e) {

				continue;
			}
			if (messagelistWe == null) {
				continue;
			}
			List<WebElement> ddWes = messagelistWe.findElements(By
					.tagName("dd"));
			for (WebElement ddWe : ddWes) {
				msgList.add(ddWe.getText());
			}
		}
		File oFileMsg = new File(ooutFileNameMsg);
		FileUtils.write(oFile, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		for (String str : msgList) {
			FileUtils.write(oFileMsg, str + "\n", Charset.forName("UTF-8"),
					true);
		}
	}

}
