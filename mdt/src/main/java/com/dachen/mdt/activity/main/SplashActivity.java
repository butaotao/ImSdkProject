package com.dachen.mdt.activity.main;

import android.content.Intent;
import android.os.Bundle;

import com.dachen.mdt.MyApplication;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        goNext();
    }

    private void goNext(){
        if(MyApplication.getInstance().mUserInfo!=null){
            startActivity(new Intent(this,MainActivity.class));
        }else{
            startActivity(new Intent(this,LoginActivity.class));
        }
        finish();
    }
}
