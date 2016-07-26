package com.dachen.imsdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dachen.common.json.ResultTemplate;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.net.PollingURLs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mcp on 2016/4/25.
 */
public class PushUtils {

    public static void registerDevice(int userType,String deviceToken,String appType){
        registerDevice(userType, deviceToken, appType,false);
    }
    public static void registerDevice(int userType,String deviceToken,String appType,boolean isHuawei){
        Map<String,Object> map=new HashMap<>();
        map.put("userType",userType);
        if(isHuawei){
            map.put("client","android/huawei");
        }else{
            map.put("client","android");
        }
        map.put("deviceToken",deviceToken);
        map.put("deviceVer",appType);
        ImCommonRequest request=new ImCommonRequest(PollingURLs.registerDevice(),map,VolleyUtil.getEmptyListener(),null);
        RequestQueue queue = VolleyUtil.getQueue(ImSdk.getInstance().context);
        queue.add(request);
    }
    public static void removeDevice( String deviceToken){
        removeDevice(deviceToken,VolleyUtil.getEmptyListener());
    }
    public static void removeDevice( String deviceToken,Listener<String> listener){
        Map<String,Object> map=new HashMap<>();
        map.put("deviceToken",deviceToken);
        ImCommonRequest request=new ImCommonRequest(PollingURLs.removeDevice(),map,listener,null);
        request.setAccessToken(ImSdk.getInstance().accessToken);
        RequestQueue queue = VolleyUtil.getQueue(ImSdk.getInstance().context);
        queue.add(request);
    }
    public static void getPushSetting(final OnGetPushSettingListener l){
        final Map<String,Object> map=new HashMap<>();
        Listener<String> lis=new Listener<String>() {
            @Override
            public void onResponse(String s) {
                ResultTemplate<PushSettingBean> res= JSON.parseObject(s,new TypeReference<ResultTemplate<PushSettingBean>>(){});
                if(res.resultCode==1&&res.data!=null){
                    l.onResult(res.data);
                }else{
                    l.onError();
                }
            }
        };
        ErrorListener errorListener=new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                l.onError();
            }
        };
        ImCommonRequest request=new ImCommonRequest(PollingURLs.getPushSetting(),map,lis,errorListener);
        RequestQueue queue = VolleyUtil.getQueue(ImSdk.getInstance().context);
        queue.add(request);
    }

    public static void setPushSetting(boolean push, Listener<String> listener, ErrorListener errorListener){
        Map<String,Object> map=new HashMap<>();
        map.put("invalid",!push);
        ImCommonRequest request=new ImCommonRequest(PollingURLs.setPushSetting(),map,listener,errorListener);
        RequestQueue queue = VolleyUtil.getQueue(ImSdk.getInstance().context);
        queue.add(request);
    }

    public static class PushSettingBean{
        public boolean receivePush;
    }
    public interface OnGetPushSettingListener{
        void onResult(PushSettingBean bean);
        void onError();
    }
}
