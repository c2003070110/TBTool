package com.walk_nie.taobao.util;

import org.openqa.selenium.WebDriver;

public class WebDriverSingleton  {
	
	private static WebDriver webDriver = null;
	
	private static WebDriver webDriverForFullScreenShot = null;
	
	public static WebDriver getWebDriverForFullScreenShot() {
		if(!testWebDriverForFullScreenShot()){
			webDriverForFullScreenShot = openWebDriverForFullScreenShot();
		}
		return webDriverForFullScreenShot;
	}
	public static WebDriver getWebDriverForFullScreenShot(String url) {
		if(!testWebDriverForFullScreenShot()){
			webDriverForFullScreenShot = openWebDriverForFullScreenShot();
		}
		webDriverForFullScreenShot.get(url);
		return webDriverForFullScreenShot;
	}

	private static boolean testWebDriverForFullScreenShot() {
		if(webDriverForFullScreenShot == null){
			return false;
		}
		try{
			webDriverForFullScreenShot.get("https://www.amazon.co.jp");
		}catch(Exception e){
			try{
				webDriverForFullScreenShot.close();
			}catch(Exception ex){
			}
			return false;
		}
		return true;
	}

	private static WebDriver openWebDriverForFullScreenShot() {
		return WebDriverUtil.getIEWebDriver();
	}
	
	public static WebDriver getWebDriver() {
		if(!testWebDriver()){
			webDriver = openWebDriver();
		}
		return webDriver;
	}
	public static WebDriver getWebDriver(String url) {
		if(!testWebDriver()){
			webDriver = openWebDriver();
		}
		webDriver.get(url);
		return webDriver;
	}
	private static WebDriver openWebDriver() {
		return WebDriverUtil.getFirefoxWebDriver();
	}

	private static boolean testWebDriver() {
		if(webDriver == null){
			return false;
		}
		try{
			webDriver.get("https://www.amazon.co.jp");
		}catch(Exception e){
			try{
				webDriver.close();
			}catch(Exception ex){
			}
			return false;
		}
		return true;
	}
}
