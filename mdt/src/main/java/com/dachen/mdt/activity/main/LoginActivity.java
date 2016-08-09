package com.dachen.mdt.activity.main;

import android.os.Bundle;

import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_btn)
    public void clickLogin(){

    }
}
