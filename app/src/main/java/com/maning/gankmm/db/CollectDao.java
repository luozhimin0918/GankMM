package com.maning.gankmm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.bean.GankEntity;

import java.util.ArrayList;

/**
 * Created by maning on 16/3/5.
 * <p/>
 * 收藏表的操作
 */
public class CollectDao {

    private GankMMHelper helper;

    private SQLiteDatabase db;

    public CollectDao() {
        helper = new GankMMHelper(MyApplication.getIntstance());
    }

    /**
     * 往数据库中插入一条收藏数据
     *
     * @param gankResult
     * @return 是否插入成功
     */
    public synchronized boolean insertOneCollect(GankEntity gankResult) {
        db = helper.getWritableDatabase();
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
        long insert = db.insert(GankMMHelper.TABLE_NAME_COLLECT, null, values);
        db.close();
        return insert != (-1);
    }

    /**
     * 删除一条数据
     *
     * @param ganID
     * @return
     */
    public synchronized boolean deleteOneCollect(String ganID) {
        db = helper.getWritableDatabase();
        int i = db.delete(GankMMHelper.TABLE_NAME_COLLECT, GankMMHelper.GankID + "=?", new String[]{ganID});
        db.close();
        return i > 0;
    }

    /**
     * 查询是否存在
     *
     * @param GankID
     * @return
     */
    public synchronized boolean queryOneCollectByID(String GankID) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.query(GankMMHelper.TABLE_NAME_COLLECT, null, GankMMHelper.GankID + "=?", new String[]{GankID}, null, null, null);
        boolean result = false;
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询每个标签的收藏数据
     *
     * @param type
     * @return 集合数据
     */
    public synchronized ArrayList<GankEntity> queryAllCollectByType(String type) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.query(GankMMHelper.TABLE_NAME_COLLECT, null, GankMMHelper.type + "=?", new String[]{type}, null, null, null);

        ArrayList<GankEntity> collectList = new ArrayList<>();
        GankEntity collect;
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

            collect = new GankEntity();
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
