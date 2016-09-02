package com.dachen.mdt.activity.order.summary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dachen.mdt.AppConstants;
import com.dachen.mdt.activity.order.BaseMdtDragSortActivity;

public class SummaryDiseasePreviewActivity extends BaseMdtDragSortActivity {
    private String mDiseaseTypeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        mDiseaseTypeId = getIntent().getStringExtra(AppConstants.INTENT_DISEASE_TOP_ID);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK,makeResultIntent());
        finish();
    }

    @Override
    public void onRightClick(View v) {
        Intent i=new Intent(mThis,ChooseSummaryDiseaseActivity.class)
                .putExtra(AppConstants.INTENT_DISEASE_TOP_ID, mDiseaseTypeId)
                .putExtra(AppConstants.INTENT_START_DATA,currentData);
        startActivityForResult(i,REQ_CODE_CHOOSE);
    }
}
