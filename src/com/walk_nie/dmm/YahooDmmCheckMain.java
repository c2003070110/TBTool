package com.walk_nie.dmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.common.io.Files;

public class YahooDmmCheckMain {

	private String loginUrl = "https://login.yahoo.co.jp/config/login?.src=ym&.done=https%3A%2F%2Fmail.yahoo.co.jp%2F";
	private String logoutUrl = "https://login.yahoo.co.jp/config/login?logout=1&.intl=jp&.done=https://mail.yahoo.co.jp&.src=ym";
	protected BufferedReader stdReader = null;
	private String checkFile = "./dmm/check.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		YahooDmmCheckMain check = new YahooDmmCheckMain();
		check.check();
	}

	protected void check() throws IOException, InterruptedException {

		System.setProperty("webdriver.chrome.driver",
				"C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
		System.setProperty("webdriver.gecko.driver",
				"C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");
		WebDriver driver = new FirefoxDriver();

		File tempFile0 = new File(checkFile);
		List<String> votes = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		for (String regMailAddress : votes) {
			String[] sl = regMailAddress.split("\t");
			if (sl.length != 2) {
				System.out.println("[ERROR] Format is WRONG " + regMailAddress);
				continue;
			}
			System.out.println("[INFO] Checking for " + regMailAddress);
			driver.get(loginUrl);
			driver.findElement(By.id("username")).sendKeys(sl[0]);
			driver.findElement(By.id("btnNext")).click();
			Thread.sleep(3 * 1000);
			By pswdBy = By.id("passwd");
			By btnSubmitBy = By.id("btnSubmit");
			try {
				driver.findElement(pswdBy).sendKeys(sl[1]);
				driver.findElement(btnSubmitBy).click();
			} catch (Exception e) {
				System.out.println("[ERROR] Checking for " + regMailAddress);
				continue;
			}
			mywait("ready for next ? ENTER;N for exit ");
			driver.get(logoutUrl);
		}
	}

	protected boolean mywait(String hint) throws IOException {
		while (true) {
			System.out.print(hint);
			String line = getStdReader().readLine().trim();
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				return false;
			}
			if ("n".equalsIgnoreCase(line)) {
				return true;
			}
		}
	}

	public BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}

}
