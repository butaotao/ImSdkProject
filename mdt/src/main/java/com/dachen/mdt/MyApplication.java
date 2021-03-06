package com.dachen.mdt;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.dachen.common.DCommonSdk;
import com.dachen.common.utils.Logger;
import com.dachen.imsdk.ImSdk;
import com.dachen.mdt.entity.DoctorInfo;
import com.dachen.mdt.push.MIPushApplication;
import com.dachen.mdt.util.AppCommonUtils;
import com.dachen.mdt.util.AppImUtils;
import com.dachen.mdt.util.SpUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.File;
import java.util.List;

/**
 * Created by Mcp on 2016/8/4.
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    private static final String TAG = MyApplication.class.getSimpleName();
    public DoctorInfo mUserInfo;

    public String mAppDir = Environment.getExternalStorageDirectory() + "/Android/data/com.dachen.dgroupdoctor";
    public String mPicturesDir = mAppDir + "/pictures";
    public String mVoicesDir = mAppDir + "/voices";
    public String mVideosDir = mAppDir + "/videos";
    public String mFilesDir = mAppDir + "/files";


    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        initAppDir();
        initImageLoader();

        DCommonSdk.initSdk(this,"eMDT");
        AppImUtils.initImAct();
        ImSdk.getInstance().initSdk(this,mAppDir,mVoicesDir,mVideosDir,mPicturesDir);
        int env=SpUtils.getSp().getInt(SpUtils.KEY_URL_ENV,0);
        AppCommonUtils.changeEvn(env);
        String token= SpUtils.getSp().getString(SpUtils.KEY_USER_TOKEN,null);
        String docStr=SpUtils.getSp().getString(SpUtils.KEY_USER_INFO,null);
        if(token!=null){
            DoctorInfo info= JSON.parseObject(docStr,DoctorInfo.class);
            mUserInfo=info;
            ImSdk.getInstance().initUser(token,info.userId,info.name,info.name,info.avatar);
        }

        if(shouldInit()){
            MiPushClient.registerPush(this, MIPushApplication.getID(), MIPushApplication.getKey());
        }
        if (Logger.isDebug()) {
            openXIAOMILog();  //打开小米debug日志
        } else {
            closeXIAOMILog(); //关闭小米debug日志
        }
    }

    private void initAppDir() {

        File file = getExternalFilesDir(null);
        if (file == null) {
            // 没有则，主动创建。
            new File(mAppDir).mkdirs();
            new File(mPicturesDir).mkdirs();
            new File(mVoicesDir).mkdirs();
            new File(mVideosDir).mkdirs();
            new File(mFilesDir).mkdirs();
            return;
        }else{
        }

        if (!file.exists()) {
            file.mkdirs();
        }

        mAppDir = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!file.exists()) {
            file.mkdirs();
        }
        mPicturesDir = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (!file.exists()) {
            file.mkdirs();
        }
        mVoicesDir = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (!file.exists()) {
            file.mkdirs();
        }
        mVideosDir = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (!file.exists()) {
            file.mkdirs();
        }
        mFilesDir = file.getAbsolutePath();
    }

    private void initImageLoader() {

        DisplayImageOptions mNormalImageOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .showImageOnLoading(R.drawable.defaultpic)
                .showImageForEmptyUri(R.drawable.image_download_fail_icon)
                .showImageOnFail(R.drawable.image_download_fail_icon)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(mNormalImageOptions)
                // .denyCacheImageMultipleSizesInMemory()
                //		.discCache(new TotalSizeLimitedDiscCache(new File(mPicturesDir), 50 * 1024 * 1024))
                .diskCache(new UnlimitedDiscCache(new File(mPicturesDir)))
                // 最多缓存50M的图片
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                //		.memoryCache((MemoryCache) memoryCache)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(4)
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
//		Picasso p=new Picasso.Builder(this).loggingEnabled(true).build();
//		Picasso.setSingletonInstance(p);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 小米推送日志
     */
    public void openXIAOMILog() {
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        com.xiaomi.mipush.sdk.Logger.setLogger(this, newLogger);
    }

    public void closeXIAOMILog() {
        com.xiaomi.mipush.sdk.Logger.disablePushFileLog(getInstance());
    }
}
