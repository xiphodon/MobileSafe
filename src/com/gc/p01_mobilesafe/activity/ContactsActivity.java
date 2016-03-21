package com.gc.p01_mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.gc.p01_mobilesafe.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 读取手机联系人
 * @author guochang
 *
 */
public class ContactsActivity extends Activity {

	private ListView lv_list;
	private ArrayList<HashMap<String, String>> contacts;
	private LinearLayout ll_ProgressBar;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			// 隐藏进度条
			ll_ProgressBar.setVisibility(View.INVISIBLE);
			
			lv_list.setAdapter(new SimpleAdapter(ContactsActivity.this, contacts,
					R.layout.contact_list_item, new String[] { "name", "phone" },
					new int[] { R.id.tv_name, R.id.tv_phone }));
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		lv_list = (ListView) findViewById(R.id.lv_list);
		ll_ProgressBar = (LinearLayout) findViewById(R.id.ll_ProgressBar);
		
		new Thread(){
			public void run() {
				contacts = readContacts();
				handler.sendEmptyMessage(0);
			};
		}.start();
		
		
		lv_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//读取当前item的电话号码
				//将数据放置在intent中返回上一个页面
				String phone = contacts.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(Activity.RESULT_OK, intent);
				
				finish();
			}
		});
	}
	

	/**
	 * 读取手机联系人
	 * @return 联系人信息
	 */
	public ArrayList<HashMap<String, String>> readContacts() {
		// 1.从raw_contacts中读取联系人的id（"contact_id"）
		// 2.根据contact_id从data表中查询出相应的电话号码和联系人名称
		// 3.根据mimetype来区分data中哪条数据是联系人和号码类型

		// 通过内容提供者访问联系人数据库(需要添加读取联系人权限)
		ContentResolver cr = getContentResolver();
		// 1.从raw_contacts中读取联系人的id（"contact_id"）
		Cursor cursorContactId = cr.query(
				Uri.parse("content://com.android.contacts/raw_contacts"),
				new String[] { "contact_id" }, null, null, null);

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		if (cursorContactId != null) {

			while (cursorContactId.moveToNext()) {
				String contactId = cursorContactId.getString(0);
				
				//实际中手机查询到的 contactId 有可能有null
				if(TextUtils.isEmpty(contactId)){
					continue;
				}

				// 2.根据contact_id从data表(实际上是视图 view_data)中查询出相应的电话号码和联系人名称
				Cursor cursorData = cr.query(
						Uri.parse("content://com.android.contacts/data"),
						new String[] { "data1", "mimetype" }, "contact_id = ?",
						new String[] { contactId }, null);

				if (cursorData != null) {

					HashMap<String, String> map = new HashMap<String, String>();

					while (cursorData.moveToNext()) {

						String data1 = cursorData.getString(0);
						String mimetype = cursorData.getString(1);
						if ("vnd.android.cursor.item/name".equals(mimetype)) {
							map.put("name", data1);
						} else if ("vnd.android.cursor.item/phone_v2"
								.equals(mimetype)) {
							map.put("phone", data1);
						}

					}

					// 将每组数据加入链表
					list.add(map);

					cursorData.close();
				}
			}
			cursorContactId.close();
		}
		return list;
	}

}
