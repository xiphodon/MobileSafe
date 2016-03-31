package com.gc.p01_mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.p01_mobilesafe.R;

/**
 * 缓存清理功能
 * 
 * @author guochang
 * 
 */
public class CleanCacheActivity extends Activity {

	private PackageManager packageManager;
	private List<CacheInfo> cacheList;
	private ListView lv_listview;
	private CacheAdapter cacheAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initUI();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_clean_cache);

		lv_listview = (ListView) findViewById(R.id.lv_listview);
		ll_ProgressBar = (LinearLayout) findViewById(R.id.ll_ProgressBar);
		btn_cleanAll = (Button) findViewById(R.id.btn_cleanAll);

		cacheList = new ArrayList<CacheInfo>();

		packageManager = getPackageManager();

		new Thread() {
			public void run() {

				/**
				 * 接收2个参数 第一个参数接收一个包名 第二个参数接收aidl的对象
				 */
				// * @hide
				// */
				// public abstract void getPackageSizeInfo(String packageName,
				// IPackageStatsObserver observer);
				//
				// packageManager.getPackageSizeInfo();

				// 安装到手机上面所有的应用程序
				List<PackageInfo> installedPackages = packageManager
						.getInstalledPackages(0);

				for (PackageInfo packageInfo : installedPackages) {
					getCacheSize(packageInfo);
				}
				//等待上面for循环中的各个子线程执行结束
				SystemClock.sleep(5000);

				// 遍历结束，通知UI线程，ListView加载adapter
				handler.sendEmptyMessage(0);

			};
		}.start();

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// ListView加载Adapter
			cacheAdapter = new CacheAdapter();
			lv_listview.setAdapter(cacheAdapter);
			ll_ProgressBar.setVisibility(View.INVISIBLE);
			btn_cleanAll.setVisibility(View.VISIBLE);
		};
	};
	private LinearLayout ll_ProgressBar;
	private Button btn_cleanAll;

	private class CacheAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cacheList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return cacheList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder viewHolder = null;
			if (convertView == null) {
				view = View.inflate(CleanCacheActivity.this,
						R.layout.clean_cache_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.icon = (ImageView) view.findViewById(R.id.iv_icon);
				viewHolder.apkName = (TextView) view
						.findViewById(R.id.tv_apkName);
				viewHolder.cacheSize = (TextView) view
						.findViewById(R.id.tv_cache_size);
				viewHolder.cleanCache = (ImageView) view
						.findViewById(R.id.iv_clean_cache);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.icon.setImageDrawable(cacheList.get(position).icon);
			viewHolder.apkName.setText(cacheList.get(position).apkName);
			viewHolder.cacheSize.setText("缓存大小："
					+ Formatter.formatFileSize(CleanCacheActivity.this,
							cacheList.get(position).cacheSize));
			viewHolder.cleanCache.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 跳转到应用详情
					Intent detail_intent = new Intent();
					detail_intent
							.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
					detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
					detail_intent.setData(Uri.parse("package:"
							+ cacheList.get(position).packageName));
					startActivity(detail_intent);
				}
			});

			return view;
		}

	}

	static class ViewHolder {
		ImageView icon;
		TextView apkName;
		TextView cacheSize;
		ImageView cleanCache;
	}

	/**
	 * 获得缓存大小
	 * 
	 * @param packageInfo
	 */
	private void getCacheSize(PackageInfo packageInfo) {
		try {
			// 通过反射获取到当前(隐藏)的方法
			Method method = PackageManager.class.getDeclaredMethod(
					"getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			// 参数一：当前方法的调用者，参数二：该方法所有参数(包名，aidl实现类)
			method.invoke(packageManager,
					packageInfo.applicationInfo.packageName,
					new MyIPackageStatsObserver(packageInfo));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
		private PackageInfo packageInfo;

		public MyIPackageStatsObserver(PackageInfo packageInfo) {
			this.packageInfo = packageInfo;
		}

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// 获取当前手机应用的缓存大小
			long cacheSize = pStats.cacheSize;
			// //获取当前手机应用的数据大小
			// long dataSize = pStats.dataSize;
			//
			// if (cacheSize > 0) {
			// System.out.println("应用名字："
			// + packageInfo.applicationInfo.loadLabel(packageManager)
			// + ",缓存大小："
			// + Formatter.formatFileSize(CleanCacheActivity.this,
			// cacheSize));
			// }
			//
			if (cacheSize > 0) {
				CacheInfo cacheInfo = new CacheInfo();
				cacheInfo.icon = packageInfo.applicationInfo
						.loadIcon(packageManager);
				cacheInfo.apkName = packageInfo.applicationInfo.loadLabel(
						packageManager).toString();
				cacheInfo.packageName = packageInfo.packageName;
				cacheInfo.cacheSize = cacheSize;
				cacheList.add(cacheInfo);
			}

		}

	}

	/**
	 * 缓存应用封装
	 * 
	 * @author guochang
	 * 
	 */
	static class CacheInfo {
		Drawable icon;
		String apkName;
		long cacheSize;
		String packageName;
	}

	/**
	 * 清理全部按钮
	 * 权限： (android.permission.CLEAR_APP_CACHE)
	 * @param view
	 */
	public void cleanAll(View view) {
		// 获得当前类中的所有方法
		Method[] methods = PackageManager.class.getMethods();
		// 迭代，找到freeStorageAndNotify方法(清理全部缓存)
		for (Method method : methods) {
			if (method.getName().equals("freeStorageAndNotify")) {
				try {
					method.invoke(packageManager, Integer.MAX_VALUE,
							new MyIPackageDataObserver());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		cacheList.clear();
		cacheAdapter.notifyDataSetChanged();
		Toast.makeText(CleanCacheActivity.this, "缓存已全部清理~", Toast.LENGTH_SHORT)
				.show();
	}

	private class MyIPackageDataObserver extends IPackageDataObserver.Stub {

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub

		}

	}
}
