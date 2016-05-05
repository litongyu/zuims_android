package com.example.kubenetes.myapplication;


import android.content.Context;
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
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import JavaBeans.CurrentRest;
import JavaBeans.Order;
import api.info.MyUrl;
import info.hoang8f.widget.FButton;
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

    private MaterialDialog mMaterialDialog;

    public OrderFragment() {
        // Required empty public constructor
    }


    public interface Listener{
        String send(String msg);
    }


    //TODO: 关于onViewCreated和setUserVisibleHint的执行顺序的问题还需要研究,代码写得太丑,初始化的东西按理说都应该写在onViewCreated中

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderListView.setOnItemClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("fragment visible", isVisibleToUser + "");
        if (isVisibleToUser) {
            context = getActivity();
            if(orderListAdapter == null) {
                orderListAdapter = new OrderListAdapter();
            }
            orderListAdapter.setContext(context);
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
                    Collections.reverse(orders);
                    orderList = new LinkedList<Order>(orders);
                    orderListAdapter.setOrderList(orderList);
                    orderListView.setAdapter(orderListAdapter);

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
        }else{
            // fragment is no longer visible
        }
    }

    //orderListAdapter的回调函数写在这里,但感觉写在orderListAdapter里面也行啊,是不是我代码写的丑,还是就该这么写呢?
    //需要注意,获取Apater的orderList需要调用orderListAdapter.getOrderList(),因为是深复制

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        mMaterialDialog = new MaterialDialog(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_handle_order, null);
        ViewHolder holder = new ViewHolder(dialogView);
        Order temp = orderListAdapter.getOrderList().get(position);

        holder.dialog_order_number.setText(temp.getOrderId().toString());

        holder.dialog_order_user.setText(temp.getUserLastName() + temp.getUserFirstName() +
                " " + (temp.getGender()==1?"先生":"女士"));

        holder.dialog_order_userVipLevel.setText(temp.getUserVipLevel());

        holder.dialog_order_phoneId.setText(temp.getPhoneId());

        holder.dialog_order_dinnerNum.setText(temp.getDinerNum() + "");

        holder.dialog_order_orderTime.setText(temp.getOrderTime());

        holder.dialog_order_orderSum.setText(temp.getDorderSum() + " 元");

        holder.dialog_order_state.setText(temp.getState());

        if(temp.getState().equals("待确认")){
            holder.dialog_order_accept.setVisibility(View.VISIBLE);
            holder.dialog_vacant_1.setVisibility(View.VISIBLE);
            holder.dialog_order_reject.setVisibility(View.VISIBLE);
            holder.dialog_vacant_2.setVisibility(View.VISIBLE);
            holder.dialog_order_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url =   MyUrl.BaseUrl + MyUrl.merchantPort + "/order/confirmOrder";
                    RequestParams params = new RequestParams(url);
                    params.addQueryStringParameter("orderId", orderListAdapter.getOrderList().get(position).getOrderId()+"");
                    params.addQueryStringParameter("opt", 1 + "");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            orderListAdapter.getOrderList().get(position).setState("已订座");
                            orderListAdapter.notifyDataSetChanged();
                            mMaterialDialog.dismiss();
                            Toast.makeText(x.app(), "订单接受成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(x.app(), "接受订单失败,请检查网络", Toast.LENGTH_SHORT).show();
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
            holder.dialog_order_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url =   MyUrl.BaseUrl + MyUrl.merchantPort + "/order/confirmOrder";
                    RequestParams params = new RequestParams(url);
                    params.addQueryStringParameter("orderId", orderListAdapter.getOrderList().get(position).getOrderId()+"");
                    params.addQueryStringParameter("opt", 0 + "");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            orderListAdapter.getOrderList().get(position).setState("已拒绝");
                            orderListAdapter.notifyDataSetChanged();
                            mMaterialDialog.dismiss();
                            Toast.makeText(x.app(), "订单已拒绝", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(x.app(), "拒绝订单失败,请检查网络", Toast.LENGTH_SHORT).show();
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

        } //end if 待确认

        if(temp.getState().equals("已订座")){
            holder.dialog_order_finish.setVisibility(View.VISIBLE);
            holder.dialog_vacant_2.setVisibility(View.VISIBLE);
            holder.dialog_order_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url =   MyUrl.BaseUrl + MyUrl.merchantPort + "/order/finishOrder";
                    RequestParams params = new RequestParams(url);
                    params.addQueryStringParameter("orderId", orderListAdapter.getOrderList().get(position).getOrderId()+"");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            orderListAdapter.getOrderList().get(position).setState("已就餐");
                            orderListAdapter.notifyDataSetChanged();
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
//        temp.setState("你是sb");
//        orderListAdapter.notifyDataSetChanged();
        //mMaterialDialog.setTitle("提示");

        //mMaterialDialog.setMessage("你好，世界~");
    }

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

        public ViewHolder(){

        }

        public ViewHolder(View view){
            x.view().inject(this, view);
        }
    }
}
