package com.walk_nie.taobao.kakaku.explore.shaver;



public class SpecObject extends com.walk_nie.taobao.kakaku.SpecObject {
	// 刃の枚数
	public String knifeAmount = "";
	// 駆動方式
	public String driverType = "";
	// 電源方式
	public String rechageType = "";
	// 充電時間
	public String rechageTime = "";
	// 使用日数
	public String usableDays = "";
	// 自動洗浄機能
	public boolean isAutoWash = false;
	// 洗浄機能
	public String washType = "";
	// 水洗い可
	public boolean isWashable = false; 
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
			sb.append(knifeAmount);
		if(isAutoWash){
			sb.append(" 自動洗浄");
		}
		if(isWashable){
			sb.append(" 可水洗");
		}
		return sb.toString();
	}

}
