package com.walk_nie.ya.auction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class YaAuAutoSend {
	private String myaucinfoUrl = "https://auctions.yahoo.co.jp/jp/show/myaucinfo";
	private String republishUrlFmt = "https://auctions.yahoo.co.jp/sell/jp/show/resubmit?aID=%s";
 
	private List<YaSendCodeObject> codeList = Lists.newArrayList();
	private Map<String, String> keyMsgPrefix = Maps.newHashMap();
	private File logFile = null;
	private List<String> codeSendOnce = Lists.newArrayList();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		YaAuAutoSend main = new YaAuAutoSend();
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
				if (hasPaid(driver)) {
					//log("[SEND]Same auction has paid and need to sent the code.");
					fetchLastestCode();
					send(driver);
				} else {
					log("[RSLT]There is NONE to sent.");
				}
				long t2 = System.currentTimeMillis();
				long dif = t2 - t1;
				if (dif > interval * 1000) {
					continue;
				}
				republish(driver);
				  t2 = System.currentTimeMillis();
				  dif = t2 - t1;
				if (dif > interval * 1000) {
					continue;
				}
				if (hasNeedReview(driver)) {
					review(driver);
				} else {
					log("[RSLT]There is NONE to review.");
				}
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

	private void init() throws IOException {

		List<String> list1 = NieConfig
				.getConfigByPrefix("yahoo.auction.autosend.key");
		for (String str : list1) {
			String[] sp = str.split(":");
			keyMsgPrefix.put(sp[0], sp[1]);
		}

		logFile = new File(
				NieConfig.getConfig("yahoo.auction.autosend.log.file"));

		List<String> list = Files.readLines(
				new File(NieConfig
						.getConfig("yahoo.auction.autosend.code.file")),
				Charset.forName("UTF-8"));
		for (String str : list) {
			String[] sp = str.split("\t");
			YaSendCodeObject obj = new YaSendCodeObject();
			obj.key = sp[0];
			obj.code = sp[1];
			obj.isUsedFlag = Boolean.parseBoolean(sp[2]);
			obj.purOrderId = sp[3];
			codeList.add(obj);
		}
		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
	}

	private boolean hasPaid(WebDriver driver) {
		driver.get(myaucinfoUrl);
		WebElement weTbl = driver.findElement(
				By.cssSelector("div[id=\"modItemNewList\"]")).findElement(
				By.tagName("table"));
		List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
		log("[hasPaid][START]Is There any auction paid?");
		for (WebElement trWe : trWeList) {
			List<WebElement> tdWeList = trWe.findElements(By.tagName("td"));
			if (tdWeList.size() != 3) {
				continue;
			}
			try {
				WebElement tdWeA = tdWeList.get(1).findElement(By.tagName("a"));
				String title = tdWeA.getText();
				//String href = tdWeA.getAttribute("href");
				
				//String auctionId = "", obider="";
//				List<NameValuePair> params = URLEncodedUtils.parse(new URI(href), Charset.forName("UTF-8"));
//				for (NameValuePair param : params) {
//					if ("aid".equalsIgnoreCase(param.getName())) {
//						auctionId = param.getValue();
//					}
//					if ("bid".equalsIgnoreCase(param.getName())) {
//						obider = param.getValue();
//					}
//				}
				if (!title.startsWith("支払い完了:")) {
					continue;
				}
				if (!isAutoSendTarget(title)) {
					continue;
				}
				//if (!hasStock(title)) {
				//	log("[ERROR][This Auction has none stock!][id]" + auctionId +"[obid]" + obider);
				//	continue;
				//}
				log("[hasPaid][END]There are some auction which had paid!");
				return true;
			} catch (Exception e) {

			}
		}
		log("[hasPaid][END]There are NONE auction which had paid!");
		return false;
	}

	private void send(WebDriver driver) throws IOException, URISyntaxException {
		driver.get(myaucinfoUrl);
		WebElement weTbl = driver.findElement(
				By.cssSelector("div[id=\"modItemNewList\"]")).findElement(
				By.tagName("table"));
		List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
		List<String> autoSendHrefList = Lists.newArrayList();
		log("[SEND]START");
		for (WebElement trWe : trWeList) {
			List<WebElement> tdWeList = trWe.findElements(By.tagName("td"));
			if (tdWeList.size() != 3) {
				continue;
			}
			WebElement tdWeA = null;
			try {
				tdWeA = tdWeList.get(1).findElement(By.tagName("a"));
			} catch (Exception e) {
				continue;
			}
			String href = tdWeA.getAttribute("href");

			String title = tdWeA.getText();
			if (!title.startsWith("支払い完了:")) {
				continue;
			}
			if (!isAutoSendTarget(title)) {
				continue;
			}
			
			String auctionId = "", obider="";
			List<NameValuePair> params = URLEncodedUtils.parse(new URI(href), Charset.forName("UTF-8"));
			for (NameValuePair param : params) {
				if ("aid".equalsIgnoreCase(param.getName())) {
					auctionId = param.getValue();
				}
				if ("bid".equalsIgnoreCase(param.getName())) {
					obider = param.getValue();
				}
			}
			if (!hasStock(title)) {
				log("[ERROR][This Auction has none stock!][id]" + auctionId +"[obid]" + obider);
				continue;
			}
			
			autoSendHrefList.add(href);
		}
		String urlFmt = "https://contact.auctions.yahoo.co.jp/seller/top?aid=%s&bid=%s";
		for (String href : autoSendHrefList) {
			YaSoldObject yaObjTemp = parseYaObjectFromUrl(href);
			log("[SEND][SETING][auctionId]" + yaObjTemp.auctionId +"[obider]" + yaObjTemp.obider);
			driver.get(String.format(urlFmt, yaObjTemp.auctionId,
					yaObjTemp.obider));
			
			codeSendOnce.clear();
			WebElement rootWe = driver.findElement(By
					.cssSelector("div[id=\"yjContentsBody\"]"));

			YaSoldObject yaObj = parseSold(rootWe);

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

			String message = composeSendMessage(yaObj);
			yaObj.statusMsg = message;
			if (StringUtil.isBlank(message)) {
				continue;
			}
			
			// 発送
			doSend(driver, rootWe, sendBtnWe,message, yaObj);

			// 発送済みコードを保存
			save(yaObj);
			
			driver.get(href);
			
			// TODO 落札者を評価する
			//doReview(driver, yaObj);
			
			// TODO republish
			//republish(driver, yaObj);
		}
		log("[SEND]END");
	}

	private YaSoldObject parseYaObjectFromUrl(String href) throws URISyntaxException {
		YaSoldObject yaObj = new YaSoldObject();
		List<NameValuePair> params = URLEncodedUtils.parse(new URI(href), Charset.forName("UTF-8"));
		for (NameValuePair param : params) {
			if ("aid".equalsIgnoreCase(param.getName())) {
				yaObj.auctionId = param.getValue();
			}
			if ("bid".equalsIgnoreCase(param.getName())) {
				yaObj.obider = param.getValue();
			}
		}
		return yaObj;
	}

	private void doSend(WebDriver driver, WebElement rootWe, WebElement sendBtnWe, String message, YaSoldObject yaObj) {
		log("[Sending][auctionId]" + yaObj.auctionId +"[obider]" + yaObj.obider);

		WebElement msgFormWe = rootWe.findElement(By
				.cssSelector("div[id=\"msgForm\"]"));
		msgFormWe.findElement(By.cssSelector("textarea[id=\"textarea\"]"))
				.sendKeys(message);
		// 送信
		msgFormWe.findElement(By.cssSelector("input[id=\"submitButton\"]"))
				.click();

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

	private void saveSendCode() {
		for (String code : codeSendOnce) {
			for (YaSendCodeObject obj : codeList) {
				if (code.equals(obj.code)) {
					obj.isUsedFlag = true;
					break;
				}
			}
		}

		String codeFile = NieConfig.getConfig("yahoo.auction.autosend.code.file");
		List<String> l = Lists.newArrayList();
		String fmt = "%s\t%s\t%b\t%s";
		for (YaSendCodeObject obj : codeList) {
			l.add(String.format(fmt, obj.key, obj.code, obj.isUsedFlag, obj.purOrderId));
		}
		try {
			FileUtils.writeLines(new File(codeFile), l, false);
		} catch (IOException e) {
			log("[ERROR][saveSendCode]"+e.getMessage());
		}
	}

	private String composeSendMessage(YaSoldObject yaObj) throws IOException {
		StringBuffer sb = new StringBuffer();
		String errMsgFmt = "[ERROR]NONE unused code!!![auctionId]%s[obider]%s[key]%s";

		List<String> list1 = NieConfig.getConfigByPrefix("yahoo.auction.autosend.message.prev");
		for (String str : list1) {
			sb.append(str).append("\n");
		}

		String[] sp = yaObj.title.split("\t");
		for (String s : sp) {
			String key = getIdentifierKeyFromTitle(s);
			for (int i = 0; i < yaObj.qtty; i++) {
				String code = getUnusedCode(key);
				if (StringUtil.isBlank(code)) {
					String msg = String.format(errMsgFmt, yaObj.auctionId,
							yaObj.obider, key);
					log(msg);
					return null;
				}
				yaObj.sendCode += " " + code;
				codeSendOnce.add(code);
				sb.append(keyMsgPrefix.get(key)).append(" ").append(code).append("\n");
			}
		}

		list1 = NieConfig.getConfigByPrefix("yahoo.auction.autosend.message.suffix");
		for (String str : list1) {
			sb.append(str).append("\n");
		}
		return sb.toString();
	}

	private String getUnusedCode(String key) throws IOException {
		for (YaSendCodeObject obj : codeList) {
			if (obj.key.equals(key) && !obj.isUsedFlag && !codeSendOnce.contains(obj.code)) {
				return obj.code;
			}
		}
		return null;
	}

	private void fetchLastestCode() {
		String src = NieConfig.getConfig("yahoo.auction.autosend.code.source.file");
		log("[FETCH][START]fetch the lastest code");
		log("[FETCH][URL]" + src);
		List<String> lines = Lists.newArrayList();
		if (src.startsWith("http")) {
			try {
				URL u = new URL(src);
				File file = new File("tmp" + System.currentTimeMillis() + ".txt");
				FileUtils.copyURLToFile(u, file);
				lines = FileUtils.readLines(file, "UTF-8");
				file.delete();
			} catch (Exception e) {
				log("[ERROR][fetchLastestCode]"+e.getMessage());
				e.printStackTrace();
				return ;
			}
		} else {
			File file = new File(src);
			try {
				lines = FileUtils.readLines(file, "UTF-8");
			} catch (Exception e) {
				log("[ERROR][fetchLastestCode]"+e.getMessage());
				e.printStackTrace();
				return ;
			}
		}
		
		List<YaSendCodeObject> codeListFetch = Lists.newArrayList();
		for (String line : lines) {
			YaSendCodeObject obj = new YaSendCodeObject();
			String[] spl = line.split("\t");
			int idx = 1;
			obj.key = spl[idx++];
			obj.code = spl[idx++];
			obj.purOrderId = spl[idx++];
			codeListFetch.add(obj);
		}

		List<YaSendCodeObject> newCodeList = Lists.newArrayList();
		for (YaSendCodeObject f : codeListFetch) {
			boolean stored = false;
			for (YaSendCodeObject obj : codeList) {
				if (obj.code.equals(f.code)) {
					stored = true;
					break;
				}
			}
			if (!stored) {
				f.isUsedFlag = false;
				newCodeList.add(f);
			}
		}
		if(!newCodeList.isEmpty()){
			codeList.addAll(newCodeList);
			saveSendCode();
			log("[FETCH][END][QTTY]The lastest code is " + newCodeList.size());
		}else{
			log("[FETCH][END]The lastest code is NONE");
		}
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

	private boolean hasStock(String title) throws IOException {

		String key = getIdentifierKeyFromTitle(title);
		return getUnusedCode(key) != null;
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

	private void save(YaSoldObject yaObj) throws IOException {

		String hadSendFile = NieConfig
				.getConfig("yahoo.auction.autosend.hadSent.file");
		List<String> l = Lists.newArrayList();
		String fmt = "%s\t%s\t%s\t%s\t%s";
		l.add(String.format(fmt, getNowDateTime(), yaObj.auctionId, yaObj.obider, yaObj.title,yaObj.statusMsg));
		FileUtils.writeLines(new File(hadSendFile), l, true);

		saveSendCode();
	}

	private boolean hasNeedReview(WebDriver driver) {
		driver.get(myaucinfoUrl);
		WebElement weTbl = driver.findElement(
				By.cssSelector("div[id=\"modItemNewList\"]")).findElement(
				By.tagName("table"));
		log("[CHECK]Is there  any one which is not reviewed.");
		List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
		for (WebElement trWe : trWeList) {
			List<WebElement> tdWeList = trWe.findElements(By.tagName("td"));
			if (tdWeList.size() != 3) {
				continue;
			}
			WebElement tdWeA = null;
			try {
				tdWeA = tdWeList.get(1).findElement(By.tagName("a"));
			} catch (Exception e) {
				continue;
			}
			String title = tdWeA.getText();
			if (title.startsWith("評価:")) {
				return true;
			}
		}
		return false;
	}

	private void review(WebDriver driver) {
		driver.get(myaucinfoUrl);
		WebElement weTbl = driver.findElement(
				By.cssSelector("div[id=\"modItemNewList\"]")).findElement(
				By.tagName("table"));
		List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
		List<String> autoSendHrefList = Lists.newArrayList();
		for (WebElement trWe : trWeList) {
			List<WebElement> tdWeList = trWe.findElements(By.tagName("td"));
			if (tdWeList.size() != 3) {
				continue;
			}
			WebElement tdWeA = null;
			try {
				tdWeA = tdWeList.get(1).findElement(By.tagName("a"));
			} catch (Exception e) {
				continue;
			}
			String title = tdWeA.getText();

			if (!title.startsWith("評価:")) {
				continue;
			}
			String href = tdWeA.getAttribute("href");
			autoSendHrefList.add(href);
		}
		for (String href : autoSendHrefList) {
			driver.get(href);
			List<String> hrefList = Lists.newArrayList();
			List<WebElement> weList = driver.findElements(By.tagName("a"));
			for (WebElement we : weList) {
				if ("評価する".equals(we.getText())) {
					hrefList.add(we.getAttribute("href"));
				}
			}
			for (String url : hrefList) {
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

	private void republish(WebDriver driver) {
		driver.get(myaucinfoUrl);
		WebElement weTbl = driver.findElement(
				By.cssSelector("div[id=\"modItemNewList\"]")).findElement(
				By.tagName("table"));
		List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));

		// log("[republish][START]Is There any auction finished?");
		List<String> list = Lists.newArrayList();//終了（落札者なし）
		for (WebElement trWe : trWeList) {
			try {
				List<WebElement> tdWeList = trWe.findElements(By.tagName("td"));
				if (tdWeList.size() != 3) {
					continue;
				}
				WebElement tdWeA = tdWeList.get(1).findElement(By.tagName("a"));
				String title = tdWeA.getText();
				String href = tdWeA.getAttribute("href");

				if (title.startsWith("終了（落札者なし）:")
						&& isAutoSendTarget(title)) {
					list.add(title + "\t" + href);
				}
				if (title.startsWith("終了（落札者あり）:")
						&& isAutoSendTarget(title)) {
					list.add(title + "\t" + href);
				}

			} catch (Exception e) {

			}
		}
		for (String str : list) {
			try {
				String[] spit = str.split("\t");
				log("[republish][publishing]" + spit[0]);
				String t = spit[1];
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
				driver.get(spit[1]);

			} catch (Exception e) {

			}
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
