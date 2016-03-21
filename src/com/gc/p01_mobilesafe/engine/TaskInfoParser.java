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
 * ��ȡ���еĵ�ǰ���̵���Ϣ
 * 
 * @author guochang
 * 
 */
public class TaskInfoParser {

	private static PackageInfo packageInfo;

	public static List<TaskInfo> getTaskInfos(Context context) {
		// ��ð�������
		PackageManager packageManager = context.getPackageManager();

		// �½�����
		List<TaskInfo> TaskInfos = new ArrayList<TaskInfo>();

		// ��ȡ�����̹�����
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		// ��ȡ���ֻ������������еĽ���
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();

		for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {

			TaskInfo taskInfo = new TaskInfo();

			// ��ȡ�����̵�����
			String processName = runningAppProcessInfo.processName;
			taskInfo.setProcessName(processName);

			try {
				// ��ȡ���ڴ������Ϣ
				/**
				 * �������һ��ֻ��һ������
				 */
				MemoryInfo[] memoryInfo = activityManager
						.getProcessMemoryInfo(new int[] { runningAppProcessInfo.pid });
				// DirtyŪ��
				// ��ȡ���ܹ�Ū������ڴ�(��ǰӦ�ó���ռ�ö����ڴ�)
				int totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty() * 1024;

				taskInfo.setMemorySize(totalPrivateDirty);

				packageInfo = packageManager.getPackageInfo(
						processName, 0);
				
				//��ȡ��Ӧ�õİ���
				String packageName = packageInfo.packageName;
				taskInfo.setPackageName(packageName);

				// ��ȡ��Ӧ�õ�����
				String appName = packageInfo.applicationInfo.loadLabel(
						packageManager).toString();

				taskInfo.setAppName(appName);

				// ��ȡ����ǰӦ�ó���ı��
				// packageInfo.applicationInfo.flags ��Ӧ�ñ��
				// ApplicationInfo.FLAG_SYSTEM ϵͳӦ�ñ��
				int flags = packageInfo.applicationInfo.flags;
				// ApplicationInfo.FLAG_SYSTEM ��ʾϵͳӦ�ó���
				if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					// ϵͳӦ��
					taskInfo.setUserApp(false);
				} else {
					// /�û�Ӧ��
					taskInfo.setUserApp(true);

				}

				// ��ȡ��ͼƬ
				Drawable icon = packageInfo.applicationInfo
						.loadIcon(packageManager);

				taskInfo.setIcon(icon);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				// ϵͳ���Ŀ�������Щϵͳû��ͼ������֡������һ��Ĭ�ϵ�ͼ�꣬û�����ֵ��ð���������
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
