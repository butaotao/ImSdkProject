package com.dachen.mdt.activity.order;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.TimeUtils;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.ExpertInfo;
import com.dachen.mdt.entity.ExpertResult;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderExpertActivity extends BaseActivity {

    private String mOrderId;

    @BindView(R.id.list_view)
    public ListView mListView;

    private ExpertAdapter mAdapter;
    private View mListHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_expert);
        ButterKnife.bind(this);
        mOrderId=getIntent().getStringExtra(AppConstants.INTENT_ORDER_ID);
        initView();
        fetchData();
    }

    private void initView(){
        mListHeader=getLayoutInflater().inflate(R.layout.experts_list_header,null);
        mAdapter=new ExpertAdapter(mThis,null);
        mListView.addHeaderView(mListHeader);
        mListView.setAdapter(mAdapter);
        mListHeader.setVisibility(View.GONE);
    }

    private void fetchData(){
        Map<String,Object> map=new HashMap<>();
        map.put("orderId",mOrderId);
        String url= UrlConstants.getUrl(UrlConstants.GET_EXPERT_LIST);
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                ExpertResult res= JSON.parseObject(dataStr,ExpertResult.class);
                mAdapter.update(res.doctorList);
                ViewHolder holder=new ViewHolder(mThis,mListHeader);
                mListHeader.setVisibility(View.VISIBLE);
                handleItem(holder,res.sponsor);
                handlePatientInfo(holder,res);
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
            }
        };
        ImCommonRequest req=new ImCommonRequest(url,map, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener) );
        VolleyUtil.getQueue(mThis).add(req);
    }

    private class ExpertAdapter extends CommonAdapterV2<ExpertInfo>{

        public ExpertAdapter(Context mContext, List<ExpertInfo> mData) {
            super(mContext, mData);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(mContext,convertView,parent,R.layout.experts_list_item,position);
            ExpertInfo item=getItem(position);
            handleItem(holder,item);
            return holder.getConvertView();
        }
    }

    private void handleItem(ViewHolder holder,ExpertInfo item){
        holder.setText(R.id.tv_name,item.name);
        String roleText=null;
        if(item.role==ExpertInfo.ROLE_MANAGER){
            roleText="主管";
        }else if(item.role==ExpertInfo.ROLE_TEAM_LEADER){
            roleText="组长";
        }
        String stateText=null;
        if(item.status==ExpertInfo.STATUS_NO_CONFIRM){
            stateText="未确认";
        }else if(item.status==ExpertInfo.STATUS_NO_REPORT){
            stateText="未填写小结";
        }
        holder.setText(R.id.tv_name_info,roleText);
        holder.setText(R.id.tv_state,stateText);
        String nameInfo=item.title+" | "+item.department;
        ImageLoader.getInstance().displayImage(item.avatar, (ImageView) holder.getView(R.id.iv_pic));
        holder.setText(R.id.tv_info,nameInfo);
    }

    private void handlePatientInfo(ViewHolder holder,ExpertResult item){
        holder.setText(R.id.tv_end_time, TimeUtils.a_format.format(new Date(item.expectEndTime)) );
        String sexStr=item.patient.sex==1?"男":"女";
        String infoStr=String.format("%s %s %d岁",item.patient.name,sexStr,item.patient.age);
        holder.setText(R.id.tv_patient,infoStr);
    }
}
