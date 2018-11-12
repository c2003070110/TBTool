package com.walk_nie.douyin;

import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

public class MyMsgHandlerFace  implements IMsgHandlerFace {

	@Override
	public String textMsgHandle(BaseMsg msg) {
		String from = msg.getFromUserName();
		if(!from.equals("")){
			// only!
			return null;
		}
		String context = msg.getContent();
		DouYinToWeibo singl = DouYinToWeibo.getInstance();
		try {
			singl.downloadAndPublish(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String picMsgHandle(BaseMsg msg) {
		return null;
	}

	@Override
	public String voiceMsgHandle(BaseMsg msg) {
		return null;
	}

	@Override
	public String viedoMsgHandle(BaseMsg msg) {
		return null;
	}

	@Override
	public String nameCardMsgHandle(BaseMsg msg) {
		return null;
	}

	@Override
	public void sysMsgHandle(BaseMsg msg) {
		
	}

	@Override
	public String verifyAddFriendMsgHandle(BaseMsg msg) {
		return null;
	}

	@Override
	public String mediaMsgHandle(BaseMsg msg) {
		return null;
	}

}
