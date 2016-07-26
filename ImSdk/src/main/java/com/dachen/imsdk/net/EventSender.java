package com.dachen.imsdk.net;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.medicine.entity.Result;
import com.dachen.medicine.volley.custom.ObjectResult;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令发送类
 *
 * @author gaozhuo
 * @date 2016/1/21
 */
public class EventSender {
    private static EventSender mInstance;
    private static Context mContext;


    private EventSender() {

    }

    private EventSender(Context context) {

    }

    public static EventSender getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new EventSender(context.getApplicationContext());
        }
        return mInstance;
    }

    public interface OnResultListener {
        void onResult(boolean isSuccess);
    }

    /**
     * 发送指令 用户ID用 | 来间隔
     *
     * @param eventType
     * @param toUserId
     * @param param
     * @param listener
     */
    public void sendEvent(int eventType, String toUserId, Map<String, String> param, final OnResultListener listener) {
        sendEvent(eventType, toUserId, param, listener,0);
    }

    public void sendEvent(int eventType, String toUserId, Map<String, String> param, final OnResultListener listener,int expireSec) {
        if(param == null){
            param = new HashMap<String, String>();
        }
        final String reqTag = "sendEvent";
        RequestQueue queue = VolleyUtil.getQueue(mContext);
        queue.cancelAll(reqTag);
        SendEventRequest request = new SendEventRequest(eventType, toUserId, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ObjectResult<Void> result = JSON.parseObject(response, new TypeReference<ObjectResult<Void>>() {
                });

                if (result == null || result.getResultCode() != Result.CODE_SUCCESS) {
                    if (listener != null) {
                        listener.onResult(false);
                    }
                } else {
                    if (listener != null) {
                        listener.onResult(true);
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) {
                    listener.onResult(false);
                }

            }
        });
        request.setExpireSec(expireSec);
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(reqTag);
        queue.add(request);
    }

    /**
     * 发送指令，不带结果监听  用户ID用 | 来间隔
     *
     * @param eventType
     * @param toUserId
     * @param param
     */
    public void sendEvent(int eventType, String toUserId, Map<String, String> param) {
        sendEvent(eventType, toUserId, param, null);
    }


    /**
     * 发送指令，不带参数  用户ID用 | 来间隔
     *
     * @param eventType
     * @param toUserId
     * @param listener
     */
    public void sendEvent(int eventType, String toUserId, final OnResultListener listener) {
        sendEvent(eventType, toUserId, null, listener);
    }

    /**
     * 发送指令，不带参数，不带结果监听器  用户ID用 | 来间隔
     *
     * @param eventType
     * @param toUserId
     */
    public void sendEvent(int eventType, String toUserId) {
        sendEvent(eventType, toUserId, null, null);
    }
}
