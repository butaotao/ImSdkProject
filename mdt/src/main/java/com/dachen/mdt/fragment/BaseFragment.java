package com.dachen.mdt.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dachen.mdt.activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot1.event.EventBus;

/**
 * Created by Mcp on 2016/8/6.
 */
public abstract class BaseFragment extends Fragment {
    protected Unbinder mUnBinder;

    protected BaseActivity mParent;
    protected boolean isActive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParent= (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(getLayoutResource(),container,false);
        mUnBinder = ButterKnife.bind(this,v);
        if(useEventBus())
            EventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        if(useEventBus())
            EventBus.getDefault().unregister(this);
        mUnBinder.unbind();
        super.onDestroyView();
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

    protected abstract int getLayoutResource();

    protected boolean useEventBus(){
        return false;
    }
}
