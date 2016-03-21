package com.gc.p01_mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 清理所有进程的广播接收者
 * @author guochang
 *
 */
public class KillAllProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		//拿到进程管理者
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		//获得当前运行的所有进程
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
	
		//清理所有进程
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
		}
		
		Toast.makeText(context, "清理完毕，当前运行状态良好~", Toast.LENGTH_SHORT).show();
	}

}
