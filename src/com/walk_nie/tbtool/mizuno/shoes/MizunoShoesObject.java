package com.walk_nie.tbtool.mizuno.shoes;

import java.util.ArrayList;
import java.util.List;


public class MizunoShoesObject {
    //public String id;
    public String kataban;
    
    public String titleOrg;
    public String title;
    
    // 0:junior;1:men;2:women;3:both  
    public int gender;
    
    
    //public String subTitleOrg;
    //public String subTitle;
    
    public String priceOrg;
    public String price;
    
    public String prodUrl;
    
    public List<String> picUrlList = new ArrayList<String>();
    public List<String> picLocalList = new ArrayList<String>();
    
    public List<SizeOption> sizeOptList = new ArrayList<SizeOption>();
    
    public List<String> colorList = new ArrayList<String>();
    
    
    class SizeOption{
        public String sizeLabel;
        public boolean isStock;
    }

}
