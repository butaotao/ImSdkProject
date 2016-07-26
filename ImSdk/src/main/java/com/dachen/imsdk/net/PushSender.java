package com.dachen.imsdk.net;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dachen.common.utils.Logger;
import com.dachen.common.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送推送消息
 *
 * @author gaozhuo
 * @date 2016/1/25
 */
public class PushSender {
    private static final String TAG = PushSender.class.getSimpleName();
    private static PushSender mInstance;
    private static Context mContext;


    private PushSender() {

    }

    private PushSender(Context context) {
        mContext=context;
    }

    public static PushSender getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PushSender(context.getApplicationContext());
        }
        return mInstance;
    }


    /**
     * 发送推送消息（通知消息）
     *
     * @param content
     * @param pushUsers
     * @param param
     */
    public void sendPushMessage(String content, String[] pushUsers, Map<String, String> param) {
        sendPushMessage(content, pushUsers, param, false);
    }

    /**
     * 发送推送消息
     *
     * @param content
     * @param pushUsers
     * @param param
     * @param isPassThrough true-透传消息；false-通知消息
     */
    public void sendPushMessage(String content, String[] pushUsers, Map<String, String> param, boolean isPassThrough) {
        if (param == null) {
            param = new HashMap<String, String>();
        }
        final String reqTag = "sendPushMessage";
        RequestQueue queue = VolleyUtil.getQueue(mContext);
        queue.cancelAll(reqTag);
        SendPushRequest request = new SendPushRequest(content, pushUsers, param, isPassThrough, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.d(TAG, "response=" + response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.d(TAG, "response=" + error.getMessage());
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(reqTag);
        queue.add(request);
    }
}
