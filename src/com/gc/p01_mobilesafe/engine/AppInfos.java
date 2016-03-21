package com.gc.p01_mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.gc.p01_mobilesafe.bean.AppInfo;

/**
 * 获取所有应用信息
 * @author guochang
 *
 */
public class AppInfos {

	/**
	 * 获取所有应用信息的链表
	 * @param context 上下文
	 * @return List<AppInfo>所有应用信息的链表
	 */
	public static List<AppInfo> getAppInfos(Context context){
		
		List<AppInfo> appInfosList = new ArrayList<AppInfo>();
		//获取到包的管理者
		PackageManager packageManager = context.getPackageManager();
		//获得已安装的所有包
		List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			
			AppInfo appInfo = new AppInfo();
			
			//获得应用的图标
			Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
			
			//获得应用的名字
			String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
			
			//获得应用的大小
				//获得应用资源路径
			String sourceDir = packageInfo.applicationInfo.sourceDir;
			File file = new File(sourceDir);
			long apkSize = file.length();
			
			//获得应用包名
			String apkPackageName = packageInfo.packageName;
			
			appInfo.setIcon(icon);
			appInfo.setApkName(apkName);
			appInfo.setApkSize(apkSize);
			appInfo.setApkPackageName(apkPackageName);
			
			//获取到安装应用的标记
			int flags = packageInfo.applicationInfo.flags;
			//判断为系统应用还是用户应用
			if((flags & ApplicationInfo.FLAG_SYSTEM) == 0){
				//用户应用
				appInfo.setUserApp(true);
			}else{
				//系统应用
				appInfo.setUserApp(false);
			}
			
			//判断应用安装在手机内存ROM中还是SD卡中
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
				//在手机ROM中
				appInfo.setInRom(true);
			}else{
				//在SD卡中
				appInfo.setInRom(false);
			}
			
			appInfosList.add(appInfo);
			
		}
		return appInfosList;
	}
}
