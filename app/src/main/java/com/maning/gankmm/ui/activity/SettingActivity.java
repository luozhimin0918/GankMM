package com.maning.gankmm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.maning.gankmm.R;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.ui.iView.ISettingView;
import com.maning.gankmm.ui.presenter.impl.SettingPresenterImpl;
import com.maning.gankmm.ui.view.MySettingItemView;
import com.maning.gankmm.utils.MySnackbar;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

public class SettingActivity extends BaseActivity implements ISettingView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_push)
    ImageView ivPush;

    @Bind(R.id.item_clean_cache)
    MySettingItemView itemCleanCache;
    @Bind(R.id.item_feedback)
    MySettingItemView itemFeedback;
    @Bind(R.id.item_app_update)
    MySettingItemView itemAppUpdate;

    private MaterialDialog mMaterialDialog;

    private SettingPresenterImpl settingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initToolBar(toolbar, "设置", R.drawable.ic_back);

        initPresenter();

        //初始化Push状态
        settingPresenter.initPushState();

        settingPresenter.initCache();

        settingPresenter.initUmeng();

    }

    public void initPresenter() {
        settingPresenter = new SettingPresenterImpl(this, this);
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

    @OnClick(R.id.item_feedback)
    void item_feedback() {
        //自定义意见反馈
        startActivity(new Intent(this, FeedBackActivity.class));
    }

    @OnClick(R.id.item_app_update)
    public void item_app_update() {
        settingPresenter.checkAppUpdate();
    }

    @OnClick(R.id.item_clean_cache)
    void item_clean_cache() {
        if (mMaterialDialog == null) {
            initCacheDialog();
        }
        mMaterialDialog.show();
    }


    private void initCacheDialog() {
        mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("缓存清理");
        mMaterialDialog.setMessage("确定要清除图片的缓存吗？");
        mMaterialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                settingPresenter.cleanCache();
            }
        });
        mMaterialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();

            }
        });
    }

    @OnClick(R.id.iv_push)
    void iv_push() {
        settingPresenter.changePushState();
    }

    @Override
    public void openPush() {
        ivPush.setImageResource(R.drawable.icon_setting_on);
    }

    @Override
    public void closePush() {
        ivPush.setImageResource(R.drawable.icon_setting_off);
    }

    @Override
    public void setCacheSize(String cacheSize) {
        itemCleanCache.setRightText(cacheSize);
    }

    @Override
    public void setUmengFeedbackState(boolean flag) {
        itemFeedback.setRedDot(flag);
    }

    @Override
    public void setUmengUpdateState(boolean flag) {
        itemAppUpdate.setRedDot(flag);
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
    public void showToast(String msg) {
        MySnackbar.makeSnackBarBlue(toolbar, msg);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingActivity");
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settingPresenter.detachView();
    }
}
