package com.walk_nie.taobao.print;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.beust.jcommander.internal.Lists;

public class PrintTest {
    
    public static BufferedReader stdReader = null;

    public static void main(String[] args) throws PrinterException, IOException {
        try {
            System.out.print("Type of Print : ");
            System.out.println("0:taobao export;1:taobao copy;2:common use address;");

            stdReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = stdReader.readLine();
                if ("0".equals(line.trim())) {
                    printTaobaoExport();
                    break;
                } else if ("1".equals(line.trim())) {
                    printTaobaoCopy();
                    break;
                } else if ("2".equals(line.trim())) {
                    printCommonUseAddress();
                    break;
                } else {
                    System.out.println("Listed number only!");
                }
            }
            stdReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printCommonUseAddress() throws PrinterException, IOException {
        List<String> adressList = PrintUtil.getCommonUseAddress();
        
        System.out.println("which address?");
        for (int idx = 0; idx < adressList.size(); idx++) {
            System.out.println(idx + ":" + adressList.get(idx));
        }
        List<String> selectedLines = Lists.newArrayList();
        //BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));
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

        //stdReader.close();
        List<PrintInfoObject> toPrintList = Lists.newArrayList();
        for (String toPrintLine : selectedLines) {
            String[] splited = toPrintLine.split(PrintUtil.splitor);
            PrintInfoObject obj = new PrintInfoObject();

            obj.receiverCountry = "中国";
            obj.receiverName = splited[0];
            PrintUtil.setAddress(obj, splited[1]);
            obj.receiverTel = splited[2];
            toPrintList.add(obj);
        }
        for(PrintInfoObject obj :toPrintList){
            while (true) {
                System.out.println("Ready for printing ? 0 for ready");
                if (isReady()) {
                    printOut(obj, labelType);
                    break;
                }
            }
        }
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

    private static void printTaobaoCopy() throws IOException, PrinterException {
        List<PrintInfoObject> toPrintList = PrintUtil.getPrintInfoList2EMS();
        if (!toPrintList.isEmpty()) {
            for (PrintInfoObject obj : toPrintList) {
                while (true) {
                    System.out.println("Ready for EMS printing ? 0 for ready");
                    if (isReady()) {
                        printOut(obj, PrintUtil.LABEL_TYPE_EMS);
                        break;
                    }
                }
            }
        }

        resetPrintJob();

        toPrintList = PrintUtil.getPrintInfoList2Postal();
        if (!toPrintList.isEmpty()) {
            for (PrintInfoObject obj : toPrintList) {
                System.out.println("Ready for SAL printing ? 0 for ready");
                while (true) {
                if (isReady()) {
                    printOut(obj, PrintUtil.LABEL_TYPE_POSTAL);
                    break;
                }
                }
            }
        }
        
    }

    private static void printTaobaoExport() throws IOException, PrinterException {
        List<PrintInfoObject> toPrintList = PrintUtil.getPrintInfoList1EMS();
        if (!toPrintList.isEmpty()) {
            for (PrintInfoObject obj : toPrintList) {
                while (true) {
                    System.out.println("Ready for EMS printing ? 0 for ready");
                    if (isReady()) {
                        printOut(obj, PrintUtil.LABEL_TYPE_EMS);
                        break;
                    }
                }
            }
        }

        resetPrintJob();

        toPrintList = PrintUtil.getPrintInfoList1Postal();
        if (!toPrintList.isEmpty()) {
            for (PrintInfoObject obj : toPrintList) {
                System.out.println("Ready for SAL printing ? 0 for ready");
                while (true) {
                if (isReady()) {
                    printOut(obj, PrintUtil.LABEL_TYPE_POSTAL);
                    break;
                }
                }
            }
        }
    }

    private static void printOut(PrintInfoObject toPrintInfo, int labelType)
            throws PrinterException {

        PrintUtil.setSenderInfo(toPrintInfo);

        PrinterJob pj = getPrinterJob();
//        if(pj instanceof RasterPrinterJob){
//            ((RasterPrinterJob)pj).debugPrint = true;
//        }
        
        PageFormat pf = getPageFormat();
        
        if (labelType == PrintUtil.LABEL_TYPE_EMS) {
            // EMS width = 27cm height=14cm
            pj.setPrintable(new EMSPrintableForSinglePage(toPrintInfo),pf);
        } else if (labelType == PrintUtil.LABEL_TYPE_POSTAL) {
            pj.setPrintable(new SALPrintableForSinglePage(toPrintInfo),pf);
        }
        pj.print();
    }
    
    protected static PrinterJob printJob = null;
    protected static PrinterJob getPrinterJob(){
        if(printJob != null){
            return printJob;
        }
        
        printJob = PrinterJob.getPrinterJob();
        if (printJob.printDialog()) {
            return printJob;
        }
        return null;
    }
    protected static PageFormat pageFormat = null;
    protected static PageFormat getPageFormat(){
        if(pageFormat != null){
            return pageFormat;
        }
        PageFormat pf = getPrinterJob().defaultPage();
        System.out.println("Imageable(default)(cm)-" + ": width = " + PrintUtil.fromPPIToCM(pf.getImageableWidth()) + "; height = " + PrintUtil.fromPPIToCM(pf.getImageableHeight()));
        
        Paper paper = pf.getPaper();
        double margin = PrintUtil.fromCMToPPI(0.2);
        paper.setImageableArea(margin, margin, 
                paper.getWidth() - margin * 2, 
                paper.getHeight() - margin * 2);
        pf.setPaper(paper);
        pageFormat = getPrinterJob().pageDialog(pf);
        System.out.println("Imageable(set)(cm)-" + ": width = " + PrintUtil.fromPPIToCM(pageFormat.getImageableWidth()) + "; height = " + PrintUtil.fromPPIToCM(pageFormat.getImageableHeight()));
        return pageFormat;
    }

    private static boolean isReady() throws IOException {
        //BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            String line = stdReader.readLine();
            if(line.equals("0")){
                break;
            }
        }
        //stdReader.close();
        return true;
    }
 


    private static void resetPrintJob() {
        printJob = null;
        pageFormat = null;
    }
}
