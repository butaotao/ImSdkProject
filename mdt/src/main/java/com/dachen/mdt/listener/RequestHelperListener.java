package com.dachen.mdt.listener;

import com.dachen.common.utils.ToastUtil;
import com.dachen.imsdk.ImSdk;

/**
 * Created by Mcp on 2016/8/9.
 */
public class RequestHelperListener {

    public void onSuccess(String dataStr){
    }
    public void onError(String msg){
        ToastUtil.showToast(ImSdk.getInstance().context,msg);
    }
}
