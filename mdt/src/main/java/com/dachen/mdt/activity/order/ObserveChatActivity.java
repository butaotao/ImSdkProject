package com.dachen.mdt.activity.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.imsdk.adapter.ChatAdapterV2;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.MoreItem;
import com.dachen.imsdk.out.ImMsgHandler;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.AppBaseChatActivity;
import com.dachen.mdt.activity.order.summary.ViewOrderSummaryActivity;
import com.dachen.mdt.entity.OrderChatParam;
import com.dachen.mdt.tools.MdtImMsgHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mcp on 2016/8/9.
 */
public class ObserveChatActivity extends AppBaseChatActivity {

    protected PopupWindow mPopWindow;
    protected View mHeader;
    protected View mRightBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    private void initView(){
        mChatBottomView.setVisibility(View.GONE);
    }

    @Override
    protected View onLoadHeaderLayout(ViewGroup parent) {
        return getLayoutInflater().inflate(R.layout.order_chat_header,parent,false);
    }

    @Override
    protected void onHeaderLayoutLoaded(View view) {
        mHeader=view.findViewById(R.id.header_container);
        mRightBtn=view.findViewById(R.id.right_btn);
        view.findViewById(R.id.layout_header_extra).setVisibility(View.GONE);
        mRightBtn.setOnClickListener(this);
        view.findViewById(R.id.back_btn).setOnClickListener(this);
        TextView tvTitle= (TextView) view.findViewById(R.id.title);
        tvTitle.setText(getIntent().getStringExtra(INTENT_EXTRA_GROUP_NAME));
    }

    @Override
    protected List<MoreItem> getMorePanelData(int page) {
        return new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.right_btn:
                onRightMenuClick(v);
                break;

        }
    }

    @Override
    protected void onRightMenuClick(View v) {
        if(mPopWindow==null){
            View contentView = getLayoutInflater().inflate(R.layout.order_pop_menu, null);
            contentView.findViewById(R.id.ll_order_case).setOnClickListener(popClickListener);
            contentView.findViewById(R.id.ll_expert).setOnClickListener(popClickListener);
            contentView.findViewById(R.id.layout_summary).setOnClickListener(popClickListener);
            mPopWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopWindow.setFocusable(true);
            mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        mPopWindow.showAsDropDown(v, 0, 20);
    }

    private OnClickListener popClickListener =new OnClickListener() {
        @Override
        public void onClick(View v) {
            mPopWindow.dismiss();
            if(TextUtils.isEmpty(groupPo.param)){
                return;
            }
            OrderChatParam param=JSON.parseObject(groupPo.param,OrderChatParam.class);
            Intent i=null;
            switch (v.getId()){
                case R.id.ll_order_case:
                    i=new Intent(mThis,ViewOrderCaseActivity.class);
                    i.putExtra(ViewOrderCaseActivity.KEY_EDITABLE,groupPo.bizStatus==1);
                    break;
                case R.id.ll_expert:
                    i=new Intent(mThis,OrderExpertActivity.class);
                    break;
                case R.id.layout_summary:
                    i=new Intent(mThis,ViewOrderSummaryActivity.class);
                    i.putExtra(AppConstants.INTENT_ORDER_ID,param.orderId);
                    i.putExtra(AppConstants.INTENT_DISEASE_TOP_ID,param.topDiseaseId);
                    i.putExtra(AppConstants.INTENT_IS_LEADER,false);
                    i.putExtra(AppConstants.INTENT_MY_ORDER_STATUS,-2);
                    break;
            }
            if(i!=null){
                i.putExtra(AppConstants.INTENT_ORDER_ID,param.orderId);
                startActivity(i);
            }

        }
    };

    public static void openUI(Context context, ChatGroupPo po) {
        Intent intent = new Intent(context, ObserveChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_EXTRA_GROUP_NAME, po.name);
        intent.putExtra(INTENT_EXTRA_GROUP_ID, po.groupId);
        intent.putExtra(INTENT_EXTRA_GROUP_PARAM, po);
        intent.putExtra(INTENT_EXTRA_IS_OBSERVE, true);
        context.startActivity(intent);
    }


    @Override
    protected void onBusinessData() {
        pollImmediately();
        updateBusinessDelay();
    }

    @Override
    protected int chatType() {
        return CHAT_TYPE_GROUP;
    }

    @Override
    protected ImMsgHandler makeMsgHandler() {
        return new MdtImMsgHandler(this) {
            @Override
            public void onClickMpt8(ChatMessagePo chatMessage, ChatAdapterV2 adapter, View v) {
            }
        };
    }

}
