package com.maning.gankmm.ui.presenter.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.skin.SkinManager;
import com.maning.gankmm.ui.activity.SettingActivity;
import com.maning.gankmm.ui.iView.ISettingView;
import com.maning.gankmm.ui.presenter.ISettingPresenter;
import com.maning.gankmm.utils.FileUtils;
import com.maning.gankmm.utils.NetUtils;
import com.maning.gankmm.utils.SharePreUtil;
import com.socks.library.KLog;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by maning on 16/6/21.
 */
public class SettingPresenterImpl extends BasePresenterImpl<ISettingView> implements ISettingPresenter {

    private Context context;

    public SettingPresenterImpl(Context context, ISettingView iSettingView) {
        this.context = context;
        attachView(iSettingView);
    }

    @Override
    public void initPushState() {
        boolean jpush = SharePreUtil.getBooleanData(context, Constants.SPJpush, true);
        if (jpush) {
            JPushInterface.resumePush(context.getApplicationContext());
            mView.openPush();
        } else {
            JPushInterface.stopPush(context.getApplicationContext());
            mView.closePush();
        }
    }

    @Override
    public void initNightModeState() {
        int currentSkinType = SkinManager.getCurrentSkinType(context);
        if (SkinManager.THEME_DAY == currentSkinType) {
            mView.closeNightMode();
        } else {
            mView.openNightMode();
        }
    }

    @Override
    public void clickNightMode() {
        int currentSkinType = SkinManager.getCurrentSkinType(context);
        if (SkinManager.THEME_DAY == currentSkinType) {
            SkinManager.changeSkin((SettingActivity) context, SkinManager.THEME_NIGHT);
            mView.openNightMode();
        } else {
            SkinManager.changeSkin((SettingActivity) context, SkinManager.THEME_DAY);
            mView.closeNightMode();
        }
        mView.recreateActivity();
    }

    @Override
    public void changePushState() {
        boolean jpush = SharePreUtil.getBooleanData(context, Constants.SPJpush, true);
        if (!jpush) {
            SharePreUtil.saveBooleanData(context, Constants.SPJpush, true);
            JPushInterface.resumePush(context.getApplicationContext());
            mView.openPush();
        } else {
            SharePreUtil.saveBooleanData(context, Constants.SPJpush, false);
            mView.closePush();
            JPushInterface.stopPush(context.getApplicationContext());
        }
    }

    @Override
    public void initCache() {
        new GetDiskCacheSizeTask().execute(new File(context.getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR));
    }

    @Override
    public void cleanCache() {
        mView.showBaseProgressDialog("正在清理缓存...");
        //清楚硬盘缓存,需要后台线程清楚
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(MyApplication.getIntstance()).clearDiskCache();
                MyApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        //清除内存缓存
                        Glide.get(context).clearMemory();
                        mView.showBasesProgressSuccess("清除完毕");
                        initCache();
                    }
                });
            }
        }).start();

    }

    @Override
    public void initUmeng() {
        //修复点击设置--意见反馈闪退的问题(packageName与applicationId不一致导致的问题)
        //具体可以参考：http://bbs.umeng.com/thread-8071-1-1.html
        com.umeng.fb.util.Res.setPackageName(R.class.getPackage().getName());
        initUmengFeedBack();
        //Umeng 反馈
        FeedbackAgent umengAgent = new FeedbackAgent(context);
        Conversation defaultConversation = umengAgent.getDefaultConversation();
        defaultConversation.sync(new SyncListener() {
            @Override
            public void onReceiveDevReply(List<Reply> list) {
                if (list != null && list.size() > 0) {
                    SharePreUtil.saveBooleanData(context, Constants.SPFeedback, true);
                    initUmengFeedBack();
                }
            }

            @Override
            public void onSendUserReply(List<Reply> list) {

            }
        });

        //--------------------Umeng更新
        boolean umengUpdate = SharePreUtil.getBooleanData(context, Constants.SPAppUpdate + MyApplication.getVersionCode(), false);
        mView.setUmengUpdateState(umengUpdate);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateResponse) {
                switch (updateStatus) {
                    case UpdateStatus.Yes:
                        break;
                    case UpdateStatus.No:
                        if (mView != null) {
                            mView.showToast("当前版本为最新版本");
                        }
                        break;
                    case UpdateStatus.Timeout:
                        KLog.i("Umeng更新-----超时");
                        break;
                }
            }
        });

    }

    @Override
    public void checkAppUpdate() {
        //版本更新检查
        if (NetUtils.hasNetWorkConection(context)) {
            //检测更新
            UmengUpdateAgent.update(context);
        } else {
            mView.showToast(context.getString(R.string.mm_no_net));
        }
    }

    private void initUmengFeedBack() {
        boolean feedback = SharePreUtil.getBooleanData(context, Constants.SPFeedback, false);
        mView.setUmengFeedbackState(feedback);
    }


    class GetDiskCacheSizeTask extends AsyncTask<File, Long, Long> {
        private static final String TAG = "GetDiskCacheSizeTask";

        @Override
        protected void onPreExecute() {
            mView.setCacheSize("计算中...");
        }

        @Override
        protected void onProgressUpdate(Long... values) { /* onPostExecute(values[values.length - 1]); */ }

        @Override
        protected Long doInBackground(File... dirs) {
            try {
                long totalSize = 0;
                for (File dir : dirs) {
                    publishProgress(totalSize);
                    totalSize += FileUtils.calculateSize(dir);
                }
                return totalSize;
            } catch (RuntimeException ex) {
                final String message = String.format("Cannot get size of %s: %s", Arrays.toString(dirs), ex);
                Log.i(TAG, message);
            }
            return 0L;
        }

        @Override
        protected void onPostExecute(Long size) {
            String sizeText = Formatter.formatFileSize(context, size);
            mView.setCacheSize(sizeText);
        }

    }
}
