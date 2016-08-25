package com.dachen.mdt.activity.order.summary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.UrlConstants;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.activity.main.CommonInputActivity;
import com.dachen.mdt.activity.order.ChooseMdtInfoActivity;
import com.dachen.mdt.activity.order.EditOrderCaseActivity;
import com.dachen.mdt.entity.DiseaseTag;
import com.dachen.mdt.entity.MdtOptionResult;
import com.dachen.mdt.entity.MdtOptionResult.MdtOptionItem;
import com.dachen.mdt.exception.TextEmptyException;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.util.OrderUtils;
import com.dachen.mdt.util.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubmitSummaryActivity extends BaseActivity {

    private static final int REQ_CODE_MDT_INFO = 2;
    private static final int REQ_CODE_INPUT = 3;
    private static final int REQ_CODE_DISEASE = 4;
    private static final int REQ_CODE_DISEASE_TAG = 5;

    public static final String KEY_IS_REPORT="isReport";

    @BindView(R.id.tv_diagnose_opinion)
    TextView tvDiagnoseOpinion;
    @BindView(R.id.tv_examine_opinion)
    TextView tvExamineOpinion;
    @BindView(R.id.tv_treat_opinion)
    TextView tvTreatOpinion;
    @BindView(R.id.tv_other)
    TextView tvOther;
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.tv_diagnose_tag)
    TextView tvDiagnoseTag;
    @BindView(R.id.ll_diagnose_tag)
    View layoutDiagnoseTag;

//    private String mdtGroupId;
    private String mDisTopId;
    private String mOrderId;
    private boolean isReport;
    private MdtOptionResult mTypeResult;
    private DiseaseTag mDisTag;
    private Map<String,MdtOptionResult> resultMap=new HashMap<>();
//    private String mDisTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_summary);
        ButterKnife.bind(this);
