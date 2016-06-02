package com.maning.gankmm.fragment.collect;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.maning.gankmm.R;
import com.maning.gankmm.adapter.RecycleCollectAdapter;
import com.maning.gankmm.base.BaseFragment;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.db.CollectDao;
import com.maning.gankmm.utils.IntentUtils;
import com.socks.library.KLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 收藏ViewPager的Fragment
 */
public class CollectPagerFragment extends BaseFragment implements OnRefreshListener {

    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;

    private String flag;
    private ArrayList<GankEntity> collects = new ArrayList<>();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


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

        initData();
    }

    public void initData() {
        collects = new CollectDao().queryAllCollectByType(flag);
        if (collects != null && collects.size() > 0) {
            KLog.i("排序前：" + collects.toString());
            //按时间排序
            Collections.sort(collects, new Comparator<GankEntity>() {
                @Override
                public int compare(GankEntity lhs, GankEntity rhs) {
                    try {
                        long l_time = sdf.parse(lhs.getCreatedAt().split("T")[0]).getTime();
                        long r_time = sdf.parse(rhs.getCreatedAt().split("T")[0]).getTime();
                        if (l_time < r_time) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
            KLog.i("排序后：" + collects.toString());
        }
        initAdapter();
    }

    private void initAdapter() {
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
                    IntentUtils.startToWebActivity(getActivity(), flag,resultsEntity.getDesc(), resultsEntity.getUrl());
                }
            }
        });

    }

    private void initRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        swipeTarget.setLayoutManager(linearLayoutManager);
        swipeTarget.setItemAnimator(new DefaultItemAnimator());
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        initData();
    }

}
