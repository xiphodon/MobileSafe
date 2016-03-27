package com.gc.p01_mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * ���������ݿ�
 * @author guochang
 *
 */
public class AppLockDAO {

	private AppLockOpenHelper helper;

	public AppLockDAO(Context context) {
		helper = new AppLockOpenHelper(context);
	}
	
	/**
	 * ���Ӧ�ð��������ݿ�
	 * @param packageName Ӧ�ð���
 	 */
	public void add(String packageName){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename", packageName);
		db.insert("applock", null, values);
		db.close();
	}
	
	/**
	 * �����ݿ�ɾ��Ӧ�ð���
	 * @param packageName
	 */
	public void delete(String packageName){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packagename=?", new String[]{packageName});
		db.close();
	}
	
	/**
	 * ��ѯ��Ӧ�ð����Ƿ������ݿ���
	 * @param packageName ��ǰ����
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
