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
 * 软件管理
 * 
 * @author guochang
 * 
 */
public class AppManagerActivity extends Activity implements OnClickListener {

	// 直接通过注解的方式，取到各个View对象
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
	 * 初始化UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub

		// xUtils 中的方法，注入activity
		ViewUtils.inject(this);

		// 获取到rom内存运行的剩余空间大小
		long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
		// 获取到sd卡的剩余空间大小
		long sd_freeSpace = Environment.getExternalStorageDirectory()
				.getFreeSpace();

		// 格式化空间大小并设置TextView
		tv_rom.setText("内存可用：" + Formatter.formatFileSize(this, rom_freeSpace));
		tv_sdcard.setText("sd卡可用："
				+ Formatter.formatFileSize(this, sd_freeSpace));

		// 动态注册一个卸载广播接受者
		UninstallReceiver uninstallReceiver = new UninstallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(uninstallReceiver, filter);

		// 设置ListView的滚动监听
		lv_apps.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			/**
			 * 滑动时回调 firstVisibleItem 可见的第一条条目 visibleItemCount 可见多少条条目
			 * totalItemCount 一共有多少条条目
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

				popupWindowDismiss();

				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size() + 1) {
						// 系统应用
						tv_appTitle.setText("系统应用：（" + systemAppInfos.size()
								+ "）");
						tv_appTitle.setTextColor(Color.BLACK);
						tv_appTitle.setBackgroundColor(Color.GRAY);
						tv_appTitle.setTextSize(15);
					} else {
						// 用户应用
						tv_appTitle.setText("用户应用：（" + userAppInfos.size()
								+ "）");
						tv_appTitle.setTextColor(Color.BLACK);
						tv_appTitle.setBackgroundColor(Color.GRAY);
						tv_appTitle.setTextSize(15);
					}
				}

			}
		});

		/**
		 * ListView 中条目长按监听
		 */
		lv_apps.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				// 获取当前长按的item对象
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

					// 让 AppManagerActivity implements OnClickListener 设置点击监听
					ll_uninstall.setOnClickListener(AppManagerActivity.this);
					ll_run.setOnClickListener(AppManagerActivity.this);
					ll_share.setOnClickListener(AppManagerActivity.this);
					ll_detail.setOnClickListener(AppManagerActivity.this);

					// 关闭之前的窗体
					popupWindowDismiss();

					// 构建popup动画
					popupWindow = new PopupWindow(contentView,
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);

					// PopupWindow必须要设置背景才能使用动画效果 （设置透明背景）
					popupWindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));

					// 获取当前点击组件的横纵坐标，在数组location中
					int[] location = new int[2];
					view.getLocationInWindow(location);

					popupWindow.showAtLocation(parent, Gravity.LEFT
							+ Gravity.TOP, 100, location[1]);

					// 缩放动画
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
	 * 广播接受者（卸载）
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
	 * popup窗口关闭
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
			// 特殊条目 + userAppInfos.size() + 特殊条目 + systemAppInfos.size();
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
				// position等于0，则显示"用户应用"
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setText("用户应用：（" + userAppInfos.size() + "）");
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);

				return textView;

			} else if (position == userAppInfos.size() + 1) {
				// position等于 用户程序的size（） + 1，则显示"系统应用"
				TextView textView = new TextView(AppManagerActivity.this);
				textView.setText("系统应用：（" + systemAppInfos.size() + "）");
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);

				return textView;
			}

			View view = null;
			ViewHolder holder = null;
			// 避免复用特殊条目
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

			//计算添加特殊条目后的  position 取值
			if (position < userAppInfos.size() + 1) {
				appInfo = userAppInfos.get(position - 1);
			} else {
				appInfo = systemAppInfos.get(position - 1 - userAppInfos.size()
						- 1);
			}

			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_apkName.setText(appInfo.getApkName());

			if (appInfo.isInRom()) {
				holder.tv_apkLocation.setText("存储位置:手机");
			} else {
				holder.tv_apkLocation.setText("存储位置:SD卡");
			}

			holder.tv_apkSize.setText("应用大小:"
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

			// 隐藏进度条
			ll_ProgressBar.setVisibility(View.INVISIBLE);

			appManagerAdapter = new AppManagerAdapter();
			lv_apps.setAdapter(appManagerAdapter);
		}
	};

	/**
	 * 初始化数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
		new Thread() {

			public void run() {
				// 获得手机中安装的所有应用信息
				appInfos = AppInfos.getAppInfos(AppManagerActivity.this);

				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();

				for (AppInfo appInfo : appInfos) {
					// 是否为用户应用
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
		// 返回时销毁窗体
		popupWindowDismiss();
		super.onDestroy();
	}

	/**
	 * AppManagerActivity implements OnClickListener 设置点击监听后 覆写的 onClick 方法
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 卸载
		case R.id.ll_uninstall:

			Intent uninstall_intent = new Intent(Intent.ACTION_DELETE);
			uninstall_intent.addCategory(Intent.CATEGORY_DEFAULT);
			uninstall_intent.setData(Uri.parse("package:"
					+ appInfo.getApkPackageName()));
			startActivity(uninstall_intent);
			popupWindowDismiss();

			break;
		// 运行
		case R.id.ll_run:

			Intent run_intent = this.getPackageManager()
					.getLaunchIntentForPackage(appInfo.getApkPackageName());
			startActivity(run_intent);
			popupWindowDismiss();

			break;
		// 分享
		case R.id.ll_share:

			Intent share_intent = new Intent("android.intent.action.SEND");
			share_intent.setType("text/plain");
			share_intent.putExtra("android.intent.extra.SUBJECT", "f分享");
			share_intent.putExtra("android.intent.extra.TEXT", "Hi~~推荐您使用软件："
					+ appInfo.getApkName() + "，下载地址："
					+ "https://play.google.com/store/apps/details?id="
					+ appInfo.getApkPackageName());
			startActivity(Intent.createChooser(share_intent, "分享"));
			popupWindowDismiss();

			break;
		// 详情
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
