package com.dachen.mdt.activity.order.summary;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.adapter.OrderSummaryAdapter;
import com.dachen.mdt.entity.SummaryResult;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewOrderSummaryActivity extends BaseActivity {
    private static final int REQ_CODE_EDIT=1;

    private String mOrderId;
    private OrderSummaryAdapter mAdapter;

    @BindView(R.id.list_view)
    public ListView mListView;
    @BindView(R.id.right_btn)
    public TextView mRightBtn;
    private boolean isLeader;
    private int myStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_summary);
        ButterKnife.bind(this);
        mOrderId=getIntent().getStringExtra(AppConstants.INTENT_ORDER_ID);
        myStatus=getIntent().getIntExtra(AppConstants.INTENT_MY_ORDER_STATUS,-1);
        mAdapter=new OrderSummaryAdapter(this,new ArrayList<SummaryResult>());
        mListView.setAdapter(mAdapter);
        fetchOrderReport();
        isLeader=getIntent().getBooleanExtra(AppConstants.INTENT_IS_LEADER,false);
        if(isLeader){
            mRightBtn.setText(myStatus==2?"填写报告":"填写小结");
        }else{
            mRightBtn.setText("填写小结");
        }
    }

    @OnClick(R.id.right_btn)
    protected void goEdit(){
        Intent i=new Intent(mThis,SubmitSummaryActivity.class);
        i.putExtra(AppConstants.INTENT_ORDER_ID,mOrderId);
        i.putExtra(AppConstants.INTENT_DISEASE_TOP_ID,getIntent().getStringExtra(AppConstants.INTENT_DISEASE_TOP_ID));
        boolean isReport= isLeader&&myStatus==2;
        i.putExtra(SubmitSummaryActivity.KEY_IS_REPORT,isReport);
        startActivityForResult(i,REQ_CODE_EDIT);
    }

    private void fetchOrderReport(){
        Map<String,Object> map=new HashMap<>();
        map.put("orderId",mOrderId);
        String url=UrlConstants.getUrl(UrlConstants.GET_SUMMARY_LIST);
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                List<SummaryResult> list= JSON.parseArray(dataStr,SummaryResult.class);
                mAdapter.update(list);
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
            }
        };
        ImCommonRequest req=new ImCommonRequest(url,map, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener) );
        VolleyUtil.getQueue(mThis).add(req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQ_CODE_EDIT){
            if(resultCode==RESULT_OK){
                fetchOrderReport();
            }
        }
    }

}
