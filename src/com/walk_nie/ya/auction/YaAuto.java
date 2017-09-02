package com.walk_nie.ya.auction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class YaAuto {

	protected BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		YaAuto main = new YaAuto();
		main.execute();
	}

	public void execute() throws IOException {
		WebDriver driver = logon();
		//mywait();

		while (true) {
			try {
				//
				int todoType = choiceTodo();
				if (todoType == 0) {
					YaAutoGetWon getWon = new YaAutoGetWon();
					getWon.execute(driver);
				}
				if (todoType == 1) {
					YaAutoReview review = new YaAutoReview();
					review.execute(driver);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private int choiceTodo() {
		int type = 0;
		try {
			System.out.print("Type of todo : ");
			System.out.println("0:落札分取得;\n" + "1:評価;\n" + "2:...;\n");

			stdReader = getStdReader();
			while (true) {
				String line = stdReader.readLine();
				if ("0".equals(line.trim())) {
					type = 0;
					break;
				} else if ("1".equals(line.trim())) {
					type = 1;
					break;
				} else if ("2".equals(line.trim())) {
					type = 2;
					break;
				} else {
					System.out.println("Listed number only!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;
	}

	private WebDriver logon() {

		String rootUrl = "https://auctions.yahoo.co.jp/user/jp/show/mystatus";
		if(System.getProperty("webdriver.chrome.driver")==null || "".equals(System.getProperty("webdriver.chrome.driver"))){
			System.setProperty("webdriver.chrome.driver",
					"C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
		}
		if(System.getProperty("webdriver.gecko.driver")==null || "".equals(System.getProperty("webdriver.gecko.driver"))){
			System.setProperty("webdriver.gecko.driver",
					"C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");
		}
		// WebDriver driver = new ChromeDriver();
		WebDriver driver = new FirefoxDriver();
		driver.get(rootUrl);

		if (!driver.findElements(By.id("idBox")).isEmpty()) {
			WebElement el1 = driver.findElements(By.id("idBox")).get(0);
			el1.findElement(By.id("username")).sendKeys("yiyi2014jp");
			driver.findElement(By.id("btnNext")).click();

			// driver.findElement(By.id("passwd")).sendKeys("dengyi");
			// driver.findElement(By.id("btnSubmit")).click();
		}
		return driver;
	}

	protected void mywait() throws IOException {
		while (true) {
			System.out.print("ready for continue? ENTER;N for exit ");
			String line = getStdReader().readLine().trim();
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				break;
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
