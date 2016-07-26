package com.dachen.imsdk.entity;

import com.dachen.imsdk.entity.GroupInfo2Bean.Data.UserInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mcp on 2016/3/19.
 */
public class VChatInviteParam implements Serializable{
    public int roomId;
    public String gid;
    public String fromUserId;
    public List<UserInfo> userList;
    public List<String> idList;
}
