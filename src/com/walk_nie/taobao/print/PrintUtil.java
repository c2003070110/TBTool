package com.walk_nie.taobao.print;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;


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
        obj.senderName="";
        obj.senderAddress1="";
        obj.senderAddress2="";
        obj.senderZipCode="123-0845";
        obj.senderTel="080-4200-1314";
        
        obj.receiverCountry = "中国";
    }
    
    public static List<String> getCommonUseAddress() throws IOException {
        File file = new File(PrintUtil.rootPathName, PrintUtil.commonUseAddFileName);
        return FileUtils.readLines(file, "UTF-8");
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
    
    public static List<PrintInfoObject> getPrintInfoList(int labelType,int patternType) throws IOException {
        List<PrintInfoObject> printList = Lists.newArrayList();
        String fileName = null;
        if(labelType==PrintUtil.LABEL_TYPE_EMS && patternType == 1){
             fileName = PrintUtil.toPrintFileName1EMS;
        }else if(labelType==PrintUtil.LABEL_TYPE_POSTAL && patternType == 1){
            fileName = PrintUtil.toPrintFileName1SAL;
        }else if(labelType==PrintUtil.LABEL_TYPE_EMS && patternType == 2){
            fileName = PrintUtil.toPrintFileName2EMS;
        }else if(labelType==PrintUtil.LABEL_TYPE_POSTAL && patternType == 2){
            fileName = PrintUtil.toPrintFileName2SAL;
        }
        File file = new File(PrintUtil.rootPathName, fileName);
        List<String> list = null;
        try {
            list = FileUtils.readLines(file, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return printList;
        }
        List<PrintInfoObject> printedInfos = PrintUtil.readPrintedInfoList();
        for (String str : list) {
            if (str.equals(""))
                continue;
            if (str.startsWith("#"))
                continue;
            int indx = 0;
            if(patternType == 1){
            	// 订单编号	买家会员名	买家支付宝账号	买家应付货款	买家应付邮费	买家支付积分	总金额	返点积分	买家实际支付金额	买家实际支付积分	订单状态	买家留言	收货人姓名	收货地址 	运送方式	联系电话 	联系手机	订单创建时间	订单付款时间 	宝贝标题 	宝贝种类 	物流单号 	物流公司	订单备注	宝贝总数量	店铺Id	店铺名称	订单关闭原因	卖家服务费	买家服务费	发票抬头	是否手机订单	分阶段订单信息	特权订金订单id	定金排名	修改后的sku	修改后的收货地址	异常信息	天猫卡券抵扣	集分宝抵扣	是否是O2O交易
                // 1930679858170647	幸福蔓岩1627	844677789@qq.com	598	0	0	598	0	598	0	买家已付款，等待卖家发货		马慧	吉林省 通化市 柳河县 柳河镇紫御府   七号楼    3单元   201室(135300)	快递		'18618486065	2016/5/23 22:02	2016/5/23 22:02	日本直邮mamakids洗面奶+化妆水+乳液护肤特惠套装mama&kids包邮	1				1	0	Funny宝贝快乐购	订单未关闭	0	0元		手机订单									

                String[] splited = str.split(PrintUtil.splitor);
                String orderNo = splited[indx++];
                if (PrintUtil.isPrintedInfo(printedInfos,orderNo)) continue;
                indx = 10;// 订单状态
                String sts = splited[indx++];
                if(!sts.equals("买家已付款")) continue;
                
                PrintInfoObject obj = new PrintInfoObject();
    
                obj.orderNo = orderNo;
                indx = 1; //买家会员名
                obj.receiverWWID = splited[indx++];
                indx = 12; //收货人姓名
                obj.receiverName = splited[indx++];
                indx = 13; //收货地址 
                PrintUtil.setAddress(obj, splited[indx++]);
                indx = 15; //联系电话  联系手机
                String tel1 = splited[indx++].replaceAll("'", "");
                String tel2 = splited[indx++].replaceAll("'", "");
                obj.receiverTel = StringUtils.isNotEmpty(tel2)?tel2:tel1;
                printList.add(obj);
            }else if(patternType == 2){
                //颜宁, 13438327357, 四川省, 成都市, 金堂县, 三星镇金堂大道9号四川师范大学文理学院, 610401
                String[] splited = str.split(",");
                PrintInfoObject obj = new PrintInfoObject();

                obj.receiverName = splited[indx++];
                obj.receiverTel = splited[indx++];
                String newAddr = splited[indx++] +" " +splited[indx++] +" " +splited[indx++] +" " +splited[indx++];
                setAddress(obj,newAddr);
                obj.receiverZipCode = splited[indx++];
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
        for (PrintInfoObject obj : printedList) {
            for (String orderNo : obj.orderNos) {
                if(StringUtils.isEmpty(orderNo)){
                    orderNo = "";
                }
                sb.append(orderNo).append(splitor);
                String receiverWWID = obj.receiverWWID;
                if(StringUtils.isEmpty(receiverWWID)){
                    receiverWWID = "";
                }
                sb.append(receiverWWID).append(splitor);
                sb.append(obj.receiverName).append(splitor);
                
                sb.append(obj.receiverAddress1).append(obj.receiverAddress2);
                if(StringUtils.isNotEmpty(obj.receiverAddress3)){
                    sb.append(obj.receiverAddress3) ;
                }
                sb.append(splitor);
                sb.append(obj.receiverTel);
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
        int splitIdx = 22;
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
