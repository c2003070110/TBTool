package com.walk_nie.mercari;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.ya.auction.YaAutoReview;

public class MercariAuto {

	protected BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		MercariAuto main = new MercariAuto();
		main.execute();
	}

	public void execute() throws IOException {
		WebDriver driver = logon();
		// mywait();

		while (true) {
			try {
				//
				int todoType = choiceTodo();
				if (todoType == 0) {
					MericariAutoGetWon getWon = new MericariAutoGetWon();
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

		String rootUrl = "https://www.mercari.com/jp/mypage/purchase/";
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		driver.get(rootUrl);

		List<WebElement> wes = driver.findElements(By.tagName("input"));
		WebElement emailWe = null;
		WebElement pswdWe = null;
		for (WebElement we : wes) {
			String name = we.getAttribute("name");
			if ("email".equals(name)) {
				emailWe = we;
				break;
			}
		}
		for (WebElement we : wes) {
			String name = we.getAttribute("name");
			if ("password".equals(name)) {
				pswdWe = we;
			}
		}
		if (emailWe != null) {
			emailWe.sendKeys("niehpjp@yahoo.co.jp");
		}
		if (pswdWe != null) {
			pswdWe.sendKeys("nhp12345");
		}
		mywait("login Finished? ENTER for continue");
		return driver;
	}

	protected void mywait(String hint) {
		while (true) {
			System.out.print(hint);
			String line;
			try {
				line = getStdReader().readLine().trim();
				if ("\r\n".equalsIgnoreCase(line)
						|| "\n".equalsIgnoreCase(line) || "".equals(line)) {
					break;
				}
			} catch (IOException e) {
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
