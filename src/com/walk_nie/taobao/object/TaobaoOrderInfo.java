package com.walk_nie.taobao.object;

import java.util.List;

import com.google.common.collect.Lists;
import com.walk_nie.object.CrObject;


public class TaobaoOrderInfo {

	public String taobaoOrderName;
	public List<TaobaoOrderProductInfo> productInfos = Lists.newArrayList();
	public String firstName;
	public String lastName;
	public String tel;
	public String state;
	public String city;
	public String adr2;
	public String adr1;
	public String postcode;
	public CrObject crObj;
	 
}
