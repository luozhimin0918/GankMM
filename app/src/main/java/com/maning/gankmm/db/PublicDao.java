package com.maning.gankmm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.bean.PublicData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maning on 16/3/5.
 * <p/>
 * 公共表的操作
 */
public class PublicDao {

    private GankMMHelper helper;

    private SQLiteDatabase db;

    public PublicDao() {
        helper = new GankMMHelper(MyApplication.getIntstance());
    }

    /**
     * 往数据库中插入一条收藏数据
     */
    public synchronized void insertList(List<PublicData.ResultsEntity> list) {
        db = helper.getWritableDatabase();
        //插入
        db.beginTransaction();
        for (int i = 0; i < list.size(); i++) {
            PublicData.ResultsEntity gankResult = list.get(i);
            //查看数据库中有没有
            boolean queryByID = queryByID(gankResult.get_id());
            if (!queryByID) {
                ContentValues values = new ContentValues();
                values.put(GankMMHelper.GankID, gankResult.get_id());
                values.put(GankMMHelper.createdAt, gankResult.getCreatedAt());
                values.put(GankMMHelper.desc, gankResult.getDesc());
                values.put(GankMMHelper.NS, gankResult.get_ns());
                values.put(GankMMHelper.publishedAt, gankResult.getPublishedAt());
                values.put(GankMMHelper.source, gankResult.getSource());
                values.put(GankMMHelper.type, gankResult.getType());
                values.put(GankMMHelper.url, gankResult.getUrl());
                values.put(GankMMHelper.who, gankResult.getWho());
                if (gankResult.isUsed()) {
                    values.put(GankMMHelper.used, "true");
                } else {
                    values.put(GankMMHelper.used, "false");
                }
                db.insert(GankMMHelper.TABLE_NAME_PUBLIC, null, values);
            }
        }
        //设置成功
        db.setTransactionSuccessful();
        //结束事务
        db.endTransaction();
        //关闭数据库
        db.close();
    }

    /**
     * 查询是否存在
     *
     * @param GankID
     * @return
     */
    private synchronized boolean queryByID(String GankID) {
        Cursor cursor = db.query(GankMMHelper.TABLE_NAME_PUBLIC, null, GankMMHelper.GankID + "=?", new String[]{GankID}, null, null, null);
        boolean result = false;
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        return result;
    }

    /**
     * 查询每个标签的收藏数据
     *
     * @param type
     * @return 集合数据
     */
    public synchronized ArrayList<PublicData.ResultsEntity> queryAllCollectByType(String type) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.query(GankMMHelper.TABLE_NAME_PUBLIC, null, GankMMHelper.type + "=?", new String[]{type}, null, null, null);

        ArrayList<PublicData.ResultsEntity> collectList = new ArrayList<>();
        PublicData.ResultsEntity collect;
        while (cursor.moveToNext()) {
            //查询
            String createdAt = cursor.getString(cursor.getColumnIndex(GankMMHelper.createdAt));
            String desc = cursor.getString(cursor.getColumnIndex(GankMMHelper.desc));
            String GankID = cursor.getString(cursor.getColumnIndex(GankMMHelper.GankID));
            String NS = cursor.getString(cursor.getColumnIndex(GankMMHelper.NS));
            String publishedAt = cursor.getString(cursor.getColumnIndex(GankMMHelper.publishedAt));
            String source = cursor.getString(cursor.getColumnIndex(GankMMHelper.source));
            String url = cursor.getString(cursor.getColumnIndex(GankMMHelper.url));
            String who = cursor.getString(cursor.getColumnIndex(GankMMHelper.who));

            collect = new PublicData.ResultsEntity();
            collect.set_id(GankID);
            collect.set_ns(NS);
            collect.setCreatedAt(createdAt);
            collect.setDesc(desc);
            collect.setPublishedAt(publishedAt);
            collect.setSource(source);
            collect.setType(type);
            collect.setUrl(url);
            collect.setWho(who);
            collectList.add(collect);
        }
        //关闭游标
        cursor.close();
        db.close();
        return collectList;
    }
}
