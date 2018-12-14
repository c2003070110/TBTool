package com.walk_nie.taobao.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.io.Files;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.object.TaobaoSaledObject;

public class TaobaoUtil {
    public static String FILE_NAME_SEPERATOR =";;;";
    
	public static Document urlToDocumentByUTF8(String url)
			throws IOException {
		return TaobaoUtil.urlToDocument(url, "UTF-8");
	}

	public static Document urlToDocument(String url,String charset)
			throws ClientProtocolException, IOException {

		System.out.println("[START]parse URL = " + url);
		
//		String PROXY_HOST = "172.16.200.2";
//		int PROXY_PORT = 8080;
//		String PROXY_USER = "546736om";
//		String PROXY_PASS = "nie1234";
//	  HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(
//                new AuthScope(proxy),
//                new UsernamePasswordCredentials(PROXY_USER, PROXY_PASS));
//        RequestConfig config = RequestConfig.custom()
//                .setProxy(proxy)
//                .build();
//        HttpClient client = HttpClients.custom()
//                .setDefaultCredentialsProvider(credsProvider)
//                .setDefaultRequestConfig(config)
//                .build();
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpUriRequest req = new HttpGet(url);
		HttpResponse res = client.execute(req);
		BufferedReader rd = new BufferedReader(new InputStreamReader(res
				.getEntity().getContent(), charset));
		String line = "";
		StringBuffer sbHtml = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			sbHtml.append(line);
			sbHtml.append("\n");
		}
		
