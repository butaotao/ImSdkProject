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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class ChooseMdtActivity extends BaseActivity {

    public static final String KEY_RESULT_MDT="resultMdt";
    public static final String KEY_PARENT="keyParent";
    public static final int REQ_CODE_NEXT = 1;

    @BindView(R.id.list_view)
    public ListView mListView;
    private String mdtGroupId;
    private ChooseMdtAdapter mAdapter;
    private MdtGroupInfo mResultMdt;
    private MdtGroupInfo mParent;
    private String diseaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mdt);
        ButterKnife.bind(this);
        mdtGroupId =getIntent().getStringExtra(AppConstants.INTENT_MDT_GROUP_ID);
        diseaseId=getIntent().getStringExtra(AppConstants.INTENT_DISEASE_TOP_ID);
        mParent= (MdtGroupInfo) getIntent().getSerializableExtra(KEY_PARENT);
        mAdapter=new ChooseMdtAdapter(this,null);
        mListView.setAdapter(mAdapter);
        if(mParent==null){
            fetchInfo();
        }else{
            List<MdtGroupInfo> list = new ArrayList<>();
            list.add(mParent);
            list.addAll(mParent.children);
            mAdapter.update(list);
        }
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        MdtGroupInfo info=mAdapter.getItem(position);
        if(info.children==null){
            clickResult(info);
        }else if(mParent!=null&& mParent.id.equals(info.id)){
            clickResult(info);
        }else{
            Intent i=new Intent(this,ChooseMdtActivity.class).putExtra(KEY_PARENT,info)
                    .putExtra(AppConstants.INTENT_MDT_GROUP_ID,mdtGroupId);
            startActivityForResult(i,REQ_CODE_NEXT);
        }
    }
    private void clickResult( MdtGroupInfo info){
        mResultMdt=info;
        mAdapter.notifyDataSetChanged();
        mdtGroupId=info.id;
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
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("diseaseTypeId",diseaseId);
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQ_CODE_NEXT){
            if(resultCode!=RESULT_OK)return;
            setResult(RESULT_OK,data );
            finish();
        }
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
            String text=info.name;
            int arrowVis=View.GONE;
            if(info.children!=null)
                arrowVis=View.VISIBLE;
            if(mParent!=null&& mParent.id.equals(info.id) ){
                text="全部";
                arrowVis= View.GONE;
            }
            holder.setVisibility(R.id.iv_arrow,arrowVis);
            holder.setText(R.id.text_view, text);
            int vis=View.INVISIBLE;
            if(TextUtils.equals(mdtGroupId,info.id)){
                vis=View.VISIBLE;
            }
            holder.setVisibility(R.id.iv_check,vis);
            return holder.getConvertView();
        }

    }
}
