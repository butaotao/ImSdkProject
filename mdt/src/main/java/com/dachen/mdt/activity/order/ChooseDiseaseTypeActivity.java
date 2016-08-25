package com.dachen.mdt.activity.order;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.entity.MdtOptionResult.MdtOptionItem;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseDiseaseTypeActivity extends BaseMdtOptionActivity {
//    public static final String KEY_RESULT = "result";
//    public static final String KEY_START = "current";
//    public static final String KEY_PARENT = "parent";
//    public static final int REQ_CODE_NEXT = 1;

//    private DiseaseType mParent;
//    private ChooseTypeAdapter mAdapter;
//    private DiseaseType mResult;
//    private DiseaseType mCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_choose_disease_type);
        tvTitle.setText("选择病种");
//        mParent = (DiseaseType) getIntent().getSerializableExtra(KEY_PARENT);
//        mCurrent= (DiseaseType) getIntent().getSerializableExtra(KEY_START);
//        mAdapter=new ChooseTypeAdapter(mThis,null);
//        mListView.setAdapter(mAdapter);
//        if (mParent == null) {
//            fetchInfo();
//        } else {
//            List<DiseaseType> list = new ArrayList<>();
//            list.add(mParent);
//            list.addAll(mParent.children);
//            mAdapter.update(list);
//        }
    }

    @Override
    protected void initData() {
        isMulti=false;
    }

    protected void fetchInfo() {
        RequestHelperListener listener = new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                List<MdtOptionItem> list = JSON.parseArray(dataStr, MdtOptionItem.class);
                mAdapter.update(list);
            }

            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis, msg);
                getProgressDialog().dismiss();
            }
        };
        String url = UrlConstants.getUrl(UrlConstants.GET_DISEASE_TYPES);
        Map<String, Object> reqMap = new HashMap<>();
        ImCommonRequest request = new ImCommonRequest(url, reqMap, RequestHelper.makeSucListener(false, listener), RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    @Override
    protected Class getChildClass() {
        return ChooseDiseaseTypeActivity.class;
    }

    //    @OnItemClick(R.id.list_view)
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//        DiseaseType type=mAdapter.getItem(position);
//        if(type.children==null){
//            clickResult(type);
//        }else if(mParent!=null&&type.id.equals(mParent.id)){
//            clickResult(type);
//        }else{
//            Intent i=new Intent(this,ChooseDiseaseTypeActivity.class).putExtra(KEY_PARENT,type)
//                    .putExtra(KEY_START,mCurrent);
//            startActivityForResult(i,REQ_CODE_NEXT);
//        }
//    }
//    private void clickResult(DiseaseType type){
//        mResult=type;
//        mCurrent=mResult;
//        mAdapter.notifyDataSetChanged();
//    }

//    @Override
//    public void clickRight(){
//        if(mResult !=null){
//            setResult(RESULT_OK,new Intent().putExtra(KEY_RESULT, mResult));
//            finish();
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode==REQ_CODE_NEXT){
//            if(resultCode!=RESULT_OK)return;
//            setResult(RESULT_OK,data );
//            finish();
//        }
//    }

//    private class ChooseTypeAdapter extends CommonAdapterV2<DiseaseType> {
//
//        public ChooseTypeAdapter(Context mContext, List<DiseaseType> mData) {
//            super(mContext, mData);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = ViewHolder.get(mContext, convertView, parent, R.layout.choose_text_item, position);
//            DiseaseType info = mData.get(position);
//            String text=info.name;
//            int arrowVis=View.GONE;
//            if(info.children!=null)
//                arrowVis=View.VISIBLE;
//            if(mParent!=null&& mParent.id.equals(info.id) ){
//                text="未确诊";
//                arrowVis= View.GONE;
//            }
//            holder.setVisibility(R.id.iv_arrow,arrowVis);
//            holder.setText(R.id.text_view, text);
//            int vis = View.INVISIBLE;
//            if (mCurrent!=null && TextUtils.equals(mCurrent.id, info.id)) {
//                vis = View.VISIBLE;
//            }
//            holder.setVisibility(R.id.iv_check, vis);
//            return holder.getConvertView();
//        }
//    }

}
