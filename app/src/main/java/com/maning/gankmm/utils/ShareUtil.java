package com.maning.gankmm.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 存储和去除配置信息的工具类
 * @author Administrator	maning
 */
public class ShareUtil {
	
	private static String CONFIG = "config";
	private static SharedPreferences sp;
	
	/**
	 * 保存boolean类配置信息
	 * @param context	上下文
	 * @param key		键
	 * @param value		值
	 */
	public static void saveBooleanData(Context context,String key,boolean value){
		if(sp == null){
			sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}
	
	/**
	 * 获取boolean类配置信息
	 * @param context	上下文
	 * @param key		键
	 * @param defValue		值
	 */
	public static boolean getBooleanData(Context context,String key,boolean defValue){
		if(sp == null){
			sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
	}
	
	/**
	 * 保存String类型配置信息
	 * @param context	上下文
	 * @param key		键
	 * @param value		值
	 */
	public static void saveStringData(Context context,String key,String value){
		if(sp == null){
			sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	
	/**
	 * 获取String类型配置信息
	 * @param context	上下文
	 * @param key		键
	 * @param defValue		值
	 */
	public static String getStringData(Context context,String key,String defValue){
		if(sp == null){
			sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
	}

}
