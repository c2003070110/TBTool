package com.walk_nie.ya.auction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.jsoup.helper.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.google.common.io.Files;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class YaAuAutoSendDeamon {
	private String myaucinfoUrl = "https://auctions.yahoo.co.jp/jp/show/myaucinfo";
	//private String soldListUrl = "https://auctions.yahoo.co.jp/closeduser/jp/show/mystatus?select=closed&hasWinner=1";
	//private String soldPageUrl = "https://contact.auctions.yahoo.co.jp/seller/top?aid=%s&bid=%s";
	//private String allwinnerUrl = "https://auctions.yahoo.co.jp/jp/show/allwinners?aID=";
	
	private List<YaSoldObject> hadSendAuctionObjectList = Lists.newArrayList();
	// key\tcode\tused
	private List<YaSendCodeObject> codeList = Lists.newArrayList();
	private Map<String,String> keyMsgPrefix = Maps.newHashMap();
	private File logFile = null;
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		YaAuAutoSendDeamon main = new YaAuAutoSendDeamon();
		main.execute();
	}

	public void execute() throws IOException {
		init();
		WebDriver driver = logon();
        int interval = 60 ;//60s
		while (true) {
			try {
				long t1 = System.currentTimeMillis();
				if(hasPaid(driver)){
					send(driver);
				}
				long t2 = System.currentTimeMillis();
				long dif = t2-t1;
				if (dif > interval * 1000) {
					continue;
				}
				if(hasNeedReview(driver)){
					review(driver);
				}
				if (dif < interval * 1000) {
					NieUtil.mySleepBySecond((new Long(interval - dif / 1000)).intValue());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void init() throws IOException {
		String hadSendFile = NieConfig.getConfig("yahoo.auction.hadSend.file");
		List<String> list = Files.readLines(new File(hadSendFile), Charset.forName("UTF-8"));
		for (String str : list) {
			String[] sp = str.split("\t");
			YaSoldObject yaObj = new YaSoldObject();
			yaObj.auctionId = sp[0];
			if (sp.length > 1) {
				yaObj.obider = sp[1];
			}
			if (sp.length > 2) {
				yaObj.sendCode = sp[2].trim();
			}
			hadSendAuctionObjectList.add(yaObj);
		}
		
		List<String> list1 = NieConfig.getConfigByPrefix("yahoo.auction.autoSend.key");
		for (String str : list1) {
			String[] sp = str.split(":");
			keyMsgPrefix.put(sp[0], sp[1]);
		}
		
		logFile = new File(NieConfig.getConfig("yahoo.auction.log.file"));

		list  = Files.readLines(new File(NieConfig.getConfig("yahoo.auction.code.file")),
				Charset.forName("UTF-8"));
		for (String str : list) {
			String[] sp = str.split("\t");
			YaSendCodeObject obj = new YaSendCodeObject();
			obj.key = sp[0];
			obj.code = sp[1];
			obj.validFlag = Boolean.getBoolean(sp[2]);
			obj.purOrderId = sp[3];
			codeList.add(obj);
		}
	}

	private boolean hasPaid(WebDriver driver) {
		driver.get(myaucinfoUrl);
		WebElement weTbl = driver.findElement(By.cssSelector("div[id=\"modItemNewList\"]"))
				.findElement(By.tagName("table"));
		List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
		for (WebElement trWe : trWeList) {
			List<WebElement> tdWeList = trWe.findElements(By.tagName("td"));
			if (tdWeList.size() != 3) {
				continue;
			}
			WebElement tdWeA = tdWeList.get(1).findElement(By.tagName("a"));
			String title = tdWeA.getText();
			if (isAutoSendTarget(title) && title.startsWith("支払い完了:")) {
				return true;
			}
		}
		return false;
	}

	private void send(WebDriver driver) throws IOException {
		driver.get(myaucinfoUrl);
		WebElement weTbl = driver.findElement(By.cssSelector("div[id=\"modItemNewList\"]"))
				.findElement(By.tagName("table"));
		List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
		List<String> autoSendHrefList = Lists.newArrayList();
		for (WebElement trWe : trWeList) {
			List<WebElement> tdWeList = trWe.findElements(By.tagName("td"));
			if (tdWeList.size() != 3) {
				continue;
			}
			WebElement tdWeA = tdWeList.get(1).findElement(By.tagName("a"));
			String title = tdWeA.getText();
			if (!isAutoSendTarget(title)) {
				continue;
			}
			if (!title.startsWith("支払い完了:")) {
				continue;
			}
			String href = tdWeA.getAttribute("href");
			autoSendHrefList.add(href);
		}
		String msgFmt = "[SUCC]send sucessfully!![auctionId]%s[obider]%s[msg]%s";
		for (String href : autoSendHrefList) {
			driver.get(href);
			YaSoldObject yaObj = parseSold(driver);
			if (hadSend(yaObj)) {
				continue;
			}

			String message = composeSendMessage(yaObj);
			if(StringUtil.isBlank(message)){
				continue;
			}
			WebElement msgFormWe = driver.findElement(By.cssSelector("div[id=\"msgForm\"]"));
			msgFormWe.findElement(By.cssSelector("textarea[id=\"textarea\"]")).sendKeys(message);
			// 送信
			msgFormWe.findElement(By.cssSelector("input[id=\"submitButton\"]")).click();
			
			// TODO 発送連絡をする
			NieUtil.mySleepBySecond(2);
			// 確認する
			NieUtil.mySleepBySecond(2);
			// 決定する
			NieUtil.mySleepBySecond(2);
			
			// 落札者を評価する
			// 非常に良い 定型コメント入力
			// 確認する
			// 評価を公開する
			
			hadSendAuctionObjectList.add(yaObj);
			String msg = String.format(msgFmt, yaObj.auctionId, yaObj.obider, message);
			System.out.println(msg);
			log(msg);
		}
		
		save();
	}

	private String composeSendMessage(YaSoldObject yaObj) throws IOException {
		StringBuffer sb = new StringBuffer();
		String errMsgFmt = "[ERROR]Code IS NOT Exist!!![auctionId]%s[obider]%s[key]%s";
		// TODO XXXX
		sb.append("XXX").append("\n");
		String[] sp = yaObj.title.split("\t");
		for (String s : sp) {
			String key = getIdentifierKeyFromTitle(s);
			String code = getValidCode(key);
			if(StringUtil.isBlank(code)){
				String msg = String.format(errMsgFmt, yaObj.auctionId, yaObj.obider, key);
				System.err.println(msg);
				log(msg);
				return null;
			}
			yaObj.sendCode += " " + code;
			sb.append(keyMsgPrefix.get(key)).append(" ").append(code).append("\n");
		}
		sb.append("XXX").append("");
		return sb.toString();
	}

	private String getValidCode(String key) {
		for (YaSendCodeObject obj : codeList) {
			if(obj.key.equals(key) && obj.validFlag){
				obj.validFlag = false;
				return obj.code;
			}
		}
		List<YaSendCodeObject> codeListFetch = fetchLastestCode();
		List<YaSendCodeObject> newCodeList = Lists.newArrayList();
		for (YaSendCodeObject f : codeListFetch) {
			boolean stored = false;
			for (YaSendCodeObject obj : codeList) {
				if (obj.code.equals(f.code)) {
					stored = true;
				}
			}
			if (!stored) {
				f.validFlag = true;
				newCodeList.add(f);
			}
		}
		codeList.addAll(newCodeList);
		for (YaSendCodeObject obj : newCodeList) {
			if(obj.key.equals(key) && obj.validFlag){
				obj.validFlag = false;
				return obj.code;
			}
		}
		return null;
	}

	private List<YaSendCodeObject> fetchLastestCode() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	private YaSoldObject parseSold(WebDriver driver) {
		YaSoldObject yaObj = new YaSoldObject();
		// TODO
		// TODO まどめ取引
		return yaObj;
	}

	private boolean isAutoSendTarget(String title) {
		if(StringUtil.isBlank(title)){
			return false;
		}
		String key = getIdentifierKeyFromTitle(title);
		if(StringUtil.isBlank(key)){
			return false;
		}
		return true;
	}

	private boolean hadSend(YaSoldObject yaObj) {
		for (YaSoldObject yaObjHad : hadSendAuctionObjectList) {
			if (yaObjHad.auctionId.equals(yaObj.auctionId) && yaObjHad.obider.equals(yaObj.obider)) {
				return true;
			}
		}
		return false;
	}

	private String getIdentifierKeyFromTitle(String title) {
		int i1 = title.indexOf("[");
		if(i1 == -1){
			return null;
		}
		int i2 = title.indexOf("]");
		if(i2 == -1){
			return null;
		}
		return title.substring(i1+1, i2);
	}


	private void save() throws IOException {
		
		String hadSendFile = NieConfig.getConfig("yahoo.auction.hadSend.file");
		List<String> l = Lists.newArrayList();
		String fmt = "%s\t%s\t%s";
		for(YaSoldObject obj :hadSendAuctionObjectList){
			l.add(String.format(fmt, obj.auctionId, obj.obider,obj.sendCode));
		}
		FileUtils.writeLines(new File(hadSendFile), l, false);
		
		
		String codeFile = NieConfig.getConfig("yahoo.auction.code.file");
		l = Lists.newArrayList();
		fmt = "%s\t%s\t%b\t%s";
		for (YaSendCodeObject obj : codeList) {
			l.add(String.format(fmt, obj.key, obj.code, obj.validFlag, obj.purOrderId));
		}
		FileUtils.writeLines(new File(codeFile), l, false);
	}

	private boolean hasNeedReview(WebDriver driver) {
		driver.get(myaucinfoUrl);
		WebElement weTbl = driver.findElement(By.cssSelector("div[id=\"modItemNewList\"]"))
				.findElement(By.tagName("table"));
		List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
		for (WebElement trWe : trWeList) {
			List<WebElement> tdWeList = trWe.findElements(By.tagName("td"));
			if (tdWeList.size() != 3) {
				continue;
			}
			WebElement tdWeA = tdWeList.get(1).findElement(By.tagName("a"));
			String title = tdWeA.getText();
			if (title.startsWith("評価:")) {
				return true;
			}
		}
		return false;
	}

	private void review(WebDriver driver) {
		driver.get(myaucinfoUrl);
		WebElement weTbl = driver.findElement(By.cssSelector("div[id=\"modItemNewList\"]"))
				.findElement(By.tagName("table"));
		List<WebElement> trWeList = weTbl.findElements(By.tagName("tr"));
		List<String> autoSendHrefList = Lists.newArrayList();
		for (WebElement trWe : trWeList) {
			List<WebElement> tdWeList = trWe.findElements(By.tagName("td"));
			if (tdWeList.size() != 3) {
				continue;
			}
			WebElement tdWeA = tdWeList.get(1).findElement(By.tagName("a"));
			String title = tdWeA.getText();
			
			if (!title.startsWith("評価:")) {
				continue;
			}
			String href = tdWeA.getAttribute("href");
			autoSendHrefList.add(href);
		}
		for (String href : autoSendHrefList) {
			driver.get(href);
			// TODO
			String yaAucId = "";

			List<WebElement> wes = driver.findElements(By.tagName("b"));
			boolean hasReviewed = false;
			for (WebElement weB : wes) {
				if ("前回、評価した内容".equals(weB.getText())) {
					hasReviewed = true;
					break;
				}
			}
			if (hasReviewed) {
				System.out.println("[INFO][評価すでに完了しています。]ヤフーオクID＝" + yaAucId);
				return;
			}
			driver.findElement(By.id("commonTextIn")).click();
			NieUtil.mySleepBySecond(2);
			driver.findElement(By.id("decCheck")).click();
			NieUtil.mySleepBySecond(2);
			driver.findElement(By.cssSelector("input[class=\"libBtnBlueL\"]"))
					.click();
		}
	}

	private WebDriver logon() {

		String rootUrl = "https://auctions.yahoo.co.jp/user/jp/show/mystatus";

		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		driver.get(rootUrl);

		if (!driver.findElements(By.id("idBox")).isEmpty()) {
			WebElement el1 = driver.findElements(By.id("idBox")).get(0);
			el1.findElement(By.id("username")).sendKeys(NieConfig.getConfig("yahoo.user.name"));
			driver.findElement(By.id("btnNext")).click();
		}

		NieUtil.mySleepBySecond(2);
		driver.findElement(By.id("passwd")).sendKeys(NieConfig.getConfig("yahoo.user.password"));
		driver.findElement(By.id("btnSubmit")).click();
		return driver;
	}

	private void log(String string) throws IOException {

		String today = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy-MM-dd");
		FileUtils.write(logFile, "[" + today+  "]" + string + "-------\n",
				Charset.forName("UTF-8"), true);
	}

}
