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

        System.setProperty("webdriver.chrome.driver", "C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
        System.setProperty("webdriver.gecko.driver", "C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");
        //WebDriver driver =  new ChromeDriver();
        WebDriver driver =  new FirefoxDriver();
        //WebDriver driver =  new HtmlUnitDriver();
		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);

		driver.get("http://pan.baidu.com/");
		mysleep(2);
		for (String pan : panList) {
			String[] panArr = pan.split("\t");
			String fileName = panArr[0].trim();
			String url = panArr[1].trim();
			String pass = panArr[2].trim();

			driver.get(url);
			mysleep(1);
//System.out.println(driver.getPageSource());
			driver.findElement(By.id("accessCode")).sendKeys(pass);
			driver.findElement(By.id("submitBtn")).findElement(By.tagName("a")).click();
			mysleep(2);
			String fileNameWeb = null;
			try {
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

	protected void mysleep(int second) throws IOException {

		long now = System.currentTimeMillis();
		long millis = second * 1000 + now;
		while (true) {
			if (millis < System.currentTimeMillis())
				break;
		}

	}
}
