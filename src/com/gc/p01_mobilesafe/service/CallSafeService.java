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
		
		//获取系统的电话号码服务
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
		tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//动态注册sms广播接受者
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
//			TelephonyManager.CALL_STATE_IDLE     电话闲置状态
//		    TelephonyManager.CALL_STATE_RINGING  电话响铃状态
//		    TelephonyManager.CALL_STATE_OFFHOOK  电话接通状态
			switch (state) {
			//电话响铃状态
			case TelephonyManager.CALL_STATE_RINGING:
				
				String mode = dao.findNumber(incomingNumber);
				
//   			 * 拦截模式
//   			 * 1,全部拦截   电话+短信
//   			 * 2,电话拦截
//   			 * 3,短信拦截
				//模式为"1"或"2"则拦截短信
				if("1".equals(mode) || "2".equals(mode)){
					System.out.println("挂断黑名单电话");
					//挂断电话
					myEndCall();
				
					Toast.makeText(CallSafeService.this, "拦截电话：" + incomingNumber , Toast.LENGTH_SHORT).show();
				
					
					Uri uri = Uri.parse("content://call_log/calls");
					//注册内容观察者
					getContentResolver().registerContentObserver(uri, true,new myContentObserver(new Handler(),incomingNumber));
				
				}
				
				break;
			}
		}

		
	}
	
	/**
	 * 创建一个电话记录的内容观察者
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
		 * 当数据改变时调用
		 */
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			
			//注销掉此内容观察者
			getContentResolver().unregisterContentObserver(this);
			
			//删除此号码的来电显示
			deleteCallLog(incomingNumber);
			
			super.onChange(selfChange);
		}
	}
	
	/**
	 * 挂断电话
	 */
	private void myEndCall() {
		// TODO Auto-generated method stub
		try {
			//通过类加载器 加载 ServiceManager
			Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
			
			//通过反射拿到当前方法
			Method method = clazz.getDeclaredMethod("getService", String.class);
			
			//  aidl ： android接口定义语言
			//远程服务  aidl  使用方法
			//1.把远程服务的方法抽取成一个单独的接口xxx.java文件
			//2.把接口xxx.java文件的后缀改为xxx.aidl
			//3.在gen文件夹中自动生成的同名xxx.java文件中，有一个静态抽象类Stub，它已经继承了Binder类，
			//	 并实现了xxx.java接口，这个类就是新的中间人
			//4.把aidl文件复制到调用者的项目中，注意：aidl文件的所在包名，必须要和被调用服务项目中aidl的包名一致
			//  **android studio中，要把aidl文件所在的包名放在 aidl文件夹下，aidl文件夹与java文件夹同级，clean project一下
			//5.在调用者项目中，强转中间人对象时，直接使用Stub.asInterface(Service)方法
			//
			
			//拿到中间人对象
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			
			//使用aidl中自动生成的方法来强转
			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
			//挂断电话
			iTelephony.endCall();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除此号码的来电显示
	 * @param incomingNumber 被拦截的来电号码
	 */
	public void deleteCallLog(String incomingNumber) {
		// TODO Auto-generated method stub
		
		Uri uri = Uri.parse("content://call_log/calls");
		getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
	}

	
	/**
	 * 动态注册 sms广播接受者
	 * @author guochang
	 *
	 */
	private class SMSReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 拿到短信的信息
    		// 短信信息封装在intent中
    		Bundle bundle = intent.getExtras();
    		// 以pdus（协议数据单元）为键，取出一个object数组，数组中的每一个元素都是一条短信
    		Object[] objects = (Object[]) bundle.get("pdus");
    		
    		// 拿到广播中的所有的短信
    		for (Object object : objects) {
    			// 通过pdu来构造短信
    			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);

    			// 短信来源号码
    			String originatingAddress = sms.getOriginatingAddress();
    			// 短信内容
    			String messageBody = sms.getMessageBody();
    			
    			if(originatingAddress.startsWith("86") && originatingAddress.length() > 11){
    				originatingAddress = originatingAddress.substring(2);
    			}
    			
    			//查看该短信号码是否在黑名单里记录拦截模式
    			String mode = dao.findNumber(originatingAddress);
    			
//    			
//    			 * 拦截模式
//    			 * 1,全部拦截   电话+短信
//    			 * 2,电话拦截
//    			 * 3,短信拦截
    			//模式为"1"或"3"则拦截短信
    			if("1".equals(mode) || "3".equals(mode)){
    				System.out.println("拦截短信");
    				abortBroadcast();
    				Toast.makeText(CallSafeService.this, "拦截短信:" + originatingAddress, Toast.LENGTH_SHORT).show();
    			}
    			
    			//智能拦截  (短信内容含有"XX"的短信)
    			if(messageBody.contains("中奖") && messageBody.contains("银行")){
    				System.out.println("拦截短信");
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
