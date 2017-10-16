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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class GetYahooIdMain {

	private String regUrl = "https://account.edit.yahoo.co.jp/registration?.src=ym&.done=https%3A%2F%2Fmail.yahoo.co.jp&src=ym&done=https%3A%2F%2Fmail.yahoo.co.jp&fl=100&no_req_email=true";
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

		List<String> lines = new ArrayList<String>();
		WebDriver driver = new FirefoxDriver();
		try {
			int i=0;
			while(true){
				RegObjInfo regInfo = createRegInfo(i);
				i++;
				try {
					reg(driver, regInfo);
					lines.add(regInfo.toString());
					if(mywait()){
						break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					if(mywait()){
						break;
					}
				}
			}
		} finally {
			File out = new File("./out", "yahooId.txt");
			FileUtils.writeLines(out, lines, true);
		}
	}

	public void reg(WebDriver driver, RegObjInfo regInfo) throws IOException {
		driver.get(regUrl);
		System.out.println(regInfo.toString());
		WebElement element = driver.findElement(By.cssSelector("div#yjContentsBody"));
		element.findElement(By.id("yid")).sendKeys(regInfo.id);
		element.findElement(By.id("passwd")).sendKeys(regInfo.pswd);
		element.findElement(By.id("postCode")).sendKeys(regInfo.postCode);

		element.findElement(By.id("birthday")).sendKeys(
				regInfo.birthYear + regInfo.birthMonth + regInfo.birthDay);

		element.findElement(By.id("male")).click();

		element.findElement(By.id("pw_q")).sendKeys(regInfo.secQuestion);
		element.findElement(By.id("pw_a")).sendKeys(regInfo.secAnswer);
		element.findElement(By.id("deliver")).sendKeys("0");
		
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

	public RegObjInfo createRegInfo(int i) {
		RegObjInfo regInfo = new RegObjInfo();
		String id1 = java.util.UUID.randomUUID().toString();
		String id2 = java.util.UUID.randomUUID().toString();
		String p1 = id1.substring(id1.length() - 4);
		String p2 = id2.substring(id2.length() - 3);
		
		String ftn = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		regInfo.id = "y" + i + p1 + ftn;
		regInfo.pswd = "p" + p1 + p2 + "2010";
		//regInfo.mailAddress = mailAddresss.get(i % mailAddresss.size());
		regInfo.secQuestion = secQ.get(i % secQ.size());
		regInfo.secAnswer = secA.get(i % secA.size());
		regInfo.sex = "1"; // 1 man 2
		regInfo.birthYear = birthYears.get(i % birthYears.size());
		regInfo.birthMonth = birthMonthes.get(i % birthMonthes.size());
		regInfo.birthDay = birthDays.get(i % birthDays.size());
		regInfo.postCode = postCodes.get(i % postCodes.size());
		return regInfo;
	}
	protected boolean mywait() throws IOException {
		while (true) {
			System.out.print("ready for continue? ENTER;N for exit ");
			String line = getStdReader().readLine().trim();
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				return false;
			}
			if ("n".equalsIgnoreCase(line)) {
				return true;
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

	public class RegObjInfo {
		public String id;
		public String pswd;
		public String mailAddress;
		public String secQuestion;
		public String secAnswer;
		public String sex;
		public String birthYear;
		public String birthMonth;
		public String birthDay;
		public String postCode;
		
		public String toString() {
			return id + "@yahoo.co.jp\t" + pswd +  "\t" + "\t" + secQuestion + "\t" + secAnswer
					+ "\t" + birthYear + "\t" + birthMonth + "\t" + birthDay;
		}
	}

}
