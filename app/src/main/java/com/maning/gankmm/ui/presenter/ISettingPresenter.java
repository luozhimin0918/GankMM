package com.maning.gankmm.ui.presenter;

/**
 * Created by maning on 16/6/21.
 */
public interface ISettingPresenter {
    //初始化Push状态
    void initPushState();

    //改变Push状态
    void changePushState();

    //计算Cache大小
    void initCache();

    //清除缓存
    void cleanCache();

    //Umeng
    void initUmeng();

    //检查更新
    void checkAppUpdate();

}
