package com.walk_nie.taobao.print;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DotPrinterTest {

    public static void main(String[] args) throws IOException {

        String bill = "your code";

        InputStream br = new ByteArrayInputStream(bill.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(br));
        String line;
        // if you use windows
        FileWriter out = new FileWriter("////IP Printer//printer name");
        // if you use linux you can try SMB:
        while ((line = in.readLine()) != null) {
            System.out.println("line" + line);
            out.write(line);
            out.write(0x0D);
            out.write('\n');
        }
        out.close();
        in.close();
    }

}
