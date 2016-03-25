package com.gc.p01_mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 程序锁数据库帮助类
 * @author guochang
 *
 */
public class AppLockOpenHelper extends SQLiteOpenHelper {

	public AppLockOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table applock (_id integer primary key autoincrement,packagename verchar(20))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
