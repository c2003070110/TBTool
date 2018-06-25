package com.walk_nie.taobao.akb48.sousenkyo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.util.WebDriverUtil;

public class VerifyNum {

	String candidateListUrl = "https://akb48-sousenkyo.jp/akb/search";
	String rootUrl = "http://akb48-sousenkyo.jp/";
	public static BufferedReader stdReader = null;
	private String candidatesFile = "./akb48/candidateList.txt";

	public static void main(String[] args) throws IOException {

		VerifyNum main = new VerifyNum();

		 main.getCandidateList();
		//main.vertify();
	}

	protected void vertify() throws IOException {
		File tempFile = new File(candidatesFile);
		List<String> candidates = Files.readLines(tempFile,
				Charset.forName("UTF-8"));
		
		WebDriver driver = new HtmlUnitDriver();
		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		 
		while (true) {
			System.out.print("please input vote code! splited by space!");

			String line = getStdReader().readLine().trim();
			String[] voteCode = line.split(" ");
			if (voteCode.length != 2) {
				continue;
			}
			/*
			 * String str =
			 * "渡辺 麻友	http://akb48-sousenkyo.jp/vote.php?membercode=1307&parent=initial&parentkey=%82%ED"
			 * ; String[] name = str.split("\t"); if(driver == null){ driver =
			 * WebDriverUtil.getWebDriver(name[1]); }else{ driver.get(name[1]);
			 * }
			 * 
			 * try { driver.findElement(By.name("serial_code_1")).sendKeys(
			 * voteCode[0]);
			 * driver.findElement(By.name("serial_code_2")).sendKeys(
			 * voteCode[1]);
			 * driver.findElement(By.cssSelector("input[type=\"submit\"]"))
			 * .click(); List<WebElement> r =
			 * driver.findElement(By.tagName("body"
			 * )).findElements(By.tagName("div")); if(r.size()>2){ String txt =
			 * r.get(r.size()-2).getText(); System.out.println("[" + name[0] +
			 * "][" + txt + "]"); }
			 * 
			 * mysleep(3); } catch (Exception e) { e.printStackTrace(); }
			 */
			
			for (String str : candidates) {
				mysleep(1);
				String[] name = str.split("\t");
				driver.get(name[1]);
				try {
					driver.findElement(By.name("serial_code_1")).sendKeys(
							voteCode[0].trim());
					driver.findElement(By.name("serial_code_2")).sendKeys(
							voteCode[1].trim());
					//String fileNm = voteCode[0]+"_" + voteCode[1] + "-" +name[0].trim() + "-1.png";
					//WebDriverUtil.screenShot(driver, driver.findElements(By.tagName("body")), "c:/temp/" + fileNm);
					driver.findElement(By.cssSelector("input[type=\"submit\"]"))
							.click();
					mysleep(1);
					//fileNm = voteCode[0]+"_" + voteCode[1] + "-" +name[0].trim() + "-2.png";
					//WebDriverUtil.screenShot(driver, driver.findElements(By.tagName("body")), "c:/temp/" + fileNm);
					
					WebElement r = driver.findElement(By.xpath("html/body/div[2]"));
					String txt = r.getText();
					txt = txt.replace("\n", "").replace("\r", "");
					System.out.println("[" + name[0] + "][" + txt + "]");
					if(txt.indexOf("既に投票されていま") > 0)break;
				} catch (Exception e) {
					e.printStackTrace();
					mysleep(4);
				}
			}
		}
	}

	protected void mysleep(int second) throws IOException {

		long now = System.currentTimeMillis();
		long millis = second * 1000 + now;
		while (true) {
			if (millis < System.currentTimeMillis())
				break;
		}

	}

	protected void getCandidateList() throws IOException {
		List<String> candidateList = Lists.newArrayList();
		WebDriver driver = WebDriverUtil.getWebDriver(candidateListUrl);
		List<WebElement> alphList = driver.findElements(By.tagName("a"));
		List<String> urls = Lists.newArrayList();
		for (WebElement element : alphList) {
			String href = element.getAttribute("href");
			if (href.endsWith("index"))
				continue;
			urls.add(href);
		}
		for (String str : urls) {
			System.out.println("[LEVEL 1]" + str);
			driver.get(str);
			List<WebElement> nameList = driver.findElements(By.tagName("a"));
			for (WebElement nameEle : nameList) {
				// System.out.println("[LEVEL 2]" + href);
				String nhref = nameEle.getAttribute("href");
				if (nhref.endsWith("index"))
					continue;
				if (nhref.endsWith("search_initial"))
					continue;
				String name = nameEle.getText() + "\t" + nhref;
				System.out.println("[LEVEL 2]" + name);
				if (!candidateList.contains(name)) {
					candidateList.add(name);
				}
			}
		}

		File candidatesF = new File(candidatesFile);
		if (!candidatesF.exists()) {
			candidatesF.getParentFile().mkdirs();
		}
		StringBuffer sb = new StringBuffer();
		for (String str : candidateList) {
			sb.append(str + "\r\n");
		}
		Files.write(sb.toString(), candidatesF, Charset.forName("UTF-8"));
	}

	public BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}
}
