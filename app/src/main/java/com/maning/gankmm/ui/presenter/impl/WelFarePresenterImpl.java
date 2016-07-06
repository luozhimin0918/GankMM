package com.maning.gankmm.ui.presenter.impl;

import android.content.Context;
import android.text.TextUtils;

import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.http.MyCallBack;
import com.maning.gankmm.ui.iView.IWelFareView;
import com.maning.gankmm.ui.presenter.IWelFarePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maning on 16/6/21.
 */
public class WelFarePresenterImpl extends BasePresenterImpl<IWelFareView> implements IWelFarePresenter {

    private Context context;

    private List<GankEntity> welFareLists;
    private int pageSize = 20;
    private int pageIndex = 1;

    public WelFarePresenterImpl(Context context, IWelFareView iWelFareView) {
        this.context = context;
        attachView(iWelFareView);
    }

    private MyCallBack httpCallBack = new MyCallBack() {
        @Override
        public void onSuccessList(int what, List results) {
            if (results == null) {
                mView.overRefresh();
                return;
            }
            switch (what) {
                case 0x001:
                    if (welFareLists == null) {
                        welFareLists = new ArrayList<>();
                    }
                    if (pageIndex == 1 && welFareLists.size() > 0) {
                        welFareLists.clear();
                    }
                    welFareLists.addAll(results);
                    mView.setWelFareList(welFareLists);
                    if (welFareLists == null || welFareLists.size() == 0 || welFareLists.size() < pageIndex * pageSize) {
                        mView.setLoadMoreEnabled(false);
                    } else {
                        mView.setLoadMoreEnabled(true);
                    }
                    pageIndex++;
                    mView.overRefresh();
                    break;
                case 0x002: //下拉刷新
                    pageIndex = 1;
                    pageIndex++;
                    welFareLists = results;
                    if (welFareLists.size() > 0) {
                        mView.setWelFareList(welFareLists);
                    }
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

    @Override
    public void getNewDatas() {
        GankApi.getCommonDataNew(Constants.FlagWelFare, pageSize, 1, 0x002, httpCallBack);
    }

    @Override
    public void getMoreDatas() {
        GankApi.getCommonDataNew(Constants.FlagWelFare, pageSize, pageIndex, 0x001, httpCallBack);
    }

    @Override
    public void detachView() {
        if (welFareLists != null) {
            welFareLists.clear();
        }
        super.detachView();
    }
}
