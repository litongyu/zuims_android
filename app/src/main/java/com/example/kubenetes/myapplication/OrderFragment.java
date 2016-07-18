package com.example.kubenetes.myapplication;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;

import JavaBeans.CurrentRest;
import JavaBeans.Order;
import api.info.MyUrl;
import cn.pedant.SweetAlert.SweetAlertDialog;
import info.hoang8f.widget.FButton;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_order)
public class OrderFragment extends BaseFragment implements AdapterView.OnItemClickListener, PullRefreshLayout.OnRefreshListener{

    private LinkedList<Order> orderList = null;

    private static OrderListAdapter orderListAdapter = null;

    private static boolean visible = false;

    private static Integer notifyCount = 0;

    private static Integer mNotificationId = 1008777;

    private static TextToSpeech tts;

    private int scrollPosition = 0;

    private int topOffset = 0;

    private Context context;

    private CurrentRest rest;

    private Listener listener;

    @ViewInject(R.id.order_list)
    private ListView orderListView;

    @ViewInject(R.id.order_search)
    private EditText orderSearch;

    @ViewInject(R.id.order_pull_refresh)
    private PullRefreshLayout order_pull_refresh;

    private SearchListener searchListener;

    private static ArrayList<String> keyWords = new ArrayList<>();

    private MaterialDialog mMaterialDialog;

    private SweetAlertDialog pDialog;

    public OrderFragment() {
        // Required empty public constructor
    }


    public interface Listener {
        String send(String msg);
    }


    //TODO: 关于onViewCreated和setUserVisibleHint的执行顺序的问题还需要研究,代码写得太丑,初始化的东西按理说都应该写在onViewCreated中

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i("fragmentTest", "onCreate");
        super.onViewCreated(view, savedInstanceState);
        searchListener = new SearchListener();
        orderListView.setOnItemClickListener(this);
        orderSearch.addTextChangedListener(this.searchListener);
        order_pull_refresh.setOnRefreshListener(this);
        orderSearch.clearFocus();
        
        tts = new TextToSpeech(this.getActivity(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.CHINESE);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("lanageTag", "not use");
                    } else {
                        tts.speak("最美食欢迎您", TextToSpeech.QUEUE_FLUSH,
                                null);
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("fragment on start", "yes");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("fragment on resume", "yes");
    }

