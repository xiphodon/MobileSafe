package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.receiver.AdminReceiver;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 手机防盗 设置向导 第4页
 * 
 * @author guochang
 * 
 */
public class Setup4Activity extends BaseSetupActivity {
	
	private CheckBox cb_protect;
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		
		//获取设备策略服务
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        //设备管理组件
        mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);
		
		cb_protect = (CheckBox) findViewById(R.id.cb_protect);
		
		//根据sp存储的状态更新checkbox
		boolean protect = mPref.getBoolean("protect", false);
		if(protect){
			cb_protect.setText("防盗保护已开启");
			cb_protect.setChecked(true);
		}else{
			cb_protect.setText("防盗保护未开启");
			cb_protect.setChecked(false);
		}
		
		cb_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					
					//激活设备管理器
					if (!mDPM.isAdminActive(mDeviceAdminSample)){
						 Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				    	
				         intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
				         intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"hello~~,设备管理器");
				         startActivity(intent);
					}
					
					if(mDPM.isAdminActive(mDeviceAdminSample)){
						cb_protect.setText("防盗保护已开启");
						mPref.edit().putBoolean("protect", true).commit();
					}else{
						cb_protect.setText("防盗保护未开启");
						cb_protect.setChecked(false);
					}
					
				}else{
					cb_protect.setText("防盗保护未开启");
					mPref.edit().putBoolean("protect", false).commit();
					
					//取消激活设备管理器
					if(mDPM.isAdminActive(mDeviceAdminSample)){
			    		mDPM.removeActiveAdmin(mDeviceAdminSample);
			    	}
				}
			}
		});
	}

	@Override
	public void touchNext() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, LostFindActivity.class));
		finish();

		// 页面跳转动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

		// 更新sp，之后不再进入向导页面
		mPref.edit().putBoolean("configed", true).commit();
	}

	@Override
	public void touchPrevious() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		// 页面跳转动画
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}
}
