package com.walk_nie.ya.auction;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;
import com.walk_nie.ya.YaUtil;

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
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		main.init(driver);
		
		// FOR TEST
//		List<YaNoticeObject> lastestNoticeList = Lists.newArrayList();
//		YaNoticeObject yaObj = new YaNoticeObject();
//		yaObj.title ="支払い完了:[即決][即時発送][3分内番号通知][土日祝も]アマゾン Amazon ギフト券 15円 住所不要 評価任意 無制限入札[AMZNJPY15]";
//		yaObj.href = "https://contact.auctions.yahoo.co.jp/seller/top?aid=u275294410&syid=yiyi2014jp&bid=aromari0407&oid=60802725-1756757115-9062737";
//		yaObj.identifierToken = main.getIdentifierKeyFromTitle(yaObj.title);
//		lastestNoticeList.add(yaObj);
//		main.send(driver, lastestNoticeList );

		// FOR TEST
		//main.getUnusedCode("AMZNJPY15", "XXX", "XX", 2);
		List<YaNoticeObject> lastestNoticeList = Lists.newArrayList();
		YaNoticeObject yaObj = new YaNoticeObject();
		yaObj.title ="終了（落札者あり）:[即時発送][土日祝も]PSNカード $40ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita[PSNUSD40]";
		yaObj.href = "https://page.auctions.yahoo.co.jp/jp/auction/x619502523";
		lastestNoticeList.add(yaObj);
		main.republish(driver, lastestNoticeList );
		//main.execute(driver);
	}

	public void execute() throws IOException {
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		init(driver);
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
					NieUtil.log(logFile, "[SLEEP]zzzZZZzzz...");
					NieUtil.mySleepBySecond((new Long(interval - dif / 1000))
							.intValue());
				}
			} catch (Exception ex) {
				NieUtil.log(logFile, ex);
			}
		}
	}

	public void execute(WebDriver driver) throws IOException {
		int interval = Integer.parseInt(NieConfig
				.getConfig("yahoo.auction.autosend.interval"));// second
		long t1 = System.currentTimeMillis();
		
		forSeller(driver,interval, t1);
		
		long t2 = System.currentTimeMillis();
		forBuyer(driver,interval,t2);
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

	private void forSeller(WebDriver driver, int interval, long t1) throws IOException {

		List<YaNoticeObject> lastestNoticeList = getLastestNotice(driver);
		if(lastestNoticeList == null || lastestNoticeList.isEmpty()){
			return;
		}
		
		addBided(driver, lastestNoticeList);
		
		if (hasPaid(driver, lastestNoticeList)) {
			send(driver, lastestNoticeList);
		} else {
			//NieUtil.log(logFile, "[RSLT]There is NONE to sent.");
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
			
			NieUtil.log(logFile, "[INFO][Service:getAplyBidMsgOne][Result]" + rslt);
			if (rslt.indexOf("ERROR") != -1) {
				return;
			}
			
			Json j = new Json();
			Map<String, Object> objMap = null;
			try {
				objMap = j.toType(rslt, Map.class);

				String auctionId = (String) objMap.get("bidId");
				String obider = (String) objMap.get("obidId");
				String replymsg = (String) objMap.get("replymsg");
				NieUtil.log(logFile, "[publishBidMsg][auctionId]" + auctionId + "[obider]" + obider + "[msg]" + replymsg);
				if (StringUtil.isBlank(replymsg)) {
					return;
				}
				driver.get(String.format(bidUrlFmt, auctionId, obider));

				WebElement rootWe = driver.findElement(By.cssSelector("div[id=\"yjContentsBody\"]"));

				WebElement msgFormWe = rootWe.findElement(By.cssSelector("div[id=\"msgForm\"]"));
				msgFormWe.findElement(By.cssSelector("textarea[id=\"textarea\"]")).sendKeys(replymsg);

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
					NieUtil.log(logFile, "[INFO][Service:updateBidMsgStatus][Result]" + rslt1);
				}

			} catch (Exception ex) {
				NieUtil.log(logFile, "[ERROR][addBidMsg]" + ex.getMessage());
				NieUtil.log(logFile, ex);
				ex.printStackTrace();
				return;
			}
		} catch (Exception e) {
			NieUtil.log(logFile, "[ERROR][addBidMsg]" + e.getMessage());
			NieUtil.log(logFile, e);
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
				NieUtil.log(logFile, "[INFO][Service:addBidMsg][Result]" + rslt);
			}
		} catch (Exception e) {
			NieUtil.log(logFile, "[ERROR][addBidMsg]" + e.getMessage());
			NieUtil.log(logFile, e);
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

			String obiderId = "";
			List<WebElement> wes = driver.findElements(By.tagName("td"));
			String finder = "/ 評価：";
			for(WebElement we:wes){
				String txt =we.getText();
				if(txt.indexOf(finder) != -1){
					String[] sparr = txt.split("/");
					if(sparr.length > 1){
						obiderId = 	sparr[0].replaceAll(" ", "");
						obiderId = 	obiderId.trim();
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
				NieUtil.log(logFile, "[INFO][Service:bided][Result]" + rslt);
				return false;
			}
			return true;
		} catch (Exception e) {
			NieUtil.log(logFile, "[ERROR][addBided]" + e.getMessage());
			NieUtil.log(logFile, e);
			e.printStackTrace();
			return false;
		}
	}

	private List<YaNoticeObject> getLastestNotice(WebDriver driver) {
		List<YaNoticeObject> list = Lists.newArrayList();
		driver.get(myaucinfoUrl);
		WebElement weTbl = null;
		try {
			weTbl = driver.findElement(
					By.cssSelector("div[id=\"modItemNewList\"]")).findElement(
					By.tagName("table"));
		} catch (Exception e) {
			NieUtil.log(logFile, "[INFO][getLastestNotice]There are NONE notice!");
			return list;
		}
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

	public void init(WebDriver driver) throws IOException {

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
		
		driver.get(myaucinfoUrl);
		logon(driver);
	}

	private boolean hasPaid(WebDriver driver, List<YaNoticeObject> lastestNoticeList) {

		//NieUtil.log(logFile, "[hasPaid][START]Is There any auction paid?");
		for (YaNoticeObject obj : lastestNoticeList) {
			String title = obj.title;

			if (!title.startsWith("支払い完了:")) {
				continue;
			}
			if (!isAutoSendTarget(title)) {
				continue;
			}

			//NieUtil.log(logFile, "[hasPaid][END]There are some auction which had paid!");
			return true;
		}
		//NieUtil.log(logFile, "[hasPaid][END]There are NONE auction which had paid!");
		return false;
	}

	private void send(WebDriver driver, List<YaNoticeObject> lastestNoticeList) throws IOException {

		List<YaNoticeObject> sendTargetList = Lists.newArrayList();
		
		//NieUtil.log(logFile, "[SEND]START");
		for (YaNoticeObject obj : lastestNoticeList) {
			String title = obj.title;
			if (!title.startsWith("支払い完了:")) {
				continue;
			}
			if (!isAutoSendTarget(title)) {
				continue;
			}
			
			sendTargetList.add(obj);
		}
		for (YaNoticeObject obj : sendTargetList) {
			YaSoldObject yaObj = parseYaObjectFromUrl(obj.href);
			NieUtil.log(logFile, "[SEND][SETING][auctionId]" + yaObj.auctionId + "[obider]" + yaObj.obider);
			driver.get(String.format(bidUrlFmt, yaObj.auctionId, yaObj.obider));

			WebElement rootWe = driver.findElement(By
					.cssSelector("div[id=\"yjContentsBody\"]"));

			YaSoldObject yaObjPar = parseSold(rootWe);

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
				NieUtil.log(logFile, "[ERROR][This Auction has sent!][auctionId]" + yaObj.auctionId +"[obider]" + yaObj.obider);
				driver.get(obj.href);
				continue;
			}
			List<YaSendCodeObject> codeList = getUnusedCode(
					obj.identifierToken, yaObj.auctionId, yaObj.obider,yaObjPar.qtty);
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
			
			driver.get(obj.href);
		}
		//NieUtil.log(logFile, "[SEND]END");
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
			NieUtil.log(logFile, e);
		}
		return null;
	}

	private void doSend(WebDriver driver, WebElement rootWe, WebElement sendBtnWe, String message, YaSoldObject yaObj) {
		NieUtil.log(logFile, "[Sending][auctionId]" + yaObj.auctionId +"[obider]" + yaObj.obider);

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
		NieUtil.log(logFile,msg);
		NieUtil.mySleepBySecond(1);
	}

	private List<YaSendCodeObject> getUnusedCode(String key, String auctionId,
			String obider, int qtty) {
		List<YaSendCodeObject> codeList = Lists.newArrayList();
		String errMsgFmt = "[ERROR]NONE unused code!!![auctionId]%s[obider]%s[key]%s";
		for (int i = 0; i < qtty; i++) {
			try {
				Map<String, String> param = Maps.newHashMap();
				param.put("action", "get");
				param.put("codeType", key);
				param.put("bidId", auctionId);
				param.put("obidId", obider);
				// return codeType:codeCd;codeType:codeCd;
				String code = NieUtil
						.httpGet(
								NieConfig
										.getConfig("yahoo.auction.autosend.service.url"),
								param);

				if (StringUtil.isBlank(code)) {
					String msg = String.format(errMsgFmt, auctionId, obider,
							key);
					NieUtil.log(logFile, msg);
					return null;
				}
				NieUtil.log(logFile, "[INFO][Service:getUnusedCode][Result]"
						+ code);
				String[] spa = code.split(";");
				for (String sps : spa) {
					String[] spaa = sps.split(":");
					if (spaa.length != 2) {
						String msg = String.format(errMsgFmt, auctionId,
								obider, key);
						NieUtil.log(logFile, msg);
						return null;
					}
					YaSendCodeObject codeObj = new YaSendCodeObject();
					codeObj.codeType = spaa[0];
					codeObj.codeCd = spaa[1];
					codeObj.obider = obider;
					codeObj.auctionId = auctionId;
					codeList.add(codeObj);
				}
			} catch (IOException e) {
				NieUtil.log(logFile, "[ERROR][getCode]" + e.getMessage());
				NieUtil.log(logFile, e);
				e.printStackTrace();
				return null;
			}
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
		l.add(String.format(fmt, NieUtil.getNowDateTime(), yaObj.auctionId, yaObj.obider, yaObj.title,yaObj.statusMsg));
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
			NieUtil.log(logFile, "[INFO][Service:asset][Result]" + rslt);
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
			//NieUtil.log(logFile, "[RSLT]There is NONE to review.");
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

		NieUtil.log(logFile, "[Reviewing][auctionId]" + yaObj.auctionId +"[obider]" + yaObj.obider);
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
		NieUtil.log(logFile, msg);
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
			//NieUtil.log(logFile, "[RSLT]There is NONE Auction to be finish");
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
				NieUtil.log(logFile, "[INFO][Service:fin][Result]" + rslt);
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
				NieUtil.log(logFile, "[republish][publishing]" + str.title);
				String t = str.href;
				int idx = t.lastIndexOf("?");
				if (idx != -1) {
					t = t.substring(0, idx);
				}
				idx = t.lastIndexOf("/");
				String id = t.substring(idx + 1, t.length());
				driver.get(String.format(republishUrlFmt, id));
//				List<WebElement> weList = driver.findElements(By.tagName("a"));
//				boolean found = false;
//				for (WebElement we : weList) {
//					if ("再出品".equals(we.getText())) {
//						we.click();
//						found = true;
//						break;
//					}
//				}
//				if(!found){
//					NieUtil.log(logFile, "[ERROR][republish]再出品ボタンがない!");
//				}
				boolean found = false;
				List<WebElement> weList = driver.findElements(By.cssSelector("input[type=\"button\"]"));
				for (WebElement we : weList) {
					if ("確認する".equals(we.getAttribute("value"))) {
						we.click();
						found = true;
						break;
					}
				}
				if(!found){
					NieUtil.log(logFile, "[ERROR][republish]確認ボタンがない!");
				}
				found = false;
				NieUtil.mySleepBySecond(1);

				weList = driver.findElements(By.tagName("input"));
				for (WebElement we : weList) {
					if ("ガイドラインと上記規約に同意して出品する".equals(we.getAttribute("value"))) {
						we.click();
						found = true;
						break;
					}
				}
				if(!found){
					NieUtil.log(logFile, "[ERROR][republish]出品するボタンがない!");
				}else{
					driver.get(str.href);
				}
				NieUtil.mySleepBySecond(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean hasStock(YaNoticeObject obj) {
		String stockCheckKey = "[3分内番号通知]";
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
				NieUtil.log(logFile, "[INFO][Service:stockCheck][Result]" + s);
			}
			return new Boolean(s).booleanValue();
		} catch (UnsupportedOperationException | IOException e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][hasStock]" + e.getMessage());
			NieUtil.log(logFile, e);
			return false;
		}
	}

	private void logon(WebDriver driver) {

		YaUtil.login(driver, NieConfig.getConfig("yahoo.user.name"), NieConfig.getConfig("yahoo.user.password"));
		/*
		String rootUrl = "https://auctions.yahoo.co.jp/user/jp/show/mystatus";

		//WebDriver driver = WebDriverUtil.getHtmlUnitDriver();
		//driver.manage().window().setSize(new Dimension(960, 960));
		//driver.manage().window().setPosition(new Point(10, 10));
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
		*/
	}

}
