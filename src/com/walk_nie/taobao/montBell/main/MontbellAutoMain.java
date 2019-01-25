package com.walk_nie.taobao.montBell.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jsoup.helper.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.montBell.StockObject;
import com.walk_nie.taobao.object.OrderDetailObject;
import com.walk_nie.taobao.object.OrderObject;
import com.walk_nie.util.NieUtil;

public class MontbellAutoMain {
	protected BufferedReader stdReader = null;
	private String outFileName =  "taobao-out.txt";
	private String outOrderMemoFileName = "taobao-order-memo.txt";
	
	private String itemSplitter =",";
	
	public static void main(String[] args) throws Exception {
		new MontbellAutoMain().process();
	}

	public void process() throws Exception {
		MontbellAutoOrder montbellAutoOrder = new MontbellAutoOrder();
		while (true) {
			try {
				//
				int todoType = choiceTodo();
				if (todoType == 0) {
					anaylizeTaobaoOrder();
				}
				if (todoType == 1) {
					montbellAutoOrder.orderForChina();
				}
				if (todoType == 2) {
					montbellAutoOrder.orderForJapan();
				}
				if (todoType == 3) {
					toPinYin();
				}
				if (todoType == 4) {
					stockCheck();
				}
				if (todoType == 5) {
					stockCheckByInputId();
				}
				if (todoType == 6) {
					montbellAutoOrder.screenShotShoppingCart();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	private void stockCheckByInputId() throws Exception {
		String productId = "";
		System.out.println("Please Input the Product ID : ");

		stdReader = getStdReader();
		while (true) {
			String line = stdReader.readLine().trim();
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				break;
			} else if (!"".equalsIgnoreCase(line)) {
				productId = line;
				break;
			}
		}

		MontbellStockChecker stockChecker = new MontbellStockChecker();
		stockChecker.processByProductId(productId);
	}


	private void stockCheck() throws Exception {
		MontbellStockChecker stockChecker = new MontbellStockChecker();
		stockChecker.processByTaobaoOrderProduct();
	}

	private void toPinYin() throws Exception {
		MontbellPinyinMain pinyin = new MontbellPinyinMain();
		pinyin.process();
	}

	private void anaylizeTaobaoOrder() throws Exception {
		//File f = new File(inOrderDir);
		File f = new File(System.getProperty("user.home"),"Downloads");
		File[] fs = f.listFiles();
		List<File> fslist = Arrays.asList(fs);
		Collections.sort(fslist, new Comparator<File>() {
			@Override
			public int compare(File arg0, File arg1) {
				return (int) (arg0.lastModified() - arg1.lastModified());
			}
		});
		List<OrderObject> orderList = Lists.newArrayList();
		List<OrderDetailObject> orderDtlList = Lists.newArrayList();
		for(File ff:fslist){
			if(!ff.isFile())continue;
			if(ff.getName().startsWith("ExportOrderList")){
				System.out.println("Read Order List From " + ff.getCanonicalPath());
				orderList = readInOrder(ff);
			}
		}
		for (File ff : fslist) {
			if (!ff.isFile())
				continue;
			if (ff.getName().startsWith("ExportOrderDetailList")) {
				System.out.println("Read Order Detail List From " + ff.getCanonicalPath());
				orderDtlList = readInOrderDetail(ff);
			}
		}
		List<String> orderHis = Lists.newArrayList();
		List<String> montbellOrderList = Lists.newArrayList();
		String fmt1 ="%s\t%s\t%s\t%s\t\t\t%s";
		for(OrderObject order:orderList){
			//if(!"买家已付款，等待卖家发货".equals(order.orderStatus)) continue;
			if("交易关闭".equals(order.orderStatus)) continue;
			if(order.baobeiTitle.toLowerCase().indexOf("MontBell".toLowerCase()) <0) continue;
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
					tmp +=productId+";" + dtl.itemAttr + itemSplitter;
				}else{
					tmp +=productId+";" + dtl.itemAttr;
				}
				
				String stockStuts = anlynizeStock(productId,dtl.itemAttr);
				
				orderHis.add(String.format(fmt1, order.buyerName,
						order.orderPayedTime, productId + ";" + dtl.itemAttr,
						order.acturalPayAmt, stockStuts));
			}
			montbellOrderList.add(tmp);
			
			String toPy = PinyinHelper.convertToPinyinString(order.recName, " ",
					PinyinFormat.WITHOUT_TONE);
			String[] spl = toPy.toLowerCase().split(" ");
			String line ="";
			for (int i=0;i<spl.length;i++) {
				String str = spl[i];
				if("".equals(str))continue;
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
		File oFile = new File(MontBellUtil.rootPathName, outFileName);
		NieUtil.appendToFile(oFile, montbellOrderList);
		File outOrderMemoFileName1 = new File(MontBellUtil.rootPathName, outOrderMemoFileName);
		System.out.println("Write The Result To " + outOrderMemoFileName1.getCanonicalPath());
		NieUtil.appendToFile(outOrderMemoFileName1, orderHis);
	}

	private String anlynizeStock(String productId, String itemAttr) throws Exception {
		String color="",siz = "";
		if(itemAttr.indexOf(";") >=0){
			String[] sl = itemAttr.split(";");
			color = realColorName(sl[0]);
			siz = realSizeName(sl[1]);
		}
		MontbellStockChecker check = new MontbellStockChecker();
		List<StockObject>  stockList = check.getMontbellStockInfo(productId);
		String stockSts = "",price="";
		for (StockObject ojb : stockList) {
			boolean isSkipColor = MontBellUtil.colorNameDefault.equals(color) ? true
					: false;
			boolean isSkipSize = MontBellUtil.sizeNameDefault.equals(siz) ? true
					: false;
			if ((isSkipColor || ojb.colorName.equalsIgnoreCase(color))
					&& (isSkipSize || ojb.sizeName.equalsIgnoreCase(siz))) {
				stockSts = ojb.stockStatus;
				price = ojb.priceJPY;
				break;
			}
		}
		return String.format("%s\t%s", stockSts,price);
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
        		idx++;
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
	private int choiceTodo() {
		int type = 0;
		try {
			System.out.println("Type of todo : ");
			System.out.println(" 0:Get From Taobao Order;\n"
					+ " 1:Order（CHINA);\n 2:Order（JAPAN);\n 3:to PinYin;\n"
			        + " 4:stock check; \n 5:stock check By input id;\n"
	        		+ " 6:screenShot shopping card..;\n"
    		        + " 7..;\n");

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
				} else if ("4".equals(line.trim())) {
					type = 4;
					break;
				} else if ("5".equals(line.trim())) {
					type = 5;
					break;
				} else if ("6".equals(line.trim())) {
					type = 6;
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
}
