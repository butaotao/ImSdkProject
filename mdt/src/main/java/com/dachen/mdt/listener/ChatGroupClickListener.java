package com.dachen.mdt.listener;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.mdt.MyApplication;
import com.dachen.mdt.activity.order.ObserveChatActivity;

/**
 * Created by Mcp on 2016/7/29.
 */
public class ChatGroupClickListener implements OnClickListener {
    private ChatGroupPo po;

    public ChatGroupClickListener(ChatGroupPo po) {
        this.po = po;
    }

    @Override
    public void onClick(View v) {
        if (po == null) return;
        Context context = MyApplication.getInstance();
//        OrderChatActivity.openUI(context,po.name,po.groupId);
        ObserveChatActivity.openUI(context,po);
    }
}
