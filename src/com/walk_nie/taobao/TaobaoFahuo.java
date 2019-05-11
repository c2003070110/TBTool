package com.walk_nie.taobao;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Maps;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;


public class TaobaoFahuo {
	String dtlUrl = "https://wuliu.taobao.com/user/consign.htm?trade_id=";

	private File logFile = null;
	public static void main(String[] args) throws IOException {
		TaobaoFahuo main = new TaobaoFahuo();
		//WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		//main.init();
		//main.process(driver);
		main.execute();
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
		
		TaobaoUtil.login(driver);
		
		List<WebElement> eles0 = driver.findElements(By.cssSelector("div[class=\"ks-contentbox\"]"));
		for (WebElement we : eles0) {
			String txt = we.getText().trim();
			if(txt.indexOf("新功能！拆单发货") != -1){
				List<WebElement> eles1 = we.findElements(By.tagName("a"));	
				for (WebElement we1 : eles1) {
					txt = we1.getText().trim();
					if(txt.indexOf("关闭") != -1){
						we1.click();
						break;
					}
				}
				break;
			}
		}
 
		WebElement weRoot = driver.findElement(By.cssSelector("form[id=\"orderForm\"]"));
		WebElement weRoot1 = weRoot.findElement(By.cssSelector("div[id=\"step3\"]"));
		
		List<WebElement> eles = weRoot1.findElements(By.cssSelector("ul[class=\"tab-selector\"]"));
		if (eles.isEmpty()) {
			return;
		}
		WebElement tabRoot = eles.get(0);
		String trackTraceNo = obj.trackTraceNo;
		if(StringUtil.isBlank(trackTraceNo)){
			//无需物流
			fahuoNoTraceNo(driver,weRoot1, tabRoot);
		}else{
			// 自己联系物流
			fahuoWithTraceNo(driver,weRoot1, tabRoot, trackTraceNo);
		}
		
		updateStatus(obj,"fahuo");
	}

	private void fahuoWithTraceNo(WebDriver driver, WebElement weRoot1,
			WebElement tabRoot, String trackTraceNo) {
		tabRoot.findElement(By.cssSelector("li[id=\"offlineTab\"]")).click();
		WebElement we1 = weRoot1.findElement(By.cssSelector("div[id=\"logis:offline\"]"));
		List<WebElement> eles = we1.findElements(By.tagName("input"));
		for (WebElement we : eles) {
			if (we.getAttribute("id").startsWith("ks-combobox")) {
				we.clear();
				we.sendKeys(trackTraceNo);
				NieUtil.mySleepBySecond(1);
				WebDriverWait wait1 = new WebDriverWait(driver,30);
				wait1.until(new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver driver) {
						try {
							List<WebElement> eles2 = driver.findElements(By.cssSelector("div[class=\"ks-contentbox\"]"));
							for (WebElement we2 : eles2) {
								List<WebElement> eles3 = we2.findElements(By.tagName("div"));	
								for (WebElement we3 : eles3) {
									String id = we3.getAttribute("id");
									if(id.toLowerCase().startsWith("ks-menuitem".toLowerCase())){
										we3.click();
										NieUtil.mySleepBySecond(1);
										try{
											we3.click();
										}catch(Exception e){}
										return Boolean.TRUE;
									}
								}
							}
						} catch (Exception e) {
						}
						return Boolean.FALSE;
					}
				});

				break;
			}
		}
		eles = we1.findElements(By.tagName("button"));
		for (WebElement we : eles) {
			if ("发货".equals(we.getText().trim())) {
				we.click();
				try{
					NieUtil.mySleepBySecond(2);
					we.click();
					NieUtil.mySleepBySecond(2);
					we.click();
				}catch(Exception e){}
				break;
			}
		}
	}
	private void fahuoNoTraceNo(WebDriver driver,WebElement weRoot1, WebElement tabRoot) {

		tabRoot.findElement(By.cssSelector("li[id=\"dummyTab\"]")).click();
		List<WebElement> eles = weRoot1.findElement(
				By.cssSelector("div[id=\"logis:dummy\"]")).findElements(
				By.tagName("button"));
		for (WebElement we : eles) {
			if ("确认".equals(we.getText().trim())) {
				we.click();
				NieUtil.mySleepBySecond(2);
				try{
					we.click();
				}catch(Exception e){}
				break;
			}
		}
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
