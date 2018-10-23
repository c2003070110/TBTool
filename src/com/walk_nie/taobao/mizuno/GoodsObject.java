package com.walk_nie.taobao.mizuno;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;


public class GoodsObject {
    //public String id;
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

	
	public List<String> sizeNameList = Lists.newArrayList();
	public List<String> colorNameList = Lists.newArrayList();
	public List<String> colorPicUrlList = new ArrayList<String>();
    public List<String> colorPicLocalNameList = Lists.newArrayList();
	
	//public List<String> pictureNameList = new ArrayList<String>();
	
	public List<String> dressOnPicsUrlList = new ArrayList<String>();
    public List<String> dressOnPicLocalNameList = Lists.newArrayList();
	

	public String detailScreenShotPicFile = "";
	public String specialPageScreenShotPicFile = "";
    //public List<SizeOption> sizeOptList = Lists.newArrayList();
    
    //public class SizeOption{
    //    public String sizeLabel;
    //    public boolean isStock;
    //}

}
