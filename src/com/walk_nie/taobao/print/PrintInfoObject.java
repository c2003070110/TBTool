package com.walk_nie.taobao.print;

import java.util.List;

import com.beust.jcommander.internal.Lists;


public class PrintInfoObject {
    
    public String senderName;
    public String senderAddress1;
    public String senderAddress2;
    public String senderAddress3;
    public String senderZipCode;
    public String senderTel;

    public String receiverCountry;
    public String receiverName;
    public String receiverAddress1;
    public String receiverAddress2;
    public String receiverAddress3;
    public String receiverZipCode;
    public String receiverTel;
    public String receiverWWID;
    public String orderNo;

    public List<String> orderNos = Lists.newArrayList();

	// 订单创建时间
	public String orderCreatedDateTime="";
}
