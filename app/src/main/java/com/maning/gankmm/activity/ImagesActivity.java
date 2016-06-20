package com.maning.gankmm.activity;

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
import com.maning.gankmm.base.BaseActivity;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.utils.BitmapUtils;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.view.PinchImageView;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImagesActivity extends BaseActivity {

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
    private TouchImageAdapter touchImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        ButterKnife.bind(this);
        mContext = this;

        initToolBar(toolbar, getString(R.string.gank_page_title_girls), R.drawable.ic_back);

        initIntent();

        initData();

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
        touchImageAdapter = new TouchImageAdapter(mContext, mDatas);
        viewPager.setAdapter(touchImageAdapter);
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

    private void initData() {
        tvShowNum.setText((startIndex + 1) + "/" + mDatas.size());
    }

    @OnClick(R.id.btn_save)
    void btn_save() {
        //显示dialog
        showProgressDialog("正在保存...");
        final Bitmap bitmap = getCurrentImageViewBitmap();
        if (bitmap == null) {
            showProgressError(getResources().getString(R.string.gank_hint_save_pic_fail));
            return;
        }
        //save Image
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean saveBitmapToSD = BitmapUtils.saveBitmapToSD(bitmap, Constants.BasePath, System.currentTimeMillis() + ".jpg", true);
                MyApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (saveBitmapToSD) {
                            showProgressSuccess("保存成功，保存目录：" + Constants.BasePath);
                        } else {
                            showProgressError(getResources().getString(R.string.gank_hint_save_pic_fail));
                        }
                    }
                });
            }
        }).start();

    }

    @Nullable
    private Bitmap getCurrentImageViewBitmap() {
        View currentItem = touchImageAdapter.getPrimaryItem();
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
        showProgressDialog("正在设置壁纸...");

        final Bitmap bitmap = getCurrentImageViewBitmap();
        if (bitmap == null) {
            showProgressError("设置失败");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                WallpaperManager manager = WallpaperManager.getInstance(mContext);
                try {
                    manager.setBitmap(bitmap);
                    flag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    flag = false;
                } finally {
                    if (flag) {
                        MyApplication.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                showProgressSuccess("设置成功");
                            }
                        });
                    } else {
                        MyApplication.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                showProgressError("设置失败");
                            }
                        });
                    }
                }
            }
        }).start();
    }


    static class TouchImageAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<String> mDatas;
        private LayoutInflater layoutInflater;
        private View mCurrentView;

        public TouchImageAdapter(Context mContext, ArrayList<String> mDatas) {
            this.mContext = mContext;
            this.mDatas = mDatas;
            layoutInflater = LayoutInflater.from(this.mContext);
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentView = (View) object;
        }

        public View getPrimaryItem() {
            return mCurrentView;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final String imageUrl = mDatas.get(position);
            View inflate = layoutInflater.inflate(R.layout.item_show_image, container, false);
            final ImageView imageView = (ImageView) inflate.findViewById(R.id.imageView);
            Glide
                    .with(mContext)
                    .load(imageUrl)
                    .thumbnail(0.2f)
                    .into(imageView);
            container.addView(inflate);

            return inflate;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
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


    @Override
    protected void onDestroy() {
        //清空集合
        if (mDatas != null) {
            mDatas.clear();
            mDatas = null;
        }
        super.onDestroy();
    }
}
