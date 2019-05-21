package com.walk_nie.ya;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class YaUtil {

	public static String loginUrl = "https://login.yahoo.co.jp/config/login?.src=ym";
	public static String logoutUrl = "https://login.yahoo.co.jp/config/login?logout=1&.intl=jp";

	public static void login(WebDriver driver, String userName,String password) {
		//driver.get(logoutUrl);
		//driver.get(loginUrl);
		boolean loginf = false;
		try {
			WebElement el1 = driver.findElements(By.id("idBox")).get(0);
			el1.findElement(By.id("username")).sendKeys(userName);
			driver.findElement(By.id("btnNext")).click();
			NieUtil.mySleepBySecond(2);
			loginf = true;
		} catch (Exception e) {
		}
		try {
			driver.findElement(By.id("passwd")).sendKeys(password);
			driver.findElement(By.id("btnSubmit")).click();
			NieUtil.mySleepBySecond(2);
			loginf = true;
		} catch (Exception e) {
		}
		if(!loginf)return ;

		String title = "ログイン";
		if(!driver.getTitle().startsWith(title)) return;
//		List<WebElement> eles = driver.findElements(By.cssSelector("div[id=\"personalbox\"]"));
//		for(WebElement ele:eles){
//			List<WebElement> eles1 = ele.findElements(By.cssSelector("h3[class=\"Personalbox__title\"]"));
//			if(eles1.isEmpty())continue;
//			if(eles1.get(0).getText().indexOf(userName) != -1)return;
//		}
		
		NieUtil.readLineFromSystemIn("Yahoo! login is finished? ANY KEY For already");
	}
	
	public static File getIdListFile(){
		return new File(NieConfig.getConfig("yahoo.work.folder"),"idlist.txt");
	}
}
