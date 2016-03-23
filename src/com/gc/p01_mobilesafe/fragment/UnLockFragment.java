package com.gc.p01_mobilesafe.fragment;

import java.util.List;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.bean.AppInfo;
import com.gc.p01_mobilesafe.engine.AppInfos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 未上锁应用界面
 * @author guochang
 *
 */
public class UnLockFragment extends Fragment{

	private TextView tv_unlock_grament;
	private ListView lv_unlock;
	private List<AppInfo> appInfos;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//填充布局
		View view = inflater.inflate(R.layout.unlock_fragment, null);
		
		tv_unlock_grament = (TextView) view.findViewById(R.id.tv_unlock_grament);
		lv_unlock = (ListView) view.findViewById(R.id.lv_unlock);
		
		return view;
	}
	
	/**
	 * 初始化数据放在  onStart方法里
	 */
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//取得所有应用
		appInfos = AppInfos.getAppInfos(getActivity());
		UnlockAdapter adapter = new UnlockAdapter();
		lv_unlock.setAdapter(adapter);
		
	}
	
	public class UnlockAdapter extends BaseAdapter{

		private ViewHolder viewHolder;
		private View view;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null){
				view = View.inflate(getActivity(), R.layout.unlock_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				viewHolder.tv_apkName = (TextView) view.findViewById(R.id.tv_apkName);
				viewHolder.iv_unlock = (ImageView) view.findViewById(R.id.iv_unlock);
				view.setTag(viewHolder);
			}else{
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			
			viewHolder.iv_icon.setImageDrawable(appInfos.get(position).getIcon());
			viewHolder.tv_apkName.setText(appInfos.get(position).getApkName());
			
			return view;
		}
		
	}
	
	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_apkName;
		ImageView iv_unlock;
	}
}
