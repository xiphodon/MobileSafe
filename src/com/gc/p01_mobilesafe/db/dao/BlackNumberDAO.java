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
 * 黑名单持久化类
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
	 * 添加拦截号码
	 * 
	 * @param number
	 *            拦截号码
	 * @param mode
	 *            拦截模式
	 * @return 添加成功与否
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
	 * 删除拦截号码
	 * 
	 * @param number
	 *            拦截号码
	 * @return 删除成功与否
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
	 * 更改拦截号码模式
	 * 
	 * @param number
	 *            拦截号码
	 * @return 更改成功与否
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
	 * 查询一个号码的拦截模式
	 * 
	 * @param number
	 *            拦截号码
	 * @return 拦截模式
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
	 * 查询所有黑名单
	 * 
	 * @return 所有的黑名单的链表
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
	 * 分页加载数据
	 * 
	 * @param pageNow
	 *            当前页
	 * @param pageSize
	 *            每页显示的条目数
	 * @return 分页查询所的链表
	 */
	public List<BlackNumberInfo> findPar(int pageNow, int pageSize) {
		SQLiteDatabase db = Helper.getReadableDatabase();
		// limit 限制当前有多少数据 offset 表示跳过 从第几条开始
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
	 * 分批加载数据
	 * 
	 * @param startIndex
	 *            从第几条开始
	 * @param count
	 *            每页显示的条目数
	 * @return
	 */
	public List<BlackNumberInfo> findPar2(int startIndex, int count) {
		SQLiteDatabase db = Helper.getReadableDatabase();
		// limit 限制当前有多少数据 offset 表示跳过 从第几条开始
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
	 * 查询一共有多少条信息
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
