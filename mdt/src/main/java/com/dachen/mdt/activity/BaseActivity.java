package com.dachen.mdt.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.dachen.imsdk.net.ImPolling;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.main.LoginActivity;
import com.dachen.mdt.activity.main.SplashActivity;

/**
 * Created by Mcp on 2016/8/4.
 */
public abstract class BaseActivity extends FragmentActivity {

    private ProgressDialog mProDialog;
    protected BaseActivity mThis;
    protected boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis=this;
    }

    public synchronized Dialog getProgressDialog(){
        if(mProDialog==null){
            mProDialog = new ProgressDialog(this, R.style.IMDialog);
            mProDialog.setCanceledOnTouchOutside(false);
            mProDialog.setMessage("请稍等...");
        }
        return mProDialog;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive=true;
        if(this instanceof SplashActivity|| this instanceof LoginActivity)
            return;
        ImPolling.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive=false;
        ImPolling.getInstance().onPause();
    }

    public void onLeftClick(View v) {
        finish();
    }
}
