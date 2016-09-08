package com.dachen.mdt.net;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dachen.common.async.SimpleResultListenerV2;
import com.dachen.common.json.ResultTemplate;
import com.dachen.common.utils.DeviceInfoUtil;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.service.ImRequestManager;
import com.dachen.mdt.MyApplication;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.me.VersionAlertActivity;
import com.dachen.mdt.entity.VersionInfo;
import com.dachen.mdt.listener.RequestHelperListener;

/**
 * Created by Mcp on 2016/8/9.
 */
public class RequestHelper {

    public static Listener<String> makeSucListener(final boolean resultCanEmpty, final RequestHelperListener mListener){
        return new Listener<String>() {
            @Override
            public void onResponse(String s) {
//                Logger.w("RequestHelper","RequestHelper res:"+ s);
                ResultTemplate<String >res= JSON.parseObject(s,new TypeReference<ResultTemplate<String>>(){});
                if(res.resultCode!=1){
                    mListener.onError(res.detailMsg);
                    return;
                }
                if(!resultCanEmpty&& TextUtils.isEmpty(res.data)){
                    mListener.onError("请求结果有误");
                    return;
                }
                mListener.onSuccess(res.data);
            }
        };
    }
    public static ErrorListener makeErrorListener(final RequestHelperListener mListener){
        return new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mListener.onError("请求失败");
            }
        };
    }

    public static void checkVersion(){
        final Context mThis= MyApplication.getInstance();
        SimpleResultListenerV2 listener=new SimpleResultListenerV2() {
            @Override
            public void onSuccess(String dataStr) {
                VersionInfo info= JSON.parseObject(dataStr,VersionInfo.class);
                String curVersion= DeviceInfoUtil.getVersionName(mThis);
                if(info.version.compareTo(curVersion)>0){
                    VersionAlertActivity.openUi(mThis,info);
                }else{
                    ToastUtil.showToast(mThis,"当前版本已是最新");
                }
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.GET_VERSION);
        ImCommonRequest request=new ImCommonRequest(url,null, ImRequestManager.makeSucListener(listener),ImRequestManager.makeErrListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
    }
}
