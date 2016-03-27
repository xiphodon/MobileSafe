package com.gc.p01_mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 程序锁数据库
 * @author guochang
 *
 */
public class AppLockDAO {

	private AppLockOpenHelper helper;

	public AppLockDAO(Context context) {
		helper = new AppLockOpenHelper(context);
	}
	
	/**
	 * 添加应用包名到数据库
	 * @param packageName 应用包名
 	 */
	public void add(String packageName){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename", packageName);
		db.insert("applock", null, values);
		db.close();
	}
	
	/**
	 * 从数据库删除应用包名
	 * @param packageName
	 */
	public void delete(String packageName){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packagename=?", new String[]{packageName});
		db.close();
	}
	
	/**
	 * 查询该应用包名是否在数据库中
	 * @param packageName 当前包名
	 * @return
	 */
	public boolean find(String packageName){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", null, "packagename=?", new String[]{packageName}, null, null, null);
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
}
