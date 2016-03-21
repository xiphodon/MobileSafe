package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 手机防盗页面
 * 
 * @author guochang
 * 
 */
public class LostFindActivity extends Activity {

	private SharedPreferences mPref;
	private TextView tv_safePhone;
	private ImageView iv_protect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		// 是否配置过 设置向导
		boolean configed = mPref.getBoolean("configed", false);

		if (configed) {
			setContentView(R.layout.activity_lost_find);
			
			//显示安全号码
			tv_safePhone = (TextView) findViewById(R.id.tv_safePhone);
			String safe_phone = mPref.getString("safe_phone", "");
			if(!TextUtils.isEmpty(safe_phone)){
				tv_safePhone.setText(safe_phone);
			}
			
			//显示是否开启安全保护(图片)
			iv_protect = (ImageView) findViewById(R.id.iv_protect);
			boolean protect = mPref.getBoolean("protect", false);
			if(protect){
				iv_protect.setImageResource(R.drawable.lock);
			}else{
				iv_protect.setImageResource(R.drawable.unlock);
			}

		} else {
			// 跳转到设置向导页
			startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
			
			finish();
		}

	}
	
	/**
	 * 重新进入设置向导
	 * @param view
	 */
	public void reEnter(View view){
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}
	
}
