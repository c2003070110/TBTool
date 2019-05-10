package com.walk_nie.taobao.montBell.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.seleniumhq.jetty9.util.StringUtil;

import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;

// xiaJia
public class MontbellBaobeiOffline {

	public static void main(String[] args)  {
		MontbellBaobeiOffline main = new MontbellBaobeiOffline();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		main.process(driver);
	}
	
	public void process(WebDriver driver){
		
		String sellingUrl = "https://item.publish.taobao.com/taobao/manager/render.htm?tab=on_sale&pagination.current=";
		driver.get(sellingUrl);
		
		TaobaoUtil.login(driver);
		
		WebElement weRoot = driver.findElement(By.cssSelector("div[id=\"root\"]"));
		
		int page = 10;
		List<WebElement> eles = weRoot.findElements(By.className("next-pagination"));
		if (!eles.isEmpty()) {
			WebElement we = eles.get(0);
			List<WebElement> eles1 = we.findElements(By.className("next-pagination-display"));
			if (!eles1.isEmpty()) {
				String txt = eles1.get(0).getText();
				if (txt.indexOf("/") != -1) {
					page = Integer.parseInt(txt.substring(txt.indexOf("/") + 1));
				}
			}
		}
		
		for (int i = 1; i <= page; i++) {
			driver.get(sellingUrl + i);
			WebElement weRootLoop = driver.findElement(By.cssSelector("div[id=\"root\"]"));
			List<WebElement> eles1 = weRootLoop.findElements(By.className("next-table-body"));
			if (eles1.isEmpty()) continue;
			List<WebElement> wes = eles1.get(0).findElements(By.tagName("tr"));
			boolean hasOffOne = false;
			for (WebElement we : wes) {
				String className = we.getAttribute("class");
				if(className.indexOf("next-table-row") == -1) continue;

				List<WebElement> westd = we.findElements(By.tagName("td"));
				if (westd.isEmpty()) continue;
				if (westd.size() != 8) continue;
				WebElement wetd2 = westd.get(1);
				
				//List<WebElement> wes1 = wetd2.findElements(By.tagName("span"));
				//if (wes1.isEmpty()) continue;
				String desc = wetd2.getText();
				if(desc.indexOf("MTBL") == -1) continue;
				
				List<WebElement> wes2 = wetd2.findElements(By.tagName("a"));
				if (wes2.isEmpty()) continue;
				String title = wes2.get(0).getText();
				Pattern p = Pattern.compile("\\d+");
				Matcher m = p.matcher(title); 
				String productId = null;
				while (m.find()) {
					productId = m.group();
				}
				if(StringUtil.isBlank(productId))continue;
				if(productId.length() >7) productId = productId.substring(0, 7);

				String url = MontBellUtil.productUrlPrefix_en + productId;
				boolean alive = testProduct(url);
				if (alive) {
					continue;
				}
				url = MontBellUtil.productUrlPrefix_en_fo + productId;
				alive = testProduct(url);
				if (alive) {
					continue;
				}
				url = MontBellUtil.productUrlPrefix + productId;
				alive = testProduct(url);
				if (alive) {
					continue;
				}
				url = MontBellUtil.productUrlPrefix_fo + productId;
				alive = testProduct(url);
				if (alive) {
					continue;
				}

				System.out.println("[INFO][DELETE]product=" + productId);
				// because of not alive yet,to delete that
				// set to delete target
				WebElement we1 = we.findElement(By.cssSelector("input[type=\"checkbox\"]"));
				we1.click();
				hasOffOne = true;
			}
			if(!hasOffOne) continue;
			// DO delete!
			eles = weRootLoop.findElements(By.className("list-page-action-toolbar"));
			if (!eles.isEmpty()) {
				WebElement we = eles.get(0);
				eles1 = we.findElements(By.cssSelector("button[name=\"batchDownShelfItemBtn\"]"));
				if (!eles1.isEmpty()) {
					eles1.get(0).click();
					eles1 = driver.findElements(By.cssSelector("div[class=\"next-feedback-title\"]"));
					if(!eles1.isEmpty()){
						eles1 = driver.findElements(By.cssSelector("div[id=\"dialog-footer-2\"]"));
						if(!eles1.isEmpty()){
							eles1.get(0).findElement(By.tagName("button")).click();
						}
					}
				}
			}
		}
	}

	private boolean testProduct(String url) {
		try{
			Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
			if (doc.select("div.rightCont").size() == 0) {
				System.err.println("[ERROR] This is NOT product URL." + url);
				return false;
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}

 
}
