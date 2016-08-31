package com.maning.gankmm.ui.iView;

/**
 * Created by maning on 16/6/21.
 */
public interface ISettingView extends IBaseView {

    void openPush();

    void closePush();

    void openNightMode();

    void closeNightMode();

    void recreateActivity();

    void setCacheSize(String cacheSize);

    void setFeedbackState(boolean flag);

    void setUmengUpdateState(boolean flag);

    void showBasesProgressSuccess(String msg);

    void showToast(String msg);

}
