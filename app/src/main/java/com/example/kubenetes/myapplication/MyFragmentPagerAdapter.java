package com.example.kubenetes.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Jay on 2015/8/31 0031.
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private final int PAGER_COUNT = 4;

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

//    @Override
//    public Object instantiateItem(ViewGroup vg, int position) {
//        return super.instantiateItem(vg, position);
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        System.out.println("position Destory" + position);
//        super.destroyItem(container, position, object);
//    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            return new OrderFragment();
        }
        if(position == 1){
            return new UserFragment();
        }
        if(position == 2){
            return new ThreeFragment();
        }

        return new FourFragment();

    }


}

