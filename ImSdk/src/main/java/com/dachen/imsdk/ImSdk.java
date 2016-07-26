package com.dachen.imsdk;

import android.content.Context;
import android.text.TextUtils;

import com.dachen.imsdk.db.ImDbHelper;
import com.dachen.imsdk.net.PollingURLs;
import com.dachen.imsdk.out.ImObserverManager;
import com.dachen.imsdk.out.OnImSdkListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mcp on 2016/2/19.
 */
public class ImSdk {

    private static ImSdk mInstance;
    public String userId;
    public String userNick;
    public String accessToken;
    public String packageName;
    public Context context;
    public String userAvatar;
    public String userName;
    public String mAppDir;
    public String mVoicesDir;
    public String mVideosDir;
    public String mPicturesDir;
    public List<OnImEnvListener> mEnvListeners=new ArrayList<>();

    public static synchronized ImSdk getInstance(){
        if(mInstance==null){
            mInstance=new ImSdk();
        }
        return mInstance;
    }
    public void initSdk(Context context,String mAppDir,String mVoicesDir,String mVideosDir,String mPicturesDir){
        this.context=context;
        this.mAppDir=mAppDir;
        this.mVoicesDir=mVoicesDir;
        this.mVideosDir=mVideosDir;
        this.mPicturesDir=mPicturesDir;
        for(OnImEnvListener listener:mEnvListeners){
            listener.onInitContext();
        }
    }

    public void initUser(String accessToken,String userId,String userName,String userNick,String userAvatar){
        initUser(accessToken, userId, userName, userNick, userAvatar,false);
    }
    public void initUser(String accessToken,String userId,String userName,String userNick,String userAvatar,boolean initVideo){
        if(TextUtils.isEmpty(userId))
            return;
        this.accessToken=accessToken;
        this.userId=userId;
        this.userName=userName;
        this.userNick=userNick;
        this.userAvatar=userAvatar;
        ImDbHelper.getInstance(context,userId);
//        if(initVideo)
//            new InitVChatTasks(userId).execute();
    }

    public void changeAvatar(String avatar){
        this.userAvatar=avatar;
    }

    public void logout(){
        this.userId=null;
        this.accessToken=null;
//        mQavsdkControl.stopContext();
    }

    public void setImSdkListener(OnImSdkListener l){
        ImObserverManager.setImSdkListener(l);
    }

    public void changeIp(String baseIp){
//        VChatUtil.changeIp(baseIp);
        PollingURLs.changeIp(baseIp);
        for(OnImEnvListener listener:mEnvListeners){
            listener.onChangeIp(baseIp);
        }
    }

    public interface OnImEnvListener{
        void onInitContext();
        void onChangeIp(String baseIp);
    }

}
