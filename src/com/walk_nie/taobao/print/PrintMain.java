package com.walk_nie.taobao.print;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
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

import org.apache.commons.io.FileUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;


public class PrintMain {
    public static String rootPathName = "./print";
    public static String toPrintFileName = "toPrint.csv";
    public static String printedOrderNosFileName = "printedOrderNos.txt";
    public static void main(String[] args) throws PrinterException, IOException {
        //listMediaSize();
        printEMS1();
        //print2();
    }
    
    protected static void  printEMS1() throws PrinterException, IOException{
    	// EMS width = 27cm height=14cm
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (pj.printDialog()) {
            
            PageFormat pf = pj.defaultPage();
            Paper paper = pf.getPaper();   
            double margin = 0; 
            paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight()
                    - margin * 2);
            pf.setPaper(paper);
            //System.out.println("PageFormat-" + ": width = " + pf.getWidth() + "; height = " + pf.getHeight());
            System.out.println("paper-" + ": width = " + paper.getWidth() + "; height = " + paper.getHeight());
            System.out.println("ems-" + ": width = " + PrintUtil.fromCMToPPI(27) + "; height = " + PrintUtil.fromCMToPPI(14));
            
            List<PrintInfoObject> toPrintList = getPrintInfoList();
            pj.setPrintable(new EMS1Printable(toPrintList), pf);
            pj.print();
            
            savePrintedOrderNos(toPrintList);
        }
    }
    private static void savePrintedOrderNos(List<PrintInfoObject> printedList) throws IOException {
        File file = new File(rootPathName, printedOrderNosFileName);
        StringBuffer sb = new StringBuffer();
        for(PrintInfoObject obj:printedList){
            for(String orderNo:obj.orderNos){
                sb.append(orderNo).append("\n");
            }
        }
        if (!file.exists()) {
            Files.write(sb.toString(), file, Charset.forName("UTF-8"));
        }else{
            Files.append(sb.toString(), file, Charset.forName("UTF-8"));   
        }
    }

    protected static List<PrintInfoObject> getPrintInfoList1() throws IOException{
        List<PrintInfoObject> printList = Lists.newArrayList();
        File file = new File(rootPathName,toPrintFileName);
        List<String> list = FileUtils.readLines(file,"UTF-8");
        List<String> printedOrderNos = readPrintedOrderNos();
        for(String str:list){
            if(str.equals(""))continue;
            String[] splited = str.split(",");
            if(splited.length != 11)continue;

            String orderNo =splited[0];
            if(printedOrderNos.contains(orderNo))continue;
            
            PrintInfoObject obj = new PrintInfoObject();
            obj.senderName="";
            obj.senderAddress1="";
            obj.senderAddress2="";
            obj.senderZipCode="123-0845";
            obj.senderZipTel="080-4200-1314";
            
            obj.receiverCountry="CHINA";
            
            obj.orderNo =orderNo;
            obj.receiverWWID =splited[0];
            obj.receiverName =splited[0];
            obj.receiverTel =splited[0];
            setAddress(obj,splited[0]);
        }
        Collections.sort(printList,new Comparator<PrintInfoObject>(){
            @Override
            public int compare(PrintInfoObject o1, PrintInfoObject o2) {
                return o1.receiverName.compareTo(o2.receiverName);
            }
        });
        
        List<PrintInfoObject> toPrintList = Lists.newArrayList();
        String name ="";
        PrintInfoObject tempObj = null;
        for(PrintInfoObject obj:printList){
            if(name.equals(obj.receiverName)){
                tempObj.orderNos.add(obj.orderNo);
            }else{
                name = obj.receiverName;
                tempObj = obj;
                tempObj.orderNos.add(obj.orderNo);
                toPrintList.add(tempObj);
            }
        }
        
        return toPrintList;
    }
    
    protected static List<String> readPrintedOrderNos() throws IOException {
        File file = new File(rootPathName, printedOrderNosFileName);
        if (!file.exists()) return Lists.newArrayList();
        List<String> lines = Files.readLines(file, Charset.forName("UTF-8"));
        return lines;
    }

    protected static void setAddress(PrintInfoObject obj, String address) {
        String[] splied = address.split(" ");
        if(splied.length>3){
            obj.receiverAddress1=splied[0] + " " + splied[1] + " " + splied[2] ;
            String newAdd = "";
            for(int j=3;j<splied.length;j++){
                newAdd += splied[j]; 
            }
            if(newAdd.length()>10){
                obj.receiverAddress2=newAdd.substring(0,10);
                obj.receiverAddress3=newAdd.substring(11);
            }else{
                obj.receiverAddress2=newAdd;
            }
        }else{
            if(address.length()>10){
                obj.receiverAddress1=address.substring(0,10);
                String newAdd =address.substring(11);
                if(newAdd.length()>10){
                    obj.receiverAddress2=newAdd.substring(0,10);
                    obj.receiverAddress3=newAdd.substring(11);
                }else{
                    obj.receiverAddress2=newAdd;
                }
            }else{
                obj.receiverAddress1=address;
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
            obj.receiverZipCode="";
            obj.receiverTel="13681515191";
            obj.receiverWWID="ID_" + i;
            String address ="";
            setAddress(obj,address);
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
}
