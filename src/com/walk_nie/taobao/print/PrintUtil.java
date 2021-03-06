package com.walk_nie.taobao.print;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.object.TaobaoSaledObject;
import com.walk_nie.taobao.util.TaobaoUtil;


public class PrintUtil {
    public static String rootPathName = "./print";

    public static String printedOrderNosFileName = "printedOrderNos.txt";
    
    public static String toPrintFileName1EMS = "toPrint1_ems.txt";
    public static String toPrintFileName1SAL = "toPrint1_sal.txt";
    public static String toPrintFileName2EMS = "toPrint2_ems.txt";
    public static String toPrintFileName2SAL = "toPrint2_sal.txt";

    public static String splitor = "\t";

    public static String commonUseAddFileName = "commonUseAddress.txt";

    public static int LABEL_TYPE_EMS = 0;

    public static int LABEL_TYPE_POSTAL = 1;
    public static void setSenderInfo(List<PrintInfoObject> list) {
        for(PrintInfoObject obj:list){
            setSenderInfo(obj);
        }
    }

    public static void setSenderInfo(PrintInfoObject obj) {
        obj.senderName="邓祎";
        obj.senderAddress1="東京都足立区";
        obj.senderAddress2="西新井本町５－２－６";
        obj.senderZipCode="123-0845";
        obj.senderTel="08042001314";
        
        obj.receiverCountry = "中国 CHINA";
    }
    
    public static List<String> getCommonUseAddress() throws IOException {
        File file = new File(PrintUtil.rootPathName, PrintUtil.commonUseAddFileName);
        List<String> list = FileUtils.readLines(file, "UTF-8");
        List<String> rtn = Lists.newArrayList();
        for(String str:list){
        	if(StringUtils.isEmpty(str) || str.startsWith("#")){
        		continue;
        	}
        	rtn.add(str);
        }
        return rtn;
    }

    public static List<PrintInfoObject> getPrintInfoList1EMS() throws IOException {
        return  getPrintInfoList(PrintUtil.LABEL_TYPE_EMS,1);
    }

    public static List<PrintInfoObject> getPrintInfoList1Postal() throws IOException {
        return  getPrintInfoList(PrintUtil.LABEL_TYPE_POSTAL,1);
    }

    public static List<PrintInfoObject> getPrintInfoList2EMS() throws IOException {
        return  getPrintInfoList(PrintUtil.LABEL_TYPE_EMS,2);
    }

