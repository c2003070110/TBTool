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
		//NieUtil.mySleepBySecond(5);
		String url = "https://login.yahoo.co.jp/config/login?.src=ym&.done=https%3A%2F%2Fmail.yahoo.co.jp%2F";
		driver.get(url);
		YaUtil.login(driver, info.id, info.pswd);

		try {
			reMailAdress(driver);
			driver.findElement(By.cssSelector("label[for=\"dontprotectaccount\"]")).click();
			List<WebElement> eles = driver.findElements(By.cssSelector("input[type=\"submit\"]"));
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
		YaUtil.login(driver, info.id, info.pswd);
		System.out.println("[reactivator][finish]id=" + info.id);
		
	}

	private void reMailAdress(WebDriver driver) {
		// TODO Auto-generated method stub
		boolean isFlag = false;
		List<WebElement> eles = driver.findElements(By.cssSelector("em[class=\"asterisk\"]"));
		for(WebElement we:eles){
			String val = we.getText();
			if(val.indexOf("連絡用メールアドレス") != -1){
				System.out.println("[reactivator][連絡用メールアドレス]");
				isFlag = true;
				break;
			}
		}
		if(!isFlag) return;
		eles = driver.findElements(By.tagName("a"));
		for(WebElement we:eles){
			String val = we.getText();
			if(val.indexOf("あとで") != -1){
				we.click();
				break;
			}
		}
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
