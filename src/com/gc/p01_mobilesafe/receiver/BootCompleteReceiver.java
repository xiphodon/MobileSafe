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
 * 监听手机开机的广播
 * @author guochang
 * (注册广播接收者  和  要接收的开机广播 ，添加 接收开机广播的权限)
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//获取绑定的sim卡得序列号
		SharedPreferences mPref = context.getSharedPreferences("config", context.MODE_PRIVATE);
		
		boolean protect = mPref.getBoolean("protect", false);
		
		//是否开启防盗保护
		if(protect){

			String sim = mPref.getString("sim", null);
			
			if(!TextUtils.isEmpty(sim)){
				//获取当前sim卡得序列号
				TelephonyManager telephonyManager = (TelephonyManager) context
						.getSystemService(context.TELEPHONY_SERVICE);
				String currentSim = telephonyManager.getSimSerialNumber();
				
				//sim卡序列号对比
				if(sim.equals(currentSim)){
					System.out.println("sim未被更换，手机安全");
				}else{
					System.out.println("sim卡已被更换，安全卫士正在发送报警短信...");
					
					String phone = mPref.getString("safe_phone", "");
					//发送短信给安全号码（发短信权限）
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phone, null, "sim card changed!", null, null);
					
					Toast.makeText(context, "已发送sim卡变更短信", Toast.LENGTH_LONG).show();
				}
			}
		}
		
	}

}
