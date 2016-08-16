package com.dachen.imsdk.adapter;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.ToastUtil;
import com.dachen.imsdk.R;
import com.dachen.imsdk.activities.ChatActivityV2;
import com.dachen.imsdk.activities.ChatGroupActivity;
import com.dachen.imsdk.archive.download.ArchiveLoader;
import com.dachen.imsdk.archive.entity.ArchiveItem;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.ChatMessageV2;
import com.dachen.imsdk.out.ImMsgHandler;

import java.util.List;

/**
 * Created by Mcp on 2016/5/20.
 */
public class MsgMenuAdapter extends BaseAdapter {
    public static final String INTENT_EXTRA_MSG_ID="msgId";
    public static final String INTENT_EXTRA_GROUP_ID="groupId";
    public static final String ITEM_FORWARD="转发";
    public static final String ITEM_RETRACT="撤回";
    public static final String ITEM_COPY="复制";
    public static final String ITEM_DELETE="删除";
    public static final String ITEM_DOWNLOAD="下载";
    private ChatMessagePo mMsg;
    private ChatActivityV2 mContext;
    private List<String> items;
    private ChatAdapterV2 mChatAdapter;
    private Dialog mDialog;
    private ClipboardManager mClipboardManager;

    public MsgMenuAdapter(ChatMessagePo msg,ChatActivityV2 context, List<String> items, ChatAdapterV2 chatAdapter, Dialog dialog) {
        this.mMsg=msg;
        this.mContext=context;
        this.items=items;
        this.mChatAdapter= chatAdapter;
        this.mDialog=dialog;
        mClipboardManager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.msg_menu_item,parent,false);
        }
        TextView tv= (TextView) convertView.findViewById(R.id.text_view);
        final String action= items.get(position);
        tv.setText(action);
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(action==null)return;
                if(action.equals(ITEM_RETRACT)){
                    mContext.retractMsg(mMsg);
                }else if(action.equals(ITEM_FORWARD)){
                    ImMsgHandler msgHandler=mChatAdapter.msgHandler;
                    if(!msgHandler.onForwardMessage(mMsg.msgId)){
                        mContext.startActivity(new Intent(mContext,ChatGroupActivity.class).putExtra(INTENT_EXTRA_MSG_ID,mMsg.msgId));
                    }
                }else if(action.equals(ITEM_COPY)){
                    mClipboardManager.setPrimaryClip(ClipData.newPlainText("text",mMsg.content));
                    ToastUtil.showToast(mContext,"文字已复制");
                }else if(action.equals(ITEM_DELETE)){
                    mContext.deleteMsg(mMsg);
                }else if(action.equals(ITEM_DOWNLOAD)){
                    ChatMessageV2.ArchiveMsgParam p = JSON.parseObject(mMsg.param, ChatMessageV2.ArchiveMsgParam.class);
                    final ArchiveItem item = new ArchiveItem(p.key, p.uri, p.name, p.format, p.size);
                    ArchiveLoader.getInstance().startDownload(item);
                    mChatAdapter.notifyDataSetChanged();
                }
                mDialog.dismiss();
            }
        });

        return convertView;
    }
}
