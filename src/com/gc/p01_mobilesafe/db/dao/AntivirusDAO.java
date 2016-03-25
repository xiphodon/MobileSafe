package com.gc.p01_mobilesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 病毒数据库
 * 
 * @author guochang
 * 
 */
public class AntivirusDAO {

	// 文件安装路径下的拷贝的数据库
	private static final String PATH = "data/data/com.gc.p01_mobilesafe/files/antivirus.db";

	/**
	 * 查询当前MD5值是否在病毒数据库中
	 * 
	 * @param fileMD5
	 *            文件MD5值
	 * @return
	 */
	public static String checkFileVirus(String fileMD5) {

		String desc = "";

		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);

		// 查询传过来的fileMD5是否在 病毒数据库里
		Cursor cursor = database.rawQuery(
				"select desc from datable where md5 = ?",
				new String[] { fileMD5 });

		if (cursor.moveToNext()) {
			desc = cursor.getString(0);
		}
		cursor.close();
		database.close();
		return desc;
	}

	/**
	 * 向数据库里增加病毒
	 * 
	 * @param md5
	 *            病毒md5码
	 * @param desc
	 *            病毒详情
	 */
	public static void addVirus(String md5, String desc) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"data/data/com.gc.p01_mobilesafe/files/antivirus.db", null,
				SQLiteDatabase.OPEN_READWRITE);

		ContentValues values = new ContentValues();

		values.put("md5", md5);

		values.put("type", 6);

		values.put("name", "Android.Hack.CarrierIQ.a");

		values.put("desc", desc);

		db.insert("datable", null, values);

		db.close();
	}
}
