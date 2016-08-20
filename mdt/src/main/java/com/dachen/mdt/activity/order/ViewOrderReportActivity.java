package com.dachen.mdt.activity.order;

import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.TimeUtils;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.MDTReportVO;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewOrderReportActivity extends BaseActivity {

    @BindView(R.id.v_report)
    View v;

    private String mOrderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_report2);
        ButterKnife.bind(this);
        mOrderId=getIntent().getStringExtra(AppConstants.INTENT_ORDER_ID);
        v.setVisibility(View.INVISIBLE);
        fetchOrderReport();
    }


    private void fetchOrderReport(){
        Map<String,Object> map=new HashMap<>();
        map.put("orderId",mOrderId);
        String url= UrlConstants.getUrl(UrlConstants.GET_MDT_REPORT);
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                MDTReportVO res= JSON.parseObject(dataStr,MDTReportVO.class);
                refreshReport(res);
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
            }
        };
        ImCommonRequest req=new ImCommonRequest(url,map, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener) );
        VolleyUtil.getQueue(mThis).add(req);
    }

    private void refreshReport(MDTReportVO item){
        v.setVisibility(View.VISIBLE);
        ViewHolder holder= new ViewHolder(mThis,v);
        holder.setText(R.id.tv_doc_manager,item.userName);
        holder.setText(R.id.tv_patient_info,item.patient.name);
        holder.setText(R.id.tv_mdt_group,item.mdtGroupName);
        holder.setText(R.id.tv_purpose,item.target);
        holder.setText(R.id.tv_first_diagnose,item.firstDiag);
        holder.setText(R.id.tv_diagnose_opinion,item.diagSuggest);
        holder.setText(R.id.tv_examine_opinion,item.checkSuggest);
        holder.setText(R.id.tv_treat_opinion,item.treatSuggest);
        holder.setText(R.id.tv_other,item.other);
        holder.setText(R.id.tv_end_time, TimeUtils.a_format.format(new Date(item.realEndTime)));

    }
}
