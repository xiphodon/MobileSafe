package com.gc.p01_mobilesafe.bean;

/**
 * 黑名单bean
 * @author Administrator
 *
 */
public class BlackNumberInfo {

	/**
	 * 拦截号码
	 */
	private String number;
	
	/**
	 * 拦截模式
	 * 1,全部拦截   电话+短信
	 * 2,电话拦截
	 * 3,短信拦截
	 */
	private String mode;

	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
}
