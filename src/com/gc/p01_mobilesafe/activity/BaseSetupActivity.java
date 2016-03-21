package com.gc.p01_mobilesafe.activity;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * ��ҳ��ĸ��࣬�����嵥ע�ᣨ�޽���չʾ��
 * 
 * @author guochang
 * 
 */
public abstract class BaseSetupActivity extends Activity {

	private GestureDetector mDetector;
	public SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);

		// �������ƻ����¼�
		// ����ʶ����
		mDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					/**
					 * e1������㣬e2�����յ�
					 * velocityX ˮƽ�ٶ�
					 * velocityY ��ֱ�ٶ�
					 */
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						// TODO Auto-generated method stub

						// ����ʱ������ȹ�������������ҳ
						if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
							Toast.makeText(BaseSetupActivity.this, "���򻬶����ȹ���~",
									Toast.LENGTH_SHORT).show();
							return true;
						}
						
						//
						if(Math.abs(velocityX) < 200){
							Toast.makeText(BaseSetupActivity.this, "�����ٶȹ���~",
									Toast.LENGTH_SHORT).show();
							return true;
						}

						// ���һ�����һҳ
						if (e2.getRawX() - e1.getRawX() > 200) {
							touchPrevious();
							return true;
						}

						// ���󻮣���һҳ
						if (e1.getRawX() - e2.getRawX() > 200) {
							touchNext();
							return true;
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}

	/**
	 * ��ť ��һҳ
	 * 
	 * @param view
	 */
	public void next(View view) {
		touchNext();
	}

	/**
	 * ��һҳ
	 */
	public abstract void touchNext();

	/**
	 * ��ť ��һҳ
	 * 
	 * @param view
	 */
	public void previous(View view) {
		touchPrevious();
	}

	/**
	 * ��һҳ
	 */
	public abstract void touchPrevious();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
