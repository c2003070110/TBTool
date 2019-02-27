package com.walk_nie.taobao.wacom;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.WebDriverSingleton;
import com.walk_nie.util.NieConfig;

/**
 *
 */
public class WacomBaobeiCreatorByCategory {
	

	public static void main(String[] args) throws IOException, URISyntaxException {
		List<String> categoryUrlList = Lists.newArrayList();
		// クリエイティブタブレット Wacom MobileStudio Pro
		categoryUrlList.add("https://store.wacom.jp/products/list.php?category_id=268");
		// 液晶ペンタブレット Wacom Cintiq Pro
		categoryUrlList.add("https://store.wacom.jp/products/list.php?category_id=271");
		// ペンタブレット Wacom Intuos Pro シリーズ
		categoryUrlList.add("https://store.wacom.jp/products/list.php?category_id=275");
		// ペンタブレット Wacom Intuos シリーズ
		categoryUrlList.add("https://store.wacom.jp/products/list.php?category_id=292");
		// Bamboo Stylusシリーズ
		categoryUrlList.add("https://store.wacom.jp/products/list.php?category_id=144");
		// 
		categoryUrlList.add("");
		// 
		categoryUrlList.add("");
		//new WacomBaobeiCreatorByCategory().scanByCategory(categoryUrlList);
		new WacomBaobeiCreatorByCategory().process();

		System.exit(0);
	}

	public void process() throws IOException, URISyntaxException {

		File outputFile = new File(NieConfig.getConfig("wacom.work.root.folder"), 
				String.format("baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		File file = new File(NieConfig.getConfig("wacom.taobao.pulishedbaibei.filepath"));
		List<BaobeiPublishObject> baobeiList = BaobeiUtil.readInPublishedBaobei(file);
		
		String scanFile = NieConfig.getConfig("wacom.scan.category.url.file");
		//
		//double currencyRate = 0.060 + 0.005;
		//double benefitRate = 0.15;
		WacomBaobeiProducer db = new WacomBaobeiProducer();
		db.addProductList(getProductInfoList(scanFile))
				//.setCurrencyRate(currencyRate)
				//.setBenefitRate(benefitRate)
				.setOutputFile(outputFile)
				.setPublishedbaobeiList(baobeiList)
				.process();
	}

	private List<WacomProductObject> getProductInfoList(String categoryFilePath) throws IOException, URISyntaxException {
		
		WebDriver webDriver = WebDriverSingleton.getWebDriver();
		List<WacomProductObject> productInfoList = Lists.newArrayList();
		List<String> productList = Lists.newArrayList();
		
		List<String> categoryUrlList = readCategoryUrls(categoryFilePath);
		
		String outFmt = "%s\t%s\t%s\t%s";
		for (String url : categoryUrlList) {
			webDriver.get(url);
			String categoryId = "";
			List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), Charset.forName("UTF-8"));
			for (NameValuePair param : params) {
				if ("category_id".equalsIgnoreCase(param.getName())) {
					categoryId = param.getValue();
					break;
				}
			}
			WebElement mainEl = webDriver.findElement(By.id("main_column"));
			List<WebElement> itemEls = mainEl.findElements(By.className("list_item_area"));
			for (WebElement we : itemEls) {
				String productId = we.findElement(By.cssSelector("input[name=\"product_id\"]")).getAttribute("value");
				String href = we.findElement(By.tagName("a")).getAttribute("href");
				String name0 = we.findElement(By.tagName("img")).getAttribute("alt");
				String name = we.findElement(By.className("name")).getText();
				if(!productList.contains(productId) && !"ストア限定".equals(name0)){

					System.out.println(String.format(outFmt, productId, href, name, categoryId));

					productList.add(productId);
					WacomProductObject obj = new WacomProductObject();
					obj.categoryId = categoryId;
					obj.productId = productId;
					obj.productName = name;
					obj.productUrl = href;
					productInfoList.add(obj);
				}
			}
		}
		return productInfoList;
	}

	private List<String> readCategoryUrls(String categoryFilePath) throws IOException {

		List<String> adrs = Files.readLines(new File(categoryFilePath), Charset.forName("UTF-8"));
		List<String> outputList = Lists.newArrayList();
		for (String line : adrs) {
			if ("".equals(line)) {
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			outputList.add(line);
		}
		return outputList;
	}

}
