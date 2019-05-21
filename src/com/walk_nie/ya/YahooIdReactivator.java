package com.walk_nie.ya;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieUtil;

public class YahooIdReactivator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new YahooIdReactivator().execute();
	}

	public void execute() throws IOException {

		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		List<RegObjInfo> list = readin();
		for(RegObjInfo info:list){
			activate(driver,info);
			NieUtil.mySleepBySecond(5);
		}
	}
	private void activate(WebDriver driver, RegObjInfo info) {
		// email
		driver.get(YaUtil.logoutUrl);
		System.out.println("[reactivator][start]id=" + info.id);
		NieUtil.mySleepBySecond(5);
		String url = "https://account.edit.yahoo.co.jp/registration?.src=ym&.done=https%3A%2F%2Fmail.yahoo.co.jp&src=ym&done=https%3A%2F%2Fmail.yahoo.co.jp&fl=100&no_req_email=true";
		driver.get(url);
		YaUtil.login(driver, info.id, info.pswd);

		try {
			driver.findElement(By.cssSelector("input[id=\"dontprotectaccount\"]")).click();
			List<WebElement> eles = driver.findElements(By.cssSelector("input[id=\"dontprotectaccount\"]"));
			for(WebElement we:eles){
				String val = we.getAttribute("value");
				if(val.indexOf("利用再開") != -1){
					System.out.println("[reactivator][activate]id=" + info.id);
					we.click();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[reactivator][finish]id=" + info.id);
		
	}

	private List<RegObjInfo> readin() {
		List<RegObjInfo> objList = Lists.newArrayList();
		File file = YaUtil.getIdListFile();
		try {
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			for(String line :lines){
				objList.add(RegObjInfo.parse(line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return objList;
	}
}
