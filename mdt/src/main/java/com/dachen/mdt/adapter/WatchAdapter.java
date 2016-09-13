package com.dachen.mdt.adapter;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.view.ViewGroup;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.R;
import com.dachen.mdt.entity.CanViewOrderResult;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * [围观适配器]
 *
 * @author huxinwu
 * @version 1.0
 * @date 2016/9/13
 */
public class WatchAdapter extends CommonAdapterV2<CanViewOrderResult.OrderItem>{

    private DisplayImageOptions opts= new DisplayImageOptions.Builder()
            .bitmapConfig(Config.RGB_565)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .resetViewBeforeLoading(true)
            .showImageForEmptyUri(R.drawable.mdt_icon)
            .showImageOnFail(R.drawable.mdt_icon)
            .showImageOnLoading(R.drawable.mdt_icon)
            .build();

    public WatchAdapter(Context mContext, List<CanViewOrderResult.OrderItem> mData) {
        super(mContext, mData);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=ViewHolder.get(mContext,convertView,parent, R.layout.im_order_list_item,position);
        CanViewOrderResult.OrderItem po=mData.get(position);
//        OrderChatParam param= JSON.parseObject(po.param,OrderChatParam.class);
//        holder.setText(R.id.tv_name,param.patientName);
//        holder.setText(R.id.tv_name_info, OrderUtils.getSexStr(param.patientSex)+" "+param.patientAge+"岁");
//        holder.setText(R.id.tv_first_diagnose, "初步诊断:"+param.firstDiag );
//        holder.setText(R.id.tv_mdt_group, "MDT小组:"+param.mdtGroupName );
//        ImageLoader.getInstance().displayImage(po.gpic, (ImageView) holder.getView(R.id.iv_pic),opts);
//        holder.getConvertView().setOnClickListener(new ChatGroupClickListener(po));
//        holder.setText(R.id.tv_doc_manager,param.userName);
//        holder.setText(R.id.tv_msg_content,po.lastMsgContent);
//        handleState(holder,po);
//        handleUnread(holder,po);
//        handleTime(holder,po,param);
//        handleFrom(holder,param);
        return holder.getConvertView();
    }
}
