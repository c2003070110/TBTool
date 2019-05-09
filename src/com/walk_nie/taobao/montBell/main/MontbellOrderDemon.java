package com.walk_nie.taobao.montBell.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walk_nie.taobao.TaobaoScanOrder;
import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.OrderDetailObject;
import com.walk_nie.taobao.object.OrderInfoObject;
import com.walk_nie.taobao.object.TaobaoOrderProductInfo;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class MontbellOrderDemon {
	private File logFile = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		MontbellOrderDemon main = new MontbellOrderDemon();
		main.execute();
	}

	public void execute() {
		init();

		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		MontbellAutoOrder order = new MontbellAutoOrder(driver);
		MontbellStockChecker stock = new MontbellStockChecker();
		MontbellPinyinMain pinyin = new MontbellPinyinMain();
		int interval = 0;// second
		while (true) {
			try {
				long t1 = System.currentTimeMillis();

				order.processForWebService();
				long t2 = System.currentTimeMillis();
				long dif = t2 - t1;
				if (dif > interval * 1000) {
					// continue;
				}
				stock.processForWebService();

				pinyin.processForWebService();
				t2 = System.currentTimeMillis();
				dif = t2 - t1;
				if (dif < interval * 1000) {
					NieUtil.log(logFile, "[SLEEP]zzzZZZzzz...");
					NieUtil.mySleepBySecond((new Long(interval - dif / 1000))
							.intValue());
				}

				try {
					scanTBOrder(driver);
				} catch (Exception e) {
					e.printStackTrace();
					NieUtil.log(logFile,
							"[ERROR][Montbell][scanTBOrder]" + e.getMessage());
					NieUtil.log(logFile, e);
				}
			} catch (Exception ex) {
				NieUtil.log(logFile, ex);
			}
		}
	}

	public void execute(WebDriver driver) {
		MontbellAutoOrder order = new MontbellAutoOrder(driver);
		MontbellStockChecker stock = new MontbellStockChecker();
		MontbellPinyinMain pinyin = new MontbellPinyinMain();

		try {
			order.processForWebService();
		} catch (IOException e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Montbell][Ordering]" + e.getMessage());
			NieUtil.log(logFile, e);
		}

		try {
			stock.processForWebService();
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile,
					"[ERROR][Montbell][StockCheck]" + e.getMessage());
			NieUtil.log(logFile, e);
		}

		try {
			pinyin.processForWebService();
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile, "[ERROR][Montbell][PINYIN]" + e.getMessage());
			NieUtil.log(logFile, e);
		}

		try {
			//scanTBOrder(driver);
		} catch (Exception e) {
			e.printStackTrace();
			NieUtil.log(logFile,
					"[ERROR][Montbell][scanTBOrder]" + e.getMessage());
			NieUtil.log(logFile, e);
		}
	}

	private void scanTBOrder(WebDriver driver)
			throws UnsupportedOperationException, IOException {

		TaobaoScanOrder scan = new TaobaoScanOrder();
		List<OrderInfoObject> orders = scan.process(driver);
		if (orders == null || orders.isEmpty())
			return;

		addTBOrderByWebService(orders);

	}

	private void addTBOrderByWebService(List<OrderInfoObject> orders)
			throws UnsupportedOperationException, IOException {

		List<OrderInfoObject> ordersMerge = Lists.newArrayList();
		for (OrderInfoObject order : orders) {
			boolean existFlag = false;
			OrderInfoObject merge = null;
			for (OrderInfoObject orderMerge : ordersMerge) {
				if (order.orderObject.buyerName
						.equals(orderMerge.orderObject.buyerName)) {
					existFlag = true;
					merge = orderMerge;
					break;
				}
			}
			if (existFlag) {
				merge.orderDtlList.addAll(order.orderDtlList);
			} else {
				ordersMerge.add(order);
			}
		}

		for (OrderInfoObject order : ordersMerge) {

			String maijia = order.orderObject.buyerName;
			String dingdanhao = order.orderObject.orderNo;
			String dingdanDt = order.orderObject.orderCreatedTime;
			String maijiadianzhiHanzi = order.orderObject.addressFull;
			String buyerNote = order.orderObject.buyerNote;
			String transferWay = "pinYou";
			String productList = ""; // ,1101532,BK,L;,1101531,BK,M;
			for (OrderDetailObject dtl : order.orderDtlList) {
				if (StringUtil.isBlank(dtl.sku)) {
					continue;
				}
				try {
					TaobaoOrderProductInfo info = MontBellUtil
							.readTaobaoProductInfo(dtl.sku);
					productList += "," + info.productId + "," + info.colorName
							+ "," + info.sizeName + ";";
				} catch (Exception e) {

				}
			}
			if (StringUtil.isBlank(productList)) {
				continue;
			}
			productList = productList.substring(0, productList.length() - 1);

			Map<String, String> param = Maps.newHashMap();
			param.put("action", "saveTBOrder");
			param.put("maijia", maijia);
			param.put("dingdanhao", dingdanhao);
			param.put("dingdanDt", dingdanDt);
			param.put("maijiadianzhiHanzi", maijiadianzhiHanzi);
			param.put("buyerNote", buyerNote);
			param.put("transferWay", transferWay);
			param.put("productList", productList);
			NieUtil.log(logFile, "[INFO][Service:saveTBOrder][Param]"
					+ "[maijia]" + maijia + "[dingdanhao]" + dingdanhao);

			String rslt = NieUtil.httpGet(
					NieConfig.getConfig("montbell.order.service.url"), param);

			if (!StringUtil.isBlank(rslt)) {
				NieUtil.log(logFile, "[INFO][Service:saveTBOrder][RESULT]"
						+ rslt);
				continue;
			}
		}
	}

	public void init() {

		logFile = new File(NieConfig.getConfig("montbell.log.file"));
		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(
				java.util.logging.Level.OFF);
		Logger.getLogger("org.openqa.selenium").setLevel(
				java.util.logging.Level.OFF);
	}

}
