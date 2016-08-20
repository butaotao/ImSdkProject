package com.dachen.mdt.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.PatientInfo;
import com.dachen.mdt.entity.PatientListResult;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.util.OrderUtils;

import java.util.HashMap;
import java.util.Map;

public class ChoosePatientActivity extends BaseActivity {

    protected ListView mListView;
    private PatientAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_patient);

       initView();
        fetchInfo();
    }
    private void initView(){
        View vHeader= LayoutInflater.from(this).inflate(R.layout.choose_patient_header,null);
        vHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goStartOrder(null);
            }
        });

        mListView= (ListView) findViewById(R.id.list_view);
        mListView.addHeaderView(vHeader);
        mAdapter=new PatientAdapter(mThis);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(itemClick);
    }

    private OnItemClickListener itemClick=new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            position-=mListView.getHeaderViewsCount();
            goStartOrder(mAdapter.getItem(position));
        }
    };
    private void goStartOrder(PatientInfo info){
        Intent i=new Intent(mThis,EditOrderCaseActivity.class);
        i.putExtra(EditOrderCaseActivity.KEY_PATIENT,info);
        startActivity(i);
        finish();
    }

    private void fetchInfo(){
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                PatientListResult result=JSON.parseObject(dataStr,PatientListResult.class);
//                List<PatientInfo> list= JSON.parseArray(dataStr,PatientInfo.class);
                mAdapter.update(result.result);
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };
//        String url= UrlConstants.getUrl(UrlConstants.GET_ALL_PATIENTS);
        String url= UrlConstants.getUrl(UrlConstants.GET_TAG_PATIENTS);
        Map<String, Object> reqMap = new HashMap<>();
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }


    private class PatientAdapter extends CommonAdapterV2<PatientInfo>{

        public PatientAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(mContext,convertView,parent,R.layout.choose_patient_item,position);
            PatientInfo info=getItem(position);
            holder.setText(R.id.tv_name,info.name);
            holder.setText(R.id.tv_info, OrderUtils.getPatientInfoStr(info));
            return holder.getConvertView();
        }

    }
}
