package com.walk_nie.webmoney;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Maps;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class WebMoneyDemon {
	private File logFile = null;
	

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		WebMoneyDemon main = new WebMoneyDemon();
		/*
		main.init();
		YaSoldObject yaObj = new YaSoldObject ();
		yaObj.title = "[PSNUSD40]";
		yaObj.qtty = 1;
		String rslt =  main.composeSendMessage(yaObj);
		yaObj.auctionId = "tst001";
		yaObj.obider = "tst001";
		main.save(yaObj);
		System.out.println(rslt);
		*/
		main.execute();
	}

	public void execute() throws IOException {
		init();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		int interval = Integer.parseInt(NieConfig
				.getConfig("webmoney.demon.interval"));// second
		while (true) {
			try {
				long t1 = System.currentTimeMillis();
				WebMoneyObject noticeObj = getLastestNotice();
				if(noticeObj != null){
					check(driver,noticeObj);
				}
				WebMoneyObject checkedObj = getChecked();
				if(checkedObj != null){
					pay(driver,checkedObj);
				}
				
				long t2 = System.currentTimeMillis();
				long dif = t2 - t1;
				if (dif < interval * 1000) {
					log("[SLEEP]zzzZZZzzz...");
					NieUtil.mySleepBySecond((new Long(interval - dif / 1000))
							.intValue());
				}
			} catch (Exception ex) {
				log(ex);
			}
		}
	}

	public void execute(WebDriver driver) {
		WebMoneyObject noticeObj = getLastestNotice();
		if (noticeObj != null) {
			check(driver, noticeObj);
		}

		WebMoneyObject checkedObj = getChecked();
		if (checkedObj != null) {
			pay(driver, checkedObj);
		}
	}

	private WebMoneyObject getLastestNotice() {
		WebMoneyObject lastestNotice = new WebMoneyObject();
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "getLastestNoticeOne");

			String line = NieUtil.httpGet(
					NieConfig.getConfig("webmoney.demon.service.url"),
					param);
			if (StringUtil.isBlank(line)) {
				return null;
			}
			log("[INFO][Service:getLastestNoticeOne][Result]" + line);

			Json j = new Json();
			Map<String, Object> objMap = null;
			objMap = j.toType(line, Map.class);
			lastestNotice.uid = (String) objMap.get("uid");
			lastestNotice.url = (String) objMap.get("url");
			lastestNotice.amtJPY = (String) objMap.get("amtJPY");
			lastestNotice.payway = (String) objMap.get("payway");
			if (StringUtil.isBlank(lastestNotice.url)) {
				log("[ERROR][getLastestNotice]URL is NULL!!");
				return null;
			}
		} catch (Exception e) {
			log("[ERROR][getLastestNotice]" + e.getMessage());
			log(e);
			return null;
		}

		return lastestNotice;
	}

	private void check(WebDriver driver, WebMoneyObject noticeObj) {
		try {
			driver.get(noticeObj.url);
		} catch (Exception e) {
			sendCheckResultAction(noticeObj.uid, "[ERROR]URL", "[ERROR]URL");
			return;
		}
		WebElement weRoot = null, we2 = null, we3 = null;
		try {
			weRoot = driver.findElement(By.id("constract"));
			we2 = weRoot.findElement(By.id("shopInfo"));
			we3 = we2.findElement(By.id("shopComent"));
		} catch (Exception e) {
			String txt = driver.findElement(By.tagName("body")).getText();
			sendCheckResultAction(noticeObj.uid, "[ERROR]" + txt, "[ERROR]" + txt);
			return;
		}
		String shopComment = we3.getText();
		StringBuffer sb = new StringBuffer();
		List<WebElement> wes = weRoot.findElements(By.tagName("tr"));
		for(WebElement we :wes){
			List<WebElement> wetds = we.findElements(By.tagName("td"));
			if(wetds.size() != 2)continue;
			String xlzName = wetds.get(0).getAttribute("class");
			String itemName = "", amtTtl="";
			if(xlzName.indexOf("item_name") != -1){
				itemName = wetds.get(0).getText();
				itemName = itemName.replaceAll(" ", "");
				itemName = itemName.replaceAll(",", "");
				itemName = itemName.replaceAll(":", "");
				itemName = itemName.replaceAll(";", "");
				sb.append(itemName).append(":");
				amtTtl = wetds.get(1).getText();
				amtTtl = amtTtl.replaceAll(" ", "");
				amtTtl = amtTtl.replaceAll(",", "");
				amtTtl = amtTtl.replaceAll(";", "");
				amtTtl = amtTtl.replaceAll(":", "");
				sb.append(amtTtl).append(";");
				continue;
			}
		}
		sendCheckResultAction(noticeObj.uid,shopComment,sb.toString());
	}

	private void sendCheckResultAction(String uid, String shopComment,
			String itemInfo) {
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "updateCheckResult");
			param.put("uid", uid);
			param.put("shopComment", shopComment);
			param.put("itemInfo", itemInfo);// itemName:amtTtl

			String line = NieUtil.httpGet(
					NieConfig.getConfig("webmoney.demon.service.url"), param);
			if (StringUtil.isBlank(line)) {
				return;
			}
			log("[INFO][Service:updateCheckResult][Result]" + line);
		} catch (Exception e) {
			log("[ERROR][check]" + e.getMessage());
			log(e);
			return;
		}
	}

	private WebMoneyObject getChecked() {
		WebMoneyObject checkedNotice = new WebMoneyObject();
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "getCheckedNoticeOne");

			String line = NieUtil.httpGet(
					NieConfig.getConfig("webmoney.demon.service.url"),
					param);
			if (StringUtil.isBlank(line)) {
				return null;
			}
			log("[INFO][Service:getCheckedNoticeOne][Result]" + line);

			Json j = new Json();
			Map<String, Object> objMap = null;
			objMap = j.toType(line, Map.class);
			checkedNotice.uid = (String) objMap.get("uid");
			checkedNotice.url = (String) objMap.get("url");
			checkedNotice.amtJPY = (String) objMap.get("amtJPY");
			checkedNotice.payway = (String) objMap.get("payway");
			if (StringUtil.isBlank(checkedNotice.url)) {
				log("[ERROR][getChecked]URL is NULL!!");
				return null;
			}
			if (StringUtil.isBlank(checkedNotice.amtJPY)) {
				log("[ERROR][getChecked]金額 is NULL!!");
				return null;
			}
			if (StringUtil.isBlank(checkedNotice.payway)) {
				log("[ERROR][getChecked]支付方式 is NULL!!");
				return null;
			}
		} catch (Exception e) {
			log("[ERROR][getChecked]" + e.getMessage());
			log(e);
			return null;
		}

		return checkedNotice;
	}

	private void pay(WebDriver driver, WebMoneyObject checkedObj) {
		try {
			driver.get(checkedObj.url);
		} catch (Exception e) {
			sendPayResultAction(checkedObj.uid, false, "[ERROR]URL");
			return;
		}
		WebElement weRoot = null, pmw = null;
		try {
			weRoot = driver.findElement(By.id("main"));
			pmw = weRoot.findElement(By.id("payment_way_select"));
		} catch (Exception e) {
			String txt = driver.findElement(By.tagName("body")).getText();
			sendPayResultAction(checkedObj.uid, false, "[ERROR]" + txt);
			return;
		}
		if (checkedObj.payway.equals("prepaidNo")
				|| checkedObj.payway.equals("cardcase")) {
			List<WebElement> wea = pmw.findElement(
					By.className("select_prepaid")).findElements(
					By.tagName("a"));
			if (wea != null && wea.size() != 0) {
				wea.get(0).click();
			}
		} else if (checkedObj.payway.equals("wallet")) {
			List<WebElement> wea = pmw.findElement(
					By.className("select_wallet"))
					.findElements(By.tagName("a"));
			if (wea != null && wea.size() != 0) {
				wea.get(0).click();
			}
		}
		boolean payResult = false;
		String payResultStr = "";
		if (checkedObj.payway.equals("prepaidNo")) {
			weRoot = driver.findElement(By.id("main"));
			WebElement wepin = weRoot.findElement(By.id("input_pin_area"));
			wepin.findElement(By.id("pno1")).sendKeys(
					NieConfig.getConfig("webmoney.pay.pin.no.1"));
			wepin.findElement(By.id("pno2")).sendKeys(
					NieConfig.getConfig("webmoney.pay.pin.no.2"));
			wepin.findElement(By.id("pno3")).sendKeys(
					NieConfig.getConfig("webmoney.pay.pin.no.3"));
			wepin.findElement(By.id("pno4")).sendKeys(
					NieConfig.getConfig("webmoney.pay.pin.no.4"));
			
			wepin.findElement(By.id("btn_settle_pin")).click();
			NieUtil.mySleepBySecond(5);
			
			// result..
			weRoot = driver.findElement(By.id("main"));
			List<WebElement>  weError1= weRoot.findElements(By.cssSelector("span[class=\"error\""));
			if(weError1 == null || weError1.isEmpty()){
				payResult = true;
			}else{
				payResult = false;
				payResultStr = weError1.get(0).getText();
			}
			if (payResult) {
				List<WebElement> weError = weRoot.findElements(By
						.id("lessAmountArea"));
				if (weError == null || weError.isEmpty()) {
					payResult = true;
				} else {
					payResult = false;
					payResultStr = weError.get(0).getText();
				}
			}
			// take a screen shot
			screenShot(driver, checkedObj);
		} else if (checkedObj.payway.equals("cardcase")) {
			// TODO how to do?
			weRoot = driver.findElement(By.id("main"));
			WebElement wepin = weRoot.findElement(By.id("appSettleArea"));
			
			wepin.findElement(By.id("app_launchAppBtn")).click();
			NieUtil.mySleepBySecond(5);

			// take a screen shot
			screenShot(driver, checkedObj);
		} else if (checkedObj.payway.equals("wallet")) {
			weRoot = driver.findElement(By.id("main"));
			WebElement wepin = weRoot.findElement(By.id("walletArea"));
			wepin.findElement(By.id("ID")).sendKeys(
					NieConfig.getConfig("webmoney.pay.wallet.id"));
			wepin.findElement(By.id("PSW")).sendKeys(
					NieConfig.getConfig("webmoney.pay.wallet.password"));
			wepin.findElement(By.cssSelector("input[name=\"spw\"]")).sendKeys(
					NieConfig.getConfig("webmoney.pay.wallet.spassword"));
			
			wepin.findElement(By.id("btn_settle_wallet")).click();
			NieUtil.mySleepBySecond(3);

			// result..
			weRoot = driver.findElement(By.id("main"));
			List<WebElement>  weError1= weRoot.findElements(By.cssSelector("span[class=\"error\""));
			if(weError1 == null || weError1.isEmpty()){
				payResult = true;
			}else{
				payResult = false;
				payResultStr = weError1.get(0).getText();
			}
			if (payResult) {
				List<WebElement> weError = weRoot.findElements(By
						.id("lessAmountArea"));
				if (weError == null || weError.isEmpty()) {
					payResult = true;
				} else {
					payResult = false;
					payResultStr = weError.get(0).getText();
				}
			}
			// take a screen shot
			screenShot(driver, checkedObj);
		}
		sendPayResultAction(checkedObj.uid, payResult, payResultStr);
	}

	private void screenShot(WebDriver driver, WebMoneyObject checkedObj) {
		File saveTo = new File(NieConfig.getConfig("webmoney.demon.work.path") + "/orderShot",
				DateUtils.formatDate(new Date(System.currentTimeMillis()), "yyyyMMddHHmmss") + checkedObj.uid + ".jpg");
		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(screenshot, saveTo);
		} catch (IOException e) {
			log(e);
			e.printStackTrace();
		}
	}

	private void sendPayResultAction(String uid, boolean payResult,String payResultStr) {
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "updatePayResult");
			param.put("uid", uid);
			if(!payResult){
				param.put("payResult", payResultStr);
			}else{
				param.put("payResult", String.valueOf(payResult));	
			}
			
			String line = NieUtil.httpGet(
					NieConfig.getConfig("webmoney.demon.service.url"),
					param);
			if (StringUtil.isBlank(line)) {
				return ;
			}
			log("[INFO][Service:requireCheck][Result]" + line);
		} catch (Exception e) {
			log("[ERROR][check]" + e.getMessage());
			log(e);
			return ;
		}
	}

	public void init() throws IOException {

		logFile = new File(
				NieConfig.getConfig("webmoney.demon.log.file"));

		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
	}
 
	private void log(String string) {

		String nowDateTimeStr = getNowDateTime();
		try {
			String str = "[" + nowDateTimeStr + "]" + string + "\n";
			System.out.print(str);
			FileUtils.write(logFile, str, Charset.forName("UTF-8"), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void log(Exception ex) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream(logFile,true));
			ex.printStackTrace(ps);
			ps.flush();
			ps.close();
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getNowDateTime(){
        TimeZone tz2 = TimeZone.getTimeZone("Asia/Tokyo");
        Calendar cal1 = Calendar.getInstance(tz2);
		return DateUtils.formatDate(cal1.getTime(), "yyyy-MM-dd HH:mm:ss");
	}

}
