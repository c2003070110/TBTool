package com.walk_nie.amzn;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.seleniumhq.jetty9.util.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class AmznAutoRegistGiftCardDeamon {

	private File logFile = null;
	private String mailAddress = null;
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		AmznAutoRegistGiftCardDeamon main = new AmznAutoRegistGiftCardDeamon();
		
		// FOR TEST
//		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
//		main.init(driver);
//		AmznGiftCardObject noticeObj = new AmznGiftCardObject();
//		noticeObj.amt = "100";
//		noticeObj.qtty = 1;
//		main.orderCode(driver, noticeObj);

		// FOR TEST
		//main.init();
		//main.recieveCode();

		// FOR TEST
		//main.init();
		//AmznGiftCardObject noticeObj = main.getLastestNotice();
		//main.finishAmazonNoticeForAddCode(noticeObj.uid, main.mailAddress);
		
		main.execute();
		
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
			if(StringUtil.isBlank(rslt)){
				return null;
			}
			NieUtil.log(logFile, "[INFO][Service:getAmazonNoticeForAddCode][RESULT]" + rslt);
			AmznGiftCardObject lastestNotice  = new  AmznGiftCardObject();
			Json j = new Json();
			Map<String, Object> objMap = null;
			objMap = j.toType(rslt, Map.class);
			lastestNotice.uid = (String) objMap.get("uid");
			if (StringUtil.isBlank(lastestNotice.uid)) {
				NieUtil.log(logFile, "[ERROR][getLastestNotice]uid is NULL!!");
				return null;
			}
			lastestNotice.amt = (String) objMap.get("amt");
			if (StringUtil.isBlank(lastestNotice.amt)) {
				NieUtil.log(logFile, "[ERROR][getLastestNotice]AMT is NULL!!");
				return null;
			}
			lastestNotice.qtty = Integer.parseInt((String)objMap.get("qtty"));
			if (lastestNotice.qtty == 0) {
				NieUtil.log(logFile, "[ERROR][getLastestNotice]QTTY is ZERO!!");
				return null;
			}
			lastestNotice.payway = (String) objMap.get("payway");
			lastestNotice.mailAddress = (String) objMap.get("mailAddress");
			if (StringUtil.isBlank(lastestNotice.mailAddress)) {
				lastestNotice.mailAddress = this.mailAddress;
			}
//			if (StringUtil.isBlank(lastestNotice.payway)) {
//				NieUtil.log(logFile, "[ERROR][getLastestNotice]payway is NULL!!");
//				return null;
//			}
			return lastestNotice;
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:addCode]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
		return null;
	}

	public void recieveCode() {
		//NieUtil.log(logFile, "[INFO][recieveCode]checking the mail to if has new code.");
		List<String> codeCdList = Lists.newArrayList();
		try {

			Properties properties = new Properties();
			properties.put("mail.imap.host", NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.imap.host"));
			properties.put("mail.imap.port", NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.imap.port"));
			properties.put("mail.imap.timeout", 3000);
			properties.put("mail.imap.ssl.enable", true);

			Session emailSession = Session.getDefaultInstance(properties);
			URLName url = new URLName(NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.type"), 
					NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.imap.host"), 
					Integer.parseInt(NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.imap.port")), 
					"amazonGift", 
					NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.account"), 
					NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.password"));
			Store emailStore = emailSession.getStore(url);
			emailStore.connect();
			Folder emailFolder = emailStore.getFolder(url);
			emailFolder.open(Folder.READ_WRITE);

			Message[] messages = emailFolder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				String subj = message.getSubject();
				if (subj.indexOf("Amazonギフト券") == -1) {
					continue;
				}
				boolean isTarget = false;
				Address[] froms = message.getFrom();
				for (Address from : froms) {
					String mailAddress = ((InternetAddress) from).getAddress();
					if (mailAddress.indexOf("amazon.co.jp") != -1) {
						isTarget = true;
						break;
					}
				}
				if (!isTarget)
					continue;
		
				String codeType = "", codeCd = "", orderNo = "";
				MimeMultipart content = (MimeMultipart)message.getContent();
				int cnt = content.getCount();
				for(int j=0;j<cnt;j++){
					MimeBodyPart bodyPart = (MimeBodyPart)content.getBodyPart(j);
					Object o = bodyPart.getContent();
					if(!(o instanceof String)) continue;
					String bodyMsg = (String)o;
					bodyMsg=bodyMsg.trim();
					if(!bodyMsg.startsWith("<!DOCTYPE html")){
						continue;
					}
					Document jsoup = Jsoup.parse(bodyMsg.trim());
					Elements els = jsoup.getElementsByTag("p");
					for(int ii= 0;ii<els.size();ii++){
						Element el =els.get(ii);
						String txt = el.text();
						if(txt.startsWith("ギフト券番号")){
							codeCd = txt.split(":")[1].replace(" ", "");
							break;
						}
					}
					els = jsoup.getElementsByTag("span");
					for(int ii= 0;ii<els.size();ii++){
						Element el =els.get(ii);
						String txt = el.text();
						if (txt.startsWith("￥")) {
							if (txt.equals("￥15")) {
								codeType = "AMZNJPY15";
							} else if (txt.equals("￥20")) {
								codeType = "AMZNJPY20";
							} else if (txt.equals("￥30")) {
								codeType = "AMZNJPY30";
							} else if (txt.equals("￥40")) {
								codeType = "AMZNJPY40";
							} else if (txt.equals("￥50")) {
								codeType = "AMZNJPY50";
							} else if (txt.equals("￥60")) {
								codeType = "AMZNJPY60";
							} else if (txt.equals("￥70")) {
								codeType = "AMZNJPY70";
							} else if (txt.equals("￥80")) {
								codeType = "AMZNJPY80";
							} else if (txt.equals("￥90")) {
								codeType = "AMZNJPY90";
							} else 	if (txt.equals("￥100")) {
									codeType = "AMZNJPY100";
							}
							break;
						}
					}
					els = jsoup.getElementsByTag("span");
					for(int ii= 0;ii<els.size();ii++){
						Element el =els.get(ii);
						String txt = el.text();
						if(txt.startsWith("注文番号")){
							orderNo = txt.split(":")[1].replace(" ", "");
							break;
						}
					}
				}
				if(StringUtil.isBlank(orderNo) || StringUtil.isBlank(codeType) || StringUtil.isBlank(codeCd) ){
					continue;
				}
				codeCdList.add(codeType + ":" + orderNo + ":" + codeCd);
				addCodeByWebService(orderNo, codeType, codeCd);
				message.setFlag(Flags.Flag.FLAGGED, true);
			}

			// 5) close the store and folder objects
			emailFolder.close(true);
			emailStore.close();
		} catch (Exception e) {

			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][recieveCode]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
		if(!codeCdList.isEmpty()){
			for(String str:codeCdList){
				NieUtil.log(logFile, "[INFO][recieveCode]receivede. " + str);
			}
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

			if (!StringUtil.isBlank(rslt)) {
				NieUtil.log(logFile, "[INFO][Service:addCode][RESULT]" + rslt);
			}
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:addCode]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
	}

	public void orderCode(WebDriver driver, AmznGiftCardObject giftObj) {

		NieUtil.log(logFile, "[INFO][orderCode]ordering[AMT]" + giftObj.amt+"[QTTY]"+giftObj.qtty);
		String orderUrl = "https://www.amazon.co.jp/Amazon%E3%82%AE%E3%83%95%E3%83%88%E5%88%B8-1_JP_Email-Amazon%E3%82%AE%E3%83%95%E3%83%88%E5%88%B8-E%E3%83%A1%E3%83%BC%E3%83%AB%E3%82%BF%E3%82%A4%E3%83%97-Amazon%E3%83%99%E3%83%BC%E3%82%B7%E3%83%83%E3%82%AF/dp/B004N3APGO/ref=lp_3131877051_1_1?s=gift-cards&ie=UTF8&qid=1556203876&sr=1-1";
		driver.get(orderUrl);
		logon(driver);
		try {
			// fill
			WebElement el10 = driver.findElement(By
					.cssSelector("div[id=\"gc-order-form-amount-wrapper\"]"));
			WebElement el1 = driver.findElement(By
					.cssSelector("input[id=\"gc-order-form-custom-amount\"]"));
			WebElement el12 = driver
					.findElement(By
							.cssSelector("button[id=\"gc-delivery-mechanism-button-Email-announce\"]"));
			WebElement el2 = driver.findElement(By
					.cssSelector("textarea[id=\"gc-order-form-recipients\"]"));
			WebElement el3 = driver.findElement(By
					.cssSelector("input[id=\"gc-order-form-quantity\"]"));

			el10.click();
			el1.click();
			el1.sendKeys(giftObj.amt);
	
			el12.click();
			el2.click();
			el2.clear();
			el2.sendKeys(giftObj.mailAddress);
			el3.click();
			el3.clear();
			el3.sendKeys(giftObj.qtty + "");

			// payment カートに入れる
//			WebElement el5 = driver.findElement(By
//					.cssSelector("input[id=\"gc-buy-box-atc\"]"));
//			el5.click();
//			try {
//				el5 = driver.findElement(By
//						.cssSelector("input[id=\"gc-buy-box-atc\"]"));
//				el5.click();
//			} catch (Exception e) {
//
//			}

			// order 今すぐ購入
			WebElement el5 = driver.findElement(By
					.cssSelector("input[id=\"gc-buy-box-bn\"]"));
			el5.click();
			try {
				el5 = driver.findElement(By
						.cssSelector("input[id=\"gc-buy-box-bn\"]"));
				el5.click();
			} catch (Exception e) {
			}
			logon(driver);
			WebElement el6 = driver.findElement(By
					.cssSelector("span[id=\"placeYourOrder\"]"));
			el6.click();
		} catch (Exception e) {

			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][orderCode]" + e.getMessage());
			NieUtil.log(logFile, e);
			return;
			// break;
		}

		finishAmazonNoticeForAddCode(giftObj.uid, giftObj.mailAddress);
	}

	private void finishAmazonNoticeForAddCode(String uid,String mailAddress) {
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "finishAmazonNoticeForAddCode");
			param.put("uid", uid);
			param.put("mailAddress", mailAddress);
			NieUtil.log(logFile, "[INFO][Service:finishAmazonNoticeForAddCode][Param]" + "[uid]" + uid + "[mailAddress]" + mailAddress);

			String rslt = NieUtil.httpGet(NieConfig.getConfig("amazon.auto.regist.giftcard.service.url"), param);

			if (!StringUtil.isBlank(rslt)) {
				NieUtil.log(logFile,
						"[INFO][Service:finishAmazonNoticeForAddCode][RESULT]"
								+ rslt);
			}
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:addCode]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
	}

	public void init() throws IOException {

		logFile = new File(NieConfig.getConfig("amazon.auto.regist.giftcard.log.file"));
		mailAddress = NieConfig.getConfig("amazon.auto.regist.giftcard.rec.mail.address");

		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
		
	}

	public void init(WebDriver driver) throws IOException {
		init();
	}

	private void logon(WebDriver driver) {

//		String loginUrl = "https://www.amazon.co.jp/ap/signin?openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.co.jp%2Fgp%2Fyourstore%2Fhome%3Fie%3DUTF8%26ref_%3Dnav_newcust&prevRID=2E61HR8PF7YBXKCHYVWB&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=jpflex&openid.mode=checkid_setup&openid.ns.pape=http%3A%2F%2Fspecs.openid.net%2Fextensions%2Fpape%2F1.0&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&pageId=jpflex&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&ubid=356-6494969-6939236";
//		driver.get(loginUrl);
//		List<WebElement> wes = driver.findElements(By
//				.cssSelector("span[id=\"glow-ingress-line2\"]"));
//		for (WebElement we : wes) {
//			if ("123-0845‌".equals(we.getText())) {
//				return ;
//			}
//		}
		boolean userI = false;
		try {
			driver.findElement(By.cssSelector("input[id=\"ap_email\"]"));
			userI = true;
		} catch (Exception e) {
		}
		boolean pwssI = false;
		try {
			driver.findElement(By.cssSelector("input[id=\"ap_password\"]"));
			pwssI = true;
		} catch (Exception e) {
		}
		if (!userI && !pwssI) {
			return;
		}
		
		if (userI && pwssI) {
			WebElement el1 = driver.findElement(By
					.cssSelector("input[id=\"ap_email\"]"));
			el1.sendKeys(NieConfig.getConfig("amazon.user.name"));
			WebElement el2 = driver.findElement(By
					.cssSelector("input[id=\"ap_password\"]"));
			el2.sendKeys(NieConfig.getConfig("amazon.user.password"));
			WebElement el3 = driver.findElement(By
					.cssSelector("input[id=\"signInSubmit\"]"));
			el3.click();
			NieUtil.mySleepBySecond(2);
		}
		if (pwssI) {
			WebElement el2 = driver.findElement(By
					.cssSelector("input[id=\"ap_password\"]"));
			el2.sendKeys(NieConfig.getConfig("amazon.user.password"));
			WebElement el3 = driver.findElement(By
					.cssSelector("input[id=\"signInSubmit\"]"));
			el3.click();
			NieUtil.mySleepBySecond(2);
		}
		
		WebDriverWait wait1 = new WebDriverWait(driver, 60);
		wait1.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					List<WebElement> wes = driver.findElements(By
							.cssSelector("span[id=\"glow-ingress-line2\"]"));
					for (WebElement we : wes) {
						if ("123-0845‌".equals(we.getText())) {
							return Boolean.TRUE;
						}
					}
				} catch (Exception ex) {
				}
				return Boolean.FALSE;
			}
		});

	}
 
}
