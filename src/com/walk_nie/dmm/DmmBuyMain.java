package com.walk_nie.dmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.common.io.Files;

public class DmmBuyMain {

	private String loginUrl = "https://www.dmm.com/my/-/login/=/path=Sg__/";
	private String buyItemUrl = "http://dlsoft.dmm.com/detail/dmmgames_0045/";
	protected BufferedReader stdReader = null;
	private String regFile = "./dmm/dmmBuy.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		DmmBuyMain reg = new DmmBuyMain();
		boolean isDemon = false;
		if (args.length == 1) {
			isDemon = args[0].equals("1");
		}
		if (isDemon) {
			reg.demon();
		} else {
			reg.oneTime();
		}
	}

	private void demon() {
		try {
			buy();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void oneTime() throws Exception {
		long updateTime = System.currentTimeMillis();
		while (true) {
			File tempFile0 = new File(regFile);
			if (updateTime < tempFile0.lastModified()) {
				updateTime = tempFile0.lastModified();
				buy();
			}
		}
	}
	protected void buy(WebDriver driver,String mailAdd,String password) throws IOException, InterruptedException {

		driver.get(buyItemUrl);
		List<WebElement> submitList = driver.findElements(By.tagName("input"));
		WebElement buyBtnWe = null;
		for (WebElement we : submitList) {
			if ("submit".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("購入する".equals(we.getAttribute("value"))|| "バスケットに入れる".equals(we.getAttribute("value"))) {
					buyBtnWe = we;
				}
			}
		}
		if (buyBtnWe == null) {
			System.err.println("ERROR. Do NOT Buy !");
			return;
		}
		buyBtnWe.click();

		submitList = driver.findElements(By.tagName("span"));
		WebElement pointChargeWe = null;
		for (WebElement we : submitList) {
			if ("ポイントをチャージする".equalsIgnoreCase(we.getText())) {
				pointChargeWe = we;
			}
		}
		if (pointChargeWe == null) {
			System.err.println("ERROR. Do NOT point charge button");
			return;
		}
		pointChargeWe.click();
/*
		submitList = driver.findElements(By.tagName("img"));
		WebElement bitCashWe = null;
		for (WebElement we : submitList) {
			if ("BitCash".equalsIgnoreCase(we.getAttribute("alt"))) {
				bitCashWe = we;
			}
		}
		if (bitCashWe == null) {
			System.err.println("ERROR. Do NOT BitCash button");
			return;
		}
		bitCashWe.click();

		submitList = driver.findElements(By.tagName("span"));
		WebElement bitCahsNextWe = null;
		for (WebElement we : submitList) {
			if ("次へ".equalsIgnoreCase(we.getText())) {
				bitCahsNextWe = we;
			}
		}
		if (bitCahsNextWe == null) {
			System.err.println("ERROR. Do NOT BitCash Next Button");
			return;
		}
		bitCahsNextWe.click();

		driver.findElement(By.id("hiragana_id")).sendKeys("ふいおなりゆてへわこくゆひいしら");
		driver.findElement(By.id("submit")).click();
		*/
	}

	protected void buy() throws IOException, InterruptedException {

		System.setProperty("webdriver.chrome.driver",
				"C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
		System.setProperty("webdriver.gecko.driver",
				"C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");
		WebDriver driver = new FirefoxDriver();
		
		File tempFile0 = new File(regFile);
		List<String> votes = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		for (String regMailAddress : votes) {
			int idx = regMailAddress.indexOf("@");
			if (idx == -1) {
				System.out.println("[ERROR] is Not Address" + regMailAddress);
				continue;
			}
			String pswd = regMailAddress.substring(0, idx);
			driver.get(loginUrl);
			driver.findElement(By.id("login_id")).sendKeys(regMailAddress);
			driver.findElement(By.id("password")).sendKeys(pswd);
			
			// driver.findElement(By.name("submit")).click();
			List<WebElement> submitList = driver.findElements(By
					.tagName("input"));
			WebElement loginBtnWe = null;
			for (WebElement we : submitList) {
				if ("submit".equalsIgnoreCase(we.getAttribute("type"))) {
					if ("ログイン".equals(we.getAttribute("value"))) {
						loginBtnWe = we;
					}
				}
			}
			if (loginBtnWe == null) {
				System.err.println("ERROR. Do NOT Found Login Button!");
				break;
			}
			loginBtnWe.click();

			Thread.sleep(1000 * 2);
			
			buy();
			// DMMポイントのチャージ
			//driver.findElement(By.id("submit")).click();
			


			 mywait() ;
		}
	}
	protected boolean mywait() throws IOException {
		while (true) {
			System.out.print("ready for continue? ENTER;N for exit ");
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

    public  BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}

}
