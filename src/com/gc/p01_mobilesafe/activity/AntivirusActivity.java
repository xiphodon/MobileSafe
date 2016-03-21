package com.gc.p01_mobilesafe.activity;

import java.util.List;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.bean.AppInfo;
import com.gc.p01_mobilesafe.db.dao.AntivirusDAO;
import com.gc.p01_mobilesafe.engine.AppInfos;
import com.gc.p01_mobilesafe.utils.MD5Utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 病毒查杀
 * 
 * @author guochang
 * 
 */
public class AntivirusActivity extends Activity {
	// 扫描开始
	protected static final int BEGING = 0;
	// 扫描中
	protected static final int SCANING = 1;
	// 扫描结束
	protected static final int FINISH = 2;

	private ImageView iv_scanning;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BEGING:
				tv_init_virus.setText("初始化八核杀毒引擎...");
				break;

			case SCANING:

				TextView child = new TextView(AntivirusActivity.this);
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				// 如果为true表示有病毒
				if (scanInfo.desc) {
					child.setTextColor(Color.RED);
					child.setText(scanInfo.appName + "——发现病毒");
				} else {
					child.setTextColor(Color.BLACK);
					// // 为false表示没有病毒
					child.setText(scanInfo.appName + "——扫描安全");
				}
				ll_antivirus_content.addView(child, 0);
				// 自动滚动
				sv_scrollView.post(new Runnable() {

					@Override
					public void run() {
						// 一直往下面进行滚动
						sv_scrollView.fullScroll(sv_scrollView.FOCUS_UP);

					}
				});
				System.out.println(scanInfo.appName + "扫描安全");

				break;

			case FINISH:
				// 扫描结束后，停止转动动画
				iv_scanning.clearAnimation();
				break;

			}
		};
	};
	private TextView tv_init_virus;
	private ProgressBar pb_antivirus_progress;
	private LinearLayout ll_antivirus_content;
	private ScrollView sv_scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initUI();
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// TODO Auto-generated method stub

		new Thread() {
			private Message message;

			public void run() {
				message = Message.obtain();
				message.what = BEGING;
				handler.sendMessage(message);

				ScanInfo scanInfo = new ScanInfo();
				// 获得包管理者
				PackageManager packageManager = getPackageManager();
				// 取得安装的所有的应用程序
				List<PackageInfo> installedPackages = packageManager
						.getInstalledPackages(0);

				// 设置杀毒进度条最大值
				int size = installedPackages.size();
				pb_antivirus_progress.setMax(size);
				// 进度条进度
				int progress = 0;

				for (PackageInfo packageInfo : installedPackages) {
					// 获得每个应用的名字
					String appName = packageInfo.applicationInfo.loadLabel(
							packageManager).toString();
					scanInfo.appName = appName;

					String appPackageName = packageInfo.applicationInfo.packageName;
					scanInfo.appPackageName = appPackageName;

					// 获得每个应用的安装目录
					String sourceDir = packageInfo.applicationInfo.sourceDir;

					String fileMD5 = MD5Utils.getFileMd5(sourceDir);

					String desc = AntivirusDAO.checkFileVirus(fileMD5);

					if (TextUtils.isEmpty(desc)) {
						scanInfo.desc = false;
					} else {
						scanInfo.desc = true;
					}

					// 设置进度条进度
					progress++;
					pb_antivirus_progress.setProgress(progress);

					message = Message.obtain();
					message.what = SCANING;
					message.obj = scanInfo;
					handler.sendMessage(message);

				}

				message = Message.obtain();
				message.what = FINISH;
				handler.sendMessage(message);

			};
		}.start();

	}

	/**
	 * 病毒扫描信息
	 * 
	 * @author guochang
	 * 
	 */
	static class ScanInfo {
		boolean desc;
		String appName;
		String appPackageName;
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_antivirus);

		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);

		/**
		 * 旋转动画： 参数：从0度旋转到360度，旋转中心为——自己宽度的一半（0.5f），自己高度的一半（0.5f）
		 */
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		// 设置动画播放时间
		rotateAnimation.setDuration(5000);
		// 循环次数:无限循环
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		// 给ImageView设置此动画，并开始
		iv_scanning.startAnimation(rotateAnimation);

		tv_init_virus = (TextView) findViewById(R.id.tv_init_virus);
		pb_antivirus_progress = (ProgressBar) findViewById(R.id.pb_antivirus_progress);
		ll_antivirus_content = (LinearLayout) findViewById(R.id.ll_antivirus_content);
		sv_scrollView = (ScrollView) findViewById(R.id.sv_scrollView);
	}
}
