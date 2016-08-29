package com.dachen.mdt.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.main.CommonListActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChooseOrderTimeActivity extends CommonListActivity {

    private TimeAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter=new TimeAdapter(this,makeOpts());
        tvTitle.setText("会诊时间");
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(itemClickListener);
    }
    private ArrayList<String> makeOpts(){
        ArrayList<String> list=new ArrayList<>();
        for(int i=0;i<2;i++){
            list.add(i+1+"天");
        }
        return list;
    }

    @Override
    public void onRightClick(View v) {
        if(mAdapter.chooseIndex>=0){
            Calendar c=Calendar.getInstance();
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,0);
            c.set(Calendar.MILLISECOND,0);
            c.add(Calendar.DAY_OF_YEAR,mAdapter.chooseIndex+1);
            Intent i=new Intent().putExtra(AppConstants.INTENT_RESULT,c.getTimeInMillis());
            setResult(RESULT_OK,i);
            finish();
        }
    }

    private OnItemClickListener itemClickListener=new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mAdapter.chooseIndex=position;
            mAdapter.notifyDataSetChanged();
        }
    };

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
