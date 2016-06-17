package com.maning.gankmm.http;

import com.maning.gankmm.bean.DayEntity;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.bean.HttpResult;
import com.maning.gankmm.constant.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;


/**
 * 接口调用的工具类
 */
public interface APIService {

    //这里填写全部路径就会覆盖掉Build得BaseUrl
    @Headers("Cache-Control: public, max-age=3600")
    @GET(Constants.URL_HistoryDate)
    Call<HttpResult<List<String>>> getGankHistoryDate();

    //http://gank.io/api/data/Android/10/1
    @Headers("Cache-Control: public, max-age=120")
    @GET("data/{type}/{count}/{pageIndex}")
    Call<HttpResult<List<GankEntity>>> getCommonDateNew(@Path("type") String type,
                                                        @Path("count") int count,
                                                        @Path("pageIndex") int pageIndex
    );

    //http://gank.io/api/day/2015/08/06 --- 每日数据
    @Headers("Cache-Control: public, max-age=300")
    @GET("day/{year}/{month}/{day}")
    Call<DayEntity> getOneDayData(@Path("year") String year,
                                  @Path("month") String month,
                                  @Path("day") String day
    );


}
