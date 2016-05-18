package com.walk_nie.taobao.print;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;

public class PrintMain2 {


    public static String toPrintFileName = "toPrint.txt";

    public static String splitor = "\t";

    public static String commonUseAddFileName = "commonUseAddress.txt";

    public static int LABEL_TYPE_EMS = 0;

    public static int LABEL_TYPE_POSTAL = 1;

    public static void main(String[] args) throws PrinterException, IOException {
        try {
        	printTaobao();
//            System.out.print("Type of Print : ");
//            System.out.println("1:common use address;2:taobao");

//            BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));
//            while (true) {
//                String line = stdReader.readLine();
//                if ("1".equals(line.trim())) {
//                    printCommonUseAddress();
//                    break;
//                } else if ("2".equals(line.trim())) {
//                    printTaobao();
//                    break;
//                } else {
//                    System.out.println("Listed number only!");
//                }
//            }
//            stdReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printCommonUseAddress() throws PrinterException, IOException {
        List<String> adressList = getCommonUseAddress();
        
        System.out.println("which address?");
        for (int idx = 0; idx < adressList.size(); idx++) {
            System.out.println(idx + ":" + adressList.get(idx));
        }
        List<String> selectedLines = Lists.newArrayList();
        BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = stdReader.readLine();
            int idx = line.indexOf(",");
            if(idx != -1){
                String[] poss = line.split(",");
                boolean allOk = true;
                for(String pos:poss){
                    boolean rslt = checkPosition(adressList, pos);
                    if(!rslt){allOk=false;break;}
                }
                if(allOk){
                    for(String pos:poss){
                        selectedLines.add(adressList.get(Integer.parseInt(pos)));
                    }
                    break;
                }
            }else{
                if(checkPosition(adressList, line)){
                    selectedLines.add(adressList.get(Integer.parseInt(line)));
                    break;
                }
            }
        }

        System.out.println("0:ems?1:sal?");
        int labelType = 0;
        while (true) {
            String line = stdReader.readLine();
            try {
                int pos = Integer.parseInt(line);
                if (pos == 0 || pos == 1) {
                    labelType = pos;
                    break;
                } else {
                    System.out.println("Listed number only!");
                }
            } catch (Exception e) {
                System.out.println("number only!");
            }
        }

        stdReader.close();
        List<PrintInfoObject> toPrintList = Lists.newArrayList();
        for (String toPrintLine : selectedLines) {
            String[] splited = toPrintLine.split(splitor);
            PrintInfoObject obj = new PrintInfoObject();

            obj.receiverCountry = "CHINA";
            obj.receiverName = splited[0];
            obj.receiverTel = splited[1];
            setAddress(obj, splited[2]);
            toPrintList.add(obj);
        }
        printOut(toPrintList, labelType);
    }

    public static boolean checkPosition(List<String> adressList, String line) {
        try {
            int pos = Integer.parseInt(line);
            if (pos >= 0 && pos <= adressList.size()) {
                return true;
            } else {
                System.out.println("Listed number only!");
                return false;
            }
        } catch (Exception e) {
            System.out.println("number only!");
            return false;
        }
    }

	private static void printTaobao() throws IOException, PrinterException {
		while(true){
		List<PrintInfoObject> toPrintList = getPrintInfoListEMS();
		if (!toPrintList.isEmpty()) {
			System.out.println("Ready for EMS printing ? 0 for ready");
			if (isReady()) {
				printOut(toPrintList, LABEL_TYPE_EMS);
				//savePrintedOrderNos(toPrintList);
			}
		}else{
			break;
		}
		}
		List<PrintInfoObject> toPrintList = getPrintInfoListPostal();
		if (!toPrintList.isEmpty()) {
			System.out.println("Ready for SAL printing ? 0 for ready");
			if (isReady()) {
				printOut(toPrintList, LABEL_TYPE_POSTAL);
				//savePrintedOrderNos(toPrintList);
			}
		}
	}

