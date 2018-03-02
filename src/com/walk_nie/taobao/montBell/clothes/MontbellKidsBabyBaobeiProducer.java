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

public class MontbellKidsBabyBaobeiProducer extends BaseBaobeiProducer{
    
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
            	MontbellProductParser parser = new MontbellProductParser();
            	parser.setPublishedbaobeiList(this.publishedbaobeiList);
                itemIdList = parser.scanItem(scanCategoryIds);    
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
		// 宝贝名称
		composeBaobeiTitle(item, obj);
        // 宝贝类目
		composeBaobeiCID(item, obj);
        // 店铺类目
        obj.seller_cids =  "1351890335";
        // 省
        obj.location_state = "\"日本\"";
        // 宝贝价格
        obj.price = MontBellUtil.convertToCNYNoneEMSFee(item,this.currencyRate,this.benefitRate);
        //obj.price = item.priceCNY;
        // 宝贝数量
        obj.num = "9999";
		
        // 邮费模版ID
        obj.postage_id = "7908392660";
        
        // 用户输入ID串;
        //obj.inputPids = "\"20000,13021751,6103476\"";
        // ダウンジャケット
        //obj.inputPids = "\"20000,13021751,6103476,1627207\"";
		obj.inputPids = "\"13021751,6103476,1627207\"";
        
        // 用户输入名-值对
        //obj.inputValues = "\"montbell,"+item.productId+",*\"";
        composeBaobeiInputValues(item, obj);
        
        // 宝贝描述
        composeBaobeiMiaoshu(item, obj);

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
		composeBaobeiPictureStatus(item,obj);
		// 新图片
		composeBaobeiPicture(item,obj);
        // 自定义属性值
        composeBaobeiInputCustomCpv(item, obj);
        // 宝贝卖点
        MontBellUtil.composeBaobeiSubtitle(item, obj);
        // 库存计数
        obj.sub_stock_type = "1";
        
        // 闪电发货
        obj.is_lighting_consigment = "80";
        // 新品
        obj.is_xinpin = "247";
        
        // 商品资质
        obj.qualification = "%7B%7D";
        // 增加商品资质
        obj.add_qualification = "0";

