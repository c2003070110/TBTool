package com.walk_nie.taobao.print;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;



public class LabelHelper {

    public static void setInfoForSALLabel(PageFormat pageFormat, Graphics2D g2d,int pageIndex,PrintInfoObject toPrintInfo) {
        // SAL label width = 27cm height=xx.xxcm
        // FIXME 
    	toPrintInfo.senderName ="";
    	toPrintInfo.senderAddress1 ="";
    	toPrintInfo.senderAddress2 ="";
    	toPrintInfo.senderAddress3 ="";
    	toPrintInfo.senderZipCode ="";
    	toPrintInfo.senderTel ="";
    	
        g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
        //g2d.draw(new Rectangle2D.Double(1, 1, width - 1, height - 1));
        FontMetrics fm = g2d.getFontMetrics();
        
        int h=PrintUtil.fromCMToPPI_i(2.42);
        double receiverLeft = 11.32;double senderLeft=2.7;

        g2d.drawString(toPrintInfo.receiverName, PrintUtil.fromCMToPPI_i(receiverLeft), h);
        
        h += fm.getHeight();
        if (StringUtils.isNotEmpty(toPrintInfo.senderName)) {
            g2d.drawString(toPrintInfo.senderName, PrintUtil.fromCMToPPI_i(senderLeft), h);
        }
        if (StringUtils.isNotEmpty(toPrintInfo.receiverAddress1)) {
            g2d.drawString(toPrintInfo.receiverAddress1, PrintUtil.fromCMToPPI_i(receiverLeft), h);
        }
        
        h += fm.getHeight();
        if (StringUtils.isNotEmpty(toPrintInfo.senderAddress1)) {
            g2d.drawString(toPrintInfo.senderAddress1, PrintUtil.fromCMToPPI_i(senderLeft), h);
        }
        if (StringUtils.isNotEmpty(toPrintInfo.receiverAddress2)) {
            g2d.drawString(toPrintInfo.receiverAddress2, PrintUtil.fromCMToPPI_i(receiverLeft), h);
        }
        
        h += fm.getHeight();
        if (StringUtils.isNotEmpty(toPrintInfo.senderAddress2)) {
            g2d.drawString(toPrintInfo.senderAddress2, PrintUtil.fromCMToPPI_i(senderLeft), h);
        }
        if (StringUtils.isNotEmpty(toPrintInfo.receiverAddress3)) {
            g2d.drawString(toPrintInfo.receiverAddress3, PrintUtil.fromCMToPPI_i(receiverLeft), h);
        }
        
        h += fm.getHeight();
        if (StringUtils.isNotEmpty(toPrintInfo.senderAddress3)) {
            g2d.drawString(toPrintInfo.senderAddress3, PrintUtil.fromCMToPPI_i(senderLeft), h);
        }
        if (StringUtils.isNotEmpty(toPrintInfo.receiverZipCode)) {
            g2d.drawString(toPrintInfo.receiverZipCode, PrintUtil.fromCMToPPI_i(receiverLeft), h);
        }
        if (StringUtils.isNotEmpty(toPrintInfo.receiverAddress1)) {
            g2d.drawString(toPrintInfo.receiverAddress1, PrintUtil.fromCMToPPI_i(14.58), h);
        }
        

        h=PrintUtil.fromCMToPPI_i(5.48);
        if (StringUtils.isNotEmpty(toPrintInfo.senderZipCode)) {
            g2d.drawString(toPrintInfo.senderZipCode, PrintUtil.fromCMToPPI_i(2.8), h);
        }
        if (StringUtils.isNotEmpty(toPrintInfo.senderTel)) {
            g2d.drawString(toPrintInfo.senderTel, PrintUtil.fromCMToPPI_i(7.2), h);
        }
        if (StringUtils.isNotEmpty(toPrintInfo.receiverTel)) {
            g2d.drawString(toPrintInfo.receiverTel, PrintUtil.fromCMToPPI_i(10.42), h);
        }
        if (StringUtils.isNotEmpty(toPrintInfo.receiverCountry)) {
            g2d.drawString(toPrintInfo.receiverCountry, PrintUtil.fromCMToPPI_i(14.64), h);
        }

        h=PrintUtil.fromCMToPPI_i(6.6);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(15.62), h);
        
        h=PrintUtil.fromCMToPPI_i(8.9);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(1.62), h);
        g2d.drawString("1", PrintUtil.fromCMToPPI_i(16.3), h);
        g2d.drawString("1", PrintUtil.fromCMToPPI_i(18.1), h);

        
        h = PrintUtil.fromCMToPPI_i(10.54);
        List<String> dateList = PrintUtil.parseDateToString();
        int idx = 0;double intr=0.34;double startPos = 10.48;
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
        g2d.drawString("邓祎", PrintUtil.fromCMToPPI_i(13.8), h);
        
        

        h=PrintUtil.fromCMToPPI_i(12.2);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(1.62), h);
        g2d.drawString("30", PrintUtil.fromCMToPPI_i(7.2), h);
        
        h=PrintUtil.fromCMToPPI_i(12.9);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(11.38), h);
        
    }

    public static void setInfoForEMSLabel(PageFormat pageFormat, Graphics2D g2d, int pageIndex,
            PrintInfoObject printInfo) {
        // EMS label width = 27cm height=15.32cm
       
        g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
        // g2d.draw(new Rectangle2D.Double(1, 1, width - 1, height - 1));
        FontMetrics fm = g2d.getFontMetrics();

        int h = PrintUtil.fromCMToPPI_i(0.7);
        if (StringUtils.isNotEmpty(printInfo.receiverWWID)) {
            g2d.drawString(printInfo.receiverWWID, PrintUtil.fromCMToPPI_i(21), h);
        }
        
        h = PrintUtil.fromCMToPPI_i(2.56);
        List<String> dateList = PrintUtil.parseDateToString();
        int idx = 0;double intr=0.52;double startPos = 6.72;
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
        h += fm.getHeight() + PrintUtil.fromCMToPPI_i(0.02);
        g2d.drawString(printInfo.receiverAddress1, PrintUtil.fromCMToPPI_i(11.2), h);
        h += fm.getHeight() + PrintUtil.fromCMToPPI_i(0.02);
        g2d.drawString(printInfo.receiverAddress2, PrintUtil.fromCMToPPI_i(11.2), h);
        if (StringUtils.isNotEmpty(printInfo.receiverAddress3)) {
            h += fm.getHeight() + PrintUtil.fromCMToPPI_i(0.02);
            g2d.drawString(printInfo.receiverAddress3, PrintUtil.fromCMToPPI_i(11.2), h);
        }

        h = PrintUtil.fromCMToPPI_i(8.12);
        g2d.drawString(printInfo.receiverCountry, PrintUtil.fromCMToPPI_i(11), h);
        g2d.drawString(printInfo.receiverTel, PrintUtil.fromCMToPPI_i(16.2), h);

        h = PrintUtil.fromCMToPPI_i(10.05);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(16.38), h);

        h = PrintUtil.fromCMToPPI_i(12.35);
        g2d.drawString("×", PrintUtil.fromCMToPPI_i(3.15), h);

        h = PrintUtil.fromCMToPPI_i(13.3);
        g2d.drawString("邓祎", PrintUtil.fromCMToPPI_i(3.4), h);
        g2d.drawString("1", PrintUtil.fromCMToPPI_i(8.8), h);
        g2d.drawString("1", PrintUtil.fromCMToPPI_i(9.9), h);
    }}
