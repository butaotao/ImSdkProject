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
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dachen.common.json.ResultTemplate;
import com.dachen.common.utils.Logger;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.activities.ChatActivityV2;
import com.dachen.imsdk.db.dao.ChatGroupDao;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.entity.GroupPollingBean;
import com.dachen.imsdk.out.ImObserverManager;
import com.dachen.imsdk.utils.ImSpUtils;
import com.dachen.imsdk.utils.ImUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupPolling {

	private static String TAG = GroupPolling.class.getSimpleName();

	private static GroupPolling instance = null;

	private Context context = null;

	// HTTP超时时间
	private static final int timeOut = 30 * 1000;

	private GroupRequest request;

	private static final int MSG_POLLING_NEW = 1;
	private static final int MSG_POLLING_OLD = 2;
	private static final int MSG_HAS_NOTIFY = 3;
	private static final int MSG_NOTICE_CHANGE = 4;
	private static final int MSG_DUTY_CHANGE = 5;
	//是否在拉历史数据.第一次登陆时使用
	private boolean isGettingOld;

	private GroupPolling() {
		context= ImSdk.getInstance().context;
	}

	public static GroupPolling getInstance() {
		if (instance == null) {
			synchronized (GroupPolling.class) {
				instance = new GroupPolling();
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
				executeTask(ImUtils.getLoginUserId(), 0, ImSpUtils.getGroupTs());
				break;
			case MSG_POLLING_OLD:
				OldMsg old=(OldMsg) msg.obj;
				executeTask(old.userId, 1,old.ts);
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
	public void executeTask(String userId, int type,long ts) {
//		if(isGettingOld){
//			if(type!=1){
//				return;
//			}
//		}
		if(TextUtils.isEmpty(ImSdk.getInstance().accessToken ))return;
		if(ts==0||type==1){
			if(isGettingOld)return;
			isGettingOld=true;
		}
		if(type==0)
			cancelTask();
		RequestQueue queue = VolleyUtil.getQueue(context);
		request = new GroupRequest(userId, type,ts);
		request.setRetryPolicy(new DefaultRetryPolicy(timeOut, 0, 0));
		queue.add(request);
		Log.w(TAG, "execute RequestQueue()");
	}

	private class GroupRequest extends ImCommonRequest {
//		private String userId;
		int type;
		long ts;
		/**
		 * @param type 0表示往后拉.1表示往前拉
		 */
		public GroupRequest(String userId, int type,long ts) {
			super( PollingURLs.getGroupList(),null, new SucListener(userId, type,ts), errListener);
//			this.userId = userId;
			this.type = type;
			this.ts=ts;
		}

		@Override
		protected Map<String, ? extends Object> getReqMap() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("access_token", ImSdk.getInstance().accessToken);
			map.put("ts",ts+"");
			map.put("cnt", "50");
			map.put("type", type + "");
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
		int type;
        long ts;

		public SucListener(String userId, int type,long ts) {
			super();
			this.userId = userId;
			this.type = type;
            this.ts=ts;
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
			ResultTemplate<GroupPollingBean> result = JSON.parseObject(response,
					new TypeReference<ResultTemplate<GroupPollingBean>>() {
					});
			if (result == null || result.resultCode != 1 || result.data == null) {
                isGettingOld=false;
				return;
			}
			GroupPollingBean bean=result.data;
			List<ChatGroupPo> groupList =bean.list;
			ChatGroupDao tDao = new ChatGroupDao(context, userId);
			for (ChatGroupPo po : groupList) {
				po.bizStatus=0;
				po.param=null;
				tDao.saveGroup(po);
				if(ChatActivityV2.instance!=null&&po.groupId.equals(ChatActivityV2.instance.getGroupId()) ){
					ChatActivityV2.instance.pollImmediately();
				}
			}
			ImObserverManager.handleChatGroup(groupList);


			if (type==1|| ts == 0) {
				isGettingOld=false;
				if(bean.more){
					long ts=groupList.get(groupList.size()-1).updateStamp;
					handler.obtainMessage(MSG_POLLING_OLD, new OldMsg(userId, ts)).sendToTarget();
				}else{
					ImSpUtils.setOldGroupDone(true);
				}
			}
            if(type==0 &&groupList.size()>0){
//				long ts=tDao.getMaxTs();
//				ImSpUtils.setGroupTs(ts);
				ImSpUtils.setGroupTs(groupList.get(0).updateStamp);
			}
//			else if(groupList.size()>0){
//				ImSpUtils.setGroupTs(groupList.get(0).updateStamp);
//			}
		}
	}

	private ErrorListener errListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Logger.w(TAG, "onErrorResponse()"+error.getMessage());
			isGettingOld=false;
		}

	};
	
	private class OldMsg{
		public String userId;
		public long ts;
		public OldMsg(String userId, long ts) {
			super();
			this.userId = userId;
			this.ts = ts;
		}
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
		if (request != null) {
            if(request.ts==0||request.type==1){
                isGettingOld=false;
            }
			request.cancel();
		}
	}
	public void startTask() {
		handler.obtainMessage(MSG_POLLING_NEW).sendToTarget();
	}
	public void startOldTask() {
		long oldTs=new ChatGroupDao().getMinTs();
		handler.obtainMessage(MSG_POLLING_OLD,new OldMsg(ImUtils.getLoginUserId(),oldTs)).sendToTarget();
	}

}
