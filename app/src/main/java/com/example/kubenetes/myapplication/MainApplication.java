package com.example.kubenetes.myapplication;

/**
 * Created by kubenetes on 16/3/27.
 */

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;

import org.xutils.x;

public class MainApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        //开启debug或影响性能
        //x.Ext.setDebug(BuildConfig.DEBUG);

        //android bootstrap init
        TypefaceProvider.registerDefaultIconSets();
    }
}
