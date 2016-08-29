package com.dachen.mdt.activity.me;

import android.os.Bundle;

import com.dachen.imsdk.entity.event.NewMsgEvent;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.view.ImOrderListView;

/**
 * Created by Mcp on 2016/8/29.
 */
public class MyOrderActivity extends BaseActivity {

    public ImOrderListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        mListView= (ImOrderListView) findViewById(R.id.list_view);
        mListView.setFilterMine(true);
    }

    public void onEventMainThread(NewMsgEvent event) {
        mListView.updateView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mListView.updateView();
    }
}
