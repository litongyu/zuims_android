package com.example.kubenetes.myapplication;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_manage)
public class ManageActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener, OrderFragment.Listener {

//    @ViewInject(R.id.restaurantId)
//    private TextView restaurantId;

    @ViewInject(R.id.vpager)
    private ViewPager managePager;

    @ViewInject(R.id.manage_tab_bar)
    private RadioGroup manageBar;

    @ViewInject(R.id.manage_order)
    private RadioButton manageOrder;

    @ViewInject(R.id.manage_todayOrder)
    private RadioButton manageTodayOrder;

    private ActionBar actionBar;

    private MyFragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //x.view().inject(this);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        managePager.setAdapter(mAdapter);
        managePager.setCurrentItem(0);
        managePager.addOnPageChangeListener(this);
        managePager.setOffscreenPageLimit(3);
        manageOrder.setChecked(true);
        manageBar.setOnCheckedChangeListener(this);
        actionBar = getSupportActionBar();
        actionBar.setTitle("最美食");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.logo);
        actionBar.setSubtitle("李桐宇的餐厅");
    }


    //重写ViewPager.OnPageChangeListener方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (managePager.getCurrentItem()) {
                case 0:
                    manageOrder.setChecked(true);
                    break;
                case 1:
                    manageTodayOrder.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.manage_order:
                managePager.setCurrentItem(0);
                break;
            case R.id.manage_todayOrder:
                managePager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public String send(String msg) {
        Toast.makeText(x.app(), msg, Toast.LENGTH_SHORT).show();
        return "haha";
    }
}

