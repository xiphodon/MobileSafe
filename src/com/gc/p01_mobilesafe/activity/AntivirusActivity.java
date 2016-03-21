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
 * ������ɱ
 * 
 * @author guochang
 * 
 */
public class AntivirusActivity extends Activity {
	// ɨ�迪ʼ
	protected static final int BEGING = 0;
	// ɨ����
	protected static final int SCANING = 1;
	// ɨ�����
	protected static final int FINISH = 2;

	private ImageView iv_scanning;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BEGING:
				tv_init_virus.setText("��ʼ���˺�ɱ������...");
				break;

			case SCANING:

				TextView child = new TextView(AntivirusActivity.this);
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				// ���Ϊtrue��ʾ�в���
				if (scanInfo.desc) {
					child.setTextColor(Color.RED);
					child.setText(scanInfo.appName + "�������ֲ���");
				} else {
					child.setTextColor(Color.BLACK);
					// // Ϊfalse��ʾû�в���
					child.setText(scanInfo.appName + "����ɨ�谲ȫ");
				}
				ll_antivirus_content.addView(child, 0);
				// �Զ�����
				sv_scrollView.post(new Runnable() {

					@Override
					public void run() {
						// һֱ��������й���
						sv_scrollView.fullScroll(sv_scrollView.FOCUS_UP);

					}
				});
				System.out.println(scanInfo.appName + "ɨ�谲ȫ");

				break;

			case FINISH:
				// ɨ�������ֹͣת������
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
	 * ��ʼ������
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
				// ��ð�������
				PackageManager packageManager = getPackageManager();
				// ȡ�ð�װ�����е�Ӧ�ó���
				List<PackageInfo> installedPackages = packageManager
						.getInstalledPackages(0);

				// ����ɱ�����������ֵ
				int size = installedPackages.size();
				pb_antivirus_progress.setMax(size);
				// ����������
				int progress = 0;

				for (PackageInfo packageInfo : installedPackages) {
					// ���ÿ��Ӧ�õ�����
					String appName = packageInfo.applicationInfo.loadLabel(
							packageManager).toString();
					scanInfo.appName = appName;

					String appPackageName = packageInfo.applicationInfo.packageName;
					scanInfo.appPackageName = appPackageName;

					// ���ÿ��Ӧ�õİ�װĿ¼
					String sourceDir = packageInfo.applicationInfo.sourceDir;

					String fileMD5 = MD5Utils.getFileMd5(sourceDir);

					String desc = AntivirusDAO.checkFileVirus(fileMD5);

					if (TextUtils.isEmpty(desc)) {
						scanInfo.desc = false;
					} else {
						scanInfo.desc = true;
					}

					// ���ý���������
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
	 * ����ɨ����Ϣ
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
	 * ��ʼ��UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_antivirus);

		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);

		/**
		 * ��ת������ ��������0����ת��360�ȣ���ת����Ϊ�����Լ���ȵ�һ�루0.5f�����Լ��߶ȵ�һ�루0.5f��
		 */
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		// ���ö�������ʱ��
		rotateAnimation.setDuration(5000);
		// ѭ������:����ѭ��
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		// ��ImageView���ô˶���������ʼ
		iv_scanning.startAnimation(rotateAnimation);

		tv_init_virus = (TextView) findViewById(R.id.tv_init_virus);
		pb_antivirus_progress = (ProgressBar) findViewById(R.id.pb_antivirus_progress);
		ll_antivirus_content = (LinearLayout) findViewById(R.id.ll_antivirus_content);
		sv_scrollView = (ScrollView) findViewById(R.id.sv_scrollView);
	}
}
