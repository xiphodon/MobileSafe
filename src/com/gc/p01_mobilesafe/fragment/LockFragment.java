package com.gc.p01_mobilesafe.fragment;

import java.util.ArrayList;
import java.util.List;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.bean.AppInfo;
import com.gc.p01_mobilesafe.db.dao.AppLockDAO;
import com.gc.p01_mobilesafe.engine.AppInfos;
import com.gc.p01_mobilesafe.fragment.UnLockFragment.ViewHolder;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ������Ӧ�ý���
 * 
 * @author guochang
 * 
 */
public class LockFragment extends Fragment {

	private TextView tv_lock_grament;
	private ListView lv_lock;
	private List<AppInfo> appInfos;
	private AppLockDAO appLockDAO;
	private List<AppInfo> lockList;
	private LockAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// ��䲼��
		View view = inflater.inflate(R.layout.lock_fragment, null);

		tv_lock_grament = (TextView) view.findViewById(R.id.tv_lock_grament);
		lv_lock = (ListView) view.findViewById(R.id.lv_lock);

		return view;
	}

	/**
	 * ��ʼ�����ݷ��� onStart������
	 */
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// ȡ������Ӧ��
		appInfos = AppInfos.getAppInfos(getActivity());
		// �õ���������DAO
		appLockDAO = new AppLockDAO(getActivity());
		// Ϊ����Ӧ�ü���
		lockList = new ArrayList<AppInfo>();
		for (AppInfo appInfo : appInfos) {
			//��Ӧ���Ƿ��ڼ������ݿ���
			if (appLockDAO.find(appInfo.getApkPackageName())) {
				lockList.add(appInfo);
			} else {
				
			}
		}

		adapter = new LockAdapter();
		lv_lock.setAdapter(adapter);
	}

	private class LockAdapter extends BaseAdapter {

		private ViewHolder viewHolder;
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			tv_lock_grament.setText("�Ѽ���Ӧ�ã�" + lockList.size() + "��");
			return lockList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lockList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final View view;
			if (convertView == null) {
				view = View.inflate(getActivity(), R.layout.lock_list_item,
						null);
				viewHolder = new ViewHolder();
				viewHolder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_icon);
				viewHolder.tv_apkName = (TextView) view
						.findViewById(R.id.tv_apkName);
				viewHolder.iv_lock = (ImageView) view
						.findViewById(R.id.iv_lock);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.iv_icon.setImageDrawable(lockList.get(position)
					.getIcon());
			viewHolder.tv_apkName
					.setText(lockList.get(position).getApkName());

			final AppInfo appInfo = lockList.get(position);
			// ������ť���õ������
			viewHolder.iv_lock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					// �ƶ�������ˮƽ��������Լ�����1���Լ������ƶ���0���Լ���λ��
					TranslateAnimation translateAnimation = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, -1.0f,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0);
					
					translateAnimation.setDuration(300);
					view.startAnimation(translateAnimation);
					
					new Thread(){
						public void run() {
							//�ȴ�����ִ�н���
							SystemClock.sleep(310);
							
							//�л���UI�߳�
							getActivity().runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// �Ӽ������ݿ���ɾ��
									appLockDAO.delect(appInfo.getApkPackageName());
									// �Ƴ���ǰ���漯��
									lockList.remove(position);
									// ���µ�ǰ����
									adapter.notifyDataSetChanged();
								}
							});
						};
					}.start();
					
				}
			});

			return view;
		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_apkName;
		ImageView iv_lock;
	}
}
