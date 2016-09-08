package com.dachen.mdt.activity.me;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.dachen.common.widget.CustomDialog;
import com.dachen.common.widget.CustomDialog.CustomClickEvent;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.VersionInfo;

/**
 * Created by Mcp on 2016/9/5.
 */
public class VersionAlertActivity extends BaseActivity {
    public static final String INTENT_VERSION_INFO="version";

    private static VersionAlertActivity instance;
    private VersionInfo mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(instance!=null){
            instance.finish();
        }
        instance=this;

        if(mInfo==null){
            finish();
            return;
        }
        showDialog();
    }

    private void showDialog(){
        CustomClickEvent event=new CustomClickEvent() {
            @Override
            public void onDismiss(CustomDialog customDialog) {
                finish();
            }
            @Override
            public void onClick(CustomDialog customDialog) {
                startDownload(mInfo.downloadUrl, mInfo.version);
                finish();
            }
        };
        String msg=String.format("软件有新的版本%s,是否下载！", mInfo.version);
        Dialog d=new CustomDialog.Builder(this, event).setMessage(msg).setPositive("下载").setNegative("取消").create();
        d.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        d.show();
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
    public static void openUi(Context context, VersionInfo info){
        Intent i=new Intent(context, VersionAlertActivity.class);
        i.putExtra(INTENT_VERSION_INFO, info);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
