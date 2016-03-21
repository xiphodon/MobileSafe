package com.gc.p01_mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.gc.p01_mobilesafe.bean.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

/**
 * �������־û���
 * 
 * @author guochang
 * 
 */
public class BlackNumberDAO {
	private BlackNumberOpenHelper Helper;

	public BlackNumberDAO(Context context) {
		Helper = new BlackNumberOpenHelper(context);
	}

	/**
	 * ������غ���
	 * 
	 * @param number
	 *            ���غ���
	 * @param mode
	 *            ����ģʽ
	 * @return ��ӳɹ����
	 */
	public boolean add(String number, String mode) {
		SQLiteDatabase db = Helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		long rowid = db.insert("blacknumber", null, values);
		db.close();
		if (rowid == -1) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * ɾ�����غ���
	 * 
	 * @param number
	 *            ���غ���
	 * @return ɾ���ɹ����
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = Helper.getWritableDatabase();
		int rowNumber = db.delete("blacknumber", "number=?",
				new String[] { number });
		db.close();
		if (rowNumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * �������غ���ģʽ
	 * 
	 * @param number
	 *            ���غ���
	 * @return ���ĳɹ����
	 */
	public boolean changeNumberMode(String number, String mode) {
		SQLiteDatabase db = Helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		int rowNumber = db.update("blacknumber", values, "number=?",
				new String[] { number });
		db.close();
		if (rowNumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ��ѯһ�����������ģʽ
	 * 
	 * @param number
	 *            ���غ���
	 * @return ����ģʽ
	 */
	public String findNumber(String number) {
		String mode = "";
		SQLiteDatabase db = Helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", new String[] { "mode" },
				"number=?", new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			mode = cursor.getString(cursor.getColumnIndex("mode"));
		}
		cursor.close();
		db.close();
		return mode;
	}

	/**
	 * ��ѯ���к�����
	 * 
	 * @return ���еĺ�����������
	 */
	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = Helper.getReadableDatabase();
		Cursor cursor = db
				.query("blacknumber", new String[] { "number", "mode" }, null,
						null, null, null, null);
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setNumber(cursor.getString(cursor
					.getColumnIndex("number")));
			blackNumberInfo.setMode(cursor.getString(cursor
					.getColumnIndex("mode")));
			list.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		SystemClock.sleep(3000);
		return list;
	}

	/**
	 * ��ҳ��������
	 * 
	 * @param pageNow
	 *            ��ǰҳ
	 * @param pageSize
	 *            ÿҳ��ʾ����Ŀ��
	 * @return ��ҳ��ѯ��������
	 */
	public List<BlackNumberInfo> findPar(int pageNow, int pageSize) {
		SQLiteDatabase db = Helper.getReadableDatabase();
		// limit ���Ƶ�ǰ�ж������� offset ��ʾ���� �ӵڼ�����ʼ
		Cursor cursor = db.rawQuery(
				"select number,mode from blacknumber limit ? offset ?",
				new String[] { String.valueOf(pageSize),
						String.valueOf(pageSize * (pageNow - 1)) });
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setNumber(cursor.getString(cursor
					.getColumnIndex("number")));
			blackNumberInfo.setMode(cursor.getString(cursor
					.getColumnIndex("mode")));
			list.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * ������������
	 * 
	 * @param startIndex
	 *            �ӵڼ�����ʼ
	 * @param count
	 *            ÿҳ��ʾ����Ŀ��
	 * @return
	 */
	public List<BlackNumberInfo> findPar2(int startIndex, int count) {
		SQLiteDatabase db = Helper.getReadableDatabase();
		// limit ���Ƶ�ǰ�ж������� offset ��ʾ���� �ӵڼ�����ʼ
		Cursor cursor = db.rawQuery(
				"select number,mode from blacknumber limit ? offset ?",
				new String[] { String.valueOf(count),
						String.valueOf(startIndex) });
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setNumber(cursor.getString(cursor
					.getColumnIndex("number")));
			blackNumberInfo.setMode(cursor.getString(cursor
					.getColumnIndex("mode")));
			list.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * ��ѯһ���ж�������Ϣ
	 * 
	 * @return
	 */
	public int getTotalNumber() {
		SQLiteDatabase db = Helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(number) from blacknumber",
				null);
		int count = 0;
		if (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}

}
