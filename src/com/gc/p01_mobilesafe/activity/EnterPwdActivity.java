package com.gc.p01_mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.p01_mobilesafe.R;

/**
 * 软件锁输入密码界面
 * @author guochang
 *
 */
public class EnterPwdActivity extends Activity {

	private EditText et_pwd;
	private Button bt_0;
	private Button bt_1;
	private Button bt_2;
	private Button bt_3;
	private Button bt_4;
	private Button bt_5;
	private Button bt_6;
	private Button bt_7;
	private Button bt_8;
	private Button bt_9;
	private Button bt_clean_all;
	private Button bt_ok;
	private Button bt_delete;
	private TextView tv_title;
	private SharedPreferences mPref;
	private String packageName;
	private String applock_pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_number);
		initUI();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		
		Intent intent = getIntent();
		if (intent != null) {
			packageName = intent.getStringExtra("packageName");
		}
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		bt_0 = (Button) findViewById(R.id.bt_0);
		bt_1 = (Button) findViewById(R.id.bt_1);
		bt_2 = (Button) findViewById(R.id.bt_2);
		bt_3 = (Button) findViewById(R.id.bt_3);
		bt_4 = (Button) findViewById(R.id.bt_4);
		bt_5 = (Button) findViewById(R.id.bt_5);
		bt_6 = (Button) findViewById(R.id.bt_6);
		bt_7 = (Button) findViewById(R.id.bt_7);
		bt_8 = (Button) findViewById(R.id.bt_8);
		bt_9 = (Button) findViewById(R.id.bt_9);
		bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_delete = (Button) findViewById(R.id.bt_delete);
		bt_clean_all = (Button) findViewById(R.id.bt_clean_all);
		
		//隐藏输入法，防止系统或第三方输入法弹出
		et_pwd.setInputType(InputType.TYPE_NULL);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		applock_pwd = mPref.getString("applock_pwd", "");
		if(TextUtils.isEmpty(applock_pwd)){
			tv_title.setText("请设置软件锁密码");
		}
		
		bt_clean_all.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				et_pwd.setText("");
			}
		});
		
		bt_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				if(TextUtils.isEmpty(pwd)){
					return;
				}else{
					et_pwd.setText(pwd.substring(0, pwd.length() - 1));
				}
			}
		});
		
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(TextUtils.isEmpty(applock_pwd)){
					//设置软件锁密码
					String str = et_pwd.getText().toString();
					if(TextUtils.isEmpty(str)){
						Toast.makeText(EnterPwdActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
					}
					mPref.edit().putString("applock_pwd", str).commit();
					Intent intent = new Intent(EnterPwdActivity.this,AppLockActivity.class);
					startActivity(intent);
					finish();
				}else{
					//输入软件锁密码
					if(TextUtils.equals(applock_pwd, et_pwd.getText().toString())){

						Intent intent = new Intent();
						// 发送广播。停止保护
						intent.setAction("com.gc.p01_mobilesafe.stopprotect");
						// 跟狗说。现在停止保护该应用
						intent.putExtra("packageName", packageName);
						sendBroadcast(intent);
						finish();
					}else{
						Toast.makeText(EnterPwdActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	
		bt_0.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				et_pwd.setText(pwd + bt_0.getText().toString());
			}
		});
	
		bt_1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				et_pwd.setText(pwd + bt_1.getText().toString());
			}
		});
		
		bt_2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				et_pwd.setText(pwd + bt_2.getText().toString());
			}
		});
		
		bt_3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				et_pwd.setText(pwd + bt_3.getText().toString());
			}
		});
		
		bt_4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				et_pwd.setText(pwd + bt_4.getText().toString());
			}
		});
		
		bt_5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				et_pwd.setText(pwd + bt_5.getText().toString());
			}
		});
		
		bt_6.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				et_pwd.setText(pwd + bt_6.getText().toString());
			}
		});
		
		bt_7.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				et_pwd.setText(pwd + bt_7.getText().toString());
			}
		});
		
		bt_8.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				et_pwd.setText(pwd + bt_8.getText().toString());
			}
		});
		
		bt_9.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pwd = et_pwd.getText().toString();
				et_pwd.setText(pwd + bt_9.getText().toString());
			}
		});
		
	}
	
	@Override
	public void onBackPressed() {
		//通过是否设置过密码来判断当前是输入密码界面还是设置密码界面
		if(!TextUtils.isEmpty(applock_pwd)){
			//当用户输入后退健 的时候，进入到桌面
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addCategory("android.intent.category.MONKEY");
			startActivity(intent);
		}else{
			super.onBackPressed();
		}
		
	}
	
	
	
}
