package com.dachen.mdt.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.adapter.ChooseTextAdapter;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseMdtInfoActivity extends BaseActivity {

    public static Map<String,ArrayList<String>> INFO_CACHE;
    public static final String KEY_TYPE="type";
    public static final String KEY_START_TEXT="startText";
    @BindView(R.id.list_view)
    public ListView mListView;

//    private String mdtGroupId;
    private String mDiseaseTypeId;
    private ChooseTextAdapter mAdapter;
    private String mType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mdt_info);
        ButterKnife.bind(this);
        mDiseaseTypeId=getIntent().getStringExtra(AppConstants.INTENT_DISEASE_TOP_ID);
        mType=getIntent().getStringExtra(KEY_TYPE);
        mAdapter=new ChooseTextAdapter(this,null);
        mAdapter.setSelectText(getIntent().getStringExtra(KEY_START_TEXT));
        mListView.setAdapter(mAdapter);
        initInfo();
    }

    private void initInfo(){
        if(INFO_CACHE!=null&&INFO_CACHE.get(mType)!=null){
            mAdapter.update(INFO_CACHE.get(mType));
            return;
        }
        fetchInfo();
    }

    @OnClick(R.id.right_btn)
    public void clickRight(){
        String res=mAdapter.getSelectText();
        if(res!=null){
            setResult(RESULT_OK,new Intent().putExtra(AppConstants.INTENT_TEXT_RESULT,res)
                    .putExtra(AppConstants.INTENT_VIEW_ID,getIntent().getIntExtra(AppConstants.INTENT_VIEW_ID,0)) );
            finish();
        }
    }

    private void fetchInfo(){
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                Map<String,ArrayList<String>>  res= JSON.parseObject(dataStr,new TypeReference<Map<String, ArrayList<String>>>(){});
                INFO_CACHE=res;
                mAdapter.update(INFO_CACHE.get(mType));
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.GET_ALL_DATABASE_LIST);
        Map<String,Object> reqMap=new HashMap<>();
        reqMap.put("diseaseTypeId",mDiseaseTypeId);
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
    }
}
