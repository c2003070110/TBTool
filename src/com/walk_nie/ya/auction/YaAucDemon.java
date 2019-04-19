package com.walk_nie.ya.auction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class YaAucDemon {
	private String myaucinfoUrl = "https://auctions.yahoo.co.jp/jp/show/myaucinfo";
	private String republishUrlFmt = "https://auctions.yahoo.co.jp/sell/jp/show/resubmit?aID=%s";
	String bidUrlFmt = "https://contact.auctions.yahoo.co.jp/seller/top?aid=%s&bid=%s";
 
	private Map<String, String> keyMsgPrefix = Maps.newHashMap();
	private File logFile = null;
	

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		YaAucDemon main = new YaAucDemon();
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
		WebDriver driver = logon();
		int interval = Integer.parseInt(NieConfig
				.getConfig("yahoo.auction.autosend.interval"));// second
		while (true) {
			try {
				long t1 = System.currentTimeMillis();
				
				forSeller(driver,interval, t1);
				
				long t2 = System.currentTimeMillis();
				long dif = t2 - t1;
				if (dif > interval * 1000) {
					continue;
				}
				
				forBuyer(driver,interval,t2);
				
				t2 = System.currentTimeMillis();
				dif = t2 - t1;
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

	private void forBuyer(WebDriver driver, int interval, long t2) {
		// TODO 
		// listItemByEmptyBidUidOne
		// insertBidObject
		
		// listBidByEmptyObiderAdrOne
		// updateBidByObiderAdr
		
		// listItemByEmptyPriceOne
		// updateItemPriceByBidId
		
		// updateBidByObiderMsg
		// updateItemStatusDepaiByBidId
		// updateItemStatusBdfhByBidId
	}

	private void forSeller(WebDriver driver, int interval, long t1) throws IOException, URISyntaxException {

		List<YaNoticeObject> lastestNoticeList = getLastestNotice(driver);
		if(lastestNoticeList == null || lastestNoticeList.isEmpty()){
			return;
		}
		
		//addBided(driver, lastestNoticeList);
		
		if (hasPaid(driver, lastestNoticeList)) {
			send(driver, lastestNoticeList);
		} else {
			//log("[RSLT]There is NONE to sent.");
		}
		long t2 = System.currentTimeMillis();
		long dif = t2 - t1;
		if (dif > interval * 1000) {
			return;
		}
		republish(driver, lastestNoticeList);
		t2 = System.currentTimeMillis();
		dif = t2 - t1;
		if (dif > interval * 1000) {
			return;
		}

		review(driver, lastestNoticeList);
		t2 = System.currentTimeMillis();
		dif = t2 - t1;
		if (dif > interval * 1000) {
			return;
		}
		finishCode(driver, lastestNoticeList);
		t2 = System.currentTimeMillis();
		dif = t2 - t1;
		if (dif > interval * 1000) {
			return;
		}

		addBidMsg(driver,lastestNoticeList);
		t2 = System.currentTimeMillis();
		dif = t2 - t1;
		if (dif > interval * 1000) {
			return;
		}
		
		publishBidMsg(driver);
	}

	private void publishBidMsg(WebDriver driver) {

		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "getAplyBidMsgOne");
			String rslt = NieUtil.httpGet(NieConfig.getConfig("yahoo.auction.autosend.service.url"), param);
			if (StringUtil.isBlank(rslt)) {
				return;
			}
			
			log("[INFO][Service:getAplyBidMsgOne][Result]" + rslt);
			if (rslt.indexOf("ERROR") != -1) {
				return;
			}
			
			Json j = new Json();
			Map<String, Object> objMap = null;
			try {
				objMap = j.toType(rslt, Map.class);

				String auctionId = (String) objMap.get("bidId");
				String obider = (String) objMap.get("obidId");
				String msg = (String) objMap.get("msg");
				log("[publishBidMsg][auctionId]" + auctionId + "[obider]" + obider + "[msg]" + msg);
				if (StringUtil.isBlank(msg)) {
					return;
				}
				driver.get(String.format(bidUrlFmt, auctionId, obider));

				WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"yjContentsBody\"]"));

				WebElement msgFormWe = rootWe.findElement(By.cssSelector("div[id=\"msgForm\"]"));
				msgFormWe.findElement(By.cssSelector("textarea[id=\"textarea\"]")).sendKeys(msg);

				WebElement summitBtn = msgFormWe.findElement(By.cssSelector("input[id=\"submitButton\"]"));
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", summitBtn);

				param = Maps.newHashMap();
				param.put("action", "updateBidMsgStatus");
				param.put("bidId", auctionId);
				param.put("obidId", obider);
				param.put("status", "sent");
				String rslt1 = NieUtil.httpGet(NieConfig.getConfig("yahoo.auction.autosend.service.url"), param);
				if (StringUtil.isNotBlank(rslt1)) {
					log("[INFO][Service:updateBidMsgStatus][Result]" + rslt1);
				}

			} catch (Exception ex) {
				log("[ERROR][addBidMsg]" + ex.getMessage());
				log(ex);
				ex.printStackTrace();
				return;
			}
		} catch (Exception e) {
			log("[ERROR][addBidMsg]" + e.getMessage());
			log(e);
			e.printStackTrace();
			return;
		}
	}

	private void addBidMsg(WebDriver driver, List<YaNoticeObject> lastestNoticeList) {

		for (YaNoticeObject obj : lastestNoticeList) {
			String title = obj.title;

			if (!title.startsWith("取引メッセージ:")) {
				continue;
			}
			if (!isAutoSendTarget(title)) {
				continue;
			}
			driver.get(obj.href);
			
			WebElement rootWe = driver.findElement(By
					.cssSelector("div[id=\"yjContentsBody\"]"));

			YaSoldObject yaObj = parseSold(rootWe);
			
			StringBuffer msg = new StringBuffer();
			WebElement messagelistWe = null;
			try {
				messagelistWe = driver.findElement(By.id("messagelist"));
			} catch (Exception e) {

				continue;
			}
			if (messagelistWe == null) {
				continue;
			}
			List<WebElement> ddWes = messagelistWe.findElements(By
					.tagName("dd"));
			for (WebElement ddWe : ddWes) {
				msg.append(ddWe.getText()).append("\n");
			}
			addBidMsgToWebService(yaObj.auctionId,yaObj.obider,msg.toString());
		}
	}
	private void addBidMsgToWebService(String auctionId,String obidId,String msg) {

		Map<String, String> param = Maps.newHashMap();
		param.put("action", "addBidMsg");
		param.put("bidId", auctionId);
		param.put("obidId", obidId);
		param.put("msg", msg);
		
		try {
			String rslt = NieUtil.httpGet(NieConfig.getConfig("yahoo.auction.autosend.service.url"), param);
			if (StringUtil.isNotBlank(rslt)) {
				log("[INFO][Service:addBidMsg][Result]" + rslt);
			}
		} catch (Exception e) {
			log("[ERROR][addBidMsg]" + e.getMessage());
			log(e);
			e.printStackTrace();
		}
	}

	protected void addBided(WebDriver driver,List<YaNoticeObject> lastestNoticeList) {
		
		for (YaNoticeObject obj : lastestNoticeList) {
			String title = obj.title;

			if (!title.startsWith("初回入札:")) {
				continue;
			}
			if (!isAutoSendTarget(title)) {
				continue;
			}
			String urlStr = obj.href;
			if (obj.href.indexOf("?") != -1) {
				urlStr = obj.href.substring(0, obj.href.indexOf("?"));
			}
			driver.get(urlStr);

			String obiderId = "";// TODO how to get??
			List<WebElement> wes = driver.findElements(By.tagName("td"));
			String finder = "XXXX";
			for(WebElement we:wes){
				String txt =we.getText();
				if(txt.startsWith(finder)){
					String[] sparr = txt.split(" ");
					for(String sp:sparr){
						if(sp.startsWith(finder)){
							obiderId = 	sp.replace(finder, "");
							obiderId = 	obiderId.trim();
							break;
						}
					}
					break;
				}
			}
			String auctionId = urlStr.substring(urlStr.lastIndexOf("/") + 1);
			if (addBidedToWebService(auctionId, obiderId, obj.identifierToken)) {
				driver.get(obj.href);
			}
		}
	}
	private boolean addBidedToWebService(String auctionId,String obidId,String codeType) {

		Map<String, String> param = Maps.newHashMap();
		param.put("action", "bided");
		param.put("bidId", auctionId);
		param.put("obidId", obidId);
		param.put("codeType", codeType);
		
		try {
			String rslt = NieUtil.httpGet(NieConfig.getConfig("yahoo.auction.autosend.service.url"), param);
			if (StringUtil.isNotBlank(rslt)) {
				log("[INFO][Service:bided][Result]" + rslt);
				return false;
			}
			return true;
		} catch (Exception e) {
			log("[ERROR][addBided]" + e.getMessage());
			log(e);
			e.printStackTrace();
			return false;
		}
	}

	private List<YaNoticeObject> getLastestNotice(WebDriver driver) {
		driver.get(myaucinfoUrl);
		WebElement weTbl = driver.findElement(
				By.cssSelector("div[id=\"modItemNewList\"]")).findElement(
				By.tagName("table"));
		List<YaNoticeObject> list = Lists.newArrayList();
		List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
		for (WebElement trWe : trWeList) {
			List<WebElement> tdWeList = trWe.findElements(By.tagName("td"));
			if (tdWeList.size() != 3) {
				continue;
			}
			try {
				YaNoticeObject obj = new YaNoticeObject();
				WebElement tdWeA = tdWeList.get(1).findElement(By.tagName("a"));
				String title = tdWeA.getText();
				obj.title = title;
				String key = getIdentifierKeyFromTitle(title);
				obj.identifierToken = key;
				
				obj.href = tdWeA.getAttribute("href");

				
				list.add(obj);
			} catch (Exception e) {

			}
		}
		
		return list;
	}

	private void init() throws IOException {

		List<String> list1 = NieConfig
				.getConfigByPrefix("yahoo.auction.autosend.key");
		for (String str : list1) {
			String[] sp = str.split(":");
			keyMsgPrefix.put(sp[0], sp[1]);
		}

		logFile = new File(
				NieConfig.getConfig("yahoo.auction.autosend.log.file"));

		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
	}

	private boolean hasPaid(WebDriver driver, List<YaNoticeObject> lastestNoticeList) {

		//log("[hasPaid][START]Is There any auction paid?");
		for (YaNoticeObject obj : lastestNoticeList) {
			String title = obj.title;

			if (!title.startsWith("支払い完了:")) {
				continue;
			}
			if (!isAutoSendTarget(title)) {
				continue;
			}

			//log("[hasPaid][END]There are some auction which had paid!");
			return true;
		}
		//log("[hasPaid][END]There are NONE auction which had paid!");
		return false;
	}

	private void send(WebDriver driver, List<YaNoticeObject> lastestNoticeList) throws IOException, URISyntaxException {

		List<String> autoSendHrefList = Lists.newArrayList();
		//log("[SEND]START");
		for (YaNoticeObject obj : lastestNoticeList) {
			String href = obj.href;

			String title = obj.title;
			if (!title.startsWith("支払い完了:")) {
				continue;
			}
			if (!isAutoSendTarget(title)) {
				continue;
			}
			
			autoSendHrefList.add(href);
		}
		for (String href : autoSendHrefList) {
			YaSoldObject yaObj = parseYaObjectFromUrl(href);
			log("[SEND][SETING][auctionId]" + yaObj.auctionId + "[obider]" + yaObj.obider);
			driver.get(String.format(bidUrlFmt, yaObj.auctionId, yaObj.obider));

			WebElement rootWe = driver.findElement(By
					.cssSelector("div[id=\"yjContentsBody\"]"));

			//YaSoldObject yaObj = parseSold(rootWe);

			WebElement sendBtnWe = null;
			List<WebElement> weList = rootWe.findElements(By
					.cssSelector("input[type=\"button\"]"));
			for (WebElement we : weList) {
				if ("発送連絡をする".equals(we.getAttribute("value"))) {
					sendBtnWe = we;
					break;
				}
			}
			if (sendBtnWe == null) {
				log("[ERROR][This Auction has sent!][auctionId]" + yaObj.auctionId +"[obider]" + yaObj.obider);
				driver.get(href);
				continue;
			}

			List<YaSendCodeObject> codeList = getUnusedCode(yaObj);
			if (codeList == null || codeList.isEmpty()) {
				continue;
			}
			String message = composeSendMessage(codeList);
			yaObj.statusMsg = message;
			if (StringUtil.isBlank(message)) {
				continue;
			}
			
			// 発送
			doSend(driver, rootWe, sendBtnWe,message, yaObj);

			// 発送済みコードを保存
			save(yaObj, codeList);
			
			driver.get(href);
		}
		//log("[SEND]END");
	}

	private YaSoldObject parseYaObjectFromUrl(String href) {
		YaSoldObject yaObj = new YaSoldObject();
		List<NameValuePair> params;
		try {
			params = URLEncodedUtils.parse(new URI(href), Charset.forName("UTF-8"));
			for (NameValuePair param : params) {
				if ("aid".equalsIgnoreCase(param.getName())) {
					yaObj.auctionId = param.getValue();
				}
				if ("bid".equalsIgnoreCase(param.getName())) {
					yaObj.obider = param.getValue();
				}
			}
			return yaObj;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			log(e);
		}
		return null;
	}

	private void doSend(WebDriver driver, WebElement rootWe, WebElement sendBtnWe, String message, YaSoldObject yaObj) {
		log("[Sending][auctionId]" + yaObj.auctionId +"[obider]" + yaObj.obider);

		WebElement msgFormWe = rootWe.findElement(By
				.cssSelector("div[id=\"msgForm\"]"));
		msgFormWe.findElement(By.cssSelector("textarea[id=\"textarea\"]"))
				.sendKeys(message);
		// 送信
		WebElement summitBtn = msgFormWe.findElement(By.cssSelector("input[id=\"submitButton\"]"));
//		summitBtn.click();
		
//		Actions actions = new Actions(driver);
//		actions.moveToElement(summitBtn).click().build().perform();
		
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", summitBtn);
		
		NieUtil.mySleepBySecond(1);

		// 発送連絡をする
		sendBtnWe.click();

		NieUtil.mySleepBySecond(1);

		// 確認する
		rootWe = driver.findElement(By.cssSelector("div[id=\"yjContentsBody\"]"));
		List<WebElement> weList = rootWe.findElements(By.cssSelector("input[type=\"submit\"]"));
		for (WebElement we : weList) {
			if ("確認する".equals(we.getAttribute("value"))) {
				we.click();
				break;
			}
		}

		NieUtil.mySleepBySecond(1);

		// 決定する
		rootWe = driver.findElement(By.cssSelector("div[id=\"yjContentsBody\"]"));
		weList = rootWe.findElements(By.cssSelector("input[type=\"submit\"]"));
		for (WebElement we : weList) {
			if ("決定する".equals(we.getAttribute("value"))) {
				we.click();
				break;
			}
		}
		String msgFmt = "[SUCC]sent sucessfully!![auctionId]%s[obider]%s[msg]%s";
		String msg = String.format(msgFmt, yaObj.auctionId, yaObj.obider, message);
		log(msg);
		NieUtil.mySleepBySecond(1);
	}

	private List<YaSendCodeObject> getUnusedCode(YaSoldObject yaObj) {
		List<YaSendCodeObject> codeList = Lists.newArrayList();
		String errMsgFmt = "[ERROR]NONE unused code!!![auctionId]%s[obider]%s[key]%s";
		String key = getIdentifierKeyFromTitle(yaObj.title);
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "get");
			param.put("codeType", key);
			param.put("bidId", yaObj.auctionId);
			param.put("obidId", yaObj.obider);
			// return codeType:codeCd;codeType:codeCd;
			String code = NieUtil.httpGet(NieConfig.getConfig("yahoo.auction.autosend.service.url"), param);

			if (StringUtil.isBlank(code)) {
				String msg = String.format(errMsgFmt, yaObj.auctionId, yaObj.obider, key);
				log(msg);
				return null;
			}
			log("[INFO][Service:getUnusedCode][Result]" + code);
			String[] spa = code.split(";");
			for (String sps : spa) {
				String[] spaa = sps.split(":");
				if (spaa.length != 2) {
					String msg = String.format(errMsgFmt, yaObj.auctionId, yaObj.obider, key);
					log(msg);
					return null;
				}
				YaSendCodeObject codeObj = new YaSendCodeObject();
				codeObj.codeType = spaa[0];
				codeObj.codeCd = spaa[1];
				codeObj.obider = yaObj.obider;
				codeObj.auctionId = yaObj.auctionId;
				codeList.add(codeObj);
			}
		} catch (IOException e) {
			log("[ERROR][getCode]" + e.getMessage());
			log(e);
			e.printStackTrace();
			return null;
		}
		return codeList;
	}

	private String composeSendMessage(List<YaSendCodeObject> codeSendOnceList) throws IOException {
		StringBuffer sb = new StringBuffer();

		List<String> list1 = NieConfig.getConfigByPrefix("yahoo.auction.autosend.message.prev");
		for (String str : list1) {
			sb.append(str).append("\n");
		}
		for (YaSendCodeObject codeObj : codeSendOnceList) {
			String str = keyMsgPrefix.get(codeObj.codeType);
			str = str == null ? "" : str + " ";
			sb.append(str).append(codeObj.codeCd).append("\n");
		}
		list1 = NieConfig.getConfigByPrefix("yahoo.auction.autosend.message.suffix");
		for (String str : list1) {
			sb.append(str).append("\n");
		}
		return sb.toString();
	}

	private YaSoldObject parseSold(WebElement rootWe) {
		YaSoldObject yaObj = new YaSoldObject();
		List<WebElement> weList = rootWe.findElement(
				By.cssSelector("dl[class=\"ptsItmInfoDl\"]")).findElements(
				By.tagName("dd"));
		for (WebElement we : weList) {
			if ("decItmName".equals(we.getAttribute("class"))) {
				yaObj.title = we.getText();
			}
			if ("decItmID".equals(we.getAttribute("class"))) {
				String str = we.getText();
				str = str.replaceAll("オークションID：", "");
				str = str.replaceAll(" ", "");
				yaObj.auctionId = str;
			}
			if ("decPrice".equals(we.getAttribute("class"))) {
				String str = we.getText();
				String numStr = str.substring(0,str.indexOf("落札価格"));
				numStr = numStr.replaceAll("落札数量", "");
				numStr = numStr.replaceAll("：", "");
				numStr = numStr.replaceAll(" ", "");
				yaObj.qtty = Integer.parseInt(numStr);
			}
			if ("decBuyerID".equals(we.getAttribute("class"))) {
				String str = we.getText();
				str = str.replaceAll("落札者：", "");
				if (str.indexOf("（") != -1) {
					str = str.substring(0, str.indexOf("（"));
				}
				str = str.replaceAll(" ", "");
				yaObj.obider = str;
			}
		}
		// FIXME まどめ取引
		return yaObj;
	}

	private boolean isAutoSendTarget(String title) {
		if (StringUtil.isBlank(title)) {
			return false;
		}
		String key = getIdentifierKeyFromTitle(title);
		if (StringUtil.isBlank(key)) {
			return false;
		}
		return true;
	}

	private String getIdentifierKeyFromTitle(String title) {
		int i1 = title.lastIndexOf("[");
		if (i1 == -1) {
			return null;
		}
		int i2 = title.lastIndexOf("]");
		if (i2 == -1) {
			return null;
		}
		return title.substring(i1 + 1, i2);
	}

	private void save(YaSoldObject yaObj, List<YaSendCodeObject> codeList) throws IOException {

		String hadSendFile = NieConfig
				.getConfig("yahoo.auction.autosend.hadSent.file");
		List<String> l = Lists.newArrayList();
		String fmt = "%s\t%s\t%s\t%s\t%s";
		l.add(String.format(fmt, getNowDateTime(), yaObj.auctionId, yaObj.obider, yaObj.title,yaObj.statusMsg));
		FileUtils.writeLines(new File(hadSendFile), l, true);

		Map<String,String> param = Maps.newHashMap();
		param.put("action", "asset");
		param.put("bidId", yaObj.auctionId);
		param.put("obidId", yaObj.obider);
		String codeS = "";
		for (YaSendCodeObject code : codeList) {
			codeS +=code.codeCd + ";";
		}
		param.put("codeCd", codeS.substring(0, codeS.length() - 1));
		String rslt = NieUtil.httpGet(NieConfig.getConfig("yahoo.auction.autosend.service.url"), param);
		if (StringUtil.isNotBlank(rslt)) {
			log("[INFO][Service:asset][Result]" + rslt);
		}
	}

	private void review(WebDriver driver, List<YaNoticeObject> lastestNoticeList) {
		
		List<String> hrefList = Lists.newArrayList();
		for (YaNoticeObject obj : lastestNoticeList) {
			String href = obj.href;

			String title = obj.title;

			if (!title.startsWith("評価:")) {
				continue;
			}
			hrefList.add(href);
		}
		if(hrefList.isEmpty()){
			//log("[RSLT]There is NONE to review.");
			return;
		}
		for (String href : hrefList) {
			driver.get(href);
			List<String> ahrefList = Lists.newArrayList();
			List<WebElement> weList = driver.findElements(By.tagName("a"));
			for (WebElement we : weList) {
				if ("評価する".equals(we.getText())) {
					ahrefList.add(we.getAttribute("href"));
				}
			}
			for (String url : ahrefList) {
				driver.get(url);
				List<NameValuePair> params = URLEncodedUtils.parse(url,
						Charset.forName("UTF-8"));
				YaSoldObject yaObj = new YaSoldObject();
				for (NameValuePair param : params) {
					if ("aID".equalsIgnoreCase(param.getName())) {
						yaObj.auctionId = param.getValue();
						break;
					}
				}
				doReview(driver, yaObj);
			}
		}
	}
	private void doReview(WebDriver driver, YaSoldObject yaObj) {

		log("[Reviewing][auctionId]" + yaObj.auctionId +"[obider]" + yaObj.obider);
		List<WebElement> weList = driver.findElements(By.tagName("input"));
		for (WebElement we : weList) {
			if ("定型コメント入力".equals(we.getAttribute("value"))) {
				we.click();
				break;
			}
		}
		for (WebElement we : weList) {
			if ("確認する".equals(we.getAttribute("value"))) {
				we.click();
				break;
			}
		}
		NieUtil.mySleepBySecond(1);

		weList = driver.findElements(By.tagName("input"));
		for (WebElement we : weList) {
			if ("評価を公開する".equals(we.getAttribute("value"))) {
				we.click();
				break;
			}
		}
		String msgFmt = "[SUCC]Review sucessfully!![auctionId]%s[obider]%s";
		String msg = String.format(msgFmt, yaObj.auctionId, yaObj.obider);
		log(msg);
	}


	private void finishCode(WebDriver driver, List<YaNoticeObject> lastestNoticeList) throws UnsupportedOperationException, IOException {
		List<String> hrefList = Lists.newArrayList();
		for (YaNoticeObject obj : lastestNoticeList) {
			String href = obj.href;

			String title = obj.title;
			if (!title.startsWith("商品受け取り完了:")) {
				continue;
			}
			if (!isAutoSendTarget(title)) {
				continue;
			}
			hrefList.add(href);
		}
		if(hrefList.isEmpty()){
			//log("[RSLT]There is NONE Auction to be finish");
			return;
		}
		for (String href : hrefList) {
			driver.get(href);
			YaSoldObject obj = parseYaObjectFromUrl(href);
			Map<String, String> param = Maps.newHashMap();
			param.put("action", "fin");
			if (StringUtil.isNotBlank(obj.auctionId)) {
				param.put("bidId", obj.auctionId);
			}
			if (StringUtil.isNotBlank(obj.obider)) {
				param.put("obidId", obj.obider);
			}
			String rslt = NieUtil.httpGet(NieConfig.getConfig("yahoo.auction.autosend.service.url"), param);
			if (StringUtil.isNotBlank(rslt)) {
				log("[INFO][Service:fin][Result]" + rslt);
			}
		}
	}
	
	private void republish(WebDriver driver, List<YaNoticeObject> lastestNoticeList) {
	
		List<YaNoticeObject> list = Lists.newArrayList();//終了（落札者なし）

		for (YaNoticeObject obj : lastestNoticeList) {
			String title = obj.title;

			if (title.startsWith("終了（落札者なし）:") && isAutoSendTarget(title) && hasStock(obj)) {
				list.add(obj);
			}
			if (title.startsWith("終了（落札者あり）:") && isAutoSendTarget(title) && hasStock(obj)) {
				list.add(obj);
			}
		}
		for (YaNoticeObject str : list) {
			try {
				log("[republish][publishing]" + str.title);
				String t = str.href;
				int idx = t.lastIndexOf("?");
				if (idx != -1) {
					t = t.substring(0, idx);
				}
				idx = t.lastIndexOf("/");
				String id = t.substring(idx + 1, t.length());
				driver.get(String.format(republishUrlFmt, id));
				List<WebElement> weList = driver.findElements(By
						.tagName("a"));
				for (WebElement we : weList) {
					if ("再出品".equals(we.getText())) {
						we.click();
						break;
					}
				}
				NieUtil.mySleepBySecond(1);

				weList = driver.findElements(By.tagName("input"));
				for (WebElement we : weList) {
					if ("確認画面へ".equals(we.getAttribute("value"))) {
						we.click();
						break;
					}
				}
				NieUtil.mySleepBySecond(1);

				weList = driver.findElements(By.tagName("input"));
				for (WebElement we : weList) {
					if ("ガイドラインと上記規約に同意して出品する".equals(we.getAttribute("value"))) {
						we.click();
						break;
					}
				}
				driver.get(str.href);

			} catch (Exception e) {
			}
		}
	}

	private boolean hasStock(YaNoticeObject obj) {
		String stockCheckKey = "[24時間]";
		if(obj.title.indexOf(stockCheckKey) == -1){
			// none for stock check
			return true;
		}
		String key = obj.identifierToken;
		Map<String, String> param = Maps.newHashMap();
		param.put("action", "stockCheck");
		param.put("codeType", key);
		try {
			String s = NieUtil.httpGet(NieConfig.getConfig("yahoo.auction.autosend.service.url"), param);
			if (StringUtil.isNotBlank(s)) {
				log("[INFO][Service:stockCheck][Result]" + s);
			}
			return new Boolean(s).booleanValue();
		} catch (UnsupportedOperationException | IOException e) {
			e.printStackTrace();
			log("[ERROR][hasStock]" + e.getMessage());
			log(e);
			return false;
		}
	}

	private WebDriver logon() {

		String rootUrl = "https://auctions.yahoo.co.jp/user/jp/show/mystatus";

		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		//WebDriver driver = WebDriverUtil.getHtmlUnitDriver();
		driver.manage().window().setSize(new Dimension(960, 960));
		driver.manage().window().setPosition(new Point(10, 10));
		driver.get(rootUrl);
		if (!driver.findElements(By.id("idBox")).isEmpty()) {
			WebElement el1 = driver.findElements(By.id("idBox")).get(0);
			el1.findElement(By.id("username")).sendKeys(
					NieConfig.getConfig("yahoo.user.name"));
			driver.findElement(By.id("btnNext")).click();
		}

		NieUtil.mySleepBySecond(2);

		driver.findElement(By.id("passwd")).sendKeys(
				NieConfig.getConfig("yahoo.user.password"));
		driver.findElement(By.id("btnSubmit")).click();

		NieUtil.mySleepBySecond(2);
		
		List<WebElement> eles = driver.findElements(By.cssSelector("div[class=\"yjmthloginarea\"]"));
		for(WebElement ele:eles){
			String txt = ele.getText();
			if(txt.indexOf("ログアウト") != -1){
				return driver;
			}
		}
		
		NieUtil.readLineFromSystemIn("Yahoo! login is finished? ANY KEY For already");

		return driver;
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
