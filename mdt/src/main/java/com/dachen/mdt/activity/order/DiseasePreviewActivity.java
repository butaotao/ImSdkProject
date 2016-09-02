package com.dachen.mdt.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dachen.mdt.AppConstants;

public class DiseasePreviewActivity extends BaseMdtDragSortActivity {
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
        Intent i=new Intent(mThis,ChooseDiseaseTypeActivity.class)
                .putExtra(AppConstants.INTENT_DISEASE_TOP_ID, mDiseaseTypeId)
                .putExtra(AppConstants.INTENT_START_DATA,currentData);
        startActivityForResult(i,REQ_CODE_CHOOSE);
    }
}
