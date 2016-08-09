package com.dachen.mdt.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.dachen.mdt.activity.BaseActivity;

/**
 * Created by Mcp on 2016/8/6.
 */
public class BaseFragment extends Fragment {

    protected BaseActivity mParent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParent= (BaseActivity) getActivity();
    }
}
