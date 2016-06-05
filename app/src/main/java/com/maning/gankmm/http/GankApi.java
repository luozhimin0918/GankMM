package com.maning.gankmm.http;

import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.bean.DayEntity;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.bean.HttpResult;
import com.maning.gankmm.callback.MyCallBack;
import com.socks.library.KLog;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by maning on 16/3/2.
 * <p/>
 * 所有的网络请求
 */
public class GankApi {

    public final static String GET_DATA_FAIL = MyApplication.getIntstance().getString(R.string.gank_get_data_fail);
    public final static String NET_FAIL = MyApplication.getIntstance().getString(R.string.gank_net_fail);

    public static Call<HttpResult<List<GankEntity>>> getCommonDataNew(String type, int count, int pageIndex, final int what, final MyCallBack myCallBack) {
        Call<HttpResult<List<GankEntity>>> commonDateNew = BuildApi.getAPIService().getCommonDateNew(type, count, pageIndex);

        commonDateNew.enqueue(new Callback<HttpResult<List<GankEntity>>>() {
            @Override
            public void onResponse(Response<HttpResult<List<GankEntity>>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    HttpResult<List<GankEntity>> httpResult = response.body();
                    if (httpResult != null) {
                        if (!httpResult.isError()) {
                            List<GankEntity> gankEntityList = httpResult.getResults();
                            KLog.i("httpCallBack---gankEntityList：" + gankEntityList.toString());
                            myCallBack.onSuccessList(what, gankEntityList);
                        } else {
                            myCallBack.onFail(what, GET_DATA_FAIL);
                        }
                    } else {
                        myCallBack.onFail(what, GET_DATA_FAIL);
                    }
                } else {
                    myCallBack.onFail(what, GET_DATA_FAIL);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                KLog.i("httpCallBack-----onFailure：" + t.toString());
                //数据错误
                myCallBack.onFail(what, NET_FAIL);
            }
        });

        return commonDateNew;

    }

    public static Call<HttpResult<List<String>>> getHistoryData(final int what, final MyCallBack myCallBack) {

        Call<HttpResult<List<String>>> gankHistoryDate = BuildApi.getAPIService().getGankHistoryDate();

        gankHistoryDate.enqueue(new Callback<HttpResult<List<String>>>() {
            @Override
            public void onResponse(Response<HttpResult<List<String>>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    HttpResult<List<String>> httpResult = response.body();
                    if (httpResult != null) {
                        if (!httpResult.isError()) {
                            List<String> gankEntityList = httpResult.getResults();
                            KLog.i("httpCallBack---gankEntityList：" + gankEntityList.toString());
                            myCallBack.onSuccessList(what, gankEntityList);

                            //保存到缓存
                            MyApplication.getACache().put("HistoryTime", httpResult);

                        } else {
                            myCallBack.onFail(what, GET_DATA_FAIL);
                        }
                    } else {
                        myCallBack.onFail(what, GET_DATA_FAIL);
                    }
                } else {
                    myCallBack.onFail(what, GET_DATA_FAIL);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                KLog.i("httpCallBack-----onFailure：" + t.toString());
                //数据错误
                myCallBack.onFail(what, NET_FAIL);
            }
        });

        return gankHistoryDate;

    }

    /**
     * 获取一天的数据
     *
     * @param year
     * @param month
     * @param day
     * @param what
     * @param myCallBack
     * @return
     */
    public static Call<DayEntity> getOneDayData(String year, String month, String day, final int what, final MyCallBack myCallBack) {

        Call<DayEntity> oneDayData = BuildApi.getAPIService().getOneDayData(year, month, day);
        oneDayData.enqueue(new Callback<DayEntity>() {
            @Override
            public void onResponse(Response<DayEntity> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    DayEntity body = response.body();
                    if (body != null) {
                        if (!body.isError()) {
                            KLog.i("httpCallBack---gankEntityList：" + body.toString());
                            myCallBack.onSuccess(what, body);
                        } else {
                            myCallBack.onFail(what, GET_DATA_FAIL);
                        }
                    } else {
                        myCallBack.onFail(what, GET_DATA_FAIL);
                    }
                } else {
                    myCallBack.onFail(what, GET_DATA_FAIL);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                KLog.i("httpCallBack-----onFailure：" + t.toString());
                //数据错误
                myCallBack.onFail(what, NET_FAIL);
            }
        });

        return oneDayData;
    }

}
