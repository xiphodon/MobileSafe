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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * �ֻ����� ������ ��3ҳ
 * 
 * @author guochang
 * 
 */
public class Setup3Activity extends BaseSetupActivity {
	
	private EditText et_phone = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		
		et_phone = (EditText) findViewById(R.id.et_phone);

		String phone = mPref.getString("safe_phone", "");
		et_phone.setText(phone);
	}

	@Override
	public void touchNext() {
		// TODO Auto-generated method stub
		//��������룬���ܽ�����һ��
		String phone = et_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "��ȫ���벻��Ϊ�գ�", Toast.LENGTH_SHORT).show();
			return;
		}
		
		//���氲ȫ����
		mPref.edit().putString("safe_phone", phone).commit();
		
		startActivity(new Intent(this, Setup4Activity.class));
		finish();

		// ҳ����ת����
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void touchPrevious() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, Setup2Activity.class));
		finish();

		// ҳ����ת����
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

	/**
	 * ��ť��ѡ����ϵ��
	 * @param view
	 */
	public void selectContact(View view) {
		Intent intent = new Intent(this, ContactsActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if(resultCode == Activity.RESULT_OK){
			String phone = data.getExtras().getString("phone");
			phone = phone.replaceAll("-", "").replaceAll(" ", "");
			et_phone.setText(phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
