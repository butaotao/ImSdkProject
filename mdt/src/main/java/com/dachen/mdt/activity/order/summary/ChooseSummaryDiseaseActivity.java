package com.dachen.mdt.activity.order.summary;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.order.BaseMdtOptionActivity;
import com.dachen.mdt.entity.MdtOptionResult.MdtOptionItem;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.util.OrderUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseSummaryDiseaseActivity extends BaseMdtOptionActivity {

    private String mDisTopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText("选择病种");

        fetchInfo();
    }

    @Override
    protected void initData() {
        mDisTopId = getIntent().getStringExtra(AppConstants.INTENT_DISEASE_TOP_ID);
        isMulti=false;
    }

    @Override
    protected void clickRight() {
        if(currentData==null|| OrderUtils.isMdtResultEmpty(currentData))return;
        super.clickRight();
    }

    protected void fetchInfo() {
        RequestHelperListener listener = new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                List<MdtOptionItem> list = JSON.parseArray(dataStr, MdtOptionItem.class);
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

}
