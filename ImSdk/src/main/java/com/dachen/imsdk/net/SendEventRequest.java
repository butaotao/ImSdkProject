package com.dachen.imsdk.net;

import com.android.volley.Response;
import com.dachen.common.utils.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mcp on 2016/1/19.
 */
public class SendEventRequest extends ImCommonRequest {
    private static final String TAG = "SendEventRequest";
    private int eventType;
    private String toUserId;
    Map<String, String> param;
    private int expireSec;

    public SendEventRequest(int eventType, String toUserId, Map<String, String> param, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(PollingURLs.sendEvent(),null, listener, errorListener);
        this.eventType = eventType;
        this.toUserId = toUserId;
        this.param = param;
    }
    public void setExpireSec(int sec){
        expireSec=sec;
    }

    @Override
    protected Map<String, ? extends Object> getReqMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("eventType", eventType);
        m.put("userId", toUserId);
        m.put("param", param);
        m.put("expires", expireSec);
        Logger.d(TAG, "event send req=" + m);
        return m;
    }
//    @Override
//    public byte[] getBody() throws AuthFailureError {
//        Map<String, Object> m = new HashMap<String, Object>();
//        m.put("eventType", eventType);
//        m.put("userId", toUserId);
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
