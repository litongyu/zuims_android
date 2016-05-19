package com.example.kubenetes.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import JavaBeans.CurrentRest;
import JavaBeans.RestInfo;
import api.info.MyUrl;

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
        actionBar.setSubtitle("获取餐厅名失败");
        String infoUrl =   MyUrl.BaseUrl + MyUrl.merchantPort + "/restaurant/info";
        RequestParams infoParams = new RequestParams(infoUrl);
        infoParams.addQueryStringParameter("id", CurrentRest.getInstance().getRestaurantId()+"");
        x.http().get(infoParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //保存当前restaurant以及用户名密码
                Gson gson = new Gson();
                RestInfo.setOurInstance(gson.fromJson(result, RestInfo.class));
                actionBar.setSubtitle(RestInfo.getInstance().getRestaurantName());
                //Toast.makeText(x.app(), params.get("restaurantId")+"", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), "获取餐厅信息失败,检查网络", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences settings = getSharedPreferences("account", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("username");
            editor.remove("password");
            editor.commit();
            CurrentRest rest = CurrentRest.getInstance();
            rest.setRestaurantId(null);
            rest.setWhetherInfoComplete(null);
            Intent intent = new Intent();
            intent.setClass(ManageActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

