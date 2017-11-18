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

public class DmmRegMain {

	private String regUrl = "https://www.dmm.com/my/-/register/";
	protected BufferedReader stdReader = null;
	private String regFile = "./dmm/dmmReg.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DmmRegMain reg = new DmmRegMain();
		boolean isDemon = false;
		if (args.length == 1) {
			isDemon = args[0].equals("1");
		}
		if (isDemon) {
			reg.demonReg();
		} else {
			reg.oneTimeReg();
		}
	}

	private void demonReg() {
		try {
			reg();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void oneTimeReg() throws IOException {
		long updateTime = System.currentTimeMillis();
		while (true) {

			File tempFile0 = new File(regFile);
			if (updateTime < tempFile0.lastModified()) {
				updateTime = tempFile0.lastModified();
				reg();
				System.out.println("Complete!continue for typing input in ["
						+ tempFile0.getAbsolutePath() + "]");
			}
		}
	}

	protected void reg() throws IOException {

		System.setProperty("webdriver.chrome.driver",
				"C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
		System.setProperty("webdriver.gecko.driver",
				"C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");
		WebDriver driver = new FirefoxDriver();

		File tempFile0 = new File(regFile);
		List<String> votes = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		for (String regMailAddress : votes) {
			String[] voteArr = regMailAddress.split("\t");
			String mailAdd = "";
			String password = "";
			if (voteArr.length == 1) {
				mailAdd = voteArr[0];
			} else if (voteArr.length >= 2) {
				mailAdd = voteArr[0];
				password = voteArr[1];
			}
			reg(driver, mailAdd, password);

			mywait("ready for continue? ENTER;N for exit ");
		}
		driver.close();
	}

	protected boolean reg(WebDriver driver, String mailAdd, String password)
			throws IOException {

		driver.get("https://www.dmm.com/my/-/login/logout/");
		int idx = mailAdd.indexOf("@");
		if (idx == -1) {
			System.out.println("[ERROR] is Not Address" + mailAdd);
			return false;
		}
		String pswd = mailAdd.substring(0, idx);
		driver.get(regUrl);
		WebElement emailA = findElementEmail(driver);
		if (emailA == null) {
			if (mywait("1 signup is failer！has to manual DO！ready for continue? ENTER;N for exit")) {
				return false;
			}
		}
		emailA.sendKeys(mailAdd);
		WebElement wePssd = findElementPassword(driver);
		if (wePssd == null) {
			if (mywait("2 signup is failer！has to manual DO！ready for continue? ENTER;N for exit")) {
				return false;
			}
		}
		wePssd.sendKeys(pswd);
		// driver.findElement(By.className("box-mailmagazine")).click();
		try{
			driver.findElement(By.className("box-checkmark")).click();
		}catch(Exception ex){
		}
		WebElement submitWE = findSubmitWebElement(driver);
		if(submitWE == null){
			if (mywait("3 signup is failer！has to manual DO！ready for continue? ENTER;N for exit")) {
				return false;
			}
		}
		submitWE.click();

		List<WebElement> submitList = driver.findElements(By.tagName("a"));
		WebElement selectedWeY = null;
		String yUrl = "https://mail.yahoo.co.jp/";
		for (WebElement we : submitList) {
			if (yUrl.equalsIgnoreCase(we.getAttribute("href"))) {
				selectedWeY = we;
				break;
			}
		}
		if (selectedWeY == null) {
			if (mywait("4 signup is failer！has to manual DO！ready for continue? ENTER;N for exit")) {
				return false;
			}
		}
		driver.get(yUrl);
		try {
			Thread.sleep(1000 * 1);
		} catch (InterruptedException e) {
		}
		WebElement msgWE = driver.findElement(By.id("msg-list"));
		submitList = msgWE.findElements(By.className("subj"));
		WebElement selectedWe1 = null;
		for (WebElement we : submitList) {
			String attr = we.getAttribute("title");
			if (attr.indexOf("DMM：会員登録認証メール") != -1) {
				selectedWe1 = we;
				break;
			}
		}
		if (selectedWe1 == null) {
			if (mywait("5 signup is failer！has to manual DO！ready for continue? ENTER;N for exit")) {
				return false;
			}
		}
		selectedWe1.click();
		WebElement msgPrevWE = driver.findElement(By.id("msg-preview"));
		submitList = msgPrevWE.findElements(By.tagName("a"));
		selectedWe1 = null;
		for (WebElement we : submitList) {
			String href = we.getAttribute("href");
			if (href != null && href.indexOf("signup") != -1
					&& href.indexOf("complete") != -1) {
				selectedWe1 = we;
				break;
			}
		}
		if (selectedWe1 == null) {
			if (mywait("6 signup is failer！has to manual DO！ready for continue? ENTER;N for exit")) {
				return false;
			}
		}
		selectedWe1.click();
		try {
			Thread.sleep(1000*1);
		} catch (InterruptedException e) {
		}
		return true;
	}

	private WebElement findElementEmail(WebDriver driver) {
		List<WebElement> submitList = driver.findElements(By.tagName("input"));
		for (WebElement we : submitList) {
			if ("メールアドレス".equals(we.getAttribute("placeholder"))) {
				return we;
			}
		}
		
		try {
			WebElement we = driver.findElement(By.id("mailaddress"));
			if (we != null) {
				return we;
			}
		} catch (Exception e) {

		}
		try {
			WebElement we = driver.findElement(By.id("email"));
			if (we != null) {
				return we;
			}
		} catch (Exception e) {

		}
		return null;
	}

	private WebElement findElementPassword(WebDriver driver) {
		List<WebElement> submitList = driver.findElements(By.tagName("input"));
		for (WebElement we : submitList) {
			if ("パスワード".equals(we.getAttribute("placeholder"))) {
				return we;
			}
		}
		
		try {
			WebElement we = driver.findElement(By.id("password"));
			if (we != null) {
				return we;
			}
		} catch (Exception e) {
		}
		return null;
	}

	private WebElement findSubmitWebElement(WebDriver driver) {
		List<WebElement> submitList = driver.findElements(By.tagName("input"));
		for (WebElement we : submitList) {
			if ("認証メールを送信".equals(we.getAttribute("value"))) {
				return we;
			}
		}
		return null;
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
