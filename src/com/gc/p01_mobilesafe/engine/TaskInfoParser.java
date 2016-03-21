package com.gc.p01_mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import android.text.TextUtils;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.bean.TaskInfo;

/**
 * 获取所有的当前进程的信息
 * 
 * @author guochang
 * 
 */
public class TaskInfoParser {

	private static PackageInfo packageInfo;

	public static List<TaskInfo> getTaskInfos(Context context) {
		// 获得包管理器
		PackageManager packageManager = context.getPackageManager();

		// 新建链表
		List<TaskInfo> TaskInfos = new ArrayList<TaskInfo>();

		// 获取到进程管理器
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		// 获取到手机上面所有运行的进程
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();

		for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {

			TaskInfo taskInfo = new TaskInfo();

			// 获取到进程的名字
			String processName = runningAppProcessInfo.processName;
			taskInfo.setProcessName(processName);

			try {
				// 获取到内存基本信息
				/**
				 * 这个里面一共只有一个数据
				 */
				MemoryInfo[] memoryInfo = activityManager
						.getProcessMemoryInfo(new int[] { runningAppProcessInfo.pid });
				// Dirty弄脏
				// 获取到总共弄脏多少内存(当前应用程序占用多少内存)
				int totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty() * 1024;

				taskInfo.setMemorySize(totalPrivateDirty);

				packageInfo = packageManager.getPackageInfo(
						processName, 0);
				
				//获取到应用的包名
				String packageName = packageInfo.packageName;
				taskInfo.setPackageName(packageName);

				// 获取到应用的名字
				String appName = packageInfo.applicationInfo.loadLabel(
						packageManager).toString();

				taskInfo.setAppName(appName);

				// 获取到当前应用程序的标记
				// packageInfo.applicationInfo.flags 该应用标记
				// ApplicationInfo.FLAG_SYSTEM 系统应用标记
				int flags = packageInfo.applicationInfo.flags;
				// ApplicationInfo.FLAG_SYSTEM 表示系统应用程序
				if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					// 系统应用
					taskInfo.setUserApp(false);
				} else {
					// /用户应用
					taskInfo.setUserApp(true);

				}

				// 获取到图片
				Drawable icon = packageInfo.applicationInfo
						.loadIcon(packageManager);

				taskInfo.setIcon(icon);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				// 系统核心库里面有些系统没有图标和名字。必须给一个默认的图标，没有名字的用包名做名字
				taskInfo.setIcon(context.getResources().getDrawable(
						R.drawable.ic_android_launcher));
				if(TextUtils.isEmpty(taskInfo.getAppName())){
					taskInfo.setAppName(processName);
				}
			}

			TaskInfos.add(taskInfo);
		}

		return TaskInfos;
	}

}