    public static List<PrintInfoObject> getPrintInfoList2Postal() throws IOException {
        return  getPrintInfoList(PrintUtil.LABEL_TYPE_POSTAL,2);
    }
    public static PrintInfoObject parseToPrintInfoObjectByTab(String str){
        // 1918806427484022 ningningtou0615 蒋依宁 上海 上海市 闵行区上海 上海市 闵行区201199) 13764337710
        String[] splited = str.split(splitor);
        if(splited.length != 5) {
            System.out.println("[ERROR]NOT convert to print object for " + str + " by [TAB]");
            return null;
        }
        PrintInfoObject obj = new PrintInfoObject();
        int indx = 0;
        obj.orderNo = splited[indx++];
        obj.receiverWWID = splited[indx++];
        obj.receiverName = splited[indx++];
        setAddress(obj,splited[indx++]);
        obj.receiverTel = splited[indx++];
        return obj;
    }
    public static PrintInfoObject parseToPrintInfoObjectByComma(String str){
        //format : 颜宁, 13438327357, 四川省, 成都市, 金堂县, 三星镇金堂大道9号四川师范大学文理学院, 610401
        String[] splited = str.split(",");
        int min = 7;
        if(splited.length < min) {
            System.out.println("[ERROR]NOT convert to print object for " + str + " by [,]");
            return null;
        }
        PrintInfoObject obj = new PrintInfoObject();
        int indx = 0;
        obj.receiverName = splited[indx++].trim();
        obj.receiverTel = splited[indx++].trim();
        String newAddr = splited[indx++].trim() +" " +splited[indx++].trim() +" " +splited[indx++].trim() +" " +splited[indx++].trim();
        //String newAddr = splited[indx++].trim() +" " +splited[indx++].trim() +" " +splited[indx++].trim() ;
        setAddress(obj,newAddr);
        obj.receiverZipCode = splited[indx++].trim() ;
        obj.receiverCountry = "中国 CHINA";
        if(splited.length > (min)){
        	obj.receiverHiddenInfo1 = splited[indx++].trim();
        }
        if(splited.length > (min+1)){
        	obj.receiverHiddenInfo2 = splited[indx++].trim();
        }
        if(splited.length > (min+2)){
        	obj.receiverHiddenInfo3 = splited[indx++].trim();
        }
        if(splited.length > (min+3)){
        	obj.receiverHiddenInfo4 = splited[indx++].trim();
        }
        if(splited.length > (min+4)){
        	obj.receiverHiddenInfo5 = splited[indx++].trim();
        }
        return obj;
    }
    public static List<PrintInfoObject> getPrintInfoList(int labelType,int patternType) throws IOException {
        List<PrintInfoObject> printList = Lists.newArrayList();
        String charset = "UTF-8";
        List<String> list = Lists.newArrayList();
        if(patternType == 1){
        	// taobao export
        	String fold = "";
        	if(labelType==PrintUtil.LABEL_TYPE_EMS){
        		fold = "ems";
        	}
        	if(labelType==PrintUtil.LABEL_TYPE_POSTAL){
        		fold = "sal";
        	}
            File file1 = new File(PrintUtil.rootPathName, fold);
            charset = "GB2312";
            File[] files = file1.listFiles();
            if(files.length == 0){
            	return printList;
            }
            for(File f : files){
                try {
                    List<String> l = FileUtils.readLines(f, charset);
                    list.addAll(l);
                } catch (Exception e) {
                    e.printStackTrace();
                    return printList;
                }
            }
		} else {
			// other...
			String fileName = null;
			if (labelType == PrintUtil.LABEL_TYPE_EMS) {
				fileName = PrintUtil.toPrintFileName2EMS;
			} else if (labelType == PrintUtil.LABEL_TYPE_POSTAL) {
				fileName = PrintUtil.toPrintFileName2SAL;
			}
			try {
		        File file = new File(PrintUtil.rootPathName, fileName);
				List<String> l = FileUtils.readLines(file, charset);
				list.addAll(l);
			} catch (Exception e) {
				e.printStackTrace();
				return printList;
			}
		} 
        
        List<PrintInfoObject> printedInfos = PrintUtil.readPrintedInfoList();
        for (String str : list) {
            if (str.equals("")) continue;
            if (str.startsWith("#")) continue;
            if (str.startsWith("\"订单编号")) continue;
            if(patternType == 1){
            	// 订单编号	买家会员名	买家支付宝账号	买家应付货款	买家应付邮费	买家支付积分	总金额	返点积分	买家实际支付金额	买家实际支付积分	订单状态	买家留言	收货人姓名	收货地址 	运送方式	联系电话 	联系手机	订单创建时间	订单付款时间 	宝贝标题 	宝贝种类 	物流单号 	物流公司	订单备注	宝贝总数量	店铺Id	店铺名称	订单关闭原因	卖家服务费	买家服务费	发票抬头	是否手机订单	分阶段订单信息	特权订金订单id	定金排名	修改后的sku	修改后的收货地址	异常信息	天猫卡券抵扣	集分宝抵扣	是否是O2O交易
                // 1930679858170647	幸福蔓岩1627	844677789@qq.com	598	0	0	598	0	598	0	买家已付款，等待卖家发货		马慧	吉林省 通化市 柳河县 柳河镇紫御府   七号楼    3单元   201室(135300)	快递		'18618486065	2016/5/23 22:02	2016/5/23 22:02	日本直邮mamakids洗面奶+化妆水+乳液护肤特惠套装mama&kids包邮	1				1	0	Funny宝贝快乐购	订单未关闭	0	0元		手机订单									
                TaobaoSaledObject saledObj = TaobaoUtil.readTaobaoSaledIn(str);
                String orderNo = saledObj.orderNo;
                if (PrintUtil.isPrintedInfo(printedInfos,orderNo)) continue;
                String sts = saledObj.orderStatus;// 订单状态
                if(sts.equals("交易关闭")||sts.equals("交易成功")
                		||sts.equals("卖家已发货，等待买家确认")) continue;
                if(StringUtils.isEmpty(saledObj.baobeiTitle)
                		|| saledObj.baobeiTitle.contains("现货")) continue;
                
                PrintInfoObject obj = new PrintInfoObject();
                obj.orderNo = orderNo;
                obj.receiverWWID = saledObj.buyerId;//买家会员名
                obj.receiverName = saledObj.buyerName;//收货人姓名
                PrintUtil.setAddress(obj, saledObj.buyerAddress);//收货地址 
                String tel1 = saledObj.tel;//联系电话
                String tel2 = saledObj.telMobile;// 联系手机
                obj.receiverTel = StringUtils.isNotEmpty(tel2)?tel2:tel1;
                obj.orderCreatedDateTime = saledObj.orderCreatedDateTime;// 
                String hdInfo1 = saledObj.baobeiTitle.replaceAll(",", "");
                hdInfo1 = hdInfo1.replaceAll("日本", "");
                hdInfo1 = hdInfo1.replaceAll("代购", "");
                hdInfo1 = hdInfo1.replaceAll("直邮", "");
                obj.receiverHiddenInfo1 = hdInfo1;
                printList.add(obj);
            }else if(patternType == 2){
                //颜宁, 13438327357, 四川省, 成都市, 金堂县, 三星镇金堂大道9号四川师范大学文理学院, 610401
                PrintInfoObject obj = parseToPrintInfoObjectByComma(str);
                if(obj == null){
                    continue;
                }
                printList.add(obj);
            }
        }
        Collections.sort(printList, new Comparator<PrintInfoObject>() {

            @Override
            public int compare(PrintInfoObject o1, PrintInfoObject o2) {
                return o1.receiverName.compareTo(o2.receiverName);
            }
        });

        List<PrintInfoObject> toPrintList = Lists.newArrayList();
        String name = "";
        PrintInfoObject tempObj = null;
        for (PrintInfoObject obj : printList) {
            if (name.equals(obj.receiverName)) {
                tempObj.orderNos.add(obj.orderNo);
            } else {
                name = obj.receiverName;
                tempObj = obj;
                tempObj.orderNos.add(obj.orderNo);
                toPrintList.add(tempObj);
            }
        }
        Collections.sort(toPrintList, new Comparator<PrintInfoObject>() {

            @Override
            public int compare(PrintInfoObject o1, PrintInfoObject o2) {
            	// 2016-05-28 21:49:45
            	try {
					Date time1 = DateUtils.parseDate(o1.orderCreatedDateTime, "yyyy-MM-dd hh:mm:ss");
					Date time2 = DateUtils.parseDate(o2.orderCreatedDateTime, "yyyy-MM-dd hh:mm:ss");
	                return time1.compareTo(time2);
				} catch (ParseException e) {
					//e.printStackTrace();
				} 
            	return 0;
            }
        });

        return toPrintList;
    }
    public static boolean isPrintedInfo(List<PrintInfoObject> printedInfos, String orderNo) {
        if(StringUtils.isEmpty(orderNo)){
            return false;
        }
        if (printedInfos == null || printedInfos.isEmpty()) {
            return false;
        }
        for (PrintInfoObject line : printedInfos) {
            if (line.orderNo.equals(orderNo)) {
                return true;
            }
        }
        return false;
    }

