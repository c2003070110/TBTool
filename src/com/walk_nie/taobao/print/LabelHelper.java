package com.walk_nie.taobao.print;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;



public class LabelHelper {

    public static void setInfoForSALLabel(PageFormat pageFormat, Graphics2D g2d,int pageIndex,PrintInfoObject toPrintInfo) {
        //double width = pageFormat.getImageableWidth();
        //double height = pageFormat.getImageableHeight();
        g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
        //g2d.draw(new Rectangle2D.Double(1, 1, width - 1, height - 1));
        FontMetrics fm = g2d.getFontMetrics();

        // TODO adjust width and height
        int h = fm.getAscent();
        // name
        if (StringUtils.isNotEmpty(toPrintInfo.senderName)) {
            g2d.drawString(toPrintInfo.senderName, PrintUtil.fromCMToPPI_i(3), h);
        }
        g2d.drawString(toPrintInfo.receiverName, PrintUtil.fromCMToPPI_i(11), h);
        
        h += fm.getHeight();
        // address
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
        // receiver post
        if (StringUtils.isNotEmpty(toPrintInfo.receiverZipCode)) {
            g2d.drawString(toPrintInfo.receiverZipCode, PrintUtil.fromCMToPPI_i(9.35), h);
        }
        g2d.drawString(toPrintInfo.receiverAddress1, PrintUtil.fromCMToPPI_i(19.35), h);
        
        h += fm.getHeight();
        g2d.drawString(toPrintInfo.senderZipCode, PrintUtil.fromCMToPPI_i(2.35), h);
        g2d.drawString(toPrintInfo.senderTel, PrintUtil.fromCMToPPI_i(12.35), h);

        g2d.drawString(toPrintInfo.receiverTel, PrintUtil.fromCMToPPI_i(20.35), h);
        g2d.drawString(toPrintInfo.receiverCountry, PrintUtil.fromCMToPPI_i(22.35), h);
        
        h = PrintUtil.fromCMToPPI_i(20.34);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(22.35), h);

        h = PrintUtil.fromCMToPPI_i(22.34);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(1.35), h);
        g2d.drawString("1", PrintUtil.fromCMToPPI_i(21.35), h);
        g2d.drawString("1", PrintUtil.fromCMToPPI_i(22.35), h);
        
        List<String> dateList = PrintUtil.parseDateToString();
        h = PrintUtil.fromCMToPPI_i(22.34);
        int idx = 0;double intr=0.1;double startPos = 22.35;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString("邓祎", PrintUtil.fromCMToPPI_i(22.35), h);

        h = PrintUtil.fromCMToPPI_i(23.34);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(1.35), h);
        g2d.drawString("30", PrintUtil.fromCMToPPI_i(6.35), h);
        
        h = PrintUtil.fromCMToPPI_i(24.34);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(1.35), h);
    }

    public static void setInfoForEMSLabel(PageFormat pageFormat, Graphics2D g2d, int pageIndex,
            PrintInfoObject printInfo) {
        // double width = pageFormat.getImageableWidth();
        // double height = pageFormat.getImageableHeight();

        // System.out.println("Imageable(Printable)(cm)-" + ": width = " +
        // PrintUtil.fromPPIToCM(width) + "; height = " + PrintUtil.fromPPIToCM(height));
        g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
        // g2d.draw(new Rectangle2D.Double(1, 1, width - 1, height - 1));
        FontMetrics fm = g2d.getFontMetrics();

        int h = PrintUtil.fromCMToPPI_i(0.7);
        if (StringUtils.isNotEmpty(printInfo.receiverWWID)) {
            g2d.drawString(printInfo.receiverWWID, PrintUtil.fromCMToPPI_i(21), h);
        }
        
        h = PrintUtil.fromCMToPPI_i(2.1);
        List<String> dateList = PrintUtil.parseDateToString();
        // TODO adjust intr and startPos
        int idx = 0;double intr=0.1;double startPos = 5.35;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        g2d.drawString(dateList.get(idx), PrintUtil.fromCMToPPI_i(startPos + idx * intr), h);
        idx++;
        
        h = PrintUtil.fromCMToPPI_i(4.6);
        g2d.drawString(printInfo.receiverName, PrintUtil.fromCMToPPI_i(13), h);
        h += fm.getHeight() + PrintUtil.fromCMToPPI_i(0.2);
        g2d.drawString(printInfo.receiverAddress1, PrintUtil.fromCMToPPI_i(11.2), h);
        h += fm.getHeight() + PrintUtil.fromCMToPPI_i(0.2);
        g2d.drawString(printInfo.receiverAddress2, PrintUtil.fromCMToPPI_i(11.2), h);
        if (StringUtils.isNotEmpty(printInfo.receiverAddress3)) {
            h += fm.getHeight() + PrintUtil.fromCMToPPI_i(0.2);
            g2d.drawString(printInfo.receiverAddress3, PrintUtil.fromCMToPPI_i(11.2), h);
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
    }}
