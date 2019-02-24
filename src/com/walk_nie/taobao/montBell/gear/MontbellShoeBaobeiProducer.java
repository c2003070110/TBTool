package com.walk_nie.taobao.montBell.gear;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

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

public class MontbellShoeBaobeiProducer extends BaseBaobeiProducer{
    
    private List<String> scanCategoryIds = Lists.newArrayList();
    private List<String> taobaoColors = Lists.newArrayList();
    {
        taobaoColors.add("-1001");taobaoColors.add("-1002");taobaoColors.add("-1003");
        taobaoColors.add("-1004");taobaoColors.add("-1005");taobaoColors.add("-1006");
        taobaoColors.add("-1007");taobaoColors.add("-1008");taobaoColors.add("-1009");
        taobaoColors.add("-1010");taobaoColors.add("-1011");taobaoColors.add("-1012");
    }
    private List<String> taobaoSizes = Lists.newArrayList();
    {
		// xxCM,
		taobaoSizes.add("44886");taobaoSizes.add("33263");taobaoSizes.add("44887");
		taobaoSizes.add("33264");taobaoSizes.add("44888");taobaoSizes.add("33265");
		taobaoSizes.add("44889");taobaoSizes.add("669");taobaoSizes.add("44890");
		taobaoSizes.add("33267");taobaoSizes.add("44891");taobaoSizes.add("33268");
		taobaoSizes.add("44892");taobaoSizes.add("30106");taobaoSizes.add("44893");
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
 
            priceBw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile), "UTF-16"));

            priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
            
			for (GoodsObject obj : itemIdList) {
				// FIXME 鞋码 -> Discard
				obj.sizeList.clear();
				obj.sizeList.add("鞋码留言 厘米单位");

				MontBellUtil.downloadPicture(obj);
			}
			String taobaoPicFolder = TaobaoUtil.getPictureFolder(outputFile);
			for (GoodsObject obj : itemIdList) {
				TaobaoUtil.copyFiles(obj.pictureNameList,
						MontBellUtil.getWebShopPicFolder(obj), taobaoPicFolder);
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
        obj.cid =  "50019272";
        // 店铺类目
		obj.seller_cids = "\"" + "1372090603," + "\"";
        // 省
        obj.location_state = "\"日本\"";
        // 宝贝价格
        obj.price = MontBellUtil.convertToCNYWithEmsFee(item,this.currencyRate,this.benefitRate);
        //obj.price = item.priceCNY;
        // 宝贝数量
        obj.num = "999";
		
        // 邮费模版ID
        obj.postage_id = MontBellUtil.composePostageId(item);
        
        // 用户输入ID串;
		obj.inputPids = "\"6103476,13021751\"";
        
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
        obj.outer_id = MontBellUtil.composeOuter_id(item);
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
        // 商品资质
        obj.qualification = "%7B%7D";
        // 增加商品资质
        obj.add_qualification = "1";

        return TaobaoUtil.composeTaobaoLine(obj);
    }

    private void composeBaobeiTitle(GoodsObject item,
            BaobeiPublishObject baobei) {
        String title = "日本直邮 MontBell";
        String cateId = item.cateogryObj.categoryId;
        if("241000".equals(cateId)){
        	// 登山靴（アルパイン）
        	title += " 登山靴" ;
        }else if("241100".equals(cateId)){
        	//登山靴（トレッキング）
        	title += " 登山靴" ;
        }else if("241200".equals(cateId)){
        	//登山靴（ハイキング）
        	title += " 登山靴" ;
        }else if("244500".equals(cateId)){
        	//スノーブーツ
        	title += " 雪地靴" ;
        }else if("243000".equals(cateId)){
        	//スパッツ
        	title += " 鞋套" ;
        }
        if(!StringUtil.isBlank(item.titleEn)){
            title += " " + item.titleEn ;
        }
        title += " " + item.productId;
        if(!StringUtil.isBlank(item.gender)){
            title += " " + item.gender;
		}
		baobei.title = "\"" + title + "\"";
    }
    protected void composeBaobeiCateProps(GoodsObject item, BaobeiPublishObject obj) {
        String cateProps = "20000:84533669;";
        if("男".equals(item.gender)){
        	cateProps += "122216608:20532;";
        }else if("女".equals(item.gender)){
        	cateProps += "122216608:20533;";
        }else{
        	cateProps += "122216608:29923;";
        }
        
        // 宝贝属性
        cateProps +=TaobaoUtil.composeBaobeiCateProps(item.colorList, item.sizeList, taobaoColors, taobaoSizes,"20549");
     
        obj.cateProps = "\"" + cateProps + "\"";
    }

    protected void composeBaobeiSkuProps(GoodsObject item, BaobeiPublishObject obj) {
        String skuProps = "";
        skuProps += TaobaoUtil.composeBaobeiSkuProps(item.colorList, item.sizeList, taobaoColors, taobaoSizes,obj.price);
        obj.skuProps ="\"" + skuProps+ "\"";
    }

	private void composeBaobeiInputValues(GoodsObject item,
			BaobeiPublishObject obj) {
		// ダウンジャケット MONTBELL,1101464,1234,GRL;颜色分类;GML
		String inputValues = obj.price + "," + item.productId + ",";
        inputValues += TaobaoUtil.composeBaobeiInputValues(item.colorList, taobaoColors);
        obj.inputValues = "\""+ inputValues+"\"";
	}

	protected void composeBaobeiPropAlias(GoodsObject item, BaobeiPublishObject obj) {
        String propAlias = "";
        propAlias += TaobaoUtil.composeBaobeiPropAlias(item.sizeList, taobaoSizes,"20549");
        obj.propAlias = "\""+ propAlias+"\"";
    }

	protected void composeBaobeiInputCustomCpv(GoodsObject item, BaobeiPublishObject obj) {
        String inputCustomCpv="";
        // 自定义属性值
        for(int i =0;i<item.colorList.size();i++){
            if(i>=taobaoColors.size())break;
            //1627207:-1001:color1;
            inputCustomCpv += "1627207:" + taobaoColors.get(i)  +":"+item.colorList.get(i)+";"; 
        }
        obj.input_custom_cpv ="\""+ inputCustomCpv +"\"";
    }
    
	protected  void composeBaobeiMiaoshu(GoodsObject item, BaobeiPublishObject obj) {
        StringBuffer detailSB = new StringBuffer();
        // 包邮
        detailSB.append(MontBellUtil.composeBaoyouMiaoshu());
        // 关税
		detailSB.append(MontBellUtil.composeHaigaiMiaoshu());
        
        // 宝贝描述
        detailSB.append(MontBellUtil.composeProductInfoMiaoshu(item));
        
        // 着装图片
        detailSB.append(MontBellUtil.composeDressOnMiaoshu(item.dressOnPics));

        // 尺寸描述
        detailSB.append(MontBellUtil.composeSizeTipMiaoshu(item));
        
        //String extraMiaoshu = MontBellUtil.composeExtraMiaoshu();
        String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
        obj.description =  "\"" + detailSB.toString()  +extraMiaoshu1+ "\"";
    }

    public MontbellShoeBaobeiProducer addScanCategory(String scanCategoryId) {

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