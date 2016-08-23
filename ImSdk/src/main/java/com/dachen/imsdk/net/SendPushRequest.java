package com.dachen.imsdk.net;

import com.android.volley.Response;
import com.dachen.common.utils.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送推送的请求
 *
 * @author gaozhuo
 * @since 2016/1/25
 */
public class SendPushRequest extends ImCommonRequest {
    private static final String TAG = "SendPushRequest";
    private String content;
    private String[] pushUsers;
    private Map<String, String> param;
    private boolean isPassThrough;

    public SendPushRequest(String content, String[] pushUsers, Map<String, String> param, boolean isPassThrough, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(PollingURLs.sendPush(),null, listener, errorListener);
        this.content = content;
        this.pushUsers = pushUsers;
        this.param = param;
        this.isPassThrough = isPassThrough;
    }

    @Override
    protected Map<String, ? extends Object> getReqMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("pushUsers", pushUsers);
        m.put("content", content);
        if(isPassThrough){//透传消息
            m.put("passThrough", 1);
        }else {//通知消息
            m.put("passThrough", 0);
        }
        m.put("param", param);
        Logger.d(TAG, "event send req=" + m);
        return m;
    }

//    @Override
//    public byte[] getBody() throws AuthFailureError {
//        Map<String, Object> m = new HashMap<String, Object>();
//        m.put("pushUsers", pushUsers);
//        m.put("content", content);
//        if(isPassThrough){//透传消息
//            m.put("passThrough", 1);
//        }else {//通知消息
//            m.put("passThrough", 0);
//        }
//        m.put("param", param);
//        String str = JSON.toJSONString(m);
//        Logger.d(TAG, "event send req=" + str);
//        byte[] result = null;
//        try {
//            result = str.getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}
