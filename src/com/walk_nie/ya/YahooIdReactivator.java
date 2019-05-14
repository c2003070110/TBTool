package com.walk_nie.ya;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;

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
		}
	}
	private void activate(WebDriver driver, RegObjInfo info) {
		
		YaUtil.login(driver, info.id, info.pswd);

		// TODO activate
		try {
			//driver.findElement(By.id("passwd"));
		} catch (Exception e) {
		}
		
	}

	private List<RegObjInfo> readin() {
		List<RegObjInfo> objList = Lists.newArrayList();
		// TODO which file??
		File file = new File("","");
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
