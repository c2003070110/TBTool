package com.walk_nie.taobao.montBell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.jsoup.helper.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MontbellBaobeiProducer extends BaseBaobeiProducer{
    
    private List<String> scanCategoryIds = Lists.newArrayList();
    private List<String> taobaoColors = Lists.newArrayList();
    {
        taobaoColors.add("-1001");taobaoColors.add("-1002");taobaoColors.add("-1003");
        taobaoColors.add("-1004");taobaoColors.add("-1005");taobaoColors.add("-1006");
        taobaoColors.add("-1007");taobaoColors.add("-1008");taobaoColors.add("-1009");
        taobaoColors.add("-1010");taobaoColors.add("-1011");taobaoColors.add("-1012");
    }
//    private List<String> taobaoSizes = Lists.newArrayList();
//    {
//        taobaoSizes.add("28381");taobaoSizes.add("28313");taobaoSizes.add("28314");
//        taobaoSizes.add("28315");taobaoSizes.add("28316");taobaoSizes.add("28317");
//        taobaoSizes.add("28319");
//    }
    private List<String> taobaoSizes = Lists.newArrayList();
    {
    	 // XS,S,M,L,XL
        taobaoSizes.add("28313");taobaoSizes.add("28314");
        taobaoSizes.add("28315");taobaoSizes.add("28316");taobaoSizes.add("28317");
    }
    
    
    public void process() {
        BufferedWriter priceBw = null;
        try {
            System.out.println("-------- START --------");
            List<GoodsObject> itemIdList = Lists.newArrayList();
            if(scanCategoryIds.isEmpty()){
                
            }else{
                itemIdList = new MontbellProductParser().scanItem(scanCategoryIds);    
            }
            if (itemIdList.isEmpty())
                return;
            String outFilePathPrice = String.format(outputFile, DateUtils
                    .formatDate(Calendar.getInstance().getTime(),
                            "yyyy_MM_dd_HH_mm_ss"));
            File csvFile = new File(outFilePathPrice);
            priceBw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(csvFile), "UTF-16"));

            priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
            
            for (GoodsObject obj : itemIdList) {
                MontBellUtil.downloadPicture(obj, MontBellUtil.rootPathName);
            }
            String taobaoPicFolder = TaobaoUtil.getPictureFolder(csvFile);
            for (GoodsObject obj : itemIdList) {
                TaobaoUtil.copyFiles(obj.pictureNameList,MontBellUtil.rootPathName, taobaoPicFolder);
                writeOut(priceBw, obj);
            }
            System.out.println("-------- FINISH--------");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (priceBw != null)
                try {
                    priceBw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    protected void writeOut(BufferedWriter priceBw, GoodsObject item)
            throws Exception {
        
        priceBw.write(composeBaobeiLine(item));
        priceBw.flush();
    }
    protected String composeBaobeiLine(GoodsObject item) throws Exception {
        BaobeiPublishObject obj = new BaobeiPublishObject();
        BaobeiUtil.setBaobeiCommonInfo(obj);

        // 宝贝名称
        composeBaobeiTitle(item,obj);
        // 宝贝类目
        composeBaobeiTaobaoCategory(item,obj);
        // 店铺类目
        composeBaobeiMyCategory(item, obj);
        // 省
        obj.location_state = "日本";
        // 宝贝价格
        obj.price = MontBellUtil.convertToCNYWithEmsFee(item,this.currencyRate,this.benefitRate);
        //obj.price = item.priceCNY;
        // 宝贝数量
        obj.num = "999";
        
        // 用户输入ID串;
        obj.inputPids = "\"20000,13021751,6103476\"";
        // ダウンジャケット
        obj.inputPids = "\"20000,13021751,6103476,1627207\"";
        
        // 用户输入名-值对
        obj.inputValues = "\"montbell,"+item.productId+",*\"";
        // ダウンジャケット MONTBELL,1101464,1234,GRL;颜色分类;GML
        obj.inputValues = "\"montbell,"+item.productId+",1234\"";
        
        // 宝贝描述
        obj.description = composeBaobeiMiaoshu(item);

        // 宝贝属性
        composeBaobeiCateProps(item, obj);
        // 销售属性组合
        composeBaobeiSkuProps(item, obj);
        // 商家编码
        obj.outer_id = "MTBL_" + item.cateogryObj.categoryId + "-" + item.productId;
        // 销售属性别名
        composeBaobeiPropAlias(item, obj);
        // 商品条形码
        //obj.barcode = item.sku;
		// 图片状态
		MontBellUtil.composeBaobeiPictureStatus(item,obj,taobaoColors);
		// 新图片
		MontBellUtil.composeBaobeiPicture(item,obj,taobaoColors);
        // 自定义属性值
        composeBaobeiInputCustomCpv(item, obj);
        // 宝贝卖点
        composeBaobeiSubtitle(item, obj);
        // 库存计数
        obj.sub_stock_type = "1";
        
        // 增加商品资质
        obj.add_qualification = "1";

        return TaobaoUtil.composeTaobaoLine(obj);
    }

    protected void composeBaobeiCateProps(GoodsObject item, BaobeiPublishObject obj) {
        // cateProps　宝贝属性：1627207:-1001;1627207:-1002;1627207:-1003;1627207:-1004;1627207:-1005;1627207:-1006;1627207:-1007;1627207:-1008;1627207:-1009;20509:28381;20509:28313;20509:28314;20509:28315;20509:28316;20509:28317;20509:28319
        String prodCId = item.cateogryObj.categoryId;
        String cateProps = "";
//        if (MontBellUtil.isCateogrySoftShell1(prodCId)
//                || MontBellUtil.isCateogryHardShell1(prodCId)
//                || MontBellUtil.isCateogrySoftShell2(prodCId)
//                || MontBellUtil.isCateogryHardShell2(prodCId)) {
//            // 冲锋衣 冲锋裤
//            cateProps += "20021:20213;122216816:20213;";
//        }
//        cateProps += "122216608:29923;";
        // ダウンジャケット
        cateProps += "20000:6217823;13021751:61043120;6103476:3231061;122216608:29923;21548:38488;";
        // 宝贝属性
//        for(int i =0;i<item.colorList.size();i++){
//            if(i>=taobaoColors.size())break;
//            cateProps +="1627207:"+taobaoColors.get(i)+";";
//        }
//        for(int i =0;i<item.sizeList.size();i++){
//            if(i>=taobaoSizes.size())break;
//            cateProps +="20509:"+taobaoSizes.get(i)+";";
//        }
        obj.cateProps =cateProps;
    }

    protected void composeBaobeiSkuProps(GoodsObject item, BaobeiPublishObject obj) {
        // skuProps　销售属性组合：0:0::1627207:-1001;20509:28381;0:0::1627207:-1001;20509:28313;0:0::1627207:-1001;20509:28314;0:0::1627207:-1001;20509:28315;0:0::1627207:-1001;20509:28316;0:0::1627207:-1001;20509:28317;0:0::1627207:-1001;20509:28319;0:0::1627207:-1002;20509:28381;0:0::1627207:-1002;20509:28313;0:0::1627207:-1002;20509:28314;0:0::1627207:-1002;20509:28315;0:0::1627207:-1002;20509:28316;0:0::1627207:-1002;20509:28317;0:0::1627207:-1002;20509:28319;0:0::1627207:-1003;20509:28381;0:0::1627207:-1003;20509:28313;0:0::1627207:-1003;20509:28314;0:0::1627207:-1003;20509:28315;0:0::1627207:-1003;20509:28316;0:0::1627207:-1003;20509:28317;0:0::1627207:-1003;20509:28319;0:0::1627207:-1004;20509:28381;0:0::1627207:-1004;20509:28313;0:0::1627207:-1004;20509:28314;0:0::1627207:-1004;20509:28315;0:0::1627207:-1004;20509:28316;0:0::1627207:-1004;20509:28317;0:0::1627207:-1004;20509:28319;0:0::1627207:-1005;20509:28381;0:0::1627207:-1005;20509:28313;0:0::1627207:-1005;20509:28314;0:0::1627207:-1005;20509:28315;0:0::1627207:-1005;20509:28316;0:0::1627207:-1005;20509:28317;0:0::1627207:-1005;20509:28319;0:0::1627207:-1006;20509:28381;0:0::1627207:-1006;20509:28313;0:0::1627207:-1006;20509:28314;0:0::1627207:-1006;20509:28315;0:0::1627207:-1006;20509:28316;0:0::1627207:-1006;20509:28317;0:0::1627207:-1006;20509:28319;0:0::1627207:-1007;20509:28381;0:0::1627207:-1007;20509:28313;0:0::1627207:-1007;20509:28314;0:0::1627207:-1007;20509:28315;0:0::1627207:-1007;20509:28316;0:0::1627207:-1007;20509:28317;0:0::1627207:-1007;20509:28319;0:0::1627207:-1008;20509:28381;0:0::1627207:-1008;20509:28313;0:0::1627207:-1008;20509:28314;0:0::1627207:-1008;20509:28315;0:0::1627207:-1008;20509:28316;0:0::1627207:-1008;20509:28317;0:0::1627207:-1008;20509:28319;0:0::1627207:-1009;20509:28381;0:0::1627207:-1009;20509:28313;0:0::1627207:-1009;20509:28314;0:0::1627207:-1009;20509:28315;0:0::1627207:-1009;20509:28316;0:0::1627207:-1009;20509:28317;0:0::1627207:-1009;20509:28319;
        String skuProps = "";
//        for (int i = 0; i < item.colorList.size(); i++) {
//            if(i>=taobaoColors.size())break;
//            for (int j = 0; j < item.sizeList.size(); j++) {
//                if(j>=taobaoSizes.size())break;
////                skuProps += obj.price + ":9999" + ":" + ":1627207" + ":" + taobaoColors.get(i)
////                        + ";20509:" + taobaoSizes.get(j) + ";";
//                skuProps += "20509:" + taobaoSizes.get(j) +":"+ obj.price + ":9999" + ":" + ":1627207" + ":" + taobaoColors.get(i)
//                        +  ";";
//            }
//        }
        obj.skuProps =skuProps;
    }

    protected void composeBaobeiPropAlias(GoodsObject item, BaobeiPublishObject obj) {
        // propAlias　销售属性别名：20509:28381:size1;20509:28313:size2;20509:28314:size3;20509:28315:size4;20509:28316:size5;20509:28317:size6;20509:28319:size7
//        String propAlias = "";
//        // 销售属性别名
//        for(int i =0;i<item.sizeList.size();i++){
//            if(i>=taobaoSizes.size())break;
//            propAlias +="20509:"+taobaoSizes.get(i)+":" +item.sizeList.get(i)+";";
//        }
//        obj.propAlias =propAlias;
    }

//    private void composeBaobeiPictureStatus(GoodsObject item, BaobeiPublishObject obj) {
//        String picStatus = "";
//        // picture_status 图片状态：2;2;2;2;2;2;2;2;2;2;
//        // 宝贝主图 main picture
//        for(int i=0;i<item.pictureNameList.size();i++){
//            if(i==5) break;
//            picStatus +="2;";
//        }
//        // 销售属性图片
//        for (int i = 0; i < item.colorList.size(); i++) {
//            if(i>=taobaoColors.size())break;
//            if(item.pictureNameList.size() == item.colorList.size()){
//                // color picture
//                picStatus +="2;";
//            }
//        }
//        obj.picture_status =picStatus;
//    }
//
//    private void composeBaobeiPicture(GoodsObject item, BaobeiPublishObject obj) {
//        String picture = "";
//        // picture　新图片：1128533_dkfs:1:0:|;1128533_mst:1:1:|;1128533_scl:1:2:|;1128533_tq:1:3:|;1128533_umr:1:4:|;1128533_dkfs:2:0:1627207:28320|;1128533_mst:2:0:1627207:28340|;1128533_scl:2:0:1627207:3232479|;1128533_tq:2:0:1627207:3232478|;1128533_umr:2:0:1627207:3232482|;
//        // 宝贝主图 main picture
//        for(int i=0;i<item.pictureNameList.size();i++){
//            if(i==5) break;
//            picture += item.pictureNameList.get(i) + ":1:" + i +":|;";
//        }
//        // 销售属性图片
//        for (int i = 0; i < item.colorList.size(); i++) {
//            if(i>=taobaoColors.size())break;
//            if(item.pictureNameList.size() == item.colorList.size()){
//                // color picture
//                picture += item.pictureNameList.get(i) + ":2:0:1627207:" + taobaoColors.get(i) +"|;";
//            }
//        }
//
//        obj.picture = picture;
//    }

    protected void composeBaobeiInputCustomCpv(GoodsObject item, BaobeiPublishObject obj) {
        String inputCustomCpv="";
        // 自定义属性值
        for(int i =0;i<item.colorList.size();i++){
            if(i>=taobaoColors.size())break;
            //1627207:-1001:color1;
            inputCustomCpv += "1627207:" + taobaoColors.get(i)  +":"+item.colorList.get(i)+";"; 
        }
        obj.input_custom_cpv =inputCustomCpv;
    }

    private void composeBaobeiTaobaoCategory(GoodsObject item,
            BaobeiPublishObject baobei) {
        String categoryId = baobei.cid;
        String prodCId = item.cateogryObj.categoryId;
        // TODO
        if (MontBellUtil.isCateogryDownClothes(prodCId) ) {
        	//羽绒衣|裤
            categoryId = "50014798";
        }
        if (MontBellUtil.isCateogryRainClothes(prodCId) ) {
            // レインウェア 户外雨衣
            categoryId = "124208012";
        }
        if (MontBellUtil.isCateogrySoftShell1(prodCId)
                || MontBellUtil.isCateogryHardShell1(prodCId)) {
            // ソフトシェルジャケット + ハードシェル>ジャケット 冲锋衣
            categoryId = "50014785";
        }
        if (MontBellUtil.isCateogrySoftShell2(prodCId)
                || MontBellUtil.isCateogryHardShell2(prodCId)) {
            // ソフトシェルパンツ + ハードシェル>パンツ 冲锋裤
            categoryId = "50014785";
        }
        if (MontBellUtil.isCateogryFreece1(prodCId) ) {
            // フリース 抓绒衣 抓绒衣
            categoryId = "50014787";
        }
        if (MontBellUtil.isCateogryFreece2(prodCId) ) {
            // フリースパンツ 抓绒裤 
            categoryId = "50014787";
        } 
        if (MontBellUtil.isCateogryTShirt(prodCId) ) {
            // Tシャツ（半袖/長袖）
            categoryId = "50013932";
        } 
        if (MontBellUtil.isCateogryPack(prodCId) ) {
            // 大型ザック
            categoryId = "";
        } 
        baobei.cid =  categoryId;
    }

    private void composeBaobeiMyCategory(GoodsObject item,
            BaobeiPublishObject baobei) {

        String categoryId = baobei.seller_cids;
        String prodCId = item.cateogryObj.categoryId;
        // TODO
        if (MontBellUtil.isCateogryDownClothes(prodCId) ) {
        	//羽绒衣|裤
            categoryId = "1184361986";
        }
        
        if (MontBellUtil.isCateogryRainClothes(prodCId) ) {
            // レインウェア 户外雨衣
            categoryId = "1184361988";
        }
        if (MontBellUtil.isCateogrySoftShell1(prodCId)
                || MontBellUtil.isCateogryHardShell1(prodCId)) {
            // ソフトシェルジャケット + ハードシェル>ジャケット 冲锋衣
            categoryId = "1184361987";
        }
        if (MontBellUtil.isCateogrySoftShell2(prodCId)
                || MontBellUtil.isCateogryHardShell2(prodCId)) {
            // ソフトシェルパンツ + ハードシェル>パンツ 冲锋裤
            categoryId = "1184361987";
        }
        if (MontBellUtil.isCateogryFreece1(prodCId) ) {
            // フリース 抓绒衣 抓绒衣
            categoryId = "1184361987";
        }
        if (MontBellUtil.isCateogryFreece2(prodCId) ) {
            // フリースパンツ 抓绒裤 
            categoryId = "1184361987";
        } 
        if (MontBellUtil.isCateogryTShirt(prodCId) ) {
            // Tシャツ（半袖/長袖）
            categoryId = "1184361987";
        } 
        if (MontBellUtil.isCateogryPack(prodCId) ) {
            // 大型ザック
            categoryId = "";
        } 
        baobei.seller_cids =  categoryId;
    }

    private void composeBaobeiSubtitle(GoodsObject item,BaobeiPublishObject baobei) {
        baobei.subtitle =  "\"日本直邮！100%正品！真正的日本代购！包邮！" + item.titleOrg + "\"";
    }
    private void composeBaobeiTitle(GoodsObject item,
            BaobeiPublishObject baobei) {
        String title = "\"日本直邮";
        title += " " + item.titleCN;
        title += " mont-bell #" + item.productId;
        if(item.gender != null && !item.gender.equals("")){
            title += " " + item.gender;
        }
//        String suffix = "/包邮";
//        if (title.length() + suffix.length() < 60) {
//            title += suffix;
//        }
        baobei.title =  title + "\"";
    }
    
    private  String composeBaobeiMiaoshu(GoodsObject item) throws IOException {
        
        StringBuffer detailSB = new StringBuffer();
        String productInfo = item.detailScreenShotPicFile;
        if(!StringUtil.isBlank(item.detailScreenShotPicFile)){
            detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝说明</h3>");
            detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
            detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;\" src=\"FILE:///" + productInfo + "\"/></p>");
            detailSB.append("</div>");
        }
        StringBuffer sizeTips = new StringBuffer();
        if(!item.sizeTipPics.isEmpty()){
            detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">尺寸参考</h3>");
            detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
            detailSB.append("<p>下单前，请认真比对尺寸大小！<span style=\";color:red;font-weight:bold\">不能因为尺寸问题 不能取消订单！！不能退款！！！</span></p>");
            for(String sizeTip:item.sizeTipPics){
                detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;\" src=\"FILE:///" + sizeTip + "\"/></p>");    
            }
            detailSB.append("</div>");
        }
        //String extraMiaoshu = MontBellUtil.composeExtraMiaoshu();
        String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
        return "\"" + detailSB.toString() +sizeTips.toString() +extraMiaoshu1+ "\"";
    }

    public MontbellBaobeiProducer addScanCategory(String scanCategoryId) {

        this.scanCategoryIds.add(scanCategoryId);
        return this;
    }

    @Override
    public BaseBaobeiParser getParser() {
        return new MontbellProductParser();
    }

}
