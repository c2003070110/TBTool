package com.walk_nie.taobao.akb48.sousenkyo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.io.Files;

public class VerifyPan {

	private String panUrl = "./shipment/panUrl.txt";

	public static void main(String[] args) throws IOException {

		VerifyPan main = new VerifyPan();

		main.vertify();
	}

	protected void vertify() throws IOException {

		File tempFile = new File(panUrl);
		List<String> panList = Files.readLines(tempFile,
				Charset.forName("UTF-8"));

		System.setProperty("webdriver.chrome.driver",
				"C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
		System.setProperty("webdriver.gecko.driver",
				"C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");
		// WebDriver driver = new ChromeDriver();
		WebDriver driver = new FirefoxDriver();
		// WebDriver driver = new HtmlUnitDriver();
		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(
				java.util.logging.Level.OFF);

		driver.get("http://pan.baidu.com/");
		mysleep(0.5);
		for (String pan : panList) {
			String[] panArr = pan.split("\t");
			String fileName = panArr[0].trim();
			String url = panArr[1].trim();
			String pass = panArr[2].trim();
			driver.get(url);
			String fileNameWeb = null;
			try {
				mysleep(1);
				// System.out.println(driver.getPageSource());
				driver.findElement(By.id("accessCode")).sendKeys(pass);
				driver.findElement(By.id("submitBtn"))
						.findElement(By.tagName("a")).click();
				mysleep(2);
				try{
					String psswdError = driver.findElement(By.id("tip")).getText();
					if(!"".equals(psswdError)) {
						System.out.println(String.format("[%s][%s][%s][%s]", url, fileName,
								fileNameWeb, psswdError));
						continue;
					}
				} catch (Exception e) {
				}
				WebDriverWait wait1 = new WebDriverWait(driver,3);
				wait1.until(new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver driver) {
						while (true){
							try {
								String title = driver.findElement(By
										.cssSelector("h2[class=\"file-name\"]")).getAttribute("title");
								if(title.toLowerCase().endsWith(".JPG".toLowerCase())){
									return true;
								}
							} catch (Exception e) {

							}
						}
					}
				});
				mysleep(2);
				WebElement el = driver.findElement(By
						.cssSelector("h2[class=\"file-name\"]"));
				fileNameWeb = el.getAttribute("title");
			} catch (Exception e) {
			}
			System.out.println(String.format("[%s][%s][%s][%s]", url, fileName,
					fileNameWeb, fileName.equals(fileNameWeb)));
		}
		driver.close();
	}

	protected void mysleep(double second) throws IOException {

		long now = System.currentTimeMillis();
		long millis = (int) second * 1000 + now;
		while (true) {
			if (millis < System.currentTimeMillis())
				break;
		}

	}
}
