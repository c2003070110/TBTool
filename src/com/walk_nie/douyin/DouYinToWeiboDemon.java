package com.walk_nie.douyin;

import com.walk_nie.util.NieConfig;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

public class DouYinToWeiboDemon {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new DouYinToWeiboDemon().execute();
	}
	protected void execute() throws Exception {

		String qrPath = NieConfig.getConfig("douyin.qrcode.folder"); // 保存登陆二维码图片的路径，这里需要在本地新建目录
		IMsgHandlerFace msgHandler = new MyMsgHandlerFace(); // 实现IMsgHandlerFace接口的类
		Wechat wechat = new Wechat(msgHandler, qrPath); // 【注入】
		wechat.start();
	}

}
