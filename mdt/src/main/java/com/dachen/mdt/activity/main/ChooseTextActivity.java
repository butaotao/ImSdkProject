package com.dachen.mdt.activity.main;

import android.os.Bundle;

import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.adapter.ChooseTextAdapter;

import java.util.ArrayList;

public class ChooseTextActivity extends BaseActivity {

    public final static String KEY_LIST="list";
    public final static String KEY_SELECTED="selected";

    private ChooseTextAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_text);
        ArrayList<String> list=getIntent().getStringArrayListExtra(KEY_LIST);
        mAdapter=new ChooseTextAdapter(this, list);
        mAdapter.setSelectText(getIntent().getStringExtra(KEY_SELECTED));
    }

}
