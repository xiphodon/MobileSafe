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
 * �������/��������ʾ���� & ȥ��㲥�����ߣ���̬ע�ᣩ
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

		// �õ��绰������
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		listener = new MyListener();
		// �����绰״̬
		// events������ PhoneStateListener ����ʲô����
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// ��̬ע�� ȥ�� �㲥������
		receiver = new OutCallReceiver();
		//Ҳ����
//		IntentFilter filter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
	}

	class MyListener extends PhoneStateListener {
		// ״̬�ı�ʱ�ص�
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println("����״̬");
				// �Ҷϵ绰ʱ���� window ���Ƴ��Զ��帡��
				if (windowManager != null && myView != null) {
					windowManager.removeView(myView);
					myView = null;
				}
				break;

			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("����״̬");
				// ������������ѯ������
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
	 * ����ȥ�� �㲥������(���ö�̬ע��) ��Ҫȥ��Ȩ�� android.permission.PROCESS_OUTGOING_CALLS
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
		// ֹͣ�������
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		// ע�� ȥ�� �㲥������
		unregisterReceiver(receiver);
	}

	/**
	 * �Զ�������ظ���
	 */
	public void showToast(String address) {
		// �������κν��浯���Լ��ĸ���
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		//�����Ļ���
		winWidth = windowManager.getDefaultDisplay().getWidth();
		winHeight = windowManager.getDefaultDisplay().getHeight();
		
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.TRANSLUCENT;
		// //����Ч��
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		
		//myView ����ʾ����ΪToast�������ȼ��Ƚϵͣ��������Ϊ TYPE_PHONE,�绰���ڣ����ڵ绰�������ر��ǵ绰����
//		����ҪȨ�ޣ�android.permission.SYSTEM_ALERT_WINDOW
//		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		
		//�����Ĵ��м��������Ϸ���0,0��
		params.gravity = Gravity.LEFT + Gravity.TOP;
		params.setTitle("Toast");
		//�����ɴ���������ɾ��
//		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		
		
		
		
		int last_left = mPref.getInt("last_left", 0);
		int last_top = mPref.getInt("last_top", 0);
		
		//���ø���λ�ã��������Ϸ���ƫ����
		params.x = last_left;
		params.y = last_top;
		
		
		
		// myView = new TextView(this);
		myView = View.inflate(this, R.layout.toast_myview_address, null);
		
		
		//��������
		int[] backgrouds = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		// ��ȡ�����style
		int address_style = mPref.getInt("address_style", 0);
		//���ñ���
		myView.setBackgroundResource(backgrouds[address_style]);

		
		TextView tv_address = (TextView) myView.findViewById(R.id.tv_address);
		tv_address.setText(address);
		// ��myView��ӵ���Ļ��window����
		windowManager.addView(myView, params);
		
		//�����¼�
		myView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// ����������
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
					
				case MotionEvent.ACTION_MOVE:
					// ��ô�ʱ������
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// ����ƶ�ƫ����
					int dx = endX - startX;
					int dy = endY - startY;

					//���ø���λ�ã��������Ϸ���ƫ����
					params.x += dx;
					params.y += dy;
					
					//��ֹ����ƫ�Ƴ���Ļ
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
					
					// ���»���������
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
					
				case MotionEvent.ACTION_UP:
					// �����¼�����
					Editor edit = mPref.edit();
					//����ԭ������ڴ˲���  (int) event.getRawX(),������myView.getLeft()
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
