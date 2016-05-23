package com.walk_nie.taobao.print;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;

public class PrintableMain implements Printable {

    public static void main(String[] args) throws PrinterException, IOException {
        new PrintableMain().print();
    }

    protected void print() throws PrinterException, IOException {
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

        /* locate a print service that can handle the request */
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        System.out.println("selected printer " + service.getName());
        
        /* create a print job for the chosen service */
        DocPrintJob pj = service.createPrintJob();
        try {
            DocAttributeSet docSet = new HashDocAttributeSet();
            Attribute a1 = new MediaPrintableArea(0,0,270,150,MediaPrintableArea.MM);
            docSet.add(a1);
            /*
             * Create a Doc object to hold the print data.
             */
            Doc doc = new SimpleDoc(this, flavor, docSet);
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            //aset.add(new MediaPrintableArea(0,0,270,150,MediaPrintableArea.MM));
            
            /* print the doc as specified */
            pj.print(doc, aset);

            /*
             * Do not explicitly call System.exit() when print returns.
             * Printing can be asynchronous so may be executing in a
             * separate thread.
             * If you want to explicitly exit the VM, use a print job
             * listener to be notified when it is safe to do so.
             */

        } catch (PrintException e) {
            System.err.println(e);
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex < 5) {
            Graphics2D g2d = (Graphics2D) graphics;
            
            double width = pf.getImageableWidth();
            double height = pf.getImageableHeight();
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            
            g2d.draw(new Rectangle2D.Double(1, 1, width - 2, height - 2));
            
            g2d.setColor(Color.black);
            g2d.drawString("example string" + pageIndex, 250, 250);
            g2d.fillRect(0, 0, 200, 200);
            return Printable.PAGE_EXISTS;
        } else {
            return Printable.NO_SUCH_PAGE;
        }
    }
}
