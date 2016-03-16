package com.maning.gankmm.http;

import com.maning.gankmm.bean.PublicData;
import com.maning.gankmm.bean.GankDate;
import com.maning.gankmm.callback.MyCallBack;
import com.socks.library.KLog;

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

    public static Call<GankDate> getGankHistoryDate(final int what, final MyCallBack myCallBack) {
        Call<GankDate> gankHistoryDate = BuildApi.getAPIService().getGankHistoryDate();
        gankHistoryDate.enqueue(new Callback<GankDate>() {
            @Override
            public void onResponse(Response<GankDate> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    GankDate gankDate = response.body();
                    KLog.i("getGankHistoryDate---onResponse：" + gankDate.toString());
                    if (!gankDate.isError()) {
                        //数据正确，把数据返回
                        myCallBack.onSuccess(what, gankDate);
                    } else {
                        //数据错误
                        myCallBack.onSuccess(what, "失败");
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                KLog.i("getGankHistoryDate----onFailure：" + t.toString());
                //数据错误
                myCallBack.onFail(what, "失败");
            }
        });
        return gankHistoryDate;
    }

    public static Call<PublicData> getCommonData(String type, int count, int pageIndex, final int what, final MyCallBack myCallBack) {
        Call<PublicData> commonDate = BuildApi.getAPIService().getCommonDate(type, count, pageIndex);
        commonDate.enqueue(new Callback<PublicData>() {
            @Override
            public void onResponse(Response<PublicData> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    PublicData publicData = response.body();
                    KLog.i("getCommonData---onResponse：" + publicData.toString());
                    if (!publicData.isError()) {
                        //数据正确，把数据返回
                        myCallBack.onSuccess(what, publicData);
                    } else {
                        //数据错误
                        myCallBack.onSuccess(what, "失败");
                    }
                }
            }
            @Override
            public void onFailure(Throwable t) {
                KLog.i("getCommonData-----onFailure：" + t.toString());
                //数据错误
                myCallBack.onFail(what, "失败");
            }
        });

        return commonDate;
    }

}
