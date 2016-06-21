package com.maning.gankmm.ui.presenter.impl;

import android.util.Log;

import com.maning.gankmm.ui.presenter.IBasePresenter;

/**
 * Created by maning on 16/6/21.
 */
public class BasePresenterImpl<T> implements IBasePresenter {

    public T mView;

    protected void attachView(T mView) {
        this.mView = mView;
    }

    @Override
    public void detachView() {
        Log.i("-----", mView.toString());
        this.mView = null;
    }
}
