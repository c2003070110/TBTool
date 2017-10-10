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
				System.out.println("Complete!continue for typing input in ["+tempFile0.getAbsolutePath()+"]");
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

			mywait();
		}
		driver.close();
	}

	protected void reg(WebDriver driver,String mailAdd,String password) throws IOException {

		int idx = mailAdd.indexOf("@");
		if (idx == -1) {
			System.out.println("[ERROR] is Not Address" + mailAdd);
			return;
		}
		String pswd = mailAdd.substring(0, idx);
		driver.get(regUrl);
		driver.findElement(By.id("mailaddress")).sendKeys(mailAdd);
		driver.findElement(By.id("password")).sendKeys(pswd);
		driver.findElement(By.className("box-mailmagazine")).click();
		driver.findElement(By.name("submit")).click();

		List<WebElement> submitList = driver.findElements(By.tagName("a"));
		WebElement selectedWeY = null;
		String yUrl = "https://mail.yahoo.co.jp/";
		for (WebElement we : submitList) {
			if (yUrl.equalsIgnoreCase(we.getAttribute("href"))) {
				selectedWeY = we;
				break;
			}
		}
		if (selectedWeY != null) {
			driver.get(yUrl);
			//driver.findElement(By.id("username")).sendKeys(mailAdd);
			//driver.findElement(By.id("btnNext")).click();
			//driver.findElement(By.id("passwd")).sendKeys(password);
			mywait();
			driver.get("https://www.dmm.com/my/-/login/logout/");
			return;
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
