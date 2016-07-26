package com.dachen.imsdk.consts;

/**
 * 会话类型： 0:系统通知；1:双人会话；2：多人会话；3：新闻
 * 
 * @author lmc
 *
 */
public class SessionType {
	public static final int not_know = -1; // 未知
	public static final int notification = 0; // 系统通知（TODO 现在变成好友验证请求）
	public static final int session_double = 1; // 双人会话
	public static final int session_multi = 2; // 多个会话
	public static final int session_customer = 3; // 客服
	public static final int session_guide = 5;// 导医会话
}
