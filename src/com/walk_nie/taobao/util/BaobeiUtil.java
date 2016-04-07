package com.walk_nie.taobao.util;

import com.walk_nie.taobao.object.BaobeiPublishObject;

public class BaobeiUtil {
    public static String getExtraMiaoshu() {
        StringBuffer miaoshu = new StringBuffer();
        miaoshu.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">购物须知</h3>");
        miaoshu.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
        miaoshu.append("<ol>");
        miaoshu.append("<li style=\"padding:10.0px;\">宝贝采购于直营店 OR 官网！加微信（东京太郎 tokyoson）可采购直播。</li>");
        miaoshu.append("<li style=\"padding:10.0px;\">日本直邮。即日本直发到您府上，无国内中转，是真真正正的日本代购！不是野鸡代购！<span style=\";color:red;font-weight:bold\">100%日本，100%真品。</span></li>");
        miaoshu.append("<li style=\"padding:10.0px;\">下单前，请认真比对尺寸大小！<span style=\";color:red;font-weight:bold\">不能因为尺寸问题 取消订单！！退款！！！</span></li>");
        miaoshu.append("<li style=\"padding:10.0px;\">店主在日本购入后，<span style=\";color:red;font-weight:bold\">宝贝不能取消！</span>除质量问题，<span style=\";color:red;font-weight:bold\">不接受退换。</span></li>");
        miaoshu.append("<li style=\"padding:10.0px;\">包裹如有开封破损的迹象，验货后再签收！如有任何问题，请<span style=\";color:red;font-weight:bold\">拒收！</span></li>");
        miaoshu.append("<li style=\"padding:10.0px;\"><span style=\";color:red;font-weight:bold\">关税：</span> 个人代购非走私，追加关税不是没有可能的。不幸被抽查产生关税，<span style=\";color:red;font-weight:bold\">关税买家自己承担。</span></li>");
        miaoshu.append("<li style=\"padding:10.0px;\"><span style=\";color:red;font-weight:bold\">代购周期：</span>正常情况下5天内发货！发货后5天左右到手！</li>");
        miaoshu.append("<li style=\"padding:10.0px;\"><span style=\";color:red;font-weight:bold\">退换货：</span>由于海外代购的特殊性质,代购商品如无严重质量问题一经售出均<span style=\";color:red;font-weight:bold\">不可退换货</span>,请买家在购买前务必确认好颜色、尺码等</li>");
        miaoshu.append("<li style=\"padding:10.0px;\"><span style=\";color:red;font-weight:bold\">产地：</span>日本产的非常少。大多数是东南亚和中国制造。<p>大家都知道的。<span style=\";color:red;font-weight:bold\">就算是同条生产线，面向日本本土，要比其他国家的质量要好很多。</span></p></li>");
        miaoshu.append("</ol>");
        miaoshu.append("</div>");
        return miaoshu.toString();
    }

    public static void setBaobeiCommonInfo(BaobeiPublishObject obj) {
        // 宝贝名称
        obj.title = "";
        // 宝贝类目;
        obj.cid = "";
        // 店铺类目;
        obj.seller_cids = "";
        // 新旧程度;
        obj.stuff_status = "1";
        // 省
        obj.location_state = "海外";
        // 城市
        obj.location_city = "日本";
        // 出售方式;
        obj.item_type = "1";
        // 宝贝价格;
        obj.price = "0";
        // 加价幅度;
        obj.auction_increment = "";
        // 宝贝数量
        obj.num = "0";
        // 有效期
        obj.valid_thru = "0";
        // 运费承担;
        obj.freight_payer = "0";
        // 平邮;
        obj.post_fee = "7.09434e-39";
        // EMS;
        obj.ems_fee = "2.8026E-45";
        // 快递
        obj.express_fee = "0";
        // 发票;
        obj.has_invoice = "0";
        // 保修;
        obj.has_warranty = "0";
        // 放入仓库;
        obj.approve_status = "1";
        // 橱窗推荐
        obj.has_showcase = "1";
        // 开始时间
        obj.list_time = "";
        // 宝贝描述;
        obj.description = "";
        // 宝贝属性;
        obj.cateProps = "";
        // 邮费模版ID;
        obj.postage_id = "1780373930";
        // 会员打折
        obj.has_discount = "0";
        // 修改时间
        obj.modified = "";
        // 上传状态;
        obj.upload_fail_msg = "";
        // 图片状态;
        obj.picture_status = "";
        // 返点比例;
        obj.auction_point = "0";
        // 新图片
        obj.picture = "";
        // 视频;
        obj.video = "";
        // 销售属性组合;
        obj.skuProps = "";
        // 用户输入ID串;
        obj.inputPids = "";
        // 用户输入名-值对
        obj.inputValues = "";
        // 商家编码
        obj.outer_id = "";
        // 销售属性别名;
        obj.propAlias = "";
        // 代充类型;
        obj.auto_fill = "0";
        // 数字ID;
        obj.num_id = "0";
        // 本地ID
        obj.local_cid = "1069841847";
        // 宝贝分类
        obj.navigation_type = "1";
        // 用户名称;
        obj.user_name = "";
        // 宝贝状态;
        obj.syncStatus = "6";
        // 闪电发货;
        obj.is_lighting_consigment = "56";
        // 新品
        obj.is_xinpin = "245";
        // 食品专项;
        obj.foodparame = "product_date_end:;product_date_start:;stock_date_end:;stock_date_start:";
        // 尺码库;
        obj.features = "mysize_tp:0";
        // 采购地;
        obj.buyareatype = "1";
        // 库存类型
        obj.global_stock_type = "2";
        // 国家地区
        obj.global_stock_country = "日本";
        // 库存计数;
        obj.sub_stock_type = "2";
        // 物流体积;
        obj.item_size = "bulk:0.000000";
        // 物流重量;
        obj.item_weight = "0";
        // 退换货承诺
        obj.sell_promise = "0";
        // 定制工具
        obj.custom_design_flag = "";
        // 无线详情;
        obj.wireless_desc = "";
        // 商品条形码;
        obj.barcode = "";
        // sku 条形码;
        obj.sku_barcode = "";
        // 7天退货
        obj.newprepay = "0";
        // 宝贝卖点;
        obj.subtitle = "";
        // 属性值备注;
        obj.cpv_memo = "";
        // 自定义属性值;
        obj.input_custom_cpv = "";
        // 商品资质
        obj.qualification = "%7B%20%20%7D";
        // 增加商品资质
        obj.add_qualification = "0";
        // 关联线下服务;
        obj.o2o_bind_service = "0";
    }
}
