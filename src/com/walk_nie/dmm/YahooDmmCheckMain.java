package com.walk_nie.dmm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.openqa.selenium.WebDriver;

import com.google.common.io.Files;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.ya.YaUtil;

public class YahooDmmCheckMain {

	private String checkFile = "./dmm/check.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		YahooDmmCheckMain check = new YahooDmmCheckMain();
		check.check();
	}

	protected void check() throws IOException, InterruptedException {

		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();

		File tempFile0 = new File(checkFile);
		List<String> votes = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		for (String regMailAddress : votes) {
			String[] sl = regMailAddress.split("\t");
			if (sl.length != 2) {
				System.out.println("[ERROR] Format is WRONG " + regMailAddress);
				continue;
			}
			System.out.println("[INFO] Checking for " + regMailAddress);
			YaUtil.login(driver, sl[0], sl[1]);
			
		}
	}

}
