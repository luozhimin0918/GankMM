package com.maning.gankmm.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.db.PublicDao;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.adapter.PublicAdapter;
import com.maning.gankmm.base.BaseFragment;
import com.maning.gankmm.bean.PublicData;
import com.maning.gankmm.callback.MyCallBack;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.utils.MyToast;
import com.maning.gankmm.utils.NetUtils;
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
    ListView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private MyCallBack myCallBack = new MyCallBack() {
        @Override
        public void onSuccess(int what, Object result) {
            switch (what) {
                case 0x001:
                    dissmissProgressDialog();
                    if (publicDataResults == null) {
                        publicDataResults = new ArrayList<>();
                    }
                    PublicData publicData = (PublicData) result;
                    KLog.i("myCallBack：" + publicData.toString());
                    List<PublicData.ResultsEntity> results = publicData.getResults();
                    //过滤一下数据,筛除重的
                    if (publicDataResults != null && publicDataResults.size() > 0) {
                        for (int i = 0; i < results.size(); i++) {
                            PublicData.ResultsEntity resultEntity2 = results.get(i);
                            for (int j = 0; j < publicDataResults.size(); j++) {
                                PublicData.ResultsEntity resultsEntity1 = publicDataResults.get(j);
                                if (resultEntity2.get_id().equals(resultsEntity1.get_id())) {
                                    //删除
                                    results.remove(i);
                                }
                            }
                        }
                    }
                    if (results != null && results.size() > 0) {
                        publicDataResults.addAll(results);
                    }
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
                    PublicData publicDataNew = (PublicData) result;
                    KLog.i("myCallBack：" + publicDataNew.toString());
                    publicDataResults = publicDataNew.getResults();
                    //保存到数据库
                    saveToDB(publicDataResults);
                    initAdapter();
                    overRefresh();
                    break;
            }
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

    /**
     * 保存到数据库
     *
     * @param results
     */
    private void saveToDB(final List<PublicData.ResultsEntity> results) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new PublicDao().insertList(results, flagFragment);
            }
        }).start();
    }

    private List<PublicData.ResultsEntity> publicDataResults;
    private PublicAdapter publicAdapter;
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
        if (publicAdapter == null) {
            publicAdapter = new PublicAdapter(context, publicDataResults);
            swipeTarget.setAdapter(publicAdapter);
        } else {
            publicAdapter.updateList(publicDataResults);
        }
    }


    private void initRefresh() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

        swipeTarget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PublicData.ResultsEntity resultsEntity = publicDataResults.get(position);
                IntentUtils.startToWebActivity(getActivity(), flagFragment, resultsEntity.getUrl());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        GankApi.getCommonData(flagFragment, pageSize, 1, 0x002, myCallBack);
    }

    @Override
    public void onLoadMore() {
        GankApi.getCommonData(flagFragment, pageSize, pageIndex, 0x001, myCallBack);
    }

    private void overRefresh() {
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(flagFragment);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(flagFragment);
    }

}
