package com.maning.gankmm.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.maning.gankmm.ui.activity.GankActivity;
import com.maning.gankmm.ui.activity.MainActivity;
import com.maning.gankmm.ui.activity.WebActivity;
import com.maning.gankmm.utils.IntentUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    //推送的字段
    public static final String PUSH_KEY_TYPE = "type";  //类型
    //三种类型
    public static final String PUSH_KEY_URL = "url";    //地址
    public static final String PUSH_KEY_TITLE = "title";    //地址 + title
    public static final String PUSH_KEY_DATE = "date";  //时间
    public static final String PUSH_KEY_TEXT = "text";  //文字展示

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            if (bundle != null) {
                //取消通知
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                JPushInterface.clearNotificationById(this.context, notifactionId);
                //其他
                String result = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if (!TextUtils.isEmpty(result)) {
                    //解析Json串，获取动作
                    openNotification(result);
                } else {
                    startMainActivity();
                }
            } else {
                startMainActivity();
            }

        }
    }

    private void openNotification(String result) {
        try {
            //解析推送
            JSONObject extrasJson = new JSONObject(result);
            String url = extrasJson.optString(PUSH_KEY_URL);
            String type = extrasJson.optString(PUSH_KEY_TYPE);
            String text = extrasJson.optString(PUSH_KEY_TEXT);
            String date = extrasJson.optString(PUSH_KEY_DATE);
            String title = extrasJson.optString(PUSH_KEY_TITLE);
            Log.d(TAG, "推动字段-----url：" + url + "---title：" + title + "---type：" + type + "---text：" + text + "---date：" + date);
            //判断具体的打开什么页面
            openPage(type, url, title, text, date);
        } catch (Exception e) {
            Log.d(TAG, "推送解析出错");
        }
    }

    private void openPage(String type, String url, String title, String text, String date) {
        if (!TextUtils.isEmpty(url)) {
            startWebActivity(url, title);
            return;
        }
        if (!TextUtils.isEmpty(date)) {
            startDayShowActivity(date);
            return;
        }
        if (!TextUtils.isEmpty(text)) {
            startMainActivity(text);
        }
    }

    private void startMainActivity() {
        //打开自定义的Activity
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    private void startMainActivity(String message) {
        //打开自定义的Activity
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(IntentUtils.PushMessage, message);
        context.startActivity(i);
    }

    private void startWebActivity(String url, String title) {
        //打开自定义的Activity
        Intent webIntent = new Intent(context, WebActivity.class);
        webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        webIntent.putExtra(IntentUtils.WebUrl, url);
        webIntent.putExtra(IntentUtils.WebTitleFlag, "推送");
        webIntent.putExtra(IntentUtils.WebTitle, title);
        context.startActivity(webIntent);
    }

    private void startDayShowActivity(String date) {
        Intent intent = new Intent(context.getApplicationContext(), GankActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(IntentUtils.DayDate, date);
        context.startActivity(intent);
    }


    //--------------------------------华丽分割线--------------------------------------------

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

}

