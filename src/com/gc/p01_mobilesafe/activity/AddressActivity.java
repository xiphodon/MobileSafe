package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.db.dao.AddressDAO;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 归属地查询页面
 * @author guochang
 *
 */
public class AddressActivity extends Activity {

	private EditText et_number;
	private TextView tv_addressResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);

		et_number = (EditText) findViewById(R.id.et_number);
		tv_addressResult = (TextView) findViewById(R.id.tv_addressResult);
		
		//EditText 文本变化监听
		et_number.addTextChangedListener(new TextWatcher() {
			
			//文本变化时回调
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String address = AddressDAO.getAddress(s.toString());
				tv_addressResult.setText(address);
			}
			
			//文本变化前回调
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			//文本变化后回调
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	/**
	 * 点击查询电话号码归属地
	 * @param view
	 */
	public void queryAddress(View view){
		String number = et_number.getText().toString().trim();
		if(!TextUtils.isEmpty(number)){
			String address = AddressDAO.getAddress(number);
			tv_addressResult.setText(address);
		}else{
			  Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			  
//			  //自定义插补器    
//			  shake.setInterpolator(new Interpolator() {
//				
//				@Override
//				public float getInterpolation(float input) { //input 时间（x轴）
//					// TODO Auto-generated method stub
//					return 0;  //距离（y轴）
//				}
//			});
			  
			  et_number.startAnimation(shake);
			  
			  vibrate();
		}
	}
	
	/**
	 * 手机震动(需要权限)
	 */
	public void vibrate(){
		 Vibrator vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		 vibrate.vibrate(50);
//		 
//		 //等待一秒，震动两秒，再等待一秒，再震动三秒、参数：-1 表示不循环；可以填0（1、2），表示从数组第0（1、2）个位置循环执行
//		 vibrate.vibrate(new long[]{1000,2000,1000,3000}, -1);
//		 
//		 //停止震动
//		 vibrate.cancel();
	}
	
	
	
}
