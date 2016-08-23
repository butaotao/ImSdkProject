package com.dachen.mdt.activity.me;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.RequestQueue;
import com.dachen.common.utils.StringUtils;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.common.widget.PasswordView;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.HashMap;
import java.util.Map;

public class ModifyPwdActivity extends BaseActivity {


    PasswordView mOldPwdView;
    PasswordView mNewPwdView;
    PasswordView mConfirmPwdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        initView();
    }

    private void initView() {
        mOldPwdView= (PasswordView) findViewById(R.id.et_pwd_old);
        mNewPwdView= (PasswordView) findViewById(R.id.et_pwd_new);
        mConfirmPwdView= (PasswordView) findViewById(R.id.et_pwd_confirm);
        mOldPwdView.setHint("请输入旧密码");
        mNewPwdView.setHint("请输入新密码");
        mConfirmPwdView.setHint("请输入确认密码");
        mOldPwdView.hideEyeImageView(true);
    }

    @Override
    public void onRightClick(View v) {
        final String oldPwd = mOldPwdView.getText().toString();
        final String newPwd = mNewPwdView.getText().toString();
        String confirmPwd = mConfirmPwdView.getText().toString();

        if (TextUtils.isEmpty(oldPwd)) {
            mOldPwdView.requestFocus();
            ToastUtil.showToast(this, "请输入旧密码");
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            mNewPwdView.requestFocus();
            ToastUtil.showToast(this, "请输入新密码");
            return;
        }
        if (!StringUtils.isPassWord(newPwd)) {
            mNewPwdView.requestFocus();
            ToastUtil.showToast(this, R.string.wrong_password);
            return;
        }
		if (TextUtils.isEmpty(confirmPwd)) {
			mConfirmPwdView.requestFocus();
			ToastUtil.showToast(this, "请输入确认密码");
			return;
		}
		if (!newPwd.equals(confirmPwd)) {
			mConfirmPwdView.requestFocus();
			ToastUtil.showToast(getApplicationContext(), "两次密码输入不一致");
			return;
		}


        getProgressDialog().show();
        RequestHelperListener listener=new RequestHelperListener(){
            @Override
            public void onSuccess(String dataStr) {
                ToastUtil.showToast(mThis, "修改密码成功");
                finish();
            }

            @Override
            public void onError(String msg) {
                getProgressDialog().dismiss();
                ToastUtil.showToast(mThis, "修改密码失败:"+msg);
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("oldPwd",oldPwd);
        map.put("newPwd",newPwd);
        String url=UrlConstants.getUrl(UrlConstants.UPDATE_PWD);
        ImCommonRequest request=new ImCommonRequest( url,map, RequestHelper.makeSucListener(true,listener),RequestHelper.makeErrorListener(listener));
        RequestQueue queue = VolleyUtil.getQueue(this);
        queue.add(request);

    }
}
