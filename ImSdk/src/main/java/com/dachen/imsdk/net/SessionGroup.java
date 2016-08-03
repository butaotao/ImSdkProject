package com.dachen.imsdk.net;

import android.content.Context;
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
import com.dachen.imsdk.HttpErrorCode;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.entity.GroupInfo2Bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于创建group和获取group信息
 *
 * @author gaozhuo
 * @since 2015年9月8日
 *
 */
public class SessionGroup {

	private static final String TAG = SessionGroup.class.getSimpleName();
	public static final int CREATE = 1;// 创建
	public static final int QUERY = 2;// 查询
	public static final int ADD_USER = 3;//

	// 超时时间
	private static final int TIMEOUT = 20 * 1000;

//	private static SessionGroup mInstance;
	private Context mContext;
	private RequestQueue mRequestQueue;
	private SessionGroupCallback mSessionGroupCallbackListener;
	private SessionGroupCallbackNew mCallbackNew;

	public interface SessionGroupCallback {
		void onGroupInfo(GroupInfo2Bean.Data data, int what);
		void onGroupInfoFailed(String msg);
	}
	public interface SessionGroupCallbackNew {
		void onGroupInfo(ChatGroupPo po, int what);
		void onGroupInfoFailed(String msg);
	}

//	private SessionGroup() {
//	}
	public SessionGroup(Context context) {
		mContext = context;
		mRequestQueue = VolleyUtil.getQueue(mContext);
	}

	/**
	 * 得到实例
	 *
	 */
//	public static SessionGroup getInstance(Context context) {
//		if (mInstance == null) {
//			synchronized (SessionGroup.class) {
//				// 单例生命周期很长，使用getApplicationContext()避免内存泄露
//				mInstance = new SessionGroup(context.getApplicationContext());
//			}
//		}
//		return mInstance;
//	}

	public void setCallback(SessionGroupCallback listener) {
		this.mSessionGroupCallbackListener = listener;
	}
	public void setCallbackNew(SessionGroupCallbackNew listener) {
		this.mCallbackNew = listener;
	}

	/**
	 * 根据userIds创建会话组
	 *
	 * @param userIds
	 */
	public void createGroup( List<String> userIds){
		createGroup(userIds, null);
	}
	public void createGroup(final List<String> userIds,String rType) {
		Map<String, String> params = new HashMap<String, String>();
//		params.put("access_token", UserSp.getInstance(mContext).getAccessToken(""));
		params.put("fromUserId", ImSdk.getInstance().userId);
		params.put("toUserId", formatUserIds(userIds));
		params.put("gtype", rType);
		Logger.d(TAG, "create group params=" + params);
		StringRequest request = new ImCommonRequest(PollingURLs.createGroup(),params, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				Logger.d(TAG, "create group response=" + response);
				handleResponse(response, CREATE);
			}

		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.w(TAG, "onErrorResponse()");
				notifyError("网络异常");
//				if (mSessionGroupCallbackListener != null) {
//					mSessionGroupCallbackListener.onGroupInfoFailed();
//				}
			}

		});

		request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, 10, 0));
		request.setTag(TAG);
		mRequestQueue.add(request);
	}
	public void addGroupUser(final List<String> userIds,String gid) {
		Map<String, String> params = new HashMap<String, String>();
//		params.put("access_token", UserSp.getInstance(mContext).getAccessToken(""));
		params.put("fromUserId", ImSdk.getInstance().userId);
		params.put("toUserId", formatUserIds(userIds));
		params.put("gid", gid);
		Logger.d(TAG, "add group user params=" + params);
		StringRequest request = new ImCommonRequest(PollingURLs.addGroupUser(),params, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				Logger.d(TAG, "add group user response=" + response);
				handleResponse(response, ADD_USER);
			}

		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.w(TAG, "onErrorResponse()");
				if (mSessionGroupCallbackListener != null) {
					mSessionGroupCallbackListener.onGroupInfoFailed("网络异常");
				}
			}

		});

		request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, 10, 0));
		request.setTag(TAG);
		mRequestQueue.add(request);
	}

	/**
	 * 获取会话组信息
	 *
	 * @param gid
	 */
	public void getGroupInfo(final String gid) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", ImSdk.getInstance().accessToken);
		params.put("userId", ImSdk.getInstance().userId);
		params.put("gid", gid);
		StringRequest request = new ImCommonRequest(  PollingURLs.getGroupInfo(),params, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.out.println("------------------->get groupinfo response=" + response);
				handleResponse(response, QUERY);
			}

		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.w(TAG, "onErrorResponse()");
				notifyError("网络异常");
			}

		});

		request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, 10, 0));
		request.setTag(TAG);
		mRequestQueue.add(request);
	}

	private void notifyError(String msg){
		if (mSessionGroupCallbackListener != null) {
			mSessionGroupCallbackListener.onGroupInfoFailed(msg);
		}
	}

	private void handleResponse(String response, int what) {
		GroupInfo2Bean groupInfo = JSON.parseObject(response, GroupInfo2Bean.class);
		if (groupInfo == null){
			notifyError("数据异常");
			return;
		} else if(groupInfo.resultCode != HttpErrorCode.successed || groupInfo.data == null|| groupInfo.data.gid == null) {
			notifyError("请求出错:"+groupInfo.detailMsg);
			return;
		}
		if (mSessionGroupCallbackListener != null) {
			mSessionGroupCallbackListener.onGroupInfo(groupInfo.data, what);
		}
	}

	public void getGroupInfoNew(final String gid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("access_token", ImSdk.getInstance().accessToken);
		params.put("userId", ImSdk.getInstance().userId);
		params.put("gid", gid);
		params.put("isNew", true);
		StringRequest request = new ImCommonRequest(  PollingURLs.getGroupInfo(),params, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.out.println("------------------->get groupinfo response=" + response);
				handleResponseNew(response, QUERY);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.w(TAG, "onErrorResponse()");
				notifyError("网络异常");
			}
		});

		request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, 10, 0));
		request.setTag(TAG);
		mRequestQueue.add(request);
	}

	private void handleResponseNew(String response, int what) {
		ResultTemplate<ChatGroupPo> resData = JSON.parseObject(response,new TypeReference<ResultTemplate<ChatGroupPo>>(){});
		if (resData == null){
			notifyError("数据异常");
			return;
		} else if(resData.resultCode != HttpErrorCode.successed || resData.data == null ) {
			notifyError("请求出错:"+resData.detailMsg);
			return;
		}
		if (mCallbackNew != null) {
			mCallbackNew.onGroupInfo(resData.data, what);
		}
	}

	/**
	 * 格式化，用|分开
	 *
	 * @param userIds
	 * @return
	 */
	public static String formatUserIds(List<String> userIds) {
		if (userIds == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < userIds.size(); i++) {
			if (i > 0) {
				sb.append('|');
			}
			sb.append(userIds.get(i));
		}
		return sb.toString();
	}
}
