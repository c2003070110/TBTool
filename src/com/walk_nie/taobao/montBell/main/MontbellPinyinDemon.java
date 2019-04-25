package com.walk_nie.taobao.montBell.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;

import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

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
				NieUtil.log(logFile, ex);
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

}
