package com.dachen.mdt.activity.me;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.QiNiuUtils;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.gallery.CustomGalleryActivity;
import com.dachen.gallery.GalleryAction;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.net.UploadEngine7Niu;
import com.dachen.imsdk.net.UploadEngine7Niu.UploadObserver7NiuV2;
import com.dachen.imsdk.utils.CameraUtil;
import com.dachen.mdt.MyApplication;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.DoctorInfo;
import com.dachen.mdt.entity.event.AvatarChangeEvent;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.util.SpUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot1.event.EventBus;

public class MyInfoActivity extends BaseActivity {

    public static final int REQ_CODE_AVATAR=1;
    public static final int REQUEST_CODE_CROP_PIC =2;

    @BindView(R.id.tv_name)
    protected TextView tvName;
    @BindView(R.id.tv_telephone)
    protected TextView tvPhone;
    @BindView(R.id.tv_department)
    protected TextView tvDepartment;
    @BindView(R.id.tv_doc_title)
    protected TextView tvDocTitle;
    @BindView(R.id.tv_hospital)
    protected TextView tvHospital;
    @BindView(R.id.iv_avatar)
    protected ImageView ivAvatar;
    private Uri mNewPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        ButterKnife.bind(this);
        DoctorInfo me= MyApplication.getInstance().mUserInfo;
        if(me!=null){
            tvName.setText(me.name);
            tvHospital.setText(me.hospital);
            tvDocTitle.setText(me.title);
            tvPhone.setText(me.telephone);
            tvDepartment.setText(me.department);
            ImageLoader.getInstance().displayImage(me.avatar,ivAvatar);
        }
    }

    @OnClick(R.id.layout_avatar)
    public void chooseAvatar(){
        CustomGalleryActivity.openUi(mThis,false,REQ_CODE_AVATAR);
    }
    private void uploadAvatar(String path){
        getProgressDialog().show();
        UploadObserver7NiuV2 callBack=new UploadObserver7NiuV2() {
            @Override
            public void onUploadSuccess(String key, String url) {
                modifyAvatar(key);
            }

            @Override
            public void onUploadFailure(String msg) {
                getProgressDialog().dismiss();
                ToastUtil.showToast(mThis,"头像上传失败");
            }
        };
        UploadEngine7Niu.uploadFileCommon(path,callBack, QiNiuUtils.BUCKET_MDT_AVATAR,null);
    }

    private void modifyAvatar(String key){
        RequestHelperListener listener=new RequestHelperListener(){
            @Override
            public void onSuccess(String dataStr) {
                DoctorInfo info= JSON.parseObject(dataStr,DoctorInfo.class);
                MyApplication.getInstance().mUserInfo=info;
                SpUtils.saveUser(info);
                ImSdk.getInstance().changeAvatar(info.avatar);
                ToastUtil.showToast(mThis,"头像设置成功");
                ImageLoader.getInstance().displayImage(info.avatar,ivAvatar);
                EventBus.getDefault().post(new AvatarChangeEvent());
                getProgressDialog().dismiss();
            }

            @Override
            public void onError(String msg) {
                getProgressDialog().dismiss();
                ToastUtil.showToast(mThis,msg);
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.MODIFY_AVATAR);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("key",key);
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK)return;
        if(requestCode==REQ_CODE_AVATAR){
            String[] all_path = data.getStringArrayExtra(GalleryAction.INTENT_ALL_PATH);
            if (all_path == null||all_path.length==0) return;
            Uri o = Uri.fromFile(new File(all_path[0]));
            mNewPhotoUri = CameraUtil.getOutputMediaFileUri(mThis,CameraUtil.MEDIA_TYPE_IMAGE);
            CameraUtil.cropImage(this, o, mNewPhotoUri, REQUEST_CODE_CROP_PIC, 1, 1, 300, 300);
//            uploadAvatar(all_path[0]);
        } else if (requestCode == REQUEST_CODE_CROP_PIC) { // 裁减图片
                if (mNewPhotoUri != null) {
                    uploadAvatar(mNewPhotoUri.getPath());
                } else {
                    ToastUtil.showToast(mThis, R.string.c_crop_failed);
                }
        }
    }

}