    public static List<PrintInfoObject> readPrintedInfoList() throws IOException {
        File file = new File(PrintUtil.rootPathName, PrintUtil.printedOrderNosFileName);
        if (!file.exists())
            return Lists.newArrayList();
        List<PrintInfoObject> list = Lists.newArrayList();
        List<String> lines = Files.readLines(file, Charset.forName("UTF-8"));
        for(String line:lines){
        	if(line.startsWith("#"))continue;
            String[] split = line.split(splitor);
            if(split.length != 5)continue;
            PrintInfoObject obj = new PrintInfoObject();

            obj.orderNo = split[0];
            obj.receiverWWID = split[1];
            obj.receiverName = split[2];
            setAddress(obj, split[3]);
            obj.receiverTel = split[4];
            
            list.add(obj);
        }
        return list;
    }
    public static void savePrintedOrderNos(PrintInfoObject printedInfo) throws IOException {
        List<PrintInfoObject> printedList = Lists.newArrayList();
        printedList.add(printedInfo);
        savePrintedOrderNos(printedList);
    }
    public static void savePrintedOrderNos(List<PrintInfoObject> printedList) throws IOException {
        File file = new File(rootPathName, printedOrderNosFileName);
        StringBuffer sb = new StringBuffer();
        if(!printedList.isEmpty()){
        	Calendar cal = Calendar.getInstance();
        	cal.setTimeInMillis(System.currentTimeMillis());
        	sb.append("#" + org.apache.http.client.utils.DateUtils.formatDate(cal.getTime(), "yyyy/MM/dd"));
            sb.append("\r\n");
        }
        String comma = ",";
        for (PrintInfoObject obj : printedList) {
            for (String orderNo : obj.orderNos) {
                if(StringUtils.isEmpty(orderNo)){
                    orderNo = "";
                }
                sb.append(obj.receiverName.replaceAll(",", "")).append(comma);
                sb.append(obj.receiverTel.replaceAll(",", "")).append(comma);
                String addr1 = obj.receiverAddress1.trim().replaceAll(" ", ",");
                sb.append(addr1).append(comma);
                sb.append(obj.receiverAddress2.replaceAll(",", ""));
                if(StringUtils.isNotEmpty(obj.receiverAddress3)){
                    sb.append(obj.receiverAddress3.replaceAll(",", "")) ;
                }
                sb.append(comma);
                
                sb.append("").append(comma);
                
                String receiverWWID = obj.receiverWWID;
                if(StringUtils.isEmpty(receiverWWID)){
                    receiverWWID = "";
                }
                sb.append(receiverWWID).append(comma);
                
                if(StringUtils.isNotEmpty(obj.receiverHiddenInfo1)){
                    sb.append(obj.receiverHiddenInfo1.replaceAll(",", "")).append(comma) ;
                }
                if(StringUtils.isNotEmpty(obj.receiverHiddenInfo2)){
                    sb.append(obj.receiverHiddenInfo2.replaceAll(",", "")).append(comma) ;
                }
                if(StringUtils.isNotEmpty(obj.receiverHiddenInfo3)){
                    sb.append(obj.receiverHiddenInfo3.replaceAll(",", "")).append(comma) ;
                }
                if(StringUtils.isNotEmpty(obj.receiverHiddenInfo4)){
                    sb.append(obj.receiverHiddenInfo4.replaceAll(",", "")).append(comma) ;
                }
                if(StringUtils.isNotEmpty(obj.receiverHiddenInfo5)){
                    sb.append(obj.receiverHiddenInfo5.replaceAll(",", "")).append(comma) ;
                }
                
                sb.append("\r\n");
            }
        }
        if (!file.exists()) {
            Files.write(sb.toString(), file, Charset.forName("UTF-8"));
        } else {
            Files.append(sb.toString(), file, Charset.forName("UTF-8"));
        }
    }

