package com.dachen.mdt.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.R;

import java.util.List;

/**
 * Created by Mcp on 2016/8/9.
 */
public class ChooseTextAdapter extends CommonAdapterV2<String> {
    String selectText;

    public ChooseTextAdapter(Context context, List<String> textList) {
        super(context,textList);
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
        notifyDataSetChanged();
    }

    public String getSelectText() {
        return selectText;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, R.layout.choose_text_item, position);
        String item=mData.get(position);
        int vis = View.INVISIBLE;
        if (TextUtils.equals(selectText, mData.get(position))) {
            vis = View.VISIBLE;
        }
        holder.setVisibility(R.id.iv_check, vis);
        holder.setText(R.id.text_view,item);
        holder.getConvertView().setOnClickListener(new TextClickListener(item ));
        return holder.getConvertView();
    }

    private class TextClickListener implements OnClickListener {
        String text;

        public TextClickListener(String text) {
            this.text = text;
        }

        @Override
        public void onClick(View v) {
            selectText = text;
            notifyDataSetChanged();
        }
    }
}
