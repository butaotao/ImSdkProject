package com.dachen.imsdk.net;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.dachen.common.DCommonSdk;
import com.dachen.common.json.EmptyResult;
import com.dachen.common.toolbox.CommonManager;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.consts.ImConsts;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mcp on 2016/2/26.
 */
public class ImCommonRequest extends StringRequest {
    private Map<String,? extends Object> reqMap;
    private String accessToken;

    public ImCommonRequest(String url, Map<String, ? extends Object> map, Listener<String> listener, ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        this.reqMap=map;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        Map<String,? extends Object> params=reqMap;
        if(params==null)
            params=getReqMap();
        String str = JSON.toJSONString(params);
        byte[] result = null;
        try {
            result = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

//    @Override
//    protected Map<String, String> getParams() throws AuthFailureError {
//        Map<String,? extends Object> params=reqMap;
//        if(params==null)
//            params=getReqMap();
//        return VolleyUtil.makeParam(params);
//    }

    protected Map<String,? extends Object> getReqMap(){
        return new HashMap<>();
    }

    public void setAccessToken(String accessToken){
        this.accessToken=accessToken;
    }

    @Override
    protected void deliverResponse(String response) {
        EmptyResult res;
        try {
            res = JSON.parseObject(response,EmptyResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            super.deliverResponse(response);
            return;
        }
        boolean b=false;
        if(res!=null){
            if(res.resultCode== ImConsts.IM_ERROR_TOKEN_ERROR){
                b= CommonManager.onRequestTokenErr();
            }
        }
        if(!b)
            super.deliverResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers =new HashMap<>();
        if (ImSdk.getInstance().context!= null) {
            StringBuilder label = new StringBuilder();
            try {
                PackageInfo pInfo = ImSdk.getInstance().context.getPackageManager().getPackageInfo(ImSdk.getInstance().context.getPackageName(), 0);
                if(!TextUtils.isEmpty(DCommonSdk.reqLabel)){
                    label.append(DCommonSdk.reqLabel).append("/");
                }
                label.append(pInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
            }
            label.append("/");
            label.append( "android_"+System.getProperty("http.agent"));
            headers.put("User-Agent", label.toString());

        }
        String token=accessToken==null? ImSdk.getInstance().accessToken:accessToken;
        headers.put("access-token", token);
//        headers.put("content-type", "application/json");
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }
}
