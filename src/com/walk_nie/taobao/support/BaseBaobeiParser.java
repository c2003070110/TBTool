package com.walk_nie.taobao.support;

import java.util.List;

import com.walk_nie.taobao.object.BaobeiPublishObject;


public abstract class BaseBaobeiParser {

	protected boolean updatePriceFlag = false;
	protected boolean updateTitleFlag = false;
	protected boolean updateOutIdFlag = false;
	protected boolean updateDescriptionFlag = false;
    
    public BaseBaobeiParser enablePriceUpdate(){
        this.updatePriceFlag = true;
        return this;
    }
    public BaseBaobeiParser enableTitleUpdate(){
        this.updateTitleFlag = true;
        return this;
    }
    public BaseBaobeiParser enableOutIdUpdate(){
        this.updateOutIdFlag = true;
        return this;
    }
    public BaseBaobeiParser enableDescriptionUpdate(){
        this.updateDescriptionFlag = true;
        return this;
    }
    
    protected List<BaobeiPublishObject> toUpdatebaobeiList;
    public BaseBaobeiParser setToUpdateBaobeiList(List<BaobeiPublishObject> baobeiList){
        toUpdatebaobeiList = baobeiList;
        return this;
    }
    
    protected List<BaobeiPublishObject> publishedbaobeiList;
    public BaseBaobeiParser setPublishedbaobeiList(List<BaobeiPublishObject> baobeiList){
    	publishedbaobeiList = baobeiList;
        return this;
    }
   
}
