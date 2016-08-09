package com.dachen.mdt.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommonInputActivity extends BaseActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";

    @BindView(R.id.et)
    public EditText et;
    @BindView(R.id.title)
    public TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_input);
        ButterKnife.bind(this);
        et.setText(getIntent().getStringExtra(KEY_TEXT));
    }

    @OnClick(R.id.right_btn)
    public void clickConfirm() {
        Intent i = new Intent();
        i.putExtra(AppConstants.INTENT_TEXT_RESULT, et.getText().toString())
                .putExtra(AppConstants.INTENT_VIEW_ID, getIntent().getIntExtra(AppConstants.INTENT_VIEW_ID, 0));
        setResult(RESULT_OK, i);
        finish();
    }
}
