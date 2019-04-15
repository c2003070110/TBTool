package com.walk_nie.taobao.montBell.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;

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
		MontbellAutoOrder order = new MontbellAutoOrder();
		MontbellStockChecker stock = new MontbellStockChecker();

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
				t2 = System.currentTimeMillis();
				dif = t2 - t1;
				if (dif < interval * 1000) {
					log("[SLEEP]zzzZZZzzz...");
					NieUtil.mySleepBySecond((new Long(interval - dif / 1000))
							.intValue());
				}
			} catch (Exception ex) {
				log(ex);
			}
		}
	}

	private void init() {

		logFile = new File(NieConfig.getConfig("montbell.log.file"));
		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(
				java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(
				java.util.logging.Level.OFF);
	}

	private void log(String string) {

		String nowDateTimeStr = getNowDateTime();
		try {
			String str = "[" + nowDateTimeStr + "]" + string + "\n";
			System.out.print(str);
			FileUtils.write(logFile, str, Charset.forName("UTF-8"), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void log(Exception ex) {
		try {
			PrintStream ps = new PrintStream(
					new FileOutputStream(logFile, true));
			ex.printStackTrace(ps);
			ps.flush();
			ps.close();
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getNowDateTime() {
		TimeZone tz2 = TimeZone.getTimeZone("Asia/Tokyo");
		Calendar cal1 = Calendar.getInstance(tz2);
		return DateUtils.formatDate(cal1.getTime(), "yyyy-MM-dd HH:mm:ss");
	}

}
