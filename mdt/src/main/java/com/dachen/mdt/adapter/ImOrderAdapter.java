package com.dachen.mdt.adapter;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.TimeUtils;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.mdt.R;
import com.dachen.mdt.entity.OrderChatParam;
import com.dachen.mdt.listener.ChatGroupClickListener;
import com.dachen.mdt.util.OrderUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;
import java.util.List;

/**
 * Created by Mcp on 2016/8/9.
 */
public class ImOrderAdapter extends CommonAdapterV2<ChatGroupPo>{

    private DisplayImageOptions opts= new DisplayImageOptions.Builder()
            .bitmapConfig(Config.RGB_565)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .resetViewBeforeLoading(true)
            .showImageForEmptyUri(R.drawable.mdt_icon)
            .showImageOnFail(R.drawable.mdt_icon)
            .showImageOnLoading(R.drawable.mdt_icon)
            .build();
    public ImOrderAdapter(Context mContext, List<ChatGroupPo> mData) {
        super(mContext, mData);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=ViewHolder.get(mContext,convertView,parent, R.layout.im_order_list_item,position);
        ChatGroupPo po=mData.get(position);
        OrderChatParam param= JSON.parseObject(po.param,OrderChatParam.class);
        holder.setText(R.id.tv_name,param.patientName);
        holder.setText(R.id.tv_name_info, OrderUtils.getSexStr(param.patientSex)+" "+param.patientAge+"岁");
        holder.setText(R.id.tv_first_diagnose, "初步诊断:"+param.firstDiag );
        holder.setText(R.id.tv_mdt_group, "MDT小组:"+param.mdtGroupName );
        ImageLoader.getInstance().displayImage(po.gpic, (ImageView) holder.getView(R.id.iv_pic),opts);
        holder.getConvertView().setOnClickListener(new ChatGroupClickListener(po));
        holder.setText(R.id.tv_doc_manager,param.userName);
        holder.setText(R.id.tv_msg_content,po.lastMsgContent);
        handleState(holder,po);
        handleUnread(holder,po);
        handleTime(holder,po,param);
        handleFrom(holder,param);
        return holder.getConvertView();
    }

    private void handleState( ViewHolder holder,  ChatGroupPo po){
        String text=null;
        if(po.bizStatus==2){
            text="已完成";
        }else if(po.bizStatus==1){
            text="进行中";
        }
        holder.setText(R.id.tv_state,text);
    }
    private void handleTime( ViewHolder holder,  ChatGroupPo po,OrderChatParam param){
        String text= TimeUtils.a_format.format(new Date(param.endTime));
        holder.setText(R.id.tv_end_time,text);
    }

    private void handleUnread( ViewHolder holder,  ChatGroupPo po){
        if(po.unreadCount==0){
            holder.setVisibility(R.id.tv_unread,View.INVISIBLE);
        }else{
            holder.setVisibility(R.id.tv_unread,View.VISIBLE);
            holder.setText(R.id.tv_unread,String.valueOf(po.unreadCount));
        }
    }
    private void handleFrom( ViewHolder holder, OrderChatParam param){
        if(ImUtils.getLoginUserId().equals(param.creator)){
            holder.setText(R.id.tv_from,"我发起的");
        }else{
            holder.setText(R.id.tv_from,"我接收的");
        }
    }

}
