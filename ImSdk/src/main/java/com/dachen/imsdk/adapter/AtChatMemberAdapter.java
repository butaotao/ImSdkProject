package com.dachen.imsdk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.dachen.common.adapter.ViewHolder;
import com.dachen.imsdk.R;
import com.dachen.imsdk.entity.GroupInfo2Bean.Data.UserInfo;
import com.dachen.imsdk.utils.ImUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Mcp on 2016/6/3.
 */
public class AtChatMemberAdapter extends BaseAdapter {

    private List<UserInfo> memberList;
    private Context context;

    public AtChatMemberAdapter(Context context, List<UserInfo> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    @Override
    public int getCount() {
        return memberList.size();
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
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.vchat_member_item, position);
        convertView = holder.getConvertView();
        RadioButton rb = holder.getView(R.id.btn_radio);
        final UserInfo user = memberList.get(position);
        ImageLoader.getInstance().displayImage(user.pic, (ImageView) holder.getView(R.id.img), ImUtils.getNormalImageOptions());
        holder.setText(R.id.title, user.name);
        rb.setVisibility(View.GONE);
        return convertView;
    }
}
