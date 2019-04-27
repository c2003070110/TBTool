package com.walk_nie;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;

import com.walk_nie.amzn.AmznAutoRegistGiftCardDeamon;
import com.walk_nie.taobao.montBell.main.MontbellOrderDemon;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;
import com.walk_nie.webmoney.WebMoneyDemon;
import com.walk_nie.ya.auction.YaAucDemon;

public class MyDeamon {
	private File logFile = null;
	protected YaAucDemon yaAucDemon = new YaAucDemon();
	protected WebMoneyDemon webMoneyDemon = new WebMoneyDemon();
	//protected MontbellPinyinDemon montbellPinyinDemon = new MontbellPinyinDemon();
	protected MontbellOrderDemon montbellOrderDemon = new MontbellOrderDemon();
	protected AmznAutoRegistGiftCardDeamon amznAutoRegistGiftCardDeamon = new AmznAutoRegistGiftCardDeamon();
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		MyDeamon main = new MyDeamon();
		main.execute();
	}
	public void execute() throws IOException {
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		init(driver);
		
		int interval = Integer.parseInt(NieConfig
				.getConfig("my.deamon.interval"));// second
		while (true) {
			try {
				long t1 = System.currentTimeMillis();
				
				yaAucDemon.execute(driver);
				webMoneyDemon.execute(driver);
				//montbellPinyinDemon.execute(driver);
				montbellOrderDemon.execute(driver);
				amznAutoRegistGiftCardDeamon.execute(driver);
				
				long t2 = System.currentTimeMillis();
				long dif = t2 - t1;
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


	private void init(WebDriver driver) throws IOException {

		logFile = new File(
				NieConfig.getConfig("my.deamon.log.file"));

		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
		
		yaAucDemon.init(driver);
		webMoneyDemon.init();
		//montbellPinyinDemon.init();
		montbellOrderDemon.init();
		amznAutoRegistGiftCardDeamon.init(driver);
	}
	
}
