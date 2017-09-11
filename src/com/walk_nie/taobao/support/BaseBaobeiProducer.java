package com.walk_nie.taobao.support;

import java.util.List;

import com.walk_nie.taobao.montBell.GoodsObject;
import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public abstract class BaseBaobeiProducer {

	protected boolean updatePriceFlag = false;
	protected boolean updateTitleFlag = false;
	protected boolean updateOutIdFlag = false;
	protected boolean updateDescriptionFlag = false;

	// for update
	protected List<BaobeiPublishObject> toUpdatebaobeiList = null;
	protected List<BaobeiPublishObject> publishedbaobeiList = null;

	protected String outputFile = "";
	// protected String publishedBaobeiFile = "";

	protected double currencyRate;
	protected double benefitRate;

	public BaseBaobeiProducer enablePriceUpdate() {
		this.updatePriceFlag = true;
		return this;
	}

	public BaseBaobeiProducer enableTitleUpdate() {
		this.updateTitleFlag = true;
		return this;
	}

	public BaseBaobeiProducer enableOutIdUpdate() {
		this.updateOutIdFlag = true;
		return this;
	}

	public BaseBaobeiProducer enableDescriptionUpdate() {
		this.updateDescriptionFlag = true;
		return this;
	}

	public BaseBaobeiProducer setToUpdateBaobeiList(
			List<BaobeiPublishObject> baobeiList) {
		toUpdatebaobeiList = baobeiList;
		return this;
	}

	public BaseBaobeiProducer setCurrencyRate(double currencyRate) {
		this.currencyRate = currencyRate;
		return this;
	}

	public BaseBaobeiProducer setBenefitRate(double benefitRate) {
		this.benefitRate = benefitRate;
		return this;
	}

	public BaseBaobeiProducer setPublishedbaobeiList(
			List<BaobeiPublishObject> baobeiList) {
		publishedbaobeiList = baobeiList;
		return this;
	}

	protected boolean isUpdateMode() {
		return (toUpdatebaobeiList != null && !toUpdatebaobeiList.isEmpty());
	}

	// public BaseBaobeiProducer setPublishedBaobeiFile(String
	// publishedBaobeiFile) {
	// this.publishedBaobeiFile = publishedBaobeiFile;
	// return this;
	// }

	public BaseBaobeiProducer setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}

	public abstract void process();

	public abstract BaseBaobeiParser getParser();

	protected String updatePublishedBaobei(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {

		// 宝贝价格
		publishedBaobei.price = MontBellUtil.convertToCNYWithEmsFee(item,
				this.currencyRate, this.benefitRate);
		// 宝贝描述
		composeBaobeiMiaoshu(item, publishedBaobei);
		// 宝贝属性
		composeBaobeiCateProps(item, publishedBaobei);
		// 销售属性组合
		composeBaobeiSkuProps(item, publishedBaobei);
		// 销售属性别名
		composeBaobeiPropAlias(item, publishedBaobei);
		// 图片状态
		composeBaobeiPictureStatus(item, publishedBaobei);
		// 新图片
		composeBaobeiPicture(item, publishedBaobei);
		// 自定义属性值
		composeBaobeiInputCustomCpv(item, publishedBaobei);
		// 宝贝卖点
		MontBellUtil.composeBaobeiSubtitle(item, publishedBaobei);
		return TaobaoUtil.composeTaobaoLine(publishedBaobei);
	}

	protected abstract void composeBaobeiMiaoshu(GoodsObject item,
			BaobeiPublishObject publishedBaobei);

	protected abstract void composeBaobeiCateProps(GoodsObject item,
			BaobeiPublishObject publishedBaobei);

	protected abstract void composeBaobeiSkuProps(GoodsObject item,
			BaobeiPublishObject publishedBaobei);

	protected abstract void composeBaobeiPropAlias(GoodsObject item,
			BaobeiPublishObject publishedBaobei);

	protected abstract void composeBaobeiPictureStatus(GoodsObject item,
			BaobeiPublishObject publishedBaobei);

	protected abstract void composeBaobeiPicture(GoodsObject item,
			BaobeiPublishObject publishedBaobei);

	protected abstract void composeBaobeiInputCustomCpv(GoodsObject item,
			BaobeiPublishObject publishedBaobei);

}
