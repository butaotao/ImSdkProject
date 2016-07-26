package com.dachen.imsdk.service;

import com.dachen.imsdk.consts.EventType;
import com.dachen.imsdk.db.dao.ChatMessageDao;
import com.dachen.imsdk.entity.EventPL;
import com.dachen.imsdk.entity.event.MsgRetractEvent;

import de.greenrobot1.event.EventBus;

/**
 * Created by Mcp on 2016/5/20.
 */
public class ImEventManager {
    public static void handleEvent(EventPL ele) {
        if ((EventType.MSG_RETRACT + "").equals(ele.eventType)) {
            String gid=ele.param.get("groupId");
            String msgId=ele.param.get("msgId");
            OnRetractMsg(gid,msgId);
        }
    }

    public static void OnRetractMsg(String gid,String msgId){
        ChatMessageDao dao=new ChatMessageDao();
        dao.retractMsg(msgId);
        EventBus.getDefault().post(new MsgRetractEvent(gid,msgId));
    }
}
