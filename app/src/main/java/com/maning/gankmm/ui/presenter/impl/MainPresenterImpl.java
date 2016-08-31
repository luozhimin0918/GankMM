package com.maning.gankmm.ui.presenter.impl;

import android.content.Context;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IWxCallback;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.bean.AppUpdateInfo;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.http.MyCallBack;
import com.maning.gankmm.ui.iView.IMainView;
import com.maning.gankmm.ui.presenter.IMainPresenter;
import com.maning.gankmm.utils.NetUtils;
import com.maning.gankmm.utils.SharePreUtil;
import com.socks.library.KLog;

import java.util.List;

/**
 * Created by maning on 16/6/21.
 */
public class MainPresenterImpl extends BasePresenterImpl<IMainView> implements IMainPresenter {

    private Context context;

    private MyCallBack httpCallBack = new MyCallBack() {
        @Override
        public void onSuccessList(int what, List results) {

        }

        @Override
        public void onSuccess(int what, Object result) {
            if (mView == null) {
                return;
            }
            switch (what) {
                case 0x001:
                    if (result == null) {
                        return;
                    }
                    //获取当前APP的版本号
                    int newVersion;
                    AppUpdateInfo appUpdateInfo;
                    try{
                        appUpdateInfo = (AppUpdateInfo) result;
                        newVersion = Integer.parseInt(appUpdateInfo.getBuild());
                    }catch (Exception e){
                        newVersion = 1;
                        appUpdateInfo = new AppUpdateInfo();
                    }
                    if (MyApplication.getVersionCode() < newVersion) {
                        SharePreUtil.saveBooleanData(context, Constants.SPAppUpdate + MyApplication.getVersionCode(), true);
                        //需要版本更新
                        if(mView!=null){
                            mView.showAppUpdateDialog(appUpdateInfo);
                        }
                    }

                    break;
            }
        }

        @Override
        public void onFail(int what, String result) {
        }
    };


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
                    KLog.i("反馈：" + count);
                    if (count > 0) {
                        SharePreUtil.saveBooleanData(context, Constants.SPFeedback, true);
                        if (mView != null) {
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
        if(NetUtils.hasNetWorkConection(context)){
            //版本更新
            GankApi.getAppUpdateInfo(0x001, httpCallBack);
        }
    }
}
