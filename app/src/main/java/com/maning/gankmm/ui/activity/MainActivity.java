package com.maning.gankmm.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.maning.gankmm.R;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.skin.SkinBroadcastReceiver;
import com.maning.gankmm.skin.SkinManager;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.ui.fragment.CategoryFragment;
import com.maning.gankmm.ui.fragment.HistoryFragment;
import com.maning.gankmm.ui.fragment.WelFareFragment;
import com.maning.gankmm.ui.fragment.collect.CollectFragment;
import com.maning.gankmm.ui.iView.IMainView;
import com.maning.gankmm.ui.presenter.impl.MainPresenterImpl;
import com.maning.gankmm.utils.DialogUtils;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends BaseActivity implements IMainView {

    @Bind(R.id.navigationView)
    NavigationView navigationView;
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;


    private Context context;
    private WelFareFragment welFareFragment;
    private CollectFragment collectFragment;
    private CategoryFragment categoryFragment;
    private HistoryFragment timeFragment;

    private int navigationCheckedItemId = R.id.nav_fuli;
    private static final String savedInstanceStateStr = "navigationCheckedItemId";

    private long exitTime = 0;
    private MaterialDialog mMaterialDialogFeedBack;
    private MaterialDialog mMaterialDialogPush;

    private MainPresenterImpl mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = this;

        int currentSkinType = SkinManager.getCurrentSkinType(this);
        if (SkinManager.THEME_DAY == currentSkinType) {
            initToolBar(toolbar, Constants.FlagWelFare, R.drawable.icon_menu2);
        } else {
            initToolBar(toolbar, Constants.FlagWelFare, R.drawable.icon_menu2_night);
        }

        initNavigationView();

        mainPresenter = new MainPresenterImpl(this, this);
        mainPresenter.initUmeng();

        initIntent();

        if (savedInstanceState != null && savedInstanceState.getInt(savedInstanceStateStr) != 0) {
            navigationCheckedItemId = savedInstanceState.getInt(savedInstanceStateStr);
        }

        setDefaultFragment();

    }

    private void initIntent() {
        Intent intent = getIntent();
        String pushMessage = intent.getStringExtra(IntentUtils.PushMessage);
        if (!TextUtils.isEmpty(pushMessage)) {
            mMaterialDialogPush = DialogUtils.initDialog(this, getString(R.string.gank_dialog_title_notify), pushMessage, getString(R.string.gank_dialog_confirm), "", null);
            mMaterialDialogPush.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置默认的Fragment显示：如果savedInstanceState不是空，证明activity被后台销毁重建了，之前有fragment，就不再创建了
     */
    private void setDefaultFragment() {
        setMenuSelection(navigationCheckedItemId);
    }

    private void setMenuSelection(int flag) {
        // 开启一个Fragment事务
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(fragmentTransaction);
        switch (flag) {
            case R.id.nav_fuli:
                if (welFareFragment == null) {
                    welFareFragment = WelFareFragment.newInstance();
                    fragmentTransaction.add(R.id.frame_content, welFareFragment);
                } else {
                    fragmentTransaction.show(welFareFragment);
                }
                break;
            case R.id.nav_history:
                if (timeFragment == null) {
                    timeFragment = timeFragment.newInstance();
                    fragmentTransaction.add(R.id.frame_content, timeFragment);
                } else {
                    fragmentTransaction.show(timeFragment);
                }
                break;
            case R.id.nav_category:
                if (categoryFragment == null) {
                    categoryFragment = CategoryFragment.newInstance();
                    fragmentTransaction.add(R.id.frame_content, categoryFragment);
                } else {
                    fragmentTransaction.show(categoryFragment);
                }
                break;
            case R.id.nav_collect:
                if (collectFragment == null) {
                    collectFragment = CollectFragment.newInstance();
                    fragmentTransaction.add(R.id.frame_content, collectFragment);
                } else {
                    fragmentTransaction.show(collectFragment);
                }
                break;

        }
        fragmentTransaction.commit();

    }

    private void hideFragments(FragmentTransaction transaction) {
        if (welFareFragment != null) {
            transaction.hide(welFareFragment);
        }
        if (collectFragment != null) {
            transaction.hide(collectFragment);
        }
        if (categoryFragment != null) {
            transaction.hide(categoryFragment);
        }
        if (timeFragment != null) {
            transaction.hide(timeFragment);
        }

    }


    /**
     * -----------------------------------------
     */
    private void initNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                setTitle(menuItem.getTitle()); // 改变页面标题，标明导航状态
                drawerLayout.closeDrawers(); // 关闭导航菜单
                switch (menuItem.getItemId()) {
                    case R.id.nav_fuli:
                    case R.id.nav_history:
                    case R.id.nav_category:
                    case R.id.nav_collect:
                        navigationCheckedItemId = menuItem.getItemId();
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(menuItem.getItemId());
                        break;
                    case R.id.nav_codes:
                        menuItem.setCheckable(false);
                        //泡在网上的日子
                        startActivity(new Intent(MainActivity.this, CodesActivity.class));
                        break;
                    case R.id.about:
                        menuItem.setChecked(false); // 改变item选中状态
                        //跳转
                        IntentUtils.startAboutActivity(context);
                        break;
                    case R.id.setting:
                        menuItem.setChecked(false); // 改变item选中状态
                        //跳转
                        IntentUtils.startSettingActivity(context);
                        //注册夜间模式广播
                        registerSkinReceiver();
                        break;
                    case R.id.share_app:
                        menuItem.setChecked(false); // 改变item选中状态
                        //分享
                        IntentUtils.startAppShareText(context, "干货营", "干货营Android客户端：" + getString(R.string.download_url));
                        break;
                }
                return true;
            }
        });
    }

    private SkinBroadcastReceiver skinBroadcastReceiver;

    private void registerSkinReceiver() {
        if (skinBroadcastReceiver == null) {
            skinBroadcastReceiver = new SkinBroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    recreate();
                }
            };
            SkinManager.registerSkinReceiver(MainActivity.this, skinBroadcastReceiver);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(savedInstanceStateStr, navigationCheckedItemId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
            return;
        }
        //福利作为首页
        if (welFareFragment.isHidden()) {
            toolbar.setTitle("福利");
            setMenuSelection(0);
            navigationView.setCheckedItem(R.id.nav_fuli);
            return;
        }
        long currtTime = System.currentTimeMillis();
        if (currtTime - exitTime > 2000) {
            MySnackbar.makeSnackBarBlack(toolbar, getString(R.string.gank_hint_exit_app));
            exitTime = currtTime;
            return;
        }
        finish();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        mainPresenter.detachView();
        if (mMaterialDialogFeedBack != null) {
            mMaterialDialogFeedBack.dismiss();
            mMaterialDialogFeedBack = null;
        }
        if (mMaterialDialogPush != null) {
            mMaterialDialogPush.dismiss();
            mMaterialDialogPush = null;
        }
        SkinManager.unregisterSkinReceiver(this, skinBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void showFeedBackDialog() {
        if (mMaterialDialogFeedBack == null) {
            mMaterialDialogFeedBack = DialogUtils.initDialog(this,
                    getString(R.string.gank_dialog_title_notify),
                    getString(R.string.gank_dialog_msg_feedback),
                    "查看", "等一会去", new DialogUtils.OnDialogClickListener() {
                        @Override
                        public void onConfirm() {
                            //自定义意见反馈
                            startActivity(new Intent(context, FeedBackActivity.class));
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        }
        mMaterialDialogFeedBack.show();
    }
}
