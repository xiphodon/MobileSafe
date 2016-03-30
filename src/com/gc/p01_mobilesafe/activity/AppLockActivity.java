package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.fragment.LockFragment;
import com.gc.p01_mobilesafe.fragment.UnLockFragment;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ������
 * 
 * @author guochang
 * 
 */
public class AppLockActivity extends FragmentActivity implements
		OnClickListener {

	private FrameLayout fl_layout;
	private TextView tv_unlock;
	private TextView tv_lock;
	private FragmentManager fragmentManager;
	private UnLockFragment unLockFragment;
	private LockFragment lockFragment;
	private LinearLayout ll_ProgressBar;
	private LinearLayout ll_showApps;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		initUI();
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_app_lock);

		fl_layout = (FrameLayout) findViewById(R.id.fl_layout);
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_lock = (TextView) findViewById(R.id.tv_lock);
		ll_ProgressBar = (LinearLayout) findViewById(R.id.ll_ProgressBar);
		ll_showApps = (LinearLayout) findViewById(R.id.ll_showApps);

		tv_unlock.setOnClickListener(this);
		tv_lock.setOnClickListener(this);

		// ���fragment�Ĺ�����
		fragmentManager = getSupportFragmentManager();
		// ��������
		FragmentTransaction mTransaction = fragmentManager.beginTransaction();
		unLockFragment = new UnLockFragment();
		lockFragment = new LockFragment();
		// ��������ʾ��֡����,���ύ
		mTransaction.replace(R.id.fl_layout, unLockFragment).commit();
		ll_ProgressBar.setVisibility(View.INVISIBLE);
		ll_showApps.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		ll_ProgressBar.setVisibility(View.VISIBLE);
		ll_showApps.setVisibility(View.INVISIBLE);

		// ��������
		FragmentTransaction mTransaction = fragmentManager.beginTransaction();

		switch (v.getId()) {

		case R.id.tv_unlock:
			// ���δ����
			tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
			tv_lock.setBackgroundResource(R.drawable.tab_right_default);

			// ��������ʾ��֡����,���ύ
			mTransaction.replace(R.id.fl_layout, unLockFragment).commit();

			ll_ProgressBar.setVisibility(View.INVISIBLE);
			ll_showApps.setVisibility(View.VISIBLE);
			
			break;

		case R.id.tv_lock:
			// �������
			tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
			tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);

			// ��������ʾ��֡����,���ύ
			mTransaction.replace(R.id.fl_layout, lockFragment).commit();
			
			ll_ProgressBar.setVisibility(View.INVISIBLE);
			ll_showApps.setVisibility(View.VISIBLE);

			break;

		}
	}
}
