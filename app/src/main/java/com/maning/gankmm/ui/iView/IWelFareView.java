package com.maning.gankmm.ui.iView;

import com.maning.gankmm.bean.GankEntity;

import java.util.List;

/**
 * Created by maning on 16/6/21.
 */
public interface IWelFareView {

    void setWelFareList(List<GankEntity> welFareList);

    void showToast(String msg);

    void overRefresh();

    void setLoadMoreEnabled(boolean flag);

}
