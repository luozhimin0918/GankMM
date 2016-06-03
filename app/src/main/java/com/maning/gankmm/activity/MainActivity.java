package com.maning.gankmm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.maning.gankmm.R;
import com.maning.gankmm.base.BaseActivity;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.fragment.CategoryFragment;
import com.maning.gankmm.fragment.TimeFragment;
import com.maning.gankmm.fragment.WelFareFragment;
import com.maning.gankmm.fragment.collect.CollectFragment;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.ShareUtil;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import com.umeng.update.UmengUpdateAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends BaseActivity {

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
    private TimeFragment timeFragment;

    private long exitTime = 0;
    private FeedbackAgent umengAgent;
    private MaterialDialog mMaterialDialog;
    private MaterialDialog mMaterialDialogPush;
    private MaterialDialog mMaterialDialogShare;
    private View inflateShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = this;

        initToolBar(toolbar, Constants.FlagWelFare, R.drawable.icon_menu2);

        initNavigationView();

        setDefaultFragment();

        initFeedbackDialog();

        //umeng
        initUmeng();

        initIntent();

    }

    private void initIntent() {
        Intent intent = getIntent();
        String pushMessage = intent.getStringExtra(IntentUtils.PushMessage);
        if (!TextUtils.isEmpty(pushMessage)) {
            mMaterialDialogPush = new MaterialDialog(this);
            mMaterialDialogPush.setTitle("通知");
            mMaterialDialogPush.setMessage(pushMessage);
            mMaterialDialogPush.setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialogPush.dismiss();
                }
            });
            mMaterialDialogPush.show();
        }
    }

    private void initFeedbackDialog() {
        mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("通知");
        mMaterialDialog.setMessage("您的反馈有回复了，是否去查看？");
        mMaterialDialog.setPositiveButton("查看", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                //自定义意见反馈
                startActivity(new Intent(context, FeedBackActivity.class));
            }
        });
        mMaterialDialog.setNegativeButton("等一会去", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();

            }
        });
    }

    private void initUmeng() {
        UmengUpdateAgent.update(this);
        initUmengFeedback();
    }

    private void initUmengFeedback() {
        umengAgent = new FeedbackAgent(this);
        Conversation defaultConversation = umengAgent.getDefaultConversation();
        defaultConversation.sync(new SyncListener() {
            @Override
            public void onReceiveDevReply(List<Reply> list) {
                if (list != null && list.size() > 0) {
                    ShareUtil.saveBooleanData(context, "feedback", true);
                    if (mMaterialDialog != null) {
                        mMaterialDialog.show();
                    }
                }
            }

            @Override
            public void onSendUserReply(List<Reply> list) {

            }
        });
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
        setMenuSelection(0);
    }

    private void setMenuSelection(int flag) {
        // 开启一个Fragment事务
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(fragmentTransaction);
        switch (flag) {
            case 0:
                if (welFareFragment == null) {
                    welFareFragment = WelFareFragment.newInstance();
                    fragmentTransaction.add(R.id.frame_content, welFareFragment);
                } else {
                    fragmentTransaction.show(welFareFragment);
                }
                break;
            case 1:
                if (collectFragment == null) {
                    collectFragment = CollectFragment.newInstance();
                    fragmentTransaction.add(R.id.frame_content, collectFragment);
                } else {
                    fragmentTransaction.show(collectFragment);
                }
                break;
            case 9:
                if (categoryFragment == null) {
                    categoryFragment = CategoryFragment.newInstance();
                    fragmentTransaction.add(R.id.frame_content, categoryFragment);
                } else {
                    fragmentTransaction.show(categoryFragment);
                }
                break;
            case 2:
                if (timeFragment == null) {
                    timeFragment = timeFragment.newInstance();
                    fragmentTransaction.add(R.id.frame_content, timeFragment);
                } else {
                    fragmentTransaction.show(timeFragment);
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
                menuItem.setChecked(true); // 改变item选中状态
                setTitle(menuItem.getTitle()); // 改变页面标题，标明导航状态
                drawerLayout.closeDrawers(); // 关闭导航菜单
                switch (menuItem.getItemId()) {
                    case R.id.nav_collect:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(1);
                        break;
                    case R.id.nav_fuli:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(0);
                        break;
                    case R.id.nav_category:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(9);
                        break;
                    case R.id.nav_time:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(2);
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
                        break;
                    case R.id.share_app:
                        menuItem.setChecked(false); // 改变item选中状态
                        //弹出二维码
                        if (inflateShare == null) {
                            inflateShare = LayoutInflater.from(MainActivity.this).inflate(R.layout.gank_share, null, false);
                        }
                        if (mMaterialDialogShare == null) {
                            mMaterialDialogShare = new MaterialDialog(MainActivity.this).setView(inflateShare);
                            mMaterialDialogShare.setBackgroundResource(R.drawable.translate_bg);
                            mMaterialDialogShare.setCanceledOnTouchOutside(true);
                        }
                        mMaterialDialogShare.show();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            KLog.i("菜单没有关闭");
            drawerLayout.closeDrawers();
            return;
        }
        //福利作为首页
        if (welFareFragment.isHidden()) {
            toolbar.setTitle("福利");
            setMenuSelection(0);
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_fuli).setChecked(true);
            return;
        }
        long currtTime = System.currentTimeMillis();
        if (currtTime - exitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
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
        super.onDestroy();
        umengAgent = null;
        mMaterialDialog = null;
        mMaterialDialogPush = null;
        mMaterialDialogShare = null;
        inflateShare = null;
    }
}
