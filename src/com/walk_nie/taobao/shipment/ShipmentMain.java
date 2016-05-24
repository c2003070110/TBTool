package com.walk_nie.taobao.shipment;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.TaobaoSaledObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class ShipmentMain  {

    public static BufferedReader stdReader = null;
 
    private File picSrcFolder = null;
    private String toShipmengFile = ".shipment/toShipment.txt";

    public static void main(String[] args)   {
        new ShipmentMain().execute();
    }

    protected void execute()  {
        
        try {
            init();
            
            List<TaobaoSaledObject> toShipmentList = getTobeShipmentInfo();
            if(!rename(toShipmentList)) return;
            if(!sendMail(toShipmentList)) return;
            if(!shipment(toShipmentList)) return;
//            
//            System.out.print("Type of Operation : ");
//            System.out.println("0:rename;1:send mail;2:shipmeng;");
//
//            while (true) {
//                String line = stdReader.readLine().trim();
//                if ("0".equals(line)) {
//                    shipment();
//                    break;
//                } else if ("1".equals(line)) {
//                    rename();
//                    break;
//                } else if ("2".equals(line)) {
//                    sendMail();
//                    break;
//                } else {
//                    System.out.println("Listed number only!");
//                }
//            }
            stdReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
       
        stdReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Folder for rename : ");
        while (true) {
            String line = stdReader.readLine().trim();
            picSrcFolder = new File(line);

            if (!picSrcFolder.exists()) {
                System.out.print("[ERROR]Folder NOT exists. " + picSrcFolder.getCanonicalPath());
                System.out.print("type in again.");
                continue;
            }
            if (!picSrcFolder.isDirectory()) {
                System.out.print("[ERROR] is NOT Folder " + picSrcFolder.getCanonicalPath());
                System.out.print("type in again.");
                continue;
            }
            break;
        }
         
//        System.out.print("file path for shipment(*.csv) : ");
//        while (true) {
//            String line = stdReader.readLine().trim();
//            saledInfoFile = new File(line);
//
//            if (!saledInfoFile.exists()) {
//                System.out.print("[ERROR]Folder NOT exists. " + saledInfoFile.getCanonicalPath());
//                System.out.print("type in again.");
//                continue;
//            }
//            if (!saledInfoFile.isFile()) {
//                System.out.print("[ERROR] is NOT File " + saledInfoFile.getCanonicalPath());
//                System.out.print("type in again.");
//                continue;
//            }
//            break;
//        }

    }

    private boolean sendMail(List<TaobaoSaledObject> toShipmentList) throws IOException {
        boolean rslt = true;
        System.out.println("[info]mail sending...");
        for (TaobaoSaledObject saledObj : toShipmentList) {
            List<String> toMailAdd = Lists.newArrayList();
            if(saledObj.buyerZhifubaoId.indexOf("@") != -1){
                toMailAdd.add(saledObj.buyerZhifubaoId);
            }
            if(saledObj.buyerNote.indexOf("@") != -1){
                toMailAdd.add(saledObj.buyerNote);
            }
            List<File> attachFiles = findSendFiles(saledObj);
            if(attachFiles == null || attachFiles.isEmpty()){
                System.out.print("[ERROR]not attachment file for send mail");
                return false;
            }
            sendMail(saledObj,toMailAdd,attachFiles);
        }
        return rslt;
        
    }
    private void sendMail(TaobaoSaledObject saledObj, List<String> toMailAdd, List<File> attachFiles) {
        // TODO Recipient's email ID needs to be mentioned.
        String to = "abcd@gmail.com";

        // TODO Sender's email ID needs to be mentioned
        String from = "web@gmail.com";

        // TODO Assuming you are sending email from localhost
        String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try{
           // Create a default MimeMessage object.
           MimeMessage message = new MimeMessage(session);

           // Set From: header field of the header.
           message.setFrom(new InternetAddress(from));

           // Set To: header field of the header.
            for (String to1 : toMailAdd) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to1));
            }
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

           // TODO Set Subject: header field
           message.setSubject("This is the Subject Line!");

           // Create the message part 
           BodyPart messageBodyPart = new MimeBodyPart();

           // TODO Fill the message
           messageBodyPart.setText("");
           
           // Create a multipar message
           Multipart multipart = new MimeMultipart();

           // Set text message part
           multipart.addBodyPart(messageBodyPart);

           // Part two is attachment
           for(File attachFile:attachFiles){
               messageBodyPart = new MimeBodyPart();
               DataSource source = new FileDataSource(attachFile);
               messageBodyPart.setDataHandler(new DataHandler(source));
               messageBodyPart.setFileName(attachFile.getName());
               multipart.addBodyPart(messageBodyPart);
           }

           // Send the complete message parts
           message.setContent(multipart );

           // Send message
           Transport.send(message);
        }catch (MessagingException mex) {
            mex.printStackTrace();
         }
    }

    private List<File> findSendFiles(TaobaoSaledObject saledObj) throws IOException {
        List<File> rtnFiles = Lists.newArrayList();
        File[] files = picSrcFolder.listFiles();
        for (File file : files) {
            if (file.isDirectory())
                continue;
            String fileName = file.getName();
            if (fileName.startsWith(saledObj.orderNo)) {
                rtnFiles.add(file);
            }
        }
        return rtnFiles;
    }

    private List<TaobaoSaledObject> getTobeShipmentInfo() throws IOException{
        File saledInfoFile = new File(toShipmengFile);
        List<String> list = FileUtils.readLines(saledInfoFile, "UTF-8");
        List<TaobaoSaledObject> objList = Lists.newArrayList();
        for (String str : list) {
            if (str.equals("")) continue;
            if (str.startsWith("#")) continue;
            TaobaoSaledObject saledObj = TaobaoUtil.readTaobaoSaledIn(str);
            if(saledObj == null)  continue;
            if(!saledObj.orderStatus.equals("买家已付款，等待卖家发货")) continue;
//            String ttl = saledObj.baobeiTitle;
//            if(!(ttl.equals("1") 
//                    && ttl.equals("2") 
//                    && ttl.equals("3"))) 
//                continue;
            objList.add(saledObj);
        }
        return objList;
    }

    private boolean rename(List<TaobaoSaledObject> toShipmentList) throws IOException {
        System.out.println("[INFO]rename...");
        List<String> fileNameList = Lists.newArrayList();
        for (TaobaoSaledObject saledObj : toShipmentList) {
            int cnt = saledObj.baobeiNum;
            if (cnt == 1) {
                fileNameList.add(String.format("%s_%s", saledObj.orderNo, saledObj.buyerId));
            } else {
                for (int i = 0; i < cnt; i++) {
                    fileNameList.add(String.format("%s_%s_%d", saledObj.orderNo, saledObj.buyerId,
                            i));
                }
            }
        }
        List<File> srcFileList = Lists.newArrayList();
        File[] files = picSrcFolder.listFiles();
        for (File file : files) {
            if(!file.isFile()) continue;
            srcFileList.add(file);
        }
        if(srcFileList.size() < fileNameList.size()){
            System.out.print("[ERROR]rename is FAILURE;size over " +"count of JPG:" +srcFileList.size()+";count of order" + fileNameList.size() );
            return false;
        }
        for (int i=0;i<fileNameList.size();i++) {
            String newFileName = fileNameList.get(i);
            File file = srcFileList.get(i);
            File parentFile = file.getParentFile();
            int idx = file.getName().indexOf(".");
            String fileName = file.getName().substring(0,idx);
            String extension = file.getName().substring(idx+1);
            System.out.println("[INFO]rename " + fileName +"." + extension + " to " + newFileName+ "." + extension);
            file.renameTo(new File(parentFile,newFileName +"." + extension));
        }
        return true;
    }

    private boolean shipment(List<TaobaoSaledObject> toShipmentList) throws IOException {
        boolean rslt = true;
        System.out.println("[info]shipment...");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Clipboard clip = kit.getSystemClipboard();
        for (TaobaoSaledObject saledObj : toShipmentList) {
            System.out.println("shipmenting for buyer:" + saledObj.buyerId);
            
            StringSelection ss = new StringSelection(saledObj.buyerId);
            clip.setContents(ss, ss);
            
            while(true){
                System.out.print("goto next buyer? : 0:yes;1:no;");
                String line = stdReader.readLine().trim();
                if(line.equals("0")){
                    break;
                }
            }
        }
        return rslt;
    }
 
}
