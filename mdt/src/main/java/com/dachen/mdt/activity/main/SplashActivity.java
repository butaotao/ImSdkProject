package com.dachen.mdt.activity.main;

import android.content.Intent;
import android.os.Bundle;

import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
