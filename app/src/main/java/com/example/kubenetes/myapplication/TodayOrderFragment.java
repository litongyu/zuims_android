package com.example.kubenetes.myapplication;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import JavaBeans.CurrentRest;
import JavaBeans.Order;
import api.info.MyUrl;
import info.hoang8f.widget.FButton;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_today_order)
public class TodayOrderFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    private LinkedList<Order> todayOrderList = null;

    private TodayOrderListAdapter todayOrderListAdapter = null;

    private int scrollPosition = 0;

    private int topOffset = 0;

    private Context context;

    private CurrentRest rest;

    @ViewInject(R.id.today_order_list)
    private ListView todayOrderListView;

    @ViewInject(R.id.no_today_order)
    private TextView noTodayOrder;

    private MaterialDialog mMaterialDialog;

    public TodayOrderFragment() {
        // Required empty public constructor
    }

    private boolean todayFilter(String dateTimeStr){
        Date today = new Date();
        String dateStr = dateTimeStr.substring(0, 10);
        DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String result = dateFormat.format(today);
        return result.equals(dateStr);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todayOrderListView.setOnItemClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            getTodayOrders();
        }
    }

    @Event(value = R.id.no_today_order, type = View.OnClickListener.class)
    private void refreshTodayOrder(View v){
        Log.i("textview click", "i see");
        getTodayOrders();
    }

    private void getTodayOrders(){

        context = getActivity();
        if (todayOrderListAdapter == null) {
            todayOrderListAdapter = new TodayOrderListAdapter();
        }
        todayOrderListAdapter.setContext(context);
        rest = CurrentRest.getInstance();
        String url = MyUrl.BaseUrl + MyUrl.merchantPort
                + "/order/infoByrestaurantid";
        RequestParams params = new RequestParams(url);
        params.addQueryStringParameter("restaurantId", rest.getRestaurantId() + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ArrayList<Order> orders = gson.fromJson(result,
                        new TypeToken<ArrayList<Order>>() {
                        }.getType());
                todayOrderList = new LinkedList<Order>();
                for(int i = 0; i < orders.size(); i++){
                    if(todayFilter(orders.get(i).getOrderTime()) &&
                            (orders.get(i).getState().equals("已订座") || orders.get(i).getState().equals("已就餐")) ){
                        todayOrderList.add(0, orders.get(i));
                    }
                }
                if(todayOrderList.size() == 0){
                    noTodayOrder.setVisibility(View.VISIBLE);
                    todayOrderListView.setVisibility(View.GONE);
                }
                else{
                    noTodayOrder.setVisibility(View.GONE);
                    todayOrderListView.setVisibility(View.VISIBLE);
                }
                todayOrderListAdapter.setTodayOrderList(todayOrderList);
                todayOrderListView.setAdapter(todayOrderListAdapter);

                //恢复滑动位置
                //orderListView.setSelectionFromTop(scrollPosition, topOffset);

                //Toast.makeText(x.app(), orderList.size()+"", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), "获取订单失败,请检查网络", Toast.LENGTH_LONG).show();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        mMaterialDialog = new MaterialDialog(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_handle_order, null);
        ViewHolder holder = new ViewHolder(dialogView);
        Order temp = todayOrderListAdapter.getTodayOrderList().get(position);

        holder.dialog_order_number.setText(temp.getOrderId().toString());

        holder.dialog_order_user.setText(temp.getUserLastName() + temp.getUserFirstName() +
                " " + (temp.getGender() == 1 ? "先生" : "女士"));

        holder.dialog_order_userVipLevel.setText(temp.getUserVipLevel());

        holder.dialog_order_phoneId.setText(temp.getPhoneId());

        final String phoneId = temp.getPhoneId();
        holder.dialog_order_phoneId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneId));
                startActivity(intent);
            }
        });

        holder.dialog_order_dinnerNum.setText(temp.getDinerNum() + "");

        holder.dialog_order_orderTime.setText(temp.getOrderTime());

        holder.dialog_order_orderSum.setText(temp.getDorderSum() + " 元");

        holder.dialog_order_state.setText(temp.getState());

        if (temp.getState().equals("已订座")) {
            holder.dialog_order_finish.setVisibility(View.VISIBLE);
            holder.dialog_vacant_2.setVisibility(View.VISIBLE);
            holder.dialog_order_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = MyUrl.BaseUrl + MyUrl.merchantPort + "/order/finishOrder";
                    RequestParams params = new RequestParams(url);
                    params.addQueryStringParameter("orderId", todayOrderListAdapter.getTodayOrderList().get(position).getOrderId() + "");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Gson gson = new Gson();
                            Order returnOrder = gson.fromJson(result, Order.class);
                            todayOrderListAdapter.getTodayOrderList().get(position).setState(returnOrder.getState());
                            todayOrderListAdapter.notifyDataSetChanged();
                            mMaterialDialog.dismiss();
                            Toast.makeText(x.app(), "订单已完成", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(x.app(), "完成订单失败,请检查网络", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                            //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                }
            });
        } //end-if 已订座

        holder.dialog_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(true)
                .setView(dialogView)
                .show();
    }//end item onclick

    private class ViewHolder {

        @ViewInject(R.id.dialog_order_number)
        TextView dialog_order_number;

        @ViewInject(R.id.dialog_order_user)
        TextView dialog_order_user;

        @ViewInject(R.id.dialog_order_userVipLevel)
        TextView dialog_order_userVipLevel;

        @ViewInject(R.id.dialog_order_phoneId)
        TextView dialog_order_phoneId;

        @ViewInject(R.id.dialog_order_dinnerNum)
        TextView dialog_order_dinnerNum;

        @ViewInject(R.id.dialog_order_orderTime)
        TextView dialog_order_orderTime;

        @ViewInject(R.id.dialog_order_orderSum)
        TextView dialog_order_orderSum;

        @ViewInject(R.id.dialog_order_state)
        TextView dialog_order_state;

        @ViewInject(R.id.dialog_order_accept)
        FButton dialog_order_accept;

        @ViewInject(R.id.dialog_vacant_1)
        TextView dialog_vacant_1;

        @ViewInject(R.id.dialog_order_reject)
        FButton dialog_order_reject;

        @ViewInject(R.id.dialog_order_finish)
        FButton dialog_order_finish;

        @ViewInject(R.id.dialog_vacant_2)
        TextView dialog_vacant_2;

        @ViewInject(R.id.dialog_return)
        FButton dialog_return;

        public ViewHolder() {

        }

        public ViewHolder(View view) {
            x.view().inject(this, view);
        }
    }


}
