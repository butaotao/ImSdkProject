package com.dachen.mdt.activity.order;

import android.os.Bundle;

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

/**
 * Created by Mcp on 2016/8/22.
 */
public class ChooseBaseDiseaseActivity extends BaseMdtOptionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText("选择基础疾病");
    }

    @Override
    protected void fetchInfo() {
        RequestHelperListener listener = new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                List<MdtOptionItem> list = JSON.parseArray(dataStr,  MdtOptionItem.class);
                mAdapter.update(list);
            }

            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis, msg);
                getProgressDialog().dismiss();
            }
        };
        String url = UrlConstants.getUrl(UrlConstants.GET_BASIC_DISEASE_LIST);
        Map<String, Object> reqMap = new HashMap<>();
        ImCommonRequest request = new ImCommonRequest(url, reqMap, RequestHelper.makeSucListener(false, listener), RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
    }

    @Override
    protected Class getChildClass() {
        return getClass();
    }
}
