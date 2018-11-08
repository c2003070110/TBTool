package com.walk_nie.taobao.akb48.sousenkyo;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.io.Files;
import com.walk_nie.util.NieUtil;

public class MassSender {

	private String senderFilePath = "./shipment/sender.txt";

	public static void main(String[] args) throws IOException, AWTException, InterruptedException {

		MassSender sender = new MassSender();
		// sender.testPointer();
		sender.send();
	}

	private void testPointer() {

		int x = 0, y = 0;
		while (true) {
			PointerInfo a = MouseInfo.getPointerInfo();
			if (x != (int) a.getLocation().getX()
					&& y != (int) a.getLocation().getY()) {
				System.out.println("(X,Y)" + x + "," + y);
				x = (int) a.getLocation().getX();
				y = (int) a.getLocation().getY();
			}
		}
	}

	protected void send() throws IOException, AWTException, InterruptedException {
		int x1 = 45;
		int y1 = 102;
		int x2 = 435;
		int y2 = 789;

		File tempFile = new File(senderFilePath);
		List<String> list = Files.readLines(tempFile, Charset.forName("UTF-8"));

		Robot robot = new Robot();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clip = kit.getSystemClipboard();
		String sendTxt ="亲 AKB48 2017投票快要截止了。请赶紧投票。\n";
		sendTxt +="已经投票的亲，请确认收货和好评哦。\n";
		sendTxt +="没有收到票码的亲，请速速与客服联系。\n";
		sendTxt +="！！票码作废不退款！！\n";

		for (String senderName : list) {
			StringSelection ss = new StringSelection(senderName);
			clip.setContents(ss, ss);
			
			robot.mouseMove(x1, y1);
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_V);
			NieUtil.mySleepBySecond(2);
			//mysleep(2);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			//mysleep(2);
			NieUtil.mySleepBySecond(1);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			NieUtil.mySleepBySecond(1);
			//mysleep(1);
			
			robot.mouseMove(x2, y2);
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);

			ss = new StringSelection(sendTxt);
			clip.setContents(ss, ss);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_V);
			 
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			NieUtil.mySleepBySecond(2);
		}

	}
	protected void mysleep(double second) throws IOException {
		
		long now = System.currentTimeMillis();
		long millis = (long)second * 1000 + now;
		while(true){
			if(millis < System.currentTimeMillis())break;
		}
		
	}

}
