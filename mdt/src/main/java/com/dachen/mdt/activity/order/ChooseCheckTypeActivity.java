package com.dachen.mdt.activity.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.dachen.mdt.entity.CheckType;
import com.dachen.mdt.entity.CheckTypeResult;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.tools.CheckResultHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class ChooseCheckTypeActivity extends BaseActivity {
    public static  final int REQ_CODE_NEXT=1;
    public static  final int REQ_CODE_OTHER=2;

    public static final String KEY_START="start";
    public static final String KEY_RESULT="result";

    @BindView(R.id.list_view)
    public ListView mListView;

    private ChooseTypeAdapter mAdapter;
//    private ArrayList<CheckType> mCurrentData;
    private CheckTypeResult mCurrentData;
//    private View mFooterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_check_type);
        ButterKnife.bind(this);
        mCurrentData= (CheckTypeResult) getIntent().getSerializableExtra(KEY_START);
        mAdapter=new ChooseTypeAdapter(mThis);
        initView();
        fetchInfo();
    }

    private void initView(){
//        mFooterView=getLayoutInflater().inflate(R.layout.choose_text_footer,null);
//        TextView tvName= (TextView) mFooterView.findViewById(R.id.text_view);
//        tvName.setText("其他");
//        mFooterView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(mThis, CommonInputActivity.class).putExtra(CommonInputActivity.KEY_TEXT, mCurrentData.text);
//                startActivityForResult(i,REQ_CODE_OTHER);
//            }
//        });
//        mListView.addFooterView(mFooterView);
//        refreshOther();
        mListView.setAdapter(mAdapter);
    }

//    private void refreshOther(){
//        ViewHolder holder=ViewHolder.get(mThis,mFooterView);
//        View vCheck=holder.getView(R.id.iv_check);
//        if(TextUtils.isEmpty(mCurrentData.text)){
//            vCheck.setVisibility(View.GONE);
//        }else{
//            vCheck.setVisibility(View.VISIBLE);
//        }
//    }

    private void fetchInfo(){
        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                List<CheckType> list= JSON.parseArray(dataStr,CheckType.class);
                mAdapter.update(list);
            }
            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }
        };
        String url= UrlConstants.getUrl(UrlConstants.GET_CHECK_TYPE);
        Map<String, Object> reqMap = new HashMap<>();
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(false,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
        getProgressDialog().show();
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent i=new Intent(this,EditCheckValueActivity.class).putExtra(EditCheckValueActivity.KEY_DATA,mCurrentData.typeList)
                .putExtra(EditCheckValueActivity.KEY_TYPE,mAdapter.getItem(position));
        startActivityForResult(i,REQ_CODE_NEXT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQ_CODE_NEXT ){
            if(resultCode!=RESULT_OK)return;
            mCurrentData.typeList= (ArrayList<CheckType>) data.getSerializableExtra(EditCheckValueActivity.KEY_RESULT);
            mAdapter.notifyDataSetChanged();
        }else if(requestCode==REQ_CODE_OTHER ){
            if(resultCode!=RESULT_OK)return;
            mCurrentData.text=data.getStringExtra(AppConstants.INTENT_TEXT_RESULT);
//            refreshOther();
        }
    }

    @Override
    public void finish() {
        Intent i=new Intent().putExtra(KEY_RESULT,mCurrentData);
        setResult(RESULT_OK,i);
        super.finish();
    }

    private class ChooseTypeAdapter extends CommonAdapterV2<CheckType> {

        @Override
        public int getCount() {
            return super.getCount();
        }

        public ChooseTypeAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder= ViewHolder.get(mContext,convertView,parent,R.layout.choose_text_item,position);
            CheckType info=mData.get(position);
            String text=info.name;
            holder.setVisibility(R.id.iv_arrow, View.VISIBLE);
            holder.setText(R.id.text_view, text);
            int vis=View.INVISIBLE;
            if(CheckResultHelper.getTypeIndex(mCurrentData.typeList,info.id)>=0){
                vis= View.VISIBLE;
            }
            holder.setVisibility(R.id.iv_check,vis);
            holder.getConvertView().setBackgroundColor(Color.WHITE);
            return holder.getConvertView();
        }

    }
}
