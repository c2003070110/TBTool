package com.walk_nie.taobao.montBell.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.By;
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
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieUtil;

public class MontbellAutoOrder {
	protected BufferedReader stdReader = null;
	private String inFileName = "./montbell/order-in.txt";
	private String ooutFileName = "./montbell/order-out.txt";
	private String crFileName = "./montbell/cr.txt";
	
	private String itemSplitter ="#";
	
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
		File tempFile0 = new File(inFileName);
		try {
			orderForJapan(driver, tempFile0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void orderForChina() {
		
		WebDriver driver = logonForChina();
		long updateTime = System.currentTimeMillis();
		File tempFile0 = new File(inFileName);
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

		List<String> crList = Lists.newArrayList();
		File file = new File(crFileName);
		if (!file.exists()) {
			return;
		}
		// id/crBrand/1234 5678 9012 123/04/17/123/name1 name2
		crList = Files.readLines(file, Charset.forName("UTF-8"));
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
		driver.get("https://www.montbell.jp/login/");
		List<WebElement> submitList = driver.findElements(By.tagName("input"));
		for (WebElement we : submitList) {
			if ("text".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("login_user_id".equals(we.getAttribute("name"))) {
					we.sendKeys("niehpjp");
					break;
				}
			}
		}
		for (WebElement we : submitList) {
			if ("password".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("login_user_password".equals(we.getAttribute("name"))) {
					we.sendKeys("mnt12345");
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
					we.sendKeys("niehpjp");
					break;
				}
			}
		}
		for (WebElement we : submitList) {
			if ("password".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("login_user_password".equals(we.getAttribute("name"))) {
					we.sendKeys("mnt12345");
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

		OrderInfo orderInfo = readInOrderInfo(tempFile0);
		logOrderInfo(orderInfo);
		
		try{
			addItemToCard(driver,orderInfo,"JP");
		}catch(Exception ex){
		}
		driver.get("https://webshop.montbell.jp/cart");
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

		OrderInfo orderInfo = readInOrderInfo(tempFile0);
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

		NieUtil.mySleepBySecond(1);
		List<WebElement> weList = null;
		driver.get("https://en.montbell.jp/products/cart/");			
		// 等待 是否打开
		WebDriverWait wait1 = new WebDriverWait(driver,10);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				while (true){
					try {
						driver.findElements(By.cssSelector("img[id=\"pcheck\"]"));
						return true;
					} catch (Exception e) {

					}
				}
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
				while (true){
					try {
						driver.findElements(By.cssSelector("input[name=\"destination_id\"]"));
						return true;
					} catch (Exception e) {

					}
				}
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
				while (true){
					try {
						driver.findElements(By.id("basicInfo"));
						return true;
					} catch (Exception e) {

					}
				}
			}
		});
		WebElement we = driver.findElement(By.id("basicInfo"));
		we.findElement(By.cssSelector("input[name=\"dest_first_name\"")).sendKeys(orderInfo.firstName);
		we.findElement(By.cssSelector("input[name=\"dest_last_name\"")).sendKeys(orderInfo.lastName);
		we.findElement(By.cssSelector("input[name=\"dest_address1\"")).sendKeys(orderInfo.adr1);
		we.findElement(By.cssSelector("input[name=\"dest_address2\"")).sendKeys(orderInfo.adr2);
		we.findElement(By.cssSelector("input[name=\"dest_city\"")).sendKeys(orderInfo.city);
		we.findElement(By.cssSelector("input[name=\"dest_state_name\"")).sendKeys(orderInfo.state);
		we.findElement(By.cssSelector("input[name=\"dest_zip\"")).sendKeys(orderInfo.postcode);
		we.findElement(By.cssSelector("input[name=\"dest_tel\"")).sendKeys(orderInfo.tel);
		
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
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				while (true){
					try {
						driver.findElements(By.id("contents"));
						return true;
					} catch (Exception e) {

					}
				}
			}
		});
		// add credit card
		if(orderInfo.crObj != null){
			fillCreditCard(driver, orderInfo.crObj);
		}
		
