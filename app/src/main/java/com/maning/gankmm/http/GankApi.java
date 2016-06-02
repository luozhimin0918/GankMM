package com.maning.gankmm.http;

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

    public static Call<HttpResult<List<GankEntity>>> getCommonDataNew(String type, int count, int pageIndex, final int what, final MyCallBack myCallBack) {
        Call<HttpResult<List<GankEntity>>> commonDateNew = BuildApi.getAPIService().getCommonDateNew(type, count, pageIndex);

        commonDateNew.enqueue(new Callback<HttpResult<List<GankEntity>>>() {
            @Override
            public void onResponse(Response<HttpResult<List<GankEntity>>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    HttpResult<List<GankEntity>> httpResult = response.body();
                    if(httpResult!=null) {
                        if (!httpResult.isError()) {
                            List<GankEntity> gankEntityList = httpResult.getResults();
                            KLog.i("httpCallBack---gankEntityList：" + gankEntityList.toString());
                            myCallBack.onSuccessList(what, gankEntityList);
                        } else {
                            myCallBack.onFail(what, "获取数据失败");
                        }
                    }else{
                        myCallBack.onFail(what, "获取数据失败");
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                KLog.i("httpCallBack-----onFailure：" + t.toString());
                //数据错误
                myCallBack.onFail(what, "获取数据失败");
            }
        });

        return commonDateNew;

    }

}
