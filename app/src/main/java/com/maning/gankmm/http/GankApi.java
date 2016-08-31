package com.maning.gankmm.http;

import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.bean.AppUpdateInfo;
import com.maning.gankmm.bean.DayEntity;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.bean.HttpResult;
import com.maning.gankmm.bean.RandomEntity;
import com.socks.library.KLog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
            public void onResponse(Call<HttpResult<List<GankEntity>>> call, Response<HttpResult<List<GankEntity>>> response) {
                if (response.isSuccessful()) {
                    HttpResult<List<GankEntity>> httpResult = response.body();
                    if (httpResult != null) {
                        if (!httpResult.isError()) {
                            List<GankEntity> gankEntityList = httpResult.getResults();
                            KLog.i("getCommonDataNew---success：" + gankEntityList.toString());
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
            public void onFailure(Call<HttpResult<List<GankEntity>>> call, Throwable t) {
                KLog.i("getCommonDataNew-----onFailure：" + t.toString());
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
            public void onResponse(Call<HttpResult<List<String>>> call, Response<HttpResult<List<String>>> response) {
                if (response.isSuccessful()) {
                    HttpResult<List<String>> httpResult = response.body();
                    if (httpResult != null) {
                        if (!httpResult.isError()) {
                            List<String> gankEntityList = httpResult.getResults();
                            KLog.i("getHistoryData---success：" + gankEntityList.toString());
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
            public void onFailure(Call<HttpResult<List<String>>> call, Throwable t) {
                KLog.i("getHistoryData-----success：" + t.toString());
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
            public void onResponse(Call<DayEntity> call, Response<DayEntity> response) {
                if (response.isSuccessful()) {
                    DayEntity body = response.body();
                    if (body != null) {
                        if (!body.isError()) {
                            KLog.i("getOneDayData---success：" + body.toString());
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
            public void onFailure(Call<DayEntity> call, Throwable t) {
                KLog.i("getOneDayData-----onFailure：" + t.toString());
                //数据错误
                myCallBack.onFail(what, NET_FAIL);
            }
        });

        return oneDayData;
    }


    public static Call<RandomEntity> getRandomDatas(int count, final int what, final MyCallBack myCallBack) {

        Call<RandomEntity> randomDatasCall = BuildApi.getAPIService().getRandomDatas("Android", count);

        randomDatasCall.enqueue(new Callback<RandomEntity>() {
            @Override
            public void onResponse(Call<RandomEntity> call, Response<RandomEntity> response) {
                if (response.isSuccessful()) {
                    RandomEntity body = response.body();
                    if (body != null) {
                        if (!body.isError()) {
                            KLog.i("getRandomDatas---success：" + body.toString());
                            myCallBack.onSuccessList(what, body.getResults());
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
            public void onFailure(Call<RandomEntity> call, Throwable t) {
                KLog.i("getRandomDatas-----onFailure：" + t.toString());
                //数据错误
                myCallBack.onFail(what, NET_FAIL);
            }
        });

        return randomDatasCall;
    }


    public static Call<AppUpdateInfo> getAppUpdateInfo(final int what, final MyCallBack myCallBack) {

        Call<AppUpdateInfo> theLastAppInfoCall = BuildApi.getAPIService().getTheLastAppInfo();

        theLastAppInfoCall.enqueue(new Callback<AppUpdateInfo>() {
            @Override
            public void onResponse(Call<AppUpdateInfo> call, Response<AppUpdateInfo> response) {
                if (response.isSuccessful()) {
                    AppUpdateInfo body = response.body();
                    if (body != null) {
                        if (body.getName().equals("干货营")) {
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
            public void onFailure(Call<AppUpdateInfo> call, Throwable t) {
                KLog.i("getRandomDatas-----onFailure：" + t.toString());
                //数据错误
                myCallBack.onFail(what, NET_FAIL);
            }
        });

        return theLastAppInfoCall;
    }

}
