package com.dachen.mdt.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dachen.mdt.R;
import com.dachen.mdt.entity.DoctorInfo;
import com.dachen.mdt.util.AppCommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;

/**
 * Created by Mcp on 2016/8/6.
 */
public class MeFragment extends BaseFragment {

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_telephone)
    TextView tvTelephone;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_me;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=super.onCreateView(inflater, container, savedInstanceState);
        DoctorInfo info= AppCommonUtils.getLoginUser();
        tvName.setText(info.name);
        tvTelephone.setText(info.telephone);
        ImageLoader.getInstance().displayImage(info.avatar,ivAvatar);
        return v;
    }


}
