package com.dachen.imsdk.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.dachen.imsdk.R;

/**
 * Created by Mcp  on 2016/2/25.
 */
public class ImBaseActivity extends FragmentActivity {

    private static final String TAG = ImBaseActivity.class.getSimpleName();
    public static BaseActRunnable ON_CREATE_RUN;
    public static BaseActRunnable ON_RESUME_RUN;
    public static BaseActRunnable ON_PAUSE_RUN;
    public static BaseActRunnable ON_DESTROY_RUN;

    protected Activity mThis;
    public ProgressDialog mDialog;
    public boolean isCurrentShow = false;
    public Object actExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis=this;
        initProgressDialog();
        tryRun(ON_CREATE_RUN);
    }

    private void initProgressDialog(){
        mDialog = new ProgressDialog(this, R.style.IMDialog);
        //		mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("正在加载");
    }
    @Override
    protected void onResume() {
        super.onResume();
        tryRun(ON_RESUME_RUN);
        isCurrentShow=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        tryRun(ON_PAUSE_RUN);
        isCurrentShow=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tryRun(ON_DESTROY_RUN);
    }

    private void tryRun(BaseActRunnable r){
        if(r!=null)
            r.run(this);
    }

    public interface BaseActRunnable{
        void run(ImBaseActivity act);
    }
}
