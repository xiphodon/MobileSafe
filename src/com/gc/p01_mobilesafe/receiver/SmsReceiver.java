package com.gc.p01_mobilesafe.receiver;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.service.LocationService;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * ���ض���
 * 
 * @author guochang
 * 
 */
public class SmsReceiver extends BroadcastReceiver {

	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	private SharedPreferences mPref;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		//��ȡ�豸���Է���
        mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //�豸�������
        mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);
        
        mPref = context.getSharedPreferences("config", context.MODE_PRIVATE);
        
        //�Ƿ��ѿ�����ȫ����
        boolean protect = mPref.getBoolean("protect", false);
        if(protect){

    		// �õ����ŵ���Ϣ
    		// ������Ϣ��װ��intent��
    		Bundle bundle = intent.getExtras();
    		// ��pdus��Э�����ݵ�Ԫ��Ϊ����ȡ��һ��object���飬�����е�ÿһ��Ԫ�ض���һ������
    		Object[] objects = (Object[]) bundle.get("pdus");

    		// �õ��㲥�е����еĶ���
    		for (Object object : objects) {
    			// ͨ��pdu���������
    			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);

    			// ������Դ����
    			String originatingAddress = sms.getOriginatingAddress();
    			// ��������
    			String messageBody = sms.getMessageBody();

    			// System.out.println(originatingAddress + ":" +messageBody);

    			if ("#*alarm*#".equals(messageBody)) {
    				// ���ű�������
    				MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
    				// ������С���������������� ���������ֵ
    				player.setVolume(1f, 1f);
    				// ѭ������
    				player.setLooping(true);
    				player.start();

    				// ��ֹ�����㲥�����߽��������㲥 4.4�������������ض��Ų�������
    				abortBroadcast();
    			} else if ("#*location*#".equals(messageBody)) {
    				// GPS��λ����ȡ����γ��
    				// ������λ����
    				context.startService(new Intent(context, LocationService.class));

    				String location = mPref.getString("location", "get location...");
    				String phone = mPref.getString("safe_phone", "");

    				System.out.println(location);

    				// ���Ͷ��Ÿ���ȫ���루������Ȩ�ޣ�
    				SmsManager smsManager = SmsManager.getDefault();
    				smsManager.sendTextMessage(phone, null, location, null, null);

    				// ��ֹ�����㲥�����߽��������㲥 4.4�������������ض��Ų�������
    				abortBroadcast();
    			} else if ("#*lockscreen*#".equals(messageBody)) {
    				// һ���������������룩
    				// �Ƿ��Ѿ������豸
    				if (mDPM.isAdminActive(mDeviceAdminSample)) {

    					// ��������
    					mDPM.lockNow();
    					// �����������룬�����������룬0�ǲ���������Ӧ���������룩
    					mDPM.resetPassword("123456", 0);
    				}

    			} else if ("#*wipedata*#".equals(messageBody)) {
    				// �������
    				// �Ƿ��Ѿ������豸
    				if (mDPM.isAdminActive(mDeviceAdminSample)) {

    					//������ݣ��ָ���������
    					mDPM.wipeData(0);
    				}
    			}
    		}
        }
		
	}

}
