package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.utils.SmsUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

/**
 * �߼�����
 * @author guochang
 *
 */
public class AToolsActivity extends Activity {

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		
		
	}
	
	/**
	 * �绰�����ز�ѯ
	 * @param view
	 */
	public void numberAddressQuery(View view){
		startActivity(new Intent(this, AddressActivity.class));
	}
	
	/**
	 * ���ű���
	 * @param view
	 */
	public void backUpSMS(View view){
		//��ʼ��һ���������Ի���
		progressDialog = new ProgressDialog(AToolsActivity.this);
		progressDialog.setTitle("��ʾ");
		progressDialog.setMessage("���ڱ����У����Ժ�...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
		
		
		new Thread(){
			public void run() {
				boolean backUpResult = SmsUtils.backUp(AToolsActivity.this,progressDialog);
				//���ٽ��ȿ�
				progressDialog.dismiss();
				
				if(backUpResult){
					Looper.prepare();
					Toast.makeText(AToolsActivity.this, "���ű��ݳɹ�", Toast.LENGTH_SHORT).show();
					Looper.loop();
				}else{
					Looper.prepare();
					Toast.makeText(AToolsActivity.this, "���ű���ʧ��", Toast.LENGTH_SHORT).show();
					Looper.loop();
				}
				
			};
		}.start();
		
	}
	
	/**
	 * ���ó�����
	 * @param view
	 */
	public void appLock(View view){
		Intent intent = new Intent(this,AppLockActivity.class);
		startActivity(intent);
	}
}
