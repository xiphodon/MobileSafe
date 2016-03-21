package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.service.AddressService;
import com.gc.p01_mobilesafe.service.CallSafeService;
import com.gc.p01_mobilesafe.utils.SystemInfoUtils;
import com.gc.p01_mobilesafe.view.SettingItemArrowView;
import com.gc.p01_mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


/**
 * �������Ľ���
 * @author guochang
 *
 */
public class SettingActivity extends Activity {

	private SettingItemView siv_update;
	private SettingItemView siv_addressShow;
	private SettingItemView siv_callsafe;
	private SettingItemArrowView siav_addressStyle;
	private SettingItemArrowView siav_addressLocation;
	private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		//�洢����������Ϣ
		mPref = getSharedPreferences("config", MODE_PRIVATE);

		initUpdateView();
		initAddressView();
		initAddressStyle();
		initAddressLocation();
		initBlackNumberView();
	}
	

	/**
	 * ��ʼ���Զ����¿���
	 */
	public void initUpdateView(){
		
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
//		stv_update.setTitle("�Զ����º�������");
		
		boolean autoUpdate = mPref.getBoolean("auto_update", false);
		//��ȡ������Ϣ
		if(autoUpdate){
			siv_update.setChecked(true);
//			stv_update.setDesc("�Զ������Ѻ���");
		}else{
			siv_update.setChecked(false);
//			stv_update.setDesc("�Զ������ѿ���");
		}
		
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//�жϸ�ѡ���Ƿ�ѡ,������
				if(siv_update.isChecked()){
					siv_update.setChecked(false);
//					stv_update.setDesc("�Զ������ѿ���");
					
					mPref.edit().putBoolean("auto_update", false).commit();
				}else{
					siv_update.setChecked(true);
//					stv_update.setDesc("�Զ������Ѻ���");
					
					mPref.edit().putBoolean("auto_update", true).commit();
				}
			}
		});
		
	}
	
	/**
	 * ��ʼ�������ؿ���
	 */
	public void initAddressView(){
		
		siv_addressShow = (SettingItemView) findViewById(R.id.siv_addressShow);

		//��������ʾ�����Ƿ�����
		boolean serviceRuning = SystemInfoUtils.isServiceRuning(SettingActivity.this,"com.gc.p01_mobilesafe.service.AddressService");
		
		if(serviceRuning){
			siv_addressShow.setChecked(true);
		}else{
			siv_addressShow.setChecked(false);
		}
		
		siv_addressShow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(siv_addressShow.isChecked()){
					siv_addressShow.setChecked(false);
					//ֹͣ�����ط���
					stopService(new Intent(SettingActivity.this, AddressService.class));
				}else{
					siv_addressShow.setChecked(true);
					//���������ط���
					startService(new Intent(SettingActivity.this, AddressService.class));
				}
			}
		});
	}
	
	
	//���õ�ѡ��ѡ��
	final String[] items = new String[]{"��͸��","������","�����","������","ƻ����"};
	
	/**
	 * ��ʼ�������ظ�����ʾ���
	 */
	public void initAddressStyle(){
		siav_addressStyle = (SettingItemArrowView) findViewById(R.id.siav_addressStyle);
		
		//��ȡ�����style
		int address_style = mPref.getInt("address_style", 0);
		siav_addressStyle.setDesc(items[address_style]);
		
		siav_addressStyle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSingleChooseDialog();
			}
		});
	}

	/**
	 * ����ѡ����ĵ�ѡ��
	 */
	protected void showSingleChooseDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher_small);
		builder.setTitle("��������������");
		
		//��ȡ�����style
		int address_style = mPref.getInt("address_style", 0);
		
		builder.setSingleChoiceItems(items, address_style, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//����ѡ�����������
				mPref.edit().putInt("address_style", which).commit();
				//��������
				siav_addressStyle.setDesc(items[which]);
				//ѡ���ѡ����ʧ
				dialog.dismiss();
			}
		});
		
		//����ȡ����ť
		builder.setNegativeButton("ȡ��", null);
		builder.show();
	}
	
	/**
	 * ��ʼ��������������λ��
	 */
	public void initAddressLocation(){
		siav_addressLocation = (SettingItemArrowView) findViewById(R.id.siav_addressLocation);
		siav_addressLocation.setDesc("���ù�������ʾ���������Ļλ��");
		
		siav_addressLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SettingActivity.this, DragAddressViewActivity.class));
			}
		});
		
	}
	
	

	/**
	 * ��ʼ����������������
	 */
	private void initBlackNumberView() {
		// TODO Auto-generated method stub

		siv_callsafe = (SettingItemView) findViewById(R.id.siv_callsafe);

		//��������ʾ�����Ƿ�����
		boolean serviceRuning = SystemInfoUtils.isServiceRuning(SettingActivity.this,"com.gc.p01_mobilesafe.service.CallSafeService");
		
		if(serviceRuning){
			siv_callsafe.setChecked(true);
		}else{
			siv_callsafe.setChecked(false);
		}
		
		siv_callsafe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(siv_callsafe.isChecked()){
					siv_callsafe.setChecked(false);
					//ֹͣ�����ط���
					stopService(new Intent(SettingActivity.this, CallSafeService.class));
				}else{
					siv_callsafe.setChecked(true);
					//���������ط���
					startService(new Intent(SettingActivity.this, CallSafeService.class));
				}
			}
		});
	}
	
	
	
	
}
