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
import butterknife.OnClick;
import de.greenrobot1.event.EventBus;

/**
 * Created by Mcp on 2016/8/6.
 */
public class OrderFragment extends BaseFragment {

    @BindView(R.id.list_view)
    public ImOrderListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListView.updateView();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_order;
    }

    @OnClick(R.id.create_order)
    public void createOrder(){
        startActivity(new Intent(mParent, EditOrderCaseActivity.class));
    }

    public void onEventMainThread(NewMsgEvent event) {
        mListView.updateView();
    }

}
