package com.walk_nie.taobao.eizo;

import java.util.ArrayList;
import java.util.List;

public class EizoProductObject {
	public String categoryName = "";
	//public String productId = "";
	public String productUrl = "";
	public String storeUrl = "";
    public String productName = "";

    //public String seriesName = "";
    public String kataban = "";
	

	public String detailScreenShotPicFile = "";
	public String specScreenShotPicFile = "";
	public List<String> productGalaryPicFileList = new ArrayList<String>();
	public List<String> productGalaryPicUrlList = new ArrayList<String>();

	public List<String> taobaoMainPicNameList = new ArrayList<String>();


	public String priceOrg = "";
	public String priceJPY = "";
	
	public int weight = 0;
    public int weightExtra = 0;
    
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		//sb.append(productId + "|");
		sb.append(weight + "|");
		sb.append("\"");
		return sb.toString();
	}
}
