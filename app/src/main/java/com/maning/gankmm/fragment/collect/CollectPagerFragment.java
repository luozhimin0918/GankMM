package com.maning.gankmm.fragment.collect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.maning.gankmm.R;
import com.maning.gankmm.adapter.CollectListViewAdapter;
import com.maning.gankmm.bean.PublicData;
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
public class CollectPagerFragment extends LazyFragment implements OnRefreshListener {

    @Bind(R.id.swipe_target)
    ListView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private String flag;
    private CollectListViewAdapter collectListViewAdapter;
    private ArrayList<PublicData.ResultsEntity> collects = new ArrayList<>();
    private boolean isPrepared;

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
        KLog.i(flag);

        isPrepared = true;
        lazyLoad();

        //因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && swipeTarget != null) {
            initData();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KLog.i(flag);
        initListView();
    }

    public void initData() {
        collects = new CollectDao().queryAllCollectByType(flag);
        if (collects != null && collects.size() > 0) {
            KLog.i("排序前：" + collects.toString());
            //按时间排序
            Collections.sort(collects, new Comparator<PublicData.ResultsEntity>() {
                @Override
                public int compare(PublicData.ResultsEntity lhs, PublicData.ResultsEntity rhs) {
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
        collectListViewAdapter = new CollectListViewAdapter(getActivity(), collects);
        swipeTarget.setAdapter(collectListViewAdapter);
    }

    private void initListView() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeTarget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PublicData.ResultsEntity resultsEntity = collects.get(position);
                if (Constants.FlagWelFare.equals(resultsEntity.getType())) {
                    swipeTarget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ArrayList<String> imageList = new ArrayList<>();
                            for (int i = 0; i < collects.size(); i++) {
                                imageList.add(collects.get(i).getUrl());
                            }
                            IntentUtils.startToImageShow(context, imageList, position);
                        }
                    });
                } else {
                    IntentUtils.startToWebActivity(getActivity(), flag, resultsEntity.getUrl());
                }
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
        initData();
    }

}
