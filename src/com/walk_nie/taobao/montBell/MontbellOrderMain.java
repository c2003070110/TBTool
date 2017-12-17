package com.walk_nie.taobao.montBell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.jsoup.helper.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.beust.jcommander.internal.Lists;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.io.Files;
import com.walk_nie.taobao.object.OrderDetailObject;
import com.walk_nie.taobao.object.OrderObject;
import com.walk_nie.taobao.util.WebDriverUtil;

public class MontbellOrderMain {
	protected BufferedReader stdReader = null;
	private String inOrderDir = "./montbell/";
	//private String pinyinInFileName = "./montbell/pinyin-in.txt";
	//private String pinyoutInFileName = "./montbell/pinyin-out.txt";
	private String inFileName = "./montbell/order-in.txt";
	private String ooutFileName = "./montbell/order-out.txt";
	private String outFileName = "./montbell/taobao-out.txt";
	private String outOrderMemoFileName = "./montbell/taobao-order-memo.txt";

	public static void main(String[] args) throws Exception {
		new MontbellOrderMain().process();
	}

	public void process() throws Exception {

		while (true) {
			try {
				//
				int todoType = choiceTodo();
				if (todoType == 0) {
					anaylizeTaobaoOrder();
				}
				if (todoType == 1) {
					orderForChina();
				}
				if (todoType == 2) {
					orderForJapan();
				}
				if (todoType == 3) {
					toPinYin();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void toPinYin() throws Exception {
		MontbellPinyinMain pinyin = new MontbellPinyinMain();
		pinyin.process();
	}

	private void anaylizeTaobaoOrder() throws PinyinException, IOException {
		File f = new File(inOrderDir);
		File[] fs = f.listFiles();
		List<OrderObject> orderList = Lists.newArrayList();
		List<OrderDetailObject> orderDtlList = Lists.newArrayList();
		for(File ff:fs){
			if(!ff.isFile())continue;
			if(ff.getName().startsWith("ExportOrderList")){
				orderList = readInOrder(ff);
			}
			if(ff.getName().startsWith("ExportOrderDetailList")){
				orderDtlList = readInOrderDetail(ff);
			}
		}
		List<String> orderHis = Lists.newArrayList();
		List<String> montbellOrderList = Lists.newArrayList();
		String fmt1 ="%s\t%s\t%s\t%s";
		for(OrderObject order:orderList){
			if(!"买家已付款，等待卖家发货".equals(order.orderStatus)) continue;
			if(order.baobeiTitle.indexOf("MontBell") <0) continue;
			//if(!"".equals(order.orderNote))continue;
			List<OrderDetailObject> orderDtls = Lists.newArrayList();
			for(OrderDetailObject orderDtl1:orderDtlList){
				if(order.orderNo.equals(orderDtl1.orderNo)){
					orderDtls.add(orderDtl1);
				}
			}
			montbellOrderList.add(order.recName + " " + order.addressFull);
			montbellOrderList.add(order.buyerName);
			String tmp = "";
			for (int i=0;i<orderDtls.size();i++) {
			   OrderDetailObject dtl = orderDtls.get(i);
				String productId = "";
				String outer_id = dtl.sku.replace("\"", "");
				if (outer_id.startsWith("MTBL_")) {
					String[] split = outer_id.split("-");
					productId = split[split.length - 1];
				}
				if(i != orderDtls.size()-1){
					tmp +=productId+";" + dtl.itemAttr + "-";
				}else{
					tmp +=productId+";" + dtl.itemAttr;
				}
				
				orderHis.add(String.format(fmt1, order.buyerName,order.orderPayedTime,productId,order.acturalPayAmt));
			}
			montbellOrderList.add(tmp);
			
			String toPy = PinyinHelper.convertToPinyinString(order.recName, " ",
					PinyinFormat.WITHOUT_TONE);
			String[] spl = toPy.toLowerCase().split(" ");
			String line ="";
			for (int i=0;i<spl.length;i++) {
				String str = spl[i];
				line += str.substring(0, 1).toUpperCase() + str.substring(1);
			}
			montbellOrderList.add(line);
			montbellOrderList.add(order.mobile.replace("'", ""));
			String add = order.addressFull;
			String postCd = "";
			if (add.endsWith(")")) {
				int idd = add.lastIndexOf("(");
				postCd = add.substring(idd + 1, add.length() - 1);
				add = add.substring(0, idd);
			}
			String addPin = PinyinHelper.convertToPinyinString(add, " ",
					PinyinFormat.WITHOUT_TONE);
			spl = addPin.toLowerCase().split(" ");
			line = "";
			for (int i = 0; i < spl.length; i++) {
				String str = spl[i];
				if ("".equals(str)) {
					if (!"".equals(line)) {
						montbellOrderList.add(line);
					}
					line = "";
					continue;
				}
				line += str.substring(0, 1).toUpperCase() + str.substring(1);
			}
			if (!"".equals(line)) {
				montbellOrderList.add(line);
			}
			montbellOrderList.add(postCd);
			montbellOrderList.add("------------");
		}
		File oFile = new File(outFileName);
		String today = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy-MM-dd");
		FileUtils.write(oFile, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		for (String str : montbellOrderList) {
			FileUtils.write(oFile, str + "\n", Charset.forName("UTF-8"), true);
		}
		File outOrderMemoFileName1 = new File(outOrderMemoFileName);
		FileUtils.write(outOrderMemoFileName1, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		for (String str : orderHis) {
			FileUtils.write(outOrderMemoFileName1, str + "\n", Charset.forName("UTF-8"), true);
		}
		oFile = null;
	}

	private List<OrderDetailObject> readInOrderDetail(File ff) {
		List<OrderDetailObject> orderList = Lists.newArrayList();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
            		ff), "GB2312"));
            String strLine = null;
            List<String> strList = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				if (!StringUtil.isBlank(strLine) && strLine.startsWith("=")) {
					strList.add(strLine);
				}
			}
            for (String str : strList) {
        		String[] split = str.split("\",\"");
        		OrderDetailObject obj = new OrderDetailObject();
        		int idx = 0;
        		obj.orderNo  = removeNull(split[idx++]);obj.baobeiTitle  = removeNull(split[idx++]);obj.price  = removeNull(split[idx++]);
        		obj.num  = removeNull(split[idx++]);obj.exterSysNo  = removeNull(split[idx++]);obj.itemAttr  = removeNull(split[idx++]);
        		obj.setI  = removeNull(split[idx++]);obj.note  = removeNull(split[idx++]);obj.orderStatus  = removeNull(split[idx++]);
        		obj.sku  = removeNull(split[idx++]);
        		orderList.add(obj);
            }
        }catch(Exception e){
        	System.err.println(e.getMessage());
        	System.out.println("[ERROR]readInPublishedBaobei.BUT continue...");
        } finally {
            if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        }
        
		return orderList;
	}

	private List<OrderObject> readInOrder(File ff) {
		List<OrderObject> orderDtlList = Lists.newArrayList();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
            		ff), "GB2312"));
            String strLine = null;
            List<String> strList = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				if (!StringUtil.isBlank(strLine) && strLine.startsWith("=")) {
					strList.add(strLine);
				}
			}
            for (String str : strList) {
        		String[] split = str.split("\",\"");
        		OrderObject obj = new OrderObject();
        		int idx = 0;
        		obj.orderNo  = removeNull(split[idx++]);obj.buyerName  = removeNull(split[idx++]);obj.buyerAlipayAccountNo  = removeNull(split[idx++]);
        		obj.payAmt  = removeNull(split[idx++]);obj.payEMSAmt  = removeNull(split[idx++]);obj.payPoint  = removeNull(split[idx++]);
        		obj.ttlAmt  = removeNull(split[idx++]);obj.returnPoint  = removeNull(split[idx++]);obj.acturalPayAmt  = removeNull(split[idx++]);
        		obj.acturalPayPoint  = removeNull(split[idx++]);obj.orderStatus  = removeNull(split[idx++]);obj.buyerNote  = removeNull(split[idx++]);
        		obj.recName  = removeNull(split[idx++]);obj.addressFull  = removeNull(split[idx++]);obj.transWay  = removeNull(split[idx++]);
        		obj.tel  = removeNull(split[idx++]);obj.mobile  = removeNull(split[idx++]);obj.orderCreatedTime  = removeNull(split[idx++]);
        		obj.orderPayedTime  = removeNull(split[idx++]);obj.baobeiTitle = removeNull(split[idx++]);obj.baobeiType  = removeNull(split[idx++]);
        		obj.transNo  = removeNull(split[idx++]);obj.transCompany  = removeNull(split[idx++]);obj.orderNote  = removeNull(split[idx++]);
        		obj.baobeiNum  = removeNull(split[idx++]);obj.storeId  = removeNull(split[idx++]);obj.storeName  = removeNull(split[idx++]);
        		orderDtlList.add(obj);
            }
        }catch(Exception e){
        	System.err.println(e.getMessage());
        	System.out.println("[ERROR]readInPublishedBaobei.BUT continue...");
        } finally {
            if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        }
		Collections.sort(orderDtlList, new Comparator<OrderObject>(){
			@Override
			public int compare(OrderObject arg0, OrderObject arg1) {
				return arg0.orderCreatedTime.compareTo(arg1.orderCreatedTime);
			}
		});

		return orderDtlList;
	}
	private String removeNull(String str) {
		String news =  str.replace("null", "");
		if(news.startsWith("'")){
			news = news.substring(1);
		}
		if(news.startsWith("=\"")){
			news = news.substring(2);
		}
		return news;
	}

	private void orderForJapan() {

	}

	private void orderForChina() {
		
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
					//driver = logon();
				}
				System.out.println("[waiting for order info in ]"
						+ tempFile0.getAbsolutePath());
			}
		}
	}

	private WebDriver logonForChina() {
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
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

	protected void orderForChina(WebDriver driver, File tempFile0) throws IOException,
			PinyinException {

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

		List<WebElement> weList = null;
		addItemToCard(driver,productInfos);
		
		driver.get("https://en.montbell.jp/products/cart/");
		weList = driver.findElements(By.tagName("img"));
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
		weList = driver.findElements(By.tagName("input"));
		for (WebElement we : weList) {
			if ("radio".equalsIgnoreCase(we.getAttribute("type"))) {
				if ("3".equalsIgnoreCase(we.getAttribute("value"))) {
					we.click();
					break;
				}
			}
		}
		weList = driver.findElements(By.tagName("input"));
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
			Thread.sleep(1000*2);
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
		addCreditCardJCB(driver);

		weList = driver.findElements(By.tagName("input"));
		for (WebElement we1 : weList) {
			if ("image".equalsIgnoreCase(we1.getAttribute("type"))) {
				if ("btn_next".equalsIgnoreCase(we1.getAttribute("name"))) {
					we1.click();
					break;
				}
			}
		}
		
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

	private void addItemToCard(WebDriver driver, String productInfos) {

		List<WebElement> weList = null;
		String[] pis = productInfos.split("-");
		for (String line : pis) {
			System.out.println("[processing]" + line);
			String[] pi = line.split(";");
			String pid = pi[0];
			String color = "";
			if (pi.length > 1) {
				color = pi[1].replace("颜色分类:", "");
				color = color.replace("颜色分类：", "");
			}
			String sizz = "";
			if (pi.length > 2) {
				sizz = pi[2].replace("尺码:", "");
				sizz = sizz.replace("尺码：", "");
				sizz = sizz.replace("鞋码：", "");
				sizz = sizz.replace("鞋码:", "");
			}
			if ("".equals(sizz)) {
				System.out.println("CANNOT process that..." + line);
				continue;
			}
			driver.get(MontBellUtil.productUrlPrefix_en + pid);
			weList = driver.findElements(By.tagName("select"));
			for (WebElement we : weList) {
				if ("sel_size".equalsIgnoreCase(we.getAttribute("name"))) {
					Select dropdown = new Select(we);
					dropdown.selectByVisibleText(sizz);
					break;
				}
			}
			weList = driver.findElements(By.tagName("select"));
			for (WebElement we : weList) {
				if ((sizz.toUpperCase() + "_" + color.toUpperCase() + "_num").equalsIgnoreCase(we
						.getAttribute("name"))) {
					Select dropdown = new Select(we);
					dropdown.selectByValue("1");
				}
			}

			weList = driver.findElements(By.tagName("img"));
			for (WebElement we : weList) {
				if ("cart_in".equalsIgnoreCase(we
						.getAttribute("name"))) {
					we.click();
					break;
				}
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

	private void addCreditCardJCB(WebDriver driver) {
		WebElement c = driver.findElement(By.id("contents"));

		List<WebElement> weList = null;
		weList = c.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			if ("card_type_id".equalsIgnoreCase(we.getAttribute("name"))) {
				Select dropdown = new Select(we);
				dropdown.selectByVisibleText("JCB");
				break;
			}
		}
		c.findElement(By.cssSelector("input[name=\"card_number1\"")).sendKeys("3574");
		c.findElement(By.cssSelector("input[name=\"card_number2\"")).sendKeys("0140");
		c.findElement(By.cssSelector("input[name=\"card_number3\"")).sendKeys("6206");
		c.findElement(By.cssSelector("input[name=\"card_number4\"")).sendKeys("3285");

		weList = c.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			if ("card_expire_month".equalsIgnoreCase(we.getAttribute("name"))) {
				Select dropdown = new Select(we);
				dropdown.selectByVisibleText("10");
				break;
			}
		}
		for (WebElement we : weList) {
			if ("card_expire_year".equalsIgnoreCase(we.getAttribute("name"))) {
				Select dropdown = new Select(we);
				dropdown.selectByVisibleText("21");
				break;
			}
		}
		c.findElement(By.cssSelector("input[name=\"security_code\"")).sendKeys("047");
	}

	private int choiceTodo() {
		int type = 0;
		try {
			System.out.println("Type of todo : ");
			System.out.println("0:Get From Taobao Order;\n" + "1:Order（CHINA);\n2:Order（JAPAN);\n3:to PinYin;\n" + "4:...;\n");

			stdReader = getStdReader();
			while (true) {
				String line = stdReader.readLine();
				if ("0".equals(line.trim())) {
					type = 0;
					break;
				} else if ("1".equals(line.trim())) {
					type = 1;
					break;
				} else if ("2".equals(line.trim())) {
					type = 2;
					break;
				} else if ("3".equals(line.trim())) {
					type = 3;
					break;
				} else {
					System.out.println("Listed number only!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;
	}
	public BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}
}
