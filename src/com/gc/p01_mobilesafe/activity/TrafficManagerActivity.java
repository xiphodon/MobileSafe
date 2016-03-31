package com.gc.p01_mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.p01_mobilesafe.R;

/**
 * ����ͳ��
 * 
 * @author guochang
 * 
 */
public class TrafficManagerActivity extends Activity {

	private ListView lv_lists;
	private List<AppTrafficInfo> list;
	private AppTrafficInfo appTrafficInfo;
	private LinearLayout ll_ProgressBar;

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

		new Thread() {
			public void run() {
				// ��ȡ���еİ�װ���ֻ��ϵ�Ӧ���������Ϣ�����һ�ȡ��Щ��������Ȩ����Ϣ
				PackageManager pm = getPackageManager();// ��ȡϵͳӦ�ð�����
				// ��ȡÿ�����ڵ�androidmanifest.xml��Ϣ������Ȩ�޵ȵ�
				List<PackageInfo> pinfos = pm
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
								| PackageManager.GET_PERMISSIONS);
				// ��������Ҫ����������Ϣ
				list = new ArrayList<AppTrafficInfo>();
				// ����ÿ��Ӧ�ð���Ϣ
				for (PackageInfo info : pinfos) {
					// ����ÿ���������Ӧ��androidManifest.xml�����Ȩ��
					String[] premissions = info.requestedPermissions;
					if (premissions != null && premissions.length > 0) {
						// �ҳ���Ҫ��������Ӧ�ó���
						for (String premission : premissions) {
							if ("android.permission.INTERNET"
									.equals(premission)) {
								// ��ȡÿ��Ӧ�ó����ڲ���ϵͳ�ڵĽ���id
								int uId = info.applicationInfo.uid;
								// �������-1������֧��ʹ�ø÷�����ע�������2.2���ϵ�
								long rx = TrafficStats.getUidRxBytes(uId);
								// �������-1������֧��ʹ�ø÷�����ע�������2.2���ϵ�
								long tx = TrafficStats.getUidTxBytes(uId);
								
								if(rx<0 || tx<0){  
	                                continue;  
	                            }else{  
	                            	// ʵ����AppTrafficInfo
									appTrafficInfo = new AppTrafficInfo();
									appTrafficInfo.rx = rx;
									appTrafficInfo.tx = tx;
									appTrafficInfo.total = tx + rx;
									appTrafficInfo.apkName = info.applicationInfo
											.loadLabel(pm).toString();
									appTrafficInfo.icon = info.applicationInfo
											.loadIcon(pm);

									// ��������
									list.add(appTrafficInfo);
	                            }  

							}
						}
					}
				}

				handler.sendEmptyMessage(0);

			};
		}.start();

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			TrafficAdapter trafficAdapter = new TrafficAdapter();
			lv_lists.setAdapter(trafficAdapter);
			ll_ProgressBar.setVisibility(View.INVISIBLE);
		};
	};


	class AppTrafficInfo {
		Drawable icon;
		String apkName;
		long rx;
		long tx;
		long total;
	}

	class TrafficAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;
			if (convertView == null) {
				view = View.inflate(TrafficManagerActivity.this,
						R.layout.traffic_list_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_apkName = (TextView) view
						.findViewById(R.id.tv_apkName);
				holder.tv_rx = (TextView) view.findViewById(R.id.tv_rx);
				holder.tv_tx = (TextView) view.findViewById(R.id.tv_tx);
				holder.tv_total_traffic = (TextView) view
						.findViewById(R.id.tv_total_traffic);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			holder.iv_icon.setImageDrawable(list.get(position).icon);
			holder.tv_apkName.setText(list.get(position).apkName);
			holder.tv_rx.setText("����:" + Formatter.formatFileSize(
					TrafficManagerActivity.this, list.get(position).rx));
			holder.tv_tx.setText("�ϴ�:" + Formatter.formatFileSize(
					TrafficManagerActivity.this, list.get(position).tx));
			holder.tv_total_traffic.setText("�ܼ�:" + Formatter.formatFileSize(
					TrafficManagerActivity.this, list.get(position).total));

			return view;
		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_apkName;
		TextView tv_tx;
		TextView tv_rx;
		TextView tv_total_traffic;
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_traffic_manager);
		lv_lists = (ListView) findViewById(R.id.lv_lists);
		ll_ProgressBar = (LinearLayout) findViewById(R.id.ll_ProgressBar);

	}
}
