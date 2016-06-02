package com.maning.gankmm.bean;


/**
 * Created by maning on 16/6/2.
 * 接口返回总Bean
 */
public class HttpResult<T> {

    private boolean error;

    private T results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "error=" + error +
                ", results=" + results.toString() +
                '}';
    }
}
