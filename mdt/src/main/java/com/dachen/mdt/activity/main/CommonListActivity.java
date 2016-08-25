package com.dachen.mdt.activity.main;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;

/**
 * Created by Mcp on 2016/8/24.
 */
public class CommonListActivity extends BaseActivity {

    protected ListView mListView;
    protected TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list);
        tvTitle = (TextView) findViewById(R.id.title);
        mListView = (ListView) findViewById(R.id.list_view);
    }

}
