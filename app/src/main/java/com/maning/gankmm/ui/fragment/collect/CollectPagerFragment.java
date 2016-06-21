package com.maning.gankmm.ui.fragment.collect;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.maning.gankmm.R;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.ui.adapter.RecycleCollectAdapter;
import com.maning.gankmm.ui.base.BaseFragment;
import com.maning.gankmm.ui.iView.ICollectPagerView;
import com.maning.gankmm.ui.presenter.impl.CollectPagerPresenterImpl;
import com.maning.gankmm.utils.IntentUtils;
import com.socks.library.KLog;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 收藏ViewPager的Fragment
 */
public class CollectPagerFragment extends BaseFragment implements OnRefreshListener, ICollectPagerView {

    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private String flag;

    private CollectPagerPresenterImpl collectPagerPresenter;


    public static CollectPagerFragment newInstance(String flag) {
        CollectPagerFragment collectPagerFragment = new CollectPagerFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FlagFragment, flag);
        collectPagerFragment.setArguments(args);
        return collectPagerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            flag = getArguments().getString(Constants.FlagFragment);
            KLog.i(flag);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect_pager, container, false);
        ButterKnife.bind(this, view);
        initRecycleView();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        collectPagerPresenter = new CollectPagerPresenterImpl(getActivity(), this);

        collectPagerPresenter.getCollectLists(flag);

    }

    private void initAdapter(final ArrayList<GankEntity> collects) {
        RecycleCollectAdapter recycleCollectAdapter = new RecycleCollectAdapter(context, collects);
        swipeTarget.setAdapter(recycleCollectAdapter);
        //点击事件
        recycleCollectAdapter.setOnItemClickLitener(new RecycleCollectAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                GankEntity resultsEntity = collects.get(position);
                if (Constants.FlagWelFare.equals(resultsEntity.getType())) {
                    ArrayList<String> imageList = new ArrayList<>();
                    for (int i = 0; i < collects.size(); i++) {
                        imageList.add(collects.get(i).getUrl());
                    }
                    IntentUtils.startToImageShow(context, imageList, position);

                } else {
                    IntentUtils.startToWebActivity(getActivity(), flag, resultsEntity.getDesc(), resultsEntity.getUrl());
                }
            }
        });

        overRefresh();

    }

    private void initRecycleView() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setRefreshEnabled(true);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        swipeTarget.setLayoutManager(linearLayoutManager);
        swipeTarget.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public void onDestroyView() {
        collectPagerPresenter.detachView();
        overRefresh();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        collectPagerPresenter.getCollectLists(flag);
    }

    @Override
    public void overRefresh() {
        if (swipeToLoadLayout == null) {
            return;
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }

    @Override
    public void setCollectList(ArrayList<GankEntity> collectList) {
        initAdapter(collectList);
    }
}
