package com.gc.p01_mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.db.dao.AddressDAO;

/**
 * 来电监听/归属地显示服务 & 去电广播接收者（动态注册）
 * 
 * @author guochang
 * 
 */
public class AddressService extends Service {

	private TelephonyManager tm;
	private MyListener listener;
	private OutCallReceiver receiver;
	private WindowManager windowManager;
	private View myView;
	private SharedPreferences mPref;
	
	private int startX;
	private int startY;
	private WindowManager.LayoutParams params;
	private int winWidth;
	private int winHeight;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		// 拿到电话管理器
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		listener = new MyListener();
		// 监听电话状态
		// events：决定 PhoneStateListener 监听什么内容
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 动态注册 去电 广播接收者
		receiver = new OutCallReceiver();
		//也可以
//		IntentFilter filter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
	}

	class MyListener extends PhoneStateListener {
		// 状态改变时回调
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println("空闲状态");
				// 挂断电话时，从 window 中移除自定义浮窗
				if (windowManager != null && myView != null) {
					windowManager.removeView(myView);
					myView = null;
				}
				break;

			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("响铃状态");
				// 根据来电号码查询归属地
				String address = AddressDAO.getAddress(incomingNumber);
				// Toast.makeText(AddressService.this, address,
				// Toast.LENGTH_LONG).show();
				showToast(address);
				break;

			default:
				break;
			}

		}
	}

	/**
	 * 监听去电 广播接受者(采用动态注册) 需要去电权限 android.permission.PROCESS_OUTGOING_CALLS
	 * 
	 * @author guochang
	 * 
	 */
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String number = getResultData();
			String address = AddressDAO.getAddress(number);
			// Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			showToast(address);
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 停止来电监听
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		// 注销 去电 广播接收者
		unregisterReceiver(receiver);
	}

	/**
	 * 自定义归属地浮窗
	 */
	public void showToast(String address) {
		// 可以在任何界面弹出自己的浮窗
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		//获得屏幕宽高
		winWidth = windowManager.getDefaultDisplay().getWidth();
		winHeight = windowManager.getDefaultDisplay().getHeight();
		
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.TRANSLUCENT;
		// //动画效果
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		
		//myView 的显示类型为Toast级别，优先级比较低，因此设置为 TYPE_PHONE,电话窗口，用于电话交互，特别是电话呼入
//		（需要权限）android.permission.SYSTEM_ALERT_WINDOW
//		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		
		//将重心从中间移至左上方（0,0）
		params.gravity = Gravity.LEFT + Gravity.TOP;
		params.setTitle("Toast");
		//“不可触摸”设置删掉
//		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		
		
		
		
		int last_left = mPref.getInt("last_left", 0);
		int last_top = mPref.getInt("last_top", 0);
		
		//设置浮窗位置，基于左上方的偏移量
		params.x = last_left;
		params.y = last_top;
		
		
		
		// myView = new TextView(this);
		myView = View.inflate(this, R.layout.toast_myview_address, null);
		
		
		//背景数组
		int[] backgrouds = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		// 读取保存的style
		int address_style = mPref.getInt("address_style", 0);
		//设置背景
		myView.setBackgroundResource(backgrouds[address_style]);

		
		TextView tv_address = (TextView) myView.findViewById(R.id.tv_address);
		tv_address.setText(address);
		// 将myView添加到屏幕（window）上
		windowManager.addView(myView, params);
		
		//触摸事件
		myView.setOnTouchListener(new OnTouchListener() {
			
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

					//设置浮窗位置，基于左上方的偏移量
					params.x += dx;
					params.y += dy;
					
					//防止坐标偏移出屏幕
					if(params.x < 0){
						params.x = 0;
					}
					
					if(params.y < 0){
						params.y = 0;
					}
					
					if(params.x > winWidth - myView.getWidth()){
						params.x = winWidth - myView.getWidth();
					}
					
					if(params.y > winHeight - myView.getHeight()){
						params.y = winHeight - myView.getHeight();
					}
					
					windowManager.updateViewLayout(myView, params);
					
					// 从新获得起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
					
				case MotionEvent.ACTION_UP:
					// 保存记录坐标点
					Editor edit = mPref.edit();
					//重心原因，因此在此采用  (int) event.getRawX(),而不是myView.getLeft()
					edit.putInt("last_left", (int) event.getRawX());
					edit.putInt("last_top", (int) event.getRawY());
					edit.commit();
					break;

				default:
					break;
				}
				return true;
			}
		});
	}

}
