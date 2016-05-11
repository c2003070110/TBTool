package com.walk_nie.taobao.print;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;
import java.util.Locale;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

import com.beust.jcommander.internal.Lists;


public class PrintMain {

    public static void main(String[] args) {
        //listMediaSize();
        printEMS1();
        //print2();
    }

    protected static void print2() {
        //印刷データの提供形式
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        //印刷要求属性
        PrintRequestAttributeSet requestAttributeSet = new HashPrintRequestAttributeSet();
        //印刷ダイアログでの出力先一覧
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, requestAttributeSet);
        //既定で選択される出力先
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        //ここから　属性指定
        //requestAttributeSet.add(new PageRanges("1-1"));    // 印字範囲  
        requestAttributeSet.add(OrientationRequested.PORTRAIT); // 用紙の向き
       
        requestAttributeSet.add(MediaSizeName.ISO_A7);               //用紙A4

        // 余白（mm）をもとに印字可能領域を設定する 
        float leftMargin = 20; 
        float rightMargin = 20;
        float topMargin = 20;
        float bottomMargin = 1;
        MediaSize mediaSize = MediaSize.ISO.A7;
        float mediaWidth = mediaSize.getX(Size2DSyntax.MM);
        float mediaHeight = mediaSize.getY(Size2DSyntax.MM);
        requestAttributeSet.add(new MediaPrintableArea(leftMargin, topMargin,
            (mediaWidth - leftMargin - rightMargin),(mediaHeight - topMargin - bottomMargin), Size2DSyntax.MM));
       
        requestAttributeSet.add(new JobName("hogehoge", Locale.getDefault())); //ジョブ名
        // ここまで印字属性

        //印刷ダイアログを表示して選択した出力先を得る　予め指定した属性でダイアログが表示された
        PrintService service = ServiceUI.printDialog(null, 100, 100, services, defaultService, flavor, requestAttributeSet);

        if (service != null){
            DocPrintJob job = service.createPrintJob(); //印刷ジョブの生成
            EMS1Printable clsPrintable = new EMS1Printable(getPrintInfoList());
            SimpleDoc doc = new SimpleDoc(clsPrintable, flavor, null);
            //ジョブに印刷を依頼する
            try {
                job.print(doc, requestAttributeSet);
            } catch (PrintException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected static void  printEMS1(){
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (pj.printDialog()) {
            
            PageFormat pf = pj.defaultPage();
            Paper paper = pf.getPaper();   
            double margin = 0; 
            paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight()
                    - margin * 2);
            pf.setPaper(paper);
            //System.out.println("PageFormat-" + ": width = " + pf.getWidth() + "; height = " + pf.getHeight());
            //System.out.println("paper-" + ": width = " + paper.getWidth() + "; height = " + paper.getHeight());
            pj.setPrintable(new EMS1Printable(getPrintInfoList()), pf);
            try {
                pj.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    protected static List<PrintInfoObject> getPrintInfoList(){
        List<PrintInfoObject> toPrintList = Lists.newArrayList();
        for(int i=0;i<10;i++){
            PrintInfoObject obj = new 
                    PrintInfoObject();
            obj.receiverCountry="CHINA";
            obj.receiverName="名称" + i;
            obj.receiverAddress1="住所１－" + i;
            obj.receiverAddress2="住所２－" + i;
            obj.receiverZipCode="";
            obj.receiverTel="０８０－１２３４－５６７８";
            toPrintList.add(obj);
        }
        return toPrintList;
    }
    protected static void listMediaSize(){
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (pj.printDialog()) {
        PrintService printService = pj.getPrintService();
      
        Media[] res = (Media[]) printService.getSupportedAttributeValues(Media.class, null, null);
        for (Media media : res) {
            if (media instanceof MediaSizeName) {
                MediaSizeName msn = (MediaSizeName) media;
                MediaSize ms = MediaSize.getMediaSizeForName(msn);
                float width = ms.getX(MediaSize.MM);
                float height = ms.getY(MediaSize.MM);
                System.out.println(media + ": width = " + width + "; height = " + height);
            }else{
                System.out.println(media  );
            }
        }
        }
    }
}
