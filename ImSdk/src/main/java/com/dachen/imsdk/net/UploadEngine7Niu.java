package com.dachen.imsdk.net;

import com.alibaba.fastjson.JSON;
import com.dachen.common.toolbox.QiniuUploadTask;
import com.dachen.common.utils.QiNiuUtils;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.utils.ImSpUtils;
import com.qiniu.android.storage.UploadManager;

/**
 * Created by Mcp on 2016/1/7.
 */
public class UploadEngine7Niu {

    public static final String TAG = UploadEngine7Niu.class.getSimpleName();
    private static UploadManager upManager;

    private static synchronized UploadManager getUploadManager() {
        if (upManager == null) {
            upManager = new UploadManager();
        }
        return upManager;
    }

    public interface FileUploadObserver7Niu {
        void onFileUploadSuccess(ChatMessagePo entity);

        void onFileUploadFailure(ChatMessagePo entity);
    }

    public interface UploadObserver7Niu {
        void onUploadSuccess(String url);

        void onUploadFailure(String msg);
    }

    public interface UploadProgress7Niu {
        void onProgress(double progress);
    }

    public static void uploadFile(final ChatMessagePo chatMessage, final FileUploadObserver7Niu observer, final UploadProgress7Niu progressCallBack) {
        String filePath = ImSpUtils.getMsgFilePath(chatMessage.clientMsgId);
        QiniuUploadTask.UpListener tListener = new QiniuUploadTask.UpListener() {
            @Override
            public void onFileUploadSuccess(String bucket, String key) {
                com.alibaba.fastjson.JSONObject obj = JSON.parseObject(chatMessage.param);
                obj.put("uri", QiNiuUtils.getFileUrl(QiNiuUtils.BUCKET_MSG, key));
                obj.put("key", key);
                chatMessage.param = obj.toJSONString();
                observer.onFileUploadSuccess(chatMessage);
            }

            @Override
            public void onFileUploadFailure(String msg) {
                observer.onFileUploadFailure(chatMessage);
            }

            @Override
            public void onFileUploadProgress(double progress) {
                if (progressCallBack != null) {
                    progressCallBack.onProgress(progress);
                }
            }
        };
        QiniuUploadTask task = new QiniuUploadTask(filePath, QiNiuUtils.BUCKET_MSG, tListener, PollingURLs.getUploadToken(), ImSdk.getInstance().accessToken, ImSdk.getInstance().context);
        task.execute();
    }

    public static void uploadPatientFile(final String filePath, final UploadObserver7Niu observer) {
        uploadFileCommon(filePath, observer, QiNiuUtils.BUCKET_PATIENT);

    }

    public static void uploadPatientFile(final String filePath, final UploadObserver7Niu observer, final UploadProgress7Niu progressCallBack) {
        uploadFileCommon(filePath, observer, QiNiuUtils.BUCKET_PATIENT, progressCallBack);

    }

    public static void uploadVoiceFile(final String filePath, final UploadObserver7Niu observer) {
        uploadFileCommon(filePath, observer, QiNiuUtils.BUCKET_TEL_RECORD);
    }

    public static QiniuUploadTask uploadDoctorFile(final String filePath, final UploadObserver7Niu observer) {
        return uploadFileCommon(filePath, observer, QiNiuUtils.BUCKET_DOCTOR);
//        QiniuUploadTask.UpListener tListener = new QiniuUploadTask.UpListener() {
//            @Override
//            public void onFileUploadSuccess(String bucket, String key) {
//                String uri = QiNiuUtils.getFileUrl(QiNiuUtils.BUCKET_DOCTOR, key);
//                observer.onUploadSuccess(uri);
//            }
//            @Override
//            public void onFileUploadFailure(String msg) {
//                observer.onUploadFailure(msg);
//            }
//        };
//        QiniuUploadTask task = new QiniuUploadTask(filePath, QiNiuUtils.BUCKET_DOCTOR,tListener,PollingURLs.getUploadToken(), ImSdk.getInstance().accessToken,ImSdk.getInstance().context);
//        task.execute();
//        return task;
    }

    public static QiniuUploadTask uploadDoctorFile(final String filePath, final UploadObserver7Niu observer, final UploadProgress7Niu progressCallBack) {
        return uploadFileCommon(filePath, observer, QiNiuUtils.BUCKET_DOCTOR, progressCallBack);
    }

    public static QiniuUploadTask uploadFileCommon(final String filePath, final UploadObserver7Niu observer, final String bucket) {
        return uploadFileCommon(filePath, observer, bucket, null);
    }

    public static QiniuUploadTask uploadFileCommon(final String filePath, final UploadObserver7Niu observer, final String bucket, final UploadProgress7Niu progressCallBack) {
        QiniuUploadTask.UpListener tListener = new QiniuUploadTask.UpListener() {
            @Override
            public void onFileUploadSuccess(String bucket, String key) {
                String uri = QiNiuUtils.getFileUrl(bucket, key);
                observer.onUploadSuccess(uri);
            }

            @Override
            public void onFileUploadFailure(String msg) {
                observer.onUploadFailure(msg);
            }

            @Override
            public void onFileUploadProgress(double progress) {
                if (progressCallBack != null) {
                    progressCallBack.onProgress(progress);
                }
            }
        };
        QiniuUploadTask task = new QiniuUploadTask(filePath, bucket, tListener, PollingURLs.getUploadToken(), ImSdk.getInstance().accessToken, ImSdk.getInstance().context);
        task.execute();
        return task;
    }
}
