package com.maning.gankmm.ui.presenter.impl;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.db.PublicDao;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.http.MyCallBack;
import com.maning.gankmm.ui.iView.IPublicView;
import com.maning.gankmm.ui.iView.IWelFareView;
import com.maning.gankmm.ui.presenter.IPublicPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maning on 16/6/21.
 */
public class PublicPresenterImpl extends BasePresenterImpl<IPublicView> implements IPublicPresenter {

    private Context context;
    //标记来自哪个标签的
    private String flagFragment;

    private List<GankEntity> publicList;
    private int pageSize = 20;
    private int pageIndex = 1;

    public PublicPresenterImpl(Context context, IPublicView iPublicView, String flagFragment) {
        this.context = context;
        this.flagFragment = flagFragment;
        attachView(iPublicView);
    }

    private MyCallBack myCallBack = new MyCallBack() {

        @Override
        public void onSuccessList(int what, List results) {
            if (results == null) {
                mView.overRefresh();
                return;
            }
            switch (what) {
                case 0x001:
                    if (publicList == null) {
                        publicList = new ArrayList<>();
                    }
                    List<GankEntity> gankEntityList = results;
                    //过滤一下数据,筛除重的
                    if (publicList.size() > 0) {
                        for (int i = 0; i < results.size(); i++) {
                            GankEntity resultEntity2 = gankEntityList.get(i);
                            for (int j = 0; j < publicList.size(); j++) {
                                GankEntity resultsEntity1 = publicList.get(j);
                                if (resultEntity2.get_id().equals(resultsEntity1.get_id())) {
                                    //删除
                                    gankEntityList.remove(i);
                                }
                            }
                        }
                    }
                    publicList.addAll(gankEntityList);
                    if (publicList == null || publicList.size() == 0 || publicList.size() < pageIndex * pageSize) {
                        mView.setLoadMoreEnabled(false);
                    } else {
                        mView.setLoadMoreEnabled(true);
                    }
                    pageIndex++;
                    mView.setPublicList(publicList);
                    mView.overRefresh();
                    break;
                case 0x002: //下拉刷新
                    pageIndex = 1;
                    pageIndex++;
                    publicList = results;
                    //保存到数据库
                    saveToDB(publicList);
                    mView.setPublicList(publicList);
                    mView.overRefresh();
                    break;
            }
        }

        @Override
        public void onSuccess(int what, Object result) {

        }

        @Override
        public void onFail(int what, String result) {
            mView.overRefresh();
            if (!TextUtils.isEmpty(result)) {
                mView.showToast(result);
            }
        }
    };

    /**
     * 保存到数据库
     *
     * @param results
     */
    private void saveToDB(final List<GankEntity> results) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new PublicDao().insertList(results, flagFragment);
            }
        }).start();
    }


    @Override
    public void getNewDatas() {
        GankApi.getCommonDataNew(flagFragment, pageSize, 1, 0x002, myCallBack);
    }

    @Override
    public void getMoreDatas() {
        GankApi.getCommonDataNew(flagFragment, pageSize, pageIndex, 0x001, myCallBack);
    }

    @Override
    public void getDBDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取数据库的数据
                publicList = new PublicDao().queryAllCollectByType(flagFragment);
                MyApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (publicList != null && publicList.size() > 0) {
                            mView.setPublicList(publicList);
                        } else {
                            //自动刷新
                            mView.setRefreshing(true);
                        }
                    }
                });
            }
        }).start();
    }

}
