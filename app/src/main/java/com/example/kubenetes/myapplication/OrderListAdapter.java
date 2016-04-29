package com.example.kubenetes.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

import JavaBeans.Order;

/**
 * Created by kubenetes on 16/4/4.
 */
public class OrderListAdapter extends BaseAdapter {

    private LinkedList<Order> orderList;

    private Context context;

    public OrderListAdapter() {
    }

    public OrderListAdapter(LinkedList<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    public LinkedList<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(LinkedList<Order> orderList) {
        this.orderList = orderList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
            holder = new ViewHolder();
            holder.order_number = (TextView) convertView.findViewById(R.id.order_number);
            holder.order_info = (TextView) convertView.findViewById(R.id.order_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Order temp = orderList.get(position);
        holder.order_number.setText("订单号: " + temp.getOrderId() +
                "  预订人: " + temp.getUserLastName() + " " + temp.getUserFirstName() +
                " " + (temp.getGender()==1?"先生":"女士"));
        holder.order_info.setText(temp.getOrderTime() + "  " +
                temp.getDinerNum() + " 人");
        return convertView;
    }

    private class ViewHolder {
        TextView order_number;
        TextView order_info;
    }
}
