package com.walk_nie.taobao.amazon;

import java.util.List;

import com.google.common.collect.Lists;
import com.walk_nie.taobao.object.StockObject;

public class AmazonGoodsObject {
    public String url;
    
    public String asin;
    public String sku;
    public String jlanCd;
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
    public long priceJPY;
    public long price;
    
    //public String productUrl;
    public int weightItem = 0;
    public int weightShipment = 0;

	public List<StockObject> stockList = Lists.newArrayList();
	
	public List<String> sizeNameList = Lists.newArrayList();
	public List<String> colorNameList = Lists.newArrayList();
	//public List<String> colorPicUrlList = new ArrayList<String>();
    //public List<String> colorPicLocalNameList = Lists.newArrayList();
	
	//public List<String> pictureNameList = new ArrayList<String>();

	public List<String> googsVideoUrlList = Lists.newArrayList();
    public List<String> googsVideoLocalNameList = Lists.newArrayList();
	
	public List<String> googsPicUrlList = Lists.newArrayList();
    public List<String> googsPicLocalNameList = Lists.newArrayList();
	
	public List<String> detailScreenShotPicFile = Lists.newArrayList();
	public List<String> specialPageScreenShotPicFile = Lists.newArrayList();

}
