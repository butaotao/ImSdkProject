package com.dachen.mdt.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dachen.mdt.R;
import com.dachen.mdt.activity.order.EditOrderCaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Mcp on 2016/8/6.
 */
public class OrderFragment extends BaseFragment {

    private Unbinder mUnbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_order,container,false);
        mUnbinder= ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.create_order)
    public void createOrder(){
        startActivity(new Intent(mParent, EditOrderCaseActivity.class));
    }
}
