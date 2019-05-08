package com.walk_nie.taobao.montBell.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;

import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class MontbellOrderDemon {
	private File logFile = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		MontbellOrderDemon main = new MontbellOrderDemon();
		main.execute();
	}

	public void execute() {
		init();

		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		MontbellAutoOrder order = new MontbellAutoOrder(driver);
		MontbellStockChecker stock = new MontbellStockChecker();
		MontbellPinyinMain pinyin = new MontbellPinyinMain();
		int interval = 0;// second
		while (true) {
			try {
				long t1 = System.currentTimeMillis();

				order.processForWebService();
				long t2 = System.currentTimeMillis();
				long dif = t2 - t1;
				if (dif > interval * 1000) {
					//continue;
				}
				stock.processForWebService();
				
				pinyin.processForWebService();
				t2 = System.currentTimeMillis();
				dif = t2 - t1;
				if (dif < interval * 1000) {
					NieUtil.log(logFile, "[SLEEP]zzzZZZzzz...");
					NieUtil.mySleepBySecond((new Long(interval - dif / 1000))
							.intValue());
				}
			} catch (Exception ex) {
				NieUtil.log(logFile, ex);
			}
		}
	}

	public void execute(WebDriver driver) {
		MontbellAutoOrder order = new MontbellAutoOrder(driver);
		MontbellStockChecker stock = new MontbellStockChecker();
		MontbellPinyinMain pinyin = new MontbellPinyinMain();

		try {
			order.processForWebService();
		} catch (IOException e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Montbell][Ordering]" + e.getMessage());
			NieUtil.log(logFile, e);
		}

		try {
			stock.processForWebService();
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile,
					"[ERROR][Montbell][StockCheck]" + e.getMessage());
			NieUtil.log(logFile, e);
		}

		try {
			pinyin.processForWebService();
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Montbell][PINYIN]" + e.getMessage());
			NieUtil.log(logFile, e);
		}

	}

	public void init() {

		logFile = new File(NieConfig.getConfig("montbell.log.file"));
		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(
				java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(
				java.util.logging.Level.OFF);
	}

}
