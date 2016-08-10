package com.dachen.mdt.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
import com.dachen.mdt.exception.TextEmptyException;
import com.dachen.mdt.listener.RequestHelperListener;
import com.dachen.mdt.net.RequestHelper;
import com.dachen.mdt.util.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubmitSummaryActivity extends BaseActivity {

    private static final int REQ_CODE_MDT_INFO = 2;
    private static final int REQ_CODE_INPUT = 3;

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

    private String mdtGroupId;
    private String mOrderId;
    private boolean isLeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_summary);
        ButterKnife.bind(this);
        mdtGroupId=getIntent().getStringExtra(AppConstants.INTENT_MDT_GROUP_ID);
        mOrderId =getIntent().getStringExtra(AppConstants.INTENT_ORDER_ID);
        isLeader=getIntent().getBooleanExtra(AppConstants.INTENT_IS_LEADER,false);
        tvTitle.setText(isLeader?"填写报告":"填写小结");
    }

    @OnClick(R.id.right_btn)
    protected void commit(View v){
        Map<String,Object> reqMap=new HashMap<>();
        reqMap.put("orderId",mOrderId);
        try {
            reqMap.put("diagSuggest", ViewUtils.checkTextEmpty(tvDiagnoseOpinion));
        } catch (TextEmptyException e) {
            e.tv.requestFocus();
            e.tv.setError(Html.fromHtml("<font color='red'>不能为空!</font>"));
            return;
        }
        reqMap.put("checkSuggest", tvExamineOpinion.getText().toString());
        reqMap.put("treatSuggest", tvTreatOpinion.getText().toString());
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
        String url= UrlConstants.getUrl(isLeader?UrlConstants.SUBMIT_REPORT:UrlConstants.SAVE_SUMMARY);
        ImCommonRequest request=new ImCommonRequest(url,reqMap, RequestHelper.makeSucListener(true,listener),RequestHelper.makeErrorListener(listener));
        VolleyUtil.getQueue(mThis).add(request);
    }

    @OnClick({R.id.ll_diagnose_opinion, R.id.ll_examine_opinion,R.id.ll_treat_opinion})
    protected void chooseMdtInfo(View v) {
        if (mdtGroupId == null) {
            // TODO: 2016/8/10
            mdtGroupId="mdt_zl01";
        }
        TextView tv = (TextView) ((ViewGroup) v).getChildAt(1);
        String type = EditOrderCaseActivity.getTypeMap().get(v.getId());
        if (type == null) return;
        Intent i = new Intent(mThis, ChooseMdtInfoActivity.class).putExtra(AppConstants.INTENT_MDT_GROUP_ID, mdtGroupId)
                .putExtra(ChooseMdtInfoActivity.KEY_TYPE, type)
                .putExtra(ChooseMdtInfoActivity.KEY_START_TEXT,tv.getText().toString())
                .putExtra(AppConstants.INTENT_VIEW_ID, tv.getId());
        startActivityForResult(i, REQ_CODE_MDT_INFO);
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
        if (requestCode == REQ_CODE_MDT_INFO || requestCode == REQ_CODE_INPUT) {
            if (resultCode != RESULT_OK) return;
            handleTextResult(data);
        }
    }

    private void handleTextResult(Intent data) {
        String res = data.getStringExtra(AppConstants.INTENT_TEXT_RESULT);
        int viewId = data.getIntExtra(AppConstants.INTENT_VIEW_ID, 0);
        TextView tv = ButterKnife.findById(mThis, viewId);
        if (tv != null) {
            tv.setText(res);
        }
    }
}
