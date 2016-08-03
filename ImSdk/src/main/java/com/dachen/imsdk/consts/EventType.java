package com.dachen.imsdk.consts;

/**
 * 通知类型
 * 
 * 
 *  * 个人通讯录相关
    ADD_FRIEND("1","新的好友"), //加好友通知
	DEL_FRIEND("2","删好友"),
	PROFILE_CHANGE("3","个人资料变化"),
	GROUP_ADD_DOCTOR("4","加入医生集团"),
	GROUP_DELETE_DOCTOR("5","离开医生集团"),
//	FRIEND_REQ_CHANGE("8","新的好友"), //验证请求列表有变化
    
	 * 群组相关
	GROUP_ADDUSER("10","增加群组成员"),
	GROUP_DELUSER("11","删除群组成员"), 
	GROUP_QUIT("12","退出群聊"),
    GROUP_CHANGE_NAME("13","修改群组名称"), 
    GROUP_CHANGE_PIC("14","修改群组头像"); 

 * @author lmc
 *
 */
public class EventType {
	public static final String friend_add = "1";
	public static final String friend_delete = "2";
	public static final String friend_info_change = "3";
	
	// 医生集团的通讯录发生变更
	public static final String doctor_bloc_address_book_add = "4";
	public static final String doctor_bloc_address_book_delete = "5";

	public static final String group_add_user = "10";
	public static final String group_delete_user = "11";
	public static final String group_user_exit = "12";
	public static final String group_change_name = "13";
	public static final String group_change_avatar_image = "14";
	public static final String guide_state_change = "23";
	public static final String medie_state_change = "26";
	public static final String doctor_verified = "27";

	public static final String DOCTOR_ONLINE="7";  //医生上线
	public static final String DOCOTR_OFFLINE="8"; //医生下线
	public static final String DOCTOR_OFFLINE_SYSTEM_FORCE="9"; //医生被系统强制下线
	public static final String DOCTOR_DISTURB_ON="16"; //"医生开启免打扰"
	public static final String DOCTOR_DISTURB_OFF="17"; //"医生关闭免打扰"
	public static final String DOCTOR_NEW_CHECKIN="19";// 患者报到
	public static final String DOCTOR_APPLY_NUM="24";// 会诊变化

	public static final int V_CHAT_INVITE =30;// 发起视频邀请
	public static final int V_CHAT_REJECT =31;// 拒绝视频
	public static final int V_CHAT_BUSY =32;// 我忙线中
	public static final int V_CHAT_CALLER_CANCEL =33;// 发起者取消视频
	public static final int V_CHAT_ADD_USER =34;// 新增用户

	public static final int MSG_RETRACT =36;// 撤回消息
	public static final int VISIT_FLUSH = 37;//协同拜访页面刷新
	public static final int VISIT_JOIN = 38;//协同拜访加入
	public static final int VISIT_CONFIRM = 39;//协同拜访确认
	public static final int VISIT_DELETE = 40;//协同拜访取消(创建者)
	public static final int VISIT_CANCEL = 41;//协同拜访取消(参与者)
	public static final int NEW_DONG_TAI = 42;//新动态

}
