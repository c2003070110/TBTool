package com.walk_nie.taobao.wacom;

import java.util.ArrayList;
import java.util.List;

public class WacomProductObject {
	public String categoryId = "";
	public String productId = "";
	public String productUrl = "";
	public String storeUrl = "";
	

	public String detailScreenShotPicFile = "";
	public String specScreenShotPicFile = "";
	public List<String> productGalaryPicFileList = new ArrayList<String>();
	public List<String> productGalaryPicUrlList = new ArrayList<String>();

	public List<String> taobaoMainPicNameList = new ArrayList<String>();

    //public String seriesName = "";
    public String productName = "";
    
    public String kataban = "";

	public String priceOrg = "";
	public String priceJPY = "";
	
	public int weight = 0;
    public int weightExtra = 0;
    
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		sb.append(productId + "|");
		sb.append(weight + "|");
		sb.append("\"");
		return sb.toString();
	}
}
