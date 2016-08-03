package com.dachen.imsdk.entity;


/**
 * 表示发送消息的返回结果
 * 
 * @author gaozhuo
 * @since 2015年8月20日
 *
 */
public class SendMessageResult {
	public Data data;
	public int resultCode;
	public String resultMsg;

	public class Data {
		public String gid;
		public String mid;
		public long time;
	}

}
