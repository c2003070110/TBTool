package com.walk_nie.taobao.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class EMSPrintableForMultiPage implements Printable {

    public PrintInfoObject printInfo = null;

    public EMSPrintableForMultiPage(PrintInfoObject printInfo) {
        this.printInfo = printInfo;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {

        if (printInfo == null) {
            return NO_SUCH_PAGE;
        }
        
        Graphics2D g2d = (Graphics2D) graphics;
        LabelHelper.setInfoForEMSLabel(pageFormat, g2d, pageIndex, printInfo);

        return PAGE_EXISTS;
    }
}
