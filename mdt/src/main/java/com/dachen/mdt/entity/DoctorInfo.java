package com.dachen.mdt.entity;

/**
 * Created by Mcp on 2016/8/5.
 */
public class DoctorInfo {

    public String avatar;
    public String department;
    public String hospital;
    public String hospitalId;
    public String name;
    public String telephone;
    public String title;
    public String userId;
    public UserSetting setting;

//    "avatar": "头像地址",
//            "department": "所属科室",
//            "hospital": "医院名称",
//            "hospitalId": "医院Id",
//            "name": "名称",
//            "setting": {
//        "receiveMsg": 1
//    },
//            "telephone": "手机号",
//            "title": "职称",
//            "userId": "用户Id"

    public static class UserSetting {
        public int receiveMsg;
    }
}
