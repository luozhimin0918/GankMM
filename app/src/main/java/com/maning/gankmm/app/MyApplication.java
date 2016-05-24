package com.maning.gankmm.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.maning.gankmm.BuildConfig;
import com.maning.gankmm.crash.CrashHandler;
import com.socks.library.KLog;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by maning on 16/3/2.
 */
public class MyApplication extends Application {

    private static MyApplication application;
    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        //Jpush
        initJpush();

        initBase();

        //初始化异常捕获
        initCrash();

        //初始化Log
        KLog.init(BuildConfig.LOG_DEBUG);

    }

    private void initJpush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    private void initBase() {
        application = this;
        mHandler = new Handler();
    }


    public static OkHttpClient defaultOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(15 * 1000, TimeUnit.MILLISECONDS);
        client.setReadTimeout(20 * 1000, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(30 * 1000, TimeUnit.MILLISECONDS);
        return client;
    }


    private void initCrash() {
        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static MyApplication getIntstance() {
        return application;
    }

    public static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

}
