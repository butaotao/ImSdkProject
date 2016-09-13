package com.dachen.mdt.adapter;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.view.ViewGroup;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.TimeUtils;
import com.dachen.mdt.R;
import com.dachen.mdt.entity.CanViewOrderResult;
import com.dachen.mdt.util.OrderUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.Date;
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
        ViewHolder holder=ViewHolder.get(mContext, convertView, parent, R.layout.watch_order_list_item, position);
        CanViewOrderResult.OrderItem bean = mData.get(position);
        holder.setText(R.id.tv_name, bean.patientName);
        holder.setText(R.id.tv_name_info, OrderUtils.getSexStr(bean.patientSex)+" "+bean.patientAge+"岁");
        holder.setText(R.id.tv_first_diagnose, "初步诊断:"+bean.firstDiag );
        holder.setText(R.id.tv_mdt_group, "MDT小组:"+bean.mdtGroupName );
//        ImageLoader.getInstance().displayImage(bean.gpic, (ImageView) holder.getView(R.id.iv_pic),opts);
//        holder.getConvertView().setOnClickListener(new ChatGroupClickListener(po));
        holder.setText(R.id.tv_doc_manager,bean.userName);
        String text= TimeUtils.a_format.format(new Date(bean.endTime));
        holder.setText(R.id.tv_end_time,text);

        return holder.getConvertView();
    }
}
