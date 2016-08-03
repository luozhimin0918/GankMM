package com.maning.gankmm.bean;

import java.util.List;

/**
 * Created by maning on 16/8/3.
 */
public class RandomEntity {


    /**
     * error : false
     * results : [{"_id":"56cc6d1c421aa95caa70750e","createdAt":"2015-11-03T08:53:59.598Z","desc":"交互式的 Canvas","publishedAt":"2015-11-04T04:01:55.585Z","type":"Android","url":"https://github.com/elevenetc/InteractiveCanvas","used":true,"who":"mthli"},{"_id":"56cc6d1c421aa95caa7074fc","createdAt":"2015-06-10T04:45:12.150Z","desc":"为Android提供不可改变的数据集支持","publishedAt":"2015-06-11T03:30:41.4Z","type":"Android","url":"https://github.com/konmik/solid","used":true,"who":"mthli"}]
     */

    private boolean error;
    /**
     * _id : 56cc6d1c421aa95caa70750e
     * createdAt : 2015-11-03T08:53:59.598Z
     * desc : 交互式的 Canvas
     * publishedAt : 2015-11-04T04:01:55.585Z
     * type : Android
     * url : https://github.com/elevenetc/InteractiveCanvas
     * used : true
     * who : mthli
     */

    private List<GankEntity> results;


    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<GankEntity> getResults() {
        return results;
    }

    public void setResults(List<GankEntity> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "RandomEntity{" +
                "error=" + error +
                ", results=" + results.toString() +
                '}';
    }
}
