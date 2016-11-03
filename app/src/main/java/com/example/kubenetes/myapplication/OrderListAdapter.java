package com.example.kubenetes.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
        final ViewHolder holder = new ViewHolder(convertView);
        final Order temp = orderList.get(position);
        holder.order_number.setText("订单号: " + temp.getOrderId() +
                "  预订人: " + temp.getUserLastName() + temp.getUserFirstName() +
                " " + (temp.getGender()==1?"先生":"女士"));
        holder.order_info.setText(temp.getOrderTime() + "  " +
                temp.getDinerNum() + " 人");
        holder.order_cooper.setVisibility(View.INVISIBLE);

        if(temp.getState().equals("已拒绝")){
            holder.handle_button.setText("已拒绝");
            holder.handle_button.setTextColor(x.app().getResources().getColor(R.color.reject_order));
            holder.handle_button.setButtonColor(x.app().getResources().getColor(R.color.eat_order));
            holder.handle_button.setEnabled(false);
        }
        if(temp.getState().equals("已取消")){
            holder.handle_button.setText("已取消");
            holder.handle_button.setTextColor(x.app().getResources().getColor(R.color.reject_order));
            holder.handle_button.setButtonColor(x.app().getResources().getColor(R.color.eat_order));
            holder.handle_button.setEnabled(false);
        }
        else if(temp.getState().equals("已就餐")){
            holder.handle_button.setText("已就餐");
            holder.handle_button.setButtonColor(x.app().getResources().getColor(R.color.eat_order));
            holder.handle_button.setEnabled(false);
        }
        else if(temp.getState().equals("已订座")){
            String url = MyUrl.BaseUrl + MyUrl.merchantPort + "/order/ddstatusByorderid";
            RequestParams params = new RequestParams(url);
            params.addParameter("orderId", temp.getOrderId());
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String res) {
                    System.out.println(res);
                    if(!res.contains(":\"didi\"")) {
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
                    else {
                        holder.order_cooper.setVisibility(View.VISIBLE);
                        holder.handle_button.setText("核销");
                        holder.handle_button.setButtonColor(x.app().getResources().getColor(R.color.finish_order));
                        holder.handle_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //完成订单
                                final EditText inputServer = new EditText(getContext());
                                AlertDialog.Builder builder  = new AlertDialog.Builder(getContext());
                                builder.setTitle("请输入核销码" );
                                builder.setIcon(android.R.drawable.ic_dialog_info);
                                builder.setView(inputServer);
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String url =  MyUrl.BaseUrl + MyUrl.merchantPort + "/didi/getShopId";
                                        RequestParams params = new RequestParams(url);
                                        params.addParameter("restaurantId",temp.getRestaurantId());
                                        x.http().get(params, new Callback.CommonCallback<String>() {
                                            @Override
                                            public void onSuccess(String res) {
                                                System.out.println(res);
                                                Gson gson = new Gson();
                                                JSONObject json = gson.fromJson(res,JSONObject.class);
                                                String shopId = json.getString("shopId");
                                                String merchantId = json.getString("merchantId");
                                                System.out.println(shopId+","+merchantId+"");
                                                String couponCode = inputServer.getText().toString();
                                                JSONObject confirmCodeParameterContext = new JSONObject();
                                                confirmCodeParameterContext.put("orderId",temp.getOrderId());
                                                confirmCodeParameterContext.put("appId","");
                                                confirmCodeParameterContext.put("token","");
                                                confirmCodeParameterContext.put("couponCode",couponCode);
                                                confirmCodeParameterContext.put("logId","");
                                                confirmCodeParameterContext.put("shopId",shopId);
                                                confirmCodeParameterContext.put("merchantId",merchantId);
                                                confirmCodeParameterContext.put("cavName","最美食");
                                                System.out.println(confirmCodeParameterContext);

                                                String url =   MyUrl.BaseUrl + MyUrl.merchantPort + "/order/ddConfirm";
                                                RequestParams params = new RequestParams(url);
                                                params.setAsJsonContent(true);
                                                params.setBodyContent(confirmCodeParameterContext.toString());
                                                x.http().post(params, new Callback.CommonCallback<Boolean>() {
                                                    @Override
                                                    public void onSuccess(Boolean isConfirmed) {
                                                        System.out.println(isConfirmed+"hehe");
                                                        System.out.println();
                                                        if(isConfirmed == true) {
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
                                                                    Toast.makeText(x.app(), "核销成功", Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void onError(Throwable ex, boolean isOnCallback) {
                                                                    Toast.makeText(x.app(), "核销失败", Toast.LENGTH_SHORT).show();
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
                                                        else {
                                                            Toast.makeText(x.app(), "核销失败", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Throwable ex, boolean isOnCallback) {
                                                        //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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

                                            @Override
                                            public void onError(Throwable ex, boolean isOnCallback) {
                                                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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
                                builder.setNegativeButton("取消" ,  null );
                                builder.show();
                            }
                        });
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

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

        @ViewInject(R.id.order_cooper)
        TextView order_cooper;

        @ViewInject(R.id.handle_button)
        FButton handle_button;

        public ViewHolder(){

        }

        public ViewHolder(View view){
            x.view().inject(this, view);
        }
    }
}
