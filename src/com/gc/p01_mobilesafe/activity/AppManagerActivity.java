package com.gc.p01_mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.bean.AppInfo;
import com.gc.p01_mobilesafe.engine.AppInfos;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.R.color;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * �������
 * 
 * @author guochang
 * 
 */
public class AppManagerActivity extends Activity implements OnClickListener {

	// ֱ��ͨ��ע��ķ�ʽ��ȡ������View����
	@ViewInject(R.id.lv_apps)
	private ListView lv_apps;
	@ViewInject(R.id.tv_rom)
	private TextView tv_rom;
	@ViewInject(R.id.tv_sdcard)
	private TextView tv_sdcard;
	@ViewInject(R.id.ll_ProgressBar)
	private LinearLayout ll_ProgressBar;
	@ViewInject(R.id.tv_appTitle)
	private TextView tv_appTitle;

	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;

	private PopupWindow popupWindow;

	private LinearLayout ll_uninstall;
	private LinearLayout ll_run;
	private LinearLayout ll_share;
	private LinearLayout ll_detail;

	private AppInfo appInfo;
	private AppManagerAdapter appManagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		initUI();
		initData();
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub

		// xUtils �еķ�����ע��activity
		ViewUtils.inject(this);

		// ��ȡ��rom�ڴ����е�ʣ��ռ��С
		long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
		// ��ȡ��sd����ʣ��ռ��С
		long sd_freeSpace = Environment.getExternalStorageDirectory()
				.getFreeSpace();

		// ��ʽ���ռ��С������TextView
		tv_rom.setText("�ڴ���ã�" + Formatter.formatFileSize(this, rom_freeSpace));
		tv_sdcard.setText("sd�����ã�"
				+ Formatter.formatFileSize(this, sd_freeSpace));

		// ��̬ע��һ��ж�ع㲥������
		UninstallReceiver uninstallReceiver = new UninstallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(uninstallReceiver, filter);

		// ����ListView�Ĺ�������
		lv_apps.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			/**
			 * ����ʱ�ص� firstVisibleItem �ɼ��ĵ�һ����Ŀ visibleItemCount �ɼ���������Ŀ
			 * totalItemCount һ���ж�������Ŀ
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

				popupWindowDismiss();

				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size() + 1) {
						// ϵͳӦ��
						tv_appTitle.setText("ϵͳӦ�ã���" + systemAppInfos.size()
								+ "��");
						tv_appTitle.setTextColor(Color.BLACK);
						tv_appTitle.setBackgroundColor(Color.GRAY);
						tv_appTitle.setTextSize(15);
					} else {
						// �û�Ӧ��
						tv_appTitle.setText("�û�Ӧ�ã���" + userAppInfos.size()
								+ "��");
						tv_appTitle.setTextColor(Color.BLACK);
						tv_appTitle.setBackgroundColor(Color.GRAY);
						tv_appTitle.setTextSize(15);
					}
				}

			}
		});

		/**
		 * ListView ����Ŀ��������
		 */
		lv_apps.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				// ��ȡ��ǰ������item����
				Object object = lv_apps.getItemAtPosition(position);

