package com.dachen.mdt.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.dachen.mdt.activity.BaseActivity;

/**
 * Created by Mcp on 2016/8/6.
 */
public class BaseFragment extends Fragment {

    protected BaseActivity mParent;
    protected boolean isActive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParent= (BaseActivity) getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive=false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive=true;
    }
}
