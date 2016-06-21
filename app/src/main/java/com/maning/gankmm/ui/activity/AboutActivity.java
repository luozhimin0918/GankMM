package com.maning.gankmm.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.utils.IntentUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        initToolBar(toolbar, getString(R.string.about), R.drawable.ic_back);
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

    @OnClick(R.id.tvThanks01)
    void tvThanks01() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_daimajia));
    }

    @OnClick(R.id.tvThanks02)
    void tvThanks02() {
        IntentUtils.startToWebActivity(this,null, getString(R.string.github), getString(R.string.github_other_app_01));
    }

    @OnClick(R.id.tvThanks03)
    void tvThanks03() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_other_app_02));
    }

    @OnClick(R.id.tvThanks04)
    void tvThanks04() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_other_app_03));
    }

    @OnClick(R.id.tvThanks05)
    void tvThanks05() {
        IntentUtils.startToWebActivity(this, null,getString(R.string.github), getString(R.string.github_other_app_04));
    }

    @OnClick(R.id.tvThanks06)
    void tvThanks06() {
        IntentUtils.startToWebActivity(this,null, getString(R.string.github), getString(R.string.github_other_app_05));
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
