package com.maning.gankmm.http;

import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.bean.HttpResult;
import com.maning.gankmm.constant.Constants;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * 接口调用的工具类
 */
public interface APIService {

//    //这里填写全部路径就会覆盖掉Build得BaseUrl
//    @GET(Constants.URL_HistoryDate)
//    Call<GankDate> getGankHistoryDate();
//
//    //http://gank.io/api/data/Android/10/1
//    @GET("data/{type}/{count}/{pageIndex}")
//    Call<PublicData> getCommonDate(@Path("type") String type,
//                                   @Path("count") int count,
//                                   @Path("pageIndex") int pageIndex
//    );

    //http://gank.io/api/data/Android/10/1
    @GET("data/{type}/{count}/{pageIndex}")
    Call<HttpResult<List<GankEntity>>> getCommonDateNew(@Path("type") String type,
                                               @Path("count") int count,
                                               @Path("pageIndex") int pageIndex
    );


}
