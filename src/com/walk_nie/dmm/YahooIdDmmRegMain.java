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

public class YahooIdDmmRegMain {

	private String regUrl = "https://account.edit.yahoo.co.jp/registration?.src=ym&.done=https%3A%2F%2Fmail.yahoo.co.jp&src=ym&done=https%3A%2F%2Fmail.yahoo.co.jp&fl=100&no_req_email=true";
    protected  BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new YahooIdDmmRegMain().execute();
	}

	public void execute() throws IOException {
		System.setProperty("webdriver.chrome.driver",
				"C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
		System.setProperty("webdriver.gecko.driver",
				"C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");
		GetYahooIdMain yahoo = new GetYahooIdMain();
		DmmRegMain dmm = new DmmRegMain();
		List<String> lines = new ArrayList<String>();
		WebDriver driver = new FirefoxDriver();
		try {
			int i=0;
			while(true){
				GetYahooIdMain.RegObjInfo regInfo = yahoo.createRegInfo(i);
				i++;
				
				try {
					yahoo.reg(driver, regInfo);
				} catch (Exception ex) {
					ex.printStackTrace();
					if(mywait()){
						break;
					}
					continue;
				}
				mywait();
				try {
					dmm.reg(driver, regInfo.id +"@yahoo.co.jp", regInfo.pswd);

					lines.add(regInfo.toString());
					if(mywait()){
						break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					if(mywait()){
						break;
					}
				}
				mywait();
			}
		} finally {
			File out = new File("./out", "yahooId-dmm.txt");
			FileUtils.writeLines(out, lines, true);
		}
	}
 

	protected boolean mywait() throws IOException {
		while (true) {
			System.out.print("ready for continue? ENTER;N for exit ");
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

    public  BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}

}
