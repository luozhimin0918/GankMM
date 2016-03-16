package com.maning.gankmm.utils;

import android.content.Context;
import android.widget.Toast;

import com.maning.gankmm.app.MyApplication;

/**
 * Created by Administrator on 2014/11/4.
 * ToastUtils
 */
public class MyToast {

    private static Toast mToast = null;

    private static void showShortToast(Context context, String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void showShortToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApplication.getIntstance(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

}
