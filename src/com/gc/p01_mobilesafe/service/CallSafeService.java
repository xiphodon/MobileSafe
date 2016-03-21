package com.gc.p01_mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.gc.p01_mobilesafe.db.dao.BlackNumberDAO;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallSafeService extends Service {

	private SMSReceiver smsReceiver;
	private BlackNumberDAO dao;


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		dao = new BlackNumberDAO(this);
		
		//��ȡϵͳ�ĵ绰�������
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
		tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//��̬ע��sms�㲥������
		smsReceiver = new SMSReceiver();
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(smsReceiver, filter);
	}
	
	class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
//			TelephonyManager.CALL_STATE_IDLE     �绰����״̬
//		    TelephonyManager.CALL_STATE_RINGING  �绰����״̬
//		    TelephonyManager.CALL_STATE_OFFHOOK  �绰��ͨ״̬
			switch (state) {
			//�绰����״̬
			case TelephonyManager.CALL_STATE_RINGING:
				
				String mode = dao.findNumber(incomingNumber);
				
//   			 * ����ģʽ
//   			 * 1,ȫ������   �绰+����
//   			 * 2,�绰����
//   			 * 3,��������
				//ģʽΪ"1"��"2"�����ض���
				if("1".equals(mode) || "2".equals(mode)){
					System.out.println("�ҶϺ������绰");
					//�Ҷϵ绰
					myEndCall();
				
					Toast.makeText(CallSafeService.this, "���ص绰��" + incomingNumber , Toast.LENGTH_SHORT).show();
				
					
					Uri uri = Uri.parse("content://call_log/calls");
					//ע�����ݹ۲���
					getContentResolver().registerContentObserver(uri, true,new myContentObserver(new Handler(),incomingNumber));
				
				}
				
				break;
			}
		}

		
	}
	
	/**
	 * ����һ���绰��¼�����ݹ۲���
	 * @author guochang
	 *
	 */
	private class myContentObserver extends ContentObserver{

		String incomingNumber;
		
		public myContentObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
			// TODO Auto-generated constructor stub
		}
		
		
		@Override
		/**
		 * �����ݸı�ʱ����
		 */
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			
			//ע���������ݹ۲���
			getContentResolver().unregisterContentObserver(this);
			
			//ɾ���˺����������ʾ
			deleteCallLog(incomingNumber);
			
			super.onChange(selfChange);
		}
	}
	
	/**
	 * �Ҷϵ绰
	 */
	private void myEndCall() {
		// TODO Auto-generated method stub
		try {
			//ͨ��������� ���� ServiceManager
			Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
			
			//ͨ�������õ���ǰ����
			Method method = clazz.getDeclaredMethod("getService", String.class);
			
			//  aidl �� android�ӿڶ�������
			//Զ�̷���  aidl  ʹ�÷���
			//1.��Զ�̷���ķ�����ȡ��һ�������Ľӿ�xxx.java�ļ�
			//2.�ѽӿ�xxx.java�ļ��ĺ�׺��Ϊxxx.aidl
			//3.��gen�ļ������Զ����ɵ�ͬ��xxx.java�ļ��У���һ����̬������Stub�����Ѿ��̳���Binder�࣬
			//	 ��ʵ����xxx.java�ӿڣ����������µ��м���
			//4.��aidl�ļ����Ƶ������ߵ���Ŀ�У�ע�⣺aidl�ļ������ڰ���������Ҫ�ͱ����÷�����Ŀ��aidl�İ���һ��
			//  **android studio�У�Ҫ��aidl�ļ����ڵİ������� aidl�ļ����£�aidl�ļ�����java�ļ���ͬ����clean projectһ��
			//5.�ڵ�������Ŀ�У�ǿת�м��˶���ʱ��ֱ��ʹ��Stub.asInterface(Service)����
			//
			
			//�õ��м��˶���
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			
			//ʹ��aidl���Զ����ɵķ�����ǿת
			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
			//�Ҷϵ绰
			iTelephony.endCall();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * ɾ���˺����������ʾ
	 * @param incomingNumber �����ص��������
	 */
	public void deleteCallLog(String incomingNumber) {
		// TODO Auto-generated method stub
		
		Uri uri = Uri.parse("content://call_log/calls");
		getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
	}

	
	/**
	 * ��̬ע�� sms�㲥������
	 * @author guochang
	 *
	 */
	private class SMSReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
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
    			
    			if(originatingAddress.startsWith("86") && originatingAddress.length() > 11){
    				originatingAddress = originatingAddress.substring(2);
    			}
    			
    			//�鿴�ö��ź����Ƿ��ں��������¼����ģʽ
    			String mode = dao.findNumber(originatingAddress);
    			
//    			
//    			 * ����ģʽ
//    			 * 1,ȫ������   �绰+����
//    			 * 2,�绰����
//    			 * 3,��������
    			//ģʽΪ"1"��"3"�����ض���
    			if("1".equals(mode) || "3".equals(mode)){
    				System.out.println("���ض���");
    				abortBroadcast();
    				Toast.makeText(CallSafeService.this, "���ض���:" + originatingAddress, Toast.LENGTH_SHORT).show();
    			}
    			
    			//��������  (�������ݺ���"XX"�Ķ���)
    			if(messageBody.contains("�н�") && messageBody.contains("����")){
    				System.out.println("���ض���");
    				abortBroadcast();
    			}
    		}
		}
		
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
