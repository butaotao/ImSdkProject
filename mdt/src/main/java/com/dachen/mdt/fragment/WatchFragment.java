package com.dachen.mdt.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.adapter.WatchAdapter;
import com.dachen.mdt.entity.CanViewOrderResult;
import com.dachen.mdt.entity.MdtOptionResult;
import com.dachen.mdt.entity.event.PatientUpdateEvent;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import de.greenrobot1.event.EventBus;

/**
 * [围观页面]
 *
 * @author huxinwu
 * @version 1.0
 * @date 2016/9/13
 */
public class WatchFragment extends BaseFragment {

    @BindView(R.id.list_view)
    public ListView mListView;

    private List<CanViewOrderResult.OrderItem> list;
    private WatchAdapter watchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        list = new CopyOnWriteArrayList<>();
        watchAdapter = new WatchAdapter(getActivity(), list);
        mListView.setAdapter(watchAdapter);
        canViewOrderList();
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
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_watch;
    }

    public void onEventMainThread(PatientUpdateEvent event){

    }

    protected void canViewOrderList() {
        RequestHelperListener listener = new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                list = JSON.parseArray(dataStr, CanViewOrderResult.OrderItem.class);
                watchAdapter.update(list);
            }

            @Override
            public void onError(String msg) {
                ToastUtil.showToast(getActivity(), msg);
            }
        };

        String url = UrlConstants.getUrl(UrlConstants.CAN_VIEW_ORDER_LIST);
        Map<String, Object> reqMap = new HashMap<>();
        ImCommonRequest request = new ImCommonRequest(url, reqMap, RequestHelper.makeSucListener(false, listener), RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(getActivity()).add(request);
    }
}
