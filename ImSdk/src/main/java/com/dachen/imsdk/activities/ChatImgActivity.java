package com.dachen.imsdk.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dachen.common.utils.StringUtils;
import com.dachen.imsdk.R;
import com.dachen.imsdk.consts.MessageType;
import com.dachen.imsdk.db.dao.ChatMessageDao;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.ChatMessageV2;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;


/**
 * Created by Mcp on 2016/2/18.
 */
public class ChatImgActivity extends ImBaseActivity implements OnClickListener{
    public static final String INTENT_CHAT_GROUP_ID="chatGroupId";
    public static final String INTENT_TARGET_MSG="targetMsg";
    private ViewPager mPager;
    private String groupId;
    private ChatMessagePo targetMsg;
    private List<ChatMessagePo> msgList;
    private ChatMessageDao messageDao;
    private ImgAdapter mAdapter;
    private View vHeader;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_img);
        messageDao=new ChatMessageDao();
        groupId=getIntent().getStringExtra(INTENT_CHAT_GROUP_ID);
        targetMsg= (ChatMessagePo) getIntent().getSerializableExtra(INTENT_TARGET_MSG);
        mPager= (ViewPager) findViewById(R.id.view_pager);
        vHeader =findViewById(R.id.header);
        vHeader.setOnClickListener(this);
        tvTitle= (TextView) findViewById(R.id.title);
        findViewById(R.id.back_btn).setOnClickListener(this);
        if(TextUtils.isEmpty(groupId)){
            msgList=new ArrayList<>();
            msgList.add(targetMsg);
        }else{
            msgList=messageDao.queryForType(groupId, MessageType.image,true);
        }
        mAdapter=new ImgAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        for(int i=msgList.size()-1;i>=0;i--){
            ChatMessagePo po=msgList.get(i);
            if( (po.clientMsgId!=null&&StringUtils.strEquals(po.clientMsgId, targetMsg.clientMsgId) )|| StringUtils.strEquals(po.msgId, targetMsg.msgId) ){
                mPager.setCurrentItem(i);
                changeTitle(i);
                break;
            }
        }
        mPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeTitle(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void toggleHeader(){
        if(vHeader.isShown()){
            vHeader.setVisibility(View.INVISIBLE);
        }else{
            vHeader.setVisibility(View.VISIBLE);
        }
    }
    private void changeTitle(int index){
        String str=(index+1)+"/"+msgList.size();
        tvTitle.setText(str);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.back_btn){
            finish();
        }
    }

    private class ImgAdapter extends FragmentPagerAdapter {

        public ImgAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ChatImgFragment.newInstance(msgList.get(position));
        }

        @Override
        public int getCount() {
            return msgList.size();
        }
    }
    public static class ChatImgFragment extends Fragment {
        private ChatMessagePo po;
        private ChatImgActivity mParent;

        public static ChatImgFragment newInstance(ChatMessagePo po){
            ChatImgFragment frag=new ChatImgFragment();
            Bundle args=new Bundle();
            args.putSerializable("msg",po);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            po= (ChatMessagePo) getArguments().getSerializable("msg");
            mParent= (ChatImgActivity) getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            PhotoView v=new PhotoView(getActivity());
            v.setBackgroundColor(Color.BLACK);
            v.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    mParent.toggleHeader();
                }
            });
            ChatMessageV2.ImageMsgParam param= JSON.parseObject(po.param, ChatMessageV2.ImageMsgParam.class);
//            ImageLoader.getInstance().displayImage(param.uri, v);
            ImageLoader.getInstance().displayImage(param.uri, v, new DisplayImageOptions.Builder().build());
            return v;
        }
    }
}
