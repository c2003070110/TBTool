package com.walk_nie.ya;

public class RegObjInfo {

	public String id;
	public String pswd;
	public String dispName;
	public String mailAddress;
	public String secQuestion;
	public String secAnswer;
	public String sex;
	public String birthYear;
	public String birthMonth;
	public String birthDay;
	public String postCode;

	public String name1;
	public String name2;
	public String name1Kana;
	public String name2Kana;

	public String telNo;

	public String toString() {
		return id + "@yahoo.co.jp\t" + pswd + "\t" + "\t" + secQuestion + "\t" + secAnswer + "\t" + birthYear + "\t"
				+ birthMonth + "\t" + birthDay + "\t" + dispName;
	}

	public static RegObjInfo parse(String line) {
		RegObjInfo obj = new RegObjInfo();
		String[] spl = line.split("\t");
		int idx = 0;
		String ss = spl[idx++];
		obj.id = ss.substring(0, ss.indexOf("@"));
		if(spl.length > idx){
			obj.pswd = spl[idx++];
		}
		if(spl.length > idx){
			obj.secQuestion = spl[idx++];
		}
		if(spl.length > idx){
			obj.secAnswer = spl[idx++];
		}
		if(spl.length > idx){
			obj.birthYear = spl[idx++];
		}
		if(spl.length > idx){
			obj.birthMonth = spl[idx++];
		}
		if(spl.length > idx){
			obj.birthDay = spl[idx++];
		}
		if(spl.length > idx){
			obj.dispName = spl[idx++];
		}
		return obj;
	}

}
