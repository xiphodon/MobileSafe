package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * �ֻ�����ҳ��
 * 
 * @author guochang
 * 
 */
public class LostFindActivity extends Activity {

	private SharedPreferences mPref;
	private TextView tv_safePhone;
	private ImageView iv_protect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		// �Ƿ����ù� ������
		boolean configed = mPref.getBoolean("configed", false);

		if (configed) {
			setContentView(R.layout.activity_lost_find);
			
			//��ʾ��ȫ����
			tv_safePhone = (TextView) findViewById(R.id.tv_safePhone);
			String safe_phone = mPref.getString("safe_phone", "");
			if(!TextUtils.isEmpty(safe_phone)){
				tv_safePhone.setText(safe_phone);
			}
			
			//��ʾ�Ƿ�����ȫ����(ͼƬ)
			iv_protect = (ImageView) findViewById(R.id.iv_protect);
			boolean protect = mPref.getBoolean("protect", false);
			if(protect){
				iv_protect.setImageResource(R.drawable.lock);
			}else{
				iv_protect.setImageResource(R.drawable.unlock);
			}

		} else {
			// ��ת��������ҳ
			startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
			
			finish();
		}

	}
	
	/**
	 * ���½���������
	 * @param view
	 */
	public void reEnter(View view){
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}
	
}
