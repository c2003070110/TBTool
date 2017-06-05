package com.walk_nie.taobao.akb48.sousenkyo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.common.io.Files;

public class VoteMain {

	public static BufferedReader stdReader = null;
	private String screenshotOutRoot = "c:/temp/vote/";
	private String voteFile = "./shipment/vote.txt";
	private String candidatesFile = "./shipment/candidateList.txt";

	public static void main(String[] args) throws IOException {

		VoteMain main = new VoteMain();
		main.vote();
	}

	protected void vote() throws IOException {
		File tempFile0 = new File(voteFile);
		List<String> votes = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		File tempFile = new File(candidatesFile);
		List<String> candidates = Files.readLines(tempFile,
				Charset.forName("UTF-8"));

		Logger.getLogger("org.openqa.selenium").setLevel(
				java.util.logging.Level.OFF);

		System.setProperty("webdriver.chrome.driver",
				"C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
		System.setProperty("webdriver.gecko.driver",
				"C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");

		FirefoxOptions opts = new FirefoxOptions().setLogLevel(Level.OFF);
		DesiredCapabilities capabilities = opts.addTo(DesiredCapabilities
				.firefox());
		capabilities.setCapability("marionette", true);
		WebDriver driver = new FirefoxDriver(capabilities);
		driver.manage().window().setPosition(new Point(10, 10));
		driver.manage().window().setSize(new Dimension(570, 780));
		// driver.manage().window().maximize() ;

		for (String vote : votes) {
			String[] voteArr = vote.split("\t");
			String orderNo = voteArr[0];
			String taobaoId = voteArr[1];
			String candidator = voteArr[2];
			String[] voteCode = voteArr[3].split(" ");

			String candidatorUrl = "";
			for (String can : candidates) {
				String[] name = can.split("\t");
				if (candidator.equals(name[0].trim())) {
					candidatorUrl = name[1].trim();
					break;
				}
			}
			if ("".equals(candidatorUrl)) {
				System.out.println("[NO URL For][" + candidator + "]");
				continue;
			}
			driver.get(candidatorUrl);
			String fileNamePrefix = orderNo + "-" + taobaoId + "/"
					+ voteCode[0] + "_" + voteCode[1] + "-" + candidator + "-";
			driver.findElement(By.name("serial_code_1")).sendKeys(
					voteCode[0].trim());
			driver.findElement(By.name("serial_code_2")).sendKeys(
					voteCode[1].trim());

			WebElement submitWE = driver.findElement(By
					.cssSelector("input[type=\"submit\"]"));

			submitWE.sendKeys(Keys.PAGE_DOWN);
			String fileNm = screenshotOutRoot + fileNamePrefix + "-1.jpg";
			screenShot(driver, fileNm);
			mysleep(2);
			submitWE.click();

			mysleep(1);
			WebElement submitWE2 = driver.findElement(By
					.cssSelector("input[type=\"submit\"]"));
			submitWE2.sendKeys(Keys.PAGE_DOWN);
			fileNm = screenshotOutRoot + fileNamePrefix + "-2.jpg";
			screenShot(driver, fileNm);
			mysleep(1);

		}
		driver.close();
	}

	private void screenShot(WebDriver driver, String string) throws IOException {
		File scrFile = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);
		File file = new File(string);
		if (!file.exists())
			file.getParentFile().mkdirs();
		FileUtils.copyFile(scrFile, file);
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
