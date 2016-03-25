package com.gc.p01_mobilesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * �������ݿ�
 * 
 * @author guochang
 * 
 */
public class AntivirusDAO {

	// �ļ���װ·���µĿ��������ݿ�
	private static final String PATH = "data/data/com.gc.p01_mobilesafe/files/antivirus.db";

	/**
	 * ��ѯ��ǰMD5ֵ�Ƿ��ڲ������ݿ���
	 * 
	 * @param fileMD5
	 *            �ļ�MD5ֵ
	 * @return
	 */
	public static String checkFileVirus(String fileMD5) {

		String desc = "";

		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);

		// ��ѯ��������fileMD5�Ƿ��� �������ݿ���
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
	 * �����ݿ������Ӳ���
	 * 
	 * @param md5
	 *            ����md5��
	 * @param desc
	 *            ��������
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
