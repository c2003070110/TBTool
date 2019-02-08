package com.walk_nie.taobao.montBell;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.StockObject;


public class GoodsObject {
	public CategoryObject cateogryObj = new CategoryObject();
	public String titleOrg = "";
    public String titleJP = "";
    public String titleCN = "";
    public String titleEn = "";
	public String genderOrg = "";
	public String gender = "";
	public String priceJPY = "";
	public String priceCNY = "";
	public String priceOrg = "";
	public String productId = "";
	public String brand = "";
	public int weight = 0;
    public int weightExtra = 0;
	public String detailScreenShotPicFile = "";
	public List<String> sizeTipPics = Lists.newArrayList();
	public List<String> sizeList = Lists.newArrayList();
	public List<String> colorList = Lists.newArrayList();
	public List<String> pictureNameList = Lists.newArrayList();
	
	public List<String> dressOnPics = Lists.newArrayList();
    //public List<String> pictureUrlList = Lists.newArrayList();
    //public List<String> pictureLocalList = Lists.newArrayList();
	
	public String materialOrg = "";// 素材
	public String weightOrg = "";// 平均重量
	public String colorOrg = "";// カラー
	public String sizeOrg = "";// サイズ
	public String specialOrg = "";// 特長
	public String functionOrg = ""; // 機能

	public List<StockObject> stockList = Lists.newArrayList();
	
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
