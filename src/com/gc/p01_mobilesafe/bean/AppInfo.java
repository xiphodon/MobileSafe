package com.gc.p01_mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Ӧ����Ϣ
 * 
 * @author guochang
 * 
 */
public class AppInfo {

	/**
	 * Ӧ��ͼƬ
	 */
	private Drawable icon;
	/**
	 * Ӧ������
	 */
	private String apkName;
	/**
	 * Ӧ�ô�С
	 */
	private long apkSize;
	/**
	 * �û�Ӧ�ã�true��false��
	 */
	private boolean userApp;
	/**
	 * Ӧ�÷������ֻ��ڴ��У�true��false��
	 */
	private boolean inRom;
	/**
	 * Ӧ�ð���
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
