package com.dachen.mdt.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.dachen.mdt.R;

/**
 * Created by Mcp on 2016/8/4.
 */
public class BaseActivity extends FragmentActivity {

    protected ProgressDialog mProDialog;
    protected BaseActivity mThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis=this;
    }

    public synchronized Dialog getProgressDialog(){
        if(mProDialog==null){
            mProDialog = new ProgressDialog(this, R.style.IMDialog);
            mProDialog.setCanceledOnTouchOutside(false);
            mProDialog.setMessage("正在加载");
        }
        return mProDialog;
    }
}
