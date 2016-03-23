package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.utils.SmsUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

/**
 * 高级工具
 * @author guochang
 *
 */
public class AToolsActivity extends Activity {

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		
		
	}
	
	/**
	 * 电话归属地查询
	 * @param view
	 */
	public void numberAddressQuery(View view){
		startActivity(new Intent(this, AddressActivity.class));
	}
	
	/**
	 * 短信备份
	 * @param view
	 */
	public void backUpSMS(View view){
		//初始化一个进度条对话框
		progressDialog = new ProgressDialog(AToolsActivity.this);
		progressDialog.setTitle("提示");
		progressDialog.setMessage("正在备份中，请稍后...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
		
		
		new Thread(){
			public void run() {
				boolean backUpResult = SmsUtils.backUp(AToolsActivity.this,progressDialog);
				//销毁进度框
				progressDialog.dismiss();
				
				if(backUpResult){
					Looper.prepare();
					Toast.makeText(AToolsActivity.this, "短信备份成功", Toast.LENGTH_SHORT).show();
					Looper.loop();
				}else{
					Looper.prepare();
					Toast.makeText(AToolsActivity.this, "短信备份失败", Toast.LENGTH_SHORT).show();
					Looper.loop();
				}
				
			};
		}.start();
		
	}
	
	/**
	 * 设置程序锁
	 * @param view
	 */
	public void appLock(View view){
		Intent intent = new Intent(this,AppLockActivity.class);
		startActivity(intent);
	}
}
