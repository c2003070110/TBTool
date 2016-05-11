package com.walk_nie.taobao.print;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.List;

public class EMS1Printable implements Printable {
    public List<PrintInfoObject> toPrintList = null;
    
    public EMS1Printable(List<PrintInfoObject> printInfo){
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
        
        int h = PrintUtil.fromCMToPPI_i(1.5);
        g2d.drawString(printInfo.receiverName, PrintUtil.fromCMToPPI_i(11), h);
        h += fm.getHeight();
        g2d.drawString(printInfo.receiverAddress1, PrintUtil.fromCMToPPI_i(9), h);
        h += fm.getHeight();
        g2d.drawString(printInfo.receiverAddress2, PrintUtil.fromCMToPPI_i(9), h);
        h += fm.getHeight();
        g2d.drawString(printInfo.receiverCountry, PrintUtil.fromCMToPPI_i(9), h);
        g2d.drawString(printInfo.receiverTel, PrintUtil.fromCMToPPI_i(12), h);
        
        return PAGE_EXISTS;
    }
}
