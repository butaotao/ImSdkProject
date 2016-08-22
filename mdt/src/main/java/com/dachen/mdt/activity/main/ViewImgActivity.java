package com.dachen.mdt.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dachen.imsdk.R;
import com.dachen.mdt.activity.BaseActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;


/**
 * Created by Mcp on 2016/2/18.
 */
public class ViewImgActivity extends BaseActivity {
    public static final String INTENT_TARGET_INDEX="targetIndex";
    public static final String INTENT_PIC_LIST="pic_list";
    private ViewPager mPager;
    private ArrayList<String> picList;
    private ImgAdapter mAdapter;
    private View vHeader;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_img);
        int targetIndex=   getIntent().getIntExtra(INTENT_TARGET_INDEX,0);
        mPager= (ViewPager) findViewById(R.id.view_pager);
        vHeader =findViewById(R.id.header);
        vHeader.setOnClickListener(this);
        tvTitle= (TextView) findViewById(R.id.title);
        findViewById(R.id.back_btn).setOnClickListener(this);
        picList=getIntent().getStringArrayListExtra(INTENT_PIC_LIST);
        if(picList==null){
            picList=new ArrayList<>();
        }
        mAdapter=new ImgAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
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
        mPager.setCurrentItem(targetIndex);
        changeTitle(targetIndex);
    }

    private void toggleHeader(){
        if(vHeader.isShown()){
            vHeader.setVisibility(View.INVISIBLE);
        }else{
            vHeader.setVisibility(View.VISIBLE);
        }
    }
    private void changeTitle(int index){
        String str=(index+1)+"/"+picList.size();
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
            return ChatImgFragment.newInstance(picList.get(position));
        }

        @Override
        public int getCount() {
            return picList.size();
        }
    }
    public static class ChatImgFragment extends Fragment {
        private String url;
        private ViewImgActivity mParent;

        public static ChatImgFragment newInstance(String url){
            ChatImgFragment frag=new ChatImgFragment();
            Bundle args=new Bundle();
            args.putString("msg",url);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            url =  getArguments().getString("msg");
            mParent= (ViewImgActivity) getActivity();
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
            ImageLoader.getInstance().displayImage(url, v, new DisplayImageOptions.Builder().build());
            return v;
        }
    }

    public static void OpenUi(Activity act,ArrayList<String> urlList, int index){
        Intent i=new Intent(act,ViewImgActivity.class);
        i.putExtra(INTENT_PIC_LIST,urlList).putExtra(INTENT_TARGET_INDEX,index);
        act.startActivity(i);
    }
}
