package com.gc.p01_mobilesafe.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.utils.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ����ҳ��
 * 
 * @author guochang
 * 
 */
public class SplashActivity extends Activity {

	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROR = 3;
	protected static final int CODE_ENTER_HOME = 4;

	private TextView tv_version;
	// ���ؽ�����ʾ
	private TextView tv_progress;

	// ������������Ϣ
	// �汾��
	private String mVersionName;
	// �汾��
	private int mVersionCode;
	// �汾����
	private String mDesc;
	// ���ص�ַ
	private String mDownloadUrl;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "url����", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "�������", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "���ݽ�������",
						Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_ENTER_HOME:
				enterHome();
				break;
			}
		};
	};
	private SharedPreferences mPref;
	private RelativeLayout rl_root;
	private InputStream in;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_version.setText("�汾����V" + getVersionName());
		// Ĭ������
		tv_progress = (TextView) findViewById(R.id.tv_progress);

		rl_root = (RelativeLayout) findViewById(R.id.rl_root);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		// ����assets�µĹ��������ݿ�
		copyDB("address.db");
		// ����assets�µĲ������ݿ�
		copyDB("antivirus.db");
		//���²�����
		updataVirus();

		//��һ�����г���ʱ������ݷ�ʽ
		boolean frist_launcher = mPref.getBoolean("frist_launcher", true);
		if(frist_launcher){
			// ������ݷ�ʽ
			createShortcut();
		}
		

		// �ж��Ƿ�����°汾
		boolean autoUpdate = mPref.getBoolean("auto_update", false);
		if (!autoUpdate) {
			checkVersion();
		} else {
			// �ӳ������ٷ���Ϣ����������
			handler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
		}

		// ���䶯��Ч��
		AlphaAnimation anim = new AlphaAnimation(0.3f, 1.0f);
		anim.setDuration(2000);
		rl_root.startAnimation(anim);
	}

	/**
	 * ���²�����
	 */
	private void updataVirus() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * ������ݷ�ʽ
	 */
	private void createShortcut() {
		// TODO Auto-generated method stub
		/*
		 * 1,׼�����㲥���á�������ݷ�ʽ�Ĺ㲥�����ߡ��յ��㲥 2,������ݷ�ʽ����ͼ��Ҫ��ʲô 3,���ÿ�ݷ�ʽ������ 4,���ÿ�ݷ�ʽ��ͼ��
		 * 5,���͹㲥
		 */

		// 1,׼�����㲥���á�������ݷ�ʽ�Ĺ㲥�����ߡ��յ��㲥
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// **Ϊtrue�������ظ�������ݷ�ʽ��false�ǲ������ظ�������ݷ�ʽ
		intent.putExtra("duplicate", false);

		// 2,������ݷ�ʽ����ͼ��Ҫ��ʲô (��ת������ҳ)
		Intent shortcut_intent = new Intent(this, SplashActivity.class);
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut_intent);

		// 3,���ÿ�ݷ�ʽ������
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "��ȫ��ʿ");

		// 4,���ÿ�ݷ�ʽ��ͼ��
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_launcher));

		// 5.���͹㲥
		sendBroadcast(intent);
		
		mPref.edit().putBoolean("frist_launcher", false).commit();
	}

	/**
	 * ��ð汾��
	 * 
	 * @return �汾��
	 */
	private String getVersionName() {
		// ��ð�������
		PackageManager packageManager = getPackageManager();

		try {
			// ��ð���Ϣ
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			// ��ð汾��
			String versionName = packageInfo.versionName;

			return versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * ��ð汾��
	 * 
	 * @return �汾��
	 */
	private int getVersionCode() {
		// ��ð�������
		PackageManager packageManager = getPackageManager();

		try {
			// ��ð���Ϣ
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			// ��ð汾��
			int versionCode = packageInfo.versionCode;

			return versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * ����Ƿ���Ҫ�汾����
	 */
	private void checkVersion() {
		final long startTime = System.currentTimeMillis();
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				Message msg = Message.obtain();
				HttpURLConnection conn = null;

				try {
					URL url = new URL("http://10.0.2.2:8080/update.json");
					conn = (HttpURLConnection) url.openConnection();
					// �������󷽷�
					conn.setRequestMethod("GET");
					// �������ӳ�ʱ
					conn.setConnectTimeout(5000);
					// ���ö�ȡ��ʱ
					conn.setReadTimeout(5000);
					// ���ӷ�����
					conn.connect();

					// ��ȡ������
					int responseCode = conn.getResponseCode();
					if (responseCode == 200) {
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.readFromStream(inputStream);

						// ����json
						JSONObject jo = new JSONObject(result);
						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mDesc = jo.getString("description");
						mDownloadUrl = jo.getString("downloadUrl");

						// �ж��Ƿ����°汾
						if (mVersionCode > getVersionCode()) {
							// �и��£����������Ի���
							msg.what = CODE_UPDATE_DIALOG;
						} else {
							msg.what = CODE_ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					// url�����쳣
					msg.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// ��������쳣
					msg.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// json����ʧ��
					msg.what = CODE_JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();
					long usedTime = endTime - startTime;
					// ��֤����ҳ������ʾ3��
					if (usedTime < 3000) {
						try {
							Thread.sleep(3000 - usedTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					handler.sendMessage(msg);
					if (conn != null) {
						// �ر���������
						conn.disconnect();
					}
				}
			}
		}.start();
	}

	/**
	 * ���������Ի���
	 */
	private void showUpdateDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("���°汾��V" + mVersionName);
		builder.setMessage(mDesc);
		// //�����Ƿ����ȡ���Ի��򣬲�����ʹ��
		// builder.setCancelable(false);
		builder.setPositiveButton("��������", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				downLoad();
			}
		});

		builder.setNegativeButton("�ݲ�����", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				enterHome();
			}
		});

		// ����ȡ���Ի����������ʾ�Ի���ʱ������ؼ�ִ�и÷���
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				enterHome();
			}
		});
		builder.show();
	}

	/**
	 * ����������
	 */
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * �����°汾apk�ļ�
	 */
	private void downLoad() {
		// xUtils
		// �ж�sd���Ƿ����
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// ����Ϊ�ɼ�����
			tv_progress.setVisibility(View.VISIBLE);

			String target = Environment.getExternalStorageDirectory()
					+ "/update.apk";

			HttpUtils utils = new HttpUtils();
			utils.download(mDownloadUrl, target, new RequestCallBack<File>() {

				// �ļ������ؽ���
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					// TODO Auto-generated method stub
					super.onLoading(total, current, isUploading);
					tv_progress.setText("���ؽ��ȣ�" + current * 100 / total + "%");
				}

				// ���سɹ�
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					// TODO Auto-generated method stub
					// ��ת��ϵͳ��װ����
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result),
							"application/vnd.android.package-archive");
					// startActivity(intent);
					// ����û�ȡ����װ�����з��ؽ�����ص�����onActivityResult
					startActivityForResult(intent, 0);

				}

				// ����ʧ��
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(SplashActivity.this, "�°汾����ʧ�ܣ�",
							Toast.LENGTH_SHORT).show();
				}
			});

		} else {
			Toast.makeText(SplashActivity.this, "û���ҵ�sd��", Toast.LENGTH_SHORT)
					.show();
		}

	}

	// �û�ȡ����װ�ص�����
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}

	/**
	 * �������ݿ�
	 */
	private void copyDB(String dbName) {
		// ����Ŀ���ַ���ļ���
		File destFile = new File(getFilesDir(), dbName);

		if (destFile.exists()) {
			System.out.println("���ݿ�" + dbName + "�Ѵ��ڣ�");
			return;
		}

		FileOutputStream out = null;

		try {
			in = getAssets().open(dbName);
			out = new FileOutputStream(destFile);

			int len = 0;
			byte[] buffer = new byte[1024];

			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
