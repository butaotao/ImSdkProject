package com.dachen.mdt.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dachen.mdt.AppConstants;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.adapter.ChooseTextAdapter;

import java.util.ArrayList;

public class ChooseTextActivity extends BaseActivity implements OnClickListener{

    public final static String KEY_LIST="list";
    public final static String KEY_SELECTED="selected";

    private ChooseTextAdapter mAdapter;
    protected ListView mListView;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_text);
        ArrayList<String> list=getIntent().getStringArrayListExtra(KEY_LIST);
        mAdapter=new ChooseTextAdapter(this, list);
        mAdapter.setSelectText(getIntent().getStringExtra(KEY_SELECTED));
        mListView= (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
        findViewById(R.id.right_btn).setOnClickListener(this);

        mTitle= (TextView) findViewById(R.id.title);
        String title = getIntent().getStringExtra(AppConstants.INTENT_TITLE);
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.right_btn){
            setResult(RESULT_OK,new Intent().putExtra(AppConstants.INTENT_RESULT,mAdapter.getSelectText()));
            finish();
        }
    }
}
