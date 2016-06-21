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
                    if (commonDataResults == null) {
                        commonDataResults = new ArrayList<>();
                    }
                    if (pageIndex == 1 && commonDataResults.size() > 0) {
                        commonDataResults.clear();
                    }
                    commonDataResults.addAll(results);
                    mView.setWelFareList(commonDataResults);
                    if (commonDataResults == null || commonDataResults.size() == 0 || commonDataResults.size() < pageIndex * pageSize) {
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
                    commonDataResults = results;
                    if (commonDataResults.size() > 0) {
                        mView.setWelFareList(commonDataResults);
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

    private List<GankEntity> commonDataResults;
    private int pageSize = 20;
    private int pageIndex = 1;

    @Override
    public void getNewDatas() {
        GankApi.getCommonDataNew(Constants.FlagWelFare, pageSize, 1, 0x002, httpCallBack);
    }

    @Override
    public void getMoreDatas() {
        GankApi.getCommonDataNew(Constants.FlagWelFare, pageSize, pageIndex, 0x001, httpCallBack);
    }
}
