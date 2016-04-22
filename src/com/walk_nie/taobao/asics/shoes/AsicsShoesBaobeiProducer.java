package com.walk_nie.taobao.asics.shoes;

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
import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class AsicsShoesBaobeiProducer  extends BaseBaobeiProducer{
    private List<String> urls = Lists.newArrayList();
	
	public void process() {
		BufferedWriter priceBw = null;
		try {
            System.out.println("-------- START --------");
            List<AsicsShoesObject> itemIdList = getParser().parse(urls);
			 
			if (itemIdList.isEmpty())
				return;
			String outFilePathPrice = String.format(outputFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			File csvFile = new File(outFilePathPrice);
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
            for (AsicsShoesObject obj : itemIdList) {
                AsicsShoesUtil.downloadPicture(obj,  AsicsShoesUtil.rootPathName);
            }
            
			String picFolder = TaobaoUtil.getPictureFolder(csvFile);
			
			for (AsicsShoesObject obj : itemIdList) {
                TaobaoUtil.copyFiles(obj.picNameList,MontBellUtil.rootPathName, picFolder);
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
	
	protected void writeOut(BufferedWriter priceBw, AsicsShoesObject item)
			throws Exception {
		priceBw.write(composeBaobeiLine(item));
		priceBw.flush();
	}
	protected String composeBaobeiLine(AsicsShoesObject item) throws Exception {
        BaobeiPublishObject obj = new BaobeiPublishObject();
        BaobeiUtil.setBaobeiCommonInfo(obj);

		// 宝贝名称
		obj.title = composeBaobeiTitle(item);
        // 宝贝类目
        obj.cid = composeBaobeiTaobaoCategory(item,obj);
        // 店铺类目
        obj.seller_cids = composeBaobeiMyCategory(item,obj);
		// 宝贝价格
		obj.price = item.priceCNY;
		// 宝贝数量
		obj.num = "9999";
		// 用户输入ID串;
		obj.inputPids = "\"20000,13021751,6103476\"";
		// 用户输入名-值对
		obj.inputValues = "\"montbell,"+item.kataban+",*\"";
		// 宝贝描述
		obj.description = composeBaobeiMiaoshu(item);
		// Array[0]:宝贝属性;1:销售属性组合;2:销售属性别名;3:宝贝主图;4:销售属性图片
		String[] props = composeBaobeiPropColor(item,obj);
		// 宝贝属性
		String str = obj.cateProps + props[0];
		str = str.replaceAll("\"\"", "");
		str = props[0];
		obj.cateProps = str;
		// 销售属性组合
		if("\"\"".equals(obj.skuProps)){
			obj.skuProps = props[1];
		}else{
			obj.skuProps = obj.skuProps + props[1];
		}
		obj.skuProps = props[1];
		// 商家编码
		obj.outer_id = item.kataban;
		// 销售属性别名
		if("\"\"".equals(obj.skuProps)){
			obj.propAlias = props[2];
		}else{
			obj.propAlias = obj.propAlias + props[2];
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

	private String composeBaobeiTaobaoCategory(AsicsShoesObject item,
            BaobeiPublishObject baobeiTemplate) {
	    String categoryId = baobeiTemplate.cid;
        return categoryId;
    }

    private String composeBaobeiMyCategory(AsicsShoesObject item,
            BaobeiPublishObject baobeiTemplate) {

        // TODO
        String categoryId = baobeiTemplate.cid;
        return categoryId;
    }

    private String composeBaobeiSubtitle(AsicsShoesObject item) {
		return "\"日本直邮！100%正品！真正的日本代购！包邮！" + item.titleOrg + "\"";
	}
	private String composeBaobeiTitle(AsicsShoesObject item) {
		String title = "\"日本直邮";
        title += " " + item.titleCN;
        title += " mont-bell #" + item.kataban;
		if(item.gender != null && !item.gender.equals("")){
			title += " " + item.gender;
		}
//		String suffix = "/包邮";
//		if (title.length() + suffix.length() < 60) {
//			title += suffix;
//		}
		return title + "\"";
	}
	
	private String[] composeBaobeiPropColor(AsicsShoesObject item,
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
		for(int i=0;i<item.picLocalFileNameList.size();i++){
			if(i==5) break;
			skuPropPic += item.picLocalFileNameList.get(i) + ":1:" + i +":|;";
			picStatus +="2;";
		}
		int i = 0;
		for(String color :item.colorNameList){
			if(i>=taobaoColors.size())break;
			// 宝贝属性格式  1627207=color; 20509=衣服大小; 20549=鞋码
			// 
			cateProps +="1627207:"+taobaoColors.get(i)+";";
			// 销售属性组合格式 价格:数量:SKU:1627207:28320;
			skuProps += item.priceCNY +":9999"+":"+":1627207"+":"+taobaoColors.get(i)+";20549:44911;";
			// 销售属性别名格式 1627207:28320:颜色1;
			//propAlias +="1627207:"+taobaoColors.get(i)+":" +Util.convertColor(color)+";";
			propAlias += "1627207:" + taobaoColors.get(i) + ":" + color + ";";
			if(item.picNameList.size() == item.colorList.size()){
				skuPropPic += item.picNameList.get(i) + ":2:0:1627207:" + taobaoColors.get(i) +"|;";
				picStatus +="2;";
			}
			i++;
		}
		
		propAlias += "20549:44911:请留言;";
		return new String[]{
				"\""+cateProps+"\"","\""+skuProps+"\"" ,"\""+propAlias+"\"" 
				,"\""+picStatus+"\"" ,"\""+skuPropPic+"\"" };
	}
	
	private  String composeBaobeiMiaoshu(AsicsShoesObject item) throws IOException {
		
		StringBuffer detailSB = new StringBuffer();
		String productInfo = item.detailScreenShotPicFile;
		if(!StringUtil.isBlank(productInfo)){
			detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝说明</h3>");
			detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
			detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;\" src=\"FILE:///" + productInfo + "\"/></p>");
			detailSB.append("</div>");
		}
		if(item.picUrlList.size()>5){
	        detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝图片</h3>");
	        detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
            for (int i = 5; i < item.picUrlList.size(); i++) {
	            detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;\" src=\"" + item.picLocalFileNameList.get(i) + "\"/></p>");
	        }
	        detailSB.append("</div>");
		}
		
		StringBuffer sizeTips = new StringBuffer();
		// TODO
		String sizeTip = "";
		detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">尺寸参考</h3>");
		detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
		detailSB.append("<p>下单前，请认真比对尺寸大小！<span style=\";color:red;font-weight:bold\">不能因为尺寸问题 不能取消订单！！不能退款！！！</span></p>");
		detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;\" src=\"" + sizeTip + "\"/></p>");	
		detailSB.append("</div>");

        String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
		return "\"" + detailSB.toString() +sizeTips.toString()+ extraMiaoshu1 + "\"";
	}

    @Override
    public AsicsShoesParser getParser() {
        return new AsicsShoesParser();
    }
    
    public AsicsShoesBaobeiProducer addParseUrl(String url){
        urls.add(url);
        return this;
    }

}
