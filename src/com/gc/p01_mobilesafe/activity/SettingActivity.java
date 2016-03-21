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
 * 设置中心界面
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
		
		//存储程序配置信息
		mPref = getSharedPreferences("config", MODE_PRIVATE);

		initUpdateView();
		initAddressView();
		initAddressStyle();
		initAddressLocation();
		initBlackNumberView();
	}
	

	/**
	 * 初始化自动更新开关
	 */
	public void initUpdateView(){
		
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
//		stv_update.setTitle("自动更新忽略设置");
		
		boolean autoUpdate = mPref.getBoolean("auto_update", false);
		//读取配置信息
		if(autoUpdate){
			siv_update.setChecked(true);
//			stv_update.setDesc("自动更新已忽略");
		}else{
			siv_update.setChecked(false);
//			stv_update.setDesc("自动更新已开启");
		}
		
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//判断复选框是否勾选,并设置
				if(siv_update.isChecked()){
					siv_update.setChecked(false);
//					stv_update.setDesc("自动更新已开启");
					
					mPref.edit().putBoolean("auto_update", false).commit();
				}else{
					siv_update.setChecked(true);
//					stv_update.setDesc("自动更新已忽略");
					
					mPref.edit().putBoolean("auto_update", true).commit();
				}
			}
		});
		
	}
	
	/**
	 * 初始化归属地开关
	 */
	public void initAddressView(){
		
		siv_addressShow = (SettingItemView) findViewById(R.id.siv_addressShow);

		//归属地显示服务是否运行
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
					//停止归属地服务
					stopService(new Intent(SettingActivity.this, AddressService.class));
				}else{
					siv_addressShow.setChecked(true);
					//开启归属地服务
					startService(new Intent(SettingActivity.this, AddressService.class));
				}
			}
		});
	}
	
	
	//设置单选框选项
	final String[] items = new String[]{"半透明","活力橙","天空蓝","金属灰","苹果绿"};
	
	/**
	 * 初始化归属地浮窗显示风格
	 */
	public void initAddressStyle(){
		siav_addressStyle = (SettingItemArrowView) findViewById(R.id.siav_addressStyle);
		
		//读取保存的style
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
	 * 弹出选择风格的单选框
	 */
	protected void showSingleChooseDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher_small);
		builder.setTitle("归属地悬浮框风格");
		
		//读取保存的style
		int address_style = mPref.getInt("address_style", 0);
		
		builder.setSingleChoiceItems(items, address_style, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//保存选择的悬浮框风格
				mPref.edit().putInt("address_style", which).commit();
				//更新描述
				siav_addressStyle.setDesc(items[which]);
				//选择后单选框消失
				dialog.dismiss();
			}
		});
		
		//设置取消按钮
		builder.setNegativeButton("取消", null);
		builder.show();
	}
	
	/**
	 * 初始化归属地悬浮窗位置
	 */
	public void initAddressLocation(){
		siav_addressLocation = (SettingItemArrowView) findViewById(R.id.siav_addressLocation);
		siav_addressLocation.setDesc("设置归属地提示悬浮框的屏幕位置");
		
		siav_addressLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SettingActivity.this, DragAddressViewActivity.class));
			}
		});
		
	}
	
	

	/**
	 * 初始化黑名单拦截设置
	 */
	private void initBlackNumberView() {
		// TODO Auto-generated method stub

		siv_callsafe = (SettingItemView) findViewById(R.id.siv_callsafe);

		//归属地显示服务是否运行
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
					//停止归属地服务
					stopService(new Intent(SettingActivity.this, CallSafeService.class));
				}else{
					siv_callsafe.setChecked(true);
					//开启归属地服务
					startService(new Intent(SettingActivity.this, CallSafeService.class));
				}
			}
		});
	}
	
	
	
	
}
