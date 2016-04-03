package com.example.kubenetes.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import JavaBeans.CurrentRest;
import JavaBeans.Order;
import api.info.MyUrl;

@ContentView(R.layout.activity_manage)
public class ManageActivity extends BaseActivity {

    @ViewInject(R.id.restaurantId)
    private TextView restaurantId;

    private CurrentRest rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rest =  CurrentRest.getInstance();
        restaurantId.setText(rest.getRestaurantId() + "");
        //Toast.makeText(x.app(), rest.getRestaurantId() + "", Toast.LENGTH_LONG).show();

        String url =   MyUrl.BaseUrl + MyUrl.merchantPort
                + "/order/infoByrestaurantid";
        RequestParams params = new RequestParams(url);
        params.addQueryStringParameter("restaurantId", rest.getRestaurantId() + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ArrayList<Order> orders = gson.fromJson(result,
                        new TypeToken<ArrayList<Order>>() {}.getType());
                Toast.makeText(x.app(), orders.get(0).toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }
}

