package com.dachen.mdt.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.R;
import com.dachen.mdt.adapter.UpImgGridAdapter.UpImgGridItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by Mcp on 2016/8/11.
 */
public class UpImgGridAdapter extends CommonAdapterV2<UpImgGridItem> {
    private int maxNum = 8;
    private UpImgGridItem mAddItem;

    public UpImgGridAdapter(Context mContext) {
        super(mContext);
        mAddItem=new UpImgGridItem();
        mAddItem.isAdd=true;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    @Override
    public int getCount() {
        return mData.size() >= maxNum ? maxNum : mData.size()+ 4;
    }

    @Override
    public UpImgGridItem getItem(int position) {
        if(position<mData.size())
            return mData.get(position);
        return mAddItem;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext,convertView,parent, R.layout.up_img_grid_item,position);
        UpImgGridItem item=getItem(position);
        if(item.isAdd){
            ImageLoader.getInstance().displayImage( "drawable://"+R.drawable.vchat_add , (ImageView) holder.getView(R.id.iv_pic));
            holder.setVisibility(R.id.btn_delete,View.INVISIBLE);

        }else{
            holder.setVisibility(R.id.btn_delete,View.VISIBLE);
            ImageLoader.getInstance().displayImage( Uri.fromFile(new File(item.imgPath)).toString(), (ImageView) holder.getView(R.id.iv_pic));
        }
        return holder.getConvertView();
    }

    public static class UpImgGridItem {
        public boolean isAdd;
        public String imgPath;
    }
}
