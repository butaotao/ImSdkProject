package com.dachen.imsdk.service;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dachen.common.async.SimpleResultListener;
import com.dachen.common.json.EmptyResult;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.net.PollingURLs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mcp on 2016/6/6.
 */
public class ImRequestManager {

    public static void clearGroupMsg(String groupId, final SimpleResultListener listener){
        Map<String,Object> reqMap=new HashMap<>();
        reqMap.put("gid",groupId);
        final Listener<String> lis=new Listener<String>() {
            @Override
            public void onResponse(String s) {
                EmptyResult result= JSON.parseObject(s,EmptyResult.class);
                if(result==null)listener.onError("请求出错");
                if(result.resultCode==1)
                    listener.onSuccess();
                else
                    listener.onError(result.detailMsg);
            }
        };
        ErrorListener errorListener=new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError("网络故障");
            }
        };
        ImCommonRequest req=new ImCommonRequest(PollingURLs.clearGroupMsg(),reqMap,lis,errorListener);
        VolleyUtil.getQueue(ImSdk.getInstance().context).add(req);
    }

    public static void forwardMsg(String msgId,String groupId,int index, final SimpleResultListener listener){
        Map<String,Object> reqMap=new HashMap<>();
        reqMap.put("msgId",msgId);
        reqMap.put("gid",groupId);
        reqMap.put("index",index);
        final Listener<String> lis=new Listener<String>() {
            @Override
            public void onResponse(String s) {
                EmptyResult result= JSON.parseObject(s,EmptyResult.class);
                if(result==null)listener.onError("请求出错");
                if(result.resultCode==1)
                    listener.onSuccess();
                else
                    listener.onError(result.detailMsg);
            }
        };
        ErrorListener errorListener=new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError("网络故障");
            }
        };
        StringRequest req=new ImCommonRequest(PollingURLs.forwardMsg(),reqMap,lis,errorListener);
        VolleyUtil.getQueue(ImSdk.getInstance().context).add(req);
    }
}