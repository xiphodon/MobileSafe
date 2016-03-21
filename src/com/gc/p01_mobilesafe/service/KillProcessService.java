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
 * 定时清理进程服务
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
		
		//注册一个锁屏广播接收者
		lockScreenReceiver = new LockScreenReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(lockScreenReceiver, filter);
		
//		//定时器
//		Timer timer = new Timer();
//		
//		//计时器任务
//		TimerTask timerTask = new TimerTask() {
//			
//			@Override
//			public void run() {
//				// 定时器调用的业务逻辑
//				
//			}
//		};
//		//5毫秒后定时器开始工作，每1000毫秒调用一下计时任务
//		timer.schedule(timerTask, 5, 1000);
	}
	
	
	private class LockScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			//获得进程管理器
			ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
			//获得当前运行的进程
			List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
			for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
				//杀死所有进程（除了自己）
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
		//解除锁屏广播接收者
		unregisterReceiver(lockScreenReceiver);
		lockScreenReceiver = null;
	}

}
