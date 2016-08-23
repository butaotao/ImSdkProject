package com.dachen.imsdk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dachen.common.json.ResultTemplate;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.db.dao.GroupUserInfoDao;
import com.dachen.imsdk.db.po.GroupUserPo;
import com.dachen.imsdk.entity.GroupInfo2Bean;
import com.dachen.imsdk.entity.GroupUserInfo;
import com.dachen.imsdk.lisener.UserInfoChangeListener;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.net.PollingURLs;
import com.dachen.imsdk.utils.RefTool;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImGroupUserInfoManager {

	private static final String TAG="ImGroupUserInfoManager";
	private Map<String, Boolean> WORK_STATE=new HashMap<String, Boolean>();
	public Map<String, GroupUserPo> userMap=new HashMap<String, GroupUserPo>();
	public static ImGroupUserInfoManager instance;
	private WeakReference<UserInfoChangeListener> listenerRef;

	public static synchronized ImGroupUserInfoManager getInstance() {
		if (instance == null) {
			instance = new ImGroupUserInfoManager();
		}
		return instance;
	}


	public void setListener(UserInfoChangeListener listener){
		listenerRef=new WeakReference<UserInfoChangeListener>(listener);
	}

	private boolean inWork(String groupId,String userId) {
		Boolean res = WORK_STATE.get(makeKey(groupId,userId));
		if (res == null)
			return false;
		
		return res;
	}
	private String makeKey(String groupId,String userId){
		return groupId+"||"+userId;
	}
	/**
	 * @return
	 */
	public GroupInfo2Bean.Data.UserInfo getUserInfoForId(String groupId, String userId) {
		GroupUserPo info = userMap.get(makeKey(groupId,userId));
		if (info != null) {
			return info.toUserInfo();
		}
		if (inWork(groupId,userId) ) {
			return null;
		}
		new FetchInfoThread(groupId,userId).start();
		return null;
	}

//	private GroupUserPo fetchGroupUserInfo(String groupId,String userId) {
//		AndroidHttpClient client = AndroidHttpClient.newInstance("");
//		HttpPost p = new HttpPost(PollingURLs.groupUserInfo());
//		p.setHeader("access_token", ImSdk.getInstance().accessToken);
//		p.setHeader("content-type", "application/json");
//		Map<String, String> m = new HashMap<String, String>();
//		m.put("gid",groupId);
//		m.put("userId", userId);
//		String paramStr = JSON.toJSONString(m);
//		try {
//			p.setEntity(new StringEntity(paramStr,"UTF-8"));
////			p.setEntity(new UrlEncodedFormEntity(paramsList));
//			HttpResponse response = client.execute(p);
//			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
//				return null;
//			String str = EntityUtils.toString(response.getEntity());
//			Log.d(TAG, "get pub info result: "+str);
//			ResultTemplate<List<GroupUserInfo>> resObj = JSON.parseObject(str,
//					new TypeReference<ResultTemplate<List<GroupUserInfo>>>() {
//					});
//			if (resObj.resultCode == 1){
//				List<GroupUserInfo> infoList= resObj.data;
//				if(infoList==null||infoList.size()==0)
//					return null;
//				GroupUserInfo info=infoList.get(0);
//				GroupUserPo result=new GroupUserPo(info.id, info.pic, info.name, info.userType, info.role);
//				return result;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			client.close();
//		}
//		return null;
//	}

    private void fetchGroupUserInfo(final String groupId, final String userId){
        Listener<String> listener=new Listener<String>() {
            @Override
            public void onResponse(String s) {
                ResultTemplate<List<GroupUserInfo>> resObj = JSON.parseObject(s,
                        new TypeReference<ResultTemplate<List<GroupUserInfo>>>() {
                        });
                if (resObj.resultCode == 1){
                    List<GroupUserInfo> infoList= resObj.data;
                    if(infoList==null||infoList.size()==0){
                        WORK_STATE.remove(makeKey(groupId,userId));
                        return;
                    }
                    GroupUserInfo info=infoList.get(0);
                    GroupUserPo result=new GroupUserPo(info.id, info.pic, info.name, info.userType, info.role);
                    result.groupId=groupId;
                    addNewInfo(result);
                    GroupUserInfoDao.saveUser(result);
                    WORK_STATE.remove(makeKey(groupId,userId));
                }
            }
        };
        ErrorListener errorListener=new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                WORK_STATE.remove(makeKey(groupId,userId));
            }
        };
        Map<String, String> m = new HashMap<String, String>();
        m.put("gid",groupId);
        m.put("userId", userId);
        ImCommonRequest request=new ImCommonRequest(PollingURLs.groupUserInfo(),m,listener,errorListener);
        VolleyUtil.getQueue(ImSdk.getInstance().context).add(request);
    }


    public void addNewInfo(GroupUserPo info){
		userMap.put(makeKey(info.groupId,info.userId), info);
		UserInfoChangeListener l=new RefTool<UserInfoChangeListener>(listenerRef).getRef();
		if(l!=null){
			l.onUserChange();
		}
	}
	
	private class FetchInfoThread extends Thread {
		String groupId;
		String userId;

		public FetchInfoThread(String groupId,String userId) {
			super();
			this.groupId = groupId;
			this.userId = userId;
		}

		@Override
		public void run() {
			if (inWork(groupId,userId))
				return;
			WORK_STATE.put(makeKey(groupId,userId), true);
			GroupUserPo info= GroupUserInfoDao.query(groupId, userId);
			if (info == null) {
//				info=fetchGroupUserInfo(groupId,userId);
//				if(info!=null){
//					info.groupId=groupId;
//					addNewInfo(info);
//					GroupUserInfoDao.saveUser(info);
//				}
                fetchGroupUserInfo(groupId,userId);
			}else{
				addNewInfo(info);
                WORK_STATE.remove(makeKey(groupId,userId));
            }
		}
	}
}
