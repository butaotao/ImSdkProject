package com.dachen.mdt.util;

import android.content.Context;
import android.text.TextUtils;

import com.dachen.common.utils.Logger;
import com.dachen.imsdk.db.dao.ChatGroupDao;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.mdt.activity.order.OrderChatActivity;

public class ChatActivityUtilsV2 {
    private static final String TAG = ChatActivityUtilsV2.class.getSimpleName();

    /**
     * 会话列表进聊天界面入口
     *
     * @param context
     * @param po
     */
    public static void openUI(Context context, ChatGroupPo po) {
        if (po == null) {
            return;
        }
        OrderChatActivity.openUI(context,po.name,po.groupId);

    }


    /**
     * 推送消息进聊天界面入口
     *
     * @param context
     * @param groupId
     */
    public static void openUI(final Context context, final String groupId, String rtype) {

        ChatGroupDao dao = new ChatGroupDao(context, ImUtils.getLoginUserId());
        ChatGroupPo po = dao.queryForId(groupId);
        if (po != null) {
            openUI(context, po);

        } else {// 刚下单时sessionMessageDB == null
            Logger.d(TAG, "rtype=" + rtype);
            if (TextUtils.isEmpty(rtype)) {
                return;
            }

        }
    }



}
