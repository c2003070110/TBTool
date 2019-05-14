package com.walk_nie.ya;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.util.NieUtil;

public class YaUtil {

	private static String loginUrl = "https://login.yahoo.co.jp/config/login?.src=ym";
	private static String logoutUrl = "https://login.yahoo.co.jp/config/login?logout=1&.intl=jp";

	public static void login(WebDriver driver, String userName,String password) {
		driver.get(logoutUrl);
		driver.get(loginUrl);

		if (!driver.findElements(By.id("idBox")).isEmpty()) {
			WebElement el1 = driver.findElements(By.id("idBox")).get(0);
			el1.findElement(By.id("username")).sendKeys(userName);
			driver.findElement(By.id("btnNext")).click();
		}

		NieUtil.mySleepBySecond(2);

		driver.findElement(By.id("passwd")).sendKeys(password);
		driver.findElement(By.id("btnSubmit")).click();

		NieUtil.mySleepBySecond(2);
		
		List<WebElement> eles = driver.findElements(By.cssSelector("div[class=\"yjmthloginarea\"]"));
		for(WebElement ele:eles){
			String txt = ele.getText();
			if(txt.indexOf("ログアウト") != -1){
				return ;
			}
		}
		
		NieUtil.readLineFromSystemIn("Yahoo! login is finished? ANY KEY For already");
	}
}
