package com.dachen.mdt.activity.order;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.main.CommonListActivity;

import java.util.List;

public class ChooseOrderTimeActivity extends CommonListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class TimeAdapter extends CommonAdapterV2<String> {
        public int chooseIndex=-1;
        public TimeAdapter(Context context, List<String> textList) {
            super(context, textList);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = ViewHolder.get(mContext, convertView, parent, R.layout.choose_text_item, position);
            String item = mData.get(position);
            int vis = View.INVISIBLE;
            if (position==chooseIndex) {
                vis = View.VISIBLE;
            }
            holder.setVisibility(R.id.iv_check, vis);
            holder.setText(R.id.text_view, item);
            return holder.getConvertView();
        }

    }



}
