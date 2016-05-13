package com.example.kubenetes.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by Jay on 2015/8/31 0031.
 */

//FragmentStatePagerAdapter有bug,只能变通了,在fragment setUserVisibleHint方法中重新刷新,参见OrderFragment

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 2;

    //private ArrayList<BaseFragment> fragmentArrayList = null;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
//        fragmentArrayList = new ArrayList<BaseFragment>();
//        fragmentArrayList.add(new OrderFragment());
//        fragmentArrayList.add(new UserFragment());
//        fragmentArrayList.add(new ThreeFragment());
//        fragmentArrayList.add(new FourFragment());
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            return new OrderFragment();
        }
        else {
            return new UserFragment();
        }
    }


}

