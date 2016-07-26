package com.dachen.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dachen.imsdk.R;

import java.util.List;

/**
 * Created by Mcp on 2016/3/31.
 */
public class GalleryFolderAdapter extends BaseAdapter {
    private List<CustomGalleryFolder> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    public GalleryFolderAdapter(List<CustomGalleryFolder> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mInflater=LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
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
            convertView=mInflater.inflate(R.layout.gallery_folder_item,parent,false);
        }
        TextView tv= (TextView) convertView.findViewById(R.id.text_view);
        CustomGalleryFolder item=mList.get(position);
        tv.setText(item.name);
        return convertView;
    }
}
