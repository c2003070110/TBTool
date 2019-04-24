package com.walk_nie.taobao.montBell.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.WebDriver;

import com.walk_nie.util.NieConfig;

public class MontbellPinyinDemon {
	MontbellPinyinMain py = new MontbellPinyinMain();
	private File logFile = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		MontbellPinyinDemon main = new MontbellPinyinDemon();
		main.execute();
	}

	public void execute()  {
		init();
		
		while (true) {
			try {
				py.process();
			} catch (Exception ex) {
				log(ex);
			}
		}
	}
	public void execute(WebDriver driver) throws Exception {
		py.process();
	}

	public void init() {

		logFile = new File(NieConfig.getConfig("montbell.log.file"));
		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(
				java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(
				java.util.logging.Level.OFF);
	}

//	private void log(String string) {
//
//		String nowDateTimeStr = getNowDateTime();
//		try {
//			String str = "[" + nowDateTimeStr + "]" + string + "\n";
//			System.out.print(str);
//			FileUtils.write(logFile, str, Charset.forName("UTF-8"), true);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

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
