package com.walk_nie.dmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class YahooIdDmmRegBuyMain {

	protected BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new YahooIdDmmRegBuyMain().execute();
	}

	public void execute() throws IOException {
		System.setProperty("webdriver.chrome.driver",
				"C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
		System.setProperty("webdriver.gecko.driver",
				"C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");
		GetYahooIdMain yahoo = new GetYahooIdMain();
		DmmRegMain dmmReg = new DmmRegMain();
		DmmBuyMain dmmBuy = new DmmBuyMain();
		List<String> lines = new ArrayList<String>();
		WebDriver driver = new FirefoxDriver();
		try {
			int i = 0;
			while (true) {
				GetYahooIdMain.RegObjInfo regInfo = yahoo.createRegInfo(i);
				i++;

				try {
					yahoo.reg(driver, regInfo);
					if (mywait("yahoo registration is finished?Ready for dmm member register? ENTER;N for No ")) {
						continue;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					if (mywait("ready for continue? ENTER;N for exit ")) {
						break;
					}
					continue;
				}
				try {
					dmmReg.reg(driver, regInfo.id + "@yahoo.co.jp",
							regInfo.pswd);
					lines.add(regInfo.toString());
					if (mywait("Dmm registration is finished? Ready for dmm buy? ENTER;N for exit ")) {
						break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					if (mywait("ready for continue? ENTER;N for exit ")) {
						break;
					}
				}
				try {
					dmmBuy.buy(driver, regInfo.id + "@yahoo.co.jp",
							regInfo.pswd);
					if (mywait("buing is finished?ready for get gift Code? ENTER;N for exit ")) {
						break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					if (mywait("ready for continue? ENTER;N for exit ")) {
						break;
					}
				}
				try {
					getGiftCode(driver, regInfo.id, regInfo.pswd);
					if (mywait("ready for continue? ENTER;N for exit ")) {
						break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					if (mywait("ready for continue? ENTER;N for exit ")) {
						break;
					}
				}
				driver.get("https://www.dmm.com/my/-/login/logout/");
				driver.get("https://login.yahoo.co.jp/config/login?logout=1&.intl=jp&.done=https://mail.yahoo.co.jp&.src=ym");
			}
		} finally {
			File out = new File("./out", "yahooId-dmm.txt");
			while (true)
				try {
					FileUtils.writeLines(out, lines, true);
					break;
				} catch (Exception ex) {
					ex.printStackTrace();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
				}
		}
	}

	private void getGiftCode(WebDriver driver, String string, String pswd) {
		String yUrl = "https://mail.yahoo.co.jp/";
		driver.get(yUrl);
	}

	protected boolean mywait(String hint) throws IOException {
		while (true) {
			// System.out.print("ready for continue? ENTER;N for exit ");
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
