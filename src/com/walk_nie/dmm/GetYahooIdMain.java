package com.walk_nie.dmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.beust.jcommander.internal.Lists;

public class GetYahooIdMain {

	private String regUrl = "https://account.edit.yahoo.co.jp/registration?.src=ym&.done=https%3A%2F%2Fmail.yahoo.co.jp&src=ym&done=https%3A%2F%2Fmail.yahoo.co.jp&fl=100&no_req_email=true";
	private String addressUrl = "https://account.edit.yahoo.co.jp/registration?.src=ym&.done=https%3A%2F%2Fmail.yahoo.co.jp&src=ym&done=https%3A%2F%2Fmail.yahoo.co.jp&fl=100&no_req_email=true";
    protected  BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new GetYahooIdMain().execute();
	}

	public void execute() throws IOException {
		System.setProperty("webdriver.chrome.driver",
				"C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
		System.setProperty("webdriver.gecko.driver",
				"C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");

		WebDriver driver = new FirefoxDriver();
		int i = 0;
		while (true) {
			RegObjInfo regInfo = createRegInfo(i);
			i++;
			try {
				if(reg(driver, regInfo)){
					fillAddress(driver,regInfo);
					record(regInfo);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				if (mywait("ready for continue? ENTER;N for exit ")) {
					break;
				}
			}
		}
	}
	private void fillAddress(WebDriver driver, RegObjInfo regInfo) {
		// TODO
//		driver.get(addressUrl);
//		WebElement element = driver.findElement(By.cssSelector("div#yjMain"));
//		element.findElement(By.cssSelector("input#LN")).sendKeys(regInfo.name1);
//		element.findElement(By.cssSelector("input#FN")).sendKeys(regInfo.name2);
//		element.findElement(By.cssSelector("input#NK1")).sendKeys(regInfo.name1Kana);
//		element.findElement(By.cssSelector("input#NK2")).sendKeys(regInfo.name2Kana);
//		element.findElement(By.cssSelector("input#tel_a")).sendKeys(regInfo.telNo);
//
//		element.findElement(By.cssSelector("button#saveBtn")).click();
	}

	private void record(GetYahooIdMain.RegObjInfo regInfo) {
		File out = new File("./out", "yahooId.txt");
		while (true)
			try {
				List<String> lines = Lists.newArrayList();
				lines.add(regInfo.toString());
				FileUtils.writeLines(out, lines, true);
				break;
			} catch (Exception ex) {
				ex.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
			}
	}

	public boolean reg(WebDriver driver, RegObjInfo regInfo) throws IOException {
		driver.get(regUrl);
		System.out.println(regInfo.toString());
		WebElement element = driver.findElement(By.cssSelector("div#yjContentsBody"));
		element.findElement(By.id("yid")).sendKeys(regInfo.id);
		element.findElement(By.id("passwd")).sendKeys(regInfo.pswd);
		element.findElement(By.id("postCode")).sendKeys(regInfo.postCode);

		element.findElement(By.id("birthday")).sendKeys(
				regInfo.birthYear + regInfo.birthMonth + regInfo.birthDay);
		try {
			element.findElement(By.id("dispname")).sendKeys(regInfo.dispName);
		} catch (Exception e) {

		}

		element.findElement(By.id("male")).click();

		element.findElement(By.id("pw_q")).sendKeys(regInfo.secQuestion);
		element.findElement(By.id("pw_a")).sendKeys(regInfo.secAnswer);
		element.findElement(By.id("deliver")).sendKeys("0");
		
		if(testRand(driver)){
		//if(mywait("yahoo rand is finished ready for continue? ENTER;N for exit")){
			try{
			driver.findElement(By.id("policy2")).click();
			}catch(Exception e){}
			driver.findElement(By.id("commit")).click();
		
			driver.get("https://mail.yahoo.co.jp/");
			
			List<WebElement> list = driver.findElements(By.tagName("a"));
			for (WebElement we : list) {
				if("start".equals(we.getAttribute("class"))){
					we.click();
					break;
				}
			}
			try {
				Thread.sleep(1000*1);
			} catch (InterruptedException e) {
			}
			// mail box
			WebElement msgWE = driver.findElement(By.id("msg-list"));
			list = msgWE.findElements(By.className("subj"));
			WebElement selectedWe1 = null;
			for (WebElement we : list) {
				String attr = we.getAttribute("title");
				if (attr.indexOf("ようこそYaho") != -1 ) {
					selectedWe1 = we;
					break;
				}
			}
			if(selectedWe1 != null){
				selectedWe1.click();
			}
			return true;
		}
		return false;
		
//		List<WebElement> submitList = element.findElements(By.tagName("input"));
//		WebElement bitCashWe = null;
//		for (WebElement we : submitList) {
//			if ("BitCash".equalsIgnoreCase(we.getAttribute("alt"))) {
//				bitCashWe = we;
//			}
//		}

        //mywait();
        //element.findElement(By.id("new-button")).click();
	}

	private boolean testRand(WebDriver driver) throws IOException {
		int trycnt = 0;
		while (true) {
			if (trycnt > 60)
				break;
			trycnt++;
			// try {
			// WebElement we = driver.findElement(By.id("policy2"));
			// if(we != null){
			// return true;
			// }
			// } catch (Exception ex) {
			//
			// }
			try {
				List<WebElement> wls = driver.findElements(By
						.cssSelector("h2.title"));
				for (WebElement we : wls) {
					if (we.getText().equals("利用規約の同意")) {
						return true;
					}
				}
			} catch (Exception ex) {
			}
			try {
				Thread.sleep(1000 * 1);
			} catch (InterruptedException e) {
			}
		}
		return mywait("yahoo rand is finished ready for continue? ENTER;N for exit");
	}

	public RegObjInfo createRegInfo(int i) {
		RegObjInfo regInfo = new RegObjInfo();
		String id1 = java.util.UUID.randomUUID().toString();
		String id2 = java.util.UUID.randomUUID().toString();
		String p1 = id1.substring(id1.length() - 3);
		String p2 = id2.substring(id2.length() - 2);
		int rInt = RandomUtils.nextInt();
		String ftn = new SimpleDateFormat("yyMMdd").format(Calendar.getInstance().getTime());
		regInfo.id = "y" + i + p1 + ftn.substring(0,6);
		regInfo.pswd = "p" + p1 + p2 + "2010";
		//regInfo.mailAddress = mailAddresss.get(i % mailAddresss.size());
		regInfo.secQuestion = secQ.get(rInt % secQ.size());
		regInfo.secAnswer = secA.get(rInt % secA.size());
		regInfo.sex = "1"; // 1 man 2
		regInfo.birthYear = birthYears.get(rInt % birthYears.size());
		regInfo.birthMonth = birthMonthes.get(rInt % birthMonthes.size());
		regInfo.birthDay = birthDays.get(rInt % birthDays.size());
		regInfo.postCode = postCodes.get(rInt % postCodes.size());
		regInfo.dispName = dispNames.get(rInt % dispNames.size());
		return regInfo;
	}
	protected boolean mywait(String hint) throws IOException {
		while (true) {
			System.out.print(hint);
			String line = getStdReader().readLine().trim();
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				return true;
			}
			if ("n".equalsIgnoreCase(line)) {
				return false;
			}
		}
	}

    public  BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}
	
	private List<String> secQ = new ArrayList<String>();
	{
		secQ.add("父親の出身地は？");
		secQ.add("初めて飛行機で行った場所は？");
		secQ.add("小学校6年生のときの先生は？");
		secQ.add("初めてデートした場所は？");
		secQ.add("初めて勤めた会社の場所は？");
		secQ.add("3歳の時に住んでいた場所は？");
		secQ.add("高校時代の所属クラブは？");
	}
	private List<String> secA = new ArrayList<String>();
	{
		secA.add("東京浅草");
		secA.add("大阪");
		secA.add("黒沢　一郎");
		secA.add("東京上野");
		secA.add("東京新宿");
		secA.add("東京浅草");
		secA.add("野球一部");
	}
	private List<String> birthYears = new ArrayList<String>();
	{
		birthYears.add("1980");
		birthYears.add("1981");
		birthYears.add("1982");
		birthYears.add("1983");
		birthYears.add("1984");
		birthYears.add("1985");
		birthYears.add("1986");
		birthYears.add("1987");
	}
	private List<String> birthMonthes = new ArrayList<String>();
	{
		birthMonthes.add("01");
		birthMonthes.add("03");
		birthMonthes.add("05");
		birthMonthes.add("07");
		birthMonthes.add("08");
		birthMonthes.add("10");
		birthMonthes.add("11");
		birthMonthes.add("01");
	}
	private List<String> birthDays = new ArrayList<String>();
	{
		birthDays.add("01");
		birthDays.add("02");
		birthDays.add("03");
		birthDays.add("11");
		birthDays.add("12");
		birthDays.add("13");
		birthDays.add("21");
		birthDays.add("22");
	}
	private List<String> postCodes = new ArrayList<String>();
	{
		postCodes.add("1360072");
		postCodes.add("0600035");
		postCodes.add("0600053");
		postCodes.add("5560022");
		postCodes.add("5560001");
		postCodes.add("5560017");
		postCodes.add("5160024");
		postCodes.add("5160009");
	}
	private List<String> dispNames = new ArrayList<String>();
	{
		dispNames.add("悠真");
		dispNames.add("悠人");
		dispNames.add("大翔");
		dispNames.add("陽太");
		dispNames.add("咲良");
		dispNames.add("結菜");
		dispNames.add("陽菜");
		dispNames.add("結衣");
		dispNames.add("陽葵");
		dispNames.add("優愛");
		dispNames.add("莉子");
		dispNames.add("芽依");
		dispNames.add("心春");
		dispNames.add("柚希");
	}

	public class RegObjInfo {
		public String id;
		public String pswd;
		public String dispName;
		public String mailAddress;
		public String secQuestion;
		public String secAnswer;
		public String sex;
		public String birthYear;
		public String birthMonth;
		public String birthDay;
		public String postCode;

		public String name1;
		public String name2;
		public String name1Kana;
		public String name2Kana;
		
		public String telNo;
		
		
		public String toString() {
			return id + "@yahoo.co.jp\t" + pswd +  "\t" + "\t" + secQuestion + "\t" + secAnswer
					+ "\t" + birthYear + "\t" + birthMonth + "\t" + birthDay + "\t" + dispName;
		}
	}

}
