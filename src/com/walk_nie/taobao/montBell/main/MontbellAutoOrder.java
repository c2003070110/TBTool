package com.walk_nie.taobao.montBell.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.walk_nie.object.CrObject;
import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.TaobaoOrderInfo;
import com.walk_nie.taobao.object.TaobaoOrderProductInfo;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class MontbellAutoOrder {
	protected BufferedReader stdReader = null;
	private String inFileName = "order-in.txt";
	private String ooutFileName ="order-out.txt";
	private String ooutFileNameForShot ="order-out-shot.txt";
	
	private String itemSplitter =",";
	
	private WebDriver driver = null;
	private List<CrObject> crObjList = Lists.newArrayList();


	public MontbellAutoOrder(){
		try {
			readInCrObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void orderForJapan() {
		WebDriver driver = logonForJapan();
		File tempFile0 = new File(MontBellUtil.rootPathName, inFileName);
		try {
			orderForJapan(driver, tempFile0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void orderForChina() {
		
		WebDriver driver = logonForChina();
		long updateTime = System.currentTimeMillis();
		File tempFile0 = new File(MontBellUtil.rootPathName, inFileName);
		System.out.println("[waiting for order info in ]"
				+ tempFile0.getAbsolutePath());
		while (true) {
			if (updateTime < tempFile0.lastModified()) {
				updateTime = tempFile0.lastModified();
				try{
					orderForChina(driver, tempFile0);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				System.out.println("[waiting for order info in ]"
						+ tempFile0.getAbsolutePath());
			}
		}
	}

	private void readInCrObject() throws IOException {

//		List<String> crList = Lists.newArrayList();
//		File file = new File(crFileName);
//		if (!file.exists()) {
//			return;
//		}
		// id/crBrand/1234 5678 9012 123/04/17/123/name1 name2
		String prefix = "montbell.cr.";
		List<String> crList = NieConfig.getConfigByPrefix(prefix);
		boolean hasStore = false;
		for (String str : crList) {
			String[] spl = str.split("/");
			CrObject obj = new CrObject();
			int j = 0;
			obj.id = spl[j++].toLowerCase();
			if (obj.id.equalsIgnoreCase("store")) {
				crObjList.add(obj);
				hasStore = true;
				continue;
			}
			obj.crBrand = spl[j++];
			String[] splTemp = spl[j++].split(" ");
			int i = 0;
			obj.numb1 = splTemp[i++];
			obj.numb2 = splTemp[i++];
			obj.numb3 = splTemp[i++];
			obj.numb4 = splTemp[i++];

			obj.expiredMon = Integer.parseInt(spl[j++]) +"";
			obj.expiredYear = spl[j++];

			obj.scode = spl[j++];

			i = 0;
			splTemp = spl[j++].split(" ");
			obj.meigi1 = splTemp[i++];
			obj.meigi2 = splTemp[i++];

			crObjList.add(obj);
		}
		if (!hasStore) {
			CrObject obj = new CrObject();
			obj.id = "store";
			crObjList.add(0, obj);
		}
	}

	private WebDriver logonForJapan() {
		if (driver != null) {
			driver.get("https://www.montbell.jp/mypage/logout.php");
		} else {
			driver = WebDriverUtil.getFirefoxWebDriver();
		}
		driver.manage().window().setSize(new Dimension(960, 960));
		driver.manage().window().setPosition(new Point(10, 10));
		driver.get("https://www.montbell.jp/login/");
		List<WebElement> submitList = driver.findElements(By.tagName("input"));
		for (WebElement we : submitList) {
			if ("text".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("login_user_id".equals(we.getAttribute("name"))) {
					we.sendKeys(NieConfig.getConfig("montbell.user.id"));
					break;
				}
			}
		}
		for (WebElement we : submitList) {
			if ("password".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("login_user_password".equals(we.getAttribute("name"))) {
				
					we.sendKeys(NieConfig.getConfig("montbell.user.password"));
					break;
				}
			}
		}
		submitList = driver.findElements(By.tagName("a"));
		for (WebElement we : submitList) {
			if ("javascript:goLoginWeb();".equalsIgnoreCase(we.getAttribute("href"))) {
				we.click();
				break;
			}
		}
		return driver;
	}

	private WebDriver logonForChina() {
		if (driver != null) {
			driver.get("https://en.montbell.jp/login/logout.php");
		} else {
			driver = WebDriverUtil.getFirefoxWebDriver();
		}
		
		driver.get("https://en.montbell.jp/login/");
		driver.manage().window().setSize(new Dimension(960, 960));
		driver.manage().window().setPosition(new Point(10, 10));

		List<WebElement> submitList = driver.findElements(By.tagName("input"));
		for (WebElement we : submitList) {
			if ("radio".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("1".equals(we.getAttribute("value"))) {
					we.click();
					break;
				}
			}
		}
		submitList = driver.findElements(By.tagName("input"));
		for (WebElement we : submitList) {
			if ("text".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("login_user_id".equals(we.getAttribute("name"))) {
					we.sendKeys(NieConfig.getConfig("montbell.user.id"));
					break;
				}
			}
		}
		for (WebElement we : submitList) {
			if ("password".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("login_user_password".equals(we.getAttribute("name"))) {
					we.sendKeys(NieConfig.getConfig("montbell.user.password"));
					break;
				}
			}
		}
		submitList = driver.findElements(By.tagName("input"));
		for (WebElement we : submitList) {
			if ("image".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("Continue".equals(we.getAttribute("alt"))) {
					we.click();
					break;
				}
			}
		}
		driver.get("http://en.montbell.jp/products/");
		submitList = driver.findElements(By.tagName("select"));
		for (WebElement we : submitList) {
			if ("site_shipping_country_code".equalsIgnoreCase(we
					.getAttribute("name"))) {
				Select dropdown = new Select(we);
				dropdown.selectByValue("156");
			}
		}

		return driver;
	}

	protected void orderForJapan(WebDriver driver, File tempFile0) throws IOException {

		TaobaoOrderInfo orderInfo = readInOrderInfo(tempFile0);
		logOrderInfo(orderInfo);
		
		try{
			addItemToCard(driver,orderInfo,"JP");
		}catch(Exception ex){
		}

//		driver.get("https://webshop.montbell.jp/cart");
//		File saveToShotF = new File(MontBellUtil.rootPathName + "/orderShot", DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss")
//				+ orderInfo.taobaoOrderName + ".jpg");
//		// take screenshot
//		WebDriverUtil.screenShot(driver, saveToShotF.getCanonicalPath());
		
		List<WebElement> weList = null;
		weList = driver.findElements(By.tagName("input[type=\"image\"]"));
		for (WebElement we : weList) {
			if ("レジに進む".equalsIgnoreCase(we
					.getAttribute("alt"))) {
				we.click();
				break;
			}
		}

		if ("store".equals(orderInfo.crObj.id.toLowerCase())) {
			// next button
			weList = driver.findElements(By
					.cssSelector("input[name=\"next_shop\"]"));
			for (WebElement we : weList) {
				if ("next_destination"
						.equalsIgnoreCase(we.getAttribute("name"))) {
					we.click();
					break;
				}
			}
			
			WebElement we = driver.findElement(By
					.cssSelector("select[name=\"shop_prefecture_id\"]"));
			Select dropdown = new Select(we);
			dropdown.selectByValue("13");// 東京都
			we = driver.findElement(By.cssSelector("img[alt=\"受取店舗入力\"]"));
			we.click();

			NieUtil.mySleepBySecond(2);
			we = driver.findElement(By.cssSelector("a[title=\"モンベル 御徒町店を選択する\"]"));
			we.click();

			we = driver.findElement(By.cssSelector("input[alt=\"次へ\"]"));
			we.click();

			we = driver.findElement(By.cssSelector("input[id=\"contact_way_id_1\"]"));
			we.click();

			we = driver.findElement(By.cssSelector("input[alt=\"確認画面へ\"]"));
			we.click();
		} else {
			// 上記「連絡先」と同じ住所
			weList = driver.findElements(By
					.cssSelector("input[type=\"radio\"]"));
			for (WebElement we : weList) {
				if ("radio".equalsIgnoreCase(we.getAttribute("type"))
						&& "destination_id_1".equalsIgnoreCase(we
								.getAttribute("id"))) {
					we.click();
					break;
				}
			}
			// next button
			WebElement we = driver.findElement(By
					.cssSelector("input[name=\"next_destination\"]"));
			we.click();

			we = driver.findElement(By.cssSelector("input[alt=\"次へ\"]"));
			we.click();
			
			// 支払方法入力
			we = driver.findElement(By.cssSelector("input[id=\"payment_type_id_1\"]"));
			we.click();
			
			if(orderInfo.crObj != null){
				fillCreditCard(driver, orderInfo.crObj);
			}
		}
	}

	protected void orderForChina(WebDriver driver, File tempFile0) throws IOException{

		TaobaoOrderInfo orderInfo = readInOrderInfo(tempFile0);
		logOrderInfo(orderInfo);
		
		if("".equals(orderInfo.postcode)){
			System.out.println("[ERROR] Order Info NO Correct! PostCode IS NULL! File=" + tempFile0.getAbsolutePath());
			return;
		}

		try{
			addItemToCard(driver,orderInfo,"CN");
		}catch(Exception ex){
			System.out.println("[ERROR] cannt select color OR size! selected by manually!");
			mywait("Color OR size Selected realdy? ENTER for realdy!");
		}
		driver.get("https://en.montbell.jp/products/cart/");
		File saveToShotF = new File(MontBellUtil.rootPathName + "/orderShot", DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss")
				+ orderInfo.taobaoOrderName + ".jpg");
		// take screenshot
		WebDriverUtil.screenShot(driver, saveToShotF.getCanonicalPath());

		NieUtil.mySleepBySecond(1);
		List<WebElement> weList = null;
		
		// 等待 是否打开
		WebDriverWait wait1 = new WebDriverWait(driver,10);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElements(By.cssSelector("img[id=\"pcheck\"]"));
					return true;
				} catch (Exception e) {

				}
				return false;
			}
		});
		weList = driver.findElements(By.cssSelector("img[id=\"pcheck\"]"));
		for (WebElement we : weList) {
			if ("PROCEED TO CHECKOUT".equalsIgnoreCase(we
					.getAttribute("alt"))) {
				we.click();
				break;
			}
		}
		weList = driver.findElement(By.id("confirmButtons")).findElements(By.tagName("a"));
		for (WebElement we : weList) {
			if ("YES".equalsIgnoreCase(we.getText())) {
				we.click();
				break;
			}
		}	
		NieUtil.mySleepBySecond(1);
		// 等待 是否打开
		wait1 = new WebDriverWait(driver,10);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElements(By
							.cssSelector("input[name=\"destination_id\"]"));
					return true;
				} catch (Exception e) {
				}
				return false;
			}
		});
		weList = driver.findElements(By.cssSelector("input[name=\"destination_id\"]"));
		for (WebElement we : weList) {
			if ("radio".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("3".equalsIgnoreCase(we.getAttribute("value"))) {
					we.click();
					break;
				}
			}
		}
		weList = driver.findElements(By.cssSelector("input[id=\"btncheck\"]"));
		for (WebElement we : weList) {
			if ("image".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("next_dest".equalsIgnoreCase(we.getAttribute("name"))) {
					we.click();
					break;
				}
			}
		}
		NieUtil.mySleepBySecond(1);
		driver.switchTo().alert().accept();

		NieUtil.mySleepBySecond(1);
		// 等待 是否打开
		wait1 = new WebDriverWait(driver,10);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElements(By.id("basicInfo"));
					return true;
				} catch (Exception e) {
				}
				return false;
			}
		});
		WebElement we = driver.findElement(By.id("basicInfo"));
		WebElement actWe = we.findElement(By.cssSelector("input[name=\"dest_first_name\""));
		actWe.clear();
		actWe.sendKeys(orderInfo.firstName);
		actWe = we.findElement(By.cssSelector("input[name=\"dest_last_name\""));
		actWe.clear();
		actWe.sendKeys(orderInfo.lastName);
		actWe = we.findElement(By.cssSelector("input[name=\"dest_address1\""));
		actWe.clear();
		actWe.sendKeys(orderInfo.adr1);
		actWe = we.findElement(By.cssSelector("input[name=\"dest_address2\""));
		actWe.clear();
		actWe.sendKeys(orderInfo.adr2);
		actWe = we.findElement(By.cssSelector("input[name=\"dest_city\""));
		actWe.clear();
		actWe.sendKeys(orderInfo.city);
		actWe = we.findElement(By.cssSelector("input[name=\"dest_state_name\""));
		actWe.clear();
		actWe.sendKeys(orderInfo.state);
		actWe = we.findElement(By.cssSelector("input[name=\"dest_zip\""));
		actWe.clear();
		actWe.sendKeys(orderInfo.postcode);
		actWe = we.findElement(By.cssSelector("input[name=\"dest_tel\""));
		actWe.clear();
		actWe.sendKeys(orderInfo.tel);
		
		weList = driver.findElements(By.tagName("input"));
		for (WebElement we1 : weList) {
			if ("image".equalsIgnoreCase(we1.getAttribute("type"))) {
				if ("btn_next".equalsIgnoreCase(we1.getAttribute("name"))) {
					we1.click();
					break;
				}
			}
		}
		NieUtil.mySleepBySecond(1);
		// 等待 是否打开
		wait1 = new WebDriverWait(driver,10);
		wait1.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElements(By.id("contents"));
					return true;
				} catch (Exception e) {

				}
				return false;
			}
		});
		// add credit card
		if(orderInfo.crObj != null){
			fillCreditCard(driver, orderInfo.crObj);
		}
		
		File oFile = new File(MontBellUtil.rootPathName,ooutFileName);
		List<String> lines = Lists.newArrayList();
		lines.add(orderInfo.taobaoOrderName);
		lines.add(toProductInfoString(orderInfo.productInfos));
		lines.add(orderInfo.firstName +" " +orderInfo.lastName);
		lines.add(orderInfo.tel);
		lines.add(orderInfo.state);
		lines.add(orderInfo.city);
		lines.add(orderInfo.adr2);
		lines.add(orderInfo.adr1);
		lines.add(orderInfo.postcode);
		NieUtil.appendToFile(oFile, lines);
		
		File oFileS = new File(MontBellUtil.rootPathName,ooutFileNameForShot);
		lines = Lists.newArrayList();
		String yyyyMMdd = DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss");
		lines.add(String.format("%s\t%s\t%s",yyyyMMdd, orderInfo.taobaoOrderName, toProductInfoString(orderInfo.productInfos)));
		NieUtil.appendToFile(oFileS, lines);
	}

	private void addItemToCard(WebDriver driver, TaobaoOrderInfo orderInfo,String type) {

		clearShoppingCart(driver, type);
		for (TaobaoOrderProductInfo p : orderInfo.productInfos) {
			boolean addCardResult = false;
			try {
				String url = "JP".equals(type) ? MontBellUtil.productUrlPrefix
						: MontBellUtil.productUrlPrefix_en;
				url = url + p.productId;
				driver.get(url);
				addCardResult = addItemToCardNrst(driver, p);
			} catch (Exception e) {
				e.printStackTrace();
				addCardResult = false;
			}
			if(!addCardResult){
				String url = "JP".equals(type) ? MontBellUtil.productUrlPrefix_fo
						: MontBellUtil.productUrlPrefix_en_fo;
				url = url + p.productId;
				driver.get(url);
				addItemToCardNrst(driver, p);
			}
			
			List<WebElement> weList = driver.findElements(By.tagName("img"));
			for (WebElement we : weList) {
				if ("cart_in".equalsIgnoreCase(we
						.getAttribute("name"))) {
					we.click();
					break;
				}
			}
		}
	}

	private boolean addItemToCardNrst(WebDriver driver, TaobaoOrderProductInfo p) {

		boolean reslt = false;
		List<WebElement> weList = null;
		String color = p.colorName;
		String sizz = p.sizeName;
		weList = driver.findElements(By.tagName("select"));
		boolean nosizzFlag = sizz.equals("-") || sizz.equals("－") || sizz.equals("选我没错");
		boolean nocolrFlag = color.equals("-") || color.equals("－") || color.equals("选我没错");
		String selKey = "";
		if (!nosizzFlag) {
			selKey = sizz;
		}
		selKey += "_";
		if (!nocolrFlag) {
			selKey += color;
		}
		selKey += "_";
		selKey += "num";
		if (nosizzFlag) {
			// NONE size choice
			for (WebElement we : weList) {
				if (selKey.equalsIgnoreCase(we.getAttribute("name"))) {
					Select dropdown = new Select(we);
					dropdown.selectByValue(p.qtty);
					reslt = true;
					break;
				}
			}
		} else {
			for (WebElement we : weList) {
				if ("sel_size".equalsIgnoreCase(we.getAttribute("name"))) {
					Select dropdown = new Select(we);
					dropdown.selectByVisibleText(sizz);
					break;
				}
			}
			for (WebElement we : weList) {
				if (selKey.equalsIgnoreCase(we.getAttribute("name"))) {
					Select dropdown = new Select(we);
					dropdown.selectByValue(p.qtty);
					reslt = true;
					break;
				}
			}
		}
		NieUtil.mySleepBySecond(2);
		return reslt;
	}
	private TaobaoOrderInfo readInOrderInfo(File tempFile0) throws IOException {
		TaobaoOrderInfo order =  new TaobaoOrderInfo();
		List<String> votes = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		int idx = 0;
		order.taobaoOrderName = votes.get(idx++);
		String productInfos = votes.get(idx++);
		String[] pis = productInfos.split(itemSplitter);
		for (String line : pis) {
			TaobaoOrderProductInfo pinfo = MontBellUtil.readTaobaoProductInfo(line);
			
			order.productInfos.add(pinfo);
		}
		String next = votes.get(idx++);
		if("store".equalsIgnoreCase(next) || "".equals(getCrBrand(next))){
			// order for japan
			order.crObj = getCrObject(next);
			return order;
		}
		String nameEn = removeEndComma(next);
		String[] names = nameEn.split(" ");
		order.firstName = names[1];
		order.lastName = names[0];
		order.tel = removeEndComma(votes.get(idx++));
		String state = removeEndComma(votes.get(idx++));
		state = state.replaceAll("Sheng", "");
		state = state.replaceAll("Shi", "");
		order.state = state.replaceAll(" ", "");
		order.city = removeEndComma(votes.get(idx++));
		order.adr2 = removeEndComma(votes.get(idx++));
		order.adr1 = removeEndComma(votes.get(idx++));
		order.postcode = removeEndComma(votes.get(idx++));
		order.crObj = getCrObject(removeEndComma(votes.get(idx++)));
		return order;
	}

	private CrObject getCrObject(String crId) {
		if(crObjList.isEmpty()){
			return null;
		}
		if (StringUtils.isNotEmpty(crId)) {
			for (CrObject o : crObjList) {
				if (o.id.equals(crId)) {
					return o;
				}
			}
		}
		return null;
	}

	private void fillCreditCard(WebDriver driver, CrObject crObj) {
		WebElement c = driver.findElement(By.id("contents"));

		List<WebElement> weList = null;
		weList = c.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			if ("card_type_id".equalsIgnoreCase(we.getAttribute("name"))) {
				Select dropdown = new Select(we);
				dropdown.selectByValue(getCrBrand(crObj.crBrand));
				break;
			}
		}
		try {
			c.findElement(By.cssSelector("input[name=\"card_number1\"")).sendKeys(crObj.numb1);
			c.findElement(By.cssSelector("input[name=\"card_number2\"")).sendKeys(crObj.numb2);
			c.findElement(By.cssSelector("input[name=\"card_number3\"")).sendKeys(crObj.numb3);
			c.findElement(By.cssSelector("input[name=\"card_number4\"")).sendKeys(crObj.numb4);
		} catch (Exception e) {
			c.findElement(By.cssSelector("input[name=\"card_number\""))
					.sendKeys(crObj.numb1 + crObj.numb2 + crObj.numb3 + crObj.numb4);
		}

		weList = c.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			if ("card_expire_month".equalsIgnoreCase(we.getAttribute("name"))) {
				Select dropdown = new Select(we);
				try {
					dropdown.selectByVisibleText(crObj.expiredMon);
				} catch (Exception e) {
					dropdown.selectByVisibleText("0" + crObj.expiredMon);
				}
				break;
			}
		}
		for (WebElement we : weList) {
			if ("card_expire_year".equalsIgnoreCase(we.getAttribute("name"))) {
				Select dropdown = new Select(we);
				dropdown.selectByVisibleText(crObj.expiredYear);
				break;
			}
		}
		c.findElement(By.cssSelector("input[name=\"security_code\"")).sendKeys(crObj.scode);
	}

	private String getCrBrand(String crBrand) {
		return creaditCardBrandMap.get(crBrand.toLowerCase());
	}
	private Map<String,String> creaditCardBrandMap = Maps.newHashMap();
	{
		creaditCardBrandMap.put("visa", "1");
		creaditCardBrandMap.put("jcb", "2");
		creaditCardBrandMap.put("master", "5");
		creaditCardBrandMap.put("amex", "8");
		creaditCardBrandMap.put("diners", "9");
	}


	public BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}
	public  boolean mywait(String hint) {
		while (true) {
			System.out.print(hint);
			String line;
			try {
				line = getStdReader().readLine().trim();
				if ("\r\n".equalsIgnoreCase(line)
						|| "\n".equalsIgnoreCase(line) || "".equals(line)) {
					return true;
				}else if ("n".equalsIgnoreCase(line)){
					return false;
				}
			} catch (IOException e) {
			}
		}
	}

	private String removeEndComma(String str) {
		if(str == null){
			return "";
		}
		if(str.endsWith(",")){
			return str.substring(0,str.length()-1);
		}
		return str;
	}

	private String toProductInfoString(List<TaobaoOrderProductInfo> productInfos) {
		StringBuffer sb = new StringBuffer();
		for(TaobaoOrderProductInfo pi:productInfos){
			sb.append("商家编码：MTBL_XX-" + pi.productId);
			sb.append(" ");
			if(!"".equals(pi.colorName)){
				sb.append("颜色分类:" + pi.colorName);
				sb.append(";");
			}
			if(!"".equals(pi.sizeName)){
				sb.append("尺码:" + pi.sizeName);
				sb.append(";");
			}
			if(!"".equals(pi.qtty)){
				sb.append("数量：" + pi.qtty);
			}
			sb.append(itemSplitter);
		}
		return sb.toString();
	}

	private void logOrderInfo(TaobaoOrderInfo orderInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("[taobaoOrderName]" + orderInfo.taobaoOrderName + "\n");
		sb.append("[firstName]" + orderInfo.firstName + "\n");
		sb.append("[lastName]" + orderInfo.lastName + "\n");
		sb.append("[tel]" + orderInfo.tel + "\n");
		sb.append("[state]" + orderInfo.state + "\n");
		sb.append("[city]" + orderInfo.city + "\n");
		sb.append("[adr2]" + orderInfo.adr2 + "\n");
		sb.append("[adr1]" + orderInfo.adr1 + "\n");
		sb.append("[postcode]" + orderInfo.postcode + "\n");
		sb.append("[crId]" + orderInfo.crObj.crBrand + "\n");
		sb.append("[productInfos]" + toProductInfoString(orderInfo.productInfos) + "\n");
		System.out.println(sb.toString());
	}

	public void screenShotShoppingCart() throws IOException {
		WebDriver driver = logonForJapan();
		String ordersFile = NieConfig.getConfig("montbell.order.screenshot.file");
		List<String> orderLines = Files.readLines(new File(MontBellUtil.rootPathName, ordersFile),
				Charset.forName("UTF-8"));

		for (String line : orderLines) {
			String[] splits = line.split("\t");
			TaobaoOrderInfo orderInfo = new TaobaoOrderInfo();
			String yyyyMMdd = splits[0];
			orderInfo.taobaoOrderName = splits[1];
			String[] pis = splits[2].split(itemSplitter);
			for (String pi : pis) {
				TaobaoOrderProductInfo pinfo = MontBellUtil.readTaobaoProductInfo(pi);
				orderInfo.productInfos.add(pinfo);
			}
			// add to card
			addItemToCard(driver, orderInfo, "JP");

			File saveToShotF = new File(MontBellUtil.rootPathName + "/orderShot",
					yyyyMMdd + orderInfo.taobaoOrderName + ".jpg");
			// take screenshot
			WebDriverUtil.screenShot(driver, saveToShotF.getCanonicalPath());

			// remove from cart
			clearShoppingCart(driver, "JP");
		}
	}
	private void clearShoppingCart(WebDriver driver,String type) {
		driver.get(("JP".equals(type) ? "https://webshop.montbell.jp/cart" : "https://en.montbell.jp/products/cart/"));
		while (true) {
			try {
				List<WebElement> weList = "JP".equals(type)  
						? (driver.findElements(By.cssSelector("input[Alt=\"削除\"]")))
						: (driver.findElements(By.cssSelector("input[Alt=\"Remove\"]")));
				if(weList == null || weList.isEmpty()){
					break;
				}
				if (weList != null && !weList.isEmpty()) {
					weList.get(0).click();
				}
			} catch (Exception e) {
				break;
			}
		}
	}
}
