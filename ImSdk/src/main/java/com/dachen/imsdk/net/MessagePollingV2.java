package com.dachen.imsdk.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

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
import com.dachen.imsdk.db.dao.ChatMessageDao;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.ChatMessageV2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP消息轮询
 * 
 * @author mcp
 * @date 2015年12月9日
 *
 */
public class MessagePollingV2 {
	private static String TAG = MessagePollingV2.class.getSimpleName();
	// 超时时间
	private static final int TIMEOUT = 20 * 1000;
	// HTTP轮询时间
	private static final long POLLING_TIME = 10 * 1000;
	private static final int HANDLER_MSG_POLLING = 1;
	private static final int PAGE_SIZE = 20;

	private static Context mContext;
	private MessageReceivableV2 mMessageListener;
	private RequestQueue mRequestQueue;
	// 最新消息的MsgId
	private ChatMessageDao dao;
	private String mLastestMsgId;
	private String mGroupId;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_MSG_POLLING:
				executeTask();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 消息接受者，需要接收消息的对象需要实现这个接口
	 *
	 */
	public interface MessageReceivableV2 {
		void receivedMessage(ChatMessageV2 receivedMessage);
	}

	public MessagePollingV2(Context context, ChatMessageDao dao) {
		mContext = context;
		this.dao=dao;
		mRequestQueue = VolleyUtil.getQueue(mContext);
	}

	/**
	 */
	public void setMessageReceiverListener(MessageReceivableV2 messageListener) {
		this.mMessageListener = messageListener;
	}

	/**
	 * 执行接受消息任务
	 */
	public void executeTask() {
		// 取消任务
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(TAG);
		}

		if (TextUtils.isEmpty(mLastestMsgId)) {
			// 从数据库获取最新消息的msgId
			mLastestMsgId =dao.getLastMsgId(mGroupId);
		}

		StringRequest request = new MessageRequest(new Listener<String>() {

			@Override
			public void onResponse(String response) {
				Logger.d(TAG, "polling response=" + response);
				ResultTemplate<ChatMessageV2> result = JSON.parseObject(response, new TypeReference<ResultTemplate<ChatMessageV2>>() {
				});
				if (result == null || result.resultCode != 1) {
					return;
				}
				// 分发消息
				dispatchMessage(result.data);
				// 下一次轮询
//				nextPolling(POLLING_TIME);
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Logger.w(TAG, "onErrorResponse()");
				// 下一次轮询
//				nextPolling(POLLING_TIME);
			}

		});

		request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, 0, 0));
		request.setTag(TAG);
		mRequestQueue.add(request);

	}

	private class MessageRequest extends ImCommonRequest {

		public MessageRequest(Listener<String> listener, ErrorListener errorListener) {
			super( PollingURLs.getMessageV2(),null, listener, errorListener);
		}

		@Override
		protected Map<String, ? extends Object> getReqMap() {
			Map<String, String> params = new HashMap<String, String>();
			params.put("access_token", ImSdk.getInstance().accessToken);
			params.put("userId", ImSdk.getInstance().userId);
			params.put("groupId", mGroupId == null ? "" : mGroupId);
			params.put("type", "0");
			params.put("msgId", mLastestMsgId == null ? "" : mLastestMsgId);
			params.put("cnt", PAGE_SIZE + "");

			Logger.d(TAG, "polling params=" + params);
			return params;
		}

//		@Override
//		protected Map<String, String> getParams() throws AuthFailureError {
//
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("access_token", ImSdk.getInstance().accessToken);
//			params.put("userId", ImSdk.getInstance().userId);
//			params.put("groupId", mGroupId == null ? "" : mGroupId);
//			params.put("type", "0");
//			params.put("msgId", mLastestMsgId == null ? "" : mLastestMsgId);
//			params.put("cnt", PAGE_SIZE + "");
//			Logger.d(TAG, "polling params=" + params);
//			return params;
//		}
	}
	
	public void setGroupId(String groupId) {
		this.mGroupId = groupId;
	}

	private void dispatchMessage(ChatMessageV2 data) {

		List<ChatMessagePo> list = data.msgList;
		if (list != null && list.size() > 0) {
			// 将最后一条消息的msgId保存到mLastestMsgId和配置文件
			ChatMessagePo msg = list.get(list.size() - 1);
			if (msg != null) {
				mLastestMsgId = msg.msgId;
			}
		}

		if (this.mMessageListener != null) {
			this.mMessageListener.receivedMessage(data);
		}

	}

	/**
	 * 不等延迟，立刻轮询
	 */
	public void pollImmediately() {
		cancelPolling();
		nextPolling(0);
	}

	/**
	 * 准备下一次轮询
	 */
	private void nextPolling(long delayMillis) {
		mHandler.sendEmptyMessageDelayed(HANDLER_MSG_POLLING, delayMillis);
	}

	/**
	 * 关闭轮询
	 */
	private void cancelPolling() {
		mHandler.removeMessages(HANDLER_MSG_POLLING);
	}

	/**
	 * 关闭接受消息任务
	 */
	public void cancelTask() {

		mLastestMsgId = null;

		// 取消轮询
		cancelPolling();

		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(TAG);
		}
	}

}
