package com.gc.p01_mobilesafe.service;

import java.util.List;

import com.gc.p01_mobilesafe.activity.EnterPwdActivity;
import com.gc.p01_mobilesafe.db.dao.AppLockDAO;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

/**
 * 看门狗服务，检查当前打开应用是否为加锁应用
 * 
 * @author guochang
 * 
 */
public class WatchDogService extends Service {

	private ActivityManager activityManager;
	private AppLockDAO appLockDAO;
	private boolean flag = true;
	// 临时停止保护的包名
	private String tempStopProtectPackageName;
	private WatchDogReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		// 获得到进程管理器
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// 软件锁DAO
		appLockDAO = new AppLockDAO(this);

		// 注册广播接受者
		receiver = new WatchDogReceiver();
		IntentFilter filter = new IntentFilter();
		// 停止保护
		filter.addAction("com.gc.p01_mobilesafe.stopprotect");

		// 注册一个锁屏的广播
		/**
		 * 当屏幕锁住的时候。让狗休息 屏幕解锁的时候。让狗工作
		 */
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		filter.addAction(Intent.ACTION_SCREEN_ON);

		registerReceiver(receiver, filter);

		startWatchDog();
	}

	/**
	 * 软件看门狗
	 */
	private void startWatchDog() {

		new Thread() {
			public void run() {

				while (flag) {
					SharedPreferences mPref = getSharedPreferences("config",
							MODE_PRIVATE);
					String applock_pwd = mPref.getString("applock_pwd", "");

					if (!TextUtils.isEmpty(applock_pwd)) {
						// 获得当前正在运行的任务栈
						List<RunningTaskInfo> runningTasks = activityManager
								.getRunningTasks(1);
						// 获得当前正在运行的进程的栈
						RunningTaskInfo runningTaskInfo = runningTasks.get(0);
						// 获得栈顶的的应用的包名
						String packageName = runningTaskInfo.topActivity
								.getPackageName();

						SystemClock.sleep(50);

						if (appLockDAO.find(packageName)
								&& !TextUtils.equals(packageName,
										tempStopProtectPackageName)) {
							// 已加锁
							Intent intent = new Intent(WatchDogService.this,
									EnterPwdActivity.class);
							// 服务跳转界面，需要flag（new 任务栈）
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							// 停止保护的对象
							intent.putExtra("packageName", packageName);
							startActivity(intent);

						}
					}
				}
			};
		}.start();

	}

	/**
	 * 看门狗广播接受者
	 * 
	 * @author guochang
	 * 
	 */
	private class WatchDogReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("com.gc.p01_mobilesafe.stopprotect")) {
				//获取到停止保护的对象

				tempStopProtectPackageName = intent
						.getStringExtra("packageName");
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				//清空临时停止保存对象
				tempStopProtectPackageName = null;
				// 让狗休息
				flag = false;
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				// 让狗继续干活
				flag = true;
				startWatchDog();
			}

		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		flag = false;
		unregisterReceiver(receiver);
		receiver = null;
	}

}
