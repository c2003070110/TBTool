package com.walk_nie.taobao.kakaku;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.kakaku.taobao.KakakuTaobaoPriceObject;
import com.walk_nie.taobao.object.BaobeiPublishObject;

public class KakakuObject {
	// kakaku id
	public String id = "";
	public String itemMaker = "";
	public String itemType = "";
	public String sku = "";

	public String priceWave = "";
	//public PriceObject price = null;
	public PriceObject priceMin = null;
	public PriceObject priceYodobashi = null;
	public PriceObject priceEEarPhone = null;
	public PriceObject priceAmazon = null;
	public PriceObject priceBiccamera = null;
	public PriceObject priceNojima = null;
	public PriceObject priceYamada = null;

	public String sellStartDate = "";

	public List<KakakuObject> colorList = Lists.newArrayList();
	public String colorName = "";
	
    public String detailScreenShotPicFile = "";

    public List<String> pictureUrlList = Lists.newArrayList();
	public List<String> pictureNameList = Lists.newArrayList();

	public List<KakakuTaobaoPriceObject> taobaoList = new ArrayList<KakakuTaobaoPriceObject>();

	public SpecObject spec = new SpecObject();

	public BaobeiPublishObject baobei = new BaobeiPublishObject();

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id=").append(id);
		sb.append(" maker=").append(itemMaker);
		sb.append(" type=").append(itemType);
		return sb.toString();
	}

}
