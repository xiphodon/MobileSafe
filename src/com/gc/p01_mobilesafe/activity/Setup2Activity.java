package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.view.SettingItemView;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * �ֻ����� ������ ��2ҳ
 * 
 * @author guochang
 * 
 */
public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView siv_sim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		siv_sim = (SettingItemView) findViewById(R.id.siv_sim);
		
		String sim = mPref.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			siv_sim.setChecked(false);
		}else{
			siv_sim.setChecked(true);
		}

		siv_sim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (siv_sim.isChecked()) {
					// ȡ����sim��
					siv_sim.setChecked(false);
					//�Ƴ���
					mPref.edit().remove("sim").commit();
				} else {
					// ��sim��
					siv_sim.setChecked(true);

					// ����sim�����к���sp��,��ҪȨ��
					TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = telephonyManager.getSimSerialNumber();
					
					mPref.edit().putString("sim", simSerialNumber).commit();
					
				}
			}
		});

	}

	@Override
	public void touchNext() {
		// TODO Auto-generated method stub
		//����sim�����ܽ�����һ��
		String sim = mPref.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			Toast.makeText(this, "���Ȱ�sim��", Toast.LENGTH_SHORT).show();
			return;
		}
		
		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		// ҳ����ת����
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void touchPrevious() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, Setup1Activity.class));
		finish();

		// ҳ����ת����
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

}
