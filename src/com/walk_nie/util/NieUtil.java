package com.walk_nie.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NieUtil {

    public static String readLineFromSystemIn(String hint) {
        System.out.println(hint);
        BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
			while ((line = rd.readLine()) == null) {
			    System.out.println("Please type the valid value again!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return line;
    }

	public static void mySleepBySecond(int i) {
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
		}
	}

}