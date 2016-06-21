package com.maning.gankmm.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.maning.gankmm.R;
import com.maning.gankmm.ui.adapter.RecycleDayAdapter;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.bean.DayEntity;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.http.MyCallBack;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.utils.DensityUtil;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.maning.gankmm.utils.StatusBarCompat;
import com.maning.gankmm.ui.view.FullyLinearLayoutManager;
import com.maning.gankmm.ui.view.ProgressWheel;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DayShowActivity extends BaseActivity {


    private static final String TAG = DayShowActivity.class.getSimpleName();
    @Bind(R.id.iv_top)
    ImageView ivTop;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.recycleView)
    RecyclerView myRecycleView;
    @Bind(R.id.progressbar)
    ProgressWheel progressbar;

    private String dayDate;
    private DayEntity dayEntity;

    private MyCallBack httpCallBack = new MyCallBack() {
        @Override
        public void onSuccessList(int what, List results) {

        }

        @Override
        public void onSuccess(int what, Object result) {
            if (isFinishing()) {
                return;
            }
            dayEntity = (DayEntity) result;
            if (dayEntity != null) {
                String url = dayEntity.getResults().get福利().get(0).getUrl();
                initImageView(url);
                //初始化数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initDatas(dayEntity);
                    }
                }).start();
            }
        }

        @Override
        public void onFail(int what, String result) {
            if (isFinishing()) {
                return;
            }
            dissmissProgressDialog();
            if (!TextUtils.isEmpty(result)) {
                MySnackbar.makeSnackBarRed(toolbar,result);
            }
        }
    };

    private List<GankEntity> dayEntityArrayList = new ArrayList<>();

    private void initDatas(DayEntity dayEntity) {
        dayEntityArrayList.clear();

        GankEntity gankEntity_other;
        List<DayEntity.ResultsEntity.AndroidEntity> androidEntityList = dayEntity.getResults().getAndroid();
        if (androidEntityList != null && androidEntityList.size() > 0) {
            GankEntity gankEntity_title = new GankEntity();
            gankEntity_title.setType("title");
            gankEntity_title.setDesc("Android");
            dayEntityArrayList.add(gankEntity_title);
            for (int i = 0; i < androidEntityList.size(); i++) {
                DayEntity.ResultsEntity.AndroidEntity androidEntity = androidEntityList.get(i);
                gankEntity_other = new GankEntity();
                gankEntity_other.set_id(androidEntity.get_id());
                gankEntity_other.setDesc(androidEntity.getDesc());
                gankEntity_other.setType(androidEntity.getType());
                gankEntity_other.setCreatedAt(androidEntity.getCreatedAt());
                gankEntity_other.setPublishedAt(androidEntity.getPublishedAt());
                gankEntity_other.setUrl(androidEntity.getUrl());
                dayEntityArrayList.add(gankEntity_other);
            }
        }

        List<DayEntity.ResultsEntity.IOSEntity> iosEntityList = dayEntity.getResults().getIOS();
        if (iosEntityList != null && iosEntityList.size() > 0) {
            GankEntity gankEntity_title02 = new GankEntity();
            gankEntity_title02.setType("title");
            gankEntity_title02.setDesc("iOS");
            dayEntityArrayList.add(gankEntity_title02);

            for (int i = 0; i < iosEntityList.size(); i++) {
                DayEntity.ResultsEntity.IOSEntity entity = iosEntityList.get(i);
                gankEntity_other = new GankEntity();
                gankEntity_other.set_id(entity.get_id());
                gankEntity_other.setDesc(entity.getDesc());
                gankEntity_other.setType(entity.getType());
                gankEntity_other.setCreatedAt(entity.getCreatedAt());
                gankEntity_other.setPublishedAt(entity.getPublishedAt());
                gankEntity_other.setUrl(entity.getUrl());
                dayEntityArrayList.add(gankEntity_other);
            }
        }

        List<DayEntity.ResultsEntity.休息视频Entity> 休息视频EntityList = dayEntity.getResults().get休息视频();
        if (休息视频EntityList != null && 休息视频EntityList.size() > 0) {
            GankEntity gankEntity_title03 = new GankEntity();
            gankEntity_title03.setType("title");
            gankEntity_title03.setDesc("休息视频");
            dayEntityArrayList.add(gankEntity_title03);

            for (int i = 0; i < 休息视频EntityList.size(); i++) {
                DayEntity.ResultsEntity.休息视频Entity entity = 休息视频EntityList.get(i);
                gankEntity_other = new GankEntity();
                gankEntity_other.set_id(entity.get_id());
                gankEntity_other.setDesc(entity.getDesc());
                gankEntity_other.setType(entity.getType());
                gankEntity_other.setCreatedAt(entity.getCreatedAt());
                gankEntity_other.setPublishedAt(entity.getPublishedAt());
                gankEntity_other.setUrl(entity.getUrl());
                dayEntityArrayList.add(gankEntity_other);
            }
        }


        List<DayEntity.ResultsEntity.拓展资源Entity> 拓展资源EntityList = dayEntity.getResults().get拓展资源();
        if (拓展资源EntityList != null && 拓展资源EntityList.size() > 0) {
            GankEntity gankEntity_title04 = new GankEntity();
            gankEntity_title04.setType("title");
            gankEntity_title04.setDesc("拓展资源");
            dayEntityArrayList.add(gankEntity_title04);

            for (int i = 0; i < 拓展资源EntityList.size(); i++) {
                DayEntity.ResultsEntity.拓展资源Entity entity = 拓展资源EntityList.get(i);
                gankEntity_other = new GankEntity();
                gankEntity_other.set_id(entity.get_id());
                gankEntity_other.setDesc(entity.getDesc());
                gankEntity_other.setType(entity.getType());
                gankEntity_other.setCreatedAt(entity.getCreatedAt());
                gankEntity_other.setPublishedAt(entity.getPublishedAt());
                gankEntity_other.setUrl(entity.getUrl());
                dayEntityArrayList.add(gankEntity_other);
            }
        }


        KLog.i("dayEntityArrayList---" + dayEntityArrayList.size());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initAdapter();
            }
        });
    }

    private ArrayList<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_show);
        //设置状态栏的颜色
        StatusBarCompat.setStatusBarColor(DayShowActivity.this, StatusBarCompat.COLOR_DEFAULT_TRANSLATE);
        ButterKnife.bind(this);

        initIntent();

        initBar();

        initView();

        //切割
        String[] dayArray = dayDate.split("-");
        if (dayArray.length > 2) {
            GankApi.getOneDayData(dayArray[0], dayArray[1], dayArray[2], 0x001, httpCallBack);
        }

    }

    private void initImageView(String imageUrl) {
        progressbar.setVisibility(View.GONE);
        Glide
                .with(this)
                .load(imageUrl)
                .fitCenter()
                .into(ivTop);
        //添加到集合
        images = new ArrayList<>();
        images.add(imageUrl);
    }

    @OnClick(R.id.iv_top)
    void iv_top() {
        if (images != null && images.size() > 0) {
            IntentUtils.startToImageShow(this, images, 0);
        }
    }

    private void initView() {
        FullyLinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myRecycleView.setLayoutManager(linearLayoutManager);
        myRecycleView.setItemAnimator(new DefaultItemAnimator());
        //设置图片的最大高度
        ivTop.setMaxHeight((int) (DensityUtil.getHeight(this) * 0.75));
    }

    private void initAdapter() {
        RecycleDayAdapter recycleDayAdapter = new RecycleDayAdapter(this, dayEntityArrayList);
        myRecycleView.setAdapter(recycleDayAdapter);
        myRecycleView.setNestedScrollingEnabled(false);
        myRecycleView.setHasFixedSize(true);
        recycleDayAdapter.setOnItemClickLitener(new RecycleDayAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!dayEntityArrayList.get(position).getType().equals("title")) {
                    IntentUtils.startToWebActivity(DayShowActivity.this, dayEntityArrayList.get(position).getType(), dayEntityArrayList.get(position).getDesc(), dayEntityArrayList.get(position).getUrl());
                }
            }
        });
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

}
