package com.gc.p01_mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.bean.TaskInfo;
import com.gc.p01_mobilesafe.engine.TaskInfoParser;
import com.gc.p01_mobilesafe.utils.SystemInfoUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 进程管理
 * 
 * @author guochang
 * 
 */
public class TaskManagerActivity extends Activity {
	@ViewInject(R.id.tv_task_process_count)
	private TextView tv_task_process_count;

	@ViewInject(R.id.tv_task_memory)
	private TextView tv_task_memory;

	@ViewInject(R.id.lv_process)
	private ListView lv_process;

	@ViewInject(R.id.ll_ProgressBar)
	private LinearLayout ll_ProgressBar;

	@ViewInject(R.id.tv_processTitle)
	private TextView tv_processTitle;

	@ViewInject(R.id.ll_four_button)
	private LinearLayout ll_four_button;

	private List<TaskInfo> taskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;

	private TaskManagerAdapter adapter;

	private int processCount;

	private long availMem;

	private long totalMem;

	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initUI();
		initData();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//刷新界面
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
		
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_task_manager);
		ViewUtils.inject(this);

		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		
		//当前进程数
		processCount = SystemInfoUtils.getProcessCount(this);

		tv_task_process_count.setText("当前进程：" + processCount + "个");

		//可用内存
		availMem = SystemInfoUtils.getAvailMem(this);
		//总内存
		totalMem = SystemInfoUtils.getTotalMem(this);

		tv_task_memory.setText("可用内存："
				+ Formatter.formatFileSize(TaskManagerActivity.this, availMem)
				+ "/"
				+ Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

		// 设置ListView的滚动监听
		lv_process.setOnScrollListener(new OnScrollListener() {

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

				if (userTaskInfos != null && systemTaskInfos != null) {
					if (firstVisibleItem > userTaskInfos.size() + 1) {
						// 系统进程
						tv_processTitle.setText("系统应用：（"
								+ systemTaskInfos.size() + "）");
						tv_processTitle.setTextColor(Color.BLACK);
						tv_processTitle.setBackgroundColor(Color.GRAY);
						tv_processTitle.setTextSize(15);
					} else {
						// 用户进程
						tv_processTitle.setText("用户应用：（" + userTaskInfos.size()
								+ "）");
						tv_processTitle.setTextColor(Color.BLACK);
						tv_processTitle.setBackgroundColor(Color.GRAY);
						tv_processTitle.setTextSize(15);
					}
				}

			}
		});

		// 设置ListView的条目点击监听
		lv_process.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				// 取得点击条目
				Object object = lv_process.getItemAtPosition(position);

				if (object != null && object instanceof TaskInfo) {
					
					TaskInfo taskInfo = (TaskInfo) object;

					ViewHolder holder = (ViewHolder) view.getTag();
					
					//把本应用置为不可点击，不可勾选
					if(getPackageName().equals(taskInfo.getPackageName())){
						return;
					}
					

					if (taskInfo.isChecked()) {
						taskInfo.setChecked(false);
						holder.cb_process_status.setChecked(false);
					} else {
						taskInfo.setChecked(true);
						holder.cb_process_status.setChecked(true);
					}
				}
			}
		});
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
		new Thread() {

			public void run() {

				taskInfos = TaskInfoParser
						.getTaskInfos(TaskManagerActivity.this);

				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();

				// 拆分为用户进程和系统进程
				for (TaskInfo taskInfo : taskInfos) {
					if (taskInfo.isUserApp()) {
						// 用户进程
						userTaskInfos.add(taskInfo);
					} else {
						// 系统进程
						systemTaskInfos.add(taskInfo);
					}
				}

				// 在主线程刷新UI，（此处也可选择使用 handler ）
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						// 隐藏进度条，显示四按钮
						ll_ProgressBar.setVisibility(View.INVISIBLE);
						ll_four_button.setVisibility(View.VISIBLE);

						// 给 ListView 填充适配器
						adapter = new TaskManagerAdapter();
						lv_process.setAdapter(adapter);
					}
				});
			};
		}.start();
	}

	/**
	 * 自定义 ListView 适配器
	 * 
	 * @author guochang
	 * 
	 */
	private class TaskManagerAdapter extends BaseAdapter {

		private TaskInfo taskInfo;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			
			boolean show_system_process = sharedPreferences.getBoolean("show_system_process", true);
			//是否需要显示系统进程
			if(show_system_process){
				return userTaskInfos.size() + systemTaskInfos.size() + 2;
			}else{
				return userTaskInfos.size() + 1;
			}
			
			
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (position == 0 || position == userTaskInfos.size() + 1) {
				return null;
			}

			if (position < userTaskInfos.size() + 1) {
				taskInfo = userTaskInfos.get(position - 1);
			} else {
				taskInfo = systemTaskInfos.get(position - 1
						- userTaskInfos.size() - 1);
			}

			return taskInfo;
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
				// position等于0，则显示"用户进程"
				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setText("用户应用：（" + userTaskInfos.size() + "）");
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);

				return textView;

			} else if (position == userTaskInfos.size() + 1) {
				// position等于 用户程序的size（） + 1，则显示"系统进程"
				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setText("系统应用：（" + systemTaskInfos.size() + "）");
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
				view = View.inflate(TaskManagerActivity.this,
						R.layout.taskmanager_list_item, null);

				holder = new ViewHolder();

				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_apkName = (TextView) view
						.findViewById(R.id.tv_apkName);
				holder.tv_memory_size = (TextView) view
						.findViewById(R.id.tv_memory_size);
				holder.cb_process_status = (CheckBox) view
						.findViewById(R.id.cb_process_status);

				view.setTag(holder);
			}

			// 计算添加特殊条目后的 position 取值
			if (position < userTaskInfos.size() + 1) {
				taskInfo = userTaskInfos.get(position - 1);
			} else {
				taskInfo = systemTaskInfos.get(position - 1
						- userTaskInfos.size() - 1);
			}

			// 设置条目各个数据
			holder.iv_icon.setImageDrawable(taskInfo.getIcon());
			holder.tv_apkName.setText(taskInfo.getAppName());
			holder.tv_memory_size.setText(String.valueOf(Formatter
					.formatFileSize(TaskManagerActivity.this,
							taskInfo.getMemorySize())));
			holder.cb_process_status.setChecked(taskInfo.isChecked());
			
			//隐藏本应用的选择框
			if(getPackageName().equals(taskInfo.getPackageName())){
				holder.cb_process_status.setVisibility(View.INVISIBLE);
			}

			return view;
		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_apkName;
		TextView tv_memory_size;
		CheckBox cb_process_status;
	}

	/**
	 * 全选按钮
	 * 
	 * @param view
	 */
	public void selectAll(View view) {
		for (TaskInfo taskInfo : userTaskInfos) {
			//通过包名，跳过本应用的选择
			if(getPackageName().equals(taskInfo.getPackageName())){
				continue;
			}
			taskInfo.setChecked(true);
		}

		for (TaskInfo taskInfo : systemTaskInfos) {
			taskInfo.setChecked(true);
		}

		// 刷新UI
		adapter.notifyDataSetChanged();
	}

	/**
	 * 反选按钮
	 * 
	 * @param view
	 */
	public void selectOppsite(View view) {
		for (TaskInfo taskInfo : userTaskInfos) {
			//通过包名，跳过本应用的选择
			if(getPackageName().equals(taskInfo.getPackageName())){
				continue;
			}
			taskInfo.setChecked(!taskInfo.isChecked());
		}

		for (TaskInfo taskInfo : systemTaskInfos) {
			taskInfo.setChecked(!taskInfo.isChecked());
		}

		// 刷新UI
		adapter.notifyDataSetChanged();
	}

	/**
	 * 一键清理按钮 杀进程需要权限（android.permission.KILL_BACKGROUND_PROCESSES）
	 * 
	 * @param view
	 */
	public void killProcess(View view) {
		// 取得进程管理器
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// 清理进程数量
		int totalKillProcess = 0;

		// 清理内存空间
		long totalKillMemory = 0L;

		// 预备清理的应用的链表集合
		List<TaskInfo> killList = new ArrayList<TaskInfo>();

		for (TaskInfo taskInfo : userTaskInfos) {
			if (taskInfo.isChecked()) {
				/**
				 * 迭代过程中 Size 不可变，不可移除集合
				 */
				//
				// // 通过包名杀死该进程
				// activityManager.killBackgroundProcesses(taskInfo.getPackageName());
				// // 移除链表集合
				// userTaskInfos.remove(taskInfo);

				killList.add(taskInfo);
				totalKillProcess++;
				totalKillMemory += taskInfo.getMemorySize();
			}
		}

		for (TaskInfo taskInfo : systemTaskInfos) {
			if (taskInfo.isChecked()) {
				/**
				 * 迭代过程中 Size 不可变，不可移除集合
				 */
				//
				// // 通过包名杀死该进程
				// activityManager.killBackgroundProcesses(taskInfo.getPackageName());
				// // 移除链表集合
				// systemTaskInfos.remove(taskInfo);

				killList.add(taskInfo);
				totalKillProcess++;
				totalKillMemory += taskInfo.getMemorySize();
			}
		}

		for (TaskInfo taskInfo : killList) {
			if (taskInfo.isUserApp()) {
				// 移除链表集合
				userTaskInfos.remove(taskInfo);
			} else {
				// 移除链表集合
				systemTaskInfos.remove(taskInfo);
			}
			// 通过包名杀死该进程
			activityManager.killBackgroundProcesses(taskInfo.getPackageName());
		}
		
		processCount -= totalKillProcess;
		tv_task_process_count.setText("当前进程：" + processCount + "个");
		
		availMem += totalKillMemory;
		tv_task_memory.setText("可用内存："
				+ Formatter.formatFileSize(TaskManagerActivity.this, availMem)
				+ "/"
				+ Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
		
		Toast.makeText(
				TaskManagerActivity.this,
				"共清理"
						+ totalKillProcess
						+ "个应用，释放"
						+ Formatter.formatFileSize(TaskManagerActivity.this,
								totalKillMemory) + "内存", Toast.LENGTH_SHORT).show();

		// 刷新UI
		adapter.notifyDataSetChanged();

	}
	
	/**
	 * 设置按钮,打开设置界面
	 * @param view
	 */
	public void openSetting(View view){
		startActivity(new Intent(this,TaskManagerSettingActivity.class));
	}
	
	
	

}
