package com.maning.gankmm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.maning.gankmm.R;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.skin.SkinManager;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.ui.iView.ISettingView;
import com.maning.gankmm.ui.presenter.impl.SettingPresenterImpl;
import com.maning.gankmm.ui.view.MySettingItemView;
import com.maning.gankmm.utils.DialogUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.maning.gankmm.utils.SharePreUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

public class SettingActivity extends BaseActivity implements ISettingView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_push)
    ImageView ivPush;
    @Bind(R.id.iv_night_mode)
    ImageView ivNightMode;

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

        int currentSkinType = SkinManager.getCurrentSkinType(this);
        if (SkinManager.THEME_DAY == currentSkinType) {
            initToolBar(toolbar, "设置", R.drawable.icon_arrow_back);
        } else {
            initToolBar(toolbar, "设置", R.drawable.icon_arrow_back_night);
        }

        initPresenter();

        settingPresenter.initPushState();

        settingPresenter.initNightModeState();

        settingPresenter.initCache();

        settingPresenter.initUmeng();

        settingPresenter.initFeedBack();

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
        //保存状态
        SharePreUtil.saveBooleanData(this, Constants.SPFeedback, false);
        setFeedbackState(false);
        //阿里百川意见反馈
        //可以设置UI自定义参数，如主题色等,map的key值具体为：
        //enableAudio(是否开启语音 1：开启 0：关闭)
        //bgColor(消息气泡背景色 "#ffffff")，
        //color(消息内容文字颜色 "#ffffff")，
        //avatar(当前登录账号的头像)，string，为http url
        //toAvatar(客服账号的头像),string，为http url
        //themeColor(标题栏自定义颜色 "#ffffff")
        //profilePlaceholder: (顶部联系方式)，string
        //profileTitle: （顶部联系方式左侧提示内容）, String
        //chatInputPlaceholder: (输入框里面的内容),string
        //profileUpdateTitle:(更新联系方式标题), string
        //profileUpdateDesc:(更新联系方式文字描述), string
        //profileUpdatePlaceholder:(更新联系方式), string
        //profileUpdateCancelBtnText: (取消更新), string
        //profileUpdateConfirmBtnText: (确定更新),string
        //sendBtnText: (发消息),string
        //sendBtnTextColor: ("white"),string
        //sendBtnBgColor: ('red'),string
        //hideLoginSuccess: true  隐藏登录成功的toast
        //pageTitle: （Web容器标题）, string
        //photoFromCamera: (拍摄一张照片),String
        //photoFromAlbum: (从相册选取), String
        //voiceContent:(点击这里录制语音), String
        //voiceCancelContent: (滑到这里取消录音), String
        //voiceReleaseContent: (松开取消录音), String
        Map<String, String> customInfoMap = new HashMap<>();
        customInfoMap.put("themeColor","#54aee6");
        customInfoMap.put("pageTitle","意见反馈");
        FeedbackAPI.setUICustomInfo(customInfoMap);
        FeedbackAPI.openFeedbackActivity(this);

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
        mMaterialDialog = DialogUtils.initDialog(this, "缓存清理", "确定要清除图片的缓存吗？", "确定", "取消", new DialogUtils.OnDialogClickListener() {
            @Override
            public void onConfirm() {
                settingPresenter.cleanCache();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @OnClick(R.id.iv_push)
    void iv_push() {
        settingPresenter.changePushState();
    }

    @OnClick(R.id.iv_night_mode)
    void iv_night_mode() {
        settingPresenter.clickNightMode();
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
    public void openNightMode() {
        ivNightMode.setImageResource(R.drawable.icon_setting_on);
    }

    @Override
    public void closeNightMode() {
        ivNightMode.setImageResource(R.drawable.icon_setting_off);
    }

    @Override
    public void recreateActivity() {
        startActivity(new Intent(this.getApplicationContext(),SettingActivity.class));
        overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
        this.finish();
    }

    @Override
    public void setCacheSize(String cacheSize) {
        itemCleanCache.setRightText(cacheSize);
    }

    @Override
    public void setFeedbackState(final boolean flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemFeedback.setRedDot(flag);
            }
        });
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
        MySnackbar.makeSnackBarBlack(toolbar, msg);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingActivity");
        MobclickAgent.onResume(this);          //统计时长
        settingPresenter.initUmeng();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        settingPresenter.detachView();
        if (mMaterialDialog != null) {
            mMaterialDialog.dismiss();
            mMaterialDialog = null;
        }
        super.onDestroy();
    }
}
