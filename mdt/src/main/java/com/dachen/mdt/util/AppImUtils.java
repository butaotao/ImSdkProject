package com.dachen.mdt.util;

import com.dachen.common.AppManager;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.activities.ChatActivityV2;
import com.dachen.imsdk.activities.ImBaseActivity;
import com.dachen.imsdk.activities.ImBaseActivity.BaseActRunnable;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.entity.EventPL;
import com.dachen.imsdk.entity.event.NewMsgEvent;
import com.dachen.imsdk.net.ImPolling;
import com.dachen.imsdk.out.OnImSdkListener;

import java.util.List;

import de.greenrobot1.event.EventBus;

/**
 * Created by Mcp on 2016/8/9.
 */
public class AppImUtils {

    public static void initImAct() {
        ImBaseActivity.ON_CREATE_RUN=new BaseActRunnable() {
            @Override
            public void run(ImBaseActivity act) {
                AppManager.getAppManager().addActivity(act);
            }
        };
        ImBaseActivity.ON_RESUME_RUN = new BaseActRunnable() {
            @Override
            public void run(ImBaseActivity act) {
                ImPolling.getInstance().onResume();
            }
        };
        ImBaseActivity.ON_PAUSE_RUN = new BaseActRunnable() {
            @Override
            public void run(ImBaseActivity act) {
                ImPolling.getInstance().onPause();
            }
        };
        ImBaseActivity.ON_DESTROY_RUN=new BaseActRunnable() {
            @Override
            public void run(ImBaseActivity act) {
                AppManager.getAppManager().removeActivity(act);
            }
        };

        ImSdk.getInstance().setImSdkListener(new OnImSdkListener() {
            @Override
            public void onGroupList(List<ChatGroupPo> list) {
                for (ChatGroupPo po : list) {
                    if (ChatActivityV2.instance != null && po.groupId.equals(ChatActivityV2.instance.getGroupId())) {
//                        ChatActivityV2.instance.pollImmediately();
                        ChatActivityV2.instance.onGroupUpdate(po);
                    }
                }
//                ChatGroupDao dao=new ChatGroupDao();
//                int orderUnread=dao.getUnreadCount(getBizTypeListOrder(),null,null);
                EventBus.getDefault().post(new NewMsgEvent());
            }

            @Override
            public void onEvent(EventPL event) {
                ImEventUtils.handleEvent(event);
            }


        });
//        DCommonSdk.setCommonRequestListener(new OnCommonRequestListener() {
//            @Override
//            public boolean onTokenErr() {
//                return true;
//            }
//
//            @Override
//            public boolean onUpdateVersionErr(String msg) {
//                return true;
//            }
//        });

    }

    public static String[] getBizTypeListOrder() {
        return new String[]{"1_3", "2_3"};
    }
}
