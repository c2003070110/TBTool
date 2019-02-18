package com.walk_nie.taobao.amazon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.jsoup.helper.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.object.StockObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.util.NieConfig;

public class AmazonSingleBaobeiProducer extends BaseBaobeiProducer{
    
    private List<String> urlList = Lists.newArrayList();
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
            List<AmazonGoodsObject> itemIdList = Lists.newArrayList();
            if(urlList.isEmpty()){
                
            }else{
            	AmazonGoodsPageParser parer = (AmazonGoodsPageParser)getParser();
            	parer.setPublishedbaobeiList(this.publishedbaobeiList);
                itemIdList = parer.scanItemUrls(urlList);    
            }
            if (itemIdList.isEmpty())
                return;
            if(!outputFile.getParentFile().exists()){
            	outputFile.getParentFile().mkdirs();
            }
            priceBw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile), "UTF-16"));

            priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
            String pictureOutFolder = NieConfig.getConfig("amazon.root.out.picture.folder");
			for (AmazonGoodsObject obj : itemIdList) {
				downloadPicture(obj, pictureOutFolder);
			}
			String taobaoPicFolder = TaobaoUtil.getPictureFolder(outputFile);
			for (AmazonGoodsObject obj : itemIdList) {
				List<String> list = Lists.newArrayList();
				if (obj.stockList.isEmpty()) {
					for (StockObject st : obj.stockList) {
						list.add(st.colorPicLocalName);
					}
				}
				list.addAll(obj.googsPicLocalNameList);
				TaobaoUtil.copyFiles(list, pictureOutFolder, taobaoPicFolder);
				
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

	private void downloadPicture(AmazonGoodsObject goods, String outFilePath) {
		if (!goods.stockList.isEmpty()) {
			for (StockObject st : goods.stockList) {
				String picName = goods.asin + "-color_" + st.colorName;
				if(goods.colorNameList.contains(picName)){
					continue;
				}
				goods.colorNameList.add(picName);
				try {
					TaobaoUtil.downloadPicture(outFilePath, st.colorPicUrl, picName);
					st.colorPicLocalName = picName;
				} catch (IOException e) {
				}
			}
		}
		if (!goods.googsPicUrlList.isEmpty()) {
			int i = 1;
			for (String str : goods.googsPicUrlList) {
				String picName = goods.asin + "-Other_" + i;
				i++;
				try {
					TaobaoUtil.downloadPicture(outFilePath, str, picName);
					//Files.copy(new File(str), new File(outFilePath, picName + "." + fileExtends));
					goods.googsPicLocalNameList.add(picName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (!goods.googsVideoUrlList.isEmpty()) {
			int i = 1;
			for (String str : goods.googsVideoUrlList) {
				String fileExtends = getFileExtends(str);
				String picName = goods.asin + "-Vedio_" + i;
				i++;
				try {
					Files.copy(new File(str), new File(outFilePath, picName + "." + fileExtends));
					goods.googsVideoLocalNameList.add(picName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private String getFileExtends(String fileName){
		int i = fileName.lastIndexOf(".");
		return fileName.substring(i+1);
	}

	protected void writeOut(BufferedWriter priceBw, AmazonGoodsObject item)
            throws Exception {
        
        priceBw.write(composeBaobeiLine(item));
        priceBw.flush();
    }
    protected String composeBaobeiLine(AmazonGoodsObject item) throws Exception {
   	
        BaobeiPublishObject obj = new BaobeiPublishObject();
        BaobeiUtil.setBaobeiCommonInfo(obj);
		// 宝贝名称
		composeBaobeiTitle(item, obj);
        // 宝贝类目
		// 户外其他服装
        obj.cid =  "";
        // 店铺类目
        obj.seller_cids =  "";
        // 省
        obj.location_state = "\"日本\"";
        // 宝贝价格
        int emsFee = TaobaoUtil.getEmsFee(item.weightShipment + (item.weightShipment-item.weightItem));
        double priceCNY = (item.priceJPY + emsFee) * currencyRate;
		priceCNY = priceCNY + priceCNY * benefitRate;
        obj.price = String.valueOf(Math.round(priceCNY));
        
        // 宝贝数量
        obj.num = "99";
		
        // 邮费模版ID TODO mianyou
        obj.postage_id = "";
        
        // 用户输入ID串;
		obj.inputPids = "";
        
        // 用户输入名-值对
        composeBaobeiInputValues(item, obj);
        
        // 宝贝描述
        composeBaobeiMiaoshu(item, obj);

        // 宝贝属性
        composeBaobeiCateProps(item, obj);
        // 销售属性组合
        composeBaobeiSkuProps(item, obj);
        // 商家编码
        obj.outer_id = "AMZ_" + item.asin;
        // 销售属性别名
        composeBaobeiPropAlias(item, obj);
        // 商品条形码
        obj.barcode = item.sku;
		// 图片状态
		composeBaobeiPictureStatus(item,obj);
		// 新图片
		composeBaobeiPicture(item,obj);
        // 自定义属性值
        composeBaobeiInputCustomCpv(item, obj);
        // 宝贝卖点
        //MontBellUtil.composeBaobeiSubtitle(item, obj);
        // 库存计数
        obj.sub_stock_type = "1";
        // 商品资质
        obj.qualification = "%7B%7D";
        // 增加商品资质
        obj.add_qualification = "1";

        return TaobaoUtil.composeTaobaoLine(obj);
    }

    protected void composeBaobeiTitle(AmazonGoodsObject item,
            BaobeiPublishObject baobei) {
        String title = "";
  
        title += "日本直邮  ";
        if(item.weightShipment ==0){
            title += "[W] ";
        }
        if(!item.googsVideoUrlList.isEmpty()){
            title += "[V] ";
        }
        
        title += item.titleJP + " ";
       
        baobei.title =  "\"" + title + "\"";
    }
    protected void composeBaobeiCateProps(AmazonGoodsObject item, BaobeiPublishObject obj) {
        String cateProps = "";
        cateProps += "20000:84533669;122216608:29923;";
        
        // 宝贝属性
        for(int i =0;i<item.colorNameList.size();i++){
            if(i>=taobaoColors.size())break;
            cateProps +="1627207:"+taobaoColors.get(i)+";";
        }
        for(int i =0;i<item.sizeNameList.size();i++){
            if(i>=taobaoSizes.size())break;
            cateProps +="20509:"+taobaoSizes.get(i)+";";
        }
        obj.cateProps =cateProps;
    }

    protected void composeBaobeiSkuProps(AmazonGoodsObject item, BaobeiPublishObject obj) {
        // skuProps　销售属性组合：0:0::1627207:-1001;20509:28381;0:0::1627207:-1001;20509:28313;0:0::1627207:-1001;20509:28314;0:0::1627207:-1001;20509:28315;0:0::1627207:-1001;20509:28316;0:0::1627207:-1001;20509:28317;0:0::1627207:-1001;20509:28319;0:0::1627207:-1002;20509:28381;0:0::1627207:-1002;20509:28313;0:0::1627207:-1002;20509:28314;0:0::1627207:-1002;20509:28315;0:0::1627207:-1002;20509:28316;0:0::1627207:-1002;20509:28317;0:0::1627207:-1002;20509:28319;0:0::1627207:-1003;20509:28381;0:0::1627207:-1003;20509:28313;0:0::1627207:-1003;20509:28314;0:0::1627207:-1003;20509:28315;0:0::1627207:-1003;20509:28316;0:0::1627207:-1003;20509:28317;0:0::1627207:-1003;20509:28319;0:0::1627207:-1004;20509:28381;0:0::1627207:-1004;20509:28313;0:0::1627207:-1004;20509:28314;0:0::1627207:-1004;20509:28315;0:0::1627207:-1004;20509:28316;0:0::1627207:-1004;20509:28317;0:0::1627207:-1004;20509:28319;0:0::1627207:-1005;20509:28381;0:0::1627207:-1005;20509:28313;0:0::1627207:-1005;20509:28314;0:0::1627207:-1005;20509:28315;0:0::1627207:-1005;20509:28316;0:0::1627207:-1005;20509:28317;0:0::1627207:-1005;20509:28319;0:0::1627207:-1006;20509:28381;0:0::1627207:-1006;20509:28313;0:0::1627207:-1006;20509:28314;0:0::1627207:-1006;20509:28315;0:0::1627207:-1006;20509:28316;0:0::1627207:-1006;20509:28317;0:0::1627207:-1006;20509:28319;0:0::1627207:-1007;20509:28381;0:0::1627207:-1007;20509:28313;0:0::1627207:-1007;20509:28314;0:0::1627207:-1007;20509:28315;0:0::1627207:-1007;20509:28316;0:0::1627207:-1007;20509:28317;0:0::1627207:-1007;20509:28319;0:0::1627207:-1008;20509:28381;0:0::1627207:-1008;20509:28313;0:0::1627207:-1008;20509:28314;0:0::1627207:-1008;20509:28315;0:0::1627207:-1008;20509:28316;0:0::1627207:-1008;20509:28317;0:0::1627207:-1008;20509:28319;0:0::1627207:-1009;20509:28381;0:0::1627207:-1009;20509:28313;0:0::1627207:-1009;20509:28314;0:0::1627207:-1009;20509:28315;0:0::1627207:-1009;20509:28316;0:0::1627207:-1009;20509:28317;0:0::1627207:-1009;20509:28319;
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
					if (j >= taobaoSizes.size())
						break;
					String num = getStock(item,
							item.colorNameList.get(i), item.sizeNameList.get(j));
					skuProps += obj.price + ":" + num + ":" + ":1627207" + ":"
							+ taobaoColors.get(i) + ";20509:"
							+ taobaoSizes.get(j) + ";";
				}
			}
		}
        obj.skuProps =skuProps;
    }

	protected void composeBaobeiInputValues(AmazonGoodsObject item,
			BaobeiPublishObject obj) {
        // ダウンジャケット MONTBELL,1101464,1234,GRL;颜色分类;GML
        String inputValues = "";
        
        for(int i =0;i<item.colorNameList.size();i++){
            if(i>=taobaoColors.size())break;
            inputValues +=item.colorNameList.get(i) +  ";颜色分类;";
        }
        
        obj.inputValues = inputValues+"\"";
	}

	protected void composeBaobeiPropAlias(AmazonGoodsObject item, BaobeiPublishObject obj) {
        // propAlias　销售属性别名：20509:28381:size1;20509:28313:size2;20509:28314:size3;20509:28315:size4;20509:28316:size5;20509:28317:size6;20509:28319:size7
        String propAlias = "";
        // 销售属性别名
        for(int i =0;i<item.sizeNameList.size();i++){
            if(i>=taobaoSizes.size())break;
            propAlias +="20509:"+taobaoSizes.get(i)+":" +item.sizeNameList.get(i)+";";
        }
        obj.propAlias =propAlias;
    }

	protected void composeBaobeiInputCustomCpv(AmazonGoodsObject item, BaobeiPublishObject obj) {
        String inputCustomCpv="";
        // 自定义属性值
        for(int i =0;i<item.colorNameList.size();i++){
            if(i>=taobaoColors.size())break;
            //1627207:-1001:color1;
            inputCustomCpv += "1627207:" + taobaoColors.get(i)  +":"+item.colorNameList.get(i)+";"; 
        }
        obj.input_custom_cpv =inputCustomCpv;
    }
    
	protected  void composeBaobeiMiaoshu(AmazonGoodsObject item, BaobeiPublishObject obj) {
        StringBuffer detailSB = new StringBuffer();
        
        // 包邮
        detailSB.append(Miaoshu_baoyou(item));
        
        // 着装图片
        detailSB.append(Miaoshu_itemDesp(item));
        
        String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
        
        obj.description =  "\"" + detailSB.toString()  + extraMiaoshu1 + "\"";
    }

	protected void composeBaobeiPictureStatus(AmazonGoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		String picStatus = TaobaoUtil.composeBaobeiPictureStatus(item.googsPicLocalNameList, item.colorNameList, this.taobaoColors);

		publishedBaobei.picture_status ="\"" + picStatus + "\""  ;
	}

	protected void composeBaobeiPicture(AmazonGoodsObject item,
			BaobeiPublishObject publishedBaobei) {
        String picture = TaobaoUtil.composeBaobeiPicture(item.googsPicLocalNameList, item.colorNameList, this.taobaoColors);

        publishedBaobei.picture = "\"" + picture + "\"" ;
	}

	private String getStock(AmazonGoodsObject item, String colorName, String sizeName) {

		boolean isStock = false;
		boolean isSkipColor = StringUtil.isBlank(colorName);
		boolean isSkipSize = StringUtil.isBlank(sizeName);
		for (StockObject stockObj : item.stockList) {
			if ((isSkipColor || stockObj.colorName.equals(colorName))
					&& (isSkipSize || stockObj.sizeName.equals(sizeName))) {
				isStock = stockObj.isStock;
				break;
			}
		}
		return isStock ? "99" : "0";
	}

	private Object Miaoshu_baoyou(AmazonGoodsObject item) {
        StringBuffer detailSB = new StringBuffer();
        detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">各位亲们</h3>");
        detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
        // TODO baoyou
        // TODO baoshui
        detailSB.append("<p style=\"text-indent:2.0em;\">直邮是 日本发货到你家，时效快但<span style=\";color:red;font-weight:bold\">关税买家承担！</span></p>");
        detailSB.append("<p style=\"text-indent:2.0em;\">本店为您急事所急，急单商量。但直邮耗时，还请做好事前安排，提前下单哦。</p>");
        detailSB.append("<p style=\"text-indent:2.0em;\"><span style=\";color:red;font-weight:bold\">如果不幸被海关查到，由买家报关通关，不能报关的买家，请不要下单！！！！</span></p>");
        detailSB.append("</div>");
        return detailSB.toString();
	}

	private Object Miaoshu_itemDesp(AmazonGoodsObject item) {
		StringBuffer detailSB = new StringBuffer();
		if (!item.detailScreenShotPicFile.isEmpty()) {
            detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝图片</h3>");
            detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
            for(String pic:item.detailScreenShotPicFile){
                detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;width:650px;\" src=\"FILE:///" + pic + "\"/></p>");    
            }
            detailSB.append("</div>");
		}
		return detailSB.toString();
	}

    public AmazonSingleBaobeiProducer addUrl(String url) {

        this.urlList.add(url);
        return this;
    }

    public AmazonSingleBaobeiProducer addUrls(List<String> urls) {

        this.urlList.addAll(urls);
        return this;
    }

    @Override
    public BaseBaobeiParser getParser() {
        return new AmazonGoodsPageParser();
    }

}
