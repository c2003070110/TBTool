package com.walk_nie.taobao.montBell;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.WebDriverUtil;

public class MontbellBaobeiPriceAnor  {
	private static String taobaoUrl = "https://s.taobao.com/search?imgfile=&commend=all&ssid=s5-e&search_type=item&sourceId=tb.index&ie=utf8&q=%s";

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		String outputPath = "out/montBell_price";
		String publishedBaobeiFile = "C:/Users/niehp/Google ドライブ/taobao-niehtjp/montbell/montbell-down-20161217.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		
		File root = new File(outputPath);
		if(!root.exists()){
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
			String fileNm = String.format(fileNameFmt, publisedProductId , baobeiObj.price);
			String searchUrl = String.format(taobaoUrl,publisedProductId);
			WebDriver webDriver = WebDriverUtil.getWebDriver(searchUrl);
			List<WebElement> reslt = webDriver.findElements(By.id("list-itemList"));
			File despFile = new File(root,fileNm);
			if(!reslt.isEmpty()){
				  WebDriverUtil.screenShot(webDriver, reslt, despFile.getAbsolutePath());
			}
		} 

		System.exit(0);
	}

}
