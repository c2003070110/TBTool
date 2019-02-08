package com.walk_nie.taobao.edwin;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.StockObject;

public class GoodsObject {
	public String brandCd = "";
	public String cateCd = "";
	public String sexVal = "";
	public String dispNo = "";
	
	public String goods_no = "";
	public String titleOrg = "";
    public String titleJP = "";
    public String titleCN = "";
	public  List<String> as_goods_no = Lists.newArrayList();//商品番号
	public String seriesName = "";//シリーズ
	
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
	
	public List<String> bodyPictureUrlList = new ArrayList<String>();//宝贝详细用图
	public List<String> bodyPictureFileNameList = new ArrayList<String>();//宝贝详细用图
	
	public List<String> colorPictureUrlList = new ArrayList<String>();//宝贝头图 颜色用图
	public List<String> colorPictureFileNameList = new ArrayList<String>();//宝贝头图 颜色用图
	
	public String sizeTipFileName = "";
	
    public String modelTipInfo = "";//模特数据

	public List<StockObject> stockList = new ArrayList<StockObject>();
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		
		sb.append("\"");
		return sb.toString();
	}
}
