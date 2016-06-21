package com.maning.gankmm.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.maning.gankmm.R;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.ui.iView.IWebView;
import com.maning.gankmm.ui.presenter.impl.WebPresenterImpl;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.maning.gankmm.utils.NetUtils;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 展示网页的
 */
public class WebActivity extends BaseActivity implements IWebView {

    private static final String TAG = WebActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;

    //标题
    private String flagTitle;
    private String titleContent;
    private String url;

    private WebPresenterImpl webPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);

        webPresenter = new WebPresenterImpl(this, this);

        initIntent();

        String title;
        if (TextUtils.isEmpty(flagTitle)) {
            title = titleContent;
        } else {
            title = flagTitle + "+" + titleContent;
        }
        initToolBar(toolbar, title, R.drawable.ic_back);

        initWebView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_copy:
                webPresenter.copy(url);
                break;
            case R.id.action_open:
                webPresenter.openBrowser(url);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initIntent() {
        Intent intent = getIntent();
        flagTitle = intent.getStringExtra(IntentUtils.WebTitleFlag);
        titleContent = intent.getStringExtra(IntentUtils.WebTitle);
        url = intent.getStringExtra(IntentUtils.WebUrl);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        //设置WebView属性，能够执行Javascript脚本
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setDomStorageEnabled(true);
        ////////////////////////////////
        // 建议缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT,无网络时，使用LOAD_CACHE_ELSE_NETWORK
        if (NetUtils.hasNetWorkConection(this)) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);   // 根据cache-control决定是否从网络上取数据。
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);   //优先加载缓存
        }

        //////////////////////////////
        webView.loadUrl(url);
        //设置了默认在本应用打开，不设置会用浏览器打开的
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                KLog.i("-----shouldOverrideUrlLoading");
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                KLog.i("-----onPageFinished");

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                KLog.i("-----onPageStarted");
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    KLog.i("-----onProgressChanged完成");
                    progressbar.setVisibility(View.GONE);
                } else {
                    // 加载中
                    KLog.i("-----onProgressChanged：" + newProgress);
                    if (progressbar.getVisibility() == View.GONE) {
                        progressbar.setVisibility(View.VISIBLE);
                    }
                    progressbar.setProgress(newProgress);
                }
            }
        });

        webView.setDownloadListener(new MyWebViewDownLoadListener());
    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //保证了webView退出后不再有声音
        webView.reload();
        //
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        webPresenter.detachView();
        webView.destroy();
        super.onDestroy();
    }


    @Override
    public void showToast(String msg) {
        MySnackbar.makeSnackBarBlue(toolbar, msg);
    }

    @Override
    public void showBaseProgressDialog(String msg) {
        showProgressDialog();
    }

    @Override
    public void hideBaseProgressDialog() {
        dissmissProgressDialog();
    }

}
