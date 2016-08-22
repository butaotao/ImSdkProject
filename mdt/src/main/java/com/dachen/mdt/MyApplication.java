package com.dachen.mdt;

import android.app.Application;
import android.graphics.Bitmap.Config;
import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.dachen.imsdk.ImSdk;
import com.dachen.mdt.entity.DoctorInfo;
import com.dachen.mdt.util.AppImUtils;
import com.dachen.mdt.util.SpUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

/**
 * Created by Mcp on 2016/8/4.
 */
public class MyApplication extends Application {
    private static MyApplication instance;

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
        AppImUtils.initImAct();
        ImSdk.getInstance().initSdk(this,mAppDir,mVoicesDir,mVideosDir,mPicturesDir);
        UrlConstants.changIp("http://192.168.3.7:8101","192.168.3.7:8102");
//        UrlConstants.changIp("http://120.25.84.65:8101","120.25.84.65:8102");
        String token= SpUtils.getSp().getString(SpUtils.KEY_USER_TOKEN,null);
        String docStr=SpUtils.getSp().getString(SpUtils.KEY_USER_INFO,null);
        if(token!=null){
            DoctorInfo info= JSON.parseObject(docStr,DoctorInfo.class);
            mUserInfo=info;
            ImSdk.getInstance().initUser(token,info.userId,info.name,info.name,info.avatar);
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
}
