package com.dachen.mdt.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.adapter.CommonAdapterV2;
import com.dachen.common.adapter.ViewHolder;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.entity.MdtGroupInfo;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class ChooseMdtActivity extends BaseActivity {

    public static final String KEY_RESULT_MDT="resultMdt";

    @BindView(R.id.list_view)
    public ListView mListView;
    private String mdtGroupId;
    private ChooseMdtAdapter mAdapter;
    private MdtGroupInfo mResultMdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mdt);
        ButterKnife.bind(this);
        mdtGroupId =getIntent().getStringExtra(AppConstants.INTENT_MDT_GROUP_ID);
        mAdapter=new ChooseMdtAdapter(this,null);
        mListView.setAdapter(mAdapter);
        fetchInfo();
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        mdtGroupId=mAdapter.getItem(position).id;
        mAdapter.notifyDataSetChanged();
        mResultMdt=mAdapter.getItem(position);
    }

    @OnClick(R.id.right_btn)
    public void clickRight(){
        if(mResultMdt!=null){
            setResult(RESULT_OK,new Intent().putExtra(KEY_RESULT_MDT,mResultMdt));
            finish();
        }
    }

    private void fetchInfo(){
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                List<MdtGroupInfo> list= JSON.parseArray(dataStr,MdtGroupInfo.class);
                mAdapter.update(list);
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.GET_MDT_GROUP_LIST);
        ImCommonRequest request=new ImCommonRequest(url,null, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    private class ChooseMdtAdapter extends CommonAdapterV2<MdtGroupInfo>{

        public ChooseMdtAdapter(Context mContext, List<MdtGroupInfo> mData) {
            super(mContext, mData);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(mContext,convertView,parent,R.layout.choose_text_item,position);
            MdtGroupInfo info=mData.get(position);
            holder.setText(R.id.text_view,info.name);
            int vis=View.INVISIBLE;
            if(TextUtils.equals(mdtGroupId,info.id)){
                vis=View.VISIBLE;
            }
            holder.setVisibility(R.id.iv_check,vis);
            return holder.getConvertView();
        }

    }
}
