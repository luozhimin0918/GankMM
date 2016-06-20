package com.maning.gankmm.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.maning.gankmm.R;
import com.maning.gankmm.adapter.RecycleTimeAdapter;
import com.maning.gankmm.base.BaseFragment;
import com.maning.gankmm.callback.MyCallBack;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 历史时间
 */
public class TimeFragment extends BaseFragment implements OnRefreshListener {

    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private MyCallBack httpCallBack = new MyCallBack() {
        @Override
        public void onSuccessList(int what, List results) {
            timeList = results;
            initRecycleView();
        }

        @Override
        public void onSuccess(int what, Object result) {

        }

        @Override
        public void onFail(int what, String result) {
            dissmissProgressDialog();
            overRefresh();
            if (!TextUtils.isEmpty(result)) {
                MySnackbar.makeSnackBarRed(swipeTarget,result);
            }
        }
    };

    private List<String> timeList;

    public static TimeFragment newInstance() {
        return new TimeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time, container, false);
        ButterKnife.bind(this, view);

        initRefresh();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //自动刷新
        swipeToLoadLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        });
    }


    private void initRecycleView() {
        RecycleTimeAdapter recyclePicAdapter = new RecycleTimeAdapter(context, timeList);
        swipeTarget.setAdapter(recyclePicAdapter);
        //点击事件
        recyclePicAdapter.setOnItemClickLitener(new RecycleTimeAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                IntentUtils.startDayShowActivity(getActivity(), timeList.get(position));
            }
        });
        overRefresh();
    }

    private void initRefresh() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        swipeTarget.setLayoutManager(linearLayoutManager);
        swipeTarget.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        swipeTarget.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.LTGRAY).build());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        overRefresh();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {

        GankApi.getHistoryData(0x001, httpCallBack);

    }

    private void overRefresh() {
        swipeToLoadLayout.setRefreshing(false);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TimeFragment");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TimeFragment");
    }
}
