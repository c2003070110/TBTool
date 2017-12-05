package com.walk_nie.taobao.montBell;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.WebDriverUtil;

public class MontbellBaobeiPriceAnor {
	private static String taobaoUrl = "https://s.taobao.com/search?imgfile=&commend=all&ssid=s5-e&search_type=item&sourceId=tb.index&ie=utf8&q=%s";

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		String outputPath = "out/montBell_price";
		String publishedBaobeiFile = "C:/temp/montbell-down-20171112.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);

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
			WebDriver webDriver = WebDriverUtil.getWebDriver(searchUrl);
			try {
				Thread.sleep(1000 * 3);
			} catch (InterruptedException e) {
			}
			// List<WebElement> reslt =
			// webDriver.findElements(By.id("list-itemList"));
			// List<WebElement> reslt =
			// webDriver.findElement(By.id("mainsrp-itemlist")).findElement(By.id("J_itemlistCont")).findElements(By.className("item"));
			List<WebElement> itemsL = webDriver.findElement(
					By.id("mainsrp-itemlist")).findElements(
					By.className("items"));
//			try {
//				Thread.sleep(1000 * 5);
//			} catch (InterruptedException e) {
//			}
			List<WebElement> reslt = null;
			if (itemsL.size() > 1) {
				reslt = itemsL.get(0).findElements(By.className("item"));
				File despFile = new File(root, fileNm);
				if (!reslt.isEmpty()) {
					screenShot(webDriver, reslt, despFile.getAbsolutePath());
				}
			}
		}

		System.exit(0);
	}

	public static void screenShot(WebDriver driver, List<WebElement> elements,
			String saveTo) throws ClientProtocolException, IOException {

		// Get entire page screenshot
		File screenshot = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);

		BufferedImage fullImg = ImageIO.read(screenshot);
		// Get the location of element on the page
		Point point1 = elements.get(0).getLocation();
		// Get width and height of the element
		int eleH = elements.get(0).getSize().height;
		int eleW = elements.get(0).getSize().width;

		int cntPerRow = 4;
		int paddingH = 22;
		int paddingW = 17;
		int mod = (elements.size()%cntPerRow);
		int rows =elements.size() / cntPerRow;
		if(mod != 0)rows ++;
		int eleWidth = (eleW + paddingW) * cntPerRow;
		int eleHeight = (eleH + paddingH) * rows;

		// Crop the entire page screenshot to get only element screenshot
		BufferedImage eleScreenshot = fullImg.getSubimage(point1.getX(),
				point1.getY(), eleWidth, eleHeight);
		String picSuffix = "png";
		ImageIO.write(eleScreenshot, picSuffix, screenshot);
		// Copy the element screenshot to disk
		FileUtils.copyFile(screenshot, new File(saveTo));

	}
}
