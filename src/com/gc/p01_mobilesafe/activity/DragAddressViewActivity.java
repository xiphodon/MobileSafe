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
 * ���ù��������������ʾλ��
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

	// ���鳤�Ⱦ��ǵ������
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

		// ��ȡ�ֻ���Ļ���
		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
		final int winHeight = getWindowManager().getDefaultDisplay()
				.getHeight();

		int last_left = mPref.getInt("last_left", winWidth / 3);
		int last_top = mPref.getInt("last_top", winHeight / 3);

		// ����˵�������ʾ���
		if (last_top > winHeight * 3 / 4) {
			tv_top.setVisibility(View.VISIBLE);
			tv_bottom.setVisibility(View.INVISIBLE);
		} else if (last_top + 20 < winHeight / 4) {
			tv_top.setVisibility(View.INVISIBLE);
			tv_bottom.setVisibility(View.VISIBLE);
		}

		// //�����ô˷�������ʼ��ͼƬλ�ã���Ϊandroid�ײ����ͼƬ��˳����
		// 1,onMeasure(����)��2,onLayout(��λ����)��3,onDraw(����)
		// ��Ϊ��ʱ��û�в����꣬���Ե���layout������������
		// iv_drag.layout(last_left, last_top, last_left + iv_drag.getWidth(),
		// last_top + iv_drag.getHeight());

		// �ڸ��ؼ�RelativeLayout��ȡ��iv_drag��λ����Ϣ������ֱ�Ӹ�ֵ��������в���
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_drag
				.getLayoutParams();
		// ������߾���ϱ߾�
		layoutParams.leftMargin = last_left;
		layoutParams.topMargin = last_top;
		// ��������iv_drag��λ��
		iv_drag.setLayoutParams(layoutParams);

		// ����iv_drag�ĵ��������˫�����У�
		iv_drag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				// ��ǰ�ѿ���ʱ��
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					//˫��ʱ���²��֣����У�
					iv_drag.layout(winWidth / 2 - iv_drag.getWidth() / 2,
							iv_drag.getTop(), winWidth / 2 + iv_drag.getWidth()
									/ 2, iv_drag.getBottom());
					mPref.edit().putInt("last_left", winWidth / 2 - iv_drag.getWidth() / 2).commit();
					mPref.edit().putInt("last_top", iv_drag.getTop()).commit();
				}
			}
		});

		// ����ק�����ô�������
		iv_drag.setOnTouchListener(new OnTouchListener() {

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

					// ���½�������
					int l = iv_drag.getLeft() + dx;
					int t = iv_drag.getTop() + dy;
					int r = iv_drag.getRight() + dx;
					int b = iv_drag.getBottom() + dy;

					// ���������������
					// b>winHeight - 20 �ֻ�������֪ͨ������ռ��һ���ĸ߶ȣ���winHeight���ֻ���Ļ�߶�
					if (l < 0 || r > winWidth || t < 0 || b > winHeight - 40) {
						break;
					}

					// ����˵�������ʾ���
					if (t > winHeight * 3 / 4) {
						tv_top.setVisibility(View.VISIBLE);
						tv_bottom.setVisibility(View.INVISIBLE);
					} else if (b < winHeight / 4) {
						tv_top.setVisibility(View.INVISIBLE);
						tv_bottom.setVisibility(View.VISIBLE);
					}

					// ���½���
					iv_drag.layout(l, t, r, b);

					// ���»���������
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					// �����¼�����
					Editor edit = mPref.edit();
					edit.putInt("last_left", iv_drag.getLeft());
					edit.putInt("last_top", iv_drag.getTop());
					edit.commit();
					break;

				default:
					break;
				}
				//�����¼���false�������أ����Լ������´��ݣ�true�����أ��¼������´���
				return false;
			}
		});
	}
}
