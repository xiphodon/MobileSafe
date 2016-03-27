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
 * ���Ź����񣬼�鵱ǰ��Ӧ���Ƿ�Ϊ����Ӧ��
 * 
 * @author guochang
 * 
 */
public class WatchDogService extends Service {

	private ActivityManager activityManager;
	private AppLockDAO appLockDAO;
	private boolean flag = true;
	// ��ʱֹͣ�����İ���
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

		// ��õ����̹�����
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// �����DAO
		appLockDAO = new AppLockDAO(this);

		// ע��㲥������
		receiver = new WatchDogReceiver();
		IntentFilter filter = new IntentFilter();
		// ֹͣ����
		filter.addAction("com.gc.p01_mobilesafe.stopprotect");

		// ע��һ�������Ĺ㲥
		/**
		 * ����Ļ��ס��ʱ���ù���Ϣ ��Ļ������ʱ���ù�����
		 */
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		filter.addAction(Intent.ACTION_SCREEN_ON);

		registerReceiver(receiver, filter);

		startWatchDog();
	}

	/**
	 * ������Ź�
	 */
	private void startWatchDog() {

		new Thread() {
			public void run() {

				while (flag) {
					SharedPreferences mPref = getSharedPreferences("config",
							MODE_PRIVATE);
					String applock_pwd = mPref.getString("applock_pwd", "");

					if (!TextUtils.isEmpty(applock_pwd)) {
						// ��õ�ǰ�������е�����ջ
						List<RunningTaskInfo> runningTasks = activityManager
								.getRunningTasks(1);
						// ��õ�ǰ�������еĽ��̵�ջ
						RunningTaskInfo runningTaskInfo = runningTasks.get(0);
						// ���ջ���ĵ�Ӧ�õİ���
						String packageName = runningTaskInfo.topActivity
								.getPackageName();

						SystemClock.sleep(50);

						if (appLockDAO.find(packageName)
								&& !TextUtils.equals(packageName,
										tempStopProtectPackageName)) {
							// �Ѽ���
							Intent intent = new Intent(WatchDogService.this,
									EnterPwdActivity.class);
							// ������ת���棬��Ҫflag��new ����ջ��
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							// ֹͣ�����Ķ���
							intent.putExtra("packageName", packageName);
							startActivity(intent);

						}
					}
				}
			};
		}.start();

	}

	/**
	 * ���Ź��㲥������
	 * 
	 * @author guochang
	 * 
	 */
	private class WatchDogReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("com.gc.p01_mobilesafe.stopprotect")) {
				//��ȡ��ֹͣ�����Ķ���

				tempStopProtectPackageName = intent
						.getStringExtra("packageName");
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				//�����ʱֹͣ�������
				tempStopProtectPackageName = null;
				// �ù���Ϣ
				flag = false;
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				// �ù������ɻ�
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