    public static void setAddress(PrintInfoObject obj, String address) {
        String[] splied = address.split(" ");
        int splitIdx = 18;
        if (splied.length > 3) {
            obj.receiverAddress1 = splied[0] + " " + splied[1] + " " + splied[2];
            String newAdd = "";
            for (int j = 3; j < splied.length; j++) {
                newAdd += splied[j];
            }
            if (newAdd.length() > splitIdx) {
                obj.receiverAddress2 = newAdd.substring(0, splitIdx);
                obj.receiverAddress3 = newAdd.substring(splitIdx);
            } else {
                obj.receiverAddress2 = newAdd;
            }
        } else {
            if (address.length() > splitIdx) {
                obj.receiverAddress1 = address.substring(0, splitIdx);
                String newAdd = address.substring(splitIdx);
                if (newAdd.length() > splitIdx) {
                    obj.receiverAddress2 = newAdd.substring(0, splitIdx);
                    obj.receiverAddress3 = newAdd.substring(splitIdx);
                } else {
                    obj.receiverAddress2 = newAdd;
                }
            } else {
                obj.receiverAddress1 = address;
            }
        }
    }

    public static List<String> parseDateToString(){
        List<String> dateList = Lists.newArrayList();
        Date d = DateUtils.addDays(new Date(System.currentTimeMillis()), 1);
        String s = org.apache.http.client.utils.DateUtils.formatDate(d, "yyyyMMdd");
        int idx = 0;
        dateList.add(s.substring(idx,++idx));
        dateList.add(s.substring(idx,++idx));
        dateList.add(s.substring(idx,++idx));
        dateList.add(s.substring(idx,++idx));
        dateList.add(s.substring(idx,++idx));
        dateList.add(s.substring(idx,++idx));
        dateList.add(s.substring(idx,++idx));
        dateList.add(s.substring(idx,++idx));
        return dateList;
    }
    public static int fromCMToPPI_i(double cm) {            
        return new Long(Math.round(toPPI(cm * 0.393700787))).intValue();            
    }

    public static double fromCMToPPI(double cm) {            
        return toPPI(cm * 0.393700787);            
    }

    public static double toPPI(double inch) {            
        return inch * 72d;            
    }

    public static double fromPPIToCM(double ppi) {            
        return ppi/(0.393700787 * 72d);            
    }

    
}
