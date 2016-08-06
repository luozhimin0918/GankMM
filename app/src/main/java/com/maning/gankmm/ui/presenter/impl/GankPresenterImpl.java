package com.maning.gankmm.ui.presenter.impl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.bean.DayEntity;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.http.MyCallBack;
import com.maning.gankmm.ui.iView.IGankView;
import com.maning.gankmm.ui.presenter.IGankPresenter;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maning on 16/6/21.
 */
public class GankPresenterImpl extends BasePresenterImpl<IGankView> implements IGankPresenter {

    private MyCallBack httpCallBack = new MyCallBack() {
        @Override
        public void onSuccessList(int what, List results) {

        }

        @Override
        public void onSuccess(int what, Object result) {
            final DayEntity dayEntity = (DayEntity) result;
            if (dayEntity != null) {
                String url = dayEntity.getResults().get福利().get(0).getUrl();
                mView.setProgressBarVisility(View.GONE);
                mView.showImageView(url);
                //初始化数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initDatas(dayEntity);
                    }
                }).start();
            }
        }

        @Override
        public void onFail(int what, String result) {
            mView.hideBaseProgressDialog();
            if (!TextUtils.isEmpty(result)) {
                mView.showToast(result);
            }
        }
    };

    private Context context;

    public GankPresenterImpl(Context context, IGankView iGankView) {
        this.context = context;
        attachView(iGankView);
    }

    @Override
    public void getOneDayDatas(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) {
            return;
        }
        //切割
        String[] dayArray = timeStr.split("-");
        if (dayArray.length > 2) {
            GankApi.getOneDayData(dayArray[0], dayArray[1], dayArray[2], 0x001, httpCallBack);
        }
    }


    private List<GankEntity> dayEntityArrayList = new ArrayList<>();

    private void initDatas(DayEntity dayEntity) {
        dayEntityArrayList.clear();

        GankEntity gankEntity_other;
        List<DayEntity.ResultsEntity.AndroidEntity> androidEntityList = dayEntity.getResults().getAndroid();
        if (androidEntityList != null && androidEntityList.size() > 0) {
            GankEntity gankEntity_title = new GankEntity();
            gankEntity_title.setType("title");
            gankEntity_title.setDesc("Android");
            dayEntityArrayList.add(gankEntity_title);
            for (int i = 0; i < androidEntityList.size(); i++) {
                DayEntity.ResultsEntity.AndroidEntity androidEntity = androidEntityList.get(i);
                gankEntity_other = new GankEntity();
                gankEntity_other.set_id(androidEntity.get_id());
                gankEntity_other.setDesc(androidEntity.getDesc());
                gankEntity_other.setType(androidEntity.getType());
                gankEntity_other.setCreatedAt(androidEntity.getCreatedAt());
                gankEntity_other.setPublishedAt(androidEntity.getPublishedAt());
                gankEntity_other.setUrl(androidEntity.getUrl());
                dayEntityArrayList.add(gankEntity_other);
            }
        }

        List<DayEntity.ResultsEntity.IOSEntity> iosEntityList = dayEntity.getResults().getIOS();
        if (iosEntityList != null && iosEntityList.size() > 0) {
            GankEntity gankEntity_title02 = new GankEntity();
            gankEntity_title02.setType("title");
            gankEntity_title02.setDesc("iOS");
            dayEntityArrayList.add(gankEntity_title02);

            for (int i = 0; i < iosEntityList.size(); i++) {
                DayEntity.ResultsEntity.IOSEntity entity = iosEntityList.get(i);
                gankEntity_other = new GankEntity();
                gankEntity_other.set_id(entity.get_id());
                gankEntity_other.setDesc(entity.getDesc());
                gankEntity_other.setType(entity.getType());
                gankEntity_other.setCreatedAt(entity.getCreatedAt());
                gankEntity_other.setPublishedAt(entity.getPublishedAt());
                gankEntity_other.setUrl(entity.getUrl());
                dayEntityArrayList.add(gankEntity_other);
            }
        }

        List<DayEntity.ResultsEntity.休息视频Entity> 休息视频EntityList = dayEntity.getResults().get休息视频();
        if (休息视频EntityList != null && 休息视频EntityList.size() > 0) {
            GankEntity gankEntity_title03 = new GankEntity();
            gankEntity_title03.setType("title");
            gankEntity_title03.setDesc("休息视频");
            dayEntityArrayList.add(gankEntity_title03);

            for (int i = 0; i < 休息视频EntityList.size(); i++) {
                DayEntity.ResultsEntity.休息视频Entity entity = 休息视频EntityList.get(i);
                gankEntity_other = new GankEntity();
                gankEntity_other.set_id(entity.get_id());
                gankEntity_other.setDesc(entity.getDesc());
                gankEntity_other.setType(entity.getType());
                gankEntity_other.setCreatedAt(entity.getCreatedAt());
                gankEntity_other.setPublishedAt(entity.getPublishedAt());
                gankEntity_other.setUrl(entity.getUrl());
                dayEntityArrayList.add(gankEntity_other);
            }
        }


        List<DayEntity.ResultsEntity.拓展资源Entity> 拓展资源EntityList = dayEntity.getResults().get拓展资源();
        if (拓展资源EntityList != null && 拓展资源EntityList.size() > 0) {
            GankEntity gankEntity_title04 = new GankEntity();
            gankEntity_title04.setType("title");
            gankEntity_title04.setDesc("拓展资源");
            dayEntityArrayList.add(gankEntity_title04);

            for (int i = 0; i < 拓展资源EntityList.size(); i++) {
                DayEntity.ResultsEntity.拓展资源Entity entity = 拓展资源EntityList.get(i);
                gankEntity_other = new GankEntity();
                gankEntity_other.set_id(entity.get_id());
                gankEntity_other.setDesc(entity.getDesc());
                gankEntity_other.setType(entity.getType());
                gankEntity_other.setCreatedAt(entity.getCreatedAt());
                gankEntity_other.setPublishedAt(entity.getPublishedAt());
                gankEntity_other.setUrl(entity.getUrl());
                dayEntityArrayList.add(gankEntity_other);
            }
        }

        KLog.i("dayEntityArrayList---" + dayEntityArrayList.size());
        MyApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mView.setGankList(dayEntityArrayList);
            }
        });
    }


    @Override
    public void detachView() {
        super.detachView();
    }

}
