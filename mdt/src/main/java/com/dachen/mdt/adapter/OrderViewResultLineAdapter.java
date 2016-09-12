package com.dachen.mdt.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.R;
import com.dachen.mdt.entity.CheckItem;

import java.util.List;

/**
 * Created by Mcp on 2016/8/17.
 */
public class OrderViewResultLineAdapter extends CommonAdapterV2<CheckItem> {


    public OrderViewResultLineAdapter(Context mContext, List<CheckItem> mData) {
        super(mContext, mData);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=ViewHolder.get(mContext,convertView,parent, R.layout.check_result_item_line,position);
        CheckItem item=mData.get(position);
        holder.setText(R.id.tv_name,item.name);
        holder.setText(R.id.tv_value,item.value);
        holder.setText(R.id.tv_unit,item.unit);
        return holder.getConvertView();
    }
}
