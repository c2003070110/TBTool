package com.walk_nie.taobao.mangazenkan;
import java.util.ArrayList;
import java.util.List;


public class MangazenkanGoodsObject {
	public String productId;

	public String title;
	public String name; // 巻数ある
	public String brand;//出版社
	public String author;
	public String latestPublishDate; // 最終巻発売日
	public String bookPublishType; // 版型
	public String categoryId; //カテゴリ
	public String categoryName; // カテゴリ
	public String priceOrg;
	public String price;
	public String priceCNY;
	public String priceCNYEMS;
	public String priceCNYSAL;
	public String priceCNYSEA;
	
	public String detailDisp;//あらすじ
	
	public List<String> pictureList = new ArrayList<String>();
	
	public List<String> pictureNameList = new ArrayList<String>();

	public boolean finished = false;;
	
	int volCount = 0 ;//総巻数
	int totalWeight = 0 ;//総重量
}
