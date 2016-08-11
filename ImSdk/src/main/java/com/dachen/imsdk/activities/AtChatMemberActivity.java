package com.dachen.imsdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.imsdk.R;
import com.dachen.imsdk.adapter.AtChatMemberAdapter;
import com.dachen.imsdk.db.dao.ChatGroupDao;
import com.dachen.imsdk.db.po.ChatGroupPo;
import com.dachen.imsdk.entity.GroupInfo2Bean.Data.UserInfo;
import com.dachen.imsdk.utils.ImUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mcp on 2016/6/3.
 */
public class AtChatMemberActivity extends ImBaseActivity implements OnClickListener,OnItemClickListener{

    public static final String ID_ALL="all";
    public static final String INTENT_USER_INFO="userInfo";
    private String mGroupId;
    private List<UserInfo> memberList=new ArrayList<>();
    private AtChatMemberAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_member_activity);
        TextView tvTitle= (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("选择用户");
        mGroupId = getIntent().getStringExtra(ChatActivityV2.INTENT_EXTRA_GROUP_ID);
        ListView lv = (ListView) findViewById(R.id.list_view);
        initMember();
        mAdapter=new AtChatMemberAdapter(this, memberList);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
        findViewById(R.id.btn_confirm).setVisibility(View.INVISIBLE);
        findViewById(R.id.back_btn).setOnClickListener(this);
    }

    private void initMember(){
        memberList.add(new UserInfo(AtChatMemberActivity.ID_ALL,"所有人", "drawable://" + R.drawable.avatar_normal2));
        ChatGroupPo groupPo=new ChatGroupDao().queryForId(mGroupId);
        List<UserInfo> uList= JSON.parseArray(groupPo.groupUsers,UserInfo.class);
        if(uList==null)return;
        for(UserInfo u:uList){
            if(ImUtils.getLoginUserId().equals(u.id) )continue;
            memberList.add(u);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_confirm) {
        } else if (v.getId() == R.id.back_btn) {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i=new Intent().putExtra(INTENT_USER_INFO,memberList.get(position));
        setResult(RESULT_OK,i);
        finish();
    }
}
