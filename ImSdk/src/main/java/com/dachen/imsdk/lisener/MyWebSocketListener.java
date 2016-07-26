package com.dachen.imsdk.lisener;

import android.os.Handler;

import com.dachen.common.utils.Logger;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import java.util.List;
import java.util.Map;

/**
 * Created by Mcp on 2016/4/28.
 */
public class MyWebSocketListener implements WebSocketListener {
    private Handler mHandler;

    public MyWebSocketListener(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
        Logger.e("ws","onStateChanged");
    }

    @Override
    public void onConnected(final WebSocket websocket, final Map<String, List<String>> headers) throws Exception {
        Logger.e("ws","连接成功");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                postConnected(websocket, headers);
            }
        });
    }
    public void postConnected(WebSocket websocket, Map<String, List<String>> headers){

    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
        Logger.e("ws","连接失败");
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        Logger.e("ws","onDisconnected");
    }

    @Override
    public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Logger.e("ws","onFrame");
    }

    @Override
    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Logger.e("ws","onTextFrame");
    }

    @Override
    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Logger.e("ws","PING");

    }

    @Override
    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onTextMessage(final WebSocket websocket, final String text) throws Exception {
        Logger.e("ws","onTextMessage"+text);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                postTextMessage(websocket, text);
            }
        });
    }
    public void postTextMessage(WebSocket websocket, String text){
    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
    }

    @Override
    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Logger.e("ws","onFrameSent");
    }

    @Override
    public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Logger.e("ws","onFrameUnsent");
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        Logger.e("ws","onError"+cause.getMessage());
    }

    @Override
    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
        Logger.e("ws","onFrameError");
    }

    @Override
    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {

    }

    @Override
    public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {

    }

    @Override
    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {

    }

    @Override
    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
        Logger.e("ws","onSendError");
    }

    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
        Logger.e("ws","onUnexpectedError");
    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
        Logger.e("ws","handleCallbackError:"+cause.getMessage());
    }

    @Override
    public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {

    }
}
