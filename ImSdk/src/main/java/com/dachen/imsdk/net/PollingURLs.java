package com.dachen.imsdk.net;

/**
 * http://192.168.3.7:8091/im/msg/getBusiness
 * 
 * @author lmc
 *
 */
public class PollingURLs {
//	private static String IP = "192.168.3.7";
	private static String HEALTH_URL = "http://192.168.3.7/health/";
	private static String IM_BASE_URL = "http://192.168.3.7/";
//	public static String getBusiness = HEALTH_URL + "/im/msg/getBusiness";
//	public static String sendMessage = HEALTH_URL + "/im/msg/send";
	public static String getMessage = HEALTH_URL + "/im/msg/get";
//	public static String createGroup = HEALTH_URL + "/im/msg/createGroup";
	public static String groupInfo = HEALTH_URL + "/im/msg/groupInfo";
	public static String updateGroup = HEALTH_URL + "/im/msg/updateGroup";
	//获取用户信息
	public static String getUser = HEALTH_URL + "/user/get";
	// 快捷回复 - 快捷回复列表（获取数据）
	public static String getFastandReply = HEALTH_URL + "pack/fastandReply/getFastandReply";
	// 快捷回复 - 添加快捷回复
	public static String addFastandReply = HEALTH_URL + "pack/fastandReply/addFastandReply";
	// 快捷回复 - 删除快捷回复
	public static String deleteFastandReply = HEALTH_URL + "pack/fastandReply/deleteFastandReply";
	// 快捷回复 - 修改快捷回复
	public static String updateFastandReply = HEALTH_URL + "pack/fastandReply/updateFastandReply";
	
	public static String getUserDetail(){
		return HEALTH_URL + "user/getUserDetail";
	}
	public static String getMessageV2(){
		return IM_BASE_URL + "im/msg/msgList.action";
	}
	public static String sendMessageV2(){
		return IM_BASE_URL + "im/convers/send.action";
	}
	public static String imPollingUrl(){
		return IM_BASE_URL + "im/msg/poll.action";
	}
	public static String getGroupList(){
		return IM_BASE_URL + "im/msg/groupList.action";
	}
	public static String getEventList(){
		return IM_BASE_URL + "im/convers/eventList.action";
	}
	public static String getGroupBiz(){
		return HEALTH_URL + "im/msg/groupParam";
	}
	public static String getUploadToken(){
		return IM_BASE_URL + "im/file/getUpToken.action";
	}
//	public static String getUploadToken(){
//		return HEALTH_URL + "getQiniuToken";
//	}
	public static String sendEvent(){
		return IM_BASE_URL + "im/convers/sendEvent.action";
	}
	public static String groupUserInfo(){
		return IM_BASE_URL + "im/group/groupUsers.action";
	}
	public static String sendPush(){
		return IM_BASE_URL + "im/convers/push.action";
	}
	public static String serverTime(){
		return HEALTH_URL + "common/getServerTime";
	}
	public static String getPub(){
		return HEALTH_URL + "pub/get";
	}

	public static String addGroupUser(){
		return IM_BASE_URL + "im/convers/addGroupUser.action";
	}
	public static String delGroupUser(){
		return IM_BASE_URL + "im/convers/delGroupUser.action";
	}
	public static String updateGroupName(){
		return IM_BASE_URL + "im/convers/updateGroupName.action";
	}
	public static String updateGroupPic(){
		return IM_BASE_URL + "im/convers/updateGroupPic.action";
	}
	public static String updateState(){
		return IM_BASE_URL + "im/convers/updateState.action";
	}
	public static String delGroupRecord(){
		return IM_BASE_URL + "im/convers/delGroupRecord.action";
	}
	public static String togglePush(){
		return IM_BASE_URL + "im/convers/togglePush.action";
	}
	public static String closeGroup(){
		return IM_BASE_URL + "im/convers/closeGroup.action";
	}
	public static String createGroup(){
		return IM_BASE_URL + "im/convers/createGroup.action";
	}
	public static String delGroup(){
		return IM_BASE_URL + "im/convers/delGroup.action";
	}
	public static String favGroup(){
		return IM_BASE_URL + "im/convers/groupFavorite.action";
	}
	public static String getGroupParams(){
		return HEALTH_URL + "im/msg/groupParams";
	}
	public static String getGroupInfo(){
		return IM_BASE_URL + "im/convers/groupInfo.action";
	}
	public static String registerDevice(){
		return IM_BASE_URL + "im/registerDeviceToken.action";
	}
	public static String removeDevice(){
		return IM_BASE_URL + "im/removeDeviceToken.action";
	}
	public static String getPushSetting(){
		return IM_BASE_URL + "im/getUserSetting.action";
	}
	public static String setPushSetting(){
		return IM_BASE_URL + "im/updatePushStatus.action";
	}
	public static String webSocket(){
		return IM_BASE_URL + "im/websocket";
	}

	public static String getTxSig(){
		return HEALTH_URL + "sig/getSig";
	}
	public static String retractMsg(){
		return IM_BASE_URL + "im/convers/withdrawMessage.action";
	}
	public static String forwardMsg(){
		return IM_BASE_URL + "im/convers/forward.action";
	}
	public static String clearGroupMsg(){
		return IM_BASE_URL + "im/convers/delGroupRecord.action";
	}
	public static String topChatGroup(){
		return IM_BASE_URL + "im/convers/groupTop.action";
	}
	public static String observeGroupInfo(){
		return IM_BASE_URL + "im/convers/groupInfo.action";
	}
	public static String observeMsgList(){
		return IM_BASE_URL + "im/convers/groupMsg.action";
	}
	/**
	 * 改变Ip,内网外网切换
	 */
	public static void changeIp(String baseIp) {

//		HEALTH_URL = "http://" + baseIp + ":8091/";
//		IM_BASE_URL = "http://" + baseIp + ":8090/";
		HEALTH_URL = "http://" + baseIp + "/health/";
		IM_BASE_URL = "http://" + baseIp + "/";
//		getBusiness = HEALTH_URL + "/im/msg/getBusiness";
//		sendMessage = HEALTH_URL + "/im/msg/send";
		getMessage = HEALTH_URL + "/im/msg/get";
//		createGroup = HEALTH_URL + "/im/msg/createGroup";
		groupInfo = HEALTH_URL + "/im/msg/groupInfo";
		updateGroup = HEALTH_URL + "/im/msg/updateGroup";
		//获取用户信息
		getUser = HEALTH_URL + "/user/get";
		getFastandReply = HEALTH_URL + "pack/fastandReply/getFastandReply";
		addFastandReply = HEALTH_URL + "pack/fastandReply/addFastandReply";
		deleteFastandReply = HEALTH_URL + "pack/fastandReply/deleteFastandReply";
		updateFastandReply = HEALTH_URL + "pack/fastandReply/updateFastandReply";
	}
}

