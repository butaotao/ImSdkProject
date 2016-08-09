package com.dachen.mdt.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.dachen.mdt.entity.PatientType;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class ChooseMdtPatientTypeActivity extends BaseActivity {

    public static final String KEY_RESULT="result";
    public static final String KEY_TYPE_ID="type_id";
    @BindView(R.id.list_view)
    public ListView mListView;

    private String mdtGroupId;
    private String mTypeId;
    private ChoosePatientTypeAdapter mAdapter;
    private PatientType mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mdt_patient_type);
        ButterKnife.bind(this);

        mdtGroupId=getIntent().getStringExtra(AppConstants.INTENT_MDT_GROUP_ID);
        mTypeId=getIntent().getStringExtra(KEY_TYPE_ID);
        mAdapter=new ChoosePatientTypeAdapter(this,null);
        mListView.setAdapter(mAdapter);
        fetchInfo();
    }

    private void fetchInfo(){
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                List<PatientType> list= JSON.parseArray(dataStr,PatientType.class);
                mAdapter.update(list);
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.GET_DISEASE_TYPES);
        Map<String,Object> reqMap=new HashMap<>();
        reqMap.put("mdtGroupId",mdtGroupId);
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        mTypeId=mAdapter.getItem(position).id;
        mAdapter.notifyDataSetChanged();
        mResult =mAdapter.getItem(position);
    }


    @OnClick(R.id.right_btn)
    public void clickRight(){
        if(mResult !=null){
            setResult(RESULT_OK,new Intent().putExtra(KEY_RESULT, mResult)
                    .putExtra(AppConstants.INTENT_VIEW_ID,getIntent().getIntExtra(AppConstants.INTENT_VIEW_ID,0)) );
            finish();
        }
    }
    private class ChoosePatientTypeAdapter extends CommonAdapterV2<PatientType> {

        public ChoosePatientTypeAdapter(Context mContext, List<PatientType> mData) {
            super(mContext, mData);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(mContext,convertView,parent,R.layout.choose_text_item,position);
            PatientType info=mData.get(position);
            holder.setText(R.id.text_view,info.name);
            int vis=View.INVISIBLE;
            if(TextUtils.equals(mTypeId,info.id)){
                vis=View.VISIBLE;
            }
            holder.setVisibility(R.id.iv_check,vis);
            return holder.getConvertView();
        }

    }
}
