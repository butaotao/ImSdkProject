package com.dachen.imsdk.net;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.dachen.common.toolbox.DCommonRequest;
import com.dachen.common.utils.Logger;
import com.dachen.imsdk.ImSdk;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 发送推送的请求
 *
 * @author gaozhuo
 * @date 2016/1/25
 */
public class SendPushRequest extends DCommonRequest {
    private static final String TAG = "SendPushRequest";
    private String content;
    private String[] pushUsers;
    private Map<String, String> param;
    private boolean isPassThrough;

    public SendPushRequest(String content, String[] pushUsers, Map<String, String> param, boolean isPassThrough, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(ImSdk.getInstance().context,Method.POST, PollingURLs.sendPush(), listener, errorListener);
        this.content = content;
        this.pushUsers = pushUsers;
        this.param = param;
        this.isPassThrough = isPassThrough;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> m = new HashMap<String, String>();
        m.put("access-token", ImSdk.getInstance().accessToken);
        m.put("content-type", "application/json");
        return m;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("pushUsers", pushUsers);
        m.put("content", content);
        if(isPassThrough){//透传消息
            m.put("passThrough", 1);
        }else {//通知消息
            m.put("passThrough", 0);
        }
        m.put("param", param);
        String str = JSON.toJSONString(m);
        Logger.d(TAG, "event send req=" + str);
        byte[] result = null;
        try {
            result = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
