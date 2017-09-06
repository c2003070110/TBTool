package com.walk_nie.taobao.montBell.clothes;

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
import com.walk_nie.taobao.montBell.GoodsObject;
import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.montBell.MontbellProductParser;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MontbellSyntheticBaobeiProducer extends BaseBaobeiProducer{
    
    private List<String> scanCategoryIds = Lists.newArrayList();
    private List<String> taobaoColors = Lists.newArrayList();
    {
        taobaoColors.add("-1001");taobaoColors.add("-1002");taobaoColors.add("-1003");
        taobaoColors.add("-1004");taobaoColors.add("-1005");taobaoColors.add("-1006");
        taobaoColors.add("-1007");taobaoColors.add("-1008");taobaoColors.add("-1009");
        taobaoColors.add("-1010");taobaoColors.add("-1011");taobaoColors.add("-1012");
//        taobaoColors.add("3741675");taobaoColors.add("3574624");taobaoColors.add("3579132");
//        taobaoColors.add("3614432");taobaoColors.add("28635738");taobaoColors.add("28635737");
//        taobaoColors.add("7928402");taobaoColors.add("85186009");taobaoColors.add("366446425");
    }
//    private List<String> taobaoSizes = Lists.newArrayList();
//    {
//        taobaoSizes.add("28381");taobaoSizes.add("28313");taobaoSizes.add("28314");
//        taobaoSizes.add("28315");taobaoSizes.add("28316");taobaoSizes.add("28317");
//        taobaoSizes.add("28319");
//    }
    private List<String> taobaoSizes = Lists.newArrayList();
    {
    	 // XS,S,M,L,XL,XXL,
        taobaoSizes.add("28313");taobaoSizes.add("28314");taobaoSizes.add("28315");
        taobaoSizes.add("28316");taobaoSizes.add("28317");taobaoSizes.add("28318");
    }
    
    
    public void process() {
        BufferedWriter priceBw = null;
        try {
            System.out.println("-------- START --------");
            List<GoodsObject> itemIdList = Lists.newArrayList();
            if(scanCategoryIds.isEmpty()){
                
            }else{
            	MontbellProductParser parer = new MontbellProductParser();
            	parer.setPublishedbaobeiList(this.publishedbaobeiList);
                itemIdList = parer.scanItem(scanCategoryIds);    
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
				MontBellUtil.downloadPicture(obj, MontBellUtil.rootPathName
						+ "/" + obj.cateogryObj.categoryId);
			}
			String taobaoPicFolder = TaobaoUtil.getPictureFolder(csvFile);
			for (GoodsObject obj : itemIdList) {
				TaobaoUtil.copyFiles(obj.pictureNameList,
						MontBellUtil.rootPathName + "/"
								+ obj.cateogryObj.categoryId, taobaoPicFolder);
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
        
		BaobeiPublishObject publishedBaobei = MontBellUtil.getPublishedBaobei(
				item, this.publishedbaobeiList);

		if (publishedBaobei != null) {
			obj.title = "\"" + publishedBaobei.title + "\"";
		} else {
			// 宝贝名称
			composeBaobeiTitle(item, obj);
		}
        // 宝贝类目 TODO
        obj.cid =  "50014798";
        // 店铺类目
        obj.seller_cids =  "1339722359";
        // 省
        obj.location_state = "\"日本\"";
        // 宝贝价格
        obj.price = MontBellUtil.convertToCNYNoneEMSFee(item,this.currencyRate,this.benefitRate);
        //obj.price = item.priceCNY;
        // 宝贝数量
        obj.num = "9999";
		if (publishedBaobei != null) {
			// 数字ID 宝贝ID
			obj.num_id = "\"" + publishedBaobei.num_id+ "\"" ;
		}
		
        // 邮费模版ID 全场90包邮
        obj.postage_id = "1780373930";
        
        // 用户输入ID串;
        obj.inputPids = "\"20000,13021751,6103476\"";
        // ダウンジャケット
        obj.inputPids = "\"20000,13021751,6103476,1627207\"";
        
        // 用户输入名-值对
        //obj.inputValues = "\"montbell,"+item.productId+",*\"";
        composeBaobeiInputValues(item, obj);
        
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
        MontBellUtil.composeBaobeiSubtitle(item, obj);
        // 库存计数
        obj.sub_stock_type = "1";
        // 商品资质
        obj.qualification = "%7B%7D";
        // 增加商品资质
        obj.add_qualification = "1";

        return TaobaoUtil.composeTaobaoLine(obj);
    }

    private    void composeBaobeiTitle(GoodsObject item,
            BaobeiPublishObject baobei) {
        String title = "\"日本直邮 MontBell";
        //title += " " + item.titleCN ;
        String cateId = item.cateogryObj.categoryId;
        if("139000".equals(cateId)){
        	// 化繊綿ジャケット
        	title += " 化棉茄克" ;
        }else if("139500".equals(cateId)){
        	//化繊綿ジャケット（軽量シリーズ）
        	title += " 超轻化棉茄克" ;
        }else if("139700".equals(cateId)){
        	//化繊綿ベスト
        	title += " 化棉背心" ;
        }else if("133000".equals(cateId)){
        	//化繊綿パンツ
        	title += " 化棉裤" ;
        }else if("133500".equals(cateId)){
        	//化繊綿スカート
        	title += " 化棉群" ;
        }
        if(!StringUtil.isBlank(item.titleEn)){
            title += " " + item.titleEn ;
        }
        title += " " + item.productId;
        if(!StringUtil.isBlank(item.gender)){
            title += " " + item.gender;
        }
//        String suffix = "/包邮";
//        if (title.length() + suffix.length() < 60) {
//            title += suffix;
//        }
        baobei.title =  title + "\"";
    }
	private void composeBaobeiCateProps(GoodsObject item, BaobeiPublishObject obj) {
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
        // freece
        //cateProps += "20000:6217823;13021751:3262315;6103476:3231061;122216608:29923;21548:38488;";
        
        // 宝贝属性
        for(int i =0;i<item.colorList.size();i++){
            if(i>=taobaoColors.size())break;
            cateProps +="1627207:"+taobaoColors.get(i)+";";
        }
        for(int i =0;i<item.sizeList.size();i++){
            if(i>=taobaoSizes.size())break;
            cateProps +="20509:"+taobaoSizes.get(i)+";";
        }
        obj.cateProps =cateProps;
    }

    private void composeBaobeiSkuProps(GoodsObject item, BaobeiPublishObject obj) {
        // skuProps　销售属性组合：0:0::1627207:-1001;20509:28381;0:0::1627207:-1001;20509:28313;0:0::1627207:-1001;20509:28314;0:0::1627207:-1001;20509:28315;0:0::1627207:-1001;20509:28316;0:0::1627207:-1001;20509:28317;0:0::1627207:-1001;20509:28319;0:0::1627207:-1002;20509:28381;0:0::1627207:-1002;20509:28313;0:0::1627207:-1002;20509:28314;0:0::1627207:-1002;20509:28315;0:0::1627207:-1002;20509:28316;0:0::1627207:-1002;20509:28317;0:0::1627207:-1002;20509:28319;0:0::1627207:-1003;20509:28381;0:0::1627207:-1003;20509:28313;0:0::1627207:-1003;20509:28314;0:0::1627207:-1003;20509:28315;0:0::1627207:-1003;20509:28316;0:0::1627207:-1003;20509:28317;0:0::1627207:-1003;20509:28319;0:0::1627207:-1004;20509:28381;0:0::1627207:-1004;20509:28313;0:0::1627207:-1004;20509:28314;0:0::1627207:-1004;20509:28315;0:0::1627207:-1004;20509:28316;0:0::1627207:-1004;20509:28317;0:0::1627207:-1004;20509:28319;0:0::1627207:-1005;20509:28381;0:0::1627207:-1005;20509:28313;0:0::1627207:-1005;20509:28314;0:0::1627207:-1005;20509:28315;0:0::1627207:-1005;20509:28316;0:0::1627207:-1005;20509:28317;0:0::1627207:-1005;20509:28319;0:0::1627207:-1006;20509:28381;0:0::1627207:-1006;20509:28313;0:0::1627207:-1006;20509:28314;0:0::1627207:-1006;20509:28315;0:0::1627207:-1006;20509:28316;0:0::1627207:-1006;20509:28317;0:0::1627207:-1006;20509:28319;0:0::1627207:-1007;20509:28381;0:0::1627207:-1007;20509:28313;0:0::1627207:-1007;20509:28314;0:0::1627207:-1007;20509:28315;0:0::1627207:-1007;20509:28316;0:0::1627207:-1007;20509:28317;0:0::1627207:-1007;20509:28319;0:0::1627207:-1008;20509:28381;0:0::1627207:-1008;20509:28313;0:0::1627207:-1008;20509:28314;0:0::1627207:-1008;20509:28315;0:0::1627207:-1008;20509:28316;0:0::1627207:-1008;20509:28317;0:0::1627207:-1008;20509:28319;0:0::1627207:-1009;20509:28381;0:0::1627207:-1009;20509:28313;0:0::1627207:-1009;20509:28314;0:0::1627207:-1009;20509:28315;0:0::1627207:-1009;20509:28316;0:0::1627207:-1009;20509:28317;0:0::1627207:-1009;20509:28319;
        String skuProps = "";
        for (int i = 0; i < item.colorList.size(); i++) {
            if(i>=taobaoColors.size())break;
            for (int j = 0; j < item.sizeList.size(); j++) {
                if(j>=taobaoSizes.size())break;
                String num  = MontBellUtil.getStock(item, item.colorList.get(i),
                		item.sizeList.get(j));
                skuProps += obj.price + ":" +  num  + ":" + ":1627207" + ":" + taobaoColors.get(i)
                        + ";20509:" + taobaoSizes.get(j) + ";";
//                skuProps += "20509:" + taobaoSizes.get(j) +":"+ obj.price + ":9999" + ":" + ":1627207" + ":" + taobaoColors.get(i)
//                        +  ";";
            }
        }
        obj.skuProps =skuProps;
    }

	private void composeBaobeiInputValues(GoodsObject item,
			BaobeiPublishObject obj) {
        // ダウンジャケット MONTBELL,1101464,1234,GRL;颜色分类;GML
        String inputValues = "\"montbell,"+item.productId+","+obj.price+",";
        for(int i =0;i<item.colorList.size();i++){
            if(i>=taobaoColors.size())break;
            inputValues +=item.colorList.get(i) +  ";颜色分类;";
        }
        obj.inputValues = inputValues+"\"";
	}

    private void composeBaobeiPropAlias(GoodsObject item, BaobeiPublishObject obj) {
        // propAlias　销售属性别名：20509:28381:size1;20509:28313:size2;20509:28314:size3;20509:28315:size4;20509:28316:size5;20509:28317:size6;20509:28319:size7
        String propAlias = "";
        // 销售属性别名
        for(int i =0;i<item.sizeList.size();i++){
            if(i>=taobaoSizes.size())break;
            propAlias +="20509:"+taobaoSizes.get(i)+":" +item.sizeList.get(i)+";";
        }
        obj.propAlias =propAlias;
    }

    private void composeBaobeiInputCustomCpv(GoodsObject item, BaobeiPublishObject obj) {
        String inputCustomCpv="";
        // 自定义属性值
        for(int i =0;i<item.colorList.size();i++){
            if(i>=taobaoColors.size())break;
            //1627207:-1001:color1;
            inputCustomCpv += "1627207:" + taobaoColors.get(i)  +":"+item.colorList.get(i)+";"; 
        }
        obj.input_custom_cpv =inputCustomCpv;
    }
    
    private  String composeBaobeiMiaoshu(GoodsObject item) throws IOException {
        StringBuffer detailSB = new StringBuffer();
        /*
        detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">拼邮包税</h3>");
        detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
        detailSB.append("<p style=\"text-indent:2.0em;\">鉴于国内海关趋于严厉，本店提供拼邮包税</p>");
        detailSB.append("<p style=\"text-indent:2.0em;\">拼邮包税，是指，您下单后，和被人的订单一起，通过包税渠道运回国内，再国内快递到您手</p>");
        detailSB.append("<p style=\"text-indent:2.0em;\">费用，根据您的地址，收费有所不同，请咨询。</p>");
        detailSB.append("<p style=\"text-indent:2.0em;\">不拼单包税的订单，<span style=\";color:red;font-weight:bold\">如发生关税，报关等由您处理，关税由您承担。</span></p>");
        detailSB.append("<p style=\"text-indent:2.0em;\"> 拼单包税的订单，<span style=\";color:red;font-weight:bold\">报关，关税等都有我来处理，承担！</span></p>");
        detailSB.append("</div>");
        */
        // 包邮
        detailSB.append(MontBellUtil.getBaoyouMiaoshu());
        
        // 宝贝描述
        detailSB.append(MontBellUtil.getProductInfoMiaoshu(item.detailScreenShotPicFile));
        // 着装图片
        detailSB.append(MontBellUtil.getDressOnMiaoshu(item.dressOnPics));

        // 尺寸描述
        detailSB.append(MontBellUtil.getSizeTipMiaoshu(item.sizeTipPics));
        
        String extraMiaoshu = MontBellUtil.getExtraMiaoshu();
        String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
        return "\"" + detailSB.toString() + extraMiaoshu +extraMiaoshu1+ "\"";
    }

    public MontbellSyntheticBaobeiProducer addScanCategory(String scanCategoryId) {

        this.scanCategoryIds.add(scanCategoryId);
        return this;
    }

    @Override
    public BaseBaobeiParser getParser() {
        return new MontbellProductParser();
    }

}
