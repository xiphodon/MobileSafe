package com.gc.p01_mobilesafe.bean;

/**
 * ������bean
 * @author Administrator
 *
 */
public class BlackNumberInfo {

	/**
	 * ���غ���
	 */
	private String number;
	
	/**
	 * ����ģʽ
	 * 1,ȫ������   �绰+����
	 * 2,�绰����
	 * 3,��������
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
