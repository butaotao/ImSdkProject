package com.dachen.mdt.activity.order;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.entity.MdtOptionResult.MdtOptionItem;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseMdtInfoActivity extends BaseMdtOptionActivity {

    public static HashMap<String, HashMap<String, ArrayList<MdtOptionItem>>> INFO_CACHE = new HashMap<>();
    public static final String KEY_TYPE = "type";

    private String mDiseaseTypeId;
    private String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if("target".equals(mType) ){
            isMulti=false;
        }
    }

    @Override
    protected void initData() {
        mDiseaseTypeId = getIntent().getStringExtra(AppConstants.INTENT_DISEASE_TOP_ID);
        mType = getIntent().getStringExtra(KEY_TYPE);
    }

    @Override
    protected boolean needFetchInfo() {
        if(INFO_CACHE.get(mDiseaseTypeId) != null && INFO_CACHE.get(mDiseaseTypeId).get(mType) != null){
            mAdapter.update(INFO_CACHE.get(mDiseaseTypeId).get(mType));
            return false;
        }
        return super.needFetchInfo();
    }

    @Override
    protected Intent makeResultIntent() {
        return super.makeResultIntent()
                .putExtra(KEY_TYPE,mType);
    }

    @Override
    protected void fetchInfo() {
        RequestHelperListener listener = new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                HashMap<String, ArrayList<MdtOptionItem>> res = JSON.parseObject(dataStr, new TypeReference<HashMap<String, ArrayList<MdtOptionItem>>>() {
                });
                INFO_CACHE.put(mDiseaseTypeId, res);
                ArrayList<MdtOptionItem> list = res.get(mType);
                mAdapter.update(list);
            }

            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis, msg);
                getProgressDialog().dismiss();
            }
        };
        String url = UrlConstants.getUrl(UrlConstants.GET_ALL_DATABASE_LIST);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("diseaseTypeId", mDiseaseTypeId);
        ImCommonRequest request = new ImCommonRequest(url, reqMap, RequestHelper.makeSucListener(false, listener), RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
    }

//    @Override
//    protected ChooseAdapter makeAdapter() {
//        return new ChooseMdtInfoAdapter(this);
//    }
//
//    private class ChooseMdtInfoAdapter extends ChooseAdapter{
//
//        public ChooseMdtInfoAdapter(Context mContext) {
//            super(mContext);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = ViewHolder.get(mContext, convertView, parent, R.layout.choose_text_item, position);
//            MdtOptionItem info = mData.get(position);
//            String text = info.name;
//            int arrowVis = View.GONE;
//            holder.setVisibility(R.id.iv_arrow, arrowVis);
//            holder.setText(R.id.text_view, text);
//            int vis = View.INVISIBLE;
//            if (chosenIdList.contains(info.id)) {
//                vis = View.VISIBLE;
//            }
//            holder.setVisibility(R.id.iv_check, vis);
//            return holder.getConvertView();
//        }
//
//    }
}
