package com.walk_nie.jppost;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.walk_nie.taobao.util.WebDriverUtil;

public class EmsApply {
	private WebDriver driver;

	public static void main(String[] args) throws Exception {
		if(args.length != 1){
			System.err.println("arg1=count");
			return;
		}
		EmsApply ems = new EmsApply();
		ems.init();
		
		int cnt = Integer.parseInt(args[0]);
		for (int i = 0; i < cnt; i++) {
			ems.apply();
		}
		
		ems.stop();
	}

	public void init() throws Exception {
		driver = WebDriverUtil.getFirefoxWebDriver();
		
		
		String baseUrl = "https://www.int-mypage.post.japanpost.jp/";
		driver.get(baseUrl);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		//driver.get(baseUrl + "mypage/M010000.do");
		try {
			new Select(driver.findElement(By.id("M010000_localeSel")))
					.selectByVisibleText("Japanese");
		} catch (Exception ex) {
		}
		driver.findElement(By.id("M010000_loginBean_id")).clear();
		driver.findElement(By.id("M010000_loginBean_id")).sendKeys(
				"niehpjp@yahoo.co.jp");
		driver.findElement(By.id("M010000_loginBean_pw")).clear();
		driver.findElement(By.id("M010000_loginBean_pw")).sendKeys("nhp12345");
		//driver.findElement(By.cssSelector("img[alt=\"ログイン\"]")).click();
		List<WebElement> list = driver.findElements(By.cssSelector("a"));
		for(WebElement web:list){
			String href = web.getAttribute("href");
			System.out.println(href);
			if(href.indexOf("login") != -1){
				web.click();
				break;
			}
		}
	}

	public void apply() throws Exception {
		driver.findElement(By.linkText("EMSラベル印字オーダー")).click();
		//driver.findElement(By.linkText("Print order (EMS)")).click();
		driver.findElement(By.cssSelector("div.mrgT10 > input.button")).click();
		driver.findElement(By.id("M040500_sel-1")).click();
		driver.findElement(
				By.cssSelector("div.boxMain > div.mrgT10 > input.button"))
				.click();
		//System.out.println(driver.getPageSource());
		List<WebElement> lableCnts = driver.findElements(By.tagName("select"));
		for(WebElement lableCnt: lableCnts){
			String id = lableCnt.getAttribute("id");
			//System.out.println(id);
			if("M040900_emsOrderBean_mLabelCnt_value".equals(id)
					|| "M040900_emsOrderBean_MLabelCnt_value".equals(id)){
				(new Select(lableCnt)).selectByVisibleText("5");
				break;
			}
		}
		// driver.findElement(By.cssSelector("div.boxMain > div.mrgT10 > input.button")).click();
//		new Select(driver.findElement(By
//				.id("M040900_emsOrderBean_mLabelCnt_value")))
//				.selectByVisibleText("5");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.cssSelector("input.button")).click();
	}

	public void stop() throws Exception {
		driver.quit();
	}

}
