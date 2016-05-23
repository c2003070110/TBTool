package com.walk_nie.taobao.print;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

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
    	try {
			PrintUtil.savePrintedOrderNos(printInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        System.out.println("[Printing]" + printInfo.receiverName + " " + printInfo.receiverAddress1
                + printInfo.receiverAddress2 + printInfo.receiverAddress3);
        
        Graphics2D g2d = (Graphics2D) graphics;
        double width = pageFormat.getImageableWidth();
        double height = pageFormat.getImageableHeight();
        
        System.out.println("Imageable(Printable)(cm)-" + ": width = " + PrintUtil.fromPPIToCM(width) + "; height = " + PrintUtil.fromPPIToCM(height));
        g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
        //g2d.draw(new Rectangle2D.Double(1, 1, width - 1, height - 1));
        FontMetrics fm = g2d.getFontMetrics();

        int h = PrintUtil.fromCMToPPI_i(0.7);
        if (StringUtils.isNotEmpty(printInfo.receiverWWID)) {
            g2d.drawString(printInfo.receiverWWID, PrintUtil.fromCMToPPI_i(21), h);
        }

        h = PrintUtil.fromCMToPPI_i(4.6);
        g2d.drawString(printInfo.receiverName, PrintUtil.fromCMToPPI_i(13), h);
        h += fm.getHeight() + PrintUtil.fromCMToPPI_i(0.2);
        g2d.drawString(printInfo.receiverAddress1, PrintUtil.fromCMToPPI_i(11.2), h);
        h += fm.getHeight() + PrintUtil.fromCMToPPI_i(0.2);
        g2d.drawString(printInfo.receiverAddress2, PrintUtil.fromCMToPPI_i(11.2), h);
		if (StringUtils.isNotEmpty(printInfo.receiverAddress3)) {
			h += fm.getHeight() + PrintUtil.fromCMToPPI_i(0.2);
			g2d.drawString(printInfo.receiverAddress3,
					PrintUtil.fromCMToPPI_i(11.2), h);
		}
        
        h = PrintUtil.fromCMToPPI_i(8.17);
        g2d.drawString(printInfo.receiverCountry, PrintUtil.fromCMToPPI_i(11), h);
        g2d.drawString(printInfo.receiverTel, PrintUtil.fromCMToPPI_i(16.2), h);

        h = PrintUtil.fromCMToPPI_i(10.07);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(16.5), h);

        h = PrintUtil.fromCMToPPI_i(12.37);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(3.2), h);

        h = PrintUtil.fromCMToPPI_i(13.3);
        g2d.drawString("邓祎", PrintUtil.fromCMToPPI_i(3.4), h);
        g2d.drawString("1", PrintUtil.fromCMToPPI_i(8.8), h);
        g2d.drawString("1", PrintUtil.fromCMToPPI_i(9.9), h);
        

        return PAGE_EXISTS;
    }
}
