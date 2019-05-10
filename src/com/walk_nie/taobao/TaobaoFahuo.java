package com.walk_nie.taobao;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.support.ui.Select;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Maps;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;


public class TaobaoFahuo {
	String dtlUrl = "https://trade.taobao.com/trade/detail/trade_item_detail.htm?bizOrderId=";

	private File logFile = null;
	public static void main(String[] args) throws IOException {
		TaobaoFahuo anor = new TaobaoFahuo();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		anor.init();
		anor.process(driver);
	}
	public void execute() throws IOException {
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		init();
		int interval = Integer.parseInt(NieConfig.getConfig("taobao.deamon.interval"));// second
		while (true) {
			try {
				long t1 = System.currentTimeMillis();
				process(driver);
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
	
	public void process(WebDriver driver) {
		TaobaoFahuoObject obj = getLastestFahuo();
		if (obj != null) {
			try {
				fahuo(driver, obj);
			} catch (Exception e) {
				updateStatus(obj, "fhFailure");
			}
		}
	}
	
	private TaobaoFahuoObject getLastestFahuo() {

		String action = "listFahuoOne";
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", action);
			
			String rslt = NieUtil.httpGet(NieConfig.getConfig("taobao.service.url"), param);
			NieUtil.log(logFile, "[INFO][Service:" + action + "][RESULT]" + rslt);
			if (StringUtil.isBlank(rslt)) {
				return null;
			}
			TaobaoFahuoObject obj  = new  TaobaoFahuoObject();
			Json j = new Json();
			Map<String, Object> objMap = null;
			objMap = j.toType(rslt, Map.class);
			obj.uid = (String) objMap.get("uid");
			obj.orderNo = (String) objMap.get("orderNo");
			obj.trackTraceNo = (String) objMap.get("trackTraceNo");
			obj.tranferProviderName = (String) objMap.get("tranferProviderName");
			if (StringUtil.isBlank(obj.orderNo)) {
				NieUtil.log(logFile, "[ERROR][executeServiceCommand]orderNo is NULL!!");
				return null;
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:" + action + "]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
		return null;
	}

	private void fahuo(WebDriver driver, TaobaoFahuoObject obj) {
		driver.get(dtlUrl + obj.orderNo);
		logon(driver);
		WebElement weTraceNo = null;// TODO
		WebElement weProvideName = null;// TODO
		WebElement weFahuo = null;// TODO
		String trackTraceNo = obj.trackTraceNo;
		if(StringUtil.isBlank(trackTraceNo)){
			// 
			Select dropdown = new Select(weProvideName);
			dropdown.selectByValue("XXXX");
			weFahuo.click();
			updateStatus(obj,"fahuo");
			return;
		}
		weTraceNo.sendKeys(trackTraceNo);
		Select dropdown = new Select(weProvideName);
		dropdown.selectByValue(mapTransferProvide(obj.trackTraceNo,obj.tranferProviderName));
		weFahuo.click();
		updateStatus(obj,"fahuo");
	}

	private String mapTransferProvide(String trackTraceNo, String tranferProviderName) {

		String provideName = "";
		if (!StringUtil.isBlank(trackTraceNo) && StringUtil.isBlank(tranferProviderName)) {
			if (trackTraceNo.toLowerCase().startsWith("EJ") || trackTraceNo.toLowerCase().startsWith("EM")
					|| trackTraceNo.toLowerCase().startsWith("CD")) {
				provideName = "EMS";
			} else {
				provideName = "ZHONGTONG"; 
			}
		}
		if (StringUtil.isBlank(provideName)) {
			provideName = "ZHONGTONG"; 
		}
		// TODO
		return provideName;
	}
	private void updateStatus(TaobaoFahuoObject obj, String status) {

		String action = "updateFahuoStatus";
		try {
			Map<String, String> param = Maps.newHashMap();
			param.put("action", action);
			param.put("uid", obj.uid);
			param.put("toStatus", status);
			
			String rslt = NieUtil.httpGet(NieConfig.getConfig("taobao.service.url"), param);
			if (!StringUtil.isBlank(rslt)) {
				NieUtil.log(logFile, "[INFO][Service:" + action + "][RESULT]" + rslt);
			}
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Service:" + action + "]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
	}
	
	private void logon(WebDriver driver) {
		// TODO
		String titleName = "";
		String title = driver.getTitle();
		if(titleName.equals(title)){
			return;
		}
		TaobaoUtil.login(driver);
		
		title = driver.getTitle();
		if(titleName.equals(title)){
			return ;
		}
		
		NieUtil.readLineFromSystemIn("taobao login is finished? ANY KEY For already");
	}

	public void init() throws IOException {

		logFile = new File(NieConfig.getConfig("taobao.log.file"));

		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
	}
	
	class TaobaoFahuoObject {
		public String uid = "";
		public String orderNo = "";
		public String trackTraceNo = "";
		public String tranferProviderName = "";
	}

}
