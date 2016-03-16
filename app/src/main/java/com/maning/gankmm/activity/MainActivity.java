package com.maning.gankmm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.maning.gankmm.R;
import com.maning.gankmm.base.BaseActivity;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.fragment.CollectFragment;
import com.maning.gankmm.fragment.PublicFragment;
import com.maning.gankmm.fragment.WelFareFragment;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MyToast;
import com.maning.gankmm.utils.ShareUtil;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.List;
import java.util.logging.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @Bind(R.id.navigationView)
    NavigationView navigationView;
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private Context context;
    private WelFareFragment welFareFragment;
    private PublicFragment publicFragment;
    private long exitTime = 0;
    private CollectFragment collectFragment;
    private FeedbackAgent umengAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = this;

        initToolBar(toolbar, Constants.FlagWelFare, R.drawable.icon_menu);

        initNavigationView();

        setDefaultFragment();

        //umeng
        initUmeng();

    }

    private void initUmeng() {
        UmengUpdateAgent.update(this);
        umengAgent = new FeedbackAgent(this);
        Conversation defaultConversation = umengAgent.getDefaultConversation();
        defaultConversation.sync(new SyncListener() {
            @Override
            public void onReceiveDevReply(List<Reply> list) {
                KLog.i("Umeng反馈onSendUserReply");
                if (list != null && list.size() > 0) {
                    KLog.i("Umeng反馈用户回复了");
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
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    welFareFragment = WelFareFragment.newInstance();
                    fragmentTransaction.add(R.id.frame_content, welFareFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    fragmentTransaction.show(welFareFragment);
                }
                break;
            case 1:
                if (publicFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    publicFragment = PublicFragment.newInstance(toolbar.getTitle().toString());
                    fragmentTransaction.add(R.id.frame_content, publicFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    fragmentTransaction.show(publicFragment);
                    publicFragment.updateFlag(toolbar.getTitle().toString());
                }
                break;
            case 2:
                if (collectFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    collectFragment = CollectFragment.newInstance();
                    fragmentTransaction.add(R.id.frame_content, collectFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
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
        if (publicFragment != null) {
            transaction.hide(publicFragment);
        }
        if (collectFragment != null) {
            transaction.hide(collectFragment);
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
                        setMenuSelection(2);
                        break;
                    case R.id.nav_fuli:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(0);
                        break;
                    case R.id.nav_android:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(1);
                        break;
                    case R.id.nav_ios:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(1);
                        break;
                    case R.id.nav_video:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(1);
                        break;
                    case R.id.nav_js:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(1);
                        break;
                    case R.id.nav_expand:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(1);
                        break;
                    case R.id.nav_recommend:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(1);
                        break;
                    case R.id.nav_app:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(1);
                        break;
                    case R.id.about:
                        menuItem.setChecked(false); // 改变item选中状态
                        //跳转
                        IntentUtils.startAboutActivity(context);
                        break;
                    case R.id.feenBack:
                        menuItem.setChecked(false); // 改变item选中状态
//                        umengAgent.startFeedbackActivity();
                        //自定义意见反馈
                        startActivity(new Intent(MainActivity.this, FeedBackActivity.class));
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


}
