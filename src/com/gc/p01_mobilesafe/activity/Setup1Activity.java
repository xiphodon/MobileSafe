package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * �ֻ����� ������ ��1ҳ
 * 
 * @author guochang
 * 
 */
public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public void touchNext() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, Setup2Activity.class));
		finish();

		// ҳ����ת����
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void touchPrevious() {
		// TODO Auto-generated method stub

	}
}
