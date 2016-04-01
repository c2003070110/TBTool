package com.walk_nie.taobao.kakaku.taobao;

import org.jsoup.helper.StringUtil;

public class KakakuTaobaoPriceObject {
	// kakaku id
	public String kakakuId = "";
	public String itemMaker = "";
	public String itemType = "";

	public double price = 0;

	public int dealCnt = 0;
	public String shipmentfee = "";
	public String title = "";

	public String shopName = "";

	public String location = "";

	public String toPrice() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		sb.append(price + "|");
		sb.append(dealCnt + "|");
		if (StringUtil.isBlank(shipmentfee)) {
		} else {
			sb.append(shipmentfee + "|");
		}
		sb.append("\"");
		return sb.toString();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		sb.append(price + "|");
		sb.append(dealCnt + "|");
		if (StringUtil.isBlank(shipmentfee)) {
			//sb.append("包邮|");
		} else {
			sb.append(shipmentfee + "|");
		}
		//sb.append(title + "|");
		//sb.append(shopName + "|");
		//sb.append(location);
		sb.append("\"");
		return sb.toString();
	}

}
