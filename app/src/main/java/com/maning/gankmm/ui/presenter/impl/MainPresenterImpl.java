package com.maning.gankmm.ui.presenter.impl;

import android.content.Context;

import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.ui.iView.IMainView;
import com.maning.gankmm.ui.presenter.IMainPresenter;
import com.maning.gankmm.utils.SharePreUtil;
import com.socks.library.KLog;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.List;

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
    public void initUmeng() {
        initUmengFeedback();
        initUmengUpdate();
    }

    private void initUmengFeedback() {
        FeedbackAgent umengAgent = new FeedbackAgent(context);
        Conversation defaultConversation = umengAgent.getDefaultConversation();
        defaultConversation.sync(new SyncListener() {
            @Override
            public void onReceiveDevReply(List<Reply> list) {
                if (list != null && list.size() > 0) {
                    SharePreUtil.saveBooleanData(context, Constants.SPFeedback, true);
                    mView.showFeedBackDialog();
                }
            }

            @Override
            public void onSendUserReply(List<Reply> list) {

            }
        });
    }

    private void initUmengUpdate() {
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
