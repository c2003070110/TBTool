package com.walk_nie.taobao.montBell.main;

import java.util.List;

import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class MontbellBaobeiOffline {

	public static void main(String[] args)  {
		
	}
	
	public void process(){
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		tbLogon(driver);
		int page = 10;
		
		// selling
		String sellingUrl = "XXX";
		for (int i = 0; i < page; i++) {
			driver.get(sellingUrl + i);
			List<WebElement> wes = driver.findElements(By.cssSelector("XX[id=\"XXX\"]"));
			for (WebElement we : wes) {
				if (!isTaget(we)) {
					continue;
				}

				String productId = getProductId(we);
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
				WebElement we1 = we.findElement(By.cssSelector("XX[id=\"XXX\"]"));
				we1.click();
			}
			// DO delete!
			WebElement we = driver.findElement(By.cssSelector("XX[id=\"XXX\"]"));
			we.click();
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

	private String getProductId(WebElement we) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	private boolean isTaget(WebElement we) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	private void tbLogon(WebDriver driver) {

		// TODO
		String logonUrl = "XXX";

		driver.get(logonUrl);
		boolean needLogin = false;
		List<WebElement> wes = driver.findElements(By.cssSelector("XX[id=\"XXX\"]"));
		for(WebElement we :wes){
			if("ログイン".equals(we.getText())){
				needLogin = true;
				we.click();
				break;
			}
		}
		if(!needLogin){
			return;
		}
		
		WebElement el1 = driver.findElement(By.cssSelector("input[id=\"identifierId\"]"));
		el1.sendKeys(NieConfig.getConfig("taobao.user.name"));
		
		wes = driver.findElements(By.tagName("span"));
		for(WebElement we :wes){
			if("XXX".equals(we.getText())){
				we.click();
				break;
			}
		}
		NieUtil.mySleepBySecond(2);
		
		WebElement el2 = driver.findElement(By.cssSelector("input[name=\"password\"]"));
		el2.sendKeys(NieConfig.getConfig("taobao.user.password"));
		
		wes = driver.findElements(By.tagName("span"));
		for(WebElement we :wes){
			if("次へ".equals(we.getText())){
				we.click();
				break;
			}
		}

		NieUtil.mySleepBySecond(2);
		
		List<WebElement> eles = driver.findElements(By.cssSelector("button[id=\"avatar-btn\"]"));
		for(WebElement ele:eles){
			String txt = ele.getAttribute("aria-label");
			if(txt.indexOf("XXXXX") != -1){
				return;
			}
		}
		
		NieUtil.readLineFromSystemIn("TAOBAO login is finished? ANY KEY For already");
	}
 
}
