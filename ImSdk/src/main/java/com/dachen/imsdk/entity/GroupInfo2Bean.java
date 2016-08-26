package com.dachen.imsdk.entity;

import java.io.Serializable;

public class GroupInfo2Bean {
	public int resultCode;
	public String resultMsg;
	public String detailMsg;
	public Data data;

	public static class Data implements Serializable {
		private static final long serialVersionUID = -1719413101285965380L;
		public long createDate; // 创建时间
		public String creator; // 创建者的userId
		public boolean disable; // TODO 好像没用
		public String headerId; // TODO 好像没用
		public int notify;
		public int status;
		public String rtype; // "3_3"
		public int type; // 1双人，2多人
		// +++ 创建
		public String gid;
		public String[] userIds; // TODO 好像没用
		public String gname;
		public String gpic;
		public UserInfo[] userList;
		
		public static class UserInfo implements Serializable {
			private static final long serialVersionUID = 2848050705270695748L;

			public UserInfo() {
			}
			public UserInfo(String id) {
				this.id = id;
			}

			public UserInfo(String id, String name, String pic) {
				this.id = id;
				this.name = name;
				this.pic = pic;
			}

			public String id; // userId
			public String name; // 名称
			public String pic; // 图片地址
			public int userType; // 用户类型
			public int role;
			public int volume;//语音通话音量
			public int phoneOnline;//语音是否在线

		}
	}

	public boolean isSuccess() {
		if (1==resultCode) {
			return true;
		}
		return false;
	}
}
