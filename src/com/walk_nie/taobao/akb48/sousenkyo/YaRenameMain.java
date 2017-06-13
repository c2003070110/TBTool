package com.walk_nie.taobao.akb48.sousenkyo;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

public class YaRenameMain  {

    public static BufferedReader stdReader = null;

	public static void main(String[] args) throws IOException {
		YaRenameMain main = new YaRenameMain();
		//main.rename();
		main.rename2();
	}

	protected void rename2() throws IOException {
		File srcFolder = srcFolderReadin();
		File[] files = srcFolder.listFiles();
		for (File file : files) {
			if (!file.isDirectory())
				continue;
			File[] chldFiles = file.listFiles();
			String fmt = "%03d";
			String fName = file.getName();
			String[] split = fName.split("-");
			String yaid = split[1];
			int cnt = 1;
			for (File jpgF : chldFiles) {
				File parentFile = jpgF.getParentFile();
				int idx = jpgF.getName().indexOf(".");
				String extension = jpgF.getName().substring(idx + 1);
				String newFileName = yaid + "-" + String.format(fmt, cnt);
				jpgF.renameTo(new File(parentFile, newFileName + "."
						+ extension.toLowerCase()));
				cnt++;
			}
		}
	}
    protected  void rename() throws IOException {
    	File srcFolder = srcFolderReadin();
    	String prefix = prefixReadin();
    	int cnt = 0;
    	File[] files = srcFolder.listFiles();
        for (File file : files) {
            if(!file.isFile()) continue;
            File parentFile = file.getParentFile();
            int idx = file.getName().indexOf(".");
            String fileName = file.getName().substring(0,idx);
            String newFileName = fileName.replaceAll(" ", "");
            
			//newFileName = prefix + "-" + (cnt++);
			newFileName = prefix + "-" + fileName;
            String extension = file.getName().substring(idx+1);
            System.out.println("[INFO]rename " + fileName +"." + extension + " to " + newFileName+ "." + extension);
            file.renameTo(new File(parentFile,newFileName +"." + extension));
        }
        //addTextToPicture(srcFolder);
    }
    private String prefixReadin() throws IOException {
    	String yaId = "";
    	String yaSellerId ="";
		while (true) {
			System.out.print("input the yahoo auction id :");
			String line = getStdReader().readLine().trim();
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				continue;
			}
			yaId = line;
			break;
		}
//		while (true) {
//			System.out.print("input the yahoo seller id :");
//			String line = getStdReader().readLine().trim();
//			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
//					|| "".equals(line)) {
//				continue;
//			}
//			yaSellerId = line.replaceAll("_", "");
//			yaSellerId = line.replaceAll("-", "");
//			break;
//		}
		return yaId + "_" + yaSellerId;
	}
	private File srcFolderReadin() throws IOException {
		while (true) {
			System.out.print("input the path:");
			String line = getStdReader().readLine().trim();
			File file = new File(line);
			if(!file.canWrite() || !file.exists() || !file.isDirectory()){
				System.out.println("Folder is invalid!!input again.");
				continue;
			}
			return file;
		}
	}
    protected  void addTextToPicture(File fol) throws IOException {
    	File[] files = fol.listFiles();
        for (File file : files) {
            if(!file.isFile()) continue;
            int idx = file.getName().indexOf(".");
            String name = file.getName();
            String fileName = name.substring(0,idx);
            BufferedImage image = ImageIO.read(file);
            Graphics g = image.getGraphics();
            Font font = g.getFont();
            font.deriveFont(80f);
            
            
            g.setFont(font);
            FontMetrics metrics = g.getFontMetrics();
            int width = metrics.stringWidth( fileName );
            int height = metrics.getHeight();
            
            g.setColor(Color.WHITE);
            g.fillRect(50, 50, 50 + width + 10, 50 + height + 10);
            g.drawString(fileName, 20, 30);
            g.dispose();

            ImageIO.write(image, "jpg", file);

        }
    }
    public  BufferedReader getStdReader() throws UnsupportedEncodingException {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in,"Shift-JIS"));
		}
		return stdReader;
	}
}
