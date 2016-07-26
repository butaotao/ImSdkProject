package com.dachen.imsdk.net;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.dachen.common.json.EmptyResult;
import com.dachen.common.toolbox.CommonManager;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.consts.ImConsts;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
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

   /* @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> m = new HashMap<String, String>();
        String token=accessToken==null? ImSdk.getInstance().accessToken:accessToken;
        m.put("access-token", token);
        m.put("content-type", "application/json");
        return m;
    }*/

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

    protected Map<String,? extends Object> getReqMap(){
        return new HashMap<>();
    }

    public void setAccessToken(String accessToken){
        this.accessToken=accessToken;
    }

    @Override
    protected void deliverResponse(String response) {
        EmptyResult res=JSON.parseObject(response,EmptyResult.class);
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
        Map<String, String> headers = super.getHeaders();
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        if (ImSdk.getInstance().context!= null) {
            StringBuilder label = new StringBuilder();
            try {
                PackageInfo pInfo = ImSdk.getInstance().context.getPackageManager().getPackageInfo(ImSdk.getInstance().context.getPackageName(), 0);
                if(pInfo.packageName.equals("com.dachen.dgroupdoctor")){
                    label.append("DGroupDoctor");
                    label.append("/");
                }else if(pInfo.packageName.equals("com.dachen.dgrouppatient")){
                    label.append("DGroupPatient");
                    label.append("/");
                }else if(pInfo.packageName.equals("com.bestunimed.dgroupdoctor")){
                    label.append("DGroupDoctor_BDJL");
                    label.append("/");
                }else if(pInfo.packageName.equals("com.bestunimed.dgrouppatient")){
                    label.append("DGroupPatient_BDJL");
                    label.append("/");
                }
                label.append(pInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
            }
            label.append("/");
            label.append(System.getProperty("http.agent"));
            headers.put("User-Agent", label.toString());

        }
        String token=accessToken==null? ImSdk.getInstance().accessToken:accessToken;
        headers.put("access-token", token);
        headers.put("content-type", "application/json");
        return headers;
    }
}
