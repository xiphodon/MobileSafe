package com.gc.p01_mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * ��ѯ���������ݿ�
 * 
 * @author guochang
 * 
 */
public class AddressDAO {

	// �ļ���װ·���µĿ��������ݿ�
	private static final String PATH = "data/data/com.gc.p01_mobilesafe/files/address.db";

	public static String getAddress(String number) {
		String address = "δ֪����";

		// ������ݿ����
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);

		// �ֻ����ص㣺1+��3.4.5.6.7.8��+���֣�9λ��
		// ������ʽ��^1[3-8]\d{9} ��\d�� Ϊת���ַ�
		if (number.matches("^1[3-8]\\d{9}")) {

			// ��ȡ����ǰ��λ�������ݿ��ѯ
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });

			if (cursor.moveToNext()) {
				address = cursor.getString(cursor.getColumnIndex("location"));
			}
			cursor.close();
		} else if (number.matches("^\\d+")) { // ������
			switch (number.length()) {
			case 3:
				address = "�����绰";
				break;

			case 4:
				address = "ģ����";
				break;

			case 5:
				address = "�ͷ��绰";
				break;

			case 7:
			case 8:
				address = "���ع̻�";
				break;
			default:
				// ����Ϊ��;�绰 010 8888888��0834 1111111
				if (number.startsWith("0") && number.length() >= 10) {
					Cursor cursor = database.rawQuery(
							"select location from data2 where area=?",
							new String[] { number.substring(1, 4) });

					if (cursor.moveToNext()) {
						address = cursor.getString(cursor
								.getColumnIndex("location"));
						
					} else {// ��λû��ƥ�䵽����ʼƥ����λ
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
