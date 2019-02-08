package com.walk_nie.taobao.amazon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;

public class AmazonGoodsPageParser extends BaseBaobeiParser {

	public List<AmazonGoodsObject> scanItemUrls(List<String> urls)
			throws IOException {
		List<AmazonGoodsObject> goodsList = new ArrayList<AmazonGoodsObject>();
		for (String url : urls) {
			AmazonGoodsObject obj = scanItemUrl(url);
			if(obj != null){
				goodsList.add(obj);
			}
		}
		return goodsList;
	}

	public AmazonGoodsObject scanItemUrl(String url) {
		AmazonGoodsObject obj = new AmazonGoodsObject();
		return obj;
	}

}
