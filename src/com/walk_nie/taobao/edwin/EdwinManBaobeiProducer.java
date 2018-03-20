package com.walk_nie.taobao.edwin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.eclipse.jetty.util.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class EdwinManBaobeiProducer extends BaseBaobeiProducer{
    
    private List<String> scanSeriesNames = Lists.newArrayList();
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
    private List<String> taobaoSizesCN = Lists.newArrayList();
    {
    	 // XS,S,M,L,XL,XXL,
    	taobaoSizesCN.add("28313");taobaoSizesCN.add("28314");taobaoSizesCN.add("28315");
    	taobaoSizesCN.add("28316");taobaoSizesCN.add("28317");taobaoSizesCN.add("28318");
    	taobaoSizesCN.add("28319");taobaoSizesCN.add("28320");taobaoSizesCN.add("28321");
    	taobaoSizesCN.add("28322");taobaoSizesCN.add("28323");taobaoSizesCN.add("28324");
    }
    private List<String> taobaoSizes = Lists.newArrayList();
    {
    	 // XS,S,M,L,XL,XXL,
        taobaoSizes.add("-1001");taobaoSizes.add("-1002");taobaoSizes.add("-1003");taobaoSizes.add("-1004");
        taobaoSizes.add("-1005");taobaoSizes.add("-1006");taobaoSizes.add("-1007");taobaoSizes.add("-1008");
        taobaoSizes.add("-1009");taobaoSizes.add("-1010");taobaoSizes.add("-1011");taobaoSizes.add("-1012");
    }
    
    private List<String> switchTaobaoSizes = null;
    
    
    public void process() {
        BufferedWriter priceBw = null;
        try {
            System.out.println("-------- START --------");
			EdwinProductParser parser = (EdwinProductParser) getParser();
			parser.setPublishedbaobeiList(this.publishedbaobeiList);
            List<GoodsObject> itemIdList = Lists.newArrayList();
            if(scanSeriesNames.isEmpty()){
				List<String> dispNos = Lists.newArrayList();
				if (this.brandCd.equals(EdwinUtil.brandCdEdwin)
						&& this.sexVal.equals(EdwinUtil.sex_val_man)) {
					dispNos.addAll(DispNoObject.edwinManPants());
					dispNos.addAll(DispNoObject.edwinManJacket());
				} else if (this.brandCd.equals(EdwinUtil.brandCdEdwin)
						&& this.sexVal.equals(EdwinUtil.sex_val_woman)) {
					dispNos.addAll(DispNoObject.edwinLadyPants());
					dispNos.addAll(DispNoObject.edwinLadyJacket());
				} else if (this.brandCd.equals(EdwinUtil.brandCdEdwin)
						&& this.sexVal.equals(EdwinUtil.sex_val_kids)) {
					dispNos.addAll(DispNoObject.edwinKidsPants());
					dispNos.addAll(DispNoObject.edwinKidsJacket());
				} else if (this.brandCd.equals(EdwinUtil.brandCdLee)
						&& this.sexVal.equals(EdwinUtil.sex_val_man)) {
					dispNos.addAll(DispNoObject.leeManPants());
					dispNos.addAll(DispNoObject.leeManJacket());
				} else if (this.brandCd.equals(EdwinUtil.brandCdLee)
						&& this.sexVal.equals(EdwinUtil.sex_val_woman)) {
					dispNos.addAll(DispNoObject.leeLadyPants());
					dispNos.addAll(DispNoObject.leeLadyJacket());
				} else if (this.brandCd.equals(EdwinUtil.brandCdLee)
						&& this.sexVal.equals(EdwinUtil.sex_val_kids)) {
					dispNos.addAll(DispNoObject.leeKidsPants());
					dispNos.addAll(DispNoObject.leeKidsJacket());
				} else if (this.brandCd.equals(EdwinUtil.brandCdSomething)) {
					dispNos.addAll(DispNoObject.somethingPants());
					dispNos.addAll(DispNoObject.somethingJacket());
				}
				itemIdList = parser.parseByDispNos(dispNos);
			} else {
				itemIdList = parser.parseByCategoryNames(scanSeriesNames);
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
            String pictureOutputRoot = EdwinUtil.rootPathName
					+ "/" + brandCd+ "/" + sexVal;
			for (GoodsObject obj : itemIdList) {
				EdwinUtil.downloadPicture(obj, pictureOutputRoot);
			}
			String taobaoPicFolder = TaobaoUtil.getPictureFolder(csvFile);
			for (GoodsObject obj : itemIdList) {
				TaobaoUtil.copyFiles(obj.colorPictureFileNameList,
						pictureOutputRoot, taobaoPicFolder);
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

		if (EdwinUtil.isXSMLSize(item)) {
			this.switchTaobaoSizes = taobaoSizesCN;
		} else {
			this.switchTaobaoSizes = taobaoSizes;
		}
		
        BaobeiPublishObject obj = new BaobeiPublishObject();
        BaobeiUtil.setBaobeiCommonInfo(obj);
		// 宝贝名称
		composeBaobeiTitle(item, obj);
        // 宝贝类目
		if (EdwinUtil.sex_val_man.equals(item.sexVal)) {
			// " 男";
			obj.cid = "50010167";
		} else if (EdwinUtil.sex_val_woman.equals(item.sexVal)) {
			// " 女";
			obj.cid = "162205";
		} else if (EdwinUtil.sex_val_kids.equals(item.sexVal)) {
			// " 女";
			obj.cid = "50010167";
		}
        // 店铺类目
	    if(EdwinUtil.brandCdEdwin.equals(item.brandCd)){
        	// Edwin
	        obj.seller_cids =  "1362257461";
        } else if(EdwinUtil.brandCdLee.equals(item.brandCd)){
        	//Lee
            obj.seller_cids =  "1362257462";
        } else if(EdwinUtil.brandCdSomething.equals(item.brandCd)){
        	//Something
            obj.seller_cids =  "1362257461";
        }
        // 省
        obj.location_state = "\"日本\"";
        // 宝贝价格
        obj.price = EdwinUtil.convertToCNYWithEmsFee(item,this.currencyRate,this.benefitRate);
        //obj.price = item.priceCNY;
        // 宝贝数量
        obj.num = "99";
		
        // 邮费模版ID
        obj.postage_id = EdwinUtil.composePostageId(item);
        
        // 用户输入ID串;
		if (EdwinUtil.isXSMLSize(item)) {
			obj.inputPids = "\"13021751\"";
		} else {
			obj.inputPids = "\"13021751,6103476,1627207\"";
		}
        // 用户输入名-值对
        composeBaobeiInputValues(item, obj);
        
        // 宝贝描述
        composeBaobeiMiaoshu(item, obj);

        // 宝贝属性
        composeBaobeiCateProps(item, obj);
        // 销售属性组合
        composeBaobeiSkuProps(item, obj);
        // 商家编码
	    if(EdwinUtil.brandCdEdwin.equals(item.brandCd)){
        	// Edwin
	        obj.outer_id = "edwin_" + item.goods_no + "_" + item.ecCd;
        } else if(EdwinUtil.brandCdLee.equals(item.brandCd)){
        	//Lee
            obj.outer_id = "lee_" + item.goods_no + "_" + item.ecCd;
        }
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
        EdwinUtil.composeBaobeiSubtitle(item, obj);
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

	private void composeBaobeiTitle(GoodsObject item, BaobeiPublishObject baobei) {
        String title = "\"日本直邮";
        if(EdwinUtil.brandCdEdwin.equals(item.brandCd)){
        	title += " EDWIN";
        }
        if(EdwinUtil.brandCdLee.equals(item.brandCd)){
        	title += " Lee";
        }
        if(EdwinUtil.brandCdSomething.equals(item.brandCd)){
        	title += " SOMETHING";
        }
    	title += "牛仔裤";
        if(EdwinUtil.sex_val_man.equals(item.sexVal)){
        	title += " 男";
        }
        if(EdwinUtil.sex_val_woman.equals(item.sexVal)){
        	title += " 女";
        }
        if(EdwinUtil.sex_val_kids.equals(item.sexVal)){
        	title += " 小孩";
        }
        if("日本".equals(item.producePlace)){
        	title += " 日本制";
        }
		if (!StringUtil.isBlank(item.seriesName)) {
			title += " " + item.seriesName;
		}
//        if("1102".equals(item.seriesName)){
//        	title += " 定番503";
//        }
//        if("1120".equals(item.seriesName)){
//        	title += " JERSEYS";
//        }
//    	title += " " + item.as_goods_no.get(0);
//        for(int i=1;i<item.as_goods_no.size();i++){
//        	String[] sp = item.as_goods_no.get(i).split("-");
//        	if(sp.length >1){
//             	title += "/" + sp[1];
//        	}else{
//        		title += "/" + sp[1];
//        	}
//        }
    	String[] sp = item.as_goods_no.get(0).split("-");
    	if(sp.length>1){
    		title += " " + sp[0];
    	}
        //title += " " + item.goods_no;
        baobei.title =  title + "\"";
    }
    protected void composeBaobeiCateProps(GoodsObject item, BaobeiPublishObject obj) {
        String cateProps = "20000:";
	    if(EdwinUtil.brandCdEdwin.equals(item.brandCd)){
        	// Edwin
	    	cateProps += "31732;";
        } else if(EdwinUtil.brandCdLee.equals(item.brandCd)){
        	//Lee
	    	cateProps += "20819;";
        } else if(EdwinUtil.brandCdSomething.equals(item.brandCd)){
        	// 
	    	cateProps += "31732;";
        }
		if (EdwinUtil.sex_val_man.equals(item.sexVal)) {
			// 男;
	    	cateProps += "42722636:3250994;122216515:29535;";
		} else if (EdwinUtil.sex_val_kids.equals(item.sexVal)) {
			// 
	    	cateProps += "42722636:3250994;122216515:29535;";
		} else if (EdwinUtil.sex_val_woman.equals(item.sexVal)) {
			// 女;
	    	cateProps += "122216347:828880787;";
		}
		// 长裤
    	cateProps += "122276111:20525;";
		if (!EdwinUtil.isXSMLSize(item)) {
			cateProps += "13021751:4667605;";
		}
        
        // 宝贝属性
        for(int i =0;i<item.colorNameList.size();i++){
            if(i>=taobaoColors.size())break;
            cateProps +="1627207:"+taobaoColors.get(i)+";";
        }
        for(int i =0;i<item.sizeNameList.size();i++){
            if(i>=switchTaobaoSizes.size())break;
            cateProps +="20518:"+switchTaobaoSizes.get(i)+";";
        }
        obj.cateProps =cateProps;
    }

    protected void composeBaobeiSkuProps(GoodsObject item, BaobeiPublishObject obj) {
        String skuProps = "";
		for (int i = 0; i < item.colorNameList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			if (item.sizeNameList.isEmpty()) {
				String num = "99";
				skuProps += obj.price + ":" + num + ":" + ":1627207" + ":"
						+ taobaoColors.get(i) + ";";
			} else {
				for (int j = 0; j < item.sizeNameList.size(); j++) {
					if (j >= switchTaobaoSizes.size())
						break;
					String num = EdwinUtil.getStock(item,
							item.colorNameList.get(i), item.sizeNameList.get(j));
					skuProps += obj.price + ":" + num + ":" + ":1627207" + ":"
							+ taobaoColors.get(i) + ";20518:"
							+ switchTaobaoSizes.get(j) + ";";
				}
			}
		}
        obj.skuProps =skuProps;
    }

	private void composeBaobeiInputValues(GoodsObject item,
			BaobeiPublishObject obj) {
        // ダウンジャケット MONTBELL,1101464,1234,GRL;颜色分类;GML
        String inputValues = "\""+item.goods_no+","+obj.price+",";
        for(int i =0;i<item.colorNameList.size();i++){
            if(i>=taobaoColors.size())break;
            inputValues +=item.colorNameList.get(i) +  ";颜色分类;";
        }
        obj.inputValues = inputValues+"\"";
	}

	protected void composeBaobeiPropAlias(GoodsObject item, BaobeiPublishObject obj) {
        // propAlias　销售属性别名：20509:28381:size1;20509:28313:size2;20509:28314:size3;20509:28315:size4;20509:28316:size5;20509:28317:size6;20509:28319:size7
        String propAlias = "";
        // 销售属性别名
        for(int i =0;i<item.sizeNameList.size();i++){
            if(i>=switchTaobaoSizes.size())break;
            propAlias +="20518:"+switchTaobaoSizes.get(i)+":" +item.sizeNameList.get(i)+";";
        }
        obj.propAlias =propAlias;
    }

	protected void composeBaobeiInputCustomCpv(GoodsObject item, BaobeiPublishObject obj) {
        String inputCustomCpv="";
        // 自定义属性值
        for(int i =0;i<item.colorNameList.size();i++){
            if(i>=taobaoColors.size())break;
            //1627207:-1001:color1;
            inputCustomCpv += "1627207:" + taobaoColors.get(i)  +":"+item.colorNameList.get(i)+";"; 
        }
        for(int i =0;i<item.sizeNameList.size();i++){
            if(i>=switchTaobaoSizes.size())break;
            inputCustomCpv += "20518:" + switchTaobaoSizes.get(i)  +":"+item.sizeNameList.get(i)+";"; 
        }
        obj.input_custom_cpv =inputCustomCpv;
    }
    
	protected  void composeBaobeiMiaoshu(GoodsObject item, BaobeiPublishObject obj) {
        StringBuffer detailSB = new StringBuffer();
   
        // 包邮
        detailSB.append(EdwinUtil.composeBaoyouMiaoshu());
        
        // 着装图片
        detailSB.append(EdwinUtil.composeDressOnMiaoshu(item.modelTipInfo,item.bodyPictureFileNameList));

        // 尺寸描述
        detailSB.append(EdwinUtil.composeSizeTipMiaoshu(item.sizeTipFileName));
        
        String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
        obj.description =  "\"" + detailSB.toString() + extraMiaoshu1+ "\"";
    }
	
	protected void composeBaobeiPictureStatus(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		EdwinUtil.composeBaobeiPictureStatus(item, publishedBaobei,
				this.taobaoColors);
	}

	protected void composeBaobeiPicture(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		EdwinUtil.composeBaobeiPicture(item, publishedBaobei,
				this.taobaoColors);
	}


    public EdwinManBaobeiProducer addScanSeriesName(String seriesName) {

        this.scanSeriesNames.add(seriesName);
        return this;
    }
    
    private String brandCd = "";
    public EdwinManBaobeiProducer setBrandCd(String brandCd) {
        this.brandCd = brandCd;
        return this;
    }
    private String sexVal = "";
    public EdwinManBaobeiProducer setSexVal(String sexValue) {
        this.sexVal = sexValue;
        return this;
    }

    @Override
    public BaseBaobeiParser getParser() {
        return new EdwinProductParser(brandCd,sexVal);
    }
    

}
