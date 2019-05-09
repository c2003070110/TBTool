package com.walk_nie.taobao.object;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * OrderObject + OrderDetailObject
 */
public class OrderInfoObject {
	public OrderObject orderObject = new OrderObject();
	public List<OrderDetailObject> orderDtlList = Lists.newArrayList();
	
	public String orderDetailUrl = "";
}
