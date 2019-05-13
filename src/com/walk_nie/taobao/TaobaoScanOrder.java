package com.walk_nie.taobao;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.seleniumhq.jetty9.util.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.walk_nie.taobao.object.OrderDetailObject;
import com.walk_nie.taobao.object.OrderInfoObject;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;


public class TaobaoScanOrder {
	private File logFile = null;
	
	String soldItemListUrl = "https://trade.taobao.com/trade/itemlist/list_sold_items.htm";
	String dtlUrl = "https://trade.taobao.com/trade/detail/trade_item_detail.htm?bizOrderId=";
	public static void main(String[] args) throws IOException {
		TaobaoScanOrder anor = new TaobaoScanOrder();
		//WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		//anor.processForWebService(driver);
		
		anor.init();
		anor.execute();
	}
	
	public void execute() throws IOException {
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		init();
		int interval = Integer.parseInt(NieConfig.getConfig("taobao.deamon.interval"));// second
		while (true) {
			try {
				long t1 = System.currentTimeMillis();
				processForWebService(driver);
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
	public void processForWebService(WebDriver driver) throws UnsupportedOperationException, IOException {
		List<OrderInfoObject> list = process(driver);
		addTBOrderByWebService(list);
	}

	private void addTBOrderByWebService(List<OrderInfoObject> orders)
			throws UnsupportedOperationException, IOException {

		for (OrderInfoObject order : orders) {

			Map<String, String> param = Maps.newHashMap();
			param.put("action", "addTaobaoOrder");
			param.put("orderNo", order.orderObject.orderNo);
			param.put("orderCreatedTime", order.orderObject.orderCreatedTime);
			param.put("buyerName", order.orderObject.buyerName);
			param.put("buyerNote", order.orderObject.buyerNote);
			param.put("addressFull", order.orderObject.addressFull);
			NieUtil.log(logFile, "[INFO][Service:addTaobaoOrder][Param]"
					+ "[orderNo]" + order.orderObject.orderNo + "[buyerName]" + order.orderObject.buyerName);

			String rslt = NieUtil.httpGet(
					NieConfig.getConfig("taobao.service.url"), param);

			NieUtil.log(logFile, "[INFO][Service:addTaobaoOrder][RESULT]"
					+ rslt);
			if(rslt.indexOf("ERROR") != -1 || rslt.indexOf("Fatal") != -1) continue;
			for (OrderDetailObject dtl : order.orderDtlList) {
				param = Maps.newHashMap();
				param.put("action", "addTaobaoOrderDetail");
				param.put("orderNo", order.orderObject.orderNo);
				param.put("baobeiTitle", dtl.baobeiTitle);
				param.put("sku", dtl.sku);
				
				NieUtil.log(logFile, "[INFO][Service:addTaobaoOrderDetail][Param]"
						+ "[orderNo]" + order.orderObject.orderNo + "[buyerName]" + order.orderObject.buyerName);

				 rslt = NieUtil.httpGet(
						NieConfig.getConfig("taobao.service.url"), param);

				NieUtil.log(logFile, "[INFO][Service:addTaobaoOrderDetail][RESULT]"
						+ rslt);
			}
		}
	}
	public List<OrderInfoObject> process(WebDriver driver) throws UnsupportedOperationException, IOException {
	
		driver.get(soldItemListUrl);
		TaobaoUtil.login(driver);

		return scanOrder(driver);
	}
	
	private List<OrderInfoObject> scanOrder(WebDriver driver) throws UnsupportedOperationException, IOException {

		List<OrderInfoObject> orderDtlList = Lists.newArrayList();
		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"sold_container\"]"));
		List<WebElement> wes = el1.findElements(By.cssSelector("div[class=\"trade-order-main\"]"));

		wes = el1.findElements(By.className("trade-order-main"));
		for(WebElement we :wes){
			List<WebElement> wes1 = we.findElements(By.tagName("table"));
			if(wes1.size() != 2) continue;
			WebElement tb1 = wes1.get(0);
			String orderNoDtString = tb1.findElements(By.tagName("tr")).get(0).findElements(By.tagName("td")).get(0).getText();
			//orderNoDtString = orderNoDtString.replaceAll(" ", "");
			int idx = orderNoDtString.indexOf("创建时间:");
			OrderInfoObject orderInfoObj = new OrderInfoObject();
			orderInfoObj.orderObject.orderNo = orderNoDtString.substring("订单号:".length(),idx).replaceAll(" ", "");
			
			if(hasRegisted(orderInfoObj.orderObject.orderNo)) continue;
			
			orderInfoObj.orderObject.orderCreatedTime = orderNoDtString.substring(idx+ "创建时间:".length()).trim();
			
			WebElement tb2 = wes1.get(1);
			List<WebElement> wes2 = tb2.findElements(By.tagName("tr"));
			
			WebElement firstTr = wes2.get(0);
			String orderStatus = firstTr.findElements(By.tagName("td")).get(5).getText();
			if(orderStatus.indexOf("买家已付款") == -1)continue;
			orderInfoObj.orderObject.buyerName = firstTr.findElements(By.tagName("td")).get(4).getText();
			
			orderInfoObj.orderObject.acturalPayAmt = "";
			
			for(WebElement we2 :wes2){
				List<WebElement> westd = we2.findElements(By.tagName("td"));
				String orderStatusDtl = westd.get(3).getText();
				if(!"".equals(orderStatusDtl))continue;

				OrderDetailObject dtl = new OrderDetailObject();
				List<WebElement> wesp = westd.get(0).findElements(By.tagName("p"));
				if(wesp.size() > 0){
					dtl.baobeiTitle = wesp.get(0).getText();
				}
				String sku1 = "",sku2 = "";
				for(WebElement wep :wesp){
					String str = wep.getText();
					if(str.startsWith("颜色分类")){
						int pos = str.indexOf("尺码");
						if(pos != -1){
							sku1 = str.substring(0,pos) + " " + str.substring(pos);
						}else{
							sku1 = str;
						}
					}
					if(str.startsWith("商家编码")){
						sku2 = str;
					}
				}
				dtl.sku = sku2 + " " + sku1;
				orderInfoObj.orderDtlList.add(dtl);
			}
			orderInfoObj.orderDetailUrl =dtlUrl + orderInfoObj.orderObject.orderNo;
			
			orderDtlList.add(orderInfoObj);
		}
		
		scanForOrderDetail(driver,orderDtlList);
		return orderDtlList;
	}
	private boolean hasRegisted(String orderNo)
			throws UnsupportedOperationException, IOException {

		Map<String, String> param = Maps.newHashMap();
		param.put("action", "isRegistedOrderNo");
		param.put("orderNo", orderNo);
		String rslt = NieUtil.httpGet(NieConfig.getConfig("taobao.service.url"), param);
		if (!StringUtil.isBlank(rslt)) {
			NieUtil.log(logFile, "[INFO][Service:isRegistedOrderNo][RESULT]" + rslt);
			return new Boolean(rslt).booleanValue();
		}
		return false;
	}

	private void scanForOrderDetail(WebDriver driver,
			List<OrderInfoObject> orderDtlList) {

		for (OrderInfoObject orderInfoObj : orderDtlList) {
			driver.get(orderInfoObj.orderDetailUrl);

			WebElement el1 = driver.findElement(By
					.cssSelector("div[id=\"detail-panel\"]"));

			List<WebElement> wesDiv = el1.findElements(By.tagName("div"));
			for (WebElement wediv : wesDiv) {
				String className = wediv.getAttribute("class");
				if (className.startsWith("address-memo-mod__address-note")) {
					String txt = wediv.getText();
					txt = txt.replaceAll("买家留言：", "");
					orderInfoObj.orderObject.buyerNote = txt;
					break;
				}
			}

			List<WebElement> wesa = el1.findElements(By.tagName("a"));
			for (WebElement wea : wesa) {
				String txt = wea.getText();
				if (txt.indexOf("收货和物流信息") != -1) {
					wea.click();
					break;
				}
			}
			wesDiv = el1.findElements(By.tagName("div"));
			for (WebElement wediv : wesDiv) {
				String className = wediv.getAttribute("class");
				if (className.startsWith("logistics-panel-mod__line-info")) {
					String txt = wediv.getText();
					txt = txt.replaceAll("收货地址：", "");
					txt = txt.replaceAll(" ", "");
					orderInfoObj.orderObject.addressFull = txt;
					break;
				}
			}
		}
	}

	public void init() throws IOException {

		logFile = new File(NieConfig.getConfig("taobao.log.file"));

		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
	}
	

}
