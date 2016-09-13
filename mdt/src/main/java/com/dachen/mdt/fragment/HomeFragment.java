package com.dachen.mdt.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dachen.common.widget.PagerSlidingTabStrip;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.order.ChoosePatientActivity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.OnClick;

/**
 * [围观页面]
 *
 * @author huxinwu
 * @version 1.0
 * @date 2016/9/13
 */
public class HomeFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private View contentView;
    private PagerSlidingTabStrip slidingtab;
    private HallPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return contentView;
    }

    private void initView(){
        fragmentList = new CopyOnWriteArrayList<>();

        OrderFragment fragment1 = new OrderFragment();
        WatchFragment fragment2 = new WatchFragment();;

        fragmentList.add(fragment1);
        fragmentList.add(fragment2);

        pagerAdapter = new HallPagerAdapter(getChildFragmentManager());
        pagerAdapter.setTitles(new String[]{"待处理订单","可查看订单"});
        pagerAdapter.setFragments(fragmentList);

        viewPager = (ViewPager) contentView.findViewById(R.id.hallViewpager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(fragmentList.size());

        slidingtab = (PagerSlidingTabStrip) contentView.findViewById(R.id.slidingtab);
        slidingtab.setViewPager(viewPager);
        slidingtab.setOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class HallPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;
        private String[] titles;

        public HallPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public CharSequence getPageTitle(int position) {
            if (titles != null) {
                return titles[position];
            }
            return null;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public List<Fragment> getFragments() {
            return fragments;
        }

        public void setFragments(List<Fragment> fragments) {
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        public String[] getTitles() {
            return titles;
        }

        public void setTitles(String[] titles) {
            this.titles = titles;
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_hall;
    }

    @OnClick(R.id.create_order)
    public void createOrder(){
        startActivity(new Intent(getActivity(), ChoosePatientActivity.class));
    }
}
