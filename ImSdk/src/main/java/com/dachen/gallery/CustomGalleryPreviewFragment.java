package com.dachen.gallery;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.dachen.imsdk.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Mcp on 2016/4/7.
 */
public class CustomGalleryPreviewFragment extends Fragment implements OnClickListener {
    private CustomGalleryActivity mParent;
    private ArrayList<CustomGallery> mList;
    private ViewPager mViewPager;
    private CheckBox mCheckSelected;
    private CheckBox mCheckOrigin;
    private TextView tvConfirm;
    private int mIndex;
    private HashSet<String> selectedPathSet;
    private TextView tvTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParent= (CustomGalleryActivity) getActivity();
        mList=mParent.currentGalleryList;
        selectedPathSet=mParent.selectedPathSet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.gallery_preview,container,false);
        mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        tvConfirm= (TextView) v.findViewById(R.id.btn_confirm);
        mCheckOrigin= (CheckBox) v.findViewById(R.id.check_origin);
        mCheckSelected= (CheckBox) v.findViewById(R.id.check_select);
        tvTitle= (TextView) v.findViewById(R.id.tv_title);
        if(!mParent.isMultiPick)
            mCheckSelected.setVisibility(View.GONE);

        mViewPager.setAdapter(new ImgAdapter());
        mViewPager.setCurrentItem(mIndex);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mIndex = position;
                refreshForIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mCheckOrigin.setChecked(mParent.mCheckOrigin.isChecked());
        mCheckOrigin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mParent.mCheckOrigin.setChecked(isChecked);
            }
        });
        mCheckSelected.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomGallery item = mList.get(mIndex);
                mParent.adapter.changeSelection(item.sdcardPath);
                mCheckSelected.setChecked(selectedPathSet.contains(item.sdcardPath));
                refreshSelNum();
            }
        });
        v.findViewById(R.id.back_btn).setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

        refreshForIndex(mIndex);
        refreshSelNum();
        return v;
    }
    public void setIndex(int index){
        mIndex=index;
        if(mViewPager!=null)
            mViewPager.setCurrentItem(index);
    }
    private void refreshForIndex(int index){
        CustomGallery item=mList.get(index);
        mCheckSelected.setChecked(selectedPathSet.contains(item.sdcardPath));
        String titleStr= (index+1)+"/"+mList.size();
        tvTitle.setText(titleStr);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.back_btn){
            getFragmentManager().beginTransaction().remove(this).commit();
            mParent.onClosePreview();
        }else if(id==R.id.btn_confirm){
            if(mParent.isMultiPick){
                mParent.clickOk();
            }else{
                mParent.setSingleResult(mList.get(mIndex).sdcardPath);
            }
        }
    }

    private void refreshSelNum(){
        if(!mParent.isMultiPick)return;
        int num=mParent.adapter.getSelectCount();
        String txt="确定";
        txt+=String.format( "(%d)",num);
        tvConfirm.setText(txt);
    }

    @Override
    public void onDestroyView() {
        mParent.onClosePreview();
        super.onDestroyView();
    }

    private class ImgAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView v=new PhotoView(getActivity());
            v.setBackgroundColor(Color.BLACK);
//            ImageLoader.getInstance().displayImage("file://" +mList.get(position).sdcardPath, v, ImUtils.getNormalImageOptions());
            Picasso.with(mParent).load("file://" +mList.get(position).sdcardPath).centerInside().resize(1920,1920).placeholder(R.drawable.defaultpic)
                    .error(R.drawable.image_download_fail_icon).into(v);
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v= (View) object;
            container.removeView(v);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }

//    private class ImgAdapter extends FragmentPagerAdapter {
//        public ImgAdapter(FragmentManager fm) {
//            super(fm);
//        }
//        @Override
//        public Fragment getItem(int position) {
//            return GalleryImgFragment.newInstance(mList.get(position));
//        }
//
//        @Override
//        public int getItemPosition(Object object) {
//            GalleryImgFragment fragment= (GalleryImgFragment) object;
//            return POSITION_NONE;
//        }
//
//        @Override
//        public int getCount() {
//            return mList.size();
//        }
//
//    }
//
//    public static class GalleryImgFragment extends Fragment {
//        public CustomGallery item;
//
//        public static GalleryImgFragment newInstance(CustomGallery item){
//            GalleryImgFragment frag=new GalleryImgFragment();
//            Bundle args=new Bundle();
//            args.putSerializable("item",item);
//            frag.setArguments(args);
//            return frag;
//        }
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            item = (CustomGallery) getArguments().getSerializable("item");
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            PhotoView v=new PhotoView(getActivity());
//            v.setBackgroundColor(Color.BLACK);
//            ImageLoader.getInstance().displayImage("file://" +item.sdcardPath, v, ImUtils.getNormalImageOptions());
//            return v;
//        }
//    }
}
