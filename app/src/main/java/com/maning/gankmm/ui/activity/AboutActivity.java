package com.maning.gankmm.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.skin.SkinManager;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.utils.IntentUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class AboutActivity extends BaseActivity {

    private static final String TAG = AboutActivity.class.getSimpleName();
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.tv_app_version)
    TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        initBar();

        initAppVersionName();

    }

    private void initAppVersionName() {

        tvAppVersion.setText("当前版本号：" + MyApplication.getVersionName());

    }

    private void initBar() {
        int currentSkinType = SkinManager.getCurrentSkinType(this);
        if (SkinManager.THEME_DAY == currentSkinType) {
            initToolBar(toolbar, getString(R.string.about), R.drawable.icon_arrow_back);
            //设置CollapsingToolbarLayout扩张时的标题颜色
            collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.translate));
            //设置CollapsingToolbarLayout收缩时的标题颜色
            collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        } else {
            initToolBar(toolbar, getString(R.string.about), R.drawable.icon_arrow_back_night);
            //设置CollapsingToolbarLayout扩张时的标题颜色
            collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.translate));
            //设置CollapsingToolbarLayout收缩时的标题颜色
            collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.gank_text1_color_night));
        }
        collapsingToolbar.setTitle(getString(R.string.about));

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

    //点击事件
    @OnClick(R.id.tvdownload)
    void tvdownload() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.download_url)));
        startActivity(intent);
    }

    @OnClick(R.id.tvMyGithub)
    void tvMyGithub() {
        IntentUtils.startToWebActivity(this, null, getString(R.string.app_name), getString(R.string.github_my));
    }

    @OnClick(R.id.tvGank)
    void tvGank() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.gankio), getString(R.string.gankio));
    }

    /*------------------感谢-------------------*/
    @OnClick(R.id.tv_github01)
    void tv_github01() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_01));
    }
    @OnClick(R.id.tv_github02)
    void tv_github02() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_02));
    }
    @OnClick(R.id.tv_github03)
    void tv_github03() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_03));
    }
    @OnClick(R.id.tv_github04)
    void tv_github04() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_04));
    }
    @OnClick(R.id.tv_github05)
    void tv_github05() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_05));
    }
    @OnClick(R.id.tv_github06)
    void tv_github06() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_06));
    }
    @OnClick(R.id.tv_github07)
    void tv_github07() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_07));
    }
    @OnClick(R.id.tv_github08)
    void tv_github08() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_08));
    }
    @OnClick(R.id.tv_github09)
    void tv_github09() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_09));
    }
    @OnClick(R.id.tv_github10)
    void tv_github10() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_10));
    }
    @OnClick(R.id.tv_github11)
    void tv_github11() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_11));
    }
    @OnClick(R.id.tv_github12)
    void tv_github12() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_12));
    }
    @OnClick(R.id.tv_github13)
    void tv_github13() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_13));
    }
    @OnClick(R.id.tv_github14)
    void tv_github14() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_14));
    }
    @OnClick(R.id.tv_github15)
    void tv_github15() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_15));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);       //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onPause(this);
    }

}
