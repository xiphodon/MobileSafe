package com.gc.p01_mobilesafe.activity;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * 向导页面的父类，无需清单注册（无界面展示）
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

		// 监听手势滑动事件
		// 手势识别器
		mDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					/**
					 * e1滑动起点，e2滑动终点
					 * velocityX 水平速度
					 * velocityY 竖直速度
					 */
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						// TODO Auto-generated method stub

						// 滑动时纵向幅度过大则不允许划屏翻页
						if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
							Toast.makeText(BaseSetupActivity.this, "纵向滑动幅度过大~",
									Toast.LENGTH_SHORT).show();
							return true;
						}
						
						//
						if(Math.abs(velocityX) < 200){
							Toast.makeText(BaseSetupActivity.this, "滑动速度过慢~",
									Toast.LENGTH_SHORT).show();
							return true;
						}

						// 向右划，上一页
						if (e2.getRawX() - e1.getRawX() > 200) {
							touchPrevious();
							return true;
						}

						// 向左划，下一页
						if (e1.getRawX() - e2.getRawX() > 200) {
							touchNext();
							return true;
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}

	/**
	 * 按钮 下一页
	 * 
	 * @param view
	 */
	public void next(View view) {
		touchNext();
	}

	/**
	 * 下一页
	 */
	public abstract void touchNext();

	/**
	 * 按钮 上一页
	 * 
	 * @param view
	 */
	public void previous(View view) {
		touchPrevious();
	}

	/**
	 * 上一页
	 */
	public abstract void touchPrevious();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
