package com.maning.gankmm.callback;

/**
 * Created by maning on 16/1/13.
 * <p/>
 * 网络回调
 */
public interface MyCallBack {

    /**
     * 成功的回调
     *
     * @param what
     * @param result
     */
    void onSuccess(int what, Object result);

    /**
     * 失败的回调
     *
     * @param what
     * @param result
     */
    void onFail(int what, String result);

}
