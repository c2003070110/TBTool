package com.walk_nie.amzn;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Maps;
import com.sun.mail.pop3.POP3Store;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class AmznAutoRegistGiftCardDeamon {

	private File logFile = null;
//	private String amt = null;
//	private int qtty = 0;
	private String mailAddress = null;
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		AmznAutoRegistGiftCardDeamon main = new AmznAutoRegistGiftCardDeamon();
		main.execute();
		//main.init();
		//AmznGiftCardObject noticeObj = main.getLastestNotice();
		//main.finishAmazonNoticeForAddCode(noticeObj.uid, main.mailAddress);
	}
	
	public void execute() throws IOException {
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		init(driver);
		int interval = Integer.parseInt(NieConfig.getConfig("amazon.auto.regist.giftcard.interval"));// second
		while (true) {
			try {
				long t1 = System.currentTimeMillis();
				execute(driver);
				long t2 = System.currentTimeMillis();
				long dif = t2 - t1;
				if (dif < interval * 1000) {
					NieUtil.log(logFile, "[SLEEP]zzzZZZzzz...");
					NieUtil.mySleepBySecond((new Long(interval - dif / 1000)).intValue());
				}
			} catch (Exception ex) {
				NieUtil.log(logFile, ex);
			}
		}
	}
	public void execute(WebDriver driver) throws IOException, MessagingException {

		AmznGiftCardObject noticeObj = getLastestNotice();
		if (noticeObj != null) {
			orderCode(driver, noticeObj);
		}
		recieveCode();
	}
	
	private AmznGiftCardObject getLastestNotice() {
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "getAmazonNoticeForAddCode");
			
			String rslt = NieUtil.httpGet(NieConfig.getConfig("amazon.auto.regist.giftcard.service.url"), param);
			NieUtil.log(logFile, "[INFO][Service:getAmazonNoticeForAddCode][RESULT]" + rslt);
			AmznGiftCardObject lastestNotice  = new  AmznGiftCardObject();
			Json j = new Json();
			Map<String, Object> objMap = null;
			objMap = j.toType(rslt, Map.class);
			lastestNotice.uid = (String) objMap.get("uid");
			lastestNotice.amt = (String) objMap.get("amt");
			lastestNotice.qtty = Integer.parseInt((String)objMap.get("qtty"));
			lastestNotice.payway = (String) objMap.get("payway");
			if (StringUtil.isBlank(lastestNotice.amt)) {
				NieUtil.log(logFile, "[ERROR][getLastestNotice]AMT is NULL!!");
				return null;
			}
			if (lastestNotice.qtty == 0) {
				NieUtil.log(logFile, "[ERROR][getLastestNotice]QTTY is ZERO!!");
				return null;
			}
			if (StringUtil.isBlank(lastestNotice.payway)) {
				NieUtil.log(logFile, "[ERROR][getLastestNotice]payway is NULL!!");
				return null;
			}
			return lastestNotice;
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:addCode]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
		return null;
	}

	public void recieveCode() {
		// TODO
		try {

			Properties properties = new Properties();
			properties.put("mail.pop3.host", NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.host"));
			Session emailSession = Session.getDefaultInstance(properties);
			POP3Store emailStore = (POP3Store) emailSession.getStore("pop3");
			emailStore.connect(mailAddress, NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.password"));
			Folder emailFolder = emailStore.getFolder("INBOX");
			emailFolder.open(Folder.READ_WRITE);// READ_WRITE ?

			Message[] messages = emailFolder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				// System.out.println("---------------------------------");
				// System.out.println("Email Number " + (i + 1));
				// System.out.println("Subject: " + message.getSubject());
				String subj = message.getSubject();
				if (subj.indexOf("XXXX") == -1) {
					continue;
				}
				boolean isTarget = false;
				Address[] froms = message.getFrom();
				for (Address from : froms) {
					String mailAddress = ((InternetAddress) from).getAddress();
					if (mailAddress.indexOf("XXXX") != -1) {
						isTarget = true;
						break;
					}
				}
				if (!isTarget)
					continue;
				String content = message.getContent().toString();
				System.out.println("Text: " + content);
				// TODO analyze the content!
				String codeType = "", codeCd = "", orderNo = "";

				addCodeByWebService(orderNo, codeType, codeCd);
			}

			// 5) close the store and folder objects
			emailFolder.close(false);
			emailStore.close();
		} catch (Exception e) {

			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][recieveCode]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
	}
	private void addCodeByWebService(String orderNo, String codeType, String codeCd) {
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "addCode");
			param.put("orderNo", orderNo);
			param.put("codeType", codeType);
			param.put("codeCd", codeCd);
			NieUtil.log(logFile, "[INFO][Service:addCode][Param]" + "[orderNo]" + orderNo + "[codeType]" + orderNo + "[codeCd]" + codeCd);
			
			String rslt = NieUtil.httpGet(NieConfig.getConfig("amazon.auto.regist.giftcard.service.url"), param);
			
			NieUtil.log(logFile, "[INFO][Service:addCode][RESULT]" + rslt);
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:addCode]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
	}

	public void orderCode(WebDriver driver ,AmznGiftCardObject giftObj) {
	
		String orderUrl = "XXXX";
		for (int i = 0; i < giftObj.qtty; i++) {
			driver.get(orderUrl);
			try {
				// fill
				WebElement el1 = driver.findElement(By.cssSelector("input[id=\"XXX\"]"));
				el1.sendKeys(giftObj.amt);
				WebElement el2 = driver.findElement(By.cssSelector("input[id=\"XXX\"]"));
				el2.sendKeys(mailAddress);
				WebElement el3 = driver.findElement(By.cssSelector("input[id=\"XXX\"]"));
				el3.click();

				// payment
				WebElement el4 = driver.findElement(By.cssSelector("input[id=\"XXX\"]"));
				el4.click();

				// order
				WebElement el5 = driver.findElement(By.cssSelector("input[id=\"XXX\"]"));
				el5.click();
			} catch (Exception e) {

				e.printStackTrace();
				NieUtil.log(logFile, "[ERROR][orderCode]" + e.getMessage());
				NieUtil.log(logFile, e);
				break;
			}
		}

		finishAmazonNoticeForAddCode(giftObj.uid, mailAddress);
	}

	private void finishAmazonNoticeForAddCode(String uid,String mailAddress) {
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "finishAmazonNoticeForAddCode");
			param.put("uid", uid);
			param.put("mailAddress", mailAddress);
			NieUtil.log(logFile, "[INFO][Service:finishAmazonNoticeForAddCode][Param]" + "[uid]" + uid + "[mailAddress]" + mailAddress);

			String rslt = NieUtil.httpGet(NieConfig.getConfig("amazon.auto.regist.giftcard.service.url"), param);

			NieUtil.log(logFile, "[INFO][Service:finishAmazonNoticeForAddCode][RESULT]" + rslt);
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:addCode]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
	}

	public void init() throws IOException {

		logFile = new File(NieConfig.getConfig("amazon.auto.regist.giftcard.log.file"));
//		amt = NieConfig.getConfig("amazon.auto.regist.giftcard.amt");
//		qtty = Integer.parseInt(NieConfig.getConfig("amazon.auto.regist.giftcard.qtty"));
		mailAddress = NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.address");

		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
		
	}

	public void init(WebDriver driver) throws IOException {
		init();
		logon(driver);
	}
	private void logon(WebDriver driver) {

		// TODO
		String rootUrl = "XXXXX";

		driver.manage().window().setSize(new Dimension(960, 960));
		driver.manage().window().setPosition(new Point(10, 10));
		driver.get(rootUrl);
		
		WebElement el1 = driver.findElement(By.cssSelector("input[id=\"ap_email\"]"));
		el1.sendKeys(NieConfig.getConfig("amazon.user.name"));
		WebElement el2 = driver.findElement(By.cssSelector("input[id=\"ap_password\"]"));
		el2.sendKeys(NieConfig.getConfig("amazon.user.password"));

		WebElement el3 = driver.findElement(By.cssSelector("input[id=\"signInSubmit\"]"));
		el3.click();

		NieUtil.mySleepBySecond(2);
		
		List<WebElement> eles = driver.findElements(By.cssSelector("div[class=\"FIXME\"]"));
		for(WebElement ele:eles){
			String txt = ele.getText();
			if(txt.indexOf("FIXME") != -1){
				return;
			}
		}
		
		NieUtil.readLineFromSystemIn("Amazon login is finished? ANY KEY For already");
	}
 
}
