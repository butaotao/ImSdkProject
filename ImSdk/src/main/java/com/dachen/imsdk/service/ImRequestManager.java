package com.dachen.imsdk.service;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dachen.common.async.SimpleResultListener;
import com.dachen.common.async.SimpleResultListenerV2;
import com.dachen.common.json.EmptyResult;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.archive.entity.ArchiveItem;
import com.dachen.imsdk.consts.MessageType;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.ChatMessageV2;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.net.MessageSenderV2;
import com.dachen.imsdk.net.MessageSenderV2.MessageSendCallbackV2;
import com.dachen.imsdk.net.PollingURLs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mcp on 2016/6/6.
 */
public class ImRequestManager {

    public static void clearGroupMsg(String groupId, final SimpleResultListener listener) {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("gid", groupId);
        final Listener<String> lis = new Listener<String>() {
            @Override
            public void onResponse(String s) {
                EmptyResult result = JSON.parseObject(s, EmptyResult.class);
                if (result == null) listener.onError("请求出错");
                if (result.resultCode == 1)
                    listener.onSuccess();
                else
                    listener.onError(result.detailMsg);
            }
        };
        ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError("网络故障");
            }
        };
        ImCommonRequest req = new ImCommonRequest(PollingURLs.clearGroupMsg(), reqMap, lis, errorListener);
        VolleyUtil.getQueue(ImSdk.getInstance().context).add(req);
    }

    public static void forwardMsg(String msgId, String groupId, int index, final SimpleResultListener listener) {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("msgId", msgId);
        reqMap.put("gid", groupId);
        reqMap.put("index", index);
        final Listener<String> lis = new Listener<String>() {
            @Override
            public void onResponse(String s) {
                EmptyResult result = JSON.parseObject(s, EmptyResult.class);
                if (result == null) listener.onError("请求出错");
                if (result.resultCode == 1)
                    listener.onSuccess();
                else
                    listener.onError(result.detailMsg);
            }
        };
        ErrorListener errorListener = new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError("网络故障");
            }
        };
        StringRequest req = new ImCommonRequest(PollingURLs.forwardMsg(), reqMap, lis, errorListener);
        VolleyUtil.getQueue(ImSdk.getInstance().context).add(req);
    }

    public static void sendArchive(ArchiveItem item, String groupId, MessageSendCallbackV2 callback) {
        ChatMessagePo chatMessage = new ChatMessagePo();
        chatMessage.type = MessageType.file;
        ChatMessageV2.ArchiveMsgParam p = new ChatMessageV2.ArchiveMsgParam();
        p.name = item.name;
        p.key = item.fileId;
        p.uri = item.url;
        p.size = item.size;
        p.format = item.suffix;
        chatMessage.param = JSON.toJSONString(p);
        chatMessage.fromUserId = ImSdk.getInstance().userId;
        chatMessage.groupId = groupId;
        MessageSenderV2 sender = MessageSenderV2.getInstance(ImSdk.getInstance().context);
        sender.setMessageSendCallbackListener(callback);
        sender.sendMessage(chatMessage);
    }

    public static void topChatGroup(String groupId, int act, final SimpleResultListenerV2 listener) {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("gid", groupId);
        reqMap.put("act", act);
        StringRequest req = new ImCommonRequest(PollingURLs.topChatGroup(), reqMap, makeSucListener(listener), makeErrListener(listener));
        VolleyUtil.getQueue(ImSdk.getInstance().context).add(req);
    }

    public static Listener<String> makeSucListener(final SimpleResultListenerV2 listener) {
        return new Listener<String>() {
            @Override
            public void onResponse(String s) {
                EmptyResult result = JSON.parseObject(s, EmptyResult.class);
                if (result == null) listener.onError("请求出错");
                if (result.resultCode == 1)
                    listener.onSuccess(result.data);
                else
                    listener.onError(result.detailMsg);
            }
        };
    }
    public static ErrorListener makeErrListener(final SimpleResultListenerV2 listener) {
        return new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError("网络故障");
            }
        };
    }
}
