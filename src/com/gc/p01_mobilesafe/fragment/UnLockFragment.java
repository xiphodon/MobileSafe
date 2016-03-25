package com.gc.p01_mobilesafe.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.bean.AppInfo;
import com.gc.p01_mobilesafe.db.dao.AppLockDAO;
import com.gc.p01_mobilesafe.engine.AppInfos;

/**
 * 未上锁应用界面
 * 
 * @author guochang
 * 
 */
public class UnLockFragment extends Fragment {

	private TextView tv_unlock_grament;
	private ListView lv_unlock;
	private List<AppInfo> appInfos;
	private AppLockDAO appLockDAO;
	private List<AppInfo> unLockList;
	private UnlockAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// 填充布局
		View view = inflater.inflate(R.layout.unlock_fragment, null);

		tv_unlock_grament = (TextView) view
				.findViewById(R.id.tv_unlock_grament);
		lv_unlock = (ListView) view.findViewById(R.id.lv_unlock);

		return view;
	}

	/**
	 * 初始化数据放在 onStart方法里
	 */
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// 取得所有应用
		appInfos = AppInfos.getAppInfos(getActivity());
		// 拿到程序锁的DAO
		appLockDAO = new AppLockDAO(getActivity());
		// 为加锁应用集合
		unLockList = new ArrayList<AppInfo>();
		for (AppInfo appInfo : appInfos) {
			//该应用是否在加锁数据库中
			if (appLockDAO.find(appInfo.getApkPackageName())) {

			} else {
				unLockList.add(appInfo);
			}
		}

		adapter = new UnlockAdapter();
		lv_unlock.setAdapter(adapter);

	}

	public class UnlockAdapter extends BaseAdapter {

		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			tv_unlock_grament.setText("未加锁应用：" + unLockList.size() + "个");
			return unLockList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return unLockList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final View view;
			if (convertView == null) {
				view = View.inflate(getActivity(), R.layout.unlock_list_item,
						null);
				viewHolder = new ViewHolder();
				viewHolder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_icon);
				viewHolder.tv_apkName = (TextView) view
						.findViewById(R.id.tv_apkName);
				viewHolder.iv_unlock = (ImageView) view
						.findViewById(R.id.iv_unlock);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.iv_icon.setImageDrawable(unLockList.get(position)
					.getIcon());
			viewHolder.tv_apkName
					.setText(unLockList.get(position).getApkName());

			final AppInfo appInfo = unLockList.get(position);
			// 加锁按钮设置点击监听
			viewHolder.iv_unlock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					// 移动动画，水平方向，相对自己，从0个自己向右移动到1个自己的位置
					TranslateAnimation translateAnimation = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 1.0f,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0);
					
					translateAnimation.setDuration(300);
					view.startAnimation(translateAnimation);
					
					new Thread(){
						public void run() {
							//等待动画执行结束
							SystemClock.sleep(310);
							
							//切换到UI线程
							getActivity().runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// 添加到加锁数据库
									appLockDAO.add(appInfo.getApkPackageName());
									// 移除当前界面集合
									unLockList.remove(position);
									// 更新当前界面
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
		ImageView iv_unlock;
	}
}
