package com.dachen.imsdk.db.po;

import com.dachen.imsdk.entity.GroupInfo2Bean;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class GroupUserPo {

	@DatabaseField(generatedId = true)
	public int fid;
	@DatabaseField
	public String groupId;
	public static final String _groupId="groupId";
	@DatabaseField
	public String userId;
	public static final String _userId="userId";
	@DatabaseField
	public String headUrl;
	@DatabaseField
	public String userNick;
	@DatabaseField
	public int userType;
	@DatabaseField
	public int role;

	public GroupUserPo() {
	}
	public GroupUserPo(String userId, String headUrl, String userNick, int userType, int role) {
		super();
		this.userId = userId;
		this.headUrl = headUrl;
		this.userNick = userNick;
		this.userType = userType;
		this.role=role;
	}

	public GroupInfo2Bean.Data.UserInfo toUserInfo(){
		GroupInfo2Bean.Data.UserInfo res=new GroupInfo2Bean.Data.UserInfo();
		res.id=userId;
		res.pic=headUrl;
		res.name=userNick;
		res.userType=userType;
		res.role=role;
		return res;
	}
}
