package com.gc.p01_mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * ϵͳ������
 * 
 * @author guochang
 * 
 */
public class SystemInfoUtils {

	private static long totalMem;

	/**
	 * �������Ƿ���������
	 * 
	 * @return
	 */
	public static boolean isServiceRuning(Context context, String serviceName) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		// �����������Ϊ100���ķ���
		List<RunningServiceInfo> runningServices = activityManager
				.getRunningServices(100);

		for (RunningServiceInfo runningServiceInfo : runningServices) {
			// ��ȡ��������
			String className = runningServiceInfo.service.getClassName();
			// System.out.println(className);
			if (className.equals(serviceName)) {
				// �������
				return true;
			}
		}
		return false;
	}

	/**
	 * ��õ�ǰ���еĽ�������
	 * 
	 * @param context
	 *            ������
	 * @return ��ǰ���еĽ�������
	 */
	public static int getProcessCount(Context context) {

		// ������������̹�����������������������������������������������packageManager������������
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		// ��ǰ���еĽ���
		List<RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();
		// ��ǰ���еĽ��̵�����
		return runningAppProcesses.size();
	}

	/**
	 * ��ȡ��ǰ��Ч�ڴ棨ʣ���ڴ棩
	 * 
	 * @param context
	 * @return
	 */
	public static long getAvailMem(Context context) {
		// ������������̹�����������������������������������������������packageManager������������
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		// ʵ�����ڴ���Ϣ
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		// ��ȡ��ǰ���̹����������µ��ڴ���Ϣ
		activityManager.getMemoryInfo(memoryInfo);

		// ��Ч�ڴ棨ʣ���ڴ棩
		return memoryInfo.availMem;
	}

	public static long getTotalMem(Context context) {
		/*
		 * // ���ڴ� long (�Ͱ汾��֧��,����ʵ��) // totalMem = memoryInfo.totalMem;
		 */

		/**
		 * ������ͨ����ȡ"/proc/cupinfo"����ȡandroid�ֻ���CPU������
		 * ͨ����ȡ"/proc/stat"�ļ�������CPU��ʹ����
		 */
		try {
			FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));

			String readLine = reader.readLine();
			// "\s":ƥ���κβ��ɼ��ַ��������ո��Ʊ������ҳ���ȵȡ��ȼ���[ \f\n\r\t\v]��
			String[] split = readLine.split("\\s+");

			totalMem = Long.parseLong(split[1]) * 1024;

			reader.close();
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalMem;
	}

}
