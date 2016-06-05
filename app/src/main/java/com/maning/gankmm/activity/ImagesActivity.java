package com.maning.gankmm.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.base.BaseActivity;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.utils.BitmapUtils;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.ShareUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

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
    private static MaterialDialog mMaterialDialogSaveImage;
    private static MaterialDialog mMaterialDialogHint;

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

        //展示提示
        initShowHint();

    }

    private void initShowHint() {
        boolean booleanData = ShareUtil.getBooleanData(MyApplication.getIntstance(), Constants.HasShowHint, false);
        if (!booleanData) {
            if (mMaterialDialogHint == null) {
                mMaterialDialogHint = new MaterialDialog(mContext);
                mMaterialDialogHint.setTitle(getString(R.string.gank_dialog_title_tishi));
                mMaterialDialogHint.setMessage(getString(R.string.gank_dialog_msg_savehint));
                mMaterialDialogHint.setPositiveButton(getString(R.string.gank_dialog_confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialogHint.dismiss();
                        ShareUtil.saveBooleanData(MyApplication.getIntstance(), Constants.HasShowHint, true);
                    }
                });
            }
            mMaterialDialogHint.show();
        }
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
        TouchImageAdapter touchImageAdapter = new TouchImageAdapter(mContext, mDatas);
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


    static class TouchImageAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<String> mDatas;
        private LayoutInflater layoutInflater;

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
        public View instantiateItem(ViewGroup container, int position) {
            final String imageUrl = mDatas.get(position);
            View inflate = layoutInflater.inflate(R.layout.item_show_image, container, false);
            final ImageView imageView = (ImageView) inflate.findViewById(R.id.imageView);
            Glide
                    .with(mContext)
                    .load(imageUrl)
                    .into(imageView);
            container.addView(inflate);

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (mMaterialDialogSaveImage == null) {
                        mMaterialDialogSaveImage = new MaterialDialog(mContext);
                        mMaterialDialogSaveImage.setTitle(mContext.getString(R.string.gank_dialog_title_tishi));
                        mMaterialDialogSaveImage.setMessage(mContext.getString(R.string.gank_dialog_msg_save_image));
                        mMaterialDialogSaveImage.setPositiveButton(mContext.getString(R.string.gank_dialog_confirm), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialogSaveImage.dismiss();
                                final Bitmap bitmap = ((GlideBitmapDrawable) imageView.getDrawable()).getBitmap();
                                //save Image
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final boolean saveBitmapToSD = BitmapUtils.saveBitmapToSD(bitmap, Constants.BasePath, System.currentTimeMillis() + ".jpg");
                                        MyApplication.getHandler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (saveBitmapToSD) {
                                                    Toast.makeText(mContext, "保存成功，保存目录：" + Constants.BasePath, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(mContext, R.string.gank_hint_save_pic_fail, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                        mMaterialDialogSaveImage.setNegativeButton(mContext.getString(R.string.gank_dialog_cancle), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialogSaveImage.dismiss();

                            }
                        });
                    }
                    mMaterialDialogSaveImage.show();


                    return true;
                }
            });


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
