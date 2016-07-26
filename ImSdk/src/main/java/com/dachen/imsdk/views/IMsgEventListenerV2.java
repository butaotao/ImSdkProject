package com.dachen.imsdk.views;


import com.dachen.imsdk.db.po.ChatMessagePo;

/**
 * 消息事件的回调
 * 
 * @author WANG 新增(代替原酷聊中的MessageEventListener)
 *
 */
public interface IMsgEventListenerV2 {
	public void onEmptyTouch();// 点击空白处，让输入框归位

	public void onMyAvatarClick();

	public void onFriendAvatarClick(String friendUserId);

	public void onMessageClick(ChatMessagePo msg);

	public void onMessageLongClick(ChatMessagePo msg);

	public void onSendAgain(ChatMessagePo msg);
}
