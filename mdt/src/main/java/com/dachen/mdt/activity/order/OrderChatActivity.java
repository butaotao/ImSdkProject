package com.dachen.mdt.activity.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.StringUtils;
import com.dachen.common.utils.TimeUtils;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.UIHelper;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.entity.MoreItem;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.AppBaseChatActivity;
import com.dachen.mdt.entity.OrderChatParam;
import com.dachen.mdt.entity.OrderDetailVO;
import com.dachen.mdt.entity.OrderStatusResult;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mcp on 2016/8/9.
 */
public class OrderChatActivity extends AppBaseChatActivity {

    protected PopupWindow mPopWindow;
    protected PopupWindow mCoverPop;
    protected View mHeader;
    protected Button btnExBar;
    protected TextView tvExInfo;
    protected ImageView ivExCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    private void initView(){
        if(TextUtils.isEmpty(groupPo.param)){
            mHeader.setVisibility(View.GONE);
            return;
        }
        OrderChatParam param=JSON.parseObject(groupPo.param,OrderChatParam.class);
        String curUserId= ImUtils.getLoginUserId();
        if(curUserId.equals(param.creator)||curUserId.equals(param.leader)||groupPo.bizStatus==2)return;
        fetchInfo(param.orderId);
    }

    @Override
    protected View onLoadHeaderLayout(ViewGroup parent) {
        return getLayoutInflater().inflate(R.layout.order_chat_header,parent,false);
    }

    @Override
    protected void onHeaderLayoutLoaded(View view) {
        mHeader=view.findViewById(R.id.header_container);
        btnExBar= (Button) view.findViewById(R.id.btn_ex);
        tvExInfo= (TextView) view.findViewById(R.id.tv_info);
        ivExCheck= (ImageView) view.findViewById(R.id.iv_check);
        view.findViewById(R.id.right_btn).setOnClickListener(this);
        view.findViewById(R.id.back_btn).setOnClickListener(this);
        btnExBar.setOnClickListener(exBtnClickListener);
        TextView tvTitle= (TextView) view.findViewById(R.id.title);
        tvTitle.setText(getIntent().getStringExtra(INTENT_EXTRA_GROUP_NAME));
    }

    @Override
    protected List<MoreItem> getMorePanelData(int page) {
        List<MoreItem> items = new ArrayList<MoreItem>();
        items.add(new MoreItem(getString(R.string.chat_poto), R.drawable.im_tool_photo_button_bg));
        items.add(new MoreItem(getString(R.string.chat_camera), R.drawable.im_tool_camera_button_bg));
        return items;
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
        View contentView = getLayoutInflater().inflate(R.layout.order_pop_menu, null);
        contentView.findViewById(R.id.ll_order_case).setOnClickListener(popClickListener);
        contentView.findViewById(R.id.ll_expert).setOnClickListener(popClickListener);
        mPopWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setFocusable(true);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.showAsDropDown(v, 0, 20);
    }

