package com.walk_nie.taobao.montBell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.jsoup.helper.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MontbellBaobeiProducer {
	
	private String taobeiTemplateFile = "";
	private String publishedBaobeiFile = "";
	private String miaoshuTemplateFile = "";
	private String outputFile = "";
	private List<String> scanCategoryIds = Lists.newArrayList();
	
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
			String picFolder = TaobaoUtil.getPictureFolder(csvFile);
			for (GoodsObject obj : itemIdList) {
			    // TODO
				//MontBellUtil.downloadPicture(obj, picFolder);
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
		BaobeiPublishObject baobeiTemplate = new BaobeiPublishObject();
		
		BufferedReader br = null;
		try {
			File file = new File(taobeiTemplateFile);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-16"));
			String str = null;
			while ((str = br.readLine()) != null) {
				if (!StringUtil.isBlank(str) && !str.startsWith("#")) {
					baobeiTemplate = TaobaoUtil.readBaobeiIn(str);
				}
			}
		} finally {
			if (br != null)
				br.close();
		}
		
		priceBw.write(composeBaobeiLine(item, baobeiTemplate));
		priceBw.flush();
	}
	protected String composeBaobeiLine(GoodsObject item,BaobeiPublishObject baobeiTemplate) throws Exception {
		BaobeiPublishObject obj = TaobaoUtil.copyTaobaoTemplate(baobeiTemplate);

		// 宝贝名称
		obj.title = composeBaobeiTitle(item);
        // 宝贝类目
        obj.cid = composeBaobeiTaobaoCategory(item,baobeiTemplate);
        // 店铺类目
        obj.seller_cids = composeBaobeiMyCategory(item,baobeiTemplate);
		// 宝贝价格
		obj.price = item.priceCNY;
		// 宝贝数量
		obj.num = "9999";
		// 省
		obj.location_state = "日本";
		
		// 用户输入ID串;
		obj.inputPids = "\"20000,13021751,6103476\"";
		// 用户输入名-值对
		obj.inputValues = "\"montbell,"+item.productId+",*\"";
		
		// 宝贝描述
		obj.description = composeBaobeiMiaoshu(item);
		
		// Array[0]:宝贝属性;1:销售属性组合;2:销售属性别名;3:宝贝主图;4:销售属性图片
		String[] props = composeBaobeiPropColor(item,baobeiTemplate);
		// 宝贝属性
		String str = baobeiTemplate.cateProps + props[0];
		str = str.replaceAll("\"\"", "");
		str = props[0];
		obj.cateProps = str;
		// 销售属性组合
		if("\"\"".equals(baobeiTemplate.skuProps)){
			obj.skuProps = props[1];
		}else{
			obj.skuProps = baobeiTemplate.skuProps + props[1];
		}
		obj.skuProps = props[1];
		// 商家编码
		obj.outer_id = item.productId;
		// 销售属性别名
		if("\"\"".equals(baobeiTemplate.skuProps)){
			obj.propAlias = props[2];
		}else{
			obj.propAlias = baobeiTemplate.propAlias + props[2];
		}
		obj.propAlias = props[2];
		// 商品条形码
		//obj.barcode = item.sku;
		// 图片状态
		obj.picture_status = props[3];
		// 新图片
		obj.picture = props[4];
		// 宝贝卖点
		obj.subtitle = composeBaobeiSubtitle(item);

		return TaobaoUtil.composeTaobaoLine(obj);
	}

	private String composeBaobeiTaobaoCategory(GoodsObject item,
            BaobeiPublishObject baobeiTemplate) {
	    String categoryId = baobeiTemplate.cid;
	    String prodCId = item.cateogryObj.categoryId;
        // TODO
        if (MontBellUtil.isCateogryRainClothes(prodCId) ) {
            // レインウェア 户外雨衣
            categoryId = "";
        }
        if (MontBellUtil.isCateogrySoftShell1(prodCId)
                || MontBellUtil.isCateogryHardShell1(prodCId)) {
            // ソフトシェルジャケット + ハードシェル>ジャケット 冲锋衣
            categoryId = "";
        }
        if (MontBellUtil.isCateogrySoftShell2(prodCId)
                || MontBellUtil.isCateogryHardShell2(prodCId)) {
            // ソフトシェルパンツ + ハードシェル>パンツ 冲锋裤
            categoryId = "";
        }
        if (MontBellUtil.isCateogryFreece1(prodCId) ) {
            // フリース 抓绒衣 抓绒衣
            categoryId = "";
        }
        if (MontBellUtil.isCateogryFreece2(prodCId) ) {
            // フリースパンツ 抓绒裤 
            categoryId = "";
        } 
        if (MontBellUtil.isCateogryPack(prodCId) ) {
            // 大型ザック
            categoryId = "";
        } 
        return categoryId;
    }

    private String composeBaobeiMyCategory(GoodsObject item,
            BaobeiPublishObject baobeiTemplate) {

        // TODO
        String categoryId = baobeiTemplate.cid;
        String prodCId = item.cateogryObj.categoryId;
        if (MontBellUtil.isCateogryRainClothes(prodCId) ) {
            // レインウェア 户外雨衣
            categoryId = "";
        }
        if (MontBellUtil.isCateogrySoftShell1(prodCId)
                || MontBellUtil.isCateogryHardShell1(prodCId)) {
            // ソフトシェルジャケット + ハードシェル>ジャケット 冲锋衣
            categoryId = "";
        }
        if (MontBellUtil.isCateogrySoftShell2(prodCId)
                || MontBellUtil.isCateogryHardShell2(prodCId)) {
            // ソフトシェルパンツ + ハードシェル>パンツ 冲锋裤
            categoryId = "";
        }
        if (MontBellUtil.isCateogryFreece1(prodCId) ) {
            // フリース 抓绒衣 抓绒衣
            categoryId = "";
        }
        if (MontBellUtil.isCateogryFreece2(prodCId) ) {
            // フリースパンツ 抓绒裤 
            categoryId = "";
        } 
        if (MontBellUtil.isCateogryPack(prodCId) ) {
            // 大型ザック
            categoryId = "";
        } 
        return categoryId;
    }

    private String composeBaobeiSubtitle(GoodsObject item) {
		return "\"日本直邮！100%正品！真正的日本代购！包邮！" + item.titleOrg + "\"";
	}
	private String composeBaobeiTitle(GoodsObject item) {
		String title = "\"日本直邮";
        title += " " + item.titleCN;
        title += " mont-bell #" + item.productId;
		if(item.gender != null && !item.gender.equals("")){
			title += " " + item.gender;
		}
//		String suffix = "/包邮";
//		if (title.length() + suffix.length() < 60) {
//			title += suffix;
//		}
		return title + "\"";
	}
	
	private String[] composeBaobeiPropColor(GoodsObject item,
			BaobeiPublishObject baobeiTemplate) {
		List<String> taobaoColors = Lists.newArrayList();
		taobaoColors.add("28320");taobaoColors.add("28340");taobaoColors.add("3232479");
		taobaoColors.add("3232478");taobaoColors.add("3232482");taobaoColors.add("60092");
		taobaoColors.add("30156");taobaoColors.add("28332");taobaoColors.add("90554");
		taobaoColors.add("3232481");taobaoColors.add("3232484");taobaoColors.add("3232483");
		// 颜色值:28320 28324 28326 28327 28329 28332 28340 28338 28335
		// 宝贝属性 -销售属性组合- 销售属性别名
		String cateProps = "";String skuProps = "";String propAlias = "";
		String picStatus = "";String skuPropPic = "";
		for(int i=0;i<item.pictureNameList.size();i++){
			if(i==5) break;
			skuPropPic += item.pictureNameList.get(i) + ":1:" + i +":|;";
			picStatus +="2;";
		}
		int i = 0;
		for(String color :item.colorList){
			if(i>=taobaoColors.size())break;
			// 宝贝属性格式  1627207=color; 20509=衣服大小; 20549=鞋码
			// 
			cateProps +="1627207:"+taobaoColors.get(i)+";";
			// 销售属性组合格式 价格:数量:SKU:1627207:28320;
			skuProps += item.priceCNY +":9999"+":"+":1627207"+":"+taobaoColors.get(i)+";20549:44911;";
			// 销售属性别名格式 1627207:28320:颜色1;
			//propAlias +="1627207:"+taobaoColors.get(i)+":" +Util.convertColor(color)+";";
			propAlias += "1627207:" + taobaoColors.get(i) + ":" + color + ";";
			if(item.pictureNameList.size() == item.colorList.size()){
				skuPropPic += item.pictureNameList.get(i) + ":2:0:1627207:" + taobaoColors.get(i) +"|;";
				picStatus +="2;";
			}
			i++;
		}
		
		propAlias += "20549:44911:请留言;";
		return new String[]{
				"\""+cateProps+"\"","\""+skuProps+"\"" ,"\""+propAlias+"\"" 
				,"\""+picStatus+"\"" ,"\""+skuPropPic+"\"" };
	}
	
	private  String composeBaobeiMiaoshu(GoodsObject item) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			File file = new File(getMiaoshuTemplateFile());
			String str = null;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
		} finally {
			if (br != null)
				br.close();
		}
		
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
		return "\"" + detailSB.toString() +sizeTips.toString()+ sb.toString() + "\"";
	}
	
	public MontbellBaobeiProducer setTaobeiTemplateFile(String taobeiTemplateFile) {
		this.taobeiTemplateFile = taobeiTemplateFile;
		return this;
	}

	public MontbellBaobeiProducer setPublishedBaobeiFile(String publishedBaobeiFile) {
		this.publishedBaobeiFile = publishedBaobeiFile;
		return this;
	}

	public MontbellBaobeiProducer setMiaoshuTemplateFile(
			String miaoshuTemplateFile) {
		this.miaoshuTemplateFile = miaoshuTemplateFile;
		return this;
	}

	public String getMiaoshuTemplateFile() {
		return this.miaoshuTemplateFile;
	}

	public MontbellBaobeiProducer setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}

	public MontbellBaobeiProducer addScanCategory(String scanCategoryId) {

		this.scanCategoryIds.add(scanCategoryId);
		return this;
	}

}
