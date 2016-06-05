package com.maning.gankmm.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.base.BaseActivity;
import com.maning.gankmm.utils.NetUtils;
import com.maning.gankmm.utils.PrettyDate;
import com.maning.gankmm.utils.ShareUtil;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedBackActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FeedBackActivity.class.getSimpleName();
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.umeng_fb_send_content)
    EditText umengFbSendContent;
    @Bind(R.id.feedback_listview)
    ListView feedbackListview;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private Context mContext;

    private Conversation mConversation;
    private FeedbackAgent mAgent;

    private BaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);

        mContext = this;

        initToolBar(toolbar, getString(R.string.gank_page_title_feedback), R.drawable.ic_back);

        initUmeng();

        //保存
        ShareUtil.saveBooleanData(this, "feedback", false);

    }

    private void initUmeng() {
        //刷新控件
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        mAgent = new FeedbackAgent(this);
        mConversation = mAgent.getDefaultConversation();
        mAdapter = new FeedbackAdapter();
        feedbackListview.setAdapter(mAdapter);
        //滚动到
        feedbackListview.setSelection(mConversation.getReplyList().size());
        sync();

        feedbackListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 滚动停止
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            // 如果滚动到底部
                        } else if (view.getFirstVisiblePosition() == 0) {
                            // 滚动到顶部
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        // 开始滚动
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        // 正在滚动
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void scrollListViewToBottom() {
        feedbackListview.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                feedbackListview.setSelection(mAdapter.getCount() - 1);
            }
        });
    }

    private void sync() {

        mConversation.sync(new SyncListener() {
            @Override
            public void onSendUserReply(List<Reply> replyList) {
            }

            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
                // SwipeRefreshLayout停止刷新
                swipeRefresh.setRefreshing(false);
                KLog.e("onReceiveDevReply:" + replyList.toString());
                // 刷新ListView
                mAdapter.notifyDataSetChanged();
                scrollListViewToBottom();
            }
        });
    }

    @Override
    public void onRefresh() {
        MyApplication.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (feedbackListview.getCount() <= 1) {
                    swipeRefresh.setRefreshing(false);
                    return;
                }
                sync();
            }
        }, 500);
    }

    @OnClick(R.id.fd_send)
    void fd_send() {
        String content = umengFbSendContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, R.string.reply_no_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetUtils.hasNetWorkConection(mContext)) {
            Toast.makeText(this, R.string.mm_no_net, Toast.LENGTH_SHORT).show();
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
        umengFbSendContent.setText(null);
        mConversation.addUserReply(content);
        sync();
        scrollListViewToBottom();
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    class FeedbackAdapter extends BaseAdapter {

        private class ViewHolder {
            TextView replyContent;
            TextView replyDate;
            ImageView replyPic;
        }

        @Override
        public int getCount() {
            return mConversation.getReplyList().size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            Reply reply = mConversation.getReplyList().get(position - 1);
            if (reply.type.equals(Reply.TYPE_DEV_REPLY)) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                switch (getItemViewType(position)) {
                    case 0:
                        convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.feedback_item_dev, null);
                        break;
                    case 1:
                        convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.feedback_item_user, null);
                        break;
                    default:
                        convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.feedback_item_dev, null);
                        break;
                }
                holder.replyContent = (TextView) convertView.findViewById(R.id.umeng_fb_reply_content);
                holder.replyDate = (TextView) convertView.findViewById(R.id.umeng_fb_reply_date);
                holder.replyPic = (ImageView) convertView.findViewById(R.id.feedback_avatar);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                holder.replyContent.setText(getString(R.string.fb_reply_content_default));
                holder.replyPic.setImageResource(R.mipmap.ic_launcher);
                holder.replyDate.setText("");
            } else {
                Reply reply = mConversation.getReplyList().get(position - 1);
                holder.replyContent.setText(reply.content);
                holder.replyDate.setText(PrettyDate.getPresentDate(mContext.getApplicationContext(), reply.created_at));
            }
            if (position % 5 == 0) {
                holder.replyDate.setVisibility(View.VISIBLE);
            } else {
                holder.replyDate.setVisibility(View.GONE);
            }
            return convertView;
        }
    }


}
