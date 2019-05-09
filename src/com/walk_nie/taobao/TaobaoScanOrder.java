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
	String soldItemListUrl = "https://XXXX/";

	public static void main(String[] args) throws IOException {
		TaobaoScanOrder anor = new TaobaoScanOrder();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		anor.process(driver);
	}
	
	public List<OrderInfoObject> process(WebDriver driver) {
		List<OrderInfoObject> orderDtlList = Lists.newArrayList();
		logon(driver);
		
		if(!driver.getCurrentUrl().equals(soldItemListUrl)){
			driver.get(soldItemListUrl);
		}

		// TODO 
		List<WebElement> wes = null;
		for(WebElement we :wes){
			String orderStatus = "";
			if(!"XXX".equals(orderStatus))continue;

			OrderInfoObject orderInfoObj = new OrderInfoObject();
			orderInfoObj.orderObject.orderNo = "";
			orderInfoObj.orderObject.buyerName = "";
			
			orderInfoObj.orderObject.orderCreatedTime = "";
			
			orderInfoObj.orderObject.acturalPayAmt = "";
			
			orderInfoObj.orderDetailUrl ="";
			
			List<WebElement> wes1 = null;
			for (WebElement we1 : wes1) {
				String orderStatusDtl = "";
				if(!"".equals(orderStatusDtl))continue;
				OrderDetailObject dtl = new OrderDetailObject();
				dtl.baobeiTitle = "";
				dtl.sku = "";
				orderInfoObj.orderDtlList.add(dtl);
			}
			orderDtlList.add(orderInfoObj);
		}
		for(OrderInfoObject orderInfoObj :orderDtlList){
			driver.get(orderInfoObj.orderDetailUrl);
			
			orderInfoObj.orderObject.buyerNote = "";
			//orderInfoObj.orderObject.recName = "";
			orderInfoObj.orderObject.addressFull = "";
			//orderInfoObj.orderObject.tel = "";
			//orderInfoObj.orderObject.mobile = "";
		}
		return orderDtlList;
	}
	
	private void logon(WebDriver driver) {
		// TODO 
		driver.get(soldItemListUrl);
		try {
			driver.findElement(By.cssSelector("div[id=\"XXX\"]"));
		} catch (Exception e) {
			// already login
			return;
		}
		

		WebElement el1 = driver.findElement(By.cssSelector("div[id=\"pl_unlogin_home_login\"]"));
		WebElement el2 = el1.findElement(By.cssSelector("a[node-type=\"normal_tab\"]"));
		el2.click();
		
		el2 = el1.findElement(By.cssSelector("input[id=\"loginname\"]"));
		el2.clear();
		el2.sendKeys(NieConfig.getConfig("taobao.user.name"));
		
		el2 = el1.findElement(By.cssSelector("input[name=\"password\"]"));
		el2.clear();
		el2.sendKeys(NieConfig.getConfig("taobao.user.password"));
		
		List<WebElement> wes = el1.findElements(By.tagName("span"));
		for(WebElement we :wes){
			if("登录".equals(we.getText())){
				we.click();
				break;
			}
		}

		NieUtil.mySleepBySecond(2);
		
		WebDriverWait wait1 = new WebDriverWait(driver,10);
		wait1 = new WebDriverWait(driver,60);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {

					WebElement el1 = driver.findElement(By.cssSelector("div[id=\"plc_top\"]"));
					List<WebElement> eles = el1.findElements(By.cssSelector("em[class=\"S_txt1\"]"));
					for(WebElement ele:eles){
						String txt = ele.getText();
						if(txt.indexOf("次郎花子") != -1){
							return Boolean.TRUE;
						}
					}
					return Boolean.FALSE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		
		el1 = driver.findElement(By.cssSelector("div[id=\"plc_top\"]"));
		List<WebElement> eles = el1.findElements(By.cssSelector("em[class=\"S_txt1\"]"));
		for(WebElement ele:eles){
			String txt = ele.getText();
			if(txt.indexOf("次郎花子") != -1){
				return;
			}
		}
		
		NieUtil.readLineFromSystemIn("taobao login is finished? ANY KEY For already");
	}

}
