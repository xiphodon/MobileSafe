package com.gc.p01_mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 黑名单数据库帮助类
 * @author guochang
 *
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {

	public BlackNumberOpenHelper(Context context) {
		super(context, "safe.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * blacknumber 表名
	 * _id 主键自增长
	 * number 拦截电话号码
	 * mode 拦截模式（电话拦截，短信拦截，电话+短信拦截）
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table blacknumber (_id integer primary key autoincrement,number verchar(20),mode verchar(2))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
