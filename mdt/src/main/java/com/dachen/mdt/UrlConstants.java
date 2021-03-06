package com.dachen.mdt;

import com.dachen.imsdk.ImSdk;

/**
 * Created by Mcp on 2016/8/5.
 */
public class UrlConstants {
    private static String IP;

    public static final String GET_VERSION="/mdt/getLatestVersion";

    public static final String LOGIN="/mdt/auth/login";
    public static final String LOGIN_AUTO="/mdt/auth/loginAuto";
    public static final String UPDATE_PWD="/mdt/auth/updatePwd";

    public static final String MODIFY_AVATAR="/mdt/user/modifyAvatar";
    public static final String GET_DOCTOR_INFO="/mdt/user/getDoctorInfo";
    public static final String MODIFY_SETTING="/mdt/user/userSetting";
//    public static final String GET_PATIENTS="/mdt/user/getPatients";//获取患者列表
    public static final String SET_PATIENT_TAG="/mdt/user/setPatientTag";//获取患者列表

    public static final String GET_PATIENT_TAGS="/mdt/base/getTags";
    public static final String GET_DISEASE_TYPES="/mdt/base/getDiseaseTypes";
    public static final String GET_ALL_DATABASE_LIST="/mdt/base/getAllDatabaseList";
    public static final String GET_MDT_GROUP_LIST="/mdt/base/getMdtGroupList";
    public static final String GET_CHECK_TYPE="/mdt/base/getCheckItemType";
    public static final String GET_CHECK_LIST="/mdt/base/getCheckItemList";
    public static final String GET_DISEASE_WITH_TAG="/mdt/base/getDiseaseWithTag";
    public static final String GET_COMPLICATION_LIST ="/mdt/base/getComplicationsList";
    public static final String GET_BASIC_DISEASE_LIST ="/mdt/base/getBasicDiseaseList";

    public static final String CREATE_ORDER="/mdt/order/createOrder";
    public static final String UPDATE_ORDER="/mdt/order/updateOrder";
    public static final String GET_ORDER="/mdt/order/getOrder";
    public static final String ADD_EXPERTS="/mdt/order/addExperts";
    public static final String DEL_EXPERT="/mdt/order/delExpert";
    public static final String GET_EXPERT_LIST="/mdt/order/getExpertList";
    public static final String SUBMIT_REPORT="/mdt/order/submitReport";
    public static final String SAVE_SUMMARY="/mdt/order/saveSummary";
    public static final String ACCEPT_ORDER="/mdt/order/accept";
    public static final String REFUSE_ORDER="/mdt/order/refused";
    public static final String GET_SUMMARY_LIST="/mdt/order/getSummaryList";
    public static final String GET_MDT_REPORT="/mdt/order/getMdtReport";
    public static final String GET_STATUS="/mdt/order/getStatus";

    public static final String GET_ALL_PATIENTS="/mdt/patient/getAllPatients";
    public static final String GET_TAG_PATIENTS="/mdt/patient/getTagPatients";
    public static final String GET_PATIENT_ORDER="/mdt/patient/getPatientOrder";

    public static final String CAN_VIEW_ORDER_LIST="/mdt/order/canViewOrderList";


    public static String getUrl(String url){
        return IP+url;
    }
    public static void changIp(String ip,String imIp){
        IP=ip;
        ImSdk.getInstance().changeIp(imIp); //"192.168.3.7:8102"
    }

}
