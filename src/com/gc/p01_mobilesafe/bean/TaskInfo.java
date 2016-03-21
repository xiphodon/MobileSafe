package com.gc.p01_mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * 进程信息
 * @author guochang
 *
 */

public class TaskInfo {
	private Drawable icon;
	private String packageName;
	private String processName;
	private String appName;
	private long memorySize;
	private boolean userApp;
	private boolean checked;
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public long getMemorySize() {
		return memorySize;
	}
	public void setMemorySize(long memorySize) {
		this.memorySize = memorySize;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	@Override
	public String toString() {
		return "TaskInfo [packageName=" + packageName + ", processName="
				+ processName + ", appName=" + appName + ", memorySize="
				+ memorySize + ", userApp=" + userApp + "]";
	}
	
	
}
