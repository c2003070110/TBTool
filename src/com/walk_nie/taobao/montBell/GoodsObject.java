package com.walk_nie.taobao.montBell;

import java.util.ArrayList;
import java.util.List;


public class GoodsObject {
	public CategoryObject cateogryObj ;
	public String titleOrg;
    public String titleCN;
    public String titleEn;
	public String genderOrg;
	public String gender;
	public String priceJPY;
	public String priceCNY;
	public String priceOrg;
	public String productId;
	public String brand;
	public String weightOrg;
	public int weight;
    public int weightExtra;
	public String detailScreenShotPicFile;
	public List<String> sizeTipPics = new ArrayList<String>();
	public List<String> sizeList = new ArrayList<String>();
	public List<String> colorList = new ArrayList<String>();
	public List<String> pictureNameList = new ArrayList<String>();
    //public List<String> pictureUrlList = new ArrayList<String>();
    //public List<String> pictureLocalList = new ArrayList<String>();
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		sb.append(cateogryObj.rootCategory.categoryId + "|");
		sb.append(cateogryObj.rootCategory.categoryName + "|");
		sb.append(cateogryObj.p02Category.categoryId + "|");
		sb.append(cateogryObj.p02Category.categoryName + "|");
		sb.append(titleCN + "|");
		//sb.append(price + "|");
		sb.append(productId + "|");
		sb.append(brand + "|");
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
