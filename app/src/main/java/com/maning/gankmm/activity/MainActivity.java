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
import com.maning.gankmm.fragment.collect.CollectFragment;
import com.maning.gankmm.fragment.PublicFragment;
import com.maning.gankmm.fragment.WelFareFragment;
import com.maning.gankmm.utils.IntentUtils;
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

public class MainActivity extends BaseActivity {

    @Bind(R.id.navigationView)
    NavigationView navigationView;
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private Context context;
    private WelFareFragment welFareFragment;
    private PublicFragment androidFragment;
    private PublicFragment iOSFragment;
    private PublicFragment videoFragment;
    private PublicFragment jsFragment;
    private PublicFragment expandFragment;
    private PublicFragment recommendFragment;
    private PublicFragment appFragment;

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
            case 2:
                if (androidFragment == null) {
                    androidFragment = PublicFragment.newInstance(toolbar.getTitle().toString());
                    fragmentTransaction.add(R.id.frame_content, androidFragment);
                } else {
                    fragmentTransaction.show(androidFragment);
                }
                break;
            case 3:
                if (iOSFragment == null) {
                    iOSFragment = PublicFragment.newInstance(toolbar.getTitle().toString());
                    fragmentTransaction.add(R.id.frame_content, iOSFragment);
                } else {
                    fragmentTransaction.show(iOSFragment);
                }
                break;
            case 4:
                if (videoFragment == null) {
                    videoFragment = PublicFragment.newInstance(toolbar.getTitle().toString());
                    fragmentTransaction.add(R.id.frame_content, videoFragment);
                } else {
                    fragmentTransaction.show(videoFragment);
                }
                break;
            case 5:
                if (jsFragment == null) {
                    jsFragment = PublicFragment.newInstance(toolbar.getTitle().toString());
                    fragmentTransaction.add(R.id.frame_content, jsFragment);
                } else {
                    fragmentTransaction.show(jsFragment);
                }
                break;
            case 6:
                if (expandFragment == null) {
                    expandFragment = PublicFragment.newInstance(toolbar.getTitle().toString());
                    fragmentTransaction.add(R.id.frame_content, expandFragment);
                } else {
                    fragmentTransaction.show(expandFragment);
                }
                break;
            case 7:
                if (recommendFragment == null) {
                    recommendFragment = PublicFragment.newInstance(toolbar.getTitle().toString());
                    fragmentTransaction.add(R.id.frame_content, recommendFragment);
                } else {
                    fragmentTransaction.show(recommendFragment);
                }
                break;
            case 8:
                if (appFragment == null) {
                    appFragment = PublicFragment.newInstance(toolbar.getTitle().toString());
                    fragmentTransaction.add(R.id.frame_content, appFragment);
                } else {
                    fragmentTransaction.show(appFragment);
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
        if (androidFragment != null) {
            transaction.hide(androidFragment);
        }
        if (iOSFragment != null) {
            transaction.hide(iOSFragment);
        }
        if (videoFragment != null) {
            transaction.hide(videoFragment);
        }
        if (expandFragment != null) {
            transaction.hide(expandFragment);
        }
        if (jsFragment != null) {
            transaction.hide(jsFragment);
        }
        if (recommendFragment != null) {
            transaction.hide(recommendFragment);
        }
        if (appFragment != null) {
            transaction.hide(appFragment);
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
                    case R.id.nav_android:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(2);
                        break;
                    case R.id.nav_ios:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(3);
                        break;
                    case R.id.nav_video:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(4);
                        break;
                    case R.id.nav_js:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(5);
                        break;
                    case R.id.nav_expand:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(6);
                        break;
                    case R.id.nav_recommend:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(7);
                        break;
                    case R.id.nav_app:
                        toolbar.setTitle(menuItem.getTitle());
                        setMenuSelection(8);
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
