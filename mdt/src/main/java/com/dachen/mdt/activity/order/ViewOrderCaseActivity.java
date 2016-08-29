package com.dachen.mdt.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.OrderDetailVO;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.util.ViewUtils;

import java.util.HashMap;
import java.util.Map;

public class ViewOrderCaseActivity extends BaseActivity {

    public static final String KEY_EDITABLE="editable";
    private View vOrderInfo;
    private String mOrderId;
    private OrderDetailVO mOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_case);
        vOrderInfo=findViewById(R.id.v_order_info);
        mOrderId=getIntent().getStringExtra(AppConstants.INTENT_ORDER_ID);
        if(!getIntent().getBooleanExtra(KEY_EDITABLE,false)){
            findViewById(R.id.right_btn).setVisibility(View.GONE);
        }
        fetchData();
    }

    private void fetchData(){
        Map<String,Object> map=new HashMap<>();
        map.put("orderId",mOrderId);
        String url= UrlConstants.getUrl(UrlConstants.GET_ORDER);
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                mOrder= JSON.parseObject(dataStr,OrderDetailVO.class);
                ViewUtils.initOrderInfo(mThis,vOrderInfo,mOrder);
            }
        };
        ImCommonRequest req=new ImCommonRequest(url,map, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener) );
        VolleyUtil.getQueue(mThis).add(req);
    }

    @Override
    public void onRightClick(View v) {
        if(mOrder==null)return;
        Intent i=new Intent(mThis,EditOrderCaseActivity.class);
        i.putExtra(EditOrderCaseActivity.KEY_ORDER,mOrder);
        startActivity(i);
        finish();
    }
}
