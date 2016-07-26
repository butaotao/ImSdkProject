package com.dachen.imsdk.db.po;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class SimpleUserInfo {
	public static class SimpleUserType{
		public static int TYPE_NORMAL=0;
		public static int TYPE_PUBLIC=1;
	}
	/**
	 * 普通类型
	 */
	public static int USER_TYPE_NORMAL=0;
	/**
	 * 公众号类型
	 */
	public static int USER_TYPE_PUBLIC=1;

	@DatabaseField(id=true)
	public String userId;
	@DatabaseField
	public String headUrl;
	@DatabaseField
	public String userNick;
	@DatabaseField
	public String userDesc;
	@DatabaseField
	public int userType;
	
	public SimpleUserInfo() {
	}
	public SimpleUserInfo(String userId, String headUrl, String userNick, String userDesc, int userType) {
		super();
		this.userId = userId;
		this.headUrl = headUrl;
		this.userNick = userNick;
		this.userDesc = userDesc;
		this.userType = userType;
	}
	
}
