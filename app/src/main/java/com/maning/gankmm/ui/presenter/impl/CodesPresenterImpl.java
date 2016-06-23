package com.maning.gankmm.ui.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.maning.gankmm.bean.CategoryContentBean;
import com.maning.gankmm.bean.CategoryTitleBean;
import com.maning.gankmm.ui.iView.ICodesView;
import com.maning.gankmm.ui.presenter.ICodesPresenter;
import com.socks.library.KLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by maning on 16/6/23.
 */
public class CodesPresenterImpl extends BasePresenterImpl<ICodesView> implements ICodesPresenter {


    private static final String baseUrl = "http://www.jcodecraeer.com";

    private ArrayList<CategoryTitleBean> titles = new ArrayList<>();
    private ArrayList<CategoryContentBean> codes = new ArrayList<>();

    private Context context;
    private String nextPageUrl;

    public CodesPresenterImpl(Context context, ICodesView iCodesView) {
        this.context = context;
        attachView(iCodesView);
    }

    private void getDatas(final String loadUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(loadUrl).get();

                    if (titles.size() <= 0) {
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
                                KLog.i("----:" + categoryTitleBean.toString());
                            }
                        }
                    }

                    //获取页码
                    Elements elementsPage = doc.select("div.paginate-container").select("a[href]");
                    KLog.i("elementsPages----" + elementsPage.size());
                    nextPageUrl = "";
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

                    Elements elements = doc.select("li.codeli");
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

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.setCodesTitleList(titles);
                            mView.setCodesContentList(codes);
                            mView.overRefresh();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.showToast("解析数据异常");
                            mView.overRefresh();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void getNewDatas(String url) {
        codes.clear();
        mView.setRefreshEnabled(true);
        mView.setLoadMoreEnabled(true);
        getDatas(url);
    }

    @Override
    public void getMoreDatas() {
        if (!TextUtils.isEmpty(nextPageUrl)) {
            getDatas(nextPageUrl);
        } else {
            mView.showToast("没有更多数据了");
            mView.overRefresh();
            mView.setLoadMoreEnabled(false);
        }
    }
}
