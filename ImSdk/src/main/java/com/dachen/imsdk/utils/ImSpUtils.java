package com.dachen.imsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.db.po.ChatMessagePo;

public class ImSpUtils {

    //	private static final String SURFIX_IM_COMMON="_msg_file_cache";
    private static final String SURFIX_IM_COMMON = "common";
    private static final String KEY_EVENT_TS = "eventTs";
    private static final String KEY_GROUP_TS = "groupTs";
    private static final String KEY_OLD_GROUP_DONE= "oldGroupDone";

	private static final String SURFIX_MSG_FILE_CACHE="_msg_file_cache";
	private static final String SURFIX_GROUP_FIRST_MSG="_group_first_msg";
	private static final String SET_NOTIFICATION_UNREAD="setNotificationUnread";


	public static SharedPreferences spCommon(){
		String userId=ImUtils.getLoginUserId();
		return ImSdk.getInstance().context.getSharedPreferences(userId + SURFIX_IM_COMMON, Context.MODE_PRIVATE);
	}
	private static SharedPreferences spMsgFile(){
		String userId=ImUtils.getLoginUserId();
		return ImSdk.getInstance().context.getSharedPreferences(userId + SURFIX_MSG_FILE_CACHE, Context.MODE_PRIVATE);
	}
	private static SharedPreferences spGroupFirstMsg(){
		String userId=ImUtils.getLoginUserId();
		return ImSdk.getInstance().context.getSharedPreferences(userId + SURFIX_GROUP_FIRST_MSG, Context.MODE_PRIVATE);
	}

	public static void setNotificationUnread(int setNotificationUnread){
		String userId=ImUtils.getLoginUserId();
		ImSdk.getInstance().context.getSharedPreferences(userId + SET_NOTIFICATION_UNREAD, Context.MODE_PRIVATE).edit().putInt("setNotificationUnread",setNotificationUnread).commit();
	}

	public static int getNotificationUnread(){
		String userId=ImUtils.getLoginUserId();
		return ImSdk.getInstance().context.getSharedPreferences(userId + SET_NOTIFICATION_UNREAD, Context.MODE_PRIVATE).getInt("setNotificationUnread",0);
	}


	public static String getMsgFilePath(ChatMessagePo msg){
		if(msg.isMySend()){
			if(TextUtils.isEmpty(msg.clientMsgId)){
				return spMsgFile().getString(msg.msgId, "");
			}else{
				String path=spMsgFile().getString(msg.clientMsgId, "");
				if(TextUtils.isEmpty(path)){
					path=spMsgFile().getString(msg.msgId, "");
				}
				return path;
			}
		}else{
			return spMsgFile().getString(msg.msgId, "");
		}
	}
	public static String getMsgFilePath(String id){
		if(TextUtils.isEmpty(id)){
			return "";
		}
		return spMsgFile().getString(id, "");
	}
	public static void setMsgFilePath(String id,String path){
		Editor e=spMsgFile().edit();
		if(TextUtils.isEmpty(path)){
			e.remove(path).commit();
		}else{
			e.putString(id, path).commit();
		}
	}

	public static String getFirstMsgId(String groupId){
		return spGroupFirstMsg().getString(groupId, "");
	}
	public static void setFirstMsgId(String groupId,String msgId){
		spGroupFirstMsg().edit().putString(groupId, msgId).commit();
	}

	public static void setEventTs(long ts){
		spCommon().edit().putLong(KEY_EVENT_TS, ts).commit();
	}
	public static long getEventTs(){
		return spCommon().getLong(KEY_EVENT_TS, 0);
	}
	public static void setGroupTs(long ts){
		spCommon().edit().putLong(KEY_GROUP_TS, ts).commit();
	}
	public static long getGroupTs(){
		return spCommon().getLong(KEY_GROUP_TS, 0);
	}

	public static boolean getOldGroupDone(){
		return spCommon().getBoolean(KEY_OLD_GROUP_DONE,false);
	}
	public static void setOldGroupDone(boolean done){
		spCommon().edit().putBoolean(KEY_OLD_GROUP_DONE,done).apply();
	}
}
