package com.maning.gankmm.ui.iView;

import com.maning.gankmm.bean.AppUpdateInfo;

/**
 * Created by maning on 16/6/21.
 */
public interface IMainView {

    void showFeedBackDialog();

    void showAppUpdateDialog(AppUpdateInfo appUpdateInfo);

}
