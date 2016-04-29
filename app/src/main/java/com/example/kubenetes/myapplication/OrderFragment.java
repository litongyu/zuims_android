package com.example.kubenetes.myapplication;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.LinkedList;

import JavaBeans.CurrentRest;
import JavaBeans.Order;
import api.info.MyUrl;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_order)
public class  OrderFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    private LinkedList<Order> orderList = null;

    private OrderListAdapter orderListAdapter = null;

    private Context context;

    private CurrentRest rest;

    private Listener listener;

    @ViewInject(R.id.order_list)
    private ListView orderListView;

    public OrderFragment() {
        // Required empty public constructor
    }


    public interface Listener{
        String send(String msg);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        context = getActivity();
        orderListView.setOnItemClickListener(this);
//        orderList = new LinkedList<Order>();
//        orderListAdapter = new OrderListAdapter(orderList, context);
//        orderListView.setAdapter(orderListAdapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("fragment visible", isVisibleToUser + "");
        if (isVisibleToUser) {
            context = getActivity();
            orderListAdapter = new OrderListAdapter();
            orderListAdapter.setContext(context);
            Toast.makeText(x.app(), "首页才显示", Toast.LENGTH_LONG).show();
            rest =  CurrentRest.getInstance();
            String url =   MyUrl.BaseUrl + MyUrl.merchantPort
                    + "/order/infoByrestaurantid";
            RequestParams params = new RequestParams(url);
            params.addQueryStringParameter("restaurantId", rest.getRestaurantId() + "");
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    Log.i("orders", result);
                    ArrayList<Order> orders = gson.fromJson(result,
                            new TypeToken<ArrayList<Order>>() {
                            }.getType());
                    orderList = new LinkedList<Order>(orders);
                    orderListAdapter.setOrderList(orderList);
                    orderListView.setAdapter(orderListAdapter);

                    //Toast.makeText(x.app(), orderList.size()+"", Toast.LENGTH_LONG).show();
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
        }else{
            // fragment is no longer visible
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_handle_order, null);
        ViewHolder holder = new ViewHolder();
        Order temp = orderList.get(position);

        holder.dialog_order_number = (TextView)dialogView.findViewById(R.id.dialog_order_number);
        holder.dialog_order_number.setText(temp.getOrderId().toString());

        holder.dialog_order_user = (TextView)dialogView.findViewById(R.id.dialog_order_user);
        holder.dialog_order_user.setText(temp.getUserLastName() + temp.getUserFirstName() +
                " " + (temp.getGender()==1?"先生":"女士"));

        holder.dialog_order_userVipLevel = (TextView)dialogView.findViewById(R.id.dialog_order_userVipLevel);
        holder.dialog_order_userVipLevel.setText(temp.getUserVipLevel());

        holder.dialog_order_createTime = (TextView)dialogView.findViewById(R.id.dialog_order_createTime);
        holder.dialog_order_createTime.setText(temp.getCreateTime());

        holder.dialog_order_dinnerNum = (TextView)dialogView.findViewById(R.id.dialog_order_dinnerNum);
        holder.dialog_order_dinnerNum.setText(temp.getDinerNum() + "");

        holder.dialog_order_orderTime = (TextView)dialogView.findViewById(R.id.dialog_order_orderTime);
        holder.dialog_order_orderTime.setText(temp.getOrderTime());

        holder.dialog_order_orderSum = (TextView)dialogView.findViewById(R.id.dialog_order_orderSum);
        holder.dialog_order_orderSum.setText(temp.getDorderSum() + " 元");

        holder.dialog_order_state = (TextView)dialogView.findViewById(R.id.dialog_order_state);
        holder.dialog_order_state.setText(temp.getState());

        MaterialDialog mMaterialDialog = new MaterialDialog(getActivity())
                .setTitle("MaterialDialog")
                .setMessage("Hello world!")
                .setCanceledOnTouchOutside(true)
                .setView(dialogView);
        mMaterialDialog.show();

// You can change the message anytime. before show
        mMaterialDialog.setTitle("提示");
// You can change the message anytime. after show
        mMaterialDialog.setMessage("你好，世界~");
    }

    private class ViewHolder {
        TextView dialog_order_number;
        TextView dialog_order_user;
        TextView dialog_order_userVipLevel;
        TextView dialog_order_createTime;
        TextView dialog_order_dinnerNum;
        TextView dialog_order_orderTime;
        TextView dialog_order_orderSum;
        TextView dialog_order_state;
    }
}
