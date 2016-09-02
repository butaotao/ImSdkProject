package com.dachen.mdt.activity.order;

import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.entity.MdtOptionResult.MdtOptionItem;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseDiseaseTypeActivity extends BaseMdtOptionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText("选择病种");
    }

    @Override
    protected void clickRight() {
        if(currentData.array.size()==0&&mParent!=null){
            currentData.array.add(mParent);
        }
        super.clickRight();
    }

    @Override
    protected boolean hasFooterEdit() {
        return mParent!=null;
    }

    @Override
    protected void initData() {
//        isMulti=false;
    }

    @Override
    protected void onClickOption(MdtOptionItem item) {
        if(currentData.array.size()>0){
            String oldTopId=currentData.array.get(0).topDiseaseId;
            if(!TextUtils.equals(item.topDiseaseId,oldTopId)){
                currentData.array.clear();
                initStartDataMap();
            }
        }
        super.onClickOption(item);
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
        String url = UrlConstants.getUrl(UrlConstants.GET_DISEASE_TYPES);
        Map<String, Object> reqMap = new HashMap<>();
        ImCommonRequest request = new ImCommonRequest(url, reqMap, RequestHelper.makeSucListener(false, listener), RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    @Override
    protected Class getChildClass() {
        return ChooseDiseaseTypeActivity.class;
    }


}
