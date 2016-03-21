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
 * 系统工具类
 * 
 * @author guochang
 * 
 */
public class SystemInfoUtils {

	private static long totalMem;

	/**
	 * 检测服务是否正在运行
	 * 
	 * @return
	 */
	public static boolean isServiceRuning(Context context, String serviceName) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		// 返回最大数量为100个的服务
		List<RunningServiceInfo> runningServices = activityManager
				.getRunningServices(100);

		for (RunningServiceInfo runningServiceInfo : runningServices) {
			// 获取服务名称
			String className = runningServiceInfo.service.getClassName();
			// System.out.println(className);
			if (className.equals(serviceName)) {
				// 服务存在
				return true;
			}
		}
		return false;
	}

	/**
	 * 获得当前运行的进程总数
	 * 
	 * @param context
	 *            上下文
	 * @return 当前运行的进程总数
	 */
	public static int getProcessCount(Context context) {

		// 活动管理器（进程管理器）——————————————————区别：packageManager（包管理器）
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		// 当前运行的进程
		List<RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();
		// 当前运行的进程的数量
		return runningAppProcesses.size();
	}

	/**
	 * 获取当前有效内存（剩余内存）
	 * 
	 * @param context
	 * @return
	 */
	public static long getAvailMem(Context context) {
		// 活动管理器（进程管理器）——————————————————区别：packageManager（包管理器）
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		// 实例化内存信息
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		// 获取当前进程管理器掌握下的内存信息
		activityManager.getMemoryInfo(memoryInfo);

		// 有效内存（剩余内存）
		return memoryInfo.availMem;
	}

	public static long getTotalMem(Context context) {
		/*
		 * // 总内存 long (低版本不支持,下面实现) // totalMem = memoryInfo.totalMem;
		 */

		/**
		 * 还可以通过读取"/proc/cupinfo"来获取android手机的CPU参数，
		 * 通过读取"/proc/stat"文件来计算CPU的使用率
		 */
		try {
			FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));

			String readLine = reader.readLine();
			// "\s":匹配任何不可见字符，包括空格、制表符、换页符等等。等价于[ \f\n\r\t\v]。
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
