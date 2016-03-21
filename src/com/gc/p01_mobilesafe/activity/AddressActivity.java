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
 * �����ز�ѯҳ��
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
		
		//EditText �ı��仯����
		et_number.addTextChangedListener(new TextWatcher() {
			
			//�ı��仯ʱ�ص�
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String address = AddressDAO.getAddress(s.toString());
				tv_addressResult.setText(address);
			}
			
			//�ı��仯ǰ�ص�
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			//�ı��仯��ص�
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	/**
	 * �����ѯ�绰���������
	 * @param view
	 */
	public void queryAddress(View view){
		String number = et_number.getText().toString().trim();
		if(!TextUtils.isEmpty(number)){
			String address = AddressDAO.getAddress(number);
			tv_addressResult.setText(address);
		}else{
			  Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			  
//			  //�Զ���岹��    
//			  shake.setInterpolator(new Interpolator() {
//				
//				@Override
//				public float getInterpolation(float input) { //input ʱ�䣨x�ᣩ
//					// TODO Auto-generated method stub
//					return 0;  //���루y�ᣩ
//				}
//			});
			  
			  et_number.startAnimation(shake);
			  
			  vibrate();
		}
	}
	
	/**
	 * �ֻ���(��ҪȨ��)
	 */
	public void vibrate(){
		 Vibrator vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		 vibrate.vibrate(50);
//		 
//		 //�ȴ�һ�룬�����룬�ٵȴ�һ�룬�������롢������-1 ��ʾ��ѭ����������0��1��2������ʾ�������0��1��2����λ��ѭ��ִ��
//		 vibrate.vibrate(new long[]{1000,2000,1000,3000}, -1);
//		 
//		 //ֹͣ��
//		 vibrate.cancel();
	}
	
	
	
}
