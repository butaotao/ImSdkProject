package com.dachen.mdt.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.R;
import com.dachen.mdt.entity.ImageInfo;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.util.List;

/**
 * Created by Mcp on 2016/8/11.
 */
public class ViewImgGridAdapter extends CommonAdapterV2<ImageInfo> {
    private String smallSuffix;

    public void setSmallSuffix(String smallSuffix) {
        this.smallSuffix = smallSuffix;
    }

    public ViewImgGridAdapter(Context mContext, List<ImageInfo> mData) {
        super(mContext, mData);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext,convertView,parent, R.layout.up_img_grid_item,position);
        holder.setVisibility(R.id.btn_delete, View.GONE);
        holder.setVisibility(R.id.tv_state, View.GONE);
        String url=getItem(position).path;
        if(smallSuffix!=null)
            url+=smallSuffix;
//        ImageLoader.getInstance().displayImage(url, (ImageView) holder.getView(R.id.iv_pic));
        Ion.with((ImageView) holder.getView(R.id.iv_pic)).animateGif(AnimateGifMode.NO_ANIMATE).load(url);
        return holder.getConvertView();
    }
}
