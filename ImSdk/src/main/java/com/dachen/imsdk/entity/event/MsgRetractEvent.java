package com.dachen.imsdk.entity.event;

/**
 * Created by Mcp on 2016/5/20.
 */
public class MsgRetractEvent {
    public String gid;
    public String msgId;

    public MsgRetractEvent(String gid, String msgId) {
        this.gid = gid;
        this.msgId = msgId;
    }
}
