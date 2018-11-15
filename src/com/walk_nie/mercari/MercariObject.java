package com.walk_nie.mercari;

import java.util.List;

import com.google.common.collect.Lists;


public class MercariObject {
    public String id;
    public String title;
    public String price;
    public String shippingFee;
    public String desp;
    public String publishDT;
    public List<String> commentList = Lists.newArrayList();
    
    public String publisher;
    public String publisherPlace;
    public String publisherRateGood;
    public String publisherRateBads;
    
    
}
