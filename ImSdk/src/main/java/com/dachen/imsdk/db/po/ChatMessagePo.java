package com.dachen.imsdk.db.po;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.entity.ChatMessageV2;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName="ChatMessage")
public class ChatMessagePo implements Serializable{
	
	private static final long serialVersionUID = 1076369828533243186L;
	
	public static final int REQ_STATES_SENDING=0;
	public static final int REQ_STATES_SEND_OK=1;
	public static final int REQ_STATES_SEND_FAIL=2;
	public static final int REQ_STATES_UP_FILE=3;
	public static final int REQ_STATES_UP_FILE_OK=4;
	public static final int REQ_STATES_UP_FILE_FAIL=5;
	
	@DatabaseField(generatedId=true)
	public int id;
	public static final String _id="id";
	@DatabaseField
	public String msgId;
	public static final String _msgId="msgId";
	@DatabaseField
	public String clientMsgId;
	public static final String _clientMsgId="clientMsgId";
	@DatabaseField
	public String fromUserId;
	@DatabaseField
	public long sendTime;
	public static final String _sendTime="sendTime";
	@DatabaseField
	public int type;
	public static final String _type="type";
	@DatabaseField
	public String content;
	@DatabaseField
	public int status;
	@DatabaseField
	public int requestState;
	public static final String _requestState="requestState";
	@DatabaseField
	public int direction;
	@DatabaseField
	public String groupId;
	public static final String _groupId="groupId";
	@DatabaseField
	public String sourceId;
	@DatabaseField
	public String fromClient;
	@DatabaseField
	public String param;
	@DatabaseField
	public int isRetract;
	@DatabaseField
	public int deleteFlag;
	public static final String _deleteFlag="deleteFlag";

	public boolean isMySend(){
		String userId= ImSdk.getInstance().userId;
		if(TextUtils.isEmpty(userId))
			return false;
		return userId.equals(fromUserId);
	}
	public boolean isRead(){
		return status==1;
	}
	
	public boolean isUploaded(){
		if(requestState==REQ_STATES_UP_FILE_OK){
			return true;
		}
		ChatMessageV2.FileMsgBaseParam p= JSON.parseObject(param, ChatMessageV2.FileMsgBaseParam.class);
		if(p!=null && !TextUtils.isEmpty(p.key)){
			return true;
		}
		return false;
	}

}
