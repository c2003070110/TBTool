package com.walk_nie.taobao.akb48.sousenkyo;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.object.TaobaoSaledObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class ShipmentMain  {

    public static BufferedReader stdReader = null;

    private String picSrcPath = "E:\\temp\\akb48";
    private File picSrcFolder = new File(picSrcPath);
    private String toShipmentFile = "C:\\Users\\niehp\\Downloads\\ExportOrderList201606132128.csv";
    //private String toShipmentFile = "./shipment/toShipment.txt";
    private String shipmentedFile = "./shipment/shipmented.txt";
    private String shipmentingFile = "./shipment/shipmenting.txt";

	public static void main(String[] args) throws IOException {

		ShipmentMain main = new ShipmentMain();

		// main.preOperate();
		//main.countSales();
		 main.remind();

		// main.assign();
		// main.addTextToPicture();
		// main.shipment();
	}
    protected  void addTextToPicture() throws IOException {
    	File fol = picSrcFolder;
    	File[] files = fol.listFiles();
        for (File file : files) {
            if(!file.isFile()) continue;
            int idx = file.getName().indexOf(".");
            String name = file.getName();
            String fileName = name.substring(0,idx);
            BufferedImage image = ImageIO.read(file);
            Graphics g = image.getGraphics();
            Font font = g.getFont();
            font.deriveFont(50f);
            
//            g.setFont(font);
//            FontMetrics metrics = g.getFontMetrics();
//            int width = metrics.stringWidth( fileName );
//            int height = metrics.getHeight();
//            
//            g.setColor(Color.WHITE);
//            g.fillRect(50, 50, 50 + width + 10, 50 + height + 10);
            g.drawString(fileName, 20, 30);
            g.dispose();

            ImageIO.write(image, "jpg", new File(picSrcPath + "\\s\\" +name));

        }
    }
    protected  void preOperate() throws IOException {
    	File fol = picSrcFolder;
    	int cnt = 0;
    	File[] files = fol.listFiles();
        for (File file : files) {
            if(!file.isFile()) continue;
            File parentFile = file.getParentFile();
            int idx = file.getName().indexOf(".");
            String fileName = file.getName().substring(0,idx);
            String newFileName = fileName.replaceAll(" ", "");
            //newFileName = fileName.replaceAll("IMG_", "img-");
            
            newFileName = "lovely71751006-" + (cnt++);
            String extension = file.getName().substring(idx+1);
            System.out.println("[INFO]rename " + fileName +"." + extension + " to " + newFileName+ "." + extension);
            file.renameTo(new File(parentFile,newFileName +"." + extension));
        }
    }
    protected  List<String> getShipmentedOrderNos() throws IOException {
    	String file = "C:\\Users\\niehp\\Google ドライブ\\taobao-niehtjp\\akb48\\akb48 8th\\finish";
        File rootFolder = new File(file);
        File[] files1 =rootFolder.listFiles();
        List<String> list = Lists.newArrayList();
        for(File tmp1:files1){
        	if(tmp1.isFile()){
        		list.add(tmp1.getName());
        	}
        	if(tmp1.isDirectory()){
        		File[] files2 = tmp1.listFiles();
                for(File tmp2:files2){
                	if(tmp2.isFile()){
                		list.add(tmp2.getName());
                	}
                }
        	}
        }
        Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
        	
        });
        List<String> orderNos = Lists.newArrayList();
        for(String fileName:list){
        	String ff = fileName.substring(0,fileName.indexOf("_"));
        	String[] sl = ff.split("-");
        	//System.out.println(sl[0]);
        	if(!orderNos.contains(sl[0]))
        		orderNos.add(sl[0]);
        }
        return orderNos;
    	
    }

    protected void remind() throws IOException {
        File saledInfoFile = new File(toShipmentFile);
        List<String> list = FileUtils.readLines(saledInfoFile, "GB2312");
        List<TaobaoSaledObject> objList = Lists.newArrayList();
        for (String str : list) {
            if (str.equals("")) continue;
            if (str.startsWith("#")) continue;
            if (str.startsWith("\"订单编号")) continue;
            TaobaoSaledObject saledObj = TaobaoUtil.readTaobaoSaledIn(str);
            if(saledObj == null)  continue;
            if(saledObj.orderStatus.equals("等待买家付款")) continue;
            if(saledObj.orderStatus.equals("交易关闭")) continue;
            String ttl = saledObj.baobeiTitle;
            if(ttl.indexOf("总选投票券") == -1) continue;
            objList.add(saledObj);
        } 
        Collections.sort(objList, new Comparator<TaobaoSaledObject>() {

            @Override
            public int compare(TaobaoSaledObject o1, TaobaoSaledObject o2) {
            	// 2016-05-28 21:49:45
            	try {
					Date time1 = DateUtils.parseDate(o1.orderCreatedDateTime, "yyyy-MM-dd hh:mm:ss");
					Date time2 = DateUtils.parseDate(o2.orderCreatedDateTime, "yyyy-MM-dd hh:mm:ss");
	                return time1.compareTo(time2);
				} catch (ParseException e) {
					e.printStackTrace();
				} 
            	return 0;
            }
        });
        List<String> buyerIdList = Lists.newArrayList();
        for (TaobaoSaledObject obj : objList) {
        	if(obj.orderStatus.equals("卖家已发货，等待买家确认")){
        		String key = obj.buyerId + "\t" + obj.orderCreatedDateTime;
        		if(!buyerIdList.contains(key)){
        			buyerIdList.add(key);
        		}
        	}
        }
		for (String buyerId : buyerIdList) {
	        System.out.println("买家Id= " +buyerId);
		}
    
	}

    protected void countSales() throws IOException {
        File saledInfoFile = new File(toShipmentFile);
        List<String> list = FileUtils.readLines(saledInfoFile, "GB2312");
        List<TaobaoSaledObject> objList = Lists.newArrayList();
        TaobaoSaledObject specialObj = null;
        for (String str : list) {
            if (str.equals("")) continue;
            if (str.startsWith("#")) continue;
            if (str.startsWith("\"订单编号")) continue;
            TaobaoSaledObject saledObj = TaobaoUtil.readTaobaoSaledIn(str);
            if(saledObj == null)  continue;
            if(saledObj.orderStatus.equals("等待买家付款")) continue;
            if(saledObj.orderStatus.equals("交易关闭")) continue;
            String ttl = saledObj.baobeiTitle;
            if(ttl.indexOf("总选投票券") == -1) continue;
            objList.add(saledObj);
            if(saledObj.orderNo.equals("1968817297701405"))  specialObj =saledObj;
        } 
        List<String> titleList = Lists.newArrayList();
        List<String> realShipmentOrders = Lists.newArrayList();
        int ttl = 0;
        int ttl1 = 0;
        int ttl2 = 0;
        int ttl3 = 0;
        for (TaobaoSaledObject obj : objList) {
        	ttl += obj.baobeiNum;
        	if(obj.orderStatus.equals("买家已付款，等待卖家发货")){
        		ttl1+=obj.baobeiNum;
        	}
        	if(obj.orderStatus.equals("卖家已发货，等待买家确认")){
        		ttl2+=obj.baobeiNum;
        		//if(!realShipmentOrders.contains(obj.orderNo)){
        		//	realShipmentOrders.add(obj.orderNo);
        		//}
        		for(int i=0;i<obj.baobeiNum;i++){
        			realShipmentOrders.add(obj.orderNo);
        		}
        	}
        	if(obj.orderStatus.equals("交易成功")){
        		ttl3+=obj.baobeiNum;
        		for(int i=0;i<obj.baobeiNum;i++){
        			realShipmentOrders.add(obj.orderNo);
        		}
        	}
			if (!titleList.contains(obj.baobeiTitle)) {
				titleList.add(obj.baobeiTitle);
			}
        }
        System.out.println("总卖出= " +ttl +" 等待卖家发货= " + ttl1+ " 等待买家确认= " + ttl2 + " 交易成功= " + ttl3);
		for (String ordor : titleList) {
			System.out.println(ordor);
		}

        Collections.sort(realShipmentOrders, new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
        	
        });
        //for(String ordor : realShipmentOrders){
        	//System.out.println(ordor);
        //}
