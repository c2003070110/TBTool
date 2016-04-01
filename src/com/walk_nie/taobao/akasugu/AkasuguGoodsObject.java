package com.walk_nie.taobao.akasugu;

import java.util.ArrayList;
import java.util.List;


public class AkasuguGoodsObject {

	//----json-
	//public boolean arrival;
	//public boolean bookmark;
	//public String cap_point;
	//public String cap_point_in_site;
	//public boolean cart;
	//public String color_cd;
	//public String colorName;
	public String itemName;
	public String priceOrg;
	public String priceIncludedTax;
	public String salePrice;
	public String salePriceIncludedTax;
	//public String sale_text;
	//public String size_cd;
	//public String size_name;
	public String sku;
	//public String stock;
	//public String teikibin;
	//public List<AkasuguImageObject> images = new ArrayList<AkasuguImageObject>();
	
	//public String kataban;
	public String productId;
	public String price;
	
	public String detailDisp;
	
	public List<AkasuguColorObject> colorList = new ArrayList<AkasuguColorObject>();
	public List<String> pictureList = new ArrayList<String>();
	
	public List<String> pictureNameList = new ArrayList<String>();
	public List<String> colorPictureNameList = new ArrayList<String>();
	
}
