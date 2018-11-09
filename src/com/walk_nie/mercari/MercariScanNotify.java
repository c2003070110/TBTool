package com.walk_nie.mercari;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;

public class MercariScanNotify {

	protected BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		WebDriver driver = MercariUtil.logon();
		MercariScanNotify main = new MercariScanNotify();
		main.execute(driver);
	}

	public void execute(WebDriver driver) throws IOException {
		List<MercariObject> objList = scan(driver);
		notify(objList);
	}

	private List<MercariObject> scan(WebDriver driver) {
		// TODO 自動生成されたメソッド・スタブ
		List<MercariObject> objList = parseSumPage(driver);
		parseDetail(driver, objList);
		return null;
	}

	private List<MercariObject> parseSumPage(WebDriver driver) {
		// FIXME URL = keyword + saling + sort
		String url = "";
		driver.get(url);
		List<MercariObject> objList = Lists.newArrayList();
		// FIXME box
		List<WebElement> boxs = driver.findElements(By.tagName("a"));
		for(WebElement box:boxs){
			MercariObject obj = new MercariObject();
			// FIXME id
			WebElement we = box.findElement(By.tagName("a"));
			obj.id = we.getText();
			objList.add(obj);
		}
		return objList;
	}

	private void parseDetail(WebDriver driver, List<MercariObject> objList) {
		// TODO 自動生成されたメソッド・スタブ
		for(MercariObject obj:objList){
			String detailUrlPage = "" + obj.id;
			driver.get(detailUrlPage);
			
		}
	}

	private void notify(List<MercariObject> objList) {
		// TODO 自動生成されたメソッド・スタブ
		
	}


}
