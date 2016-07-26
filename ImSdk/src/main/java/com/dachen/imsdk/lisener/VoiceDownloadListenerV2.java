package com.dachen.imsdk.lisener;

import android.view.View;

import com.dachen.common.utils.downloader.DownloadListener;
import com.dachen.imsdk.db.dao.ChatMessageDao;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.utils.ImSpUtils;

public class VoiceDownloadListenerV2 implements DownloadListener {
	private ChatMessagePo chatMessage;
	private ChatMessageDao dao;

	public VoiceDownloadListenerV2(ChatMessagePo chatMessage,ChatMessageDao dao) {
		this.chatMessage = chatMessage;
		this.dao=dao;
	}

	@Override
	public void onStarted(String uri, View view) {
		if (view != null) {
			view.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onFailed(String uri, com.dachen.common.utils.downloader.FailReason failReason, View view) {
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	@Override
	public void onComplete(String uri, String filePath, View view) {
		if (view != null) {
			view.setVisibility(View.GONE);
		}
		//用本地filePath服务器上的uri替代
//		VoiceMsgParam p=JSON.parseObject(chatMessage.param, VoiceMsgParam.class);
//		p.localFilePath = filePath;
//		chatMessage.param=JSON.toJSONString(p);
//		//uri变了，需要更新数据库
//		dao.saveMessage(chatMessage);
		ImSpUtils.setMsgFilePath(chatMessage.msgId, filePath);
		
	}

	@Override
	public void onCancelled(String uri, View view) {

	}
}
