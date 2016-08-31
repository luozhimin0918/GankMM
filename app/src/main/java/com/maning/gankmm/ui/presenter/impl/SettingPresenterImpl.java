package com.maning.gankmm.ui.presenter.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IWxCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.bean.AppUpdateInfo;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.http.MyCallBack;
import com.maning.gankmm.skin.SkinManager;
import com.maning.gankmm.ui.activity.SettingActivity;
import com.maning.gankmm.ui.iView.ISettingView;
import com.maning.gankmm.ui.presenter.ISettingPresenter;
import com.maning.gankmm.utils.FileUtils;
import com.maning.gankmm.utils.NetUtils;
import com.maning.gankmm.utils.SharePreUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by maning on 16/6/21.
 */
public class SettingPresenterImpl extends BasePresenterImpl<ISettingView> implements ISettingPresenter {

    private Context context;
    private long lastTime = System.currentTimeMillis();


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

    public SettingPresenterImpl(Context context, ISettingView iSettingView) {
        this.context = context;
        attachView(iSettingView);
    }

    @Override
    public void initPushState() {
        boolean jpush = SharePreUtil.getBooleanData(context, Constants.SPJpush, true);
        if (jpush) {
            JPushInterface.resumePush(context.getApplicationContext());
            if (mView == null) {
                return;
            }
            mView.openPush();
        } else {
            JPushInterface.stopPush(context.getApplicationContext());
            if (mView == null) {
                return;
            }
            mView.closePush();
        }
    }

    @Override
    public void initNightModeState() {
        if (mView == null) {
            return;
        }
        int currentSkinType = SkinManager.getCurrentSkinType(context);
        if (SkinManager.THEME_DAY == currentSkinType) {
            mView.closeNightMode();
        } else {
            mView.openNightMode();
        }
    }

    @Override
    public void clickNightMode() {
        if (mView == null) {
            return;
        }
        //不可快速点击，设定1秒内不能连续点击
        long currtTime = System.currentTimeMillis();
        if (currtTime - lastTime < 1000) {
            mView.showToast("你的手速太快了...");
            lastTime = currtTime;
            return;
        }
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
        if (mView == null) {
            return;
        }
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
                        if (mView == null) {
                            return;
                        }
                        mView.showBasesProgressSuccess("清除完毕");
                        initCache();
                    }
                });
            }
        }).start();

    }

    @Override
    public void initFeedBack() {
        setFeedBackState();
        FeedbackAPI.getFeedbackUnreadCount(context, "", new IWxCallback() {
            @Override
            public void onSuccess(final Object... result) {
                if (result != null && result.length == 1 && result[0] instanceof Integer) {
                    int count = (Integer) result[0];
                    if (count > 0) {
                        SharePreUtil.saveBooleanData(context, Constants.SPFeedback, true);
                        setFeedBackState();
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
    public void initAppUpdateState() {
        boolean isUpdate = SharePreUtil.getBooleanData(context, Constants.SPAppUpdate + MyApplication.getVersionCode(), false);
        if(mView != null){
            mView.setAppUpdateState(isUpdate);
        }
    }

    @Override
    public void checkAppUpdate() {
        //版本更新检查
        if (NetUtils.hasNetWorkConection(context)) {
            GankApi.getAppUpdateInfo(0x001, httpCallBack);
        } else {
            if (mView != null) {
                mView.showToast(context.getString(R.string.mm_no_net));
            }
        }
    }

    private void setFeedBackState() {
        boolean feedback = SharePreUtil.getBooleanData(context, Constants.SPFeedback, false);
        if (mView != null) {
            mView.setFeedbackState(feedback);
        }
    }


    class GetDiskCacheSizeTask extends AsyncTask<File, Long, Long> {
        private static final String TAG = "GetDiskCacheSizeTask";

        @Override
        protected void onPreExecute() {
            if (mView != null) {
                mView.setCacheSize("计算中...");
            }
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
            if (mView != null) {
                mView.setCacheSize(sizeText);
            }
        }

    }

}