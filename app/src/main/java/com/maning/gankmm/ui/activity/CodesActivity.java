package com.maning.gankmm.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.maning.gankmm.R;
import com.maning.gankmm.bean.CategoryContentBean;
import com.maning.gankmm.bean.CategoryTitleBean;
import com.maning.gankmm.ui.adapter.RecycleCodesContentAdapter;
import com.maning.gankmm.ui.adapter.RecycleCodesTitleAdapter;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 泡在网上的日子的数据的抓取
 */
public class CodesActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycle_content)
    RecyclerView recycleContent;
    @Bind(R.id.recycle_menu)
    RecyclerView recycleMenu;
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    private static final String TAG = MainActivity.class.getSimpleName() + "----";
    private static final String baseUrl = "http://www.jcodecraeer.com";
    private String url = baseUrl + "/plus/list.php?tid=31";
    private String nextPageUrl = "";

    private ArrayList<CategoryTitleBean> titles = new ArrayList<>();
    private ArrayList<CategoryContentBean> codes = new ArrayList<>();

    private RecycleCodesContentAdapter recycleContentAdapter;
    private RecycleCodesTitleAdapter recycleTitleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codes);
        ButterKnife.bind(this);

        initToolBar(toolbar, "泡在网上的日子", R.drawable.ic_back);

        initViews();

        getDatas(url);

    }

    private void initViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        recycleMenu = (RecyclerView) findViewById(R.id.recycle_menu);
        recycleContent = (RecyclerView) findViewById(R.id.recycle_content);

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
    }

    private void initMenuAdapter() {
        if (recycleTitleAdapter == null) {
            recycleTitleAdapter = new RecycleCodesTitleAdapter(this, titles);
            recycleMenu.setAdapter(recycleTitleAdapter);
            recycleTitleAdapter.setOnItemClickLitener(new RecycleCodesTitleAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    drawerLayout.closeDrawers();
                    //清除内容
                    codes.clear();
                    initContentAdapter();
                    nextPageUrl = "";
                    //获取数据
                    getDatas(titles.get(position).getUrl());

                }
            });
        }
    }

    private void initContentAdapter() {
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
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                            if (!TextUtils.isEmpty(nextPageUrl)) {
                                getDatas(nextPageUrl);
                            } else {
                                MySnackbar.makeSnackBarBlack(toolbar, "没有更多数据了");
                            }
                        }
                    }
                }
            });
        } else {
            recycleContentAdapter.setDatas(codes);
        }

    }

    private void getDatas(final String loadUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(loadUrl).get();

                    if (titles.size() <= 0) {
                        Log.i(TAG, "----------------------categoryTitles-----------------------");
                        //分类
                        Elements categorys = doc.select("div.col-md-2").select("ul.slidebar-box").select("li.slidebar-category-one");
                        CategoryTitleBean categoryTitleBean;
                        for (Element element : categorys) {
                            String url = element.select("li.slidebar-category-one").select("a[href]").attr("href");
                            String title = element.select("li.slidebar-category-one").select("a[href]").text();
                            if (!TextUtils.isEmpty(url)) {
                                categoryTitleBean = new CategoryTitleBean();
                                categoryTitleBean.setUrl(baseUrl + url);
                                categoryTitleBean.setTitle(title);
                                titles.add(categoryTitleBean);
                                Log.e(TAG, "categoryTitleBean----" + categoryTitleBean.toString());
                            }
                        }
                    }

                    //获取页码
                    Elements elementsPage = doc.select("div.paginate-container").select("a[href]");
                    KLog.i("elementsPages----" + elementsPage.size());
                    for (Element element : elementsPage) {
                        String text = element.text();
                        if ("下一页".equals(text.trim())) {
                            String pageUrl = element.select("a[href]").attr("href");
                            if (!TextUtils.isEmpty(pageUrl)) {
                                nextPageUrl = baseUrl + pageUrl;
                                KLog.i("nextPageUrl----" + nextPageUrl);
                            }
                        }
                    }

                    KLog.i("----------------------contents-----------------------");

                    Elements elements = doc.select("li.codeli");
                    Log.i(TAG, "element----" + elements.size());
                    CategoryContentBean categoryContentBean;
                    for (int i = 0; i < elements.size(); i++) {
                        Element element = elements.get(i);
                        String url = element.select("div.codeli-photo").select("a[href]").attr("href");
                        String imageUrl = element.select("div.codeli-photo").select("img[src]").attr("src");
                        String title = element.select("h2.codeli-name").select("a[href]").text();
                        String description = element.select("p.codeli-description").text();
                        String type = element.select("div.otherinfo").select("a[href]").text();
                        String otherInfo = element.select("div.otherinfo").select("span").text();

                        if (!TextUtils.isEmpty(url)) {
                            categoryContentBean = new CategoryContentBean();
                            categoryContentBean.setUrl(baseUrl + url);
                            categoryContentBean.setTitle(title);
                            categoryContentBean.setImageUrl(baseUrl + imageUrl);
                            categoryContentBean.setDescription(description);
                            categoryContentBean.setType(type);
                            categoryContentBean.setOtherInfo(otherInfo);
                            codes.add(categoryContentBean);
                            KLog.i("categoryContentBean----" + categoryContentBean.toString());
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initMenuAdapter();
                            initContentAdapter();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    KLog.i("catch----" + e.toString());
                }
            }
        }).start();
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
}
