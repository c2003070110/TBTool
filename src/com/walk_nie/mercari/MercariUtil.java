package com.walk_nie.mercari;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;

public class MercariUtil {

	protected static BufferedReader stdReader = null;

	public static WebDriver logon() {

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
			emailWe.sendKeys(NieConfig.getConfig("mercari.user.id"));
		}
		if (pswdWe != null) {
			pswdWe.sendKeys(NieConfig.getConfig("mercari.user.password"));
		}
		return driver;
	}

	protected static boolean mywait(String hint) {
		while (true) {
			System.out.print(hint);
			String line;
			try {
				line = getStdReader().readLine().trim();
				if ("\r\n".equalsIgnoreCase(line)
						|| "\n".equalsIgnoreCase(line) || "".equals(line)) {
					return true;
				}else if ("n".equalsIgnoreCase(line)){
					return false;
				}
			} catch (IOException e) {
			}
		}
	}

	public static BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}

	public static void writeOut(List<String> outputList, String outFileName) throws IOException {

		File oFile = new File(outFileName);
		String today = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy-MM-dd");
		FileUtils.write(oFile, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		for (String str : outputList) {
			FileUtils.write(oFile, str + "\n", Charset.forName("UTF-8"), true);
		}
	}
}
