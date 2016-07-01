package com.maning.gankmm.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.gankmm.R;
import com.maning.gankmm.ui.adapter.ImagesAdapter;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.ui.iView.IImageView;
import com.maning.gankmm.ui.presenter.impl.ImagePresenterImpl;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class ImagesActivity extends BaseActivity implements IImageView, EasyPermissions.PermissionCallbacks{

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

    private static final int REQUECT_CODE_STORAGE = 2;  //保存图片申请权限Code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        ButterKnife.bind(this);
        mContext = this;

        initToolBar(toolbar, getString(R.string.gank_page_title_girls), R.drawable.ic_back);

        imagePresenter = new ImagePresenterImpl(this, this);

        initIntent();

        tvShowNum.setText(String.valueOf((startIndex + 1) + "/" + mDatas.size()));

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
                tvShowNum.setText(String.valueOf((position + 1) + "/" + mDatas.size()));
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
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            imagePresenter.saveImage();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "请求存储文件权限，用来保存图片到本地",
                    REQUECT_CODE_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
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

    @Override
    public Bitmap getCurrentImageViewBitmap() {
        return imageAdapter.getCurrentImageViewBitmap();
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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        MySnackbar.makeSnackBarBlack(toolbar,"权限申请成功");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        //当用户点击拒绝权限，并且再也不提示的时候，再次请求
        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this, "请求存储文件权限，用来保存图片到本地", R.string.setting, R.string.cancel, perms);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
