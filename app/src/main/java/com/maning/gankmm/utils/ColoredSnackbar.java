package com.maning.gankmm.utils;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.maning.gankmm.R;


/**
 * Created by maning on 16/1/18.
 * <p/>
 * 给SnackBar设置颜色的
 */
public class ColoredSnackbar {

    private static final int red = 0xfff44336;
    private static final int green = 0xff4caf50;
    private static final int blue = 0xff2195f3;
    private static final int orange = 0xffffc107;
    private static final int black = 0xff2e2e2e;

    private static View getSnackBarLayout(Snackbar snackbar) {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;
    }

    private static Snackbar colorSnackBar(Snackbar snackbar, int colorId) {
        View snackBarView = getSnackBarLayout(snackbar);
        if (snackBarView != null) {
            //设置背景色
            snackBarView.setBackgroundColor(colorId);
            //设置透明度
            snackBarView.setAlpha(0.8f);
            //这只内容文字的颜色
            ((TextView) snackBarView.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#FFFFFF"));
            //设置显示位置
//            ViewGroup.LayoutParams vl = snackBarView.getLayoutParams();
//            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(vl.width, vl.height);
//            ll.gravity = Gravity.TOP;
//            snackBarView.setLayoutParams(ll);
        }

        return snackbar;
    }

    public static Snackbar defaultInfo(Snackbar snackbar) {
        return colorSnackBar(snackbar, black);
    }

    public static Snackbar info(Snackbar snackbar) {
        return colorSnackBar(snackbar, blue);
    }

    public static Snackbar warning(Snackbar snackbar) {
        return colorSnackBar(snackbar, orange);
    }

    public static Snackbar alert(Snackbar snackbar) {
        return colorSnackBar(snackbar, red);
    }

    public static Snackbar confirm(Snackbar snackbar) {
        return colorSnackBar(snackbar, green);
    }

}
