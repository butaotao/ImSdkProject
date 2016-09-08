package com.dachen.mdt.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.alibaba.fastjson.JSON;
import com.dachen.mdt.MyApplication;
import com.dachen.mdt.entity.DoctorInfo;

/**
 * Created by Mcp on 2016/8/9.
 */
public class SpUtils {

    private static final String SP_NAME="mdt";
    public static final String KEY_USER_TOKEN="userToken";
    public static final String KEY_USER_ID="user_id";;
    public static final String KEY_USER_INFO="userInfo";
    public static final String KEY_LAST_LOGIN_PHONE="lastLoginPhone";
    public static final String KEY_XIAOMI_TOKEN="xiaomiTOken";
    public static final String KEY_URL_ENV="urlEnv";
    public static final String KEY_SKIP_VERSION="skipVersion";

    public static SharedPreferences getSp(){
        return MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }
    public static Editor edit(){
        return getSp().edit();
    }

    public static void saveUser(String token, DoctorInfo info){
        edit().putString(KEY_USER_ID,info.userId).putString(KEY_USER_INFO, JSON.toJSONString(info)).putString(KEY_USER_TOKEN,token).apply();
    }
    public static void saveUser(DoctorInfo info){
        edit().putString(KEY_USER_ID,info.userId).putString(KEY_USER_INFO, JSON.toJSONString(info)).apply();
    }
    public static void clearUser(){
        edit().remove(KEY_USER_ID).remove(KEY_USER_INFO).remove(KEY_USER_TOKEN).apply();
    }
}
