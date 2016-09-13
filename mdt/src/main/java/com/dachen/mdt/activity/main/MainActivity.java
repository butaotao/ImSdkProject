package com.dachen.mdt.activity.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

import com.dachen.imsdk.utils.PushUtils;
import com.dachen.mdt.R;
import com.dachen.mdt.activity.BaseActivity;
import com.dachen.mdt.fragment.HomeFragment;
import com.dachen.mdt.fragment.MeFragment;
import com.dachen.mdt.fragment.OrderFragment;
import com.dachen.mdt.fragment.PatientFragment;
import com.dachen.mdt.listener.DefaultPageChangeListener;
import com.dachen.mdt.util.SpUtils;

public class MainActivity extends BaseActivity {

    private int[] tabIds=new int[]{R.id.rl_tab_order,R.id.rl_tab_patient,R.id.rl_tab_me};
    private Fragment[] mFragments=new Fragment[3];
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragments[0]=new HomeFragment();
        mFragments[1]=new PatientFragment();
        mFragments[2]=new MeFragment();
        mPager= (ViewPager) findViewById(R.id.view_pager);
        mPager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        mPager.setOnPageChangeListener(new DefaultPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                selectTab(position);
            }
        });

        for(int i=0;i<tabIds.length;i++){
            final int finalI = i;
            findViewById(tabIds[i]).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(finalI==mPager.getCurrentItem())return;
                    mPager.setCurrentItem(finalI,false);
                }
            });
        }
        selectTab(0);
        PushUtils.registerDevice(0, SpUtils.getSp().getString(SpUtils.KEY_XIAOMI_TOKEN,""),null);
    }

    private void selectTab(int selIndex){
        for(int i=0;i<tabIds.length;i++){
            findViewById(tabIds[i]).setSelected(i==selIndex);
        }
    }

    private class MainAdapter extends FragmentPagerAdapter {

        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
