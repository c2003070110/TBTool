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
import org.openqa.selenium.support.ui.Select;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.walk_nie.object.CrObject;
import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.util.WebDriverUtil;

public class MontbellAutoOrder {
	protected BufferedReader stdReader = null;
	private String inFileNameJP = "./montbell/order-in-japan.txt";
	private String inFileNameCN = "./montbell/order-in-china.txt";
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
		File tempFile0 = new File(inFileNameJP);
		try {
			orderForJapan(driver, tempFile0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void orderForChina() {
		
		WebDriver driver = logonForChina();
		long updateTime = System.currentTimeMillis();
		File tempFile0 = new File(inFileNameCN);
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
		for(String str:crList){
			String[] spl = str.split("/");
			CrObject obj = new CrObject();
			int j=0;
			obj.id = spl[j++];
			obj.crBrand = spl[j++];
			String[] splTemp = spl[j++].split(" ");
			int i=0;
			obj.numb1 = splTemp[i++];
			obj.numb2 = splTemp[i++];
			obj.numb3 = splTemp[i++];
			obj.numb4 = splTemp[i++];
			
			obj.expiredMon = spl[j++];
			obj.expiredYear = spl[j++];

			obj.scode = spl[j++];

			i=0;
			splTemp = spl[j++].split(" ");
			obj.meigi1 = splTemp[i++];
			obj.meigi2 = splTemp[i++];
			
			crObjList.add(obj);
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
		List<String> orders = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		int idx = 0;
		//String taobaoOrderName = orders.get(idx++);
		String productInfos = orders.get(idx++);
		String crId = removeEndComma(orders.get(idx++));
		try{
			addItemToCard(driver,productInfos,"JP");
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

		if ("store".equals(crId.toLowerCase())) {
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
			try {
				Thread.sleep(1000 * 2);
			} catch (InterruptedException e) {
			}
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
			
			addCreditCard(driver, crId);
		}
	}

	protected void orderForChina(WebDriver driver, File tempFile0) throws IOException{

		List<String> votes = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		int idx = 0;
		String taobaoOrderName = votes.get(idx++);
		String productInfos = votes.get(idx++);
		String nameEn = removeEndComma(votes.get(idx++));
		String[] names = nameEn.split(" ");
		String tel = removeEndComma(votes.get(idx++));
		String state = removeEndComma(votes.get(idx++));
		state = state.replaceAll("Sheng", "");
		state = state.replaceAll("Shi", "");
		state = state.replaceAll(" ", "");
		String city = removeEndComma(votes.get(idx++));
		String adr2 = removeEndComma(votes.get(idx++));
		String adr1 = removeEndComma(votes.get(idx++));
		String postcode = removeEndComma(votes.get(idx++));
		String crId = removeEndComma(votes.get(idx++));
		
		if("".equals(postcode)){
			System.out.println("[ERROR] Order Info NO Correct! PostCode IS NULL! File=" + tempFile0.getAbsolutePath());
			return;
		}

		try{
			addItemToCard(driver,productInfos,"CN");
		}catch(Exception ex){
			System.out.println("[ERROR] cannt select color OR size! selected by manually!");
			mywait("Color OR size Selected realdy? ENTER for realdy!");
		}

		List<WebElement> weList = null;
		driver.get("https://en.montbell.jp/products/cart/");
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
		driver.switchTo().alert().accept();
		try {
			Thread.sleep(1000*1);
		} catch (InterruptedException e) {
		}
		WebElement we = driver.findElement(By.id("basicInfo"));
		we.findElement(By.cssSelector("input[name=\"dest_first_name\"")).sendKeys(names[1]);
		we.findElement(By.cssSelector("input[name=\"dest_last_name\"")).sendKeys(names[0]);
		we.findElement(By.cssSelector("input[name=\"dest_address1\"")).sendKeys(adr1);
		we.findElement(By.cssSelector("input[name=\"dest_address2\"")).sendKeys(adr2);
		we.findElement(By.cssSelector("input[name=\"dest_city\"")).sendKeys(city);
		we.findElement(By.cssSelector("input[name=\"dest_state_name\"")).sendKeys(state);
		we.findElement(By.cssSelector("input[name=\"dest_zip\"")).sendKeys(postcode);
		we.findElement(By.cssSelector("input[name=\"dest_tel\"")).sendKeys(tel);
		
		weList = driver.findElements(By.tagName("input"));
		for (WebElement we1 : weList) {
			if ("image".equalsIgnoreCase(we1.getAttribute("type"))) {
				if ("btn_next".equalsIgnoreCase(we1.getAttribute("name"))) {
					we1.click();
					break;
				}
			}
		}
		// add credit card
		addCreditCard(driver, crId);
		
		File oFile = new File(ooutFileName);
		String today = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy-MM-dd");
		FileUtils.write(oFile, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, taobaoOrderName + "\n",
				Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, productInfos + "\n", Charset.forName("UTF-8"),
				true);
		FileUtils.write(oFile, nameEn + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, tel + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, state + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, city + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, adr2 + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, adr1 + "\n", Charset.forName("UTF-8"), true);
		FileUtils.write(oFile, postcode + "\n", Charset.forName("UTF-8"), true);
		oFile = null;
	}

	private void addItemToCard(WebDriver driver, String productInfos,String type) {

		List<WebElement> weList = null;
		String[] pis = productInfos.split(itemSplitter);
		for (String line : pis) {
			System.out.println("[processing]" + line);
			// line:商家编码：MTBL_136000-1101581 颜色分类:BK;尺码:XL
			String[] pi = line.split(" ");
			String pid = realProductId(pi[0]);
			if(pid.startsWith("MTBL_")){
				String[] newP = pid.split("-");
				pid = newP[1];
			}
			driver.get(("JP".equals(type)?MontBellUtil.productUrlPrefix: MontBellUtil.productUrlPrefix_en) + pid);
			String color = "";
			String sizz = "";
			if (pi.length > 1) {
				String[] pii = pi[1].split(";");
				color = realColorName(pii[0]);
				if (pii.length > 1) {
					sizz = realSizeName(pii[1]);
				}
			}
			if ("".equals(sizz)) {
				System.out.println("CANNOT process that..." + line);
				mywait("Color OR size Selected realdy? ENTER for realdy!");
				continue;
			}
			weList = driver.findElements(By.tagName("select"));
			boolean has = false;
			for (WebElement we : weList) {
				if ("sel_size".equalsIgnoreCase(we.getAttribute("name"))) {
					Select dropdown = new Select(we);
					dropdown.selectByVisibleText(sizz);
					has = true;
					break;
				}
			}
			if(!has){
				System.out.println("[ERROR] cannt select color OR size! selected by manually!");
				mywait("Color OR size Selected realdy? ENTER for realdy!");
				continue;
			}
			has = false;
			weList = driver.findElements(By.tagName("select"));
			for (WebElement we : weList) {
				if ((sizz.toUpperCase() + "_" + color.toUpperCase() + "_num").equalsIgnoreCase(we
						.getAttribute("name"))) {
					Select dropdown = new Select(we);
					dropdown.selectByValue("1");
					has = true;
				}
			}
			if(!has){
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
			try {
				Thread.sleep(1000*1);
			} catch (InterruptedException e) {
			}
		}
	}

	private void addCreditCard(WebDriver driver, String crId) {
		if(crObjList.isEmpty()){
			return;
		}
		CrObject crObj = crObjList.get(0);
		if (StringUtils.isNotEmpty(crId)) {
			for (CrObject o : crObjList) {
				if (o.id.equals(crId)) {
					crObj = o;
					break;
				}
			}
		}
		fillCreditCard(driver, crObj);
	}

	private void fillCreditCard(WebDriver driver,CrObject crObj) {
		WebElement c = driver.findElement(By.id("contents"));

		List<WebElement> weList = null;
		weList = c.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			if ("card_type_id".equalsIgnoreCase(we.getAttribute("name"))) {
				Select dropdown = new Select(we);
				dropdown.selectByVisibleText(getCrBrand(crObj.crBrand));
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
}
