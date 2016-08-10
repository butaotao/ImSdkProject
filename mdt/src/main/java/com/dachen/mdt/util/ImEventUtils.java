package com.dachen.mdt.util;

import android.content.Context;

import com.dachen.imsdk.consts.EventType;
import com.dachen.imsdk.db.dao.ChatGroupDao;
import com.dachen.imsdk.entity.EventPL;
import com.dachen.imsdk.entity.event.NewMsgEvent;
import com.dachen.mdt.MyApplication;

import de.greenrobot1.event.EventBus;

/**
 * Created by Mcp on 2016/8/9.
 */
public class ImEventUtils {
    public static void handleEvent(EventPL event) {
        Context context = MyApplication.getInstance();
//        Logger.d(TAG, "handleEvent():event.eventType:" + event.eventType);
        if(EventType.group_user_exit.equals(event.eventType)){
            ChatGroupDao dao=new ChatGroupDao();
            dao.deleteById(event.param.get("groupId"));
            EventBus.getDefault().post(new NewMsgEvent());
        }
    }

}
