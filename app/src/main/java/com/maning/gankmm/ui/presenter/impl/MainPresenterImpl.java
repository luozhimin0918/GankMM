package com.maning.gankmm.ui.presenter.impl;

import android.content.Context;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IWxCallback;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.ui.iView.IMainView;
import com.maning.gankmm.ui.presenter.IMainPresenter;
import com.maning.gankmm.utils.SharePreUtil;
import com.socks.library.KLog;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * Created by maning on 16/6/21.
 */
public class MainPresenterImpl extends BasePresenterImpl<IMainView> implements IMainPresenter {

    private Context context;

    public MainPresenterImpl(Context context, IMainView iMainView) {
        this.context = context;
        attachView(iMainView);
    }

    @Override
    public void initFeedBack() {
        FeedbackAPI.getFeedbackUnreadCount(context, "", new IWxCallback() {
            @Override
            public void onSuccess(final Object... result) {
                if (result != null && result.length == 1 && result[0] instanceof Integer) {
                    int count = (Integer) result[0];
                    KLog.i("反馈："+count);
                    if(count > 0){
                        SharePreUtil.saveBooleanData(context, Constants.SPFeedback, true);
                        if(mView != null){
                            mView.showFeedBackDialog();
                        }
                    }
                }
            }

            @Override
            public void onError(int code, String info) {

            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public void initAppUpdate() {
        UmengUpdateAgent.setDeltaUpdate(true);//增量更新，默认true
        UmengUpdateAgent.setUpdateAutoPopup(true);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateResponse) {
                switch (updateStatus) {
                    case UpdateStatus.Yes:
                        KLog.i("Umeng更新-----有新版本了---新版文件大小为：" + updateResponse.target_size + "---下载文件大小为：" + updateResponse.size);
                        SharePreUtil.saveBooleanData(context, Constants.SPAppUpdate + MyApplication.getVersionCode(), true);
                        break;
                    case UpdateStatus.No:
                        KLog.i("Umeng更新-----没有新版本");
                        SharePreUtil.saveBooleanData(context, Constants.SPAppUpdate + MyApplication.getVersionCode(), false);
                        break;
                    case UpdateStatus.Timeout:
                        KLog.i("Umeng更新-----超时");
                        break;
                }
            }
        });

        //对话框按键的监听，对于强制更新的版本，如果用户未选择更新的行为，关闭app
        UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
            @Override
            public void onClick(int status) {
                switch (status) {
                    case UpdateStatus.Update:
                        KLog.i("Umeng更新-----点击了更新");
                        SharePreUtil.saveBooleanData(context, Constants.SPAppUpdate + MyApplication.getVersionCode(), false);
                        break;
                    case UpdateStatus.Ignore:
                        KLog.i("Umeng更新-----点击了忽略");
                        break;
                    case UpdateStatus.NotNow:
                        KLog.i("Umeng更新-----点击了暂时不更新");
                        break;
                }
            }
        });

        //检测更新
        UmengUpdateAgent.update(context);
    }
}
