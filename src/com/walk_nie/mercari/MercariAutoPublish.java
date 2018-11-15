package com.walk_nie.mercari;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.util.NieUtil;

public class MercariAutoPublish {
	private String inFileName = "./mercari/publish-in.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		WebDriver driver = MercariUtil.logon();
		MercariAutoPublish main = new MercariAutoPublish();
		main.execute(driver);
	}

	public void execute(WebDriver driver) throws IOException, InterruptedException {

		File tempFile0 = new File(inFileName);
		List<String> lines = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		int idx = 0;
		String publishCount = lines.get(idx++);
		String itemName = lines.get(idx++);
		String itemDesp = lines.get(idx++);
		String itemPrice = lines.get(idx++);

		List<String> pictures = Lists.newArrayList();
		
		String pic = lines.get(idx++);
		if(pic!=null && !"".equals(pic)){
			pictures.add(pic);
		}
		pic = lines.get(idx++);
		if(pic!=null && !"".equals(pic)){
			pictures.add(pic);
		}pic = lines.get(idx++);
		if(pic!=null && !"".equals(pic)){
			pictures.add(pic);
		}
		boolean reslt = false;
		String hint ="Execute is OK? ENTER For continue.N for Exit!\n";
		hint+=" [publish Time] "+ publishCount +"\n";
		hint+=" [Item Title] "+ itemName +"\n";
		hint+=" [Item Desp] "+ itemDesp +"\n";
		hint+=" [Item Price] "+ itemPrice +"\n";
		while (true) {
			System.out.print(hint);
			String line;
			try {
				line = MercariUtil.getStdReader().readLine().trim();
				if ("\r\n".equalsIgnoreCase(line)
						|| "\n".equalsIgnoreCase(line) || "".equals(line)) {
					reslt =  true;
					break;
				}else if ("n".equalsIgnoreCase(line)){
					reslt =  false;
					break;
				}
			} catch (IOException e) {
			}
		}
		if(!reslt){
			return;
		}
		/*
		String itemName = "送料無料 iPhone7 用 ケース No312";// 商品名
		// 商品の説明
		String itemDesp = "箱はありません、割安出品しております。OPP袋に入れて発送する予定です。\n"
				+ "ご購入は、コメント不要です。\n"
		        + "即購入は歓迎です。\n";// 商品の説明
		String itemPrice = "666";// 商品の説明
		
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo311-0.jpg");
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo311-1.jpg");
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo311-4.jpg");
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo312-0.jpg");
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo312-1.jpg");
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo312-4.jpg");
		*/
		for (int i = 0; i < Integer.parseInt(publishCount); i++) {
			publish(driver, itemName, itemDesp, itemPrice, pictures);
		}

	}
	private void publish(WebDriver driver,String itemName,String itemDesp,String itemPrice,List<String> pictures) throws InterruptedException{
		driver.get("https://www.mercari.com/jp/sell/");
		WebElement containerWe = driver.findElement(By.id("sell-container"));
		
		List<WebElement> weList = containerWe.findElements(By.tagName("input"));
		WebElement selWe = null;
		for (WebElement we : weList) {
			if ("image1".equals(we.getAttribute("name"))) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [PICTURE] Element!");
			MercariUtil.mywait("[ERROR]Picture Upload ERROR!upload manually !ENTER when upload finish!");
		}else{
			selWe.sendKeys(pictures.get(0));
			NieUtil.mySleepBySecond(5);
			selWe.sendKeys(pictures.get(1));
			NieUtil.mySleepBySecond(5);
			selWe.sendKeys(pictures.get(2));
		}
		
//		List<WebElement> weList1 = containerWe.findElements(By.tagName("pre"));
//		WebElement selWe1 = null;
//		for (WebElement we : weList1) {
//			String txt = we.getText();
//			txt = txt.replaceAll("\n", "");
//			if ("ドラッグアンドドロップまたはクリックしてファイルをアップロード".equals(txt)) {
//				selWe1 = we;
//			}
//		}
//		if(selWe1 == null){
//			System.err.println("[ERROR]NOT FOUND [PICTURE] Element!");
//			MercariUtil.mywait("[ERROR]Picture Upload ERROR!upload manually !ENTER when upload finish!");
//		}
//		selWe1.click();
//		driver.switchTo().window("File Upload");
//		WebElement el=driver.findElement(By.name("fileName"));
//		el.sendKeys("C:\\temp\\test\\IMG_6519.PNG");
		
		
		// 商品名
		weList = containerWe.findElements(By.tagName("input"));
		selWe = null;
		for (WebElement we : weList) {
			if ("商品名（必須 40文字まで)".equalsIgnoreCase(we.getAttribute("placeholder"))) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [商品名] Element!");
			return;
		}
		selWe.sendKeys(itemName);
		// 商品の説明
		selWe = null;
		weList = containerWe.findElements(By.tagName("textarea"));
		for (WebElement we : weList) {
			if ("商品の説明（必須 1,000文字以内）（色、素材、重さ、定価、注意点など）例）2010年頃に1万円で購入したジャケットです。ライトグレーで傷はありません。あわせやすいのでおすすめです。".equalsIgnoreCase(we.getAttribute("placeholder"))) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [商品の説明] Element!");
			return;
		}
		selWe.sendKeys(itemDesp);
		
		// 商品の詳細
		// カテゴリー
		selWe = null;
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("レディース".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [カテゴリー1] Element!");
			return;
		}
		Select select = new Select(selWe);
		select.selectByValue("7");
		
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("スマートフォン/携帯電話".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [カテゴリー2] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("862");
		
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("Android用ケース".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [カテゴリー3] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("1208");
		
		//商品の状態
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("新品、未使用".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [商品の状態] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("1");
		
		//配送料の負担
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("送料込み(出品者負担)".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [配送料の負担] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("2");
		
		//配送の方法
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("未定".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [配送の方法] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("9");

		//発送元の地域
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("北海道".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [発送元の地域] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("13");
		
		//発送までの日数
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("1~2日で発送".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [発送までの日数] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("2");
		
		//    価格
		weList = containerWe.findElements(By.tagName("input"));
		selWe = null;
		for (WebElement we : weList) {
			if ("例）300".equalsIgnoreCase(we.getAttribute("placeholder"))) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [価格] Element!");
			return;
		}
		selWe.sendKeys(itemPrice);

		//出品する
		weList = containerWe.findElements(By.tagName("button"));
		selWe = null;
		for (WebElement we : weList) {
			if ("出品する".equalsIgnoreCase(we.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [出品Button] Element!");
			return;
		}
		selWe.click();
	}
}
