package com.maning.gankmm.ui.activity;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.ui.adapter.ImagesAdapter;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.ui.iView.IImageView;
import com.maning.gankmm.ui.presenter.impl.ImagePresenterImpl;
import com.maning.gankmm.utils.BitmapUtils;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.ui.view.PinchImageView;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImagesActivity extends BaseActivity implements IImageView {

    private static final String TAG = ImagesActivity.class.getSimpleName();
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tv_showNum)
    TextView tvShowNum;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private Context mContext;

    private ArrayList<String> mDatas = new ArrayList<>();
    private int startIndex;
    private ImagesAdapter imageAdapter;

    private ImagePresenterImpl imagePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        ButterKnife.bind(this);
        mContext = this;

        initToolBar(toolbar, getString(R.string.gank_page_title_girls), R.drawable.ic_back);

        imagePresenter = new ImagePresenterImpl(this, this);

        initIntent();

        tvShowNum.setText((startIndex + 1) + "/" + mDatas.size());

        //初始化ViewPager
        initViewPager();

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

    private void initViewPager() {
        imageAdapter = new ImagesAdapter(mContext, mDatas);
        viewPager.setAdapter(imageAdapter);
        if (startIndex > 0) {
            viewPager.setCurrentItem(startIndex);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvShowNum.setText((position + 1) + "/" + mDatas.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initIntent() {
        //获取传递的数据
        Intent intent = getIntent();
        mDatas = intent.getStringArrayListExtra(IntentUtils.ImageArrayList);
        startIndex = intent.getIntExtra(IntentUtils.ImagePositionForImageShow, 0);
    }

    @OnClick(R.id.btn_save)
    void btn_save() {
        imagePresenter.saveImage();
    }

    public Bitmap getCurrentImageViewBitmap() {
        View currentItem = imageAdapter.getPrimaryItem();
        if (currentItem == null) {
            KLog.i("btn_save----currentItem是空");
            return null;
        }
        PinchImageView imageView = (PinchImageView) currentItem.findViewById(R.id.imageView);
        if (imageView == null) {
            KLog.i("btn_save----imageView是空");
            return null;
        }
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    @OnClick(R.id.btn_wallpaper)
    void btn_wallpaper() {
        imagePresenter.setWallpaper();
    }


    @Override
    public void showBaseProgressDialog(String msg) {
        showProgressDialog(msg);
    }

    @Override
    public void hideBaseProgressDialog() {
        dissmissProgressDialog();
    }

    @Override
    public void showBasesProgressSuccess(String msg) {
        showProgressSuccess(msg);
    }

    @Override
    public void showBasesProgressError(String msg) {
        showProgressError(msg);
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


    @Override
    protected void onDestroy() {
        //清空集合
        if (mDatas != null) {
            mDatas.clear();
            mDatas = null;
        }
        imagePresenter.detachView();
        super.onDestroy();
    }
}
