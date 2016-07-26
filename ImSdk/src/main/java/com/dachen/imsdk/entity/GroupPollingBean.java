package com.dachen.imsdk.entity;

import com.dachen.imsdk.db.po.ChatGroupPo;

import java.util.List;

public class GroupPollingBean {

	public int ur;
	public int count;
	public long ts;
	public boolean more;
	public List<ChatGroupPo> list;
}
