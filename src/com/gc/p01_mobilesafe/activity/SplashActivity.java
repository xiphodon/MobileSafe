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
 * 闪屏页面
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
	// 下载进度显示
	private TextView tv_progress;

	// 服务器返回信息
	// 版本名
	private String mVersionName;
	// 版本号
	private int mVersionCode;
	// 版本描述
	private String mDesc;
	// 下载地址
	private String mDownloadUrl;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "url错误", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "数据解析错误",
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
		tv_version.setText("版本名：V" + getVersionName());
		// 默认隐藏
		tv_progress = (TextView) findViewById(R.id.tv_progress);

		rl_root = (RelativeLayout) findViewById(R.id.rl_root);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		// 拷贝assets下的归属地数据库
		copyDB("address.db");
		// 拷贝assets下的病毒数据库
		copyDB("antivirus.db");
		//更新病毒库
		updataVirus();

		//第一次运行程序时创建快捷方式
		boolean frist_launcher = mPref.getBoolean("frist_launcher", true);
		if(frist_launcher){
			// 创建快捷方式
			createShortcut();
		}
		

		// 判断是否检查更新版本
		boolean autoUpdate = mPref.getBoolean("auto_update", false);
		if (!autoUpdate) {
			checkVersion();
		} else {
			// 延迟两秒再发消息进入主界面
			handler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
		}

		// 渐变动画效果
		AlphaAnimation anim = new AlphaAnimation(0.3f, 1.0f);
		anim.setDuration(2000);
		rl_root.startAnimation(anim);
	}

	/**
	 * 更新病毒库
	 */
	private void updataVirus() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 创建快捷方式
	 */
	private void createShortcut() {
		// TODO Auto-generated method stub
		/*
		 * 1,准备发广播，让“创建快捷方式的广播接收者”收到广播 2,创建快捷方式的意图，要做什么 3,设置快捷方式的名字 4,设置快捷方式的图标
		 * 5,发送广播
		 */

		// 1,准备发广播，让“创建快捷方式的广播接收者”收到广播
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// **为true是允许重复创建快捷方式，false是不允许重复创建快捷方式
		intent.putExtra("duplicate", false);

		// 2,创建快捷方式的意图，要做什么 (跳转到闪屏页)
		Intent shortcut_intent = new Intent(this, SplashActivity.class);
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut_intent);

		// 3,设置快捷方式的名字
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安全卫士");

		// 4,设置快捷方式的图标
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_launcher));

		// 5.发送广播
		sendBroadcast(intent);
		
		mPref.edit().putBoolean("frist_launcher", false).commit();
	}

	/**
	 * 获得版本名
	 * 
	 * @return 版本名
	 */
	private String getVersionName() {
		// 获得包管理器
		PackageManager packageManager = getPackageManager();

		try {
			// 获得包信息
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			// 获得版本名
			String versionName = packageInfo.versionName;

			return versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获得版本号
	 * 
	 * @return 版本名
	 */
	private int getVersionCode() {
		// 获得包管理器
		PackageManager packageManager = getPackageManager();

		try {
			// 获得包信息
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			// 获得版本号
			int versionCode = packageInfo.versionCode;

			return versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 检查是否需要版本升级
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
					// 设置请求方法
					conn.setRequestMethod("GET");
					// 设置连接超时
					conn.setConnectTimeout(5000);
					// 设置读取超时
					conn.setReadTimeout(5000);
					// 连接服务器
					conn.connect();

					// 获取请求码
					int responseCode = conn.getResponseCode();
					if (responseCode == 200) {
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.readFromStream(inputStream);

						// 解析json
						JSONObject jo = new JSONObject(result);
						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mDesc = jo.getString("description");
						mDownloadUrl = jo.getString("downloadUrl");

						// 判断是否有新版本
						if (mVersionCode > getVersionCode()) {
							// 有更新，弹出升级对话框
							msg.what = CODE_UPDATE_DIALOG;
						} else {
							msg.what = CODE_ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					// url错误异常
					msg.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// 网络错误异常
					msg.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// json解析失败
					msg.what = CODE_JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();
					long usedTime = endTime - startTime;
					// 保证闪屏页至少显示3秒
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
						// 关闭网络连接
						conn.disconnect();
					}
				}
			}
		}.start();
	}

	/**
	 * 弹出升级对话框
	 */
	private void showUpdateDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("最新版本：V" + mVersionName);
		builder.setMessage(mDesc);
		// //设置是否可以取消对话框，不建议使用
		// builder.setCancelable(false);
		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				downLoad();
			}
		});

		builder.setNegativeButton("暂不更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				enterHome();
			}
		});

		// 设置取消对话框监听，显示对话框时点击返回键执行该方法
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
	 * 进入主界面
	 */
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 下载新版本apk文件
	 */
	private void downLoad() {
		// xUtils
		// 判断sd卡是否存在
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 设置为可见进度
			tv_progress.setVisibility(View.VISIBLE);

			String target = Environment.getExternalStorageDirectory()
					+ "/update.apk";

			HttpUtils utils = new HttpUtils();
			utils.download(mDownloadUrl, target, new RequestCallBack<File>() {

				// 文件的下载进度
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					// TODO Auto-generated method stub
					super.onLoading(total, current, isUploading);
					tv_progress.setText("下载进度：" + current * 100 / total + "%");
				}

				// 下载成功
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					// TODO Auto-generated method stub
					// 跳转到系统安装界面
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result),
							"application/vnd.android.package-archive");
					// startActivity(intent);
					// 如果用户取消安装，会有返回结果，回调方法onActivityResult
					startActivityForResult(intent, 0);

				}

				// 下载失败
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(SplashActivity.this, "新版本下载失败！",
							Toast.LENGTH_SHORT).show();
				}
			});

		} else {
			Toast.makeText(SplashActivity.this, "没有找到sd卡", Toast.LENGTH_SHORT)
					.show();
		}

	}

	// 用户取消安装回调方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}

	/**
	 * 拷贝数据库
	 */
	private void copyDB(String dbName) {
		// 拷贝目标地址，文件名
		File destFile = new File(getFilesDir(), dbName);

		if (destFile.exists()) {
			System.out.println("数据库" + dbName + "已存在！");
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
