package com.walk_nie.taobao.yonex.badminton;

import java.util.ArrayList;
import java.util.List;


public class GoodsObject {
	public boolean isUpdate = false;
	// 1:badminton racquets;2:badminton shoes;3:tennis racquets;4:tennis shoes;
	public int categoryType = -1;
	public boolean isOut = false;
	
	public String title;
	public String titleJP;
	public String titleEN;
	
	public String genderOrg;
	public String gender;
	public String price;
	public String priceOrg;
	public String kataban;
	//public String brand;
	public String weightOrg;
	public String weight;
	public String detailDisp;
	public String productPlace;
	public String productPlaceOrg;
	public List<String> sizeList = new ArrayList<String>();
	public List<String> colorList = new ArrayList<String>();
	public List<String> strings = new ArrayList<String>();
	public List<String> pictureList = new ArrayList<String>();
	public List<String> pictureNameList = new ArrayList<String>();
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		sb.append(titleJP + "|");
		sb.append(titleEN + "|");
		sb.append(price + "|");
		sb.append(kataban + "|");
		//sb.append(brand + "|");
		sb.append(weight + "|");
		
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
