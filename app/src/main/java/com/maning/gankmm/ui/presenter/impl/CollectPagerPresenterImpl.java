package com.maning.gankmm.ui.presenter.impl;

import android.content.Context;

import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.db.CollectDao;
import com.maning.gankmm.ui.iView.ICollectPagerView;
import com.maning.gankmm.ui.presenter.ICollectPagerPresenter;
import com.socks.library.KLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by maning on 16/6/21.
 */
public class CollectPagerPresenterImpl extends BasePresenterImpl<ICollectPagerView> implements ICollectPagerPresenter {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private Context context;

    public CollectPagerPresenterImpl(Context context, ICollectPagerView iCollectPagerView) {
        this.context = context;
        attachView(iCollectPagerView);
    }

    @Override
    public void getCollectLists(String flag) {
        ArrayList<GankEntity> collects = new CollectDao().queryAllCollectByType(flag);
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
        mView.setCollectList(collects);
        mView.overRefresh();
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
