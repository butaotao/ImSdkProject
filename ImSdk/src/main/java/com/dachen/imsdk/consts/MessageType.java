package com.dachen.imsdk.consts;

/**
 * msgType{ 1, //文本 2, //图片 3, //语音 4, //位置 5, //动画 6, //视频 7, //音频 8, //名片 9,
 * //文件 10,//提醒 #11,//群组邀请（暂时不用） 12 // 指令事件 };
 * 
 * 
 * 消息类型
 * 
 * @author lmc
 *
 */
public class MessageType {
	public static final int text = 1;// 文本
	public static final int image = 2;// 图片
	public static final int voice = 3;// 语音
	public static final int location = 4; // 位置
	public static final int gif = 5;// 动画
	public static final int video = 6;// 视频
	public static final int audio = 7;// 音频
	public static final int card = 8;// 名片
	public static final int file = 9; // 文件
	public static final int remind = 10; // 提醒
	// public static final int roomInvite = 11;// 群组邀请
	public static final int notification = 12;// 指令事件 或者 系统通知 或者 通知 或者 待办 == 提醒
	public static final int text_and_uri = 13;// 文本+链接
	public static final int image_and_text = 14;// 图文消息 == 图片+文字
	public static final int notification_guide = 15;// 导医通知
	public static final int newmpt16 = 16;//公众号 整个
	public static final int newmpt17 = 17;// 公众号 小
	public static final int newmpt18 = 18;//多图文消息
//	@Deprecated
//	public static final int command = notification;// 指令事件 或者 系统通知

	public static boolean isMptMsg(int type) {
		return type == image_and_text || type == newmpt16||type==newmpt17;
	}
}
