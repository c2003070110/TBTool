package com.walk_nie.taobao.print;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.List;

public class PackagePrintable implements Printable {
    public List<PrintInfoObject> toPrintList = null;
    
    public PackagePrintable(List<PrintInfoObject> printInfo){
        this.toPrintList = printInfo;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        
        if(toPrintList == null || pageIndex >= toPrintList.size()){
            return NO_SUCH_PAGE;
        }
        PrintInfoObject printInfo = toPrintList.get(pageIndex);
        
        Graphics2D g2d = (Graphics2D) graphics;
        double width = pageFormat.getImageableWidth();
        double height = pageFormat.getImageableHeight();
        g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
        g2d.draw(new Rectangle2D.Double(1, 1, width - 1, height - 1));
        FontMetrics fm = g2d.getFontMetrics();
        
        int h = fm.getAscent();
        g2d.drawString(printInfo.senderName, PrintUtil.fromCMToPPI_i(3), h);
        g2d.drawString(printInfo.receiverName, PrintUtil.fromCMToPPI_i(11), h);
        h += fm.getHeight();
        g2d.drawString(printInfo.senderAddress1, PrintUtil.fromCMToPPI_i(1), h);
        g2d.drawString(printInfo.receiverAddress1, PrintUtil.fromCMToPPI_i(9.35), h);
        h += fm.getHeight();
        g2d.drawString(printInfo.senderAddress2, PrintUtil.fromCMToPPI_i(1), h);
        g2d.drawString(printInfo.receiverAddress2, PrintUtil.fromCMToPPI_i(9.35), h);
        h += fm.getHeight();
        g2d.drawString(printInfo.receiverCountry, PrintUtil.fromCMToPPI_i(9.35), h);
        g2d.drawString(printInfo.receiverTel, PrintUtil.fromCMToPPI_i(19.35), h);
        
        return PAGE_EXISTS;
    }
}
