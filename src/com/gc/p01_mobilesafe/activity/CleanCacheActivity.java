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
 * ����������
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
	 * ��ʼ��UI
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
				 * ����2������ ��һ����������һ������ �ڶ�����������aidl�Ķ���
				 */
				// * @hide
				// */
				// public abstract void getPackageSizeInfo(String packageName,
				// IPackageStatsObserver observer);
				//
				// packageManager.getPackageSizeInfo();

				// ��װ���ֻ��������е�Ӧ�ó���
				List<PackageInfo> installedPackages = packageManager
						.getInstalledPackages(0);

				for (PackageInfo packageInfo : installedPackages) {
					getCacheSize(packageInfo);
				}
				//�ȴ�����forѭ���еĸ������߳�ִ�н���
				SystemClock.sleep(5000);

				// ����������֪ͨUI�̣߳�ListView����adapter
				handler.sendEmptyMessage(0);

			};
		}.start();

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// ListView����Adapter
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
			viewHolder.cacheSize.setText("�����С��"
					+ Formatter.formatFileSize(CleanCacheActivity.this,
							cacheList.get(position).cacheSize));
			viewHolder.cleanCache.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// ��ת��Ӧ������
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
	 * ��û����С
	 * 
	 * @param packageInfo
	 */
	private void getCacheSize(PackageInfo packageInfo) {
		try {
			// ͨ�������ȡ����ǰ(����)�ķ���
			Method method = PackageManager.class.getDeclaredMethod(
					"getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			// ����һ����ǰ�����ĵ����ߣ����������÷������в���(������aidlʵ����)
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
			// ��ȡ��ǰ�ֻ�Ӧ�õĻ����С
			long cacheSize = pStats.cacheSize;
			// //��ȡ��ǰ�ֻ�Ӧ�õ����ݴ�С
			// long dataSize = pStats.dataSize;
			//
			// if (cacheSize > 0) {
			// System.out.println("Ӧ�����֣�"
			// + packageInfo.applicationInfo.loadLabel(packageManager)
			// + ",�����С��"
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
	 * ����Ӧ�÷�װ
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
	 * ����ȫ����ť
	 * Ȩ�ޣ� (android.permission.CLEAR_APP_CACHE)
	 * @param view
	 */
	public void cleanAll(View view) {
		// ��õ�ǰ���е����з���
		Method[] methods = PackageManager.class.getMethods();
		// �������ҵ�freeStorageAndNotify����(����ȫ������)
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
		Toast.makeText(CleanCacheActivity.this, "������ȫ������~", Toast.LENGTH_SHORT)
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
