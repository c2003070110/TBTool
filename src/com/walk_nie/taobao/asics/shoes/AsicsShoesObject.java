package com.walk_nie.taobao.asics.shoes;

import java.util.ArrayList;
import java.util.List;


public class AsicsShoesObject {
    public String id;
    public String kataban;
    
    public String titleOrg;
    public String titleCN;
    
    public String subTitleOrg;
    public String subTitle;
    
    public String gender;
    
    public String priceOrg;
    public String priceJPY;
    public String priceCNY;
    
    public String prodUrl;
    
    public String detailScreenShotPicFile;
    
    public List<String> picUrlList = new ArrayList<String>();
    public List<String> picLocalFileNameList = new ArrayList<String>();
    public List<String> picNameList = new ArrayList<String>();
    
    public List<SizeOption> sizeOptList = new ArrayList<SizeOption>();
    
    public List<AsicsShoesObject> colorList = new ArrayList<AsicsShoesObject>();
    
    public List<String> colorNameList = new ArrayList<String>();
    
    
    class SizeOption{
        public String sizeLabel;
        public boolean isStock;
    }

}
