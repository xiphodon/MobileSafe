package com.gc.p01_mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.service.KillProcessService;
import com.gc.p01_mobilesafe.utils.SystemInfoUtils;
import com.gc.p01_mobilesafe.view.SettingItemView;

/**
 * 进程管理设置
 * 
 * @author guochang
 * 
 */
public class TaskManagerSettingActivity extends Activity {

	/**
	 * 显示系统进程
	 */
	private SettingItemView siv_showSystemProcess;
	/**
	 * 定时清理进程
	 */
	private SettingItemView siv_timingKillProcess;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initUI();
		initData();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_task_manager_settting);
		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

		siv_showSystemProcess = (SettingItemView) findViewById(R.id.siv_showSystemProcess);

		// 初始化"显示系统进程"选择框
		siv_showSystemProcess.setChecked(sharedPreferences.getBoolean(
				"show_system_process", true));

		// 点击监听
		siv_showSystemProcess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (siv_showSystemProcess.isChecked()) {
					siv_showSystemProcess.setChecked(false);
					sharedPreferences.edit()
							.putBoolean("show_system_process", false).commit();
				} else {
					siv_showSystemProcess.setChecked(true);
					sharedPreferences.edit()
							.putBoolean("show_system_process", true).commit();
				}
			}
		});

		siv_timingKillProcess = (SettingItemView) findViewById(R.id.siv_timingKillProcess);

		final Intent intent = new Intent(TaskManagerSettingActivity.this,
				KillProcessService.class);

		// 点击监听
		siv_timingKillProcess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (siv_timingKillProcess.isChecked()) {
					siv_timingKillProcess.setChecked(false);
					stopService(intent);
				} else {
					siv_timingKillProcess.setChecked(true);
					startService(intent);
				}
			}
		});

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// "定时清理进程"服务是否运行
		boolean serviceRuning = SystemInfoUtils.isServiceRuning(
				TaskManagerSettingActivity.this,
				"com.gc.p01_mobilesafe.service.KillProcessService");

		// 初始化"定时清理进程"选择框
		if (serviceRuning) {
			siv_timingKillProcess.setChecked(true);
		} else {
			siv_timingKillProcess.setChecked(false);
		}
	}
}
