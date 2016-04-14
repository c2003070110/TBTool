package com.walk_nie.taobao.support;

import java.util.List;

import com.walk_nie.taobao.object.BaobeiPublishObject;



public abstract class BaseBaobeiProducer {

	protected boolean updatePriceFlag = false;
	protected boolean updateTitleFlag = false;
	protected boolean updateOutIdFlag = false;
	protected boolean updateDescriptionFlag = false;
	protected List<BaobeiPublishObject> toUpdatebaobeiList = null;
    
    public BaseBaobeiProducer enablePriceUpdate(){
        this.updatePriceFlag = true;
        return this;
    }
    public BaseBaobeiProducer enableTitleUpdate(){
        this.updateTitleFlag = true;
        return this;
    }
    public BaseBaobeiProducer enableOutIdUpdate(){
        this.updateOutIdFlag = true;
        return this;
    }
    public BaseBaobeiProducer enableDescriptionUpdate(){
        this.updateDescriptionFlag = true;
        return this;
    }
    
    public BaseBaobeiProducer setToUpdateBaobeiList(List<BaobeiPublishObject> baobeiList){
        toUpdatebaobeiList = baobeiList;
        return this;
    }
    public abstract void process() ;
    public abstract BaseBaobeiParser getParser();
   
}
