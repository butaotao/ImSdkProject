package com.dachen.mdt.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.actionsheet.ActionSheet.ActionSheetListener;
import com.dachen.common.toolbox.DCommonRequest;
import com.dachen.common.utils.Logger;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.common.widget.PasswordView;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.MyApplication;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.DoctorInfo;
import com.dachen.mdt.exception.TextEmptyException;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.util.AppCommonUtils;
import com.dachen.mdt.util.SpUtils;
import com.dachen.mdt.util.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    public static final String TAG="LoginActivity";

    @BindView(R.id.et_phone)
    public EditText etPhone;
    @BindView(R.id.et_pwd)
    public PasswordView etPwd;
    private int clickTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        etPhone.setText(SpUtils.getSp().getString(SpUtils.KEY_LAST_LOGIN_PHONE,null));
//        etPwd.setText("123456");
    }

    @OnClick(R.id.login_btn)
    public void clickLogin(){
        final String userName;
        final String pwd;
        try {
            userName = ViewUtils.checkTextEmpty(etPhone);
            pwd=ViewUtils.checkTextEmpty(etPwd.getEditView());
        } catch (TextEmptyException e) {
            e.tv.requestFocus();
            e.tv.setError(Html.fromHtml("<font color='red'>不能为空!</font>"));
            return;
        }
        String url= UrlConstants.getUrl(UrlConstants.LOGIN);
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                Map<String,String> map= JSON.parseObject(dataStr,new TypeReference<Map<String, String>>(){});
                String token=map.get("access-token");
                getLoginInfo(token);
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };
        DCommonRequest req=new DCommonRequest(Method.POST,url, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener)){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("accountNum",userName);
                param.put("password",pwd);
                return param;
            }
        };
        getProgressDialog().show();
        VolleyUtil.getQueue(mThis).add(req);
    }
    private void getLoginInfo(final String token){
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                Logger.w(TAG,"login info res:"+dataStr);
                getProgressDialog().dismiss();
                DoctorInfo info=JSON.parseObject(dataStr,DoctorInfo.class);
                MyApplication.getInstance().mUserInfo=info;
                SpUtils.edit().putString(SpUtils.KEY_LAST_LOGIN_PHONE,etPhone.getText().toString()).apply();
                SpUtils.saveUser(token,info);
                ImSdk.getInstance().initUser(token,info.userId,info.name,info.name,info.avatar);
                startActivity(new Intent(mThis,MainActivity.class));
                finish();
            }
            @Override
            public void onError(String msg) {
                Logger.w(TAG,"login info err:"+msg);
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };

        String url=UrlConstants.getUrl(UrlConstants.GET_DOCTOR_INFO);
        ImCommonRequest req=new ImCommonRequest(url,null,RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        req.setAccessToken(token);
//        DCommonRequest req=new DCommonRequest(Method.POST,url, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener)){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers=super.getHeaders();
//                headers.put("access-token", token);
//                headers.put("content-type", "application/json");
//                return headers;
//            }
//        };
        VolleyUtil.getQueue(mThis).add(req);
    }

    @OnClick(R.id.tv_login_title)
    void onLoginTitleClicked() {
        if(clickTitle>3)
        {
            clickTitle = 0;
            ActionSheet.createBuilder(this,  getSupportFragmentManager())
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles("65环境(默认)", "37环境")
                    .setCancelableOnTouchOutside(false).setListener(titleListener).show();

        }
        else {
            clickTitle++;
        }
    }

    private ActionSheetListener titleListener=new ActionSheetListener() {
        @Override
        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

        }

        @Override
        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
            if (index == 0) {
                //测试环境
                ToastUtil.showToast(mThis,"已切换到65环境(默认)");
            } else if (index == 1) {
                //生产环境
                ToastUtil.showToast(mThis,"已切换到37环境");
            }
            SpUtils.edit().putInt(SpUtils.KEY_URL_ENV,index).apply();
            AppCommonUtils.changeEvn(index);
        }
    };
}
