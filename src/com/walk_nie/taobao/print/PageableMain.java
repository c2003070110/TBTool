package com.walk_nie.taobao.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.beust.jcommander.internal.Lists;

public class PageableMain implements Pageable {

    public static BufferedReader stdReader = null;

    int labelType = 0;

    List<PrintInfoObject> toPrintList = null;

    public static void main(String[] args) throws PrinterException, IOException {
        new PageableMain().print();
    }

    protected void print() throws PrinterException, IOException {
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
            String[] splited = toPrintLine.split(PrintUtil.splitor);
            PrintInfoObject obj = new PrintInfoObject();

            obj.receiverCountry = "中国";
            obj.receiverName = splited[0];
            PrintUtil.setAddress(obj, splited[1]);
            obj.receiverTel = splited[2];
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
            System.out.println("Ready for SAL printing ? 0 for ready");
            while (true) {
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
            System.out.println("Ready for SAL printing ? 0 for ready");
            while (true) {
                if (isReady()) {
                    printOut(toPrintList, PrintUtil.LABEL_TYPE_POSTAL);
                    break;
                }
            }
        }
    }

    private void printOut(List<PrintInfoObject> toPrintList, int labelType) throws PrinterException {

        PrintUtil.setSenderInfo(toPrintList);

        this.labelType = labelType;
        this.toPrintList = toPrintList;

        PrinterJob pj = getPrinterJob();

        pj.setPageable(this);
        pj.print();
    }

    @Override
    public int getNumberOfPages() {
        return toPrintList.size();
    }

    @Override
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        if (labelType == PrintUtil.LABEL_TYPE_EMS) {
            // EMS width = 27cm height=14cm
            return getPageFormat();
        } else if (labelType == PrintUtil.LABEL_TYPE_POSTAL) {
            return getPageFormat();
        } else {
            return null;
        }
    }

    @Override
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        if (labelType == PrintUtil.LABEL_TYPE_EMS) {
            return new EMS1Printable(toPrintList.get(pageIndex));
        } else if (labelType == PrintUtil.LABEL_TYPE_POSTAL) {
            return new PostalParcel1Printable(toPrintList.get(pageIndex));
        } else {
            return null;
        }
    }

    protected PrinterJob printJob = null;

    protected PrinterJob getPrinterJob() {
        if (printJob != null) {
            return printJob;
        }

        printJob = PrinterJob.getPrinterJob();
        if (printJob.printDialog()) {
            getPageFormat();
            return printJob;
        }
        return null;
    }

    protected PageFormat pageFormat = null;

    protected PageFormat getPageFormat() {
        if (pageFormat != null) {
            return pageFormat;
        }
        PageFormat pf = getPrinterJob().defaultPage();
        System.out.println("Imageable(default)(cm)-" + ": width = "
                + PrintUtil.fromPPIToCM(pf.getImageableWidth()) + "; height = "
                + PrintUtil.fromPPIToCM(pf.getImageableHeight()));

        Paper paper = pf.getPaper();
        double margin = PrintUtil.fromCMToPPI(0.2);
        paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight()
                - margin * 2);
        pf.setPaper(paper);
        pageFormat = getPrinterJob().pageDialog(pf);
        System.out.println("Imageable(set)(cm)-" + ": width = "
                + PrintUtil.fromPPIToCM(pageFormat.getImageableWidth()) + "; height = "
                + PrintUtil.fromPPIToCM(pageFormat.getImageableHeight()));
        return pageFormat;
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