    private void getAllOrders(final boolean scroll){
        if(orderSearch != null){
            orderSearch.setText("");
        }
        rest = CurrentRest.getInstance();
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(x.app().getResources().getColor(R.color.colorOrange));
        pDialog.setTitleText("订单加载中");
        pDialog.setCancelable(false);
        pDialog.show();
        String url = MyUrl.BaseUrl + MyUrl.merchantPort
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
                orderListAdapter.setAllOrder(orderList);
                orderListAdapter.setOrderList(orderList);
                orderListView.setAdapter(orderListAdapter);

                //恢复滑动位置
                if(scroll) {
                    orderListView.setSelectionFromTop(scrollPosition, topOffset);
                }

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
                order_pull_refresh.setRefreshing(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            pDialog.dismiss();
                        }
                        catch(Exception e){

                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
        if(orderSearch != null){
            orderSearch.setText("");
        }
        keyWords.clear();
        if (isVisibleToUser) {
            Log.i("fragmentTest", "visible");
            notifyCount = 0;
            NotificationManager mNotifyMgr = (NotificationManager)x.app().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.cancel(mNotificationId);
            context = getActivity();
            if (orderListAdapter == null) {
                orderListAdapter = new OrderListAdapter();
            }
            orderListAdapter.setContext(context);
            getAllOrders(true);
        }
        else {
            // fragment is no longer visible
            if(orderList != null){
                //记住滑动位置
                scrollPosition = orderListView.getFirstVisiblePosition();
                View v = orderListView.getChildAt(0);
                topOffset = (v == null) ? 0 : v.getTop();
            }
        }
    }


    //下拉刷新监听
    @Override
    public void onRefresh(){
        //下拉刷新不需要记住滑动位置
        getAllOrders(false);

    }

    //orderListAdapter的回调函数写在这里,但感觉写在orderListAdapter里面也行啊,是不是我代码写的丑,还是就该这么写呢?
    //需要注意,获取Apater的orderList需要调用orderListAdapter.getOrderList(),因为是深复制

    //订单列表点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        mMaterialDialog = new MaterialDialog(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_handle_order, null);
        ViewHolder holder = new ViewHolder(dialogView);
        Order temp = orderListAdapter.getOrderList().get(position);

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

        if (temp.getState().equals("待确认")) {
            holder.dialog_order_accept.setVisibility(View.VISIBLE);
            holder.dialog_vacant_1.setVisibility(View.VISIBLE);
            holder.dialog_order_reject.setVisibility(View.VISIBLE);
            holder.dialog_vacant_2.setVisibility(View.VISIBLE);
            holder.dialog_order_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = MyUrl.BaseUrl + MyUrl.merchantPort + "/order/confirmOrder";
                    RequestParams params = new RequestParams(url);
                    params.addQueryStringParameter("orderId", orderListAdapter.getOrderList().get(position).getOrderId() + "");
                    params.addQueryStringParameter("opt", 1 + "");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Gson gson = new Gson();
                            Order returnOrder = gson.fromJson(result, Order.class);
                            orderListAdapter.getOrderList().get(position).setState(returnOrder.getState());
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
                    String url = MyUrl.BaseUrl + MyUrl.merchantPort + "/order/confirmOrder";
                    RequestParams params = new RequestParams(url);
                    params.addQueryStringParameter("orderId", orderListAdapter.getOrderList().get(position).getOrderId() + "");
                    params.addQueryStringParameter("opt", 0 + "");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Gson gson = new Gson();
                            Order returnOrder = gson.fromJson(result, Order.class);
                            orderListAdapter.getOrderList().get(position).setState(returnOrder.getState());
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

        if (temp.getState().equals("已订座")) {
            holder.dialog_order_finish.setVisibility(View.VISIBLE);
            holder.dialog_vacant_2.setVisibility(View.VISIBLE);
            holder.dialog_order_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = MyUrl.BaseUrl + MyUrl.merchantPort + "/order/finishOrder";
                    RequestParams params = new RequestParams(url);
                    params.addQueryStringParameter("orderId", orderListAdapter.getOrderList().get(position).getOrderId() + "");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Gson gson = new Gson();
                            Order returnOrder = gson.fromJson(result, Order.class);
                            orderListAdapter.getOrderList().get(position).setState(returnOrder.getState());
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
    }//end item onclick

    //搜索框监听
    private class SearchListener implements TextWatcher{
        @Override
        public void afterTextChanged(Editable arg0) {
            String temp = orderSearch.getText().toString();
            keyWords.clear();
            keyWords.addAll(Arrays.asList(temp.split(" ")));
            orderListAdapter.filter(keyWords);
            Log.i("searchView", "kk".contains("")+"");
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1,
                                      int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        }
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

        public ViewHolder() {

        }

        public ViewHolder(View view) {
            x.view().inject(this, view);
        }
    }

    //是否运行在前台
    private static boolean isRunningForeground (Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(x.app().getPackageName())) {
            return true ;
        }

        return false ;
    }
    //消息推送
    public static class CustomReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("orders")) {
                try {
                    JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
                    Log.i("receive order", json+"");
                    
                    tts.speak("您有新的订单，请及时处理" , TextToSpeech.QUEUE_FLUSH,
                            null);
                    
                    String orderStr = json.getString("order");
                    String type = json.getString("operation");
                    Gson gson = new Gson();
                    Order newOrder = gson.fromJson(orderStr, Order.class);
                    if(type.equals("待确认")) {
                        String message = "[新订单]" + "订单号:" + newOrder.getOrderId();
                        //if (!visible || (visible && !runInFrontEnd)) {
                            notifyCount++;
                            if (notifyCount > 1) {
                                message = "[新订单]" + notifyCount + "个新订单";
                            }
                            Intent resultIntent = new Intent(AVOSCloud.applicationContext, MainActivity.class);
                            PendingIntent pendingIntent =
                                    PendingIntent.getActivity(AVOSCloud.applicationContext, 0, resultIntent,
                                            PendingIntent.FLAG_CANCEL_CURRENT);
                            Bitmap LargeBitmap = BitmapFactory.decodeResource(AVOSCloud.applicationContext.getResources(), R.drawable.notification);
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(AVOSCloud.applicationContext)
                                            .setLargeIcon(LargeBitmap)
                                            .setSmallIcon(R.drawable.notification)
                                            .setContentTitle(AVOSCloud.applicationContext.getResources().getString(R.string.app_name))
                                            .setContentText(message)
                                            .setTicker(message)
                                            .setWhen(System.currentTimeMillis())
                                            .setAutoCancel(true)
                                            .setContentIntent(pendingIntent)
                                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);


                            NotificationManager mNotifyMgr =
                                    (NotificationManager) AVOSCloud.applicationContext
                                            .getSystemService(
                                                    Context.NOTIFICATION_SERVICE);
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());
                        //}
                        if (orderListAdapter != null) {
                            Log.i("avpush", "there");
                            orderListAdapter.getAllOrder().add(0, newOrder);
                            orderListAdapter.filter(keyWords);
                            orderListAdapter.notifyDataSetChanged();
                        }
                    }
                    else{
                        if (orderListAdapter != null && visible) {
                            for(int i = 0; i < orderListAdapter.getAllOrder().size(); i++){
                                if(orderListAdapter.getAllOrder().get(i).getOrderId().equals(newOrder.getOrderId())){
                                    orderListAdapter.getAllOrder().get(i).setState(newOrder.getState());
                                    break;
                                }
                            }
                            orderListAdapter.filter(keyWords);
                            orderListAdapter.notifyDataSetChanged();
                        }
                    }
                }
                catch (Exception e){
                    Log.i("avError", e.getMessage());
                }
            }
        }
    }
}
