package com.dachen.mdt.util;

import android.content.Context;
import android.content.Intent;

import com.dachen.common.AppManager;
import com.dachen.common.utils.QiNiuUtils;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.utils.PushUtils;
import com.dachen.mdt.MyApplication;
import com.dachen.mdt.UrlConstants;
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

    public static void changeEvn(int envCode){
        if(envCode==1){
            UrlConstants.changIp("http://192.168.3.7:8101","192.168.3.7:8102");
            QiNiuUtils.changeEnv(QiNiuUtils.DOMAIN_3_7);
        }else{
            UrlConstants.changIp("http://120.25.84.65:8101","120.25.84.65:8102");
            QiNiuUtils.changeEnv(QiNiuUtils.DOMAIN_KANGZHE_TEST);
        }

    }
}
