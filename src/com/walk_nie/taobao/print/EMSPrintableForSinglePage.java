package com.walk_nie.taobao.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class EMSPrintableForSinglePage implements Printable {

    public PrintInfoObject printInfo = null;

    public EMSPrintableForSinglePage(PrintInfoObject printInfo) {
        this.printInfo = printInfo;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {

        if (printInfo == null) {
            return NO_SUCH_PAGE;
        }
//        if (pageIndex >= 1) {
//            try {
//                PrintUtil.savePrintedOrderNos(printInfo);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return NO_SUCH_PAGE;
//        }
//        
//        System.out.println("[Printing]" + printInfo.receiverName + " " + printInfo.receiverAddress1
//                + printInfo.receiverAddress2 + printInfo.receiverAddress3);
//        
        Graphics2D g2d = (Graphics2D) graphics;
        LabelHelper.setInfoForEMSLabel(pageFormat, g2d, pageIndex, printInfo);

        return PAGE_EXISTS;
    }
}