//        mdtGroupId=getIntent().getStringExtra(AppConstants.INTENT_MDT_GROUP_ID);
        mDisTopId=getIntent().getStringExtra(AppConstants.INTENT_DISEASE_TOP_ID);
        mOrderId =getIntent().getStringExtra(AppConstants.INTENT_ORDER_ID);
        isReport =getIntent().getBooleanExtra(KEY_IS_REPORT,false);
        tvTitle.setText(isReport ?"填写报告":"填写小结");
        refreshTagForDisease();
    }

    @OnClick(R.id.right_btn)
    protected void commit(View v){
        Map<String,Object> reqMap=new HashMap<>();
        reqMap.put("orderId",mOrderId);
        try {
            ViewUtils.checkTextEmpty(tvDiagnoseOpinion);
        } catch (TextEmptyException e) {
            ViewUtils.setError(e.tv,"不能为空!");
            return;
        }
        if(mDisTag!=null)
            reqMap.put("tagId", mDisTag.id);
        MdtOptionItem type=OrderUtils.getMdtSingleOption(mTypeResult);
        if(type!=null)
            reqMap.put("diseaseTypeId", type.id);
        reqMap.put("diagSuggest",  mTypeResult);
        if(resultMap.get("checksuggest")!=null)
            reqMap.put("checkSuggest",   resultMap.get("checksuggest") );
        if(resultMap.get("treatSuggest")!=null)
            reqMap.put("treatSuggest",  resultMap.get("treatSuggest") );
        reqMap.put("other", tvOther.getText().toString());

        RequestHelperListener listener=new RequestHelperListener() {
            @Override
            public void onSuccess(String dataStr) {
                getProgressDialog().dismiss();
                ToastUtil.showToast(mThis,"提交成功");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mThis,msg);
                getProgressDialog().dismiss();
            }

        };
        getProgressDialog().show();
        String url= UrlConstants.getUrl(isReport ?UrlConstants.SUBMIT_REPORT:UrlConstants.SAVE_SUMMARY);
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(true,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
    }

    @OnClick({ R.id.ll_examine_opinion,R.id.ll_treat_opinion})
    protected void chooseMdtInfo(View v) {
        TextView tv = (TextView) ((ViewGroup) v).getChildAt(1);
        String type = EditOrderCaseActivity.getTypeMap().get(v.getId());
        if (type == null) return;
        Intent i = new Intent(mThis, MdtInfoPreviewActivity.class)
                .putExtra(AppConstants.INTENT_DISEASE_TOP_ID, mDisTopId)
                .putExtra(ChooseMdtInfoActivity.KEY_TYPE, type)
                .putExtra(AppConstants.INTENT_START_DATA,resultMap.get(type))
                .putExtra(AppConstants.INTENT_VIEW_ID, tv.getId());
        startActivityForResult(i, REQ_CODE_MDT_INFO);
    }
    @OnClick(R.id.ll_diagnose_opinion)
    protected void chooseDiagnoseOpinion(View v) {
        tvDiagnoseOpinion.clearFocus();
        tvDiagnoseOpinion.setError(null);
        Intent i = new Intent(mThis, ChooseSummaryDiseaseActivity.class)
                .putExtra(AppConstants.INTENT_START_DATA, mTypeResult)
                .putExtra(AppConstants.INTENT_DISEASE_TOP_ID, mDisTopId);
        startActivityForResult(i, REQ_CODE_DISEASE);
    }
    @OnClick(R.id.ll_diagnose_tag)
    protected void chooseDiseaseTag(View v) {
        ArrayList<String> list=new ArrayList<>();
        MdtOptionItem type= OrderUtils.getMdtSingleOption(mTypeResult);
        for(DiseaseTag tag: type.tagList){
            list.add(tag.name);
        }
        Intent i = new Intent(mThis, ChooseDiseaseTagActivity.class)
                .putExtra(AppConstants.INTENT_START_DATA, mDisTag)
                .putExtra(ChooseDiseaseTagActivity.KEY_TYPE, mTypeResult);
        startActivityForResult(i, REQ_CODE_DISEASE_TAG);
    }

    @OnClick({R.id.ll_other})
    protected void goInput(View v) {
        TextView tv = (TextView) ((ViewGroup) v).getChildAt(1);
        Intent i = new Intent(mThis, CommonInputActivity.class)
                .putExtra(CommonInputActivity.KEY_TEXT, tv.getText().toString())
                .putExtra(AppConstants.INTENT_VIEW_ID, tv.getId());
        startActivityForResult(i, REQ_CODE_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQ_CODE_MDT_INFO ) {
            handleMdtResult(data);
        }else if(requestCode==REQ_CODE_INPUT){
            handleTextResult(data);
        }else if(requestCode==REQ_CODE_DISEASE){
            mTypeResult = (MdtOptionResult) data.getSerializableExtra(AppConstants.INTENT_RESULT);
            tvDiagnoseOpinion.setText(mTypeResult.showText);
            refreshTagForDisease();
        }else if(requestCode==REQ_CODE_DISEASE_TAG){
            mDisTag= (DiseaseTag) data.getSerializableExtra(AppConstants.INTENT_RESULT);
            tvDiagnoseTag.setText(mDisTag.name);
        }
    }
    private void refreshTagForDisease(){
//        DiseaseTag tag=mDisTag;
//        String tag=mDisTag;
        mDisTag=null;
        tvDiagnoseTag.setText(null);
        MdtOptionItem type=OrderUtils.getMdtSingleOption(mTypeResult);
        if(!isReport || type ==null|| type.tagList==null|| type.tagList.size()==0){
            layoutDiagnoseTag.setVisibility(View.GONE);
            return;
        }
        layoutDiagnoseTag.setVisibility(View.VISIBLE);

    }

    private void handleTextResult(Intent data) {
        String res = data.getStringExtra(AppConstants.INTENT_TEXT_RESULT);
        int viewId = data.getIntExtra(AppConstants.INTENT_VIEW_ID, 0);
        TextView tv = ButterKnife.findById(mThis, viewId);
        if (tv != null) {
            tv.setText(res);
        }
    }
    private void handleMdtResult(Intent data) {
        MdtOptionResult res = (MdtOptionResult) data.getSerializableExtra(AppConstants.INTENT_RESULT);
        String type=data.getStringExtra(ChooseMdtInfoActivity.KEY_TYPE);
        resultMap.put(type,res);
        int viewId = data.getIntExtra(AppConstants.INTENT_VIEW_ID, 0);
        TextView tv = ButterKnife.findById(mThis, viewId);
        if (tv != null) {
            tv.setText(res.showText);
        }
    }
}