//        List<String> shipmentedOrders = getShipmentedOrderNos();
//        for(String ordor : shipmentedOrders){
//        	if(!realShipmentOrders.contains(ordor)){
//        		System.out.println("do NOT shipmented order no:" + ordor);
//        	}
//        }
    
	}

    protected void init() throws IOException {
        
            if (!picSrcFolder.exists()) {
                System.out.print("[ERROR]Folder NOT exists. " + picSrcFolder.getCanonicalPath());
                System.out.print("type in again.");
            }
            if (!picSrcFolder.isDirectory()) {
                System.out.print("[ERROR] is NOT Folder " + picSrcFolder.getCanonicalPath());
                System.out.print("type in again.");
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
    protected List<TaobaoSaledObject> getTobeShipmentInfo1() throws IOException{
    	String file = "E:\\temp\\orders.txt";
        File saledInfoFile = new File(file);
        List<String> list = FileUtils.readLines(saledInfoFile, "UTF-8");
        List<TaobaoSaledObject> objList = Lists.newArrayList();
        for (String str : list) {
        	String[] sts = str.split("\t");
        	TaobaoSaledObject obj = new TaobaoSaledObject();
        	obj.orderNo = sts[0].trim();
        	obj.buyerId = sts[1].trim();
        	obj.baobeiNum = Integer.parseInt(sts[2].trim());
        	objList.add(obj);
        }
        return objList;
    }

    protected List<TaobaoSaledObject> getTobeShipmentInfo() throws IOException{
        File saledInfoFile = new File(toShipmentFile);
        List<String> list = FileUtils.readLines(saledInfoFile, "GB2312");
        List<TaobaoSaledObject> objList = Lists.newArrayList();
        List<String> shipmentedOrderNos = readShipmentedInfoList();
        for (String str : list) {
            if (str.equals("")) continue;
            if (str.startsWith("#")) continue;
            if (str.startsWith("\"订单编号")) continue;
            TaobaoSaledObject saledObj = TaobaoUtil.readTaobaoSaledIn(str);
            if(saledObj == null)  continue;
            if(!saledObj.orderStatus.equals("买家已付款，等待卖家发货")) continue;
            if(shipmentedOrderNos.contains(saledObj.orderNo)) continue;
            String ttl = saledObj.baobeiTitle;
            if(ttl.indexOf("总选投票券") == -1) continue;
            System.out.println("title:status " + ttl +":"+ saledObj.orderStatus);
            objList.add(saledObj);
        }
        Collections.sort(objList, new Comparator<TaobaoSaledObject>() {

            @Override
            public int compare(TaobaoSaledObject o1, TaobaoSaledObject o2) {
            	// 2016-05-28 21:49:45
            	try {
					Date time1 = DateUtils.parseDate(o1.orderCreatedDateTime, "yyyy-MM-dd hh:mm:ss");
					Date time2 = DateUtils.parseDate(o2.orderCreatedDateTime, "yyyy-MM-dd hh:mm:ss");
	                return time1.compareTo(time2);
				} catch (ParseException e) {
					e.printStackTrace();
				} 
            	return 0;
            }
        });
//        List<TaobaoSaledObject> objList1 = Lists.newArrayList();
//		Date today = new Date(System.currentTimeMillis());
//		Date baseDate = DateUtils.addDays(today, -2);
//        for (TaobaoSaledObject obj : objList) {
//        	try {
//				Date time1 = DateUtils.parseDate(obj.orderCreatedDateTime, "yyyy-MM-dd hh:mm:ss");
//				if(time1.after(baseDate)){
//					objList1.add(obj);
//				}
//				
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }
        int ttl = 0;
        for (TaobaoSaledObject obj : objList) {
        	ttl += obj.baobeiNum;
        }
        System.out.println("total count for rename = " +ttl);
        return objList;
    }

    protected void assign() throws IOException {
        System.out.println("[INFO]rename...");
        init();
        List<TaobaoSaledObject> toShipmentList = getTobeShipmentInfo();
        List<String> fileNameList = Lists.newArrayList();
        for (TaobaoSaledObject saledObj : toShipmentList) {
            int cnt = saledObj.baobeiNum;
            if (cnt == 1) {
                fileNameList.add(String.format("%s-%s", saledObj.orderNo, saledObj.buyerId));
            } else {
                for (int i = 0; i < cnt; i++) {
                    fileNameList.add(String.format("%s-%s-%d", saledObj.orderNo, saledObj.buyerId,
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
            return ;
        }
        for (int i=0;i<fileNameList.size();i++) {
            File file = srcFileList.get(i);
            File parentFile = file.getParentFile();
            int idx = file.getName().indexOf(".");
            String fileName = file.getName().substring(0,idx);
            String extension = file.getName().substring(idx+1);
            String newFileName = fileNameList.get(i) + "_" + fileName;
            System.out.println("[INFO]rename " + fileName +"." + extension + " to " + newFileName+ "." + extension);
            file.renameTo(new File(parentFile,newFileName +"." + extension));
        }
        
        File shipmenting = new File(shipmentingFile);
        StringBuffer sb = new StringBuffer();
        for (TaobaoSaledObject saledObj : toShipmentList) {
        	String line = String.format("%s\t%s\t%d",saledObj.orderNo,saledObj.buyerId,saledObj.baobeiNum);
        	sb.append(line+ "\r\n");
        }
        Files.write(sb, shipmenting, Charset.forName("UTF-8"));
        
    }

    protected void shipment() throws IOException {
        List<TaobaoSaledObject> toShipmentList = Lists.newArrayList();
        System.out.println("[info]shipment...");
        File tempFile = new File(shipmentingFile);
        List<String> shipmentings = Files.readLines(tempFile, Charset.forName("UTF-8"));
        for (String str : shipmentings) {
        	TaobaoSaledObject obj = new TaobaoSaledObject();
        	String[] sl = str.split("\t");
        	obj.orderNo = sl[0];
        	obj.buyerId = sl[1];
        	obj.baobeiNum = Integer.parseInt(sl[2]);
        	toShipmentList.add(obj);
        }
        
        Toolkit kit = Toolkit.getDefaultToolkit();
        Clipboard clip = kit.getSystemClipboard();
        int ttl = toShipmentList.size();
        int ii = 0;
        for (TaobaoSaledObject saledObj : toShipmentList) {
            System.out.println("shipmenting for buyer:" + saledObj.buyerId + " count: " + saledObj.baobeiNum);
            
            StringSelection ss = new StringSelection(saledObj.buyerId);
            clip.setContents(ss, ss);
            saveShipmentedOrderNos(saledObj);
            while(true){
                System.out.print("goto next buyer? : 0:yes;1:no;(size for left:" + (ttl-(ii++)) + ")");
                
                String line = getStdReader().readLine().trim();
                if(line.equals("0")){
                    break;
                }
            }
        }
        tempFile.deleteOnExit();
    }

    public  BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}

	public void saveShipmentedOrderNos(TaobaoSaledObject obj)
			throws IOException {
		File file = new File(shipmentedFile);
		StringBuffer sb = new StringBuffer();
		String orderNo = obj.orderNo;
		if (StringUtils.isEmpty(orderNo)) {
			orderNo = "";
		}
		sb.append(orderNo);
		sb.append("\r\n");
		if (!file.exists()) {
			Files.write(sb.toString(), file, Charset.forName("UTF-8"));
		} else {
			Files.append(sb.toString(), file, Charset.forName("UTF-8"));
		}
	}
    public  List<String> readShipmentedInfoList() throws IOException {
		File file = new File(shipmentedFile);
        if (!file.exists())
            return Lists.newArrayList();
        List<String> lines = Files.readLines(file, Charset.forName("UTF-8"));
      
        return lines;
    }


    protected boolean sendMail(List<TaobaoSaledObject> toShipmentList) throws IOException {
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
    protected void sendMail(TaobaoSaledObject saledObj, List<String> toMailAdd, List<File> attachFiles) {
        // TODO Recipient's email ID needs to be mentioned.
        String to = "niehaiping@gmail.com";

        // TODO Sender's email ID needs to be mentioned
        String from = "niehpjp@163.com";

        // TODO Assuming you are sending email from localhost
        String host = "smtp.163.com";

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

    protected List<File> findSendFiles(TaobaoSaledObject saledObj) throws IOException {
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
}
