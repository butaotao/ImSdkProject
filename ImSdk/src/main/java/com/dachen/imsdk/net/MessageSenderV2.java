package com.dachen.imsdk.net;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dachen.common.utils.Logger;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.consts.MessageType;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.ChatMessageV2.AtMsgParam;
import com.dachen.imsdk.entity.ImgTextMsgV2;
import com.dachen.imsdk.entity.SendMessageResult;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息发送类
 *
 * @author mcp
 * @since 2015年12月10日
 */
public class MessageSenderV2 {

    private static String TAG = MessageSenderV2.class.getSimpleName();

    // 超时时间
    private static final int TIMEOUT = 20 * 1000;
    // HTTP轮询时间
    private static MessageSenderV2 mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;
    private MessageSendCallbackV2 mMessageSendCallbackListener;

    public interface MessageSendCallbackV2 {
        void sendSuccessed(ChatMessagePo msg, String groudId, String msgId, long time);

        void sendFailed(ChatMessagePo msg, int resultCode, String resultMsg);
    }

    private MessageSenderV2() {
    }

    private MessageSenderV2(Context context) {
        mContext = context;
        mRequestQueue = VolleyUtil.getQueue(mContext);
    }

    /**
     * 得到实例
     *
     * @return
     */
    public static MessageSenderV2 getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MessageSenderV2.class) {
                // 单例生命周期很长，使用getApplicationContext()避免内存泄露
                mInstance = new MessageSenderV2(context.getApplicationContext());
            }
        }
        return mInstance;
    }

    public void setMessageSendCallbackListener(MessageSendCallbackV2 listener) {
        this.mMessageSendCallbackListener = listener;
    }

    /**
     * 发送消息
     *
     * @param chatMessage
     */
    public void sendMessage(final ChatMessagePo chatMessage) {
        Map<String, Object> params = makeParams(chatMessage);
        Logger.d(TAG, "send params=" + params.toString());
        StringRequest request = new ImCommonRequest(PollingURLs.sendMessageV2(),params, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.d(TAG, "------------------->send message response=" + response);

                SendMessageResult result = JSON.parseObject(response, SendMessageResult.class);
                if (result == null) {
                    if (mMessageSendCallbackListener != null) {
                        mMessageSendCallbackListener.sendFailed(chatMessage, -1, "");
                    }
                    return;
                }

                if (result.resultCode != 1) {
                    if (mMessageSendCallbackListener != null) {
                        mMessageSendCallbackListener.sendFailed(chatMessage, result.resultCode, result.resultMsg);
                    }
                    return;
                }

                if (result.data == null) {
                    if (mMessageSendCallbackListener != null) {
                        mMessageSendCallbackListener.sendFailed(chatMessage, -1, "");
                    }
                    return;
                }

                if (mMessageSendCallbackListener != null) {
                    mMessageSendCallbackListener.sendSuccessed(chatMessage, result.data.gid, result.data.mid, result.data.time);
                }

            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.w(TAG, "onErrorResponse()");
                if (mMessageSendCallbackListener != null) {
                    mMessageSendCallbackListener.sendFailed(chatMessage, -2, "");
                }

            }

        }) ;

        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, 0, 0));
        request.setTag(TAG);
        mRequestQueue.add(request);

    }

    /**
     * 组装参数
     *
     * @param chatMessage
     * @return
     */
    private Map<String, Object> makeParams(ChatMessagePo chatMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", ImSdk.getInstance().accessToken);
        params.put("type", chatMessage.type + "");
        params.put("fromUserId", chatMessage.fromUserId);
        params.put("gid", chatMessage.groupId);
        params.put("clientMsgId", chatMessage.clientMsgId);
        switch (chatMessage.type) {
            case MessageType.text:
                params.put("content", chatMessage.content);
                AtMsgParam p=JSON.parseObject(chatMessage.param,AtMsgParam.class);
                params.put("param",p);
                break;
            case MessageType.image:
            case MessageType.voice:
            case MessageType.video:
            case MessageType.file:
                Map<String, String> m = JSON.parseObject(chatMessage.param, new TypeReference<Map<String, String>>() {});
//			ImageMsgParam imageP=JSON.parseObject(chatMessage.param, ImageMsgParam.class);
//			params.put("uri", imageP.uri);
//			params.put("name", imageP.name);
//			params.put("size", imageP.size);
//			params.put("width", imageP.width + "");
//			params.put("height", imageP.height + "");
                params.putAll(m);
                break;
//            case MessageType.voice:
//			params.put("uri", chatMessage.uri);
//			params.put("name", chatMessage.name);
//			params.put("time", chatMessage.time);
//                params.putAll(m);
//                break;
//            case MessageType.video:
//            case MessageType.file:
//			params.put("uri", chatMessage.uri);
//			params.put("name", chatMessage.name);
//			params.put("time", chatMessage.time);
//                params.putAll(m);
//                break;
            case MessageType.image_and_text:
                ImgTextMsgV2 msg = JSON.parseObject(chatMessage.param, ImgTextMsgV2.class);
                if (msg.bizParam != null) {
                    msg.param = new HashMap<String, String>();
                    msg.param.putAll(msg.bizParam);
                }
                params.put("content", JSON.toJSONString(msg));
                break;

            default:
                break;
        }
        return params;

    }

}