		File oFile = new File(ooutFileName);
		String today = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy-MM-dd");
		FileUtils.write(oFile, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, orderInfo.taobaoOrderName + "\n",
				Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, toString(orderInfo.productInfos) + "\n", Charset.forName("UTF-8"),
				true);
		FileUtils.write(oFile, orderInfo.firstName +" " +orderInfo.lastName + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, orderInfo.tel + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, orderInfo.state + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, orderInfo.city + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, orderInfo.adr2 + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, orderInfo.adr1 + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, orderInfo.postcode + "\n", Charset.forName("UTF-8"), true);
		oFile = null;
	}

	private void addItemToCard(WebDriver driver, OrderInfo orderInfo,String type) {

		List<WebElement> weList = null;
		for (ProductInfo p : orderInfo.productInfos) {
			driver.get(("JP".equals(type)?MontBellUtil.productUrlPrefix: MontBellUtil.productUrlPrefix_en) + p.productId);
			String color = p.colorName;
			String sizz = p.sizeName;
			weList = driver.findElements(By.tagName("select"));
			boolean hasError = false;
			try {
				for (WebElement we : weList) {
					if ("sel_size".equalsIgnoreCase(we.getAttribute("name"))) {
						Select dropdown = new Select(we);
						dropdown.selectByVisibleText(sizz);
						break;
					}
				}
			} catch (Exception e) {
				hasError = true;
			}
			if (hasError) {
				System.out.println("[ERROR] cannt select color OR size! selected by manually!");
				mywait("Color OR size Selected realdy? ENTER for realdy!");
				continue;
			}
			hasError = false;
			try {
				for (WebElement we : weList) {
					if ((sizz.toUpperCase() + "_" + color.toUpperCase() + "_num")
							.equalsIgnoreCase(we.getAttribute("name"))) {
						Select dropdown = new Select(we);
						dropdown.selectByValue("1");
						break;
					}
				}
			} catch (Exception e) {
				hasError = true;
			}
			if(hasError){
				System.out.println("[ERROR] cannt select color OR size! selected by manually!");
				mywait("Color OR size Selected realdy? ENTER for realdy!");
				continue;
			}

			weList = driver.findElements(By.tagName("img"));
			for (WebElement we : weList) {
				if ("cart_in".equalsIgnoreCase(we
						.getAttribute("name"))) {
					we.click();
					break;
				}
			}
			NieUtil.mySleepBySecond(1);
		}
	}
	private OrderInfo readInOrderInfo(File tempFile0) throws IOException {
		OrderInfo order =  new OrderInfo();
		List<String> votes = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		int idx = 0;
		order.taobaoOrderName = votes.get(idx++);
		String productInfos = votes.get(idx++);
		String[] pis = productInfos.split(itemSplitter);
		for (String line : pis) {
			ProductInfo pinfo = new ProductInfo();
			String[] pi = line.split(" ");
			String pid = realProductId(pi[0]);
			if (pid.startsWith("MTBL_")) {
				String[] newP = pid.split("-");
				pid = newP[1];
			}
			pinfo.productId = pid;
			String color = "";
			String sizz = "";
			if (pi.length > 1) {
				String[] pii = pi[1].split(";");
				color = realColorName(pii[0]);
				if (pii.length > 1) {
					sizz = realSizeName(pii[1]);
				}
			}
			pinfo.colorName = color;
			pinfo.sizeName = sizz;
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
	private String realProductId(String pid) {
		pid = pid.replace("商家编码", "");
		pid = pid.replace("：", "");
		pid = pid.replace(":", "");
		return pid;
	}
	
	private String realColorName(String str){

		String color = "";
		if (str == null) {
			return color;
		}
		color = str.replace("颜色分类:", "");
		color = color.replace("颜色分类：", "");
		return color.trim();
	}
	private String realSizeName(String str){

		String sizz = "";
		if (str == null) {
			return sizz;
		}
		sizz = str.replace("尺码:", "");
		sizz = sizz.replace("尺码：", "");
		sizz = sizz.replace("鞋码：", "");
		sizz = sizz.replace("鞋码:", "");
		return sizz.trim();
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

	private String toString(List<ProductInfo> productInfos) {
		StringBuffer sb = new StringBuffer();
		for(ProductInfo pi:productInfos){
			sb.append(pi.productId);
			if(!"".equals(pi.colorName)){
				sb.append(":" + pi.colorName);
			}
			if(!"".equals(pi.sizeName)){
				sb.append(":" + pi.sizeName);
			}
			sb.append("#");
		}
		return sb.toString();
	}

	private void logOrderInfo(OrderInfo orderInfo) {
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
		sb.append("[productInfos]" + toString(orderInfo.productInfos) + "\n");
		System.out.println(sb.toString());
	}
	
	class OrderInfo{
		public String taobaoOrderName;
		public List<ProductInfo> productInfos = Lists.newArrayList();
		public String firstName;
		public String lastName;
		public String tel;
		public String state;
		public String city;
		public String adr2;
		public String adr1;
		public String postcode;
		public CrObject crObj;
	}
	class ProductInfo{
		public String productId;
		public String sizeName;
		public String colorName;
		
	}
}
