package com.walk_nie.taobao;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.OrderDetailObject;
import com.walk_nie.taobao.object.OrderInfoObject;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;


public class TaobaoScanOrder {
	String soldItemListUrl = "https://trade.taobao.com/trade/itemlist/list_sold_items.htm";
	String dtlUrl = "https://trade.taobao.com/trade/detail/trade_item_detail.htm?bizOrderId=";
	public static void main(String[] args) throws IOException {
		TaobaoScanOrder anor = new TaobaoScanOrder();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		anor.process(driver);
	}
	
	public List<OrderInfoObject> process(WebDriver driver) {
		logon(driver);
		
		if(!driver.getCurrentUrl().equals(soldItemListUrl)){
			driver.get(soldItemListUrl);
		}

		return scanOrder(driver);
	}
	
	private List<OrderInfoObject> scanOrder(WebDriver driver) {

		List<OrderInfoObject> orderDtlList = Lists.newArrayList();
		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"sold_container\"]"));
		List<WebElement> wes = el1.findElements(By.cssSelector("div[class=\"trade-order-main\"]"));

		wes = el1.findElements(By.className("trade-order-main"));
		for(WebElement we :wes){
			List<WebElement> wes1 = we.findElements(By.tagName("table"));
			if(wes1.size() != 2) continue;
			WebElement tb1 = wes1.get(0);
			String orderNoDtString = tb1.findElements(By.tagName("tr")).get(0).findElements(By.tagName("td")).get(0).getText();
			orderNoDtString = orderNoDtString.replaceAll(" ", "");
			int idx = orderNoDtString.indexOf("创建时间:");
			OrderInfoObject orderInfoObj = new OrderInfoObject();
			orderInfoObj.orderObject.orderNo = orderNoDtString.substring("订单号:".length(),idx);
			orderInfoObj.orderObject.orderCreatedTime = orderNoDtString.substring(idx+ "创建时间:".length());
			
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
						sku1 = str;
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

	private void logon(WebDriver driver) {
		driver.get(soldItemListUrl);
		String title = driver.getTitle();
		if("已卖出的宝贝".equals(title)){
			return;
		}

		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"J_StaticForm\"]"));
		
		WebElement el2 = el1.findElement(By.cssSelector("input[id=\"TPL_username_1\"]"));
		el2.clear();
		el2.sendKeys(NieConfig.getConfig("taobao.user.name"));
		
		el2 = el1.findElement(By.cssSelector("input[id=\"TPL_password_1\"]"));
		el2.clear();
		el2.sendKeys(NieConfig.getConfig("taobao.user.password"));
		
		el2 = el1.findElement(By.cssSelector("button[id=\"J_SubmitStatic\"]"));
		el2.click();
		
		WebDriverWait wait1 = new WebDriverWait(driver,10);
		wait1 = new WebDriverWait(driver,60);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					String title = driver.getTitle();
					if("已卖出的宝贝".equals(title)){
						return Boolean.TRUE;
					}
					return Boolean.FALSE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		
		title = driver.getTitle();
		if("已卖出的宝贝".equals(title)){
			return ;
		}
		
		NieUtil.readLineFromSystemIn("taobao login is finished? ANY KEY For already");
	}

}
