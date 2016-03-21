package com.gc.p01_mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * 应用信息
 * 
 * @author guochang
 * 
 */
public class AppInfo {

	/**
	 * 应用图片
	 */
	private Drawable icon;
	/**
	 * 应用名字
	 */
	private String apkName;
	/**
	 * 应用大小
	 */
	private long apkSize;
	/**
	 * 用户应用（true、false）
	 */
	private boolean userApp;
	/**
	 * 应用放置在手机内存中（true、false）
	 */
	private boolean inRom;
	/**
	 * 应用包名
	 */
	private String apkPackageName;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public long getApkSize() {
		return apkSize;
	}

	public void setApkSize(long apkSize) {
		this.apkSize = apkSize;
	}

	public boolean isUserApp() {
		return userApp;
	}

	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}

	public boolean isInRom() {
		return inRom;
	}

	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}

	public String getApkPackageName() {
		return apkPackageName;
	}

	public void setApkPackageName(String apkPackageName) {
		this.apkPackageName = apkPackageName;
	}

	@Override
	public String toString() {
		return "AppInfo [apkName=" + apkName + ", apkSize=" + apkSize
				+ ", userApp=" + userApp + ", inRom=" + inRom
				+ ", apkPackageName=" + apkPackageName + "]";
	}

	
}
