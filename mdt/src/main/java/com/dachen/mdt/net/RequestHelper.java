package com.dachen.mdt.net;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dachen.common.json.ResultTemplate;
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
}
