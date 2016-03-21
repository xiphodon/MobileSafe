package com.gc.p01_mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 查询归属地数据库
 * 
 * @author guochang
 * 
 */
public class AddressDAO {

	// 文件安装路径下的拷贝的数据库
	private static final String PATH = "data/data/com.gc.p01_mobilesafe/files/address.db";

	public static String getAddress(String number) {
		String address = "未知号码";

		// 获得数据库对象
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);

		// 手机号特点：1+（3.4.5.6.7.8）+数字（9位）
		// 正则表达式：^1[3-8]\d{9} “\d” 为转义字符
		if (number.matches("^1[3-8]\\d{9}")) {

			// 截取号码前七位进行数据库查询
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });

			if (cursor.moveToNext()) {
				address = cursor.getString(cursor.getColumnIndex("location"));
			}
			cursor.close();
		} else if (number.matches("^\\d+")) { // 纯数字
			switch (number.length()) {
			case 3:
				address = "报警电话";
				break;

			case 4:
				address = "模拟器";
				break;

			case 5:
				address = "客服电话";
				break;

			case 7:
			case 8:
				address = "本地固话";
				break;
			default:
				// 可能为长途电话 010 8888888，0834 1111111
				if (number.startsWith("0") && number.length() >= 10) {
					Cursor cursor = database.rawQuery(
							"select location from data2 where area=?",
							new String[] { number.substring(1, 4) });

					if (cursor.moveToNext()) {
						address = cursor.getString(cursor
								.getColumnIndex("location"));
						
					} else {// 四位没有匹配到，开始匹配三位
						cursor.close();

						cursor = database.rawQuery(
								"select location from data2 where area=?",
								new String[] { number.substring(1, 3) });

						if (cursor.moveToNext()) {
							address = cursor.getString(cursor
									.getColumnIndex("location"));
						}
					}
					cursor.close();
				}
				break;
			}
		}

		database.close();
		return address;
	}
}
