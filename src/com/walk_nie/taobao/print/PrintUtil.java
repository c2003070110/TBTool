package com.walk_nie.taobao.print;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;


public class PrintUtil {
    public static String rootPathName = "./print";

    public static String printedOrderNosFileName = "printedOrderNos.txt";
    
    public static String toPrintFileName = "toPrint.txt";

    public static String splitor = "\t";

    public static String commonUseAddFileName = "commonUseAddress.txt";

    public static int LABEL_TYPE_EMS = 0;

    public static int LABEL_TYPE_POSTAL = 1;

    public static void setSenderInfo(PrintInfoObject obj) {
        obj.senderName="";
        obj.senderAddress1="";
        obj.senderAddress2="";
        obj.senderZipCode="123-0845";
        obj.senderZipTel="080-4200-1314";
    }
    public static boolean isPrintedInfo(List<PrintInfoObject> printedInfos, String orderNo) {
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
                sb.append(orderNo).append(splitor);
                sb.append(obj.receiverWWID).append(splitor);
                sb.append(obj.receiverName).append(splitor);
                
                sb.append(obj.receiverAddress1).append(obj.receiverAddress1);
                if(StringUtils.isNotEmpty(obj.receiverAddress3)){
                    sb.append(obj.receiverAddress3) ;
                }
                sb.append(splitor);
                sb.append(obj.receiverTel);
                sb.append("\n");
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
