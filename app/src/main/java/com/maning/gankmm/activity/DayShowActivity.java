package com.maning.gankmm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.maning.gankmm.R;
import com.maning.gankmm.base.BaseActivity;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.StatusBarCompat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DayShowActivity extends BaseActivity {


    @Bind(R.id.iv_top)
    ImageView ivTop;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;

    private String dayDate;
    private String imageUrl = "http://ac-OLWHHM4o.clouddn.com/4063qegYjlC8nx6uEqxV0kT3hn6hdqJqVWPKpdrS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_show);
        ButterKnife.bind(this);

        //设置状态栏的颜色
        StatusBarCompat.setStatusBarColor(this,StatusBarCompat.COLOR_DEFAULT_TRANSLATE);

        initIntent();

        initBar();

        Glide
                .with(this)
                .load(imageUrl)
                .into(ivTop);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initIntent() {

        Intent intent = getIntent();

        dayDate = intent.getStringExtra(IntentUtils.DayDate);

    }


    private void initBar() {
        initToolBar(toolbar, dayDate, R.drawable.ic_back);
        collapsingToolbar.setTitle(dayDate);
    }

}
