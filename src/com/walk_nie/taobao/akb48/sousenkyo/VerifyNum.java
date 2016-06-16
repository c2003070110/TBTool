package com.walk_nie.taobao.akb48.sousenkyo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.util.WebDriverUtil;

public class VerifyNum  {

	String candidateListUrl = "http://akb48-sousenkyo.jp/web/akb2016/vote/search_initial";
    public static BufferedReader stdReader = null;
    private String candidatesFile = "./shipment/candidateList.txt";

	public static void main(String[] args) throws IOException {

		VerifyNum main = new VerifyNum();

		//main.getCandidateList();
		main.vertify();
	}
    protected  void getCandidateList() throws IOException {
    	String rootUrl = "http://akb48-sousenkyo.jp/web/akb2016/vote/";
        List<String> candidateList = Lists.newArrayList();
        WebDriver driver = WebDriverUtil.getWebDriver(candidateListUrl);
        List<WebElement> alphList = driver.findElements(By.tagName("a"));
        List<String> urls = Lists.newArrayList();
        for(WebElement element:alphList){
        	String href = element.getAttribute("href");
        	if(href.endsWith("index"))continue;
        	urls.add(href);
        }
        for(String str:urls){
        	System.out.println("[LEVEL 1]" + str);
        	driver.get(str);
            List<WebElement> nameList = driver.findElements(By.tagName("a"));
            for(WebElement nameEle:nameList){
            	//System.out.println("[LEVEL 2]" + href);
            	String nhref = nameEle.getAttribute("href");
            	if(nhref.endsWith("index"))continue;
            	if(nhref.endsWith("search_initial"))continue;
            	String name = nameEle.getText() + "\t" +nhref;
            	System.out.println("[LEVEL 2]" + name);
            	if(!candidateList.contains(name)){
            		candidateList.add(name);
            	}
            }
        }
        
        File candidatesF = new File(candidatesFile);
        StringBuffer sb = new StringBuffer();
        for (String str : candidateList) {
        	sb.append(str+ "\r\n");
        }
        Files.write(sb.toString(), candidatesF, Charset.forName("UTF-8"));
        
    }
    protected  void vertify() throws IOException {
        while(true){
            System.out.print("please input vote code! splited by space!");
            
            String line = getStdReader().readLine().trim();
            String[] voteCode = line.split(" ");
            if(voteCode.length != 2){
                continue;
            }

            File tempFile = new File(candidatesFile);
            List<String> candidates = Files.readLines(tempFile, Charset.forName("UTF-8"));
            for (String str : candidates) {
            	String[] name = str.split("\t");
                WebDriver driver = WebDriverUtil.getWebDriver(name[1]);
                driver.findElement(By.id("form_serialnum1")).sendKeys(voteCode[0]);
                driver.findElement(By.id("form_serialnum2")).sendKeys(voteCode[1]);
                driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
            }
            break;
        }
    	
    }

    public  BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}
}
