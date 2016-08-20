package com.dachen.mdt.net;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.utils.ImSpUtils;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.MyApplication;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.db.dao.PatientInfoDao;
import com.dachen.mdt.entity.PatientInfo;
import com.dachen.mdt.entity.PatientListResult;
import com.dachen.mdt.entity.event.PatientUpdateEvent;
import com.dachen.mdt.listener.RequestHelperListener;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot1.event.EventBus;

/**
 * Created by Mcp on 2016/8/19.
 */
public class PatientUpdater {
    private static Map<String,PatientUpdater> instanceMap =new HashMap<>();
    private boolean inWork;
    private PatientInfoDao dao;
    private SharedPreferences sp;
    private String accessToken;

    public static synchronized PatientUpdater getInstance() {
        String userId= ImUtils.getLoginUserId();
        PatientUpdater instance=instanceMap.get(userId);
        if(instance==null){
            instance=new PatientUpdater();
            instanceMap.put(userId,instance);
        }
        return instance;
    }

    private PatientUpdater() {
        dao=new PatientInfoDao();
        sp=ImSpUtils.spCommon();
        accessToken= ImSdk.getInstance().accessToken;
    }

    public void execute(){
        if(TextUtils.isEmpty(accessToken)||inWork)return;
        inWork=true;
        String url= UrlConstants.getUrl(UrlConstants.GET_TAG_PATIENTS);
        long ts=ImSpUtils.spCommon().getLong(AppConstants.SP_KEY_PATIENT_TS,0);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("lastUpdateTime",ts);
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        request.setAccessToken(accessToken);
        VolleyUtil.getQueue(MyApplication.getInstance()).add(request);
    }

    private RequestHelperListener listener=new RequestHelperListener() {
        @Override
        public void onSuccess(final String dataStr) {
            new Thread(){
                @Override
                public void run() {
                    PatientListResult res=JSON.parseObject(dataStr,PatientListResult.class);
                    if(res.result!=null&&res.result.size()>0){
                        for(PatientInfo info:res.result){
                            dao.savePatient(info);
                            EventBus.getDefault().post(new PatientUpdateEvent());
                        }
                    }
                    sp.edit().putLong(AppConstants.SP_KEY_PATIENT_TS,res.ts).apply();
                    inWork=false;
                }
            }.start();

        }
        @Override
        public void onError(String msg) {
            inWork=false;
        }
    };
}
