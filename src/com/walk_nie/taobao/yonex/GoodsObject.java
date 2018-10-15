package com.walk_nie.taobao.yonex;

import java.util.List;

import com.google.common.collect.Lists;


public class GoodsObject {
	// 0:all;1:badminton racquets;2:badminton shoes;3:tennis racquets;4:tennis shoes;
	public int categoryType = 0;
	//public boolean isOut = false;
	
	public String productUrl = "";
	
	public String title;
	public String titleJP;
	public String titleEN;
	
	//public String genderOrg;
	//public String gender;
	
	public String price;
	public String priceOrg;
	public String kataban;
	//public String brand;
	//public String weightOrg;
	//public String weight;
	//public String detailDisp;
	public String producePlace;
	public String producePlaceOrg;
	
	public String detailScreenShotPicFile = "";
	
	public List<String> sizeList = Lists.newArrayList();
	public List<String> colorList = Lists.newArrayList();
	public List<String> pictureList = Lists.newArrayList();
	public List<String> pictureNameList = Lists.newArrayList();
	
	public List<GoodsRankObject> rankList = Lists.newArrayList();
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		sb.append(titleJP + "|");
		sb.append(titleEN + "|");
		sb.append(price + "|");
		sb.append(kataban + "|");
		//sb.append(brand + "|");
		//sb.append(weight + "|");
		
		String sizeS = "";
		if(!sizeList.isEmpty()){
			for(String str :sizeList){
				sizeS = sizeS +" " + str;
			}
		}else{
			sizeS = "-";
		}
		sb.append(sizeS + "|");
		
		String colorS = "";
		if(!colorList.isEmpty()){
			for(String str :colorList){
				colorS = colorS +" " + str;
			}
		}else{
			colorS = "-";
		}
		sb.append(colorS + "|");
		
		sb.append("\"");
		return sb.toString();
	}
}
