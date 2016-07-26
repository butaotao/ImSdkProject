package com.dachen.imsdk.out;

import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.entity.EventPL;
import com.dachen.imsdk.service.ImEventManager;

import java.util.List;

/**
 * Created by Mcp on 2016/2/19.
 */
public class ImObserverManager {
    public static OnImSdkListener imSdkListener;
//    public static OnImRequestListener imRequestListener;

    public static void setImSdkListener(OnImSdkListener l){
//        imSdkListener =new WeakReference<OnImSdkListener>(l);
        imSdkListener = l;
    }


    public static void handleChatGroup(List<ChatGroupPo> list) {
//        OnImSdkListener l =   new RefTool<OnImSdkListener>(imSdkListener).getRef();
        OnImSdkListener l = imSdkListener;
        if (l == null)
            return;
        l.onGroupList(list);
    }

    public static void notifyEvent(EventPL ele) {
        ImEventManager.handleEvent(ele);
        if(imSdkListener==null)return;
        imSdkListener.onEvent(ele);
    }


}