				if (object != null && object instanceof AppInfo) {

					appInfo = (AppInfo) object;

					View contentView = View.inflate(AppManagerActivity.this,
							R.layout.app_popup_list_item, null);

					ll_uninstall = (LinearLayout) contentView
							.findViewById(R.id.ll_uninstall);
					ll_run = (LinearLayout) contentView
							.findViewById(R.id.ll_run);
					ll_share = (LinearLayout) contentView
							.findViewById(R.id.ll_share);
					ll_detail = (LinearLayout) contentView
							.findViewById(R.id.ll_detail);

					// �� AppManagerActivity implements OnClickListener ���õ������
					ll_uninstall.setOnClickListener(AppManagerActivity.this);
					ll_run.setOnClickListener(AppManagerActivity.this);
					ll_share.setOnClickListener(AppManagerActivity.this);
					ll_detail.setOnClickListener(AppManagerActivity.this);

					// �ر�֮ǰ�Ĵ���
					popupWindowDismiss();

					// ����popup����
					popupWindow = new PopupWindow(contentView,
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);

					// PopupWindow����Ҫ���ñ�������ʹ�ö���Ч�� ������͸��������
					popupWindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));

					// ��ȡ��ǰ�������ĺ������꣬������location��
					int[] location = new int[2];
					view.getLocationInWindow(location);

					popupWindow.showAtLocation(parent, Gravity.LEFT
							+ Gravity.TOP, 100, location[1]);

					// ���Ŷ���
					ScaleAnimation scaleAnimation = new ScaleAnimation(0.3f,
							1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);

					scaleAnimation.setDuration(300);

					contentView.setAnimation(scaleAnimation);
				}

				return false;
			}
		});

	}

	/**
	 * �㲥�����ߣ�ж�أ�
	 * 
	 * @author Administrator
	 * 
	 */
	class UninstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (appInfo.isUserApp()) {
				userAppInfos.remove(appInfo);
			} else {
				systemAppInfos.remove(appInfo);
			}
			appManagerAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * popup���ڹر�
	 */
	protected void popupWindowDismiss() {
		// TODO Auto-generated method stub
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	class AppManagerAdapter extends BaseAdapter {

		private AppInfo appInfo;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// ������Ŀ + userAppInfos.size() + ������Ŀ + systemAppInfos.size();
			return appInfos.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (position == 0 || position == userAppInfos.size() + 1) {
				return null;
			}

			if (position < userAppInfos.size() + 1) {
				appInfo = userAppInfos.get(position - 1);
			} else {
				appInfo = systemAppInfos.get(position - 1 - userAppInfos.size()
						- 1);
			}

			return appInfo;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (position == 0) {
				// position����0������ʾ"�û�Ӧ��"
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setText("�û�Ӧ�ã���" + userAppInfos.size() + "��");
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);

				return textView;

			} else if (position == userAppInfos.size() + 1) {
				// position���� �û������size���� + 1������ʾ"ϵͳӦ��"
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setText("ϵͳӦ�ã���" + systemAppInfos.size() + "��");
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);

				return textView;
			}

			View view = null;
			ViewHolder holder = null;
			// ���⸴��������Ŀ
			if (convertView != null && convertView instanceof LinearLayout) {

				view = convertView;
				holder = (ViewHolder) view.getTag();

			} else {
				view = View.inflate(AppManagerActivity.this,
						R.layout.appmanager_list_item, null);

				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_apkName = (TextView) view
						.findViewById(R.id.tv_apkName);
				holder.tv_apkLocation = (TextView) view
						.findViewById(R.id.tv_apkLocation);
				holder.tv_apkSize = (TextView) view
						.findViewById(R.id.tv_apkSize);

				view.setTag(holder);
			}

			//�������������Ŀ���  position ȡֵ
			if (position < userAppInfos.size() + 1) {
				appInfo = userAppInfos.get(position - 1);
			} else {
				appInfo = systemAppInfos.get(position - 1 - userAppInfos.size()
						- 1);
			}

			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_apkName.setText(appInfo.getApkName());

			if (appInfo.isInRom()) {
				holder.tv_apkLocation.setText("�洢λ��:�ֻ�");
			} else {
				holder.tv_apkLocation.setText("�洢λ��:SD��");
			}

			holder.tv_apkSize.setText("Ӧ�ô�С:"
					+ Formatter.formatFileSize(AppManagerActivity.this,
							appInfo.getApkSize()));

			return view;
		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_apkName;
		TextView tv_apkLocation;
		TextView tv_apkSize;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			// ���ؽ�����
			ll_ProgressBar.setVisibility(View.INVISIBLE);

			appManagerAdapter = new AppManagerAdapter();
			lv_apps.setAdapter(appManagerAdapter);
		}
	};

	/**
	 * ��ʼ������
	 */
	private void initData() {
		// TODO Auto-generated method stub
		new Thread() {

			public void run() {
				// ����ֻ��а�װ������Ӧ����Ϣ
				appInfos = AppInfos.getAppInfos(AppManagerActivity.this);

				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();

				for (AppInfo appInfo : appInfos) {
					// �Ƿ�Ϊ�û�Ӧ��
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}
				}

				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// ����ʱ���ٴ���
		popupWindowDismiss();
		super.onDestroy();
	}

	/**
	 * AppManagerActivity implements OnClickListener ���õ�������� ��д�� onClick ����
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// ж��
		case R.id.ll_uninstall:

			Intent uninstall_intent = new Intent(Intent.ACTION_DELETE);
			uninstall_intent.addCategory(Intent.CATEGORY_DEFAULT);
			uninstall_intent.setData(Uri.parse("package:"
					+ appInfo.getApkPackageName()));
			startActivity(uninstall_intent);
			popupWindowDismiss();

			break;
		// ����
		case R.id.ll_run:

			Intent run_intent = this.getPackageManager()
					.getLaunchIntentForPackage(appInfo.getApkPackageName());
			startActivity(run_intent);
			popupWindowDismiss();

			break;
		// ����
		case R.id.ll_share:

			Intent share_intent = new Intent("android.intent.action.SEND");
			share_intent.setType("text/plain");
			share_intent.putExtra("android.intent.extra.SUBJECT", "f����");
			share_intent.putExtra("android.intent.extra.TEXT", "Hi~~�Ƽ���ʹ�������"
					+ appInfo.getApkName() + "�����ص�ַ��"
					+ "https://play.google.com/store/apps/details?id="
					+ appInfo.getApkPackageName());
			startActivity(Intent.createChooser(share_intent, "����"));
			popupWindowDismiss();

			break;
		// ����
		case R.id.ll_detail:

			Intent detail_intent = new Intent();
			detail_intent
					.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
			detail_intent.setData(Uri.parse("package:"
					+ appInfo.getApkPackageName()));
			startActivity(detail_intent);
			popupWindowDismiss();
			
			break;
		}
	}

}
