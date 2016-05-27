package com.walk_nie.taobao.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class PrintableForSAL implements Printable {

    public PrintInfoObject toPrintInfo = null;

    public PrintableForSAL(PrintInfoObject printInfo) {
        this.toPrintInfo = printInfo;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {

        if (toPrintInfo == null) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        LabelHelper.setInfoForSALLabel(pageFormat, g2d, pageIndex, toPrintInfo);

        return PAGE_EXISTS;
    }
}
