package com.dachen.imsdk.archive.download;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.dachen.common.utils.Md5Util;
import com.dachen.common.utils.UIHelper;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.archive.entity.ArchiveItem;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mcp on 2016/1/14.
 */
public class ArchiveLoader {
    public static final String ARCHIVE_DIR = "archive";

    private static ArchiveLoader instance;
    private Context mContext;
    private Map<String, ArchiveTaskInfo> taskInfoMap;
    private ExecutorService taskPool;

    private ArchiveLoader() {
        mContext = ImSdk.getInstance().context;
        taskInfoMap = new HashMap<>();
        taskPool = Executors.newFixedThreadPool(5);
    }

    public synchronized static ArchiveLoader getInstance() {
        if (instance == null)
            instance = new ArchiveLoader();
        return instance;
    }

    public File getDownloadFile(ArchiveItem item) {
        File dir = mContext.getExternalFilesDir(ARCHIVE_DIR);
        String fileName=makeFileKey(item.url);
        if(!TextUtils.isEmpty(item.suffix)){
            fileName+="."+item.suffix;
        }
        return new File(dir, fileName);
    }
    public File getTempFile(ArchiveItem item) {
        File dir = mContext.getExternalFilesDir(ARCHIVE_DIR);
        return new File(dir, makeFileKey(item.url)+".temp");
    }
    private String makeFileKey(String url){
        return Md5Util.toMD5(url);
    }

    public ArchiveTaskInfo getInfo(ArchiveItem item) {
        if(TextUtils.isEmpty(item.url)){
            return new ArchiveTaskInfo(ArchiveTaskInfo.STATE_NOT_DOWNLOAD);
        }
        if (taskInfoMap.get(item.url) != null) {
            return taskInfoMap.get(item.url);
        }
        File f = getDownloadFile(item);
        if (f.exists()) {
            return new ArchiveTaskInfo(ArchiveTaskInfo.STATE_DOWNLOAD_OK);
        }
        return new ArchiveTaskInfo(ArchiveTaskInfo.STATE_NOT_DOWNLOAD);
    }
    public void startDownload(ArchiveItem item){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            UIHelper.ToastMessage(mContext, "SD卡不能使用.无法下载");
            return;
        }
        ArchiveTaskInfo info=getInfo(item);
        if(info.state!=ArchiveTaskInfo.STATE_NOT_DOWNLOAD){
            return;
        }
        info.state=ArchiveTaskInfo.STATE_IN_DOWNLOADIN;
        info.canceled=false;
        info.url=item.url;
        ArchiveDownloadTask task=new ArchiveDownloadTask(item);
        taskInfoMap.put(item.url,info);
        taskPool.submit(task);
    }
    public void cancelDownload(ArchiveItem item){
        ArchiveTaskInfo info=taskInfoMap.get(item.url);
        if(info==null){
            return;
        }
        info.canceled=true;
        info.state=ArchiveTaskInfo.STATE_NOT_DOWNLOAD;
    }

    public static class DownloadChangeEvent{
        public String url;

        public DownloadChangeEvent(String url) {
            this.url = url;
        }
    }
}
