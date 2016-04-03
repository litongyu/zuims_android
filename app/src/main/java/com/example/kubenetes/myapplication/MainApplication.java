package com.example.kubenetes.myapplication;

/**
 * Created by kubenetes on 16/3/27.
 */
import android.app.Application;
import org.xutils.x;

public class MainApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
