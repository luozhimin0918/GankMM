package com.maning.gankmm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.base.BaseActivity;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.utils.MyToast;
import com.maning.gankmm.utils.NetUtils;
import com.maning.gankmm.utils.SharePreUtil;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import me.drakeet.materialdialog.MaterialDialog;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_push)
    ImageView ivPush;
    @Bind(R.id.tv_cache)
    TextView tvCache;
    @Bind(R.id.iv_feedback_red)
    ImageView ivFeedbackRed;
    @Bind(R.id.iv_update_red)
    ImageView ivUpdateRed;

    FeedbackAgent umengAgent;
    private Context context;
    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        context = this;

        initToolBar(toolbar, "设置", R.drawable.ic_back);

        initPushState();

        initCache();

        initUmengFeedback();

        initUmengUpdate();

    }

    private void initUmengUpdate() {
        boolean umengUpdate = SharePreUtil.getBooleanData(context, Constants.SPAppUpdate + MyApplication.getVersionCode(), false);
        if (umengUpdate) {
            ivUpdateRed.setVisibility(View.VISIBLE);
        } else {
            ivUpdateRed.setVisibility(View.GONE);
        }
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateResponse) {
                switch (updateStatus) {
                    case UpdateStatus.Yes:
                        break;
                    case UpdateStatus.No:
                        MyToast.showShortToast("当前版本为最新版本");
                        break;
                    case UpdateStatus.Timeout:
                        KLog.i("Umeng更新-----超时");
                        break;
                }
            }
        });
    }

    private void initUmengFeedback() {
        umengAgent = new FeedbackAgent(this);
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
    }


    private void initUmengFeedBack() {
        boolean feedback = SharePreUtil.getBooleanData(this, Constants.SPFeedback, false);
        if (feedback) {
            ivFeedbackRed.setVisibility(View.VISIBLE);
        } else {
            ivFeedbackRed.setVisibility(View.GONE);
        }
    }

    private void initCache() {
        new GetDiskCacheSizeTask(tvCache).execute(new File(getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR));
    }

    private void initPushState() {

        boolean jpush = SharePreUtil.getBooleanData(this, Constants.SPJpush, true);
        if (jpush) {
            JPushInterface.resumePush(getApplicationContext());
            ivPush.setImageResource(R.drawable.icon_setting_on);
        } else {
            ivPush.setImageResource(R.drawable.icon_setting_off);
            JPushInterface.stopPush(getApplicationContext());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.rl_feedback)
    void rl_feedback() {
        //自定义意见反馈
        startActivity(new Intent(this, FeedBackActivity.class));
    }

    @OnClick(R.id.rl_clean_cache)
    void rl_clean_cache() {
        if (mMaterialDialog == null) {
            initCacheDialog();
        }
        mMaterialDialog.show();
    }

    @OnClick(R.id.rl_update)
    public void rl_update() {
        //版本更新检查
        if (NetUtils.hasNetWorkConection(context)) {
            //检测更新
            UmengUpdateAgent.update(this);
        } else {
            MyToast.showShortToast(getString(R.string.mm_no_net));
        }
    }


    private void initCacheDialog() {
        mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("缓存清理");
        mMaterialDialog.setMessage("确定要清除图片的缓存吗？");
        mMaterialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                cleanCache();
            }
        });
        mMaterialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();

            }
        });
    }

    private void cleanCache() {
        showProgressDialog("正在清理缓存...");
        //清楚硬盘缓存,需要后台线程清楚
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(MyApplication.getIntstance()).clearDiskCache();
                MyApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        //清除内存缓存
                        Glide.get(SettingActivity.this).clearMemory();
                        showProgressSuccess("清除完毕");
                        new GetDiskCacheSizeTask(tvCache).execute(new File(getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR));
                    }
                });
            }
        }).start();
    }

    @OnClick(R.id.iv_push)
    void iv_push() {
        boolean jpush = SharePreUtil.getBooleanData(this, Constants.SPJpush, true);
        if (!jpush) {
            SharePreUtil.saveBooleanData(this, Constants.SPJpush, true);
            JPushInterface.resumePush(getApplicationContext());
            ivPush.setImageResource(R.drawable.icon_setting_on);
        } else {
            SharePreUtil.saveBooleanData(this, Constants.SPJpush, false);
            ivPush.setImageResource(R.drawable.icon_setting_off);
            JPushInterface.stopPush(getApplicationContext());
        }
    }

    //----------------------------

    class GetDiskCacheSizeTask extends AsyncTask<File, Long, Long> {
        private final TextView resultView;

        public GetDiskCacheSizeTask(TextView resultView) {
            this.resultView = resultView;
        }

        @Override
        protected void onPreExecute() {
            resultView.setText("计算中...");
        }

        @Override
        protected void onProgressUpdate(Long... values) { /* onPostExecute(values[values.length - 1]); */ }

        @Override
        protected Long doInBackground(File... dirs) {
            try {
                long totalSize = 0;
                for (File dir : dirs) {
                    publishProgress(totalSize);
                    totalSize += calculateSize(dir);
                }
                return totalSize;
            } catch (RuntimeException ex) {
                final String message = String.format("Cannot get size of %s: %s", Arrays.toString(dirs), ex);
            }
            return 0L;
        }

        @Override
        protected void onPostExecute(Long size) {
            String sizeText = Formatter.formatFileSize(resultView.getContext(), size);
            resultView.setText(sizeText);
        }

    }

    private long calculateSize(File dir) {
        if (dir == null) return 0;
        if (!dir.isDirectory()) return dir.length();
        long result = 0;
        File[] children = dir.listFiles();
        if (children != null)
            for (File child : children)
                result += calculateSize(child);
        return result;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingActivity");
        MobclickAgent.onResume(this);          //统计时长
        initUmengFeedBack();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dissmissProgressDialog();
    }
}
