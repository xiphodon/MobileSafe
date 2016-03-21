package com.gc.p01_mobilesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * ��ʱ������̷���
 * @author guochang
 *
 */
public class KillProcessService extends Service {

	private LockScreenReceiver lockScreenReceiver;


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		//ע��һ�������㲥������
		lockScreenReceiver = new LockScreenReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(lockScreenReceiver, filter);
		
//		//��ʱ��
//		Timer timer = new Timer();
//		
//		//��ʱ������
//		TimerTask timerTask = new TimerTask() {
//			
//			@Override
//			public void run() {
//				// ��ʱ�����õ�ҵ���߼�
//				
//			}
//		};
//		//5�����ʱ����ʼ������ÿ1000�������һ�¼�ʱ����
//		timer.schedule(timerTask, 5, 1000);
	}
	
	
	private class LockScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			//��ý��̹�����
			ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
			//��õ�ǰ���еĽ���
			List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
			for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
				//ɱ�����н��̣������Լ���
				if(!runningAppProcessInfo.processName.equals(context.getApplicationInfo().processName)){
					activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
				}
			}
		}
		
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//��������㲥������
		unregisterReceiver(lockScreenReceiver);
		lockScreenReceiver = null;
	}

}
