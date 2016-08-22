package com.dachen.mdt.activity.order;

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

    private View vOrderInfo;
    private String mOrderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_case);
        vOrderInfo=findViewById(R.id.v_order_info);
        mOrderId=getIntent().getStringExtra(AppConstants.INTENT_ORDER_ID);
        fetchData();
    }

    private void fetchData(){
        Map<String,Object> map=new HashMap<>();
        map.put("orderId",mOrderId);
        String url= UrlConstants.getUrl(UrlConstants.GET_ORDER);
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                OrderDetailVO res= JSON.parseObject(dataStr,OrderDetailVO.class);
                ViewUtils.initOrderInfo(mThis,vOrderInfo,res);
            }
        };
        ImCommonRequest req=new ImCommonRequest(url,map, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener) );
        VolleyUtil.getQueue(mThis).add(req);
    }
}
