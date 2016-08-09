package com.dachen.mdt.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dachen.imsdk.entity.event.NewMsgEvent;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.order.EditOrderCaseActivity;
import com.dachen.mdt.view.ImOrderListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.greenrobot1.event.EventBus;

/**
 * Created by Mcp on 2016/8/6.
 */
public class OrderFragment extends BaseFragment {
    private Unbinder mUnBinder;
    @BindView(R.id.list_view)
    public ImOrderListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_order,container,false);
        mUnBinder = ButterKnife.bind(this,v);
        EventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        mUnBinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListView.updateView();
    }

    @OnClick(R.id.create_order)
    public void createOrder(){
        startActivity(new Intent(mParent, EditOrderCaseActivity.class));
    }

    public void onEventMainThread(NewMsgEvent event) {
        mListView.updateView();
    }

}
