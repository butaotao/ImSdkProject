package com.dachen.mdt.util;

import com.dachen.mdt.MyApplication;
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

}
