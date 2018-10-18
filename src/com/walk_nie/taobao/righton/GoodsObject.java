package com.walk_nie.taobao.righton;

import java.util.List;

import com.google.common.collect.Lists;


public class GoodsObject {
    public String brand;
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
    
	public List<String> sizeTipPics = Lists.newArrayList();

	
	public List<String> sizeNameList = Lists.newArrayList();
	public List<String> colorNameList = Lists.newArrayList();
	public List<String> colorPicUrlList = Lists.newArrayList();
    public List<String> colorPicLocalNameList = Lists.newArrayList();
	
	public List<String> dressOnPicsUrlList = Lists.newArrayList();
    public List<String> dressOnPicLocalNameList = Lists.newArrayList();
	

	public String sizeTipScreenShotPicFile = "";
	public String detailScreenShotPicFile = "";

	public String producePlace;
    //public List<SizeOption> sizeOptList = Lists.newArrayList();
    
    //public class SizeOption{
    //    public String sizeLabel;
    //    public boolean isStock;
    //}

}
