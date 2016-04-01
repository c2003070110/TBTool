package com.walk_nie.taobao.asics.shoes;

import java.util.ArrayList;
import java.util.List;


public class AsicsShoesObject {
    public String id;
    public String kataban;
    
    public String titleOrg;
    public String title;
    
    public String subTitleOrg;
    public String subTitle;
    
    public String priceOrg;
    public String price;
    
    public String prodUrl;
    
    public List<String> picUrlList = new ArrayList<String>();
    public List<String> picLocalList = new ArrayList<String>();
    
    public List<SizeOption> sizeOptList = new ArrayList<SizeOption>();
    
    public List<AsicsShoesObject> colorList = new ArrayList<AsicsShoesObject>();
    
    
    class SizeOption{
        public String sizeLabel;
        public boolean isStock;
    }

}
