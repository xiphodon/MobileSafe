package com.gc.p01_mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.p01_mobilesafe.R;

/**
 * 设置归属地悬浮框的显示位置
 * 
 * @author guochang
 * 
 */
public class DragAddressViewActivity extends Activity {
	private TextView tv_top;
	private TextView tv_bottom;

	private ImageView iv_drag;

	private int startX;
	private int startY;
	private SharedPreferences mPref;

	// 数组长度就是点击次数
	long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_address_view);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		tv_top = (TextView) findViewById(R.id.tv_top);
		tv_bottom = (TextView) findViewById(R.id.tv_bottom);

		iv_drag = (ImageView) findViewById(R.id.iv_drag);

		// 获取手机屏幕宽高
		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
		final int winHeight = getWindowManager().getDefaultDisplay()
				.getHeight();

		int last_left = mPref.getInt("last_left", winWidth / 3);
		int last_top = mPref.getInt("last_top", winHeight / 3);

		// 上下说明框的显示与否
		if (last_top > winHeight * 3 / 4) {
			tv_top.setVisibility(View.VISIBLE);
			tv_bottom.setVisibility(View.INVISIBLE);
		} else if (last_top + 20 < winHeight / 4) {
			tv_top.setVisibility(View.INVISIBLE);
			tv_bottom.setVisibility(View.VISIBLE);
		}

		// //不能用此方法来初始化图片位置，因为android底层绘制图片的顺序是
		// 1,onMeasure(测量)，2,onLayout(定位放置)，3,onDraw(绘制)
		// 因为此时还没有测量完，所以调用layout方法不起作用
		// iv_drag.layout(last_left, last_top, last_left + iv_drag.getWidth(),
		// last_top + iv_drag.getHeight());

		// 在父控件RelativeLayout中取得iv_drag的位置信息参数，直接赋值，无需进行测量
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_drag
				.getLayoutParams();
		// 设置左边距和上边距
		layoutParams.leftMargin = last_left;
		layoutParams.topMargin = last_top;
		// 从新设置iv_drag的位置
		iv_drag.setLayoutParams(layoutParams);

		// 设置iv_drag的点击监听（双击居中）
		iv_drag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				// 当前已开机时间
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					//双击时更新布局（居中）
					iv_drag.layout(winWidth / 2 - iv_drag.getWidth() / 2,
							iv_drag.getTop(), winWidth / 2 + iv_drag.getWidth()
									/ 2, iv_drag.getBottom());
					mPref.edit().putInt("last_left", winWidth / 2 - iv_drag.getWidth() / 2).commit();
					mPref.edit().putInt("last_top", iv_drag.getTop()).commit();
				}
			}
		});

		// 给拖拽框设置触摸监听
		iv_drag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 获得起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					// 获得此时的坐标
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 获得移动偏移量
					int dx = endX - startX;
					int dy = endY - startY;

					// 更新界面坐标
					int l = iv_drag.getLeft() + dx;
					int t = iv_drag.getTop() + dy;
					int r = iv_drag.getRight() + dx;
					int b = iv_drag.getBottom() + dy;

					// 不允许悬浮框出界
					// b>winHeight - 20 手机上面有通知栏，会占用一定的高度，而winHeight是手机屏幕高度
					if (l < 0 || r > winWidth || t < 0 || b > winHeight - 40) {
						break;
					}

					// 上下说明框的显示与否
					if (t > winHeight * 3 / 4) {
						tv_top.setVisibility(View.VISIBLE);
						tv_bottom.setVisibility(View.INVISIBLE);
					} else if (b < winHeight / 4) {
						tv_top.setVisibility(View.INVISIBLE);
						tv_bottom.setVisibility(View.VISIBLE);
					}

					// 更新界面
					iv_drag.layout(l, t, r, b);

					// 从新获得起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					// 保存记录坐标点
					Editor edit = mPref.edit();
					edit.putInt("last_left", iv_drag.getLeft());
					edit.putInt("last_top", iv_drag.getTop());
					edit.commit();
					break;

				default:
					break;
				}
				//拦截事件，false：不拦截，可以继续向下传递；true：拦截，事件不往下传递
				return false;
			}
		});
	}
}
