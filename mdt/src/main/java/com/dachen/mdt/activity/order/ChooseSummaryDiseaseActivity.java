package com.dachen.mdt.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.DiseaseType;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseSummaryDiseaseActivity extends BaseActivity {

    public static final String KEY_START = "start";
    public static final String KEY_RESULT = "RESULT";

    protected ListView mListView;
    private ChooseTypeAdapter mAdapter;

    private DiseaseType mCurrent;
    private String mDisTopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_summary_disease);
        mDisTopId = getIntent().getStringExtra(AppConstants.INTENT_DISEASE_TOP_ID);
        mCurrent = (DiseaseType) getIntent().getSerializableExtra(KEY_START);

        mListView = (ListView) findViewById(R.id.list_view);
        mAdapter = new ChooseTypeAdapter(mThis);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClick);
        findViewById(R.id.right_btn).setOnClickListener(this);
        fetchInfo();
    }

    private OnItemClickListener mItemClick=new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrent=mAdapter.getItem(position);
            mAdapter.notifyDataSetChanged();

        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.right_btn){
            if(mCurrent==null)return;
            setResult(RESULT_OK,new Intent().putExtra(KEY_RESULT,mCurrent));
            finish();
        }
    }

    private void fetchInfo() {
        RequestHelperListener listener = new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                List<DiseaseType> list = JSON.parseArray(dataStr, DiseaseType.class);
                mAdapter.update(list);
            }

            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis, msg);
                getProgressDialog().dismiss();
            }
        };
        String url = UrlConstants.getUrl(UrlConstants.GET_DISEASE_WITH_TAG);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("topDiseaseId", mDisTopId);
        ImCommonRequest request = new ImCommonRequest(url, reqMap, RequestHelper.makeSucListener(false, listener), RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    private class ChooseTypeAdapter extends CommonAdapterV2<DiseaseType> {

        public ChooseTypeAdapter(Context mContext) {
            super(mContext);
        }

        public ChooseTypeAdapter(Context mContext, List<DiseaseType> mData) {
            super(mContext, mData);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = ViewHolder.get(mContext, convertView, parent, R.layout.choose_text_item, position);
            DiseaseType info = mData.get(position);
            String text = info.name;
            int arrowVis = View.GONE;
            holder.setVisibility(R.id.iv_arrow, arrowVis);
            holder.setText(R.id.text_view, text);
            int vis = View.INVISIBLE;
            if (mCurrent != null && TextUtils.equals(mCurrent.id, info.id)) {
                vis = View.VISIBLE;
            }
            holder.setVisibility(R.id.iv_check, vis);
            return holder.getConvertView();
        }
    }
}