    private static void printOut(List<PrintInfoObject> toPrintList, int labelType)
            throws PrinterException {

        setSenderInfo(toPrintList);
//        PrintService[] printServices = PrinterJob.lookupPrintServices();
//        DocPrintJob pj1 = null;
//        for(PrintService printService:printServices){
//        	System.out.println(printService.getName());
//        	if("EPSON VP-6200 ESC/P".equals(printService.getName())){
//        		pj1 = printService.createPrintJob();
//        	}
//        }
        PrinterJob pj = PrinterJob.getPrinterJob();
        
        if (pj.printDialog()) {

            PageFormat pf = pj.defaultPage();
            Paper paper = pf.getPaper();
            double margin = 0;
            paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight()
                    - margin * 2);
            pf.setPaper(paper);
            // System.out.println("PageFormat-" + ": width = " + pf.getWidth() + "; height = " +
            // pf.getHeight());
            //pf = pj.pageDialog(pf);
            paper = pf.getPaper();
            //System.out.println("paper-" + ": width = " + paper.getWidth() + "; height = " + paper.getHeight());
            //System.out.println("ems-" + ": width = " + PrintUtil.fromCMToPPI(26) + "; height = " + PrintUtil.fromCMToPPI(14));
            for(PrintInfoObject obj:toPrintList){
            	List<PrintInfoObject> newList = Lists.newArrayList();
            	newList.add(obj);
            if (labelType == LABEL_TYPE_EMS) {
                // EMS width = 27cm height=14cm
                pj.setPrintable(new EMS1Printable(newList), pf);
            } else if (labelType == LABEL_TYPE_POSTAL) {
                pj.setPrintable(new PostalParcelPrintable(newList), pf);
            }
            }
            pj.print();
        }
    }

    private static List<String> getCommonUseAddress() throws IOException {
        File file = new File(PrintUtil.rootPathName, commonUseAddFileName);
        return FileUtils.readLines(file, "UTF-8");
    }

    protected static List<PrintInfoObject> getPrintInfoListEMS() throws IOException {
        return  getPrintInfoList(LABEL_TYPE_EMS);
    }

    protected static List<PrintInfoObject> getPrintInfoListPostal() throws IOException {
        return  getPrintInfoList(LABEL_TYPE_POSTAL);
    }
    
    protected static List<PrintInfoObject> getPrintInfoList(int labelType) throws IOException {
        List<PrintInfoObject> printList = Lists.newArrayList();
        File file = new File(PrintUtil.rootPathName, toPrintFileName);
        List<String> list = FileUtils.readLines(file, "UTF-8");
        List<String> printedOrderNos = readPrintedOrderNos();
        for (String str : list) {
            if (str.equals(""))
                continue;
            String[] splited = str.split(splitor);
            //if (splited.length != 11)
            //    continue;

            String labelTypeStr = splited[0];
            if((labelType==LABEL_TYPE_EMS && !"ems".equalsIgnoreCase(labelTypeStr))
                    || (labelType==LABEL_TYPE_POSTAL && !"sal".equalsIgnoreCase(labelTypeStr))){
                continue;
            }

            String orderNo = splited[1];
            if (printedOrderNos.contains(orderNo))
                continue;
            PrintInfoObject obj = new PrintInfoObject();

            obj.receiverCountry = "中国";

            obj.orderNo = orderNo;
            obj.receiverWWID = splited[2];
            obj.receiverName = splited[3];
            setAddress(obj, splited[4]);
            obj.receiverTel = splited[5];
            
            printList.add(obj);
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

    protected static List<String> readPrintedOrderNos() throws IOException {
        File file = new File(PrintUtil.rootPathName, PrintUtil.printedOrderNosFileName);
        if (!file.exists())
            return Lists.newArrayList();
        List<String> lines = Files.readLines(file, Charset.forName("UTF-8"));
        return lines;
    }

    protected static void setAddress(PrintInfoObject obj, String address) {
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

    private static boolean isReady() throws IOException {
        BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            String line = stdReader.readLine();
            if(line.equals("0")){
                break;
            }
        }
        //stdReader.close();
        return true;
    }

    private static void setSenderInfo(List<PrintInfoObject> toPrintList) {
        for (PrintInfoObject obj : toPrintList) {
            obj.senderName="";
            obj.senderAddress1="";
            obj.senderAddress2="";
            obj.senderZipCode="123-0845";
            obj.senderZipTel="080-4200-1314";
        }
    }
}
