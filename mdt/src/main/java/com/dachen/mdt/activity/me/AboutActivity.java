package com.dachen.mdt.activity.me;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.async.SimpleResultListenerV2;
import com.dachen.common.utils.DeviceInfoUtil;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.service.ImRequestManager;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.VersionInfo;

public class AboutActivity extends BaseActivity {

    private TextView tvCheckVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tvCheckVersion= (TextView) findViewById(R.id.versionUpdate);
        tvCheckVersion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.versionUpdate:
                checkVersion();
                break;
        }
    }

    private void checkVersion(){
        SimpleResultListenerV2 listener=new SimpleResultListenerV2() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                VersionInfo info= JSON.parseObject(dataStr,VersionInfo.class);
                String curVersion= DeviceInfoUtil.getVersionName(mThis);
                if(info.version.compareTo(curVersion)>0){
                    ToastUtil.showToast(mThis,"有新版本");
                }else{
                    ToastUtil.showToast(mThis,"当前版本已是最新");
                }
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.GET_VERSION);
        ImCommonRequest request=new ImCommonRequest(url,null, ImRequestManager.makeSucListener(listener),ImRequestManager.makeErrListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    private void startDownload(String url,String version) {
        DownloadManager mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //设置通知栏标题
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("正在下载");
        request.setDescription("MDT");
        request.setMimeType("application/vnd.android.package-archive");
        String fileName="MDT_"+version;
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        mDownloadManager.enqueue(request);
    }
}
