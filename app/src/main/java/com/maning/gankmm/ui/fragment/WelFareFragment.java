package com.maning.gankmm.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.maning.gankmm.R;
import com.maning.gankmm.ui.adapter.RecyclePicAdapter;
import com.maning.gankmm.ui.base.BaseFragment;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.http.MyCallBack;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 福利Fragment
 */
public class WelFareFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private MyCallBack httpCallBack = new MyCallBack() {
        @Override
        public void onSuccessList(int what, List results) {
            if (results == null) {
                overRefresh();
                dissmissProgressDialog();
                return;
            }
            switch (what) {
                case 0x001:
                    dissmissProgressDialog();
                    if (commonDataResults == null) {
                        commonDataResults = new ArrayList<>();
                    }
                    if (pageIndex == 1 && commonDataResults.size() > 0) {
                        commonDataResults.clear();
                    }
                    commonDataResults.addAll(results);
                    initRecycleView();
                    if (commonDataResults == null || commonDataResults.size() == 0 || commonDataResults.size() < pageIndex * pageSize) {
                        swipeToLoadLayout.setLoadMoreEnabled(false);
                    } else {
                        swipeToLoadLayout.setLoadMoreEnabled(true);
                    }
                    pageIndex++;
                    overRefresh();
                    break;
                case 0x002: //下拉刷新
                    pageIndex = 1;
                    pageIndex++;
                    commonDataResults = results;
                    if (commonDataResults.size() > 0) {
                        initRecycleView();
                    }
                    overRefresh();
                    break;
            }
        }

        @Override
        public void onSuccess(int what, Object result) {

        }

        @Override
        public void onFail(int what, String result) {
            dissmissProgressDialog();
            overRefresh();
            if (!TextUtils.isEmpty(result)) {
                MySnackbar.makeSnackBarRed(swipeTarget, result);
            }
        }
    };

    private List<GankEntity> commonDataResults;
    private RecyclePicAdapter recyclePicAdapter;
    private int pageSize = 20;
    private int pageIndex = 1;

    public static WelFareFragment newInstance() {
        return new WelFareFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wel_fare, container, false);
        ButterKnife.bind(this, view);

        initRefresh();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        },100);
    }

    private void initRecycleView() {
        if (recyclePicAdapter == null) {
            recyclePicAdapter = new RecyclePicAdapter(context, commonDataResults);
            swipeTarget.setAdapter(recyclePicAdapter);
            //点击事件
            recyclePicAdapter.setOnItemClickLitener(new RecyclePicAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (int i = 0; i < commonDataResults.size(); i++) {
                        arrayList.add(commonDataResults.get(i).getUrl());
                    }
                    IntentUtils.startToImageShow(context, arrayList, position);
                }
            });

        } else {
            recyclePicAdapter.updateDatas(commonDataResults);
        }

    }

    private void initRefresh() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        swipeTarget.setLayoutManager(staggeredGridLayoutManager);
        swipeTarget.setItemAnimator(new DefaultItemAnimator());
        //到底自动刷新
//        swipeTarget.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
//                        swipeToLoadLayout.setLoadingMore(true);
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        GankApi.getCommonDataNew(Constants.FlagWelFare, pageSize, 1, 0x002, httpCallBack);
    }

    private void overRefresh() {
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void onLoadMore() {
        GankApi.getCommonDataNew(Constants.FlagWelFare, pageSize, pageIndex, 0x001, httpCallBack);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WelFareFragment");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WelFareFragment");
    }
}
