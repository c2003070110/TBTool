package com.walk_nie.dmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class GetExciteIdMain {

	private String regUrl = "https://ssl2.excite.co.jp/idc/new/?si=extop";
    protected  BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new GetExciteIdMain().execute();
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
				driver.get(regUrl);
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
			File out = new File("./out", "exciteId.txt");
			FileUtils.writeLines(out, lines, true);
		}
	}

	private void reg(WebDriver driver, RegObjInfo regInfo) throws IOException {
		WebElement element = driver.findElement(By.cssSelector("div#regist"));
		element.findElement(By.id("id-exid")).sendKeys(regInfo.id);
		element.findElement(By.name("omitlogin")).click();
		element.findElement(By.id("id-password")).sendKeys(regInfo.pswd);
		element.findElement(By.id("id-password-retype")).sendKeys(regInfo.pswd);
		element.findElement(By.id("id-email")).sendKeys(regInfo.mailAddress);
		element.findElement(By.name("hint_quest"))
				.sendKeys(regInfo.secQuestion);
		element.findElement(By.name("hint_answer")).sendKeys(regInfo.secAnswer);
		//element.findElement(By.id("qf_7b15e4")).click();
		element.findElement(By.name("birthdate[Y]"))
				.sendKeys(regInfo.birthYear);
		element.findElement(By.name("birthdate[m]")).sendKeys(
				regInfo.birthMonth);
		element.findElement(By.name("birthdate[d]")).sendKeys(regInfo.birthDay);
		element.findElement(By.id("id-zipcode")).sendKeys(regInfo.postCode);
        mywait();
        //element.findElement(By.id("new-button")).click();
	}

	private RegObjInfo createRegInfo(int i) {
		RegObjInfo regInfo = new RegObjInfo();
		String id1 = java.util.UUID.randomUUID().toString();
		String id2 = java.util.UUID.randomUUID().toString();
		String p1 = id1.substring(id1.length() - 3);
		String p2 = id2.substring(id2.length() - 4);
		regInfo.id = "ex" + p1 + "200" + i + p2;
		regInfo.pswd = "pw" + p1 + p2 + "2010";
		regInfo.mailAddress = mailAddresss.get(i % mailAddresss.size());
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
	private List<String> mailAddresss = new ArrayList<String>();
	{
		mailAddresss.add("manga2014yy@126.com");
		mailAddresss.add("dengyi0110@163.com");
		mailAddresss.add("manga2014yy@163.com");
		mailAddresss.add("manga2014yy@yahoo.co.jp");
		mailAddresss.add("manga2014yy@yahoo.com");
		mailAddresss.add("manga2014yy@gmail.com");
		mailAddresss.add("manga2014yy@qq.com");
		mailAddresss.add("manga2014yy@163.com");
	}
	private List<String> secQ = new ArrayList<String>();
	{
		secQ.add("尊敬する人の名前は？");
		secQ.add("祖母の下の名前は？");
		secQ.add("生まれ育った故郷は？");
		secQ.add("初めて好きになった有名人は？");
		secQ.add("母親の旧姓は？");
		secQ.add("初恋の人の名前は？");
		secQ.add("一番印象に残っている先生の名前は？");
	}
	private List<String> secA = new ArrayList<String>();
	{
		secA.add("黒沢　一郎");
		secA.add("huang");
		secA.add("liangyuan");
		secA.add("黒沢　一郎");
		secA.add("huang");
		secA.add("鈴木　あやま");
		secA.add("黒沢　一郎");
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
		birthMonthes.add("1");
		birthMonthes.add("3");
		birthMonthes.add("5");
		birthMonthes.add("7");
		birthMonthes.add("8");
		birthMonthes.add("10");
		birthMonthes.add("11");
		birthMonthes.add("1");
	}
	private List<String> birthDays = new ArrayList<String>();
	{
		birthDays.add("1");
		birthDays.add("2");
		birthDays.add("3");
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
			return id + "\t" + pswd +  "\t" + mailAddress + "\t" + secQuestion + "\t" + secAnswer
					+ "\t" + birthYear + "\t" + birthMonth + "\t" + birthDay;
		}
	}

}
