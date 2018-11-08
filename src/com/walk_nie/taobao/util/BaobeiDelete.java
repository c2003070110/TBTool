package com.walk_nie.taobao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.util.NieUtil;

public class BaobeiDelete {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new BaobeiDelete().process();
	}
	public  void process() throws IOException {

		WebDriver driver = logon();
		mywait("Login is ready? Y For Yes;N for No","Y");
		
		String sellingPageUrl = "https://auctions.yahoo.co.jp/user/jp/show/mystatus";
		driver.get(sellingPageUrl);
		// fill
		// search
		// xia jia
	}

	private WebDriver logon() {

		String rootUrl = "https://auctions.yahoo.co.jp/user/jp/show/mystatus";
		
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		driver.get(rootUrl);

		if (!driver.findElements(By.id("idBox")).isEmpty()) {
			WebElement el1 = driver.findElements(By.id("idBox")).get(0);
			el1.findElement(By.id("username")).sendKeys("yiyi2014jp");
			driver.findElement(By.id("btnNext")).click();
		}
		NieUtil.mySleepBySecond(2);
		driver.findElement(By.id("passwd")).sendKeys("dengyi");
		 driver.findElement(By.id("btnSubmit")).click();
		return driver;
	}

	protected void mywait(String hint, String answer) throws IOException {
		while (true) {
			System.out.print(hint);
			String line = getStdReader().readLine().trim();
			if (answer.equalsIgnoreCase(line)) {
				break;
			}
		}
	}
	protected BufferedReader stdReader = null;
	public BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}
}
