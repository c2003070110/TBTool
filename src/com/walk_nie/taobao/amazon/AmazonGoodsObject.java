package com.walk_nie.taobao.amazon;

import java.util.List;

import com.google.common.collect.Lists;
import com.walk_nie.taobao.object.StockObject;

public class AmazonGoodsObject {
    public String asin;
    public String kataban;//品番
    public String goodsNo;
    
    public String titleOrg;
    public String title;
    public String titleJP;
    
    // 0:junior;1:men;2:women;3:both  
    public int gender;
    
    //public String subTitleOrg;
    //public String subTitle;
    
    public String priceOrg;
    public String price;
    
    public String productUrl;

	public List<StockObject> stockList = Lists.newArrayList();
	
	//public List<String> sizeNameList = Lists.newArrayList();
	//public List<String> colorNameList = Lists.newArrayList();
	//public List<String> colorPicUrlList = new ArrayList<String>();
    //public List<String> colorPicLocalNameList = Lists.newArrayList();
	
	//public List<String> pictureNameList = new ArrayList<String>();
	
	public List<String> googsPicUrlList = Lists.newArrayList();
    public List<String> googsPicLocalNameList = Lists.newArrayList();
	
	public List<String> detailScreenShotPicFile = Lists.newArrayList();
	public List<String> specialPageScreenShotPicFile = Lists.newArrayList();

}
