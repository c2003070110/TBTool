package com.walk_nie.taobao.kakaku.explore.earphone;

import org.jsoup.helper.StringUtil;


public class SpecObject extends com.walk_nie.taobao.kakaku.SpecObject {
	// 装着方式
	public String setType = "";
	// 構造
	public String constructType = "";
	// 駆動方式
	public String driverType = "";
	// ワイヤレス
	public String wirelessType = "";
	// マイク
	public boolean hasMicro = false;
	// 再生周波数帯域 频响范围
	public String waveBand = "";
	// インピーダンス 阻抗
	public String impedance = "";
	// 音圧感度 灵敏度
	public String voiceDepth = "";
	// ハイレゾ 
	public boolean isHighRes = false;
	// コード長 缆线长
	public String codeLength = "";
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		if(isHighRes){
			sb.append("");
		}
		if(hasMicro){
			sb.append("有麦克风");
		}
		if(!StringUtil.isBlank(wirelessType)){
			sb.append("");
		}
		if(driverType.equals("ダイナミック")||driverType.equals("ダイナミック型")){
			sb.append("动圈");
		}else if(driverType.equals("バランスド・アーマチュア")||driverType.equals("バランスド・アーマチュア型")){
			sb.append("铁圈");
		}else if(driverType.equals("コンデンサ")||driverType.equals("コンデンサ型型")){
			sb.append("静电");
		}
		if(!StringUtil.isBlank(waveBand)){
			sb.append("频响范围　：").append(waveBand);
		}
		if(!StringUtil.isBlank(impedance)){
			sb.append("阻抗　：").append(impedance);
		}
		if(!StringUtil.isBlank(voiceDepth)){
			sb.append("灵敏度　：").append(voiceDepth);
		}
		return sb.toString();
	}

}
