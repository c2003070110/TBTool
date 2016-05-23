package com.walk_nie.taobao.print;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class PostalParcelPrintableForMultiPage implements Printable {

    public PrintInfoObject toPrintInfo = null;

    public PostalParcelPrintableForMultiPage(PrintInfoObject printInfo) {
        this.toPrintInfo = printInfo;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {

        if (toPrintInfo == null) {
            return NO_SUCH_PAGE;
        }
        try {
            PrintUtil.savePrintedOrderNos(toPrintInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("[Printing]" + toPrintInfo.receiverName + " " + toPrintInfo.receiverAddress1
                + toPrintInfo.receiverAddress2 + toPrintInfo.receiverAddress3);
        

        Graphics2D g2d = (Graphics2D) graphics;
        double width = pageFormat.getImageableWidth();
        double height = pageFormat.getImageableHeight();
        g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
        //g2d.draw(new Rectangle2D.Double(1, 1, width - 1, height - 1));
        FontMetrics fm = g2d.getFontMetrics();

        int h = fm.getAscent();
        if (StringUtils.isNotEmpty(toPrintInfo.senderName)) {
            g2d.drawString(toPrintInfo.senderName, PrintUtil.fromCMToPPI_i(3), h);
        }
        g2d.drawString(toPrintInfo.receiverName, PrintUtil.fromCMToPPI_i(11), h);
        
        h += fm.getHeight();
        if (StringUtils.isNotEmpty(toPrintInfo.senderAddress1)) {
            g2d.drawString(toPrintInfo.senderAddress1, PrintUtil.fromCMToPPI_i(1), h);
        }
        g2d.drawString(toPrintInfo.receiverAddress1, PrintUtil.fromCMToPPI_i(9.35), h);
        
        h += fm.getHeight();
        if (StringUtils.isNotEmpty(toPrintInfo.senderAddress2)) {
            g2d.drawString(toPrintInfo.senderAddress2, PrintUtil.fromCMToPPI_i(1), h);
        }
        g2d.drawString(toPrintInfo.receiverAddress2, PrintUtil.fromCMToPPI_i(9.35), h);
        
        h += fm.getHeight();
        if (StringUtils.isNotEmpty(toPrintInfo.senderAddress3)) {
            g2d.drawString(toPrintInfo.senderAddress3, PrintUtil.fromCMToPPI_i(1), h);
        }
        if (StringUtils.isNotEmpty(toPrintInfo.senderAddress3)) {
            g2d.drawString(toPrintInfo.senderAddress3, PrintUtil.fromCMToPPI_i(1), h);
        }

        h += fm.getHeight();
        g2d.drawString(toPrintInfo.receiverCountry, PrintUtil.fromCMToPPI_i(9.35), h);
        g2d.drawString(toPrintInfo.receiverTel, PrintUtil.fromCMToPPI_i(19.35), h);

        return PAGE_EXISTS;
    }
}
