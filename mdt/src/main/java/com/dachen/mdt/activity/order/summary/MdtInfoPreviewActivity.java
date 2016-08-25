package com.dachen.mdt.activity.order.summary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dachen.mdt.AppConstants;
import com.dachen.mdt.activity.order.BaseMdtDragSortActivity;
import com.dachen.mdt.activity.order.ChooseMdtInfoActivity;

public class MdtInfoPreviewActivity extends BaseMdtDragSortActivity {
    private String mDiseaseTypeId;
    private String mType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText("添加数据");
    }

    @Override
    protected void initData() {
        mDiseaseTypeId = getIntent().getStringExtra(AppConstants.INTENT_DISEASE_TOP_ID);
        mType=getIntent().getStringExtra(ChooseMdtInfoActivity.KEY_TYPE);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK,makeResultIntent().putExtra(ChooseMdtInfoActivity.KEY_TYPE,mType));
        finish();
    }

    @Override
    public void onRightClick(View v) {
        Intent i=new Intent(mThis,ChooseMdtInfoActivity.class)
                .putExtra(AppConstants.INTENT_DISEASE_TOP_ID, mDiseaseTypeId)
                .putExtra(ChooseMdtInfoActivity.KEY_TYPE, mType)
                .putExtra(AppConstants.INTENT_START_DATA,currentData)
                .putExtra(AppConstants.INTENT_VIEW_ID, getIntent().getIntExtra(AppConstants.INTENT_VIEW_ID,0));;
        startActivityForResult(i,REQ_CODE_CHOOSE);
    }
}
