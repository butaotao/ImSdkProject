package com.dachen.imsdk.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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
import com.dachen.common.toolbox.SingleMediaScanner;
import com.dachen.common.utils.Md5Util;
import com.dachen.common.utils.StringUtils;
import com.dachen.common.utils.ToastUtil;
import com.dachen.imsdk.R;
import com.dachen.imsdk.consts.MessageType;
import com.dachen.imsdk.db.dao.ChatMessageDao;
import com.dachen.imsdk.db.po.ChatMessagePo;
import com.dachen.imsdk.entity.ChatMessageV2;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
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
        findViewById(R.id.right_btn).setOnClickListener(this);
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
        }else if(v.getId()==R.id.right_btn){
            ChatMessagePo msg=msgList.get(mPager.getCurrentItem());
            ChatMessageV2.ImageMsgParam param= JSON.parseObject(msg.param, ChatMessageV2.ImageMsgParam.class);
            saveImg(param.uri);
        }
    }
    private void saveImg(String url){
        final File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(!dir.exists()&&!dir.mkdirs()){
            ToastUtil.showToast(mThis,"存储失败.请确认外部存储状态是否正常");
            return;
        }
        final String fileName= Md5Util.toMD5(url)+".png";
        final File picFile=new File(dir,fileName);
//        Ion.with(mThis).load(url).asBitmap().setCallback(new FutureCallback<Bitmap>() {
//            @Override
//            public void onCompleted(Exception err, Bitmap result) {
//                if(err!=null){
//                    ToastUtil.showToast(mThis,"图片加载失败");
//                    return;
//                }
//                FileOutputStream out = null;
//                try {
//                    out = new FileOutputStream(picFile);
//                    result.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//                    ToastUtil.showToast(mThis,"图片已保存");
//                    new SingleMediaScanner(mThis,picFile);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    ToastUtil.showToast(mThis,"图片保存失败");
//                } finally {
//                    try {
//                        if (out != null) {
//                            out.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        Ion.with(mThis).load(url).write(picFile).setCallback(new FutureCallback<File>() {
            @Override
            public void onCompleted(Exception e, File result) {
                String toastStr=e==null?"图片已保存":"图片保存失败";
                ToastUtil.showToast(mThis,toastStr);
                new SingleMediaScanner(mThis,picFile);
            }
        });
//        ImageLoader.getInstance().loadImage(url,null,new DisplayImageOptions.Builder().build(), new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                ToastUtil.showToast(mThis,"图片加载失败");
//            }
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                File picFile=new File(dir,fileName);
//                FileOutputStream out = null;
//                try {
//                    out = new FileOutputStream(picFile);
//                    loadedImage.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//                    ToastUtil.showToast(mThis,"图片已保存");
//                    new SingleMediaScanner(mThis,picFile);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    ToastUtil.showToast(mThis,"图片保存失败");
//                } finally {
//                    try {
//                        if (out != null) {
//                            out.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//            }
//        });

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
//            ImageLoader.getInstance().displayImage(param.uri, v, new DisplayImageOptions.Builder().build());
            Ion.with(v).smartSize(false).load(param.uri);
            return v;
        }
    }
}
