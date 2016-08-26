package com.dachen.mdt.util;

import android.content.Context;
import android.content.Intent;

import com.dachen.common.AppManager;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.utils.PushUtils;
import com.dachen.mdt.MyApplication;
import com.dachen.mdt.activity.main.LoginActivity;
import com.dachen.mdt.entity.DoctorInfo;

/**
 * Created by Mcp on 2016/8/8.
 */
public class AppCommonUtils {

    public static DoctorInfo getLoginUser(){
        DoctorInfo info=MyApplication.getInstance().mUserInfo;
        if(info==null)
            info=new DoctorInfo();
        return info;
    }

    public static void logout(){
        PushUtils.removeDevice(SpUtils.getSp().getString(SpUtils.KEY_XIAOMI_TOKEN,""));
        ImSdk.getInstance().logout();
        SpUtils.clearUser();
        MyApplication.getInstance().mUserInfo=null;
        Context context=MyApplication.getInstance();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        AppManager.getAppManager().finishAllActivity();
    }
    public static void deleteLastChar(StringBuilder builder){
        if(builder==null||builder.length()==0)return;
        builder.deleteCharAt(builder.length()-1);
    }
}
