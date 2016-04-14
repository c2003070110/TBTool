package com.walk_nie.taobao.kakaku.explore.earphone;

import java.util.List;

import org.jsoup.helper.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.kakaku.KakakuBaobeiProceducer;
import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;

public class EarphoneBaobeiProducer extends KakakuBaobeiProceducer  {

    protected void composeBaobeiTitle(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 宝贝名称
        SpecObject spec = (SpecObject)kakakuObj.spec;
        String title = "\"东京直邮/";
        title += kakakuObj.itemMaker + " " + kakakuObj.itemType;
        if (!StringUtil.isBlank(spec.setType)) {
            title += "/" + spec.setType;
        }
        //if (!StringUtil.isBlank(spec.driverType)) {
        //  title += "/" + spec.driverType;
        //}
        String suffix = "/日行耳机";
        if (title.length() + suffix.length() < 60) {
            title += suffix;
        }
        suffix = "/包邮";
        if (title.length() + suffix.length() < 60) {
            title += suffix;
        }
        title = title + "\"";
        baobeiObj.title = title;
    }

    protected void composeBaobeiPrice(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 宝贝价格
        baobeiObj.price = EarphoneUtil.convertToCNY(kakakuObj);
    }
    
    protected void composeBaobeiMiaoshu(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 宝贝描述
    }
    
    protected void composeBaobeiCateProps(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        //宝贝属性
        String[] vals = composeBaobeiPropColor(baobeiObj,kakakuObj);
        baobeiObj.cateProps = vals[0];
    }
    
    protected void composeBaobeiSkuProps(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 销售属性组合
        String[] vals = composeBaobeiPropColor(baobeiObj,kakakuObj);
        baobeiObj.cateProps = vals[1];
    }
    
    protected void composeBaobeiOutId(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 商家编码
        baobeiObj.barcode = "KKKU_" + kakakuObj.id;
    }
    
    protected void composeBaobeiPropAlias(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 销售属性别名
        String[] vals = composeBaobeiPropColor(baobeiObj,kakakuObj);
        baobeiObj.cateProps = vals[3];
    }
    
    protected void composeBaobeiBarCode(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 商品条形码
    }
    
    protected void composeBaobeiMaidian(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 宝贝卖点
        SpecObject spec = (SpecObject)kakakuObj.spec;
        String str ="\"";
        if(!StringUtil.isBlank(spec.setType)){
            str += spec.setType +"!";
        }
        if(!StringUtil.isBlank(spec.driverType)){
            str += spec.driverType +"!";
        }
        if(!StringUtil.isBlank(spec.wirelessType)){
            str += "无线耳机!";
        }
        if(spec.hasMicro){
            str += "有麦!";
        }
        str +="包邮！东京直邮！100%正品！日本行货！";
        baobeiObj.subtitle = str +"\"";
    
    }
    
    protected void composeBaobeiPictureStatus(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 图片状态
    }
    
    protected void composeBaobeiPicture(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 新图片
    }

    @Override
    public BaseBaobeiParser getParser() {
        return new EarphoneBaobeiParser();
    }
    private List<String> taobaoColors = Lists.newArrayList();
    {
        taobaoColors.add("28320");taobaoColors.add("28324");taobaoColors.add("28326");
        taobaoColors.add("28327");taobaoColors.add("28329");taobaoColors.add("28332");
        taobaoColors.add("28340");taobaoColors.add("28338");taobaoColors.add("28335");
    }
    private String[] composeBaobeiPropColor(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj) {
        int i = 0;
        // 颜色值:28320 28324 28326 28327 28329 28332 28340 28338 28335
        // 宝贝属性 -销售属性组合- 销售属性别名
        String cateProps = "";String skuProps = "";String propAlias = "";
        cateProps += getBaobeiCategory(kakakuObj);
        for(KakakuObject colorObj :kakakuObj.colorList){
            if(i>=taobaoColors.size())break;
            // 宝贝属性格式  1627207:28320;
            cateProps +="1627207:"+taobaoColors.get(i)+";";
            // 销售属性组合格式 价格:数量:SKU:1627207:28320;
            skuProps += EarphoneUtil.convertToCNY(colorObj) +":9999:"+colorObj.sku+":1627207:"+taobaoColors.get(i)+";";
            // 销售属性别名格式 1627207:28320:颜色1;
            //propAlias +="1627207:"+taobaoColors.get(i)+":" +Util.convertColor(color)+";";
            propAlias +="1627207:"+taobaoColors.get(i)+":" +EarphoneUtil.convertColor(colorObj.colorName)+ "[K"+colorObj.id+"8]"+";";
            i++;
            
        }
        return new String[]{"\""+cateProps+"\"","\""+skuProps+"\"" ,"\""+propAlias+"\"" };
    }

    private String getBaobeiCategory(KakakuObject item) {
        if("Sony/索尼".equals(item.itemMaker)){
            return "20000:10752;";
        }
        if("Audio Technica/铁三角".equals(item.itemMaker)){
            return "20000:21980;";
        }
        if("JVC/杰伟世".equals(item.itemMaker)){
            //return "20000:27287;";
            return "20000:58615113;";
        }
        if("Pioneer/先锋".equals(item.itemMaker)){
            return "20000:20804;";
        }
        if("Yamaha/雅马哈".equals(item.itemMaker)){
            return "20000:27207;";
        }
        if("Philips/飞利浦".equals(item.itemMaker)){
            return "20000:10246;";
        }
        if("DENON/天龙".equals(item.itemMaker)){
            return "20000:27269;";
        }
        if("Onkyo/安桥".equals(item.itemMaker)){
            return "20000:27273;";
        }
        if("Panasonic/松下".equals(item.itemMaker)){
            return "20000:81147;";
        }
        return "";
    }
	 
}
