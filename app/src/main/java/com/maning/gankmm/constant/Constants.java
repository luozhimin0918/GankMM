package com.maning.gankmm.constant;

import android.os.Environment;

/**
 * 常量类
 * 一些接口地址等常量
 */
public class Constants {

    //接口请求的Url
    public static final String BASEURL = "http://gank.io/api/";

    //干活历史日期
    public static final String URL_HistoryDate = "http://gank.io/api/day/history";

    //保存图片的地址
    public static final String BasePath = Environment.getExternalStorageDirectory() + "/GankMM";

    //保存图片操作提示的Key
    public static final String HasShowHint = "PicFlag";

    //标签
    public static final String FlagFragment = "Flag";
    public static final String FlagWelFare = "福利";
    public static final String FlagAndroid = "Android";
    public static final String FlagIOS = "iOS";
    public static final String FlagVideo = "休息视频";
    public static final String FlagJS = "前端";
    public static final String FlagExpand = "拓展资源";
    public static final String FlagRecommend = "瞎推荐";
    public static final String FlagAPP = "App";
    public static final String FlagCollect = "收藏";


}
