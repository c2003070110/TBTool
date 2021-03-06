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

public class PrintMain {

    public static BufferedReader stdReader = null;

    public static void main(String[] args) throws PrinterException, IOException {
        new PrintMain().print();
    }

    protected void print() throws PrinterException, IOException {
        try {
            System.out.print("Type of Print : ");
            System.out.println("0:taobao export;\n" +
            		           "1:taobao copy;\n" +
            		           "2:common use address;\n" +
            		           "3:type in directly;");

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
                } else if ("3".equals(line.trim())) {
                    printTypeInDirectly();
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

    private void printTypeInDirectly() throws IOException, PrinterException {
        List<PrintInfoObject> toPrintList = Lists.newArrayList();
        while(true){
            System.out.println("type the address for printing or 0 for printing");
            String line = stdReader.readLine();
            if("0".equals(line)){
                break;
            }
            PrintInfoObject obj = PrintUtil.parseToPrintInfoObjectByTab(line);
            if(obj == null) continue;
            toPrintList.add(obj);
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
        printOut(toPrintList, labelType);
    }

    private void printCommonUseAddress() throws PrinterException, IOException {
        List<String> adressList = PrintUtil.getCommonUseAddress();

        System.out.println("which address?");
        for (int idx = 0; idx < adressList.size(); idx++) {
            System.out.println(idx + ":" + adressList.get(idx));
        }
        List<String> selectedLines = Lists.newArrayList();
        // BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = stdReader.readLine();
            int idx = line.indexOf(",");
            if (idx != -1) {
                String[] poss = line.split(",");
                boolean allOk = true;
                for (String pos : poss) {
                    boolean rslt = checkPosition(adressList, pos);
                    if (!rslt) {
                        allOk = false;
                        break;
                    }
                }
                if (allOk) {
                    for (String pos : poss) {
                        selectedLines.add(adressList.get(Integer.parseInt(pos)));
                    }
                    break;
                }
            } else {
                if (checkPosition(adressList, line)) {
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

        // stdReader.close();
        List<PrintInfoObject> toPrintList = Lists.newArrayList();
        for (String toPrintLine : selectedLines) {
            PrintInfoObject obj = PrintUtil.parseToPrintInfoObjectByComma(toPrintLine);
            if(obj == null){
                continue;
            }
            toPrintList.add(obj);
        }
        while (true) {
            System.out.println("Ready for printing ? 0 for ready");
            if (isReady()) {
                printOut(toPrintList, labelType);
                break;
            }
        }
    }

    public boolean checkPosition(List<String> adressList, String line) {
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

    private void printTaobaoCopy() throws IOException, PrinterException {
        List<PrintInfoObject> toPrintList = PrintUtil.getPrintInfoList2EMS();
        if (!toPrintList.isEmpty()) {
            System.out.println("size of printing list  = " + toPrintList.size());
            while (true) {
                System.out.println("Ready for EMS printing ? 0 for ready");
                if (isReady()) {
                    printOut(toPrintList, PrintUtil.LABEL_TYPE_EMS);
                    break;
                }
            }
        }

        resetPrintJob();

        toPrintList = PrintUtil.getPrintInfoList2Postal();
        if (!toPrintList.isEmpty()) {
            System.out.println("size of printing list  = " + toPrintList.size());
            while (true) {
                System.out.println("Ready for SAL printing ? 0 for ready");
                if (isReady()) {
                    printOut(toPrintList, PrintUtil.LABEL_TYPE_POSTAL);
                    break;
                }
            }
        }

    }

    private void printTaobaoExport() throws IOException, PrinterException {
        List<PrintInfoObject> toPrintList = PrintUtil.getPrintInfoList1EMS();
        if (!toPrintList.isEmpty()) {
            System.out.println("size of printing list  = " + toPrintList.size());
            while (true) {
                System.out.println("Ready for EMS printing ? 0 for ready");
                if (isReady()) {
                    printOut(toPrintList, PrintUtil.LABEL_TYPE_EMS);
                    break;
                }
            }
        }

        resetPrintJob();

        toPrintList = PrintUtil.getPrintInfoList1Postal();
        if (!toPrintList.isEmpty()) {
            System.out.println("size of printing list  = " + toPrintList.size());
            while (true) {
                System.out.println("Ready for SAL printing ? 0 for ready");
                if (isReady()) {
                    printOut(toPrintList, PrintUtil.LABEL_TYPE_POSTAL);
                    break;
                }
            }
        }
    }

	private void printOut(List<PrintInfoObject> toPrintList, int labelType)
			throws PrinterException {

		PrintUtil.setSenderInfo(toPrintList);
		PrinterJob pj = getPrinterJob();

		pj.setPageable(new MyPageable(toPrintList, getPageFormat(), labelType));
		pj.print();
		try {
			PrintUtil.savePrintedOrderNos(toPrintList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// int interupt = 5;
		// int loopCnt = (int) Math.ceil(toPrintList.size() / interupt);
		// for (int i = 0; i <= loopCnt; i++) {
		// List<PrintInfoObject> newList = Lists.newArrayList();
		// for (int j = i * interupt; j < i * interupt + 5; j++) {
		// if(j>=toPrintList.size())break;
		// newList.add(toPrintList.get(j));
		// }
		// if(newList.isEmpty()) break;
		//
		// PrinterJob pj = getPrinterJob();
		//
		// pj.setPageable(new MyPageable(newList,getPageFormat(),labelType));
		// pj.print();
		//
		// resetPrintJob();
		// try {
		// PrintUtil.savePrintedOrderNos(newList);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
	}

    protected PrinterJob printJob = null;

    protected PrinterJob getPrinterJob() {
        if (printJob != null) {
            return printJob;
        }

        printJob = PrinterJob.getPrinterJob();
//        PrintRequestAttributeSet pas = new HashPrintRequestAttributeSet() ;
//        
//        if (labelType == PrintUtil.LABEL_TYPE_EMS) {
//            // rare tray for EMS
//            pas.add(MediaTray.SIDE);
//        } else if (labelType == PrintUtil.LABEL_TYPE_POSTAL) {
//            // front tray for SAL
//            pas.add(MediaTray.MANUAL);
//        }
		
        if (printJob.printDialog()) {
            initPageFormat();
            return printJob;
        }
        return null;
    }

    protected PageFormat pageFormat = null;
    private PageFormat getPageFormat() {
        return pageFormat;
    }

    private void initPageFormat() {
        if (pageFormat != null) {
            return;
        }
        PageFormat pf = getPrinterJob().defaultPage();
        Paper paper = pf.getPaper();
        // double width = PrintUtil.fromCMToPPI(27);
        // double height = PrintUtil.fromCMToPPI(15.3);
        double width = paper.getWidth();
        double height = paper.getHeight();
        double margin = PrintUtil.fromCMToPPI(0.0);
        paper.setImageableArea(margin, margin, width - margin * 2, height - margin * 2);
        pf.setPaper(paper);
        
        
        pageFormat = getPrinterJob().pageDialog(pf);
        System.out.println("Imageable(set)(cm)-" + ": width = "
                + PrintUtil.fromPPIToCM(pageFormat.getImageableWidth()) + "; height = "
                + PrintUtil.fromPPIToCM(pageFormat.getImageableHeight()));
    }

    private void resetPrintJob() {
        printJob = null;
        pageFormat = null;
    }
    private static boolean isReady() throws IOException {
        while (true) {
            String line = stdReader.readLine();
            if (line.equals("0")) {
                break;
            }
        }
        return true;
    }
}
