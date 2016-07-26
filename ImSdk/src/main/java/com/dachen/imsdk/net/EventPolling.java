package com.dachen.imsdk.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dachen.common.json.ResultTemplate;
import com.dachen.common.utils.Logger;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.entity.EventPL;
import com.dachen.imsdk.entity.EventPollingBean;
import com.dachen.imsdk.out.ImObserverManager;
import com.dachen.imsdk.utils.ImSpUtils;
import com.dachen.imsdk.utils.ImUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventPolling {

	private static String TAG = EventPolling.class.getSimpleName();

	private static EventPolling instance = null;

	private Context context = null;

	// HTTP超时时间
	private static final int timeOut = 30 * 1000;

	private StringRequest request;

	private static final int MSG_POLLING_NEW = 1;
	//是否在拉历史数据.第一次登陆时使用


	private EventPolling() {
		context= ImSdk.getInstance().context;
	}

	public static EventPolling getInstance() {
		if (instance == null) {
			synchronized (EventPolling.class) {
				instance = new EventPolling();
			}
		}
		return instance;
	}

	/**
	 * 初始化
	 * @param context
	 */
	public void init(Context context) {
		this.context = context;
	}

	/**
	 * 关闭轮询
	 */
	private void cancelPolling() {
		Log.w(TAG, "cancelPolling()");
		handler.removeMessages(MSG_POLLING_NEW);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg == null) {
				return;
			}
			switch (msg.what) {
			case MSG_POLLING_NEW:
				executeTask(ImUtils.getLoginUserId(),  ImSpUtils.getEventTs());
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};
	/**
	 * 执行任务
	 */
	public void executeTask(String userId,long ts) {
		cancelTask();
		RequestQueue queue = VolleyUtil.getQueue(context);
		request = new EventRequest(userId,ts);
		request.setRetryPolicy(new DefaultRetryPolicy(timeOut, 0, 0));
		queue.add(request);
		Log.w(TAG, "execute RequestQueue()");
	}

	private class EventRequest extends ImCommonRequest {
//		private String userId;
		private int type;
		private long ts;
		/**
		 */
		public EventRequest(String userId, long ts) {
			super( PollingURLs.getEventList(),null, new SucListener(userId), errListener);
//			this.userId = userId;
			this.type = type;
			this.ts=ts;
		}

		@Override
		protected Map<String, ? extends Object> getReqMap() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("access_token", ImSdk.getInstance().accessToken);
			map.put("ts",ts+"");
			Logger.d(TAG, "request params=" + map);
			return map;
		}

//		@Override
//		protected Map<String, String> getParams() throws AuthFailureError {
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("access_token", ImSdk.getInstance().accessToken);
//			map.put("ts",ts+"");
//			map.put("cnt", "50");
//			map.put("type", type + "");
//			Logger.d(TAG, "request params=" + map);
//			return map;
//		}
	}

	private class SucListener implements Listener<String> {
		String userId;

		public SucListener(String userId) {
			super();
			this.userId = userId;
		}

		@Override
		public void onResponse(final String response) {
			new Thread(){
				@Override
				public void run() {
					handleResponce(response);
				}
			}.start();
		}
		
		public void handleResponce(String response){
			Logger.d(TAG, "business polling result=" + response);
			ResultTemplate<EventPollingBean> result = JSON.parseObject(response,new TypeReference<ResultTemplate<EventPollingBean>>() {});
			if (result == null || result.resultCode != 1 || result.data == null) {
				return;
			}
			EventPollingBean bean=result.data;
			final List<EventPL> eventList =bean.list;
			if(eventList==null)return;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    for (EventPL e : eventList) {
                        ImObserverManager.notifyEvent(e);
                    }
                }
            });
            ImSpUtils.setEventTs(bean.ts);
		}
	}

	private ErrorListener errListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Logger.w(TAG, "onErrorResponse()");
		}

	};
	
	/**
	 * 关闭任务
	 */
	public void cancelTask() {
		Log.w(TAG, "cancelTask()");
		/**
		 * 关闭轮询
		 */
		cancelPolling();
		if (request != null) {
			request.cancel();
		}
	}
	public void startTask() {
		handler.obtainMessage(MSG_POLLING_NEW).sendToTarget();
	}

}
