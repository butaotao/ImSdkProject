package com.dachen.mdt.activity.patient;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
import com.dachen.mdt.entity.PatientOrderHistory;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.util.OrderUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientInfoActivity extends BaseActivity {
    public static final String KEY_PATIENT="patient";

    private ListView mListView;
    private TextView tvName;
    private TextView tvInfo;
    private PatientInfo mPatient;
    private PatientOrderAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        mPatient= (PatientInfo) getIntent().getSerializableExtra(KEY_PATIENT);

        tvName= (TextView) findViewById(R.id.tv_name);
        tvInfo= (TextView) findViewById(R.id.tv_info);
        mListView= (ListView) findViewById(R.id.list_view);
        mAdapter=new PatientOrderAdapter(mThis);
        mListView.setAdapter(mAdapter);
        fetchInfo();
        tvName.setText(mPatient.name);
        tvInfo.setText(OrderUtils.getPatientInfoStr(mPatient));
    }

    private void fetchInfo(){
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                List<PatientOrderHistory> list= JSON.parseArray(dataStr,PatientOrderHistory.class);
                mAdapter.update(list);
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.GET_PATIENT_ORDER);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("patientId",mPatient.id);
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }
    private class PatientOrderAdapter extends CommonAdapterV2<PatientOrderHistory>{

        public PatientOrderAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(mContext,convertView,parent,R.layout.patient_order_item,0);
            PatientOrderHistory order=getItem(position);
            holder.setText(R.id.tv_mdt_group,order.mdtGroupName);
            holder.setText(R.id.tv_purpose,OrderUtils.getMdtOptionResultText(order.target));
            holder.setText(R.id.tv_first_diagnose,OrderUtils.getMdtOptionResultText(order.firstDiag));
            holder.setText(R.id.tv_diagnose_opinion,OrderUtils.getMdtOptionResultText(order.diagSuggest));
            holder.setText(R.id.tv_treat_opinion,OrderUtils.getMdtOptionResultText(order.treatSuggest));
            holder.setText(R.id.tv_examine_opinion,OrderUtils.getMdtOptionResultText(order.checkSuggest));
            holder.setText(R.id.tv_end_time,order.realEndTime);
            return holder.getConvertView();
        }
    }
}
