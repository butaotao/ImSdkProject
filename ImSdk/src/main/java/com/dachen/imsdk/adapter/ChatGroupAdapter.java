package com.dachen.imsdk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.TimeUtils;
import com.dachen.imsdk.R;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Mcp on 2016/5/23.
 */
public class ChatGroupAdapter extends BaseAdapter {
    private List<ChatGroupPo> mGroupList;
    private Context mContext;

    public ChatGroupAdapter(Context mContext, List<ChatGroupPo> mGroupList) {
        this.mContext = mContext;
        this.mGroupList = mGroupList;
    }

    @Override
    public int getCount() {
        return mGroupList.size();
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
        ViewHolder holder=ViewHolder.get(mContext,convertView,parent,R.layout.chat_group_item,position);
        ImageView ivHead=holder.getView(R.id.session_message_avatar_image);
        TextView tvUnread=holder.getView(R.id.session_message_unread_count);
        TextView tvName=holder.getView(R.id.session_message_nick_name); // 名称
        TextView tvContent=holder.getView(R.id.session_message_content); // 消息内容
        TextView tvTime=holder.getView(R.id.session_message_time); // 时间

        ChatGroupPo group=mGroupList.get(position);
        ImageLoader.getInstance().displayImage(group.gpic,ivHead);

        if(group.unreadCount==0){
            tvUnread.setVisibility(View.INVISIBLE);
        }else{
            tvUnread.setVisibility(View.VISIBLE);
            tvUnread.setText(group.unreadCount+"");
        }
        tvName.setText(group.name);
        tvContent.setText(group.lastMsgContent);
        tvTime.setText(TimeUtils.getTimeStateForCircle(group.updateStamp));
        return holder.getConvertView();
    }

}
