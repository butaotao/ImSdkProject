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
import com.dachen.common.utils.TimeUtils;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.UIHelper;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.common.widget.CustomDialog;
import com.dachen.common.widget.CustomDialog.CustomClickEvent;
import com.dachen.imsdk.adapter.ChatAdapterV2;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.ImgTextMsgV2;
import com.dachen.imsdk.entity.MoreItem;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.out.ImMsgHandler;
import com.dachen.imsdk.utils.ImUtils;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.AppBaseChatActivity;
import com.dachen.mdt.activity.order.summary.ViewOrderSummaryActivity;
import com.dachen.mdt.entity.OrderChatParam;
import com.dachen.mdt.entity.OrderDetailVO;
import com.dachen.mdt.entity.OrderStatusResult;
import com.dachen.mdt.entity.event.SubmitSummaryEvent;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.tools.MdtImMsgHandler;
import com.dachen.mdt.util.ViewUtils;

import java.text.SimpleDateFormat;
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
    protected View mHeaderExtra;
    protected View mRightBtn;
    protected Button btnExBar;
    protected TextView tvExInfo;
    protected ImageView ivExCheck;
    private int myStatus=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    private void initView(){
        if(TextUtils.isEmpty(groupPo.param)){
            mHeaderExtra.setVisibility(View.GONE);
            return;
        }
        OrderChatParam param=JSON.parseObject(groupPo.param,OrderChatParam.class);
//        String curUserId= ImUtils.getLoginUserId();
        if(groupPo.bizStatus==2)return;
        fetchStatus(param.orderId);
    }

    @Override
    protected View onLoadHeaderLayout(ViewGroup parent) {
        return getLayoutInflater().inflate(R.layout.order_chat_header,parent,false);
    }

    @Override
    protected void onHeaderLayoutLoaded(View view) {
        mHeader=view.findViewById(R.id.header_container);
        mHeaderExtra=view.findViewById(R.id.layout_header_extra);
        mRightBtn=view.findViewById(R.id.right_btn);
        btnExBar= (Button) view.findViewById(R.id.btn_ex);
        tvExInfo= (TextView) view.findViewById(R.id.tv_info);
        ivExCheck= (ImageView) view.findViewById(R.id.iv_check);
        mRightBtn.setOnClickListener(this);
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
                    i.putExtra(ViewOrderCaseActivity.KEY_EDITABLE,groupPo.bizStatus==1);
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
                Intent i=new Intent(mThis,ViewOrderReportActivity.class);
                i.putExtra(AppConstants.INTENT_ORDER_ID,param.orderId);
                startActivity(i);
            }else if(groupPo.bizStatus==1){
                Intent i=new Intent(mThis,ViewOrderSummaryActivity.class);
                i.putExtra(AppConstants.INTENT_ORDER_ID,param.orderId);
                i.putExtra(AppConstants.INTENT_DISEASE_TOP_ID,param.topDiseaseId);
                i.putExtra(AppConstants.INTENT_IS_LEADER,isLeader(param));
                i.putExtra(AppConstants.INTENT_MY_ORDER_STATUS,myStatus);
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
    public static void openUI(Context context, ChatGroupPo po) {
        Intent intent = new Intent(context, OrderChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_EXTRA_GROUP_NAME, po.name);
        intent.putExtra(INTENT_EXTRA_GROUP_ID, po.groupId);
        intent.putExtra(INTENT_EXTRA_GROUP_PARAM, po);
        intent.putExtra(INTENT_EXTRA_IS_OBSERVE, true);
        context.startActivity(intent);
    }

    private void showConfirmPop(OrderDetailVO vo) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.order_confirm_window, null);
        ViewUtils.initOrderInfo(mThis,contentView,vo);
        contentView.findViewById(R.id.btn_cancel).setOnClickListener(orderConfirmClickListener);
        contentView.findViewById(R.id.btn_confirm).setOnClickListener(orderConfirmClickListener);
        mCoverPop = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT, true);
        mCoverPop.setFocusable(false);
        mCoverPop.setBackgroundDrawable(new BitmapDrawable());
        UIHelper.showPopAsDropdown(mHeader, mCoverPop, 0, 0);
        mRightBtn.setVisibility(View.GONE);
    }
    private OnClickListener orderConfirmClickListener =new OnClickListener() {
        @Override
        public void onClick(View v) {
            OrderChatParam param=JSON.parseObject(groupPo.param,OrderChatParam.class);
            showConfirmDialog(v.getId()==R.id.btn_confirm,param);
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
                }else{
                    mRightBtn.setVisibility(View.VISIBLE);
                }
            }
        };
        ImCommonRequest req=new ImCommonRequest(url,map, RequestHelper.makeSucListener(true,listener),RequestHelper.makeErrorListener(listener) );
        VolleyUtil.getQueue(mThis).add(req);
    }

    private void showConfirmDialog(final boolean accept,final OrderChatParam param){
        CustomClickEvent event=new CustomClickEvent() {
            @Override
            public void onClick(CustomDialog customDialog) {
                confirmOrder(accept,param.orderId);
                customDialog.dismiss();
            }

            @Override
            public void onDismiss(CustomDialog customDialog) {
                customDialog.dismiss();
            }
        };
        String text;
        if(accept){
            String timeStr= new SimpleDateFormat("MM月dd日 hh:mm").format(new Date(param.endTime));
            text="加入会诊后您需要及时查看患者病情并填写小结,请尽量在 "+timeStr+"会诊结束前提交";
        }else{
            text="确认暂无时间参与本次会诊吗?";
        }
        CustomDialog dialog=new CustomDialog.Builder(mThis,event)
                .setMessage(text).setPositive("确定").setNegative("取消").create();
        dialog.show();
    }

    private void fetchStatus(String orderId){
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                OrderStatusResult res= JSON.parseObject(dataStr,OrderStatusResult.class);
                myStatus=res.status;
                onBusinessData();
                if(res.status==0&&res.orderDetail!=null){
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
            mHeaderExtra.setVisibility(View.GONE);
            return;
        }
        OrderChatParam param=JSON.parseObject(groupPo.param,OrderChatParam.class);
        mHeaderExtra.setVisibility(View.VISIBLE);
        if(groupPo.bizStatus==2){
            tvExInfo.setText("本次会诊已完成");
            ivExCheck.setVisibility(View.VISIBLE);
            btnExBar.setText("查看报告");
        }else{
            ivExCheck.setVisibility(View.GONE);
            long endTs=param.endTime;
            String text="结束时间:"+ TimeUtils.a_format.format(new Date(endTs));
            tvExInfo.setText(text);
            if(myStatus==-1){
                btnExBar.setText("查看会诊意见");
            }else if(myStatus==2){
                btnExBar.setText(isLeader(param)?"撰写会诊报告":"查看会诊意见");
            }else{
                btnExBar.setText("撰写本人会诊意见");
            }
        }
    }
    private boolean isLeader( OrderChatParam param){
        return ImUtils.getLoginUserId().equals(param.leader);
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
                ImgTextMsgV2 mpt = getImgTextMsgV2(chatMessage);
                if (mpt == null) return;
                String bizType=mpt.bizParam.get("bizType");
                if("101".equals(bizType)){
                    Intent i = new Intent(mThis, ViewOrderCaseActivity.class);
                    i.putExtra(ViewOrderCaseActivity.KEY_EDITABLE, groupPo.bizStatus == 1);
                    i.putExtra(AppConstants.INTENT_ORDER_ID, mpt.bizParam.get("bizId"));
                    startActivity(i);
                }
            }
        };
    }

    public void onEventMainThread(SubmitSummaryEvent event) {
        if(TextUtils.isEmpty(groupPo.param))
            return;
        OrderChatParam param=JSON.parseObject(groupPo.param,OrderChatParam.class);
        if(!TextUtils.isEmpty(param.orderId) || !param.orderId.equals(event.orderId))
            return;
        fetchStatus(param.orderId);
    }
}
