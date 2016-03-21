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
 * ��ȡ����Ӧ����Ϣ
 * @author guochang
 *
 */
public class AppInfos {

	/**
	 * ��ȡ����Ӧ����Ϣ������
	 * @param context ������
	 * @return List<AppInfo>����Ӧ����Ϣ������
	 */
	public static List<AppInfo> getAppInfos(Context context){
		
		List<AppInfo> appInfosList = new ArrayList<AppInfo>();
		//��ȡ�����Ĺ�����
		PackageManager packageManager = context.getPackageManager();
		//����Ѱ�װ�����а�
		List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			
			AppInfo appInfo = new AppInfo();
			
			//���Ӧ�õ�ͼ��
			Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
			
			//���Ӧ�õ�����
			String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
			
			//���Ӧ�õĴ�С
				//���Ӧ����Դ·��
			String sourceDir = packageInfo.applicationInfo.sourceDir;
			File file = new File(sourceDir);
			long apkSize = file.length();
			
			//���Ӧ�ð���
			String apkPackageName = packageInfo.packageName;
			
			appInfo.setIcon(icon);
			appInfo.setApkName(apkName);
			appInfo.setApkSize(apkSize);
			appInfo.setApkPackageName(apkPackageName);
			
			//��ȡ����װӦ�õı��
			int flags = packageInfo.applicationInfo.flags;
			//�ж�ΪϵͳӦ�û����û�Ӧ��
			if((flags & ApplicationInfo.FLAG_SYSTEM) == 0){
				//�û�Ӧ��
				appInfo.setUserApp(true);
			}else{
				//ϵͳӦ��
				appInfo.setUserApp(false);
			}
			
			//�ж�Ӧ�ð�װ���ֻ��ڴ�ROM�л���SD����
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
				//���ֻ�ROM��
				appInfo.setInRom(true);
			}else{
				//��SD����
				appInfo.setInRom(false);
			}
			
			appInfosList.add(appInfo);
			
		}
		return appInfosList;
	}
}
