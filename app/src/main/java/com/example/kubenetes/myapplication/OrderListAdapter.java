package com.example.kubenetes.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;

import JavaBeans.Order;
import api.info.MyUrl;
import info.hoang8f.widget.FButton;
/**
 * Created by kubenetes on 16/4/4.
 */
public class OrderListAdapter extends BaseAdapter {

    private LinkedList<Order> orderList = new LinkedList<Order>();

    private LinkedList<Order> allOrder = new LinkedList<Order>();

    private Context context;

    private LayoutInflater inflater;

    public OrderListAdapter() {
    }

    public OrderListAdapter(LinkedList<Order> orderList, Context context) {
        this.orderList.clear();
        this.orderList.addAll(orderList);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public LinkedList<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(LinkedList<Order> orderList) {
        this.orderList.clear();
        this.orderList.addAll(orderList);
    }

    public LinkedList<Order> getAllOrder() {
        return allOrder;
    }

    public void setAllOrder(LinkedList<Order> allOrder) {
        this.allOrder.clear();
        this.allOrder.addAll(allOrder);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO: 没有按照网上的优化,貌似这几个if判断会出bug,优化的事情后面再说吧

        convertView = inflater.inflate(R.layout.order_item, parent, false);
        ViewHolder holder = new ViewHolder(convertView);
        Order temp = orderList.get(position);
        holder.order_number.setText("订单号: " + temp.getOrderId() +
                "  预订人: " + temp.getUserLastName() + temp.getUserFirstName() +
                " " + (temp.getGender()==1?"先生":"女士"));
        holder.order_info.setText(temp.getOrderTime() + "  " +
                temp.getDinerNum() + " 人");

        if(temp.getState().equals("已拒绝")){
            holder.handle_button.setText("\b\b已拒绝\b\b");
            holder.handle_button.setTextColor(x.app().getResources().getColor(R.color.reject_order));
            holder.handle_button.setButtonColor(x.app().getResources().getColor(R.color.eat_order));
            holder.handle_button.setEnabled(false);
        }
        else if(temp.getState().equals("已就餐")){
            holder.handle_button.setText("\b\b已就餐\b\b");
            holder.handle_button.setButtonColor(x.app().getResources().getColor(R.color.eat_order));
            holder.handle_button.setEnabled(false);
        }
        else if(temp.getState().equals("已订座")){
            holder.handle_button.setText("确认就餐");
            holder.handle_button.setButtonColor(x.app().getResources().getColor(R.color.finish_order));
            holder.handle_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //完成订单
                    String url =  MyUrl.BaseUrl + MyUrl.merchantPort + "/order/finishOrder";
                    RequestParams params = new RequestParams(url);
                    params.addQueryStringParameter("orderId", orderList.get(position).getOrderId()+"");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Gson gson = new Gson();
                            Order returnOrder = gson.fromJson(result, Order.class);
                            orderList.get(position).setState(returnOrder.getState());
                            Log.i("complish order", result);
                            notifyDataSetChanged();
                            Toast.makeText(x.app(), "订单已完成", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
        else if(temp.getState().equals("待确认")){
            holder.handle_button.setText("接受订单");
            holder.handle_button.setButtonColor(x.app().getResources().getColor(R.color.accept_order));
            holder.handle_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //接受订单
                    String url =   MyUrl.BaseUrl + MyUrl.merchantPort + "/order/confirmOrder";
                    RequestParams params = new RequestParams(url);
                    params.addQueryStringParameter("orderId", orderList.get(position).getOrderId()+"");
                    params.addQueryStringParameter("opt", 1 + "");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Gson gson = new Gson();
                            Order returnOrder = gson.fromJson(result, Order.class);
                            orderList.get(position).setState(returnOrder.getState());
                            notifyDataSetChanged();
                            Toast.makeText(x.app(), "订单接受成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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
        }

        return convertView;
    }

    public void filter(ArrayList<String> keyWords){
        if(keyWords == null || keyWords.size() == 0){
            this.orderList.clear();
            this.orderList.addAll(this.allOrder);
            notifyDataSetChanged();
            return;
        }
        this.orderList.clear();
        //是否包含所有的字符串
        for(int i = this.allOrder.size() - 1; i >= 0; i--){
            boolean flag = true;
            for(int j = 0; j < keyWords.size(); j++) {
                if (!this.allOrder.get(i).getSearchString().contains(keyWords.get(j))) {
                    flag = false;
                    break;
                }
            }
            if(flag) {
                this.orderList.add(0, this.allOrder.get(i));
            }
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        @ViewInject(R.id.order_number)
        TextView order_number;

        @ViewInject(R.id.order_info)
        TextView order_info;

        @ViewInject(R.id.handle_button)
        FButton handle_button;

        public ViewHolder(){

        }

        public ViewHolder(View view){
            x.view().inject(this, view);
        }
    }
}
