package com.dachen.imsdk.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dachen.common.json.EmptyResult;
import com.dachen.common.utils.ToastUtil;
import com.dachen.common.utils.VolleyUtil;
import com.dachen.imsdk.R;
import com.dachen.imsdk.adapter.ChatGroupAdapter;
import com.dachen.imsdk.adapter.MsgMenuAdapter;
import com.dachen.imsdk.db.dao.ChatGroupDao;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.net.ImCommonRequest;
import com.dachen.imsdk.net.PollingURLs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mcp on 2016/3/25.
 */
public class ChatGroupActivity extends ImBaseActivity implements OnClickListener {
    public static final String TAG = "ChatGroupActivity";

    private ChatGroupAdapter mAdapter;
    private List<ChatGroupPo> mList=new ArrayList<>();
    private String msgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vchat_menber_activity);

        msgId=getIntent().getStringExtra(MsgMenuAdapter.INTENT_EXTRA_MSG_ID);
        TextView tv= (TextView) findViewById(R.id.tv_title);
        tv.setText("转发消息");
        mAdapter = new ChatGroupAdapter(this, mList);
        ListView lv = (ListView) findViewById(R.id.list_view);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(itemClickListener);
        findViewById(R.id.btn_confirm).setVisibility(View.GONE);
        findViewById(R.id.back_btn).setOnClickListener(this);
        new InitTask().execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn) {
            finish();
        }
    }

    private OnItemClickListener itemClickListener=new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ChatGroupPo group=mList.get(position);
//            Intent i=new Intent();
//            i.putExtra(ChatActivityV2.INTENT_EXTRA_GROUP_ID,group.groupId);
//            setResult(RESULT_OK,i);
//            finish();
            forwardMsg(group.groupId);
        }
    };
    private class InitTask extends AsyncTask<Void,Void,List<ChatGroupPo>>{
        @Override
        protected List<ChatGroupPo> doInBackground(Void... params) {
            ChatGroupDao dao=new ChatGroupDao();
            return dao.queryInBizTypeExcept(null,new String[]{"GROUP_0001","pub_010","pub_001","pub_002",});
        }

        @Override
        protected void onPostExecute(List<ChatGroupPo> chatGroupPos) {
            mList.addAll(chatGroupPos);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void forwardMsg(String groupId){
        Map<String,Object> reqMap=new HashMap<>();
        reqMap.put("msgId",msgId);
        reqMap.put("gid",groupId);
        reqMap.put("index",0);
        mDialog.show();
        Listener<String> listener=new Listener<String>() {
            @Override
            public void onResponse(String s) {
                EmptyResult result= JSON.parseObject(s,EmptyResult.class);
                if(result.resultCode==1){
                    ToastUtil.showToast(mThis,"转发成功");
                    finish();
                }else{
                    ToastUtil.showToast(mThis,result.detailMsg);
                }
                mDialog.dismiss();
            }
        };

        ErrorListener errorListener=new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.showErrorNet(mThis);
                mDialog.dismiss();
            }
        };
        StringRequest req=new ImCommonRequest(PollingURLs.forwardMsg(),reqMap,listener,errorListener);
        VolleyUtil.getQueue(this).add(req);
    }
}
