package com.maning.gankmm.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.maning.gankmm.utils.StatusBarCompat;

/**
 * Created by maning on 16/3/2.
 * <p/>
 * 父类
 */
public class BaseActivity extends AppCompatActivity {

    private SVProgressHUD mSVProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatus();

        initDialog();

    }

    private void initStatus() {
        //设置状态栏的颜色
        StatusBarCompat.setStatusBarColor(this);
    }

    private void initDialog() {
        mSVProgressHUD = new SVProgressHUD(this);
    }

    public void showProgressDialog() {
        dissmissProgressDialog();
        mSVProgressHUD.showWithStatus("加载中...", SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
    }

    public void dissmissProgressDialog() {
        if (mSVProgressHUD.isShowing()) {
            mSVProgressHUD.dismiss();
        }
    }

    public void initToolBar(Toolbar toolbar, String title, int icon) {
        toolbar.setTitle(title);// 标题的文字需在setSupportActionBar之前，不然会无效
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(icon);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mSVProgressHUD.isShowing()) {
                mSVProgressHUD.dismiss();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

}
