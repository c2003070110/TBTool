package com.walk_nie.taobao.kakaku.explore.earphone;

import java.util.List;

import org.jsoup.helper.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.kakaku.KakakuBaobeiProceducer;
import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.kakaku.KakakuUtil;
import com.walk_nie.taobao.montBell.GoodsObject;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.BaobeiUtil;

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
        baobeiObj.price = EarphoneUtil.convertToCNY(kakakuObj,this.currencyRate,this.benefitRate);
    }
    
    protected void composeBaobeiMiaoshu(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj){
        // 宝贝描述
        StringBuffer detailSB = new StringBuffer();
        List<String> productInfo = kakakuObj.detailScreenShotPicFile;
        if(productInfo != null && !productInfo.isEmpty()){
            detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝说明</h3>");
            detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
            for(String str:productInfo){
            	detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;\" src=\"FILE:///" + str + "\"/></p>");
            }
            detailSB.append("</div>");
        }
        StringBuffer sizeTips = new StringBuffer();
//        if(!itemkakakuObj.sizeTipPics.isEmpty()){
//            detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">尺寸参考</h3>");
//            detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
//            detailSB.append("<p>下单前，请认真比对尺寸大小！<span style=\";color:red;font-weight:bold\">不能因为尺寸问题 不能取消订单！！不能退款！！！</span></p>");
//            for(String sizeTip:item.sizeTipPics){
//                detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;\" src=\"FILE:///" + sizeTip + "\"/></p>");    
//            }
//            detailSB.append("</div>");
//        }
        String extraMiaoshu = EarphoneUtil.getExtraMiaoshu();
        String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
        baobeiObj.description = "\"" + detailSB.toString() +sizeTips.toString()+ extraMiaoshu +extraMiaoshu1+ "\"";
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
        baobeiObj.barcode = "KKKU-" + kakakuObj.id;
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
            String price = EarphoneUtil.convertToCNY(colorObj,this.currencyRate,this.benefitRate);
            // 宝贝属性格式  1627207:28320;
            cateProps +="1627207:"+taobaoColors.get(i)+";";
            // 销售属性组合格式 价格:数量:SKU:1627207:28320;
            skuProps += price +":9999:"+colorObj.sku+":1627207:"+taobaoColors.get(i)+";";
            // 销售属性别名格式 1627207:28320:颜色1;
            //propAlias +="1627207:"+taobaoColors.get(i)+":" +Util.convertColor(color)+";";
            propAlias +="1627207:"+taobaoColors.get(i)+":" +KakakuUtil.convertColor(colorObj.colorName)+ "[K"+colorObj.id+"8]"+";";
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

	@Override
	protected void composeBaobeiMiaoshu(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void composeBaobeiCateProps(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void composeBaobeiSkuProps(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void composeBaobeiPropAlias(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void composeBaobeiPictureStatus(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void composeBaobeiPicture(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void composeBaobeiInputCustomCpv(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		// TODO Auto-generated method stub
		
	}
	 
}
