package com.dachen.imsdk.entity;

import java.io.Serializable;

/**
 * Created by Mcp on 2016/3/19.
 */
public class VChatRejectParam implements Serializable {
    public static final int REASON_NORMAL = 0;//正常拒绝
    public static final int REASON_TIMEOUT = 1;//超时没有接听导致的拒绝
    public int roomId;
    public String gid;
    public String rejectId;
    //拒绝原因，0-正常拒绝 1-超时没有接听导致的拒绝
    public int reason;
}
