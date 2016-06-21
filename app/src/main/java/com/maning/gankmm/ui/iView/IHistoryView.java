package com.maning.gankmm.ui.iView;

import java.util.List;

/**
 * Created by maning on 16/6/21.
 */
public interface IHistoryView extends IBaseView{

    void setHistoryList(List<String> historyList);

    void showToast(String msg);

    void overRefresh();

}