		//client.close();
		Document doc = Jsoup.parse(sbHtml.toString());
		// System.out.println("[END]parse URL =" + url);
		return doc;
	}

	public static String composeTaobaoLine(BaobeiPublishObject obj) {
		String split = "\t";
		StringBuffer sb = new StringBuffer();
		// 宝贝名称
		sb.append(obj.title).append(split);
		// 宝贝类目
		sb.append(obj.cid).append(split);
		// 店铺类目
		sb.append(obj.seller_cids).append(split);
		// 新旧程度
		sb.append(obj.stuff_status).append(split);
		// 省
		sb.append(obj.location_state).append(split);
		// 城市
		sb.append(obj.location_city).append(split);
		// 出售方式
		sb.append(obj.item_type).append(split);
		// 宝贝价格
		sb.append(obj.price).append(split);
		// 加价幅度
		sb.append(obj.auction_increment).append(split);
		// 宝贝数量
		sb.append(obj.num).append(split);
		// 有效期
		sb.append(obj.valid_thru).append(split);
		// 运费承担
		sb.append(obj.freight_payer).append(split);
		// 平邮
		sb.append(obj.post_fee).append(split);
		// EMS
		sb.append(obj.ems_fee).append(split);
		// 快递
		sb.append(obj.express_fee).append(split);
		// 发票
		sb.append(obj.has_invoice).append(split);
		// 保修
		sb.append(obj.has_warranty).append(split);
		// 放入仓库
		sb.append(obj.approve_status).append(split);
		// 橱窗推荐
		sb.append(obj.has_showcase).append(split);
		// 开始时间
		sb.append(obj.list_time).append(split);
		// 宝贝描述
		sb.append(obj.description).append(split);
		// 0:宝贝属性 1:销售属性组合 2:销售属性别名
		sb.append(obj.cateProps).append(split);
		// 邮费模版ID
		sb.append(obj.postage_id).append(split);
		// 会员打折
		sb.append(obj.has_discount).append(split);
		// 修改时间
		sb.append(obj.modified).append(split);
		// 上传状态
		sb.append(obj.upload_fail_msg).append(split);
		// 图片状态
		sb.append(obj.picture_status).append(split);
		// 返点比例
		sb.append(obj.auction_point).append(split);
		// 新图片
		sb.append(obj.picture).append(split);
		// 视频
		sb.append(obj.video).append(split);
		// 销售属性组合
		sb.append(obj.skuProps).append(split);
		// 用户输入ID串
		sb.append(obj.inputPids).append(split);
		// 用户输入名-值对
		sb.append(obj.inputValues).append(split);
		// 商家编码
		sb.append(obj.outer_id).append(split);
		// 销售属性别名
		sb.append(obj.propAlias).append(split);
		// 代充类型
		sb.append(obj.auto_fill).append(split);
		// 数字ID
		sb.append(obj.num_id).append(split);
		// 本地ID
		sb.append(obj.local_cid).append(split);
		// 宝贝分类
		sb.append(obj.navigation_type).append(split);
		// 用户名称
		sb.append(obj.user_name).append(split);
		// 宝贝状态
		sb.append(obj.syncStatus).append(split);
		// 闪电发货
		sb.append(obj.is_lighting_consigment).append(split);
		// 新品
		sb.append(obj.is_xinpin).append(split);
		// 食品专项
		sb.append(obj.foodparame).append(split);
		// 尺码库
		sb.append(obj.features).append(split);
		// 采购地
		sb.append(obj.buyareatype).append(split);
		// 库存类型
		sb.append(obj.global_stock_type).append(split);
		// 国家地区
		sb.append(obj.global_stock_country).append(split);
		// 库存计数
		sb.append(obj.sub_stock_type).append(split);
		// 物流体积
		sb.append(obj.item_size).append(split);
		// 物流重量
		sb.append(obj.item_weight).append(split);
		// 退换货承诺
		sb.append(obj.sell_promise).append(split);
		// 定制工具
		sb.append(obj.custom_design_flag).append(split);
		// 无线详情
		sb.append(obj.wireless_desc).append(split);
		// 商品条形码
		sb.append(obj.barcode).append(split);
		// sku 条形码
		sb.append(obj.sku_barcode).append(split);
		// 7天退货
		sb.append(obj.newprepay).append(split);
		// 宝贝卖点
		sb.append(obj.subtitle).append(split);
		// 属性值备注
		sb.append(obj.cpv_memo).append(split);
		// 自定义属性值
		sb.append(obj.input_custom_cpv).append(split);
		// 商品资质
		sb.append(obj.qualification).append(split);
		// 增加商品资质
		sb.append(obj.add_qualification).append(split);
		// 关联线下服务
		sb.append(obj.o2o_bind_service);

		return sb.append("\n").toString();
	}

	public static BaobeiPublishObject copyTaobaoTemplate(BaobeiPublishObject template) {
		BaobeiPublishObject obj = new BaobeiPublishObject();
		// 宝贝名称
		obj.title = template.title;
		// 宝贝类目;
		obj.cid = template.cid;
		// 店铺类目;
		obj.seller_cids = template.seller_cids;
		// 新旧程度;
		obj.stuff_status = template.stuff_status;
		// 省
		obj.location_state = template.location_state;
		// 城市
		obj.location_city = template.location_city;
		// 出售方式;
		obj.item_type = template.item_type;
		// 宝贝价格;
		obj.price = template.price;
		// 加价幅度;
		obj.auction_increment = template.auction_increment;
		// 宝贝数量
		obj.num = template.num;
		// 有效期
		obj.valid_thru = template.valid_thru;
		// 运费承担;
		obj.freight_payer = template.freight_payer;
		// 平邮;
		obj.post_fee = template.post_fee;
		// EMS;
		obj.ems_fee = template.ems_fee;
		// 快递
		obj.express_fee = template.express_fee;
		// 发票;
		obj.has_invoice = template.has_invoice;
		// 保修;
		obj.has_warranty = template.has_warranty;
		// 放入仓库;
		obj.approve_status = template.approve_status;
		// 橱窗推荐
		obj.has_showcase = template.has_showcase;
		// 开始时间
		obj.list_time = template.list_time;
		// 宝贝描述;
		obj.description = template.description;
		// 宝贝属性;
		obj.cateProps = template.cateProps;
		// 邮费模版ID;
		obj.postage_id = template.postage_id;
		// 会员打折
		obj.has_discount = template.has_discount;
		// 修改时间
		obj.modified = template.modified;
		// 上传状态;
		obj.upload_fail_msg = template.upload_fail_msg;
		// 图片状态;
		obj.picture_status = template.picture_status;
		// 返点比例;
		obj.auction_point = template.auction_point;
		// 新图片
		obj.picture = template.picture;
		// 视频;
		obj.video = template.video;
		// 销售属性组合;
		obj.skuProps = template.skuProps;
		// 用户输入ID串;
		obj.inputPids = template.inputPids;
		// 用户输入名-值对
		obj.inputValues = template.inputValues;
		// 商家编码
		obj.outer_id = template.outer_id;
		// 销售属性别名;
		obj.propAlias = template.propAlias;
		// 代充类型;
		obj.auto_fill = template.auto_fill;
		// 数字ID;
		obj.num_id = template.num_id;
		// 本地ID
		obj.local_cid = template.local_cid;
		// 宝贝分类
		obj.navigation_type = template.navigation_type;
		// 用户名称;
		obj.user_name = template.user_name;
		// 宝贝状态;
		obj.syncStatus = template.syncStatus;
		// 闪电发货;
		obj.is_lighting_consigment = template.is_lighting_consigment;
		// 新品
		obj.is_xinpin = template.is_xinpin;
		// 食品专项;
		obj.foodparame = template.foodparame;
		// 尺码库;
		obj.features = template.features;
		// 采购地;
		obj.buyareatype = template.buyareatype;
		// 库存类型
		obj.global_stock_type = template.global_stock_type;
		// 国家地区
		obj.global_stock_country = template.global_stock_country;
		// 库存计数;
		obj.sub_stock_type = template.sub_stock_type;
		// 物流体积;
		obj.item_size = template.item_size;
		// 物流重量;
		obj.item_weight = template.item_weight;
		// 退换货承诺
		obj.sell_promise = template.sell_promise;
		// 定制工具
		obj.custom_design_flag = template.custom_design_flag;
		// 无线详情;
		obj.wireless_desc = template.wireless_desc;
		// 商品条形码;
		obj.barcode = template.barcode;
		// sku 条形码;
		obj.sku_barcode = template.sku_barcode;
		// 7天退货
		obj.newprepay = template.newprepay;
		// 宝贝卖点;
		obj.subtitle = template.subtitle;
		// 属性值备注;
		obj.cpv_memo = template.cpv_memo;
		// 自定义属性值;
		obj.input_custom_cpv = template.input_custom_cpv;
		// 商品资质
		obj.qualification = template.qualification;
		// 增加商品资质
		obj.add_qualification = template.add_qualification;
		// 关联线下服务;
		obj.o2o_bind_service = template.o2o_bind_service;

		return obj;
	}
	public static BaobeiPublishObject readInBaobeiTemplate(String taobeiTemplateFile) throws IOException {
		BufferedReader br = null;
		BaobeiPublishObject baobeiTemplate = null;
		try {
			File file = new File(taobeiTemplateFile);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-16"));
			String str = null;
			int idx = 0;
			while ((str = br.readLine()) != null) {
				idx++;
				if(idx == 4){
					baobeiTemplate = readBaobeiIn(str);
				}
			}
		} finally {
			if (br != null)
				br.close();
		}
		return baobeiTemplate;
	}

	public static BaobeiPublishObject readBaobeiIn(String str) {
		String[] split = str.split("\t");
		if (split.length == 1)
			return null;
		if (split[0].equals("title")) {
			return null;
		}
		if (split[0].equals("宝贝名称")) {
			return null;
		}
		BaobeiPublishObject obj = new BaobeiPublishObject();
		int idx = 0;

		// 宝贝名称
		obj.title = remove(split[idx++]);
		// 宝贝类目
		obj.cid = remove(split[idx++]);
		// 店铺类目
		obj.seller_cids = remove(split[idx++]);
		// 新旧程度
		obj.stuff_status = remove(split[idx++]);
		// 省
		obj.location_state = remove(split[idx++]);
		// 城市
		obj.location_city = remove(split[idx++]);
		// 出售方式
		obj.item_type = remove(split[idx++]);
		// 宝贝价格
		obj.price = remove(split[idx++]);
		// 加价幅度
		obj.auction_increment = remove(split[idx++]);
		// 宝贝数量
		obj.num = remove(split[idx++]);
		// 有效期
		obj.valid_thru = remove(split[idx++]);
		// 运费承担
		obj.freight_payer = remove(split[idx++]);
		// 平邮
		obj.post_fee = remove(split[idx++]);
		// EMS
		obj.ems_fee = remove(split[idx++]);
		// 快递
		obj.express_fee = remove(split[idx++]);
		// 发票
		obj.has_invoice = remove(split[idx++]);
		// 保修
		obj.has_warranty = remove(split[idx++]);
		// 放入仓库
		obj.approve_status = remove(split[idx++]);
		// 橱窗推荐
		obj.has_showcase = remove(split[idx++]);
		// 开始时间
		obj.list_time = remove(split[idx++]);
		// 宝贝描述
		obj.description = split[idx++];
		// 宝贝属性
		obj.cateProps = split[idx++];
		// 邮费模版ID
		obj.postage_id = split[idx++];
		// 会员打折
		obj.has_discount = split[idx++];
		// 修改时间
		obj.modified = split[idx++];
		// 上传状态
		obj.upload_fail_msg = split[idx++];
		// 图片状态
		obj.picture_status = split[idx++];
		// 返点比例
		obj.auction_point = split[idx++];
		// 新图片
		obj.picture = split[idx++];
		// 视频
		obj.video = split[idx++];
		// 销售属性组合
		obj.skuProps = split[idx++];
		// 用户输入ID串
		obj.inputPids = split[idx++];
		// 用户输入名-值对
		obj.inputValues = split[idx++];
		// 商家编码
		obj.outer_id = remove(split[idx++]);
		// 销售属性别名
		obj.propAlias = split[idx++];
		// 代充类型
		obj.auto_fill = split[idx++];
		// 数字ID
		obj.num_id = split[idx++];
		// 本地ID
		obj.local_cid = split[idx++];
		// 宝贝分类
		obj.navigation_type = split[idx++];
		// 用户名称
		obj.user_name = split[idx++];
		// 宝贝状态
		obj.syncStatus = split[idx++];
		// 闪电发货
		obj.is_lighting_consigment = split[idx++];
		// 新品
		obj.is_xinpin = split[idx++];
		// 食品专项
		obj.foodparame = split[idx++];
		// 尺码库
		obj.features = split[idx++];
		// 采购地
		obj.buyareatype = split[idx++];
		// 库存类型
		obj.global_stock_type = split[idx++];
		// 国家地区
		obj.global_stock_country = split[idx++];
		// 库存计数
		obj.sub_stock_type = split[idx++];
		// 物流体积
		obj.item_size = split[idx++];
		// 物流重量
		obj.item_weight = split[idx++];
		// 退换货承诺
		obj.sell_promise = split[idx++];
		// 定制工具
		obj.custom_design_flag = split[idx++];
		// 无线详情
		obj.wireless_desc = split[idx++];
		// 商品条形码
		obj.barcode = split[idx++];
		// sku 条形码
		obj.sku_barcode = split[idx++];
		// 7天退货
		obj.newprepay = split[idx++];
		// 宝贝卖点
		obj.subtitle = split[idx++];
		// 属性值备注
		obj.cpv_memo = split[idx++];
		// 自定义属性值
		obj.input_custom_cpv = split[idx++];
		// 商品资质
		obj.qualification = split[idx++];
		// 增加商品资质
		obj.add_qualification = split[idx++];
		// 关联线下服务
		if(split.length > idx){
			obj.o2o_bind_service = split[idx++];
		}else{
			obj.o2o_bind_service = "";
		}
		return obj;
	}

    public static TaobaoSaledObject readTaobaoSaledIn(String str) {
        String[] split = str.split(",");
        if (split.length == 1)
            return null;
        if (split[0].equals("title")) {
            return null;
        }
        if (split[0].equals("宝贝名称")) {
            return null;
        }
        TaobaoSaledObject obj = new TaobaoSaledObject();
        int idx = 0;

        // 订单编号
        obj.orderNo = remove(split[idx++]);
        // 买家会员名
        obj.buyerId = remove(split[idx++]);
        // 买家支付宝账号
        obj.buyerZhifubaoId = remove(split[idx++]);
        // 买家应付货款
        obj.amount = remove(split[idx++]);
        // 买家应付邮费
        obj.logiFee = remove(split[idx++]);
        // 买家支付积分
        obj.amountForBuyerPaid = remove(split[idx++]);
        // 总金额
        obj.totalAmount = remove(split[idx++]);
        // 返点积分
        obj.point = remove(split[idx++]);
        // 买家实际支付金额
        obj.amountForBuyerPaid1 = remove(split[idx++]);
        // 买家实际支付积分
        obj.pointForBuyerPaid1 = remove(split[idx++]);
        // 订单状态
        obj.orderStatus = remove(split[idx++]);
        // 买家留言
        obj.buyerNote = remove(split[idx++]);
        // 收货人姓名
        obj.buyerName = remove(split[idx++]);
        // 收货地址
        obj.buyerAddress = remove(split[idx++]);
        // 运送方式
        obj.LogiType = remove(split[idx++]);
        // 联系电话
        obj.tel = remove(split[idx++]).replaceAll("'", "");
        // 联系手机
        obj.telMobile = remove(split[idx++]).replaceAll("'", "");
        // 订单创建时间
        obj.orderCreatedDateTime = remove(split[idx++]);
        // 订单付款时间
        obj.orderPaidDateTime = remove(split[idx++]);
        // 宝贝标题
        obj.baobeiTitle = remove(split[idx++]);
        // 宝贝种类
        obj.baobeiCategory = remove(split[idx++]);
        // 物流单号
        obj.LogiNo = remove(split[idx++]);
        // 物流公司
        obj.logiCompany = remove(split[idx++]);
        // 订单备注
        obj.orderNote = remove(split[idx++]);
        // 宝贝总数量
        obj.baobeiNum = Integer.parseInt(remove(split[idx++]));
        // 店铺Id
        obj.storeId = remove(split[idx++]);
        // 店铺名称
        obj.storeName = remove(split[idx++]);
        // 订单关闭原因
        obj.reasonForClose = remove(split[idx++]);
        // 卖家服务费
        obj.feeForSaler = remove(split[idx++]);
        // 买家服务费
        obj.feeForBuyer = remove(split[idx++]);
        // 发票抬头
        obj.recieptHeader = remove(split[idx++]);
        // 是否手机订单
        obj.isMobileOrder = remove(split[idx++]);
        // 分阶段订单信息
        obj.orderInfoForSeg = remove(split[idx++]);
        // 特权订金订单id
        obj.orderIdForPrepay = remove(split[idx++]);
        // 定金排名
        obj.prePayRank = remove(split[idx++]);
        // 修改后的sku
        obj.skuAdjust = remove(split[idx++]);
        // 修改后的收货地址
        obj.addressAdjust = remove(split[idx++]);
        // 异常信息
        obj.exceptionInfo = remove(split[idx++]);
        // 天猫卡券抵扣
        obj.discountForTianmao = remove(split[idx++]);
        // 集分宝抵扣
        obj.discountForJifenbao = remove(split[idx++]);
        // 是否是O2O交易
        obj.is020 = remove(split[idx++]);
        return obj;
    }

	private static String remove(String str) {

    	str = str.replaceAll("=\"", "");
    	str = str.replaceAll("\"", "");
		return str;
	}

	public static File downloadPicture(String pathName, String pictureUrl,
			String picName,String pathToWaterMarkPNG) throws ClientProtocolException, IOException {

		//String photoName = "out/" + itemType;
		File path = new File(pathName);
		if (!path.exists()) {
			path.mkdirs();
		}
		File downloadFile = new File(path, picName + ".jpg");
		if(downloadFile.exists()){
		    return downloadFile;
		}
		//File downloadFile = new File(path, picName + ".tbi");
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpUriRequest req = new HttpGet(pictureUrl);

		HttpResponse res1 = client.execute(req);
		BufferedImage originalImage = ImageIO.read(res1.getEntity()
				.getContent());
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
				: originalImage.getType();
		int originalH = originalImage.getHeight();
		int originalW = originalImage.getWidth();

		BufferedImage resizedImage = new BufferedImage(originalW, originalH,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage.getScaledInstance(originalW, originalH,
				Image.SCALE_AREA_AVERAGING), 0, 0, originalW, originalH, null);
		if (!StringUtil.isBlank(pathToWaterMarkPNG)) {
			ImageIcon imgIcon = new ImageIcon(pathToWaterMarkPNG);
			int interval = 0;
			Image img = imgIcon.getImage();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					0.15f/* 水印透明度 */));
			for (int height = interval + imgIcon.getIconHeight(); height < resizedImage
					.getHeight(); height = height + interval
					+ imgIcon.getIconHeight()) {
				for (int weight = interval + imgIcon.getIconWidth(); weight < resizedImage
						.getWidth(); weight = weight + interval
						+ imgIcon.getIconWidth()) {
					g.drawImage(img, weight - imgIcon.getIconWidth(), height
							- imgIcon.getIconHeight(), null);
				}
			}
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		}
		g.dispose();
		ImageIO.write(resizedImage, "jpg", downloadFile);
		return downloadFile;
	}

	public static File downloadPicture(String pathName, String pictureUrl,
			String picName) throws ClientProtocolException, IOException {
		return downloadPicture(pathName, pictureUrl, picName, null);
	}
    
	public static String composeTaobaoHeaderLine() {
		StringBuffer sb = new StringBuffer();
		sb.append("version 1.00");
		sb.append("\n");
		sb.append("title	cid	seller_cids	stuff_status	location_state	location_city	item_type	price	auction_increment	num	valid_thru	freight_payer	post_fee	ems_fee	express_fee	has_invoice	has_warranty	approve_status	has_showcase	list_time	description	cateProps	postage_id	has_discount	modified	upload_fail_msg	picture_status	auction_point	picture	video	skuProps	inputPids	inputValues	outer_id	propAlias	auto_fill	num_id	local_cid	navigation_type	user_name	syncStatus	is_lighting_consigment	is_xinpin	foodparame	features	buyareatype	global_stock_type	global_stock_country	sub_stock_type	item_size	item_weight	sell_promise	custom_design_flag	wireless_desc	barcode	sku_barcode	newprepay	subtitle	cpv_memo	input_custom_cpv	qualification	add_qualification	o2o_bind_service");
		sb.append("\n");
		sb.append("宝贝名称	宝贝类目	店铺类目	新旧程度	省	城市	出售方式	宝贝价格	加价幅度	宝贝数量	有效期	运费承担	平邮	EMS	快递	发票	保修	放入仓库	橱窗推荐	开始时间	宝贝描述	宝贝属性	邮费模版ID	会员打折	修改时间	上传状态	图片状态	返点比例	新图片	视频	销售属性组合	用户输入ID串	用户输入名-值对	商家编码	销售属性别名	代充类型	数字ID	本地ID	宝贝分类	用户名称	宝贝状态	闪电发货	新品	食品专项	尺码库	采购地	库存类型	国家地区	库存计数	物流体积	物流重量	退换货承诺	定制工具	无线详情	商品条形码	sku 条形码	7天退货	宝贝卖点	属性值备注	自定义属性值	商品资质	增加商品资质	关联线下服务");
		sb.append("\n");
		return sb.toString();
	}

	public static int getSeaFee(int weight) {
		int base = 1600;int crt = 300;
		if(weight <=1000){
			return base + crt * 0;
		}
		if(weight <=2000){
			return base + crt * 1;
		}
		if(weight <=3000){
			return base + crt * 2;
		}
		if(weight <=4000){
			return base + crt * 3;
		}
		if(weight <=5000){
			return base + crt * 4;
		}
		if (weight <= 6000) {
			return base + crt * 5;
		}
		if (weight <= 7000) {
			return base + crt * 6;
		}
		if (weight <= 8000) {
			return base + crt * 7;
		}
		if (weight <= 9000) {
			return base + crt * 8;
		}
		if (weight <= 10000) {
			return base + crt * 9;
		}
		base = 4300;crt = 250;
		if (weight <= 11000) {
			return base + crt * 1;
		}
		if (weight <= 12000) {
			return base + crt * 2;
		}
		if (weight <= 13000) {
			return base + crt * 3;
		}
		if (weight <= 14000) {
			return base + crt * 4;
		}
		if (weight <= 15000) {
			return base + crt * 5;
		}
		if (weight <= 16000) {
			return base + crt * 6;
		}
		if (weight <= 17000) {
			return base + crt * 7;
		}
		if (weight <= 18000) {
			return base + crt * 8;
		}
		if (weight <= 19000) {
			return base + crt * 9;
		}
		if (weight <= 20000) {
			return base + crt * 10;
		}
		if (weight <= 21000) {
			return base + crt * 11;
		}
		if (weight <= 22000) {
			return base + crt * 12;
		}
		if (weight <= 23000) {
			return base + crt * 13;
		}
		if (weight <= 24000) {
			return base + crt * 14;
		}
		if (weight <= 25000) {
			return base + crt * 15;
		}
		if (weight <= 26000) {
			return base + crt * 16;
		}
		if (weight <= 27000) {
			return base + crt * 17;
		}
		if (weight <= 28000) {
			return base + crt * 18;
		}
		if (weight <= 29000) {
			return base + crt * 19;
		}
		if (weight <= 30000) {
			return base + crt * 29;
		}
		return 999999999;
	}

	public static int getSalFee(int weight) {
		int base = 1800;int crt = 600;
		if(weight <=1000){
			return base + crt * 0;
		}
		if(weight <=2000){
			return base + crt * 1;
		}
		if(weight <=3000){
			return base + crt * 2;
		}
		if(weight <=4000){
			return base + crt * 3;
		}
		if(weight <=5000){
			return base + crt * 4;
		}
		if (weight <= 6000) {
			return base + crt * 5;
		}
		base = 4700; crt = 500;
		if (weight <= 7000) {
			return base + crt * 1;
		}
		if (weight <= 8000) {
			return base + crt * 2;
		}
		if (weight <= 9000) {
			return base + crt * 3;
		}
		if (weight <= 10000) {
			return base + crt * 4;
		}
		
		base = 6700; crt = 300;
		if (weight <= 11000) {
			return base + crt * 1;
		}
		if (weight <= 12000) {
			return base + crt * 2;
		}
		if (weight <= 13000) {
			return base + crt * 3;
		}
		if (weight <= 14000) {
			return base + crt * 4;
		}
		if (weight <= 15000) {
			return base + crt * 5;
		}
		if (weight <= 16000) {
			return base + crt * 6;
		}
		if (weight <= 17000) {
			return base + crt * 7;
		}
		if (weight <= 18000) {
			return base + crt * 8;
		}
		if (weight <= 19000) {
			return base + crt * 9;
		}
		if (weight <= 20000) {
			return base + crt * 10;
		}
		if (weight <= 21000) {
			return base + crt * 11;
		}
		if (weight <= 22000) {
			return base + crt * 12;
		}
		if (weight <= 23000) {
			return base + crt * 13;
		}
		if (weight <= 24000) {
			return base + crt * 14;
		}
		if (weight <= 25000) {
			return base + crt * 15;
		}
		if (weight <= 26000) {
			return base + crt * 16;
		}
		if (weight <= 27000) {
			return base + crt * 17;
		}
		if (weight <= 28000) {
			return base + crt * 18;
		}
		if (weight <= 29000) {
			return base + crt * 19;
		}
		if (weight <= 30000) {
			return base + crt * 20;
		}
		return 999999999;
	} 

	public static int getEmsFee(int weight) {
	    int inc = 300;
//		if(weight <=300){
//			return 900;
//		}
		if(weight <=500){
			return 1100 + inc;
		}
		if(weight <=600){
			return 1240 + inc;
		}
		if(weight <=700){
			return 1380 + inc;
		}
		if(weight <=800){
			return 1520 + inc;
		}
		if(weight <=900){
			return 1660 + inc;
		}
		if(weight <=1000){
			return 1800 + inc;
		}
		int base = 2100;int crt = 300;
		if(weight <=1250){
			return base + crt * 1;
		}
		if(weight <=1500){
			return base + crt * 2;
		}
		if(weight <=1750){
			return base + crt * 3;
		}
		if(weight <=2000){
			return base + crt * 4;
		}
		
		base = 3300; crt = 500;
		if(weight <=2500){
			return base + crt * 1;
		}
		if(weight <=3000){
			return base + crt * 2;
		}
		if(weight <=3500){
			return base + crt * 3;
		}
		if(weight <=4000){
			return base + crt * 4;
		}
		if(weight <=4500){
			return base + crt * 5;
		}
		if(weight <=5000){
			return base + crt * 6;
		}
		if(weight <=5500){
			return base + crt * 7;
		}
		if(weight <=6000){
			return base + crt * 8;
		}
		
		base = 7300; crt = 800;
		if(weight <=7000){
			return base + crt * 1;
		}
		if (weight <= 8000) {
			return base + crt * 2;
		}
		if (weight <= 9000) {
			return base + crt * 3;
		}
		if (weight <= 10000) {
			return base + crt * 4;
		}
		if (weight <= 11000) {
			return base + crt * 5;
		}
		if (weight <= 12000) {
			return base + crt * 6;
		}
		if (weight <= 13000) {
			return base + crt * 7;
		}
		if (weight <= 14000) {
			return base + crt * 8;
		}
		if (weight <= 15000) {
			return base + crt * 9;
		}
		if (weight <= 16000) {
			return base + crt * 10;
		}
		if (weight <= 17000) {
			return base + crt * 11;
		}
		if (weight <= 18000) {
			return base + crt * 12;
		}
		if (weight <= 19000) {
			return base + crt * 13;
		}
		if (weight <= 20000) {
			return base + crt * 14;
		}
		if (weight <= 21000) {
			return base + crt * 15;
		}
		if (weight <= 22000) {
			return base + crt * 16;
		}
		if (weight <= 23000) {
			return base + crt * 17;
		}
		if (weight <= 24000) {
			return base + crt * 18;
		}
		if (weight <= 25000) {
			return base + crt * 19;
		}
		if (weight <= 26000) {
			return base + crt * 20;
		}
		if (weight <= 27000) {
			return base + crt * 21;
		}
		if (weight <= 28000) {
			return base + crt * 22;
		}
		if (weight <= 29000) {
			return base + crt * 23;
		}
		if (weight <= 30000) {
			return base + crt * 24;
		}
		return 999999999;
	}

    public static String getPictureFolder(File csvFile) {
        String fileName = csvFile.getParentFile().getAbsolutePath() +"/"+ csvFile.getName().replace(".csv", "");
        File file = new File(fileName);
        if(!file.exists()){
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }
    
    public static void copyFiles(List<String> pictureNameList,String fromFolder, String toFolder)
            throws IOException {
		for (String name : pictureNameList) {
			File from = new File(fromFolder, name + ".jpg");
			File to = new File(toFolder, name + ".tbi");
            Files.copy(from, to);
        }
    }


	public static String composeBaobeiCateProps(List<String> baobeiColorList, List<String> baobeiSizeList,
			List<String> taobaoColors, List<String> taobaoSizes,String sizeCateKey) {
		String cateProps = "";
		// 宝贝属性
		for (int i = 0; i < baobeiColorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			cateProps += "1627207:" + taobaoColors.get(i) + ";";
		}
		for (int i = 0; i < baobeiSizeList.size(); i++) {
			if (i >= taobaoSizes.size())
				break;
			cateProps += sizeCateKey + ":" + taobaoSizes.get(i) + ";";
		}
		return cateProps;
	}

	public static String composeBaobeiPictureStatus( List<String> baobeiPictureNameList,List<String> baobeiColorList,
			List<String> taobaoColors) {

		String picStatus = "";
		// picture_status 图片状态：2;2;2;2;2;2;2;2;2;2;
		// 宝贝主图 main picture
		for (int i = 0; i < baobeiPictureNameList.size(); i++) {
			if (i == 5)
				break;
			picStatus += "2;";
		}
		if (baobeiColorList != null && !baobeiColorList.isEmpty()) {
			int maxColorLen = Math.min(baobeiColorList.size(), taobaoColors.size());
			// 销售属性图片
			for (int i = 0; i < maxColorLen; i++) {
				if (baobeiPictureNameList.size() == baobeiColorList.size()) {
					// color picture
					picStatus += "2;";
				}
			}
		}
		return picStatus;
	}

	public static String composeBaobeiPicture(List<String> baobeiPictureNameList,List<String> baobeiColorList, 
			List<String> taobaoColors) {
		String picture = "";
		// picture
		// 新图片：1128533_dkfs:1:0:|;1128533_mst:1:1:|;1128533_scl:1:2:|;1128533_tq:1:3:|;1128533_umr:1:4:|;1128533_dkfs:2:0:1627207:28320|;1128533_mst:2:0:1627207:28340|;1128533_scl:2:0:1627207:3232479|;1128533_tq:2:0:1627207:3232478|;1128533_umr:2:0:1627207:3232482|;
		// 宝贝主图 main picture
		for (int i = 0; i < baobeiPictureNameList.size(); i++) {
			if (i == 5)
				break;
			picture += baobeiPictureNameList.get(i) + ":1:" + i + ":|;";
		}
		if (baobeiColorList != null && !baobeiColorList.isEmpty()) {
			int maxColorLen = Math.min(baobeiColorList.size(), taobaoColors.size());
			// 销售属性图片
			for (int i = 0; i < maxColorLen; i++) {
				if (baobeiPictureNameList.size() == baobeiColorList.size()) {
					// color picture
					picture += baobeiPictureNameList.get(i) + ":2:0:1627207:" + taobaoColors.get(i) + "|;";
				}
			}
		}

		return picture;
	}

	public static String composeBaobeiPropAlias(List<String> baobeiSizesList, List<String> taobaoSizes,String sizePropKey) {
		String propAlias = "";
		// 销售属性别名
		for (int i = 0; i < baobeiSizesList.size(); i++) {
			if (i >= taobaoSizes.size())
				break;
			propAlias += sizePropKey + ":" + taobaoSizes.get(i) + ":" + baobeiSizesList.get(i) + ";";
		}

		return propAlias;
	}

	public static String composeBaobeiInputCustomCpv(List<String> baobeiColorList, List<String> taobaoColors) {
		String inputCustomCpv = "";
		// 自定义属性值
		for (int i = 0; i < baobeiColorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			// 1627207:-1001:color1;
			inputCustomCpv += "1627207:" + taobaoColors.get(i) + ":" + baobeiColorList.get(i) + ";";
		}
		return inputCustomCpv;
	}

	public static String composeBaobeiInputValues(List<String> baobeiColorList, List<String> taobaoColors) {
		String inputValues = "";
		for (int i = 0; i < baobeiColorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			inputValues += baobeiColorList.get(i) + ";颜色分类;";
		}
		return inputValues;
	}

	public static String composeBaobeiCateProps(List<String> baobeiColorList, List<String> baobeiSizeList,
			List<String> taobaoColors, List<String> taobaoSizes) {
		String cateProps = "";
		// 宝贝属性
		for (int i = 0; i < baobeiColorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			cateProps += "1627207:" + taobaoColors.get(i) + ";";
		}
		for (int i = 0; i < baobeiSizeList.size(); i++) {
			if (i >= taobaoSizes.size())
				break;
			cateProps += "20509:" + taobaoSizes.get(i) + ";";
		}
		return cateProps;
	}

	public static String composeBaobeiSkuProps(List<String> baobeiColorList, List<String> baobeiSizeList,
			List<String> taobaoColors, List<String> taobaoSizes, String price) {
		// skuProps
		// 销售属性组合：0:0::1627207:-1001;20509:28381;0:0::1627207:-1001;20509:28313;0:0::1627207:-1001;20509:28314;0:0::1627207:-1001;20509:28315;0:0::1627207:-1001;20509:28316;0:0::1627207:-1001;20509:28317;0:0::1627207:-1001;20509:28319;0:0::1627207:-1002;20509:28381;0:0::1627207:-1002;20509:28313;0:0::1627207:-1002;20509:28314;0:0::1627207:-1002;20509:28315;0:0::1627207:-1002;20509:28316;0:0::1627207:-1002;20509:28317;0:0::1627207:-1002;20509:28319;0:0::1627207:-1003;20509:28381;0:0::1627207:-1003;20509:28313;0:0::1627207:-1003;20509:28314;0:0::1627207:-1003;20509:28315;0:0::1627207:-1003;20509:28316;0:0::1627207:-1003;20509:28317;0:0::1627207:-1003;20509:28319;0:0::1627207:-1004;20509:28381;0:0::1627207:-1004;20509:28313;0:0::1627207:-1004;20509:28314;0:0::1627207:-1004;20509:28315;0:0::1627207:-1004;20509:28316;0:0::1627207:-1004;20509:28317;0:0::1627207:-1004;20509:28319;0:0::1627207:-1005;20509:28381;0:0::1627207:-1005;20509:28313;0:0::1627207:-1005;20509:28314;0:0::1627207:-1005;20509:28315;0:0::1627207:-1005;20509:28316;0:0::1627207:-1005;20509:28317;0:0::1627207:-1005;20509:28319;0:0::1627207:-1006;20509:28381;0:0::1627207:-1006;20509:28313;0:0::1627207:-1006;20509:28314;0:0::1627207:-1006;20509:28315;0:0::1627207:-1006;20509:28316;0:0::1627207:-1006;20509:28317;0:0::1627207:-1006;20509:28319;0:0::1627207:-1007;20509:28381;0:0::1627207:-1007;20509:28313;0:0::1627207:-1007;20509:28314;0:0::1627207:-1007;20509:28315;0:0::1627207:-1007;20509:28316;0:0::1627207:-1007;20509:28317;0:0::1627207:-1007;20509:28319;0:0::1627207:-1008;20509:28381;0:0::1627207:-1008;20509:28313;0:0::1627207:-1008;20509:28314;0:0::1627207:-1008;20509:28315;0:0::1627207:-1008;20509:28316;0:0::1627207:-1008;20509:28317;0:0::1627207:-1008;20509:28319;0:0::1627207:-1009;20509:28381;0:0::1627207:-1009;20509:28313;0:0::1627207:-1009;20509:28314;0:0::1627207:-1009;20509:28315;0:0::1627207:-1009;20509:28316;0:0::1627207:-1009;20509:28317;0:0::1627207:-1009;20509:28319;
		String skuProps = "";
		for (int i = 0; i < baobeiColorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			if (baobeiSizeList.isEmpty()) {
				String num = "99";
				skuProps += price + ":" + num + ":" + ":1627207" + ":" + taobaoColors.get(i) + ";";
			} else {
				for (int j = 0; j < baobeiSizeList.size(); j++) {
					if (j >= taobaoSizes.size())
						break;
					String num = "99";
					skuProps += price + ":" + num + ":" + ":1627207" + ":" + taobaoColors.get(i) + ";20509:"
							+ taobaoSizes.get(j) + ";";
				}
			}
		}
		return skuProps;
	}
}
