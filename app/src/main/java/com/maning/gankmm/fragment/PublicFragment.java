package com.maning.gankmm.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.maning.gankmm.R;
import com.maning.gankmm.adapter.RecyclePublicAdapter;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.base.BaseFragment;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.callback.MyCallBack;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.db.PublicDao;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.utils.IntentUtils;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 公用的Fragment：Android，ios，休息视频，前端，拓展资源，瞎推荐，App
 */
public class PublicFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private MyCallBack myCallBack = new MyCallBack() {

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
                    if (publicDataResults == null) {
                        publicDataResults = new ArrayList<>();
                    }
                    List<GankEntity> gankEntityList = results;
                    //过滤一下数据,筛除重的
                    if (publicDataResults.size() > 0) {
                        for (int i = 0; i < results.size(); i++) {
                            GankEntity resultEntity2 = gankEntityList.get(i);
                            for (int j = 0; j < publicDataResults.size(); j++) {
                                GankEntity resultsEntity1 = publicDataResults.get(j);
                                if (resultEntity2.get_id().equals(resultsEntity1.get_id())) {
                                    //删除
                                    gankEntityList.remove(i);
                                }
                            }
                        }
                    }
                    publicDataResults.addAll(gankEntityList);
                    initAdapter();
                    if (publicDataResults == null || publicDataResults.size() == 0 || publicDataResults.size() < pageIndex * pageSize) {
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
                    publicDataResults = results;
                    //保存到数据库
                    saveToDB(publicDataResults);
                    initAdapter();
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
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private RecyclePublicAdapter recyclePublicAdapter;

    /**
     * 保存到数据库
     *
     * @param results
     */
    private void saveToDB(final List<GankEntity> results) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new PublicDao().insertList(results, flagFragment);
            }
        }).start();
    }

    private List<GankEntity> publicDataResults;
    private int pageSize = 20;
    private int pageIndex = 1;
    //标记来自哪个标签的
    private String flagFragment;

    public static PublicFragment newInstance(String flag) {
        PublicFragment publicFragment = new PublicFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FlagFragment, flag);
        publicFragment.setArguments(args);
        return publicFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            flagFragment = getArguments().getString(Constants.FlagFragment);
            KLog.i("CommentFragment-----onCreate:" + flagFragment);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        KLog.i("PublicFragment-----onCreateView");
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        ButterKnife.bind(this, view);

        initRefresh();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KLog.i("PublicFragment-----onViewCreated");

        getDBDatas();

    }

    private void getDBDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取数据库的数据
                publicDataResults = new PublicDao().queryAllCollectByType(flagFragment);
                MyApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (publicDataResults != null && publicDataResults.size() > 0) {
                            initAdapter();
                        } else {
                            //自动刷新
                            swipeToLoadLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeToLoadLayout.setRefreshing(true);
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    private void initAdapter() {

        if (recyclePublicAdapter == null) {
            recyclePublicAdapter = new RecyclePublicAdapter(context, publicDataResults);
            swipeTarget.setAdapter(recyclePublicAdapter);
            //点击事件
            recyclePublicAdapter.setOnItemClickLitener(new RecyclePublicAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    GankEntity resultsEntity = publicDataResults.get(position);
                    IntentUtils.startToWebActivity(getActivity(), flagFragment, resultsEntity.getDesc(), resultsEntity.getUrl());
                }
            });

        } else {
            recyclePublicAdapter.updateDatas(publicDataResults);
        }

    }


    private void initRefresh() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        swipeTarget.setLayoutManager(linearLayoutManager);
        swipeTarget.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        overRefresh();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        GankApi.getCommonDataNew(flagFragment, pageSize, 1, 0x002, myCallBack);
    }

    @Override
    public void onLoadMore() {
        GankApi.getCommonDataNew(flagFragment, pageSize, pageIndex, 0x001, myCallBack);
    }

    private void overRefresh() {
        if(swipeToLoadLayout==null){
            return;
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

}
