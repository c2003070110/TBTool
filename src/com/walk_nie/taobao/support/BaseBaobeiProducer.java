package com.walk_nie.taobao.support;

import java.io.File;
import java.util.List;

import com.walk_nie.taobao.object.BaobeiPublishObject;

public abstract class BaseBaobeiProducer {

	protected boolean updatePriceFlag = false;
	protected boolean updateTitleFlag = false;
	protected boolean updateOutIdFlag = false;
	protected boolean updateDescriptionFlag = false;

	// for update
	protected List<BaobeiPublishObject> toUpdatebaobeiList = null;
	protected List<BaobeiPublishObject> publishedbaobeiList = null;

	protected File outputFile = null;
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

	public BaseBaobeiProducer setOutputFile(File outputFile) {
		this.outputFile = outputFile;
		return this;
	}

	public abstract void process();

	public abstract BaseBaobeiParser getParser();


}
