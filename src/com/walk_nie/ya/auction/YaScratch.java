package com.walk_nie.ya.auction;

import java.io.BufferedReader;
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
import com.walk_nie.ya.RegObjInfo;
import com.walk_nie.ya.YaUtil;

public class YaScratch {

	protected BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		YaScratch main = new YaScratch();
		main.execute();
	}

	public void execute() throws IOException {		
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		String url = "https://auctions.yahoo.co.jp/topic/promo/scratch/?cpid=pr_scratch&menu=auc&tar=top&cr=toppage&crnum=top";
		List<RegObjInfo> list = readin();
		for(RegObjInfo info:list){
			System.out.println("[scratch][start]id=" + info.id);
			driver.get(YaUtil.logoutUrl);
			//NieUtil.mySleepBySecond(5);
			driver.get(YaUtil.loginUrl);
			YaUtil.login(driver, info.id, info.pswd);
			driver.get(url);
			scratch(driver,info);
			driver.get(YaUtil.logoutUrl);
			NieUtil.mySleepBySecond(5);
			System.out.println("[scratch][end]id=" + info.id);
		}
	}

	private void scratch(WebDriver driver, RegObjInfo info) {

		List<WebElement> eles = driver.findElements(By.tagName("a"));
		for(WebElement we:eles){
			String val = we.getText();
			if(val.indexOf("スクラッチをけずる") != -1){
				we.click();
				NieUtil.mySleepBySecond(2);
				break;
			}
		}
		List<WebElement> eles1 = driver.findElements(By.tagName("div"));
		for(WebElement we:eles1){
			String val = we.getAttribute("class");
			String txt = we.getText();
			if(val.indexOf("scratchHead__resultprice") != -1){
				System.out.println("[scratch][result]\t" + info.id + "\t" + txt);
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
