package com.maning.gankmm.ui.presenter.impl;

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
        this.mView = null;
    }
}
