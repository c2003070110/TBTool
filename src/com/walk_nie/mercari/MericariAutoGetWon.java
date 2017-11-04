package com.walk_nie.mercari;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;

public class MericariAutoGetWon {

	protected BufferedReader stdReader = null;

	public void execute(WebDriver driver) throws IOException {
		String aucUrl = "https://www.mercari.com/jp/mypage/purchase/";
		driver.get(aucUrl);
		WebElement mytab= driver.findElement(By.id("mypage-tab-transaction-now"));
		List<WebElement> awes = mytab.findElements(By.tagName("a"));
		List<String> wonItemIdIds = Lists.newArrayList();
		for(WebElement awe:awes){
			String clsName = awe.getAttribute("class");
			if("mypage-item-link".equals(clsName)){
				String url =  awe.getAttribute("href");
				if(url.endsWith("/")){
					url = url.substring(0,url.length()-1);
				}
				String itemId = url.substring(url.lastIndexOf("/")+1);
				wonItemIdIds.add(itemId);
			}
		}
		String itemUrlPrefix ="https://item.mercari.com/jp/" ;
		//String orderurlPrefix ="https://www.mercari.com/jp/transaction/order_status/" ;
		StringBuffer sb = new StringBuffer();
		String fmt = "%s\t\t350\t%s\t%s\t%s\t%s\t%s\t支払済\t%s";
		for(String itemId:wonItemIdIds){
			String url = itemUrlPrefix + itemId;
			driver.get(url);
			WebElement myCont= driver.findElement(By.cssSelector("div.l-content"));
			String itemName = myCont.findElement(By.cssSelector("h2.item-name")).getText();
			String price = myCont.findElement(By.cssSelector("span.item-price")).getText();
			price = price.replace("¥", "");
			List<WebElement> trs =  myCont.findElement(By.cssSelector("table.item-detail-table")).findElements(By.tagName("tr"));
			String seller = trs.get(0).findElement(By.tagName("a")).getText();
			String gen = trs.get(6).findElement(By.tagName("a")).getText();

			sb.append(
					String.format(fmt, "", itemId, itemName,
							price.trim(), "", seller,gen)).append("\n");
		}
		System.out.println(sb);
	}

}
