package com.dachen.mdt.net;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.dachen.imsdk.net.ImCommonRequest;

import java.io.UnsupportedEncodingException;

/**
 * Created by Mcp on 2016/8/9.
 */
public class ImObjectRequest extends ImCommonRequest {
    private  Object reqObj;

    public ImObjectRequest(String url, Object obj, Listener<String> listener, ErrorListener errorListener) {
        super(url, null, listener, errorListener);
        this.reqObj=obj;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if(reqObj==null)
            return null;
        String str = JSON.toJSONString(reqObj);
        byte[] result = null;
        try {
            result = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