    private OnClickListener popClickListener =new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(TextUtils.isEmpty(groupPo.param)){
                return;
            }
            OrderChatParam param=JSON.parseObject(groupPo.param,OrderChatParam.class);
            Intent i=null;
            switch (v.getId()){
                case R.id.ll_order_case:
                    i=new Intent(mThis,ViewOrderCaseActivity.class);
                    break;
                case R.id.ll_expert:
                    i=new Intent(mThis,OrderExpertActivity.class);
                    break;
            }
            if(i!=null){
                i.putExtra(AppConstants.INTENT_ORDER_ID,param.orderId);
                startActivity(i);
            }
            mPopWindow.dismiss();
        }
    };

    private OnClickListener exBtnClickListener =new OnClickListener() {
        @Override
        public void onClick(View v) {
            OrderChatParam param=JSON.parseObject(groupPo.param,OrderChatParam.class);
            if(groupPo.bizStatus==2){
                // TODO: 2016/8/10 查看报告
                Intent i=new Intent(mThis,ViewOrderReportActivity.class);
                i.putExtra(AppConstants.INTENT_ORDER_ID,param.orderId);
                startActivity(i);
            }else if(groupPo.bizStatus==1){
                Intent i=new Intent(mThis,ViewOrderSummaryActivity.class);
                i.putExtra(AppConstants.INTENT_ORDER_ID,param.orderId);
                i.putExtra(AppConstants.INTENT_IS_LEADER,isLeader(param));
                startActivity(i);
            }
        }
    };

    public static void openUI(Context context, String groupName, String groupId) {
        Intent intent = new Intent(context, OrderChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_EXTRA_GROUP_NAME, groupName);
        intent.putExtra(INTENT_EXTRA_GROUP_ID, groupId);
        context.startActivity(intent);
    }

    private void showConfirmPop(OrderDetailVO vo) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.order_confirm_window, null);
        initOrderInfo(contentView,vo);
        contentView.findViewById(R.id.btn_cancel).setOnClickListener(orderConfirmClickListener);
        contentView.findViewById(R.id.btn_confirm).setOnClickListener(orderConfirmClickListener);
        mCoverPop = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT, true);
        mCoverPop.setFocusable(false);
        mCoverPop.setBackgroundDrawable(new BitmapDrawable());
        UIHelper.showPopAsDropdown(mHeader, mCoverPop, 0, 0);
    }
    private OnClickListener orderConfirmClickListener =new OnClickListener() {
        @Override
        public void onClick(View v) {
            OrderChatParam param=JSON.parseObject(groupPo.param,OrderChatParam.class);
            switch (v.getId()){
                case R.id.btn_cancel:
                    confirmOrder(false,param.orderId);
                    break;
                case R.id.btn_confirm:
                    confirmOrder(true,param.orderId);
                    break;
            }
        }
    };
    private void confirmOrder(final boolean accept, String mOrderId){
        Map<String,Object> map=new HashMap<>();
        map.put("orderId",mOrderId);
        String url= UrlConstants.getUrl(accept?UrlConstants.ACCEPT_ORDER:UrlConstants.REFUSE_ORDER);
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                mCoverPop.dismiss();
                if(!accept){
                    finish();
                }
            }
        };
        ImCommonRequest req=new ImCommonRequest(url,map, RequestHelper.makeSucListener(true,listener),RequestHelper.makeErrorListener(listener) );
        VolleyUtil.getQueue(mThis).add(req);
    }

    public static void initOrderInfo(View v, OrderDetailVO vo){
        ((TextView) v.findViewById(R.id.tv_patient_info)).setText(String.format("%s,%s,%s岁",vo.patient.name, StringUtils.getSexStr(vo.patient.sex),vo.patient.age) );
        ((TextView) v.findViewById(R.id.tv_patient_num)).setText(vo.patient.number);
        ((TextView) v.findViewById(R.id.tv_mdt_num)).setText(vo.mdtNum);
        ((TextView) v.findViewById(R.id.tv_purpose)).setText(vo.target);
        ((TextView) v.findViewById(R.id.tv_first_diagnose)).setText(vo.disease.firstDiag);
        ((TextView) v.findViewById(R.id.tv_chief_complaint)).setText(vo.disease.desc);
        ((TextView) v.findViewById(R.id.tv_present_history)).setText(vo.disease.diseaseNow);
        ((TextView) v.findViewById(R.id.tv_previous_history)).setText(vo.disease.diseaseOld);
        ((TextView) v.findViewById(R.id.tv_family_history)).setText(vo.disease.diseaseFamily);
        ((TextView) v.findViewById(R.id.tv_personal_history)).setText(vo.disease.diseaseSelf);
        ((TextView) v.findViewById(R.id.tv_body_sign)).setText(vo.disease.symptom);
        ((TextView) v.findViewById(R.id.tv_examine_result)).setText(vo.disease.result);
        ((TextView) v.findViewById(R.id.tv_treat_process)).setText(vo.disease.checkProcess);
        ((TextView) v.findViewById(R.id.tv_end_time)).setText(TimeUtils.a_format.format(new Date(vo.expectEndTime)) );
    }

    private void fetchInfo(String orderId){
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                OrderStatusResult res= JSON.parseObject(dataStr,OrderStatusResult.class);
                if(res.status==0){
                    showConfirmPop(res.orderDetail);
                }
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.GET_STATUS);
        Map<String,Object> reqMap=new HashMap<>();
        reqMap.put("orderId",orderId);
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
    }

    @Override
    protected void onBusinessData() {
        if(TextUtils.isEmpty(groupPo.param)){
            mHeader.setVisibility(View.GONE);
            return;
        }
        OrderChatParam param=JSON.parseObject(groupPo.param,OrderChatParam.class);
        mHeader.setVisibility(View.VISIBLE);
        if(groupPo.bizStatus==2){
            tvExInfo.setText("本次会诊已完成");
            ivExCheck.setVisibility(View.VISIBLE);
            btnExBar.setText("查看报告");
        }else{
            ivExCheck.setVisibility(View.GONE);
            long endTs=param.endTime;
            String text="结束时间:"+ TimeUtils.a_format.format(new Date(endTs));
            tvExInfo.setText(text);
            btnExBar.setText(isLeader(param)?"撰写报告":"撰写小结");
        }
    }
    private boolean isLeader( OrderChatParam param){
        return ImUtils.getLoginUserId().equals(param.leader);
    }

    @Override
    protected int chatType() {
        return CHAT_TYPE_GROUP;
    }
}
