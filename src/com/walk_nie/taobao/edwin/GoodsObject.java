package com.walk_nie.taobao.edwin;

import java.util.ArrayList;
import java.util.List;

public class GoodsObject {
	public String goods_no = "";
	public String titleOrg = "";
    public String titleJP = "";
    public String titleCN = "";
	public String as_goods_no = "";
	public String seriesName = "";
	
	public int goods_sale_price = 0; // 税込　原价
	public int goods_real_price = 0; // 税込　实价
	public int goods_sale_bare_price = 0; // 税抜き　原价
	public int goods_real_bare_price = 0; // 税抜き　实价
	
	public String riseType ="";// 股上
	public String meterialJP = "";// 素材
	public String meterialCN = "";// 素材
	public String producePlace = "";//
	
	public String ecCd = "";
	public List<String> colorNameList = new ArrayList<String>();
	public List<String> sizeNameList = new ArrayList<String>();
	
	public List<String> pictureUrlList = new ArrayList<String>();
	public List<String> pictureNameList = new ArrayList<String>();
	
	public List<String> colorPictureUrlList = new ArrayList<String>();
	public List<String> colorPictureNameList = new ArrayList<String>();
	
    public List<String> modelList = new ArrayList<String>();

	public List<StockObject> stockList = new ArrayList<StockObject>();
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		
		sb.append("\"");
		return sb.toString();
	}
}
