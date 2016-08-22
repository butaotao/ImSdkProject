package com.dachen.mdt.activity.me;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.util.AppCommonUtils;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        showVersion();
        findViewById(R.id.btn_logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_logout:
                AppCommonUtils.logout();
                break;
        }
    }

    private void showVersion(){
        TextView tvVersion= (TextView) findViewById(R.id.tv_version);
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            String version = info.versionName;
            tvVersion.setText("V" + version);
        }
    }
}
