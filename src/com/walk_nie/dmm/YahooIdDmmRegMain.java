package com.walk_nie.dmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieUtil;
import com.walk_nie.ya.GetYahooIdMain;
import com.walk_nie.ya.RegObjInfo;

public class YahooIdDmmRegMain {

	protected BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new YahooIdDmmRegMain().execute();
	}

	public void execute() throws IOException {

		GetYahooIdMain yahoo = new GetYahooIdMain();
		DmmRegMain dmmReg = new DmmRegMain();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		int i = 0;
		while (true) {
			com.walk_nie.ya.RegObjInfo regInfo = yahoo.createRegInfo(i);
			i++;
			try {
				if (!execute1(driver, yahoo, dmmReg, regInfo)) {
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	private boolean execute1(WebDriver driver, GetYahooIdMain yahoo,
			DmmRegMain dmmReg, RegObjInfo regInfo) throws IOException {
		if (!yahoo.reg(driver, regInfo)) {
			System.out.println("Yahoo register is failure!");
			if (mywait("ready for continue? ENTER;N for exit ")) {
				return false;
			}
			return true;
		}
		if(dmmReg.reg(driver, regInfo.id + "@yahoo.co.jp", regInfo.pswd)){
			record(regInfo);
		}
		driver.get("https://www.dmm.com/my/-/login/logout/");
		driver.get("https://login.yahoo.co.jp/config/login?logout=1&.intl=jp&.done=https://mail.yahoo.co.jp&.src=ym");
		return true;
	}

	private void record(RegObjInfo regInfo) {
		// String ftn = new
		// SimpleDateFormat("yyMMddHH:mm:ss.SSS").format(Calendar.getInstance().getTime());
		// File out = new File("./out", "yahooId-dmm" + ftn + ".txt");
		File out = new File("./out", "yahooId-dmm.txt");
		while (true)
			try {
				List<String> lines = Lists.newArrayList();
				lines.add(regInfo.toString());
				FileUtils.writeLines(out, lines, true);
				break;
			} catch (Exception ex) {
				ex.printStackTrace();
				NieUtil.mySleepBySecond(2);
			}
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
