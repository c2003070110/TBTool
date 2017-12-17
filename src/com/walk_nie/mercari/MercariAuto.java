package com.walk_nie.mercari;

import java.io.BufferedReader;
import java.io.IOException;

import org.openqa.selenium.WebDriver;

public class MercariAuto {

	protected BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		MercariAuto main = new MercariAuto();
		main.execute();
	}

	public void execute() throws IOException {
		WebDriver driver = MercariUtil.logon();
		// mywait();

		while (true) {
			try {
				//
				int todoType = choiceTodo();
				if (todoType == 0) {
					MericariAutoGetWon main = new MericariAutoGetWon();
					main.execute(driver);
				}
				if (todoType == 1) {
					MercariAutoPublish main = new MercariAutoPublish();
					main.execute(driver);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private int choiceTodo() {
		int type = 0;
		try {
			System.out.print("Type of todo : ");
			System.out.println("0:落札分取得;\n" + "1:出品する;\n" + "2:...;\n");

			stdReader = MercariUtil.getStdReader();
			while (true) {
				String line = stdReader.readLine();
				if ("0".equals(line.trim())) {
					type = 0;
					break;
				} else if ("1".equals(line.trim())) {
					type = 1;
					break;
				} else if ("2".equals(line.trim())) {
					type = 2;
					break;
				} else {
					System.out.println("Listed number only!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;
	}

}