        return TaobaoUtil.composeTaobaoLine(obj);
    }

	private void composeBaobeiCID(GoodsObject item, BaobeiPublishObject baobei) {
        String cid = "";
        String cateId = item.cateogryObj.categoryId;
        if("159000".equals(cateId)){
        	// スノースポーツ（キッズ）
        	cid += "50014799";
        }else if("153000".equals(cateId)){
        	//ダウン/化繊綿入り（キッズ）
        	cid += "50014798";
        }else if("152000".equals(cateId)){
        	//ジャケット/ベスト（キッズ）
        	cid += "50014785";
        }else if("154000".equals(cateId)){
        	// フリース（キッズ）
        	cid += "50014787";
        }else if("157000".equals(cateId)){
        	// 帽子（キッズ）
        	cid += "50014493";
        }else if("173700".equals(cateId)){
        	// スノースポーツ（ベビー） 
        	cid += "50014799";
        }else if("173000".equals(cateId)){
        	// ジャケット/ベスト（ベビー）
        	cid += "50014785";
        }else if("171000".equals(cateId)){
        	// 雨具（ベビー） 
        	cid += "50014785";
        }else if("173500".equals(cateId)){
        	// フリース（ベビー）
        	cid += "50014787";
        }else if("175000".equals(cateId)){
        	// パンツ（ベビー）
        	cid += "50014785";
        }else if("176000".equals(cateId)){
        	// 帽子（ベビー）
        	cid += "50014785";
        }else if("342000".equals(cateId)){
        	// ブーツ/サンダル（子供用）
        	cid += "50019272" ;
        }else if("".equals(cateId)){
        	// 
        	cid += " " ;
        }
        baobei.cid = cid;
		
	}

	private void composeBaobeiTitle(GoodsObject item, BaobeiPublishObject baobei) {
        String title = "\"日本直邮 MontBell";
        //title += " " + item.titleCN ;
        
        String cateId = item.cateogryObj.categoryId;
        if("159000".equals(cateId)){
        	// スノースポーツ（キッズ）
        	title += " 防水保暖棉衣裤" ;
        }else if("153000".equals(cateId)){
        	//ダウン/化繊綿入り（キッズ）
        	title += " 羽绒衣" ;
        }else if("152000".equals(cateId)){
        	//ジャケット/ベスト（キッズ）
        	title += " 防水茄克" ;
        }else if("154000".equals(cateId)){
        	// フリース（キッズ）
        	title += " 抓绒衣" ;
        }else if("157000".equals(cateId)){
        	// 帽子（キッズ）
        	title += " " ;
        }else if("173700".equals(cateId)){
        	// スノースポーツ（ベビー） 
        	title += " 防水保暖棉衣裤" ;
        }else if("173000".equals(cateId)){
        	// ジャケット/ベスト（ベビー）
        	title += " 防水茄克" ;
        }else if("171000".equals(cateId)){
        	// 雨具（ベビー） 
        	title += " 防水雨衣" ;
        }else if("173500".equals(cateId)){
        	// フリース（ベビー）
        	title += " 抓绒衣" ;
        }else if("175000".equals(cateId)){
        	// パンツ（ベビー）
        	title += " " ;
        }else if("176000".equals(cateId)){
        	// 帽子（ベビー）
        	title += " " ;
        }else if("342000".equals(cateId)){
        	// ブーツ/サンダル（子供用）
        	title += " " ;
        }else if("".equals(cateId)){
        	// 
        	title += " " ;
        }
        
        if(!StringUtil.isBlank(item.titleEn)){
            title += " " + item.titleEn ;
        }
        title += " " + item.productId;
        if(!StringUtil.isBlank(item.gender)){
            title += " " + item.gender;
        }
        
        title= title.replaceAll("儿童", "");
        title= title.replaceAll("  ", "");

        baobei.title =  title + "\"";
    }
    protected void composeBaobeiCateProps(GoodsObject item, BaobeiPublishObject obj) {
        // cateProps　宝贝属性：1627207:-1001;1627207:-1002;1627207:-1003;1627207:-1004;1627207:-1005;1627207:-1006;1627207:-1007;1627207:-1008;1627207:-1009;20509:28381;20509:28313;20509:28314;20509:28315;20509:28316;20509:28317;20509:28319
        String cateId = item.cateogryObj.categoryId;
        String cateProps = "20000:84533669;";
        if("159000".equals(cateId)){
        	// スノースポーツ（キッズ）
        	cateProps += "20021:37454349;122216608:29923;21548:42580;122216816:39036084;" ;
        }else if("153000".equals(cateId)){
        	//ダウン/化繊綿入り（キッズ）
        	cateProps += "13021751:124459527;6103476:3375990;122216608:29923;21548:38488;" ;
        }else if("152000".equals(cateId)){
        	//ジャケット/ベスト（キッズ）
        	cateProps += "20021:37454349;122216608:29923;21548:42580;122216816:39036084;" ;
        }else if("154000".equals(cateId)){
        	// フリース（キッズ）
        	cateProps += "122216608:29923;21548:38488;" ;
        }else if("157000".equals(cateId)){
        	// 帽子（キッズ）
        	cateProps += "" ;
        }else if("173700".equals(cateId)){
        	// スノースポーツ（ベビー） 
        	cateProps += "20021:37454349;122216608:29923;21548:42580;122216816:39036084;" ;
        }else if("173000".equals(cateId)){
        	// ジャケット/ベスト（ベビー）
        	//cateProps += "13021751:124459527;6103476:3375990;122216608:29923;21548:38488;" ;
        	cateProps += "20021:37454349;122216608:29923;21548:42580;122216816:39036084;" ;
        }else if("171000".equals(cateId)){
        	// 雨具（ベビー） 
        	cateProps += "20021:37454349;122216608:29923;21548:42580;122216816:39036084;" ;
        }else if("173500".equals(cateId)){
        	// フリース（ベビー）
        	cateProps += "122216608:29923;21548:38488;" ;
        }else if("175000".equals(cateId)){
        	// パンツ（ベビー）
        	cateProps += "20021:37454349;122216608:29923;21548:42580;122216816:39036084;" ;
        }else if("176000".equals(cateId)){
        	// 帽子（ベビー）
        	cateProps += "" ;
        }else if("342000".equals(cateId)){
        	// ブーツ/サンダル（子供用）
        	cateProps += "122216608:29923;" ;
        }else if("".equals(cateId)){
        	// 
        	cateProps += "" ;
        }
        // ダウンジャケット
        //cateProps += "20000:6217823;13021751:61043120;6103476:3231061;122216608:29923;21548:38488;";
        //cateProps += "20000:84533669;13021751:61043120;6103476:3231061;122216608:29923;21548:38488;";
        //cateProps += "20000:84533669;13021751:124459527;6103476:3375990;122216608:29923;21548:38488;";
        
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

    protected void composeBaobeiSkuProps(GoodsObject item, BaobeiPublishObject obj) {
        // skuProps　销售属性组合：0:0::1627207:-1001;20509:28381;0:0::1627207:-1001;20509:28313;0:0::1627207:-1001;20509:28314;0:0::1627207:-1001;20509:28315;0:0::1627207:-1001;20509:28316;0:0::1627207:-1001;20509:28317;0:0::1627207:-1001;20509:28319;0:0::1627207:-1002;20509:28381;0:0::1627207:-1002;20509:28313;0:0::1627207:-1002;20509:28314;0:0::1627207:-1002;20509:28315;0:0::1627207:-1002;20509:28316;0:0::1627207:-1002;20509:28317;0:0::1627207:-1002;20509:28319;0:0::1627207:-1003;20509:28381;0:0::1627207:-1003;20509:28313;0:0::1627207:-1003;20509:28314;0:0::1627207:-1003;20509:28315;0:0::1627207:-1003;20509:28316;0:0::1627207:-1003;20509:28317;0:0::1627207:-1003;20509:28319;0:0::1627207:-1004;20509:28381;0:0::1627207:-1004;20509:28313;0:0::1627207:-1004;20509:28314;0:0::1627207:-1004;20509:28315;0:0::1627207:-1004;20509:28316;0:0::1627207:-1004;20509:28317;0:0::1627207:-1004;20509:28319;0:0::1627207:-1005;20509:28381;0:0::1627207:-1005;20509:28313;0:0::1627207:-1005;20509:28314;0:0::1627207:-1005;20509:28315;0:0::1627207:-1005;20509:28316;0:0::1627207:-1005;20509:28317;0:0::1627207:-1005;20509:28319;0:0::1627207:-1006;20509:28381;0:0::1627207:-1006;20509:28313;0:0::1627207:-1006;20509:28314;0:0::1627207:-1006;20509:28315;0:0::1627207:-1006;20509:28316;0:0::1627207:-1006;20509:28317;0:0::1627207:-1006;20509:28319;0:0::1627207:-1007;20509:28381;0:0::1627207:-1007;20509:28313;0:0::1627207:-1007;20509:28314;0:0::1627207:-1007;20509:28315;0:0::1627207:-1007;20509:28316;0:0::1627207:-1007;20509:28317;0:0::1627207:-1007;20509:28319;0:0::1627207:-1008;20509:28381;0:0::1627207:-1008;20509:28313;0:0::1627207:-1008;20509:28314;0:0::1627207:-1008;20509:28315;0:0::1627207:-1008;20509:28316;0:0::1627207:-1008;20509:28317;0:0::1627207:-1008;20509:28319;0:0::1627207:-1009;20509:28381;0:0::1627207:-1009;20509:28313;0:0::1627207:-1009;20509:28314;0:0::1627207:-1009;20509:28315;0:0::1627207:-1009;20509:28316;0:0::1627207:-1009;20509:28317;0:0::1627207:-1009;20509:28319;
        String skuProps = "";
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			if (item.sizeList.isEmpty()) {
				String num = "999";
				skuProps += obj.price + ":" + num + ":" + ":1627207" + ":"
						+ taobaoColors.get(i) + ";";
			} else {
				for (int j = 0; j < item.sizeList.size(); j++) {
					if (j >= taobaoSizes.size())
						break;
					String num = MontBellUtil.getStock(item,
							item.colorList.get(i), item.sizeList.get(j));
					skuProps += obj.price + ":" + num + ":" + ":1627207" + ":"
							+ taobaoColors.get(i) + ";20509:"
							+ taobaoSizes.get(j) + ";";
					// skuProps += "20509:" + taobaoSizes.get(j) +":"+ obj.price
					// + ":9999" + ":" + ":1627207" + ":" + taobaoColors.get(i)
					// + ";";
				}
			}
		}
        obj.skuProps =skuProps;
    }

	private void composeBaobeiInputValues(GoodsObject item,
			BaobeiPublishObject obj) {
        // ダウンジャケット MONTBELL,1101464,1234,GRL;颜色分类;GML
        String inputValues = "\""+item.productId+","+obj.price+",";
        for(int i =0;i<item.colorList.size();i++){
            if(i>=taobaoColors.size())break;
            inputValues +=item.colorList.get(i) +  ";颜色分类;";
        }
        obj.inputValues = inputValues+"\"";
	}

	protected void composeBaobeiPropAlias(GoodsObject item, BaobeiPublishObject obj) {
        // propAlias　销售属性别名：20509:28381:size1;20509:28313:size2;20509:28314:size3;20509:28315:size4;20509:28316:size5;20509:28317:size6;20509:28319:size7
        String propAlias = "";
        // 销售属性别名
        for(int i =0;i<item.sizeList.size();i++){
            if(i>=taobaoSizes.size())break;
            propAlias +="20509:"+taobaoSizes.get(i)+":" +item.sizeList.get(i)+";";
        }
        obj.propAlias =propAlias;
    }

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
    
	protected  void composeBaobeiMiaoshu(GoodsObject item, BaobeiPublishObject obj) {
        StringBuffer detailSB = new StringBuffer();
 
        // 包邮
        //detailSB.append(MontBellUtil.composeBaoyouMiaoshu());
        
        // 宝贝描述
        detailSB.append(MontBellUtil.composeProductInfoMiaoshu(item.detailScreenShotPicFile));

        // 尺寸描述
        detailSB.append(MontBellUtil.composeSizeTipMiaoshu(item.sizeTipPics));
        
        // 着装图片
        detailSB.append(MontBellUtil.composeDressOnMiaoshu(item.dressOnPics));
        
        detailSB.append(composeExtraMiaoshu());
        obj.description =  "\"" + detailSB.toString() + "\"";
    }

    public  String composeExtraMiaoshu() {
        StringBuffer miaoshu = new StringBuffer();
        miaoshu.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">购物须知</h3>");
        miaoshu.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
        miaoshu.append("<ul>");
        //miaoshu.append("<li style=\"padding:10.0px;\">0）	店主长驻日本，真真正正的日本代购！不是野鸡代购！<span style=\";color:red;font-weight:bold\">100%日本，100%真品。</span></li>");
        miaoshu.append("<li style=\"padding:10.0px;\">1）	发货时间：下单后采购，采购时间3天左右<span style=\";color:red;font-weight:bold\">采购时间3天左右。</span>如果断货需要预定的话时间可能长一点，日本直邮发货后7-10天左右收到，接受拼邮，拼邮时间为15天左右到达国内，具体事项请咨询客服。</li>");
        miaoshu.append("<li style=\"padding:10.0px;\">2）	代购商品不接受退换货，不接受个人喜好原因，尺寸大小等原因的退款，除非严重质量问题，发货前我们会对商品的完整性等确认好，关于衣服尺寸大小等问题参照商品详情描述，对于尺寸拿捏不好的以及商品细节，色差等问题可以咨询客服，确认好了再付款交易哦。</li>");
        miaoshu.append("<li style=\"padding:10.0px;\">3）	日本商品，大多数包装简洁，尤其是衣物等只是塑料袋包装或者无包装，采购时会和店员一起确认，买回来后会用透明玻璃纸包装，一般不提供专柜纸袋，如需提供请拍下后备注，商品经过长途跋涉到达国内很有可能会有压痕，亲们拿到衣服后抖开整理一下一般就不会有问题，介意者慎拍！</li>");
        miaoshu.append("<li style=\"padding:10.0px;\">4）	店铺商品均为商场专柜采购或者官网预定！</li>");
        miaoshu.append("<li style=\"padding:10.0px;\">5）关税：个人代购非走私，追加关税不是没有可能的。不幸被抽查产生关税，<span style=\";color:red;font-weight:bold\">关税买家自己承担。</span></li>");
        miaoshu.append("</ul>");
        miaoshu.append("</div>");
        return miaoshu.toString();
    }
    public MontbellKidsBabyBaobeiProducer addScanCategory(String scanCategoryId) {

        this.scanCategoryIds.add(scanCategoryId);
        return this;
    }

    @Override
    public BaseBaobeiParser getParser() {
        return new MontbellProductParser();
    }

	protected void composeBaobeiPictureStatus(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		MontBellUtil.composeBaobeiPictureStatus(item, publishedBaobei,
				this.taobaoColors);
	}

	protected void composeBaobeiPicture(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		MontBellUtil.composeBaobeiPicture(item, publishedBaobei, this.taobaoColors);
	}

}
