package com.maning.gankmm.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.bean.CategoryContentBean;
import com.maning.gankmm.bean.CategoryTitleBean;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.skin.SkinManager;
import com.maning.gankmm.ui.adapter.RecycleCodesContentAdapter;
import com.maning.gankmm.ui.adapter.RecycleCodesTitleAdapter;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.ui.iView.ICodesView;
import com.maning.gankmm.ui.presenter.impl.CodesPresenterImpl;
import com.maning.gankmm.utils.DensityUtil;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.maning.gankmm.utils.NetUtils;
import com.maning.gankmm.utils.SharePreUtil;
import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 泡在网上的日子的数据的抓取
 */
public class CodesActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener, ICodesView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.swipe_target)
    RecyclerView recycleContent;
    @Bind(R.id.recycle_menu)
    RecyclerView recycleMenu;
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.iv_top_quick)
    ImageView ivTopQuick;

    private static final String baseUrl = "http://www.jcodecraeer.com";
    private String url = baseUrl + "/plus/list.php?tid=31";

    private ArrayList<CategoryTitleBean> titles = new ArrayList<>();
    private ArrayList<CategoryContentBean> codes = new ArrayList<>();

    private RecycleCodesContentAdapter recycleContentAdapter;
    private RecycleCodesTitleAdapter recycleTitleAdapter;

    private Animation animation_up;
    private Animation animation_down;

    private CodesPresenterImpl codesPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codes);
        ButterKnife.bind(this);

        int currentSkinType = SkinManager.getCurrentSkinType(this);
        if (SkinManager.THEME_DAY == currentSkinType) {
            initToolBar(toolbar, "泡在网上的日子", R.drawable.icon_arrow_back);
        } else {
            initToolBar(toolbar, "泡在网上的日子", R.drawable.icon_arrow_back_night);
        }

        initViews();

        initAnimation();

        codesPresenter = new CodesPresenterImpl(this, this);

        //加载数据
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        }, 100);

    }

    private void initAnimation() {
        animation_up = AnimationUtils.loadAnimation(this, R.anim.translate_up);
        animation_down = AnimationUtils.loadAnimation(this, R.anim.translate_down);
    }

    private void initViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        recycleMenu = (RecyclerView) findViewById(R.id.recycle_menu);
        recycleContent = (RecyclerView) findViewById(R.id.swipe_target);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycleMenu.setLayoutManager(linearLayoutManager);
        recycleMenu.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recycleMenu.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).build());

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycleContent.setLayoutManager(linearLayoutManager2);
        recycleContent.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recycleContent.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).build());
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(true);
    }

    private void initMenuAdapter() {
        if (recycleTitleAdapter == null) {
            recycleTitleAdapter = new RecycleCodesTitleAdapter(this, titles);
            recycleMenu.setAdapter(recycleTitleAdapter);
            recycleTitleAdapter.setOnItemClickLitener(new RecycleCodesTitleAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    drawerLayout.closeDrawers();
                    if (NetUtils.hasNetWorkConection(CodesActivity.this)) {
                        recycleTitleAdapter.setType(titles.get(position).getTitle());
                        url = titles.get(position).getUrl();
                        scrollToTop();
                        //加载数据
                        swipeToLoadLayout.setRefreshing(true);
                    } else {
                        showToast(getString(R.string.gank_net_fail));
                    }

                }
            });
            //默认第一次展开菜单
            boolean booleanData = SharePreUtil.getBooleanData(MyApplication.getIntstance(), Constants.SPCodesMenu, false);
            if (!booleanData) {
                SharePreUtil.saveBooleanData(MyApplication.getIntstance(), Constants.SPCodesMenu, true);
                drawerLayout.openDrawer(GravityCompat.END);
            }

        }
    }

    private void initContentAdapter() {
        overRefresh();
        if (recycleContentAdapter == null) {
            recycleContentAdapter = new RecycleCodesContentAdapter(this, codes);
            recycleContent.setAdapter(recycleContentAdapter);
            recycleContentAdapter.setOnItemClickLitener(new RecycleCodesContentAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    IntentUtils.startToWebActivity(CodesActivity.this, "codes", codes.get(position).getTitle(), codes.get(position).getUrl());
                }
            });
            recycleContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    boolean isSignificantDelta = Math.abs(dy) > DensityUtil.dip2px(CodesActivity.this, 4);
                    if (isSignificantDelta) {
                        if (dy > 0) {
                            onScrollUp();
                        } else {
                            onScrollDown();
                        }
                    }
                }
            });
        } else {
            recycleContentAdapter.setDatas(codes);
        }
    }

    private boolean dismissFlag = true;

    private void onScrollUp() {
        if (ivTopQuick.getVisibility() == View.VISIBLE) {
            if (!dismissFlag) {
                return;
            }
            dismissFlag = false;
            if (animation_down != null) {
                animation_down.cancel();
            }
            ivTopQuick.setVisibility(View.GONE);
            ivTopQuick.startAnimation(animation_down);
            MyApplication.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissFlag = true;
                }
            }, 500);
        }
    }

    private boolean flag = true;

    private void onScrollDown() {
        if (!(ivTopQuick.getVisibility() == View.VISIBLE)) {
            if (!flag) {
                return;
            }
            flag = false;
            if (animation_up != null) {
                animation_up.cancel();
            }
            ivTopQuick.setVisibility(View.VISIBLE);
            ivTopQuick.startAnimation(animation_up);
            MyApplication.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    flag = true;
                }
            }, 500);
        }
    }

    @OnClick(R.id.iv_top_quick)
    void iv_top_quick() {
        scrollToTop();
    }

    @OnClick(R.id.iv_right_menu)
    void iv_right_menu() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    public void scrollToTop() {
        recycleContent.scrollToPosition(0);
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
        codesPresenter.detachView();
        animation_up.cancel();
        animation_up = null;
        animation_down.cancel();
        animation_down = null;
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        //获取数据
        if (NetUtils.hasNetWorkConection(CodesActivity.this)) {
            codesPresenter.getNewDatas(url);
        } else {
            showToast(getString(R.string.gank_net_fail));
            overRefresh();
        }
    }

    @Override
    public void onLoadMore() {
        codesPresenter.getMoreDatas();
    }

    @Override
    public void setCodesTitleList(ArrayList<CategoryTitleBean> codesTitleList) {
        titles = codesTitleList;
        initMenuAdapter();
    }

    @Override
    public void setCodesContentList(ArrayList<CategoryContentBean> codesContentList) {
        codes = codesContentList;
        initContentAdapter();
    }

    @Override
    public void setRefreshEnabled(boolean flag) {
        swipeToLoadLayout.setRefreshEnabled(flag);
    }

    @Override
    public void setLoadMoreEnabled(boolean flag) {
        swipeToLoadLayout.setLoadMoreEnabled(flag);
    }

    @Override
    public void showToast(String msg) {
        MySnackbar.makeSnackBarRed(toolbar, msg);
    }

    @Override
    public void overRefresh() {
        if (swipeToLoadLayout == null) {
            return;
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }
}
