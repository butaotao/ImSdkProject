package com.dachen.imsdk.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dachen.common.json.ResultTemplate;
import com.dachen.common.toolbox.DCommonRequest;
import com.dachen.common.utils.Logger;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.entity.EventPL;
import com.dachen.imsdk.entity.ImPollingBean;
import com.dachen.imsdk.entity.ImWebSocketBean;
import com.dachen.imsdk.lisener.MyWebSocketListener;
import com.dachen.imsdk.out.ImObserverManager;
import com.dachen.imsdk.utils.ImSpUtils;
import com.neovisionaries.ws.client.PayloadGenerator;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP轮询 1.业务：会话、验证请求。。。 业务轮询里的消息是增量的，但未读消息要跟WEB同步，不过APP客户端要处理点击清零。
 * 
 * @author mcp
 */
public class ImPolling {

	public static long APP_OPEN_TIME;
	// 服务器时间差 服务器时间-本地时间
	private static Long serveTimeDiff;

	private static String TAG = ImPolling.class.getSimpleName();
	private static ImPolling instance = null;
	private Context context = null;
	// HTTP超时时间
	private static final int timeOut = 30 * 1000;
	// 第一次HTTP轮询时间
	private static final long pollingTime_Default = 10 * 1000;
	// HTTP轮询时间
	private long pollingTime = 10 * 1000;
	// 是否保存了轮询时间
	private boolean isPreservePollingTime = false;
	private PollingRequest request;
	private static final int MSG_POLLING = 1010101;
	private static final int MSG_UI_PAUSE = 2020202;
	private static final int MSG_WS_RECONNECT = 2020203;
	private boolean IN_WORK = false;
	private boolean pollPaused = true;
	private boolean uiPaused = true;
	private WebSocket mSocket;
    private long wsEventTime;
    private long wsGroupTime;
    private long wsRecTimes;
    private boolean wsPaused=true;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg == null) {
				return;
			}
			switch (msg.what) {
			case MSG_POLLING:
				Log.w(TAG, "what == handler_msg_polling");
				executeTask();

				break;
			case MSG_UI_PAUSE:
                wsPaused=true;
                uiPaused=true;
				cancelTask();
				closeWebSocket();
				pollPaused = true;
				break;
			case MSG_WS_RECONNECT:
                doRecWebSocket();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private ImPolling() {
		context = ImSdk.getInstance().context;
	}

	public static ImPolling getInstance() {
		if (instance == null) {
			synchronized (ImPolling.class) {
				instance = new ImPolling();
			}
		}
		return instance;
	}

	/**
	 * 准备下一次轮询
	 */
	private void prepareNextPolling(long delayMillis) {
		Log.w(TAG, "prepareNextPolling():delayMillis:" + delayMillis);
		handler.sendEmptyMessageDelayed(MSG_POLLING, delayMillis);
	}

	/**
	 * 关闭轮询
	 */
	private void cancelPolling() {
		handler.removeMessages(MSG_POLLING);
	}

	/**
	 * 处理指令
	 */
	private void handleEvent(EventPL event) {
		if (event == null || event.eventType == null) {
			return;
		}
		ImObserverManager.notifyEvent(event);
	}

	/**
	 * 执行任务
	 */
	public void executeTask() {
		String userId =ImSdk.getInstance().userId;
		String token=ImSdk.getInstance().accessToken;
		if(TextUtils.isEmpty(userId)|| TextUtils.isEmpty(token) ||mSocket!=null){
			pollPaused =true;
			return;
		}
		if (IN_WORK) {
			return;
		}

		pollPaused = false;

		cancelTask();

		RequestQueue queue = VolleyUtil.getQueue(context);
		Listener<String> sucListener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				Logger.w(TAG, "im polling result=" + response);
				ResultTemplate<ImPollingBean> result = JSON.parseObject(response,
						new TypeReference<ResultTemplate<ImPollingBean>>() {
						});
				if (result == null || result.resultCode != 1 || result.data == null) {
					prepareNextPolling(getPollingTime());
					return;
				}
				ImPollingBean data = result.data;
				// 指令组
				EventPL[] eventList = data.eventList;
				if (eventList != null && eventList.length > 0) {
					for (EventPL _event : eventList) {
						// 处理指令
						handleEvent(_event);
					}
				}
				if (data.ts > 0) {
					ImSpUtils.setEventTs(data.ts);
				}
				if (result.data.hasNewMsg == 1) {
					GroupPolling.getInstance().startTask();
				}

				IN_WORK = false;
				prepareNextPolling(getPollingTime());
			}
		};

		request = new PollingRequest(userId, sucListener, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Logger.w(TAG, "onErrorResponse()" + error.getMessage());
				// ToastUtil.showToast(mContext, "failed");
				IN_WORK = false;
				prepareNextPolling(getPollingTime());
			}
		});
		request.setRetryPolicy(new DefaultRetryPolicy(timeOut, 0, 0));
		queue.add(request);
		Log.w(TAG, "execute RequestQueue()");
	}

	private class PollingRequest extends ImCommonRequest {

		public PollingRequest( String userId, Listener<String> listener, ErrorListener errorListener) {
			super( PollingURLs.imPollingUrl(),null, listener, errorListener);
		}

		@Override
		protected Map<String, ? extends Object> getReqMap() {
			Map<String, Long> m = new HashMap<String, Long>();
			m.put("ts_msg", ImSpUtils.getGroupTs());
			m.put("ts_event", ImSpUtils.getEventTs());
			Logger.w(TAG, "im polling req=" + m);
			return m;
		}

	}

	/**
	 * 获取轮询时间（单位为毫秒）
	 */
	private long getPollingTime() {
		if (isPreservePollingTime()) {
			return pollingTime;
		} else {
			return pollingTime_Default;
		}
	}

	/**
	 * 设置是否保存了轮询时间
	 * 
	 * @param isPreserve
	 * @return
	 */
	private void setPreservePollingTime(boolean isPreserve) {
		isPreservePollingTime = isPreserve;
	}

	/**
	 * 是否保存了轮询时间
	 * 
	 * @return
	 */
	private boolean isPreservePollingTime() {
		return isPreservePollingTime;
	}

	/**
	 * 关闭任务
	 */
	public void cancelTask() {
		Log.w(TAG, "cancelTask()");
		/**
		 * 关闭轮询
		 */
		cancelPolling();
		setPreservePollingTime(false);
		if (request != null) {
			request.cancel();
		}
		IN_WORK = false;
	}

	public void onResume() {
		handler.removeMessages(MSG_UI_PAUSE);
        uiPaused=false;
		String userId =ImSdk.getInstance().userId;
		String token=ImSdk.getInstance().accessToken;
		if(TextUtils.isEmpty(userId)|| TextUtils.isEmpty(token)){
			closeWebSocket();
            wsPaused=true;
		}else{
            if(pollPaused){
                if(!ImSpUtils.getOldGroupDone()){
                    GroupPolling.getInstance().startOldTask();
                }
                executeTask();
            }
			if(mSocket==null && wsPaused){
                initWebSocket();
            }
        	wsPaused=false;
    	}
    }

	public void onPause() {
		handler.sendMessageDelayed(handler.obtainMessage(MSG_UI_PAUSE), 2000);
	}

	public static long getServerTimeDiff() {
		Long diff = serveTimeDiff;
		if (diff == null) {
			diff = 0L;
			RequestQueue que = VolleyUtil.getQueue(ImSdk.getInstance().context);
			StringRequest req = new DCommonRequest(ImSdk.getInstance().context,Method.POST, PollingURLs.serverTime(), new Listener<String>() {
				@Override
				public void onResponse(String response) {
					ResultTemplate<Long> result = JSON.parseObject(response, new TypeReference<ResultTemplate<Long>>() {
					});
					if (result.resultCode == 1) {
						serveTimeDiff = result.data - System.currentTimeMillis();
						setAppOpenTime();
					}
				}
			}, null);
			que.add(req);
		}
		return diff;
	}
	public static void setAppOpenTime(){
		APP_OPEN_TIME=System.currentTimeMillis()+getServerTimeDiff();
	}
	private void initWebSocket(){
        handler.removeMessages(MSG_WS_RECONNECT);
		closeWebSocket();
		WebSocketFactory factory=new WebSocketFactory();
		try {
			WebSocket soc =factory.createSocket(PollingURLs.webSocket(),10000);
			soc.addHeader("access-token",ImSdk.getInstance().accessToken);
			soc.addHeader("Upgrade","websocket");
			soc.addHeader("Connection","Upgrade");
            soc.setPingInterval(45000);
			soc.setPingPayloadGenerator(new PayloadGenerator() {
				@Override
				public byte[] generate() {
					return "ping".getBytes();
				}
			});
			soc.addListener(socketListener);
			soc.connectAsynchronously();
//            Toast.makeText(this,"连接成功", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void closeWebSocket(){
		if(mSocket==null|| !mSocket.isOpen())return;
		mSocket.sendClose();
		mSocket=null;
	}

	private WebSocketListener socketListener=new MyWebSocketListener(handler) {

        @Override
        public void postConnected(WebSocket websocket, Map<String, List<String>> headers) {
//            Toast.makeText(context,"连接成功",Toast.LENGTH_SHORT).show();
            wsRecTimes=0;
            executeTask();
            mSocket=websocket;
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
            super.onConnectError(websocket, cause);
            sendRecWebSocket();
        }

        @Override
		public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            if(websocket==mSocket)
                mSocket=null;
            if(!uiPaused)
                prepareNextPolling(getPollingTime());
            sendRecWebSocket();
			Logger.e("ws","onDisconnected");
		}

        @Override
        public void postTextMessage(WebSocket websocket, String text) {
            ImWebSocketBean bean=JSON.parseObject(text,ImWebSocketBean.class);
            String cmd=bean.cmd;
            if(TextUtils.isEmpty(cmd))return;
            if(cmd.equals(ImWebSocketBean.CMD_MSG)){
                GroupPolling.getInstance().startTask();
            }else if(cmd.equals(ImWebSocketBean.CMD_EVENT)){
                EventPolling.getInstance().startTask();
            }
        }
	};

	private void sendRecWebSocket(){
        handler.removeMessages(MSG_WS_RECONNECT);
        if(mSocket!=null||uiPaused)return;
        String userId =ImSdk.getInstance().userId;
        String token=ImSdk.getInstance().accessToken;
        if(TextUtils.isEmpty(userId)|| TextUtils.isEmpty(token)){
            return;
        }
        int delay;
        if(wsRecTimes==0) {
            delay =0;
        }else if(wsRecTimes==1){
            delay = 60 * 1000;
        }else if(wsRecTimes==2){
            delay=120*1000;
        }else {
            delay=600*1000;
        }
        Log.e("ws","set reconnect in "+delay);
        handler.sendEmptyMessageDelayed(MSG_WS_RECONNECT,delay);
	}
	public void doRecWebSocket(){
        if(mSocket!=null||uiPaused)return;
        String userId =ImSdk.getInstance().userId;
        String token=ImSdk.getInstance().accessToken;
        if(TextUtils.isEmpty(userId)|| TextUtils.isEmpty(token)){
            return;
        }
        wsRecTimes++;
        initWebSocket();
	}
}
