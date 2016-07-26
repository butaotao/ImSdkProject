package com.dachen.imsdk.service;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dachen.common.json.ResultTemplate;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.db.po.SimpleUserInfo;
import com.dachen.imsdk.entity.PublicUserInfo;
import com.dachen.imsdk.net.PollingURLs;
import com.dachen.imsdk.utils.RefTool;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImSimpleUserInfoManager {

	private static final String TAG="ImSimpleUserInfoManager";
	private Map<String, Boolean> WORK_STATE=new HashMap<String, Boolean>();
	public Map<String, SimpleUserInfo> userMap=new HashMap<String, SimpleUserInfo>();
	public static ImSimpleUserInfoManager instance;
	private WeakReference<SimpleUserChangeListener> listenerRef;

	public static synchronized ImSimpleUserInfoManager getInstance() {
		if (instance == null) {
			instance = new ImSimpleUserInfoManager();
		}
		return instance;
	}
	
	public interface SimpleUserChangeListener{
		void onUserChange();
	}
	
	public void setListener(SimpleUserChangeListener listener){
		listenerRef=new WeakReference<SimpleUserChangeListener>(listener);
	}

	private boolean inWork(String id) {
		Boolean res = WORK_STATE.get(id);
		if (res == null)
			return false;
		
		return res;
	}
	/**
	 * 
	 * @param id 
	 * @param type {@link SimpleUserInfo} type
	 * @return
	 */
	public SimpleUserInfo getUserInfoForId(String id, int type) {
		SimpleUserInfo info = userMap.get(id);
		if (info != null) {
			return info;
		}
		if (inWork(id)) {
			return null;
		}
		new FetchInfoThread(id, type).start();
		return null;

	}

	/**
	 * 获取公众号详情
	 */
	private SimpleUserInfo fetchPubUserInfo(String id) {
		AndroidHttpClient client = AndroidHttpClient.newInstance("");
		HttpPost p = new HttpPost(PollingURLs.getPub());
		ArrayList<NameValuePair> paramsList= new ArrayList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("pid", id));
		paramsList.add(new BasicNameValuePair("access_token", ImSdk.getInstance().accessToken) );
//		Log.d(TAG, "get pub info params: pid="+id+";token="+ UserSp.getInstance(DApplication.getInstance()).getAccessToken(""));
		try {
			p.setEntity(new UrlEncodedFormEntity(paramsList));
			HttpResponse response = client.execute(p);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				return null;
			String str = EntityUtils.toString(response.getEntity());
			Log.d(TAG, "get pub info result: "+str);
			ResultTemplate<PublicUserInfo> resObj = JSON.parseObject(str,
					new TypeReference<ResultTemplate<PublicUserInfo>>() {
					});
			if (resObj.resultCode == 1){
				PublicUserInfo info= resObj.data;
				SimpleUserInfo result=new SimpleUserInfo(info.pid, info.photourl, info.nickName, info.note, SimpleUserInfo.SimpleUserType.TYPE_PUBLIC);
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		return null;
	}

	public void addNewInfo(SimpleUserInfo info){
		userMap.put(info.userId, info);
		SimpleUserChangeListener l=new RefTool<SimpleUserChangeListener>(listenerRef).getRef();
		if(l!=null){
			l.onUserChange();
		}
	}
	
	private class FetchInfoThread extends Thread {
		String id;
		int type;

		public FetchInfoThread(String id, int type) {
			super();
			this.id = id;
			this.type = type;
		}

		@Override
		public void run() {
			if (inWork(id))
				return;
			WORK_STATE.put(id, true);
//			SimpleUserInfo info = SimpleUserInfoDao.queryForId(id);
			SimpleUserInfo info = null;
			if (info == null) {
				info=fetchPubUserInfo(id);
				if(info!=null){
					addNewInfo(info);
//					SimpleUserInfoDao.getInstance().saveUser(info);
				}
			}else{
				addNewInfo(info);
			}
			WORK_STATE.remove(id);
		}
	}
}
