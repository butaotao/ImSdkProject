package com.dachen.mdt.activity.order.summary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.DiseaseTag;
import com.dachen.mdt.entity.MdtOptionResult.MdtOptionItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class ChooseDiseaseTagActivity extends BaseActivity {
    public static final String KEY_TYPE="type";

    @BindView(R.id.list_view)
    public ListView mListView;
    @BindView(R.id.title)
    public TextView tvTitle;
    private ChooseTagAdapter mAdapter;
    private DiseaseTag mTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list);
        ButterKnife.bind(this);
        tvTitle.setText("分期");
        MdtOptionItem type= (MdtOptionItem) getIntent().getSerializableExtra(KEY_TYPE);
        mTag= (DiseaseTag) getIntent().getSerializableExtra(AppConstants.INTENT_START_DATA);
        mAdapter=new ChooseTagAdapter(this,type.tagList);
        mListView.setAdapter(mAdapter);
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        DiseaseTag info=mAdapter.getItem(position);
        clickResult(info);
    }

    private void clickResult( DiseaseTag info){
        mTag=info;
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.right_btn)
    public void clickRight(){
        if(mTag!=null){
            setResult(RESULT_OK,new Intent().putExtra(AppConstants.INTENT_RESULT,mTag));
            finish();
        }
    }

    private class ChooseTagAdapter extends CommonAdapterV2<DiseaseTag>{

        public ChooseTagAdapter(Context mContext, List<DiseaseTag> mData) {
            super(mContext, mData);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(mContext,convertView,parent,R.layout.choose_text_item,position);
            DiseaseTag info=mData.get(position);
            holder.setText(R.id.text_view, info.name);
            int vis=View.INVISIBLE;
            if(mTag!=null&&TextUtils.equals(mTag.id,info.id)){
                vis=View.VISIBLE;
            }
            holder.setVisibility(R.id.iv_check,vis);
            return holder.getConvertView();
        }

    }
}
