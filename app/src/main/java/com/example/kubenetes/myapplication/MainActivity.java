package com.example.kubenetes.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import JavaBeans.CurrentRest;
import api.info.MyUrl;
import cn.pedant.SweetAlert.SweetAlertDialog;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;

//    @ViewInject(R.id.fab)
//    private FloatingActionButton fab;

    @ViewInject(R.id.button_login)
    private Button button_login;

    @ViewInject(R.id.userName)
    private EditText account;

    @ViewInject(R.id.password)
    private EditText password;

    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("最美食");//设置主标题
        //toolbar.setLogo(R.drawable.logo);
        //toolbar.setSubtitle("Subtitle");//设置子标题
        //setSupportActionBar(toolbar);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                Log.i("email", "litongyu");
//            }
//        });
        SharedPreferences settings = getSharedPreferences("account", Activity.MODE_PRIVATE);
        String usernameStr = settings.getString("username", null);
        String passwordStr = settings.getString("password", null);
        if(usernameStr != null && passwordStr != null) {
            Log.i("username", usernameStr);
            Log.i("password", passwordStr);
            account.setText(usernameStr);
            password.setText(passwordStr);
            button_login.performClick();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CurrentRest.getInstance().getRestaurantId() != null &&
                CurrentRest.getInstance().getWhetherInfoComplete() != null){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ManageActivity.class);
            startActivity(intent);
        }
    }

    @Event(value = R.id.button_login, type = View.OnClickListener.class)
    private void login(View view) {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(x.app().getResources().getColor(R.color.colorOrange));
        pDialog.setTitleText("登录中");
        pDialog.setCancelable(false);
        pDialog.show();
        String url =   MyUrl.BaseUrl + MyUrl.merchantPort + "/restaurant/login";
        RequestParams params = new RequestParams(url);
        params.setHeader("Content-Type","application/json;charset=UTF-8");
        params.addBodyParameter("account", account.getText().toString());
        params.addBodyParameter("password", password.getText().toString());
        params.setAsJsonContent(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //保存当前restaurant以及用户名密码
                Gson gson = new Gson();
                CurrentRest.setOurInstance(gson.fromJson(result, CurrentRest.class));
                SharedPreferences settings = getSharedPreferences("account", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", account.getText().toString());
                editor.putString("password", password.getText().toString());
                editor.commit();
                PushService.subscribe(x.app(), CurrentRest.getInstance().getRestaurantId()+"", MainActivity.class);
                AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        AVInstallation.getCurrentInstallation().saveInBackground();
                    }
                });
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ManageActivity.class);
                startActivity(intent);
                //Toast.makeText(x.app(), params.get("restaurantId")+"", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), "登录失败,请检查网络", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {
                pDialog.dismiss();
            }
        });
    }
}
