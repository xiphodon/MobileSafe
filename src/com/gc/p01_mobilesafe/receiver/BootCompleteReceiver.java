package com.gc.p01_mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * �����ֻ������Ĺ㲥
 * @author guochang
 * (ע��㲥������  ��  Ҫ���յĿ����㲥 ����� ���տ����㲥��Ȩ��)
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//��ȡ�󶨵�sim�������к�
		SharedPreferences mPref = context.getSharedPreferences("config", context.MODE_PRIVATE);
		
		boolean protect = mPref.getBoolean("protect", false);
		
		//�Ƿ�����������
		if(protect){

			String sim = mPref.getString("sim", null);
			
			if(!TextUtils.isEmpty(sim)){
				//��ȡ��ǰsim�������к�
				TelephonyManager telephonyManager = (TelephonyManager) context
						.getSystemService(context.TELEPHONY_SERVICE);
				String currentSim = telephonyManager.getSimSerialNumber();
				
				//sim�����кŶԱ�
				if(sim.equals(currentSim)){
					System.out.println("simδ���������ֻ���ȫ");
				}else{
					System.out.println("sim���ѱ���������ȫ��ʿ���ڷ��ͱ�������...");
					
					String phone = mPref.getString("safe_phone", "");
					//���Ͷ��Ÿ���ȫ���루������Ȩ�ޣ�
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phone, null, "sim card changed!", null, null);
					
					Toast.makeText(context, "�ѷ���sim���������", Toast.LENGTH_LONG).show();
				}
			}
		}
		
	}

}
