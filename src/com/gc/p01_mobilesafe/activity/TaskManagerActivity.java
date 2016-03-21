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
 * ���̹���
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
		
		//ˢ�½���
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
		
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_task_manager);
		ViewUtils.inject(this);

		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		
		//��ǰ������
		processCount = SystemInfoUtils.getProcessCount(this);

		tv_task_process_count.setText("��ǰ���̣�" + processCount + "��");

		//�����ڴ�
		availMem = SystemInfoUtils.getAvailMem(this);
		//���ڴ�
		totalMem = SystemInfoUtils.getTotalMem(this);

		tv_task_memory.setText("�����ڴ棺"
				+ Formatter.formatFileSize(TaskManagerActivity.this, availMem)
				+ "/"
				+ Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

		// ����ListView�Ĺ�������
		lv_process.setOnScrollListener(new OnScrollListener() {

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

				if (userTaskInfos != null && systemTaskInfos != null) {
					if (firstVisibleItem > userTaskInfos.size() + 1) {
						// ϵͳ����
						tv_processTitle.setText("ϵͳӦ�ã���"
								+ systemTaskInfos.size() + "��");
						tv_processTitle.setTextColor(Color.BLACK);
						tv_processTitle.setBackgroundColor(Color.GRAY);
						tv_processTitle.setTextSize(15);
					} else {
						// �û�����
						tv_processTitle.setText("�û�Ӧ�ã���" + userTaskInfos.size()
								+ "��");
						tv_processTitle.setTextColor(Color.BLACK);
						tv_processTitle.setBackgroundColor(Color.GRAY);
						tv_processTitle.setTextSize(15);
					}
				}

			}
		});

		// ����ListView����Ŀ�������
		lv_process.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				// ȡ�õ����Ŀ
				Object object = lv_process.getItemAtPosition(position);

				if (object != null && object instanceof TaskInfo) {
					
					TaskInfo taskInfo = (TaskInfo) object;

					ViewHolder holder = (ViewHolder) view.getTag();
					
					//�ѱ�Ӧ����Ϊ���ɵ�������ɹ�ѡ
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
	 * ��ʼ������
	 */
	private void initData() {
		// TODO Auto-generated method stub
		new Thread() {

			public void run() {

				taskInfos = TaskInfoParser
						.getTaskInfos(TaskManagerActivity.this);

				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();

				// ���Ϊ�û����̺�ϵͳ����
				for (TaskInfo taskInfo : taskInfos) {
					if (taskInfo.isUserApp()) {
						// �û�����
						userTaskInfos.add(taskInfo);
					} else {
						// ϵͳ����
						systemTaskInfos.add(taskInfo);
					}
				}

				// �����߳�ˢ��UI�����˴�Ҳ��ѡ��ʹ�� handler ��
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						// ���ؽ���������ʾ�İ�ť
						ll_ProgressBar.setVisibility(View.INVISIBLE);
						ll_four_button.setVisibility(View.VISIBLE);

						// �� ListView ���������
						adapter = new TaskManagerAdapter();
						lv_process.setAdapter(adapter);
					}
				});
			};
		}.start();
	}

	/**
	 * �Զ��� ListView ������
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
			//�Ƿ���Ҫ��ʾϵͳ����
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
				// position����0������ʾ"�û�����"
				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setText("�û�Ӧ�ã���" + userTaskInfos.size() + "��");
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextSize(15);

				return textView;

			} else if (position == userTaskInfos.size() + 1) {
				// position���� �û������size���� + 1������ʾ"ϵͳ����"
				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setText("ϵͳӦ�ã���" + systemTaskInfos.size() + "��");
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

			// �������������Ŀ��� position ȡֵ
			if (position < userTaskInfos.size() + 1) {
				taskInfo = userTaskInfos.get(position - 1);
			} else {
				taskInfo = systemTaskInfos.get(position - 1
						- userTaskInfos.size() - 1);
			}

			// ������Ŀ��������
			holder.iv_icon.setImageDrawable(taskInfo.getIcon());
			holder.tv_apkName.setText(taskInfo.getAppName());
			holder.tv_memory_size.setText(String.valueOf(Formatter
					.formatFileSize(TaskManagerActivity.this,
							taskInfo.getMemorySize())));
			holder.cb_process_status.setChecked(taskInfo.isChecked());
			
			//���ر�Ӧ�õ�ѡ���
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
	 * ȫѡ��ť
	 * 
	 * @param view
	 */
	public void selectAll(View view) {
		for (TaskInfo taskInfo : userTaskInfos) {
			//ͨ��������������Ӧ�õ�ѡ��
			if(getPackageName().equals(taskInfo.getPackageName())){
				continue;
			}
			taskInfo.setChecked(true);
		}

		for (TaskInfo taskInfo : systemTaskInfos) {
			taskInfo.setChecked(true);
		}

		// ˢ��UI
		adapter.notifyDataSetChanged();
	}

	/**
	 * ��ѡ��ť
	 * 
	 * @param view
	 */
	public void selectOppsite(View view) {
		for (TaskInfo taskInfo : userTaskInfos) {
			//ͨ��������������Ӧ�õ�ѡ��
			if(getPackageName().equals(taskInfo.getPackageName())){
				continue;
			}
			taskInfo.setChecked(!taskInfo.isChecked());
		}

		for (TaskInfo taskInfo : systemTaskInfos) {
			taskInfo.setChecked(!taskInfo.isChecked());
		}

		// ˢ��UI
		adapter.notifyDataSetChanged();
	}

	/**
	 * һ������ť ɱ������ҪȨ�ޣ�android.permission.KILL_BACKGROUND_PROCESSES��
	 * 
	 * @param view
	 */
	public void killProcess(View view) {
		// ȡ�ý��̹�����
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// �����������
		int totalKillProcess = 0;

		// �����ڴ�ռ�
		long totalKillMemory = 0L;

		// Ԥ�������Ӧ�õ�������
		List<TaskInfo> killList = new ArrayList<TaskInfo>();

		for (TaskInfo taskInfo : userTaskInfos) {
			if (taskInfo.isChecked()) {
				/**
				 * ���������� Size ���ɱ䣬�����Ƴ�����
				 */
				//
				// // ͨ������ɱ���ý���
				// activityManager.killBackgroundProcesses(taskInfo.getPackageName());
				// // �Ƴ�������
				// userTaskInfos.remove(taskInfo);

				killList.add(taskInfo);
				totalKillProcess++;
				totalKillMemory += taskInfo.getMemorySize();
			}
		}

		for (TaskInfo taskInfo : systemTaskInfos) {
			if (taskInfo.isChecked()) {
				/**
				 * ���������� Size ���ɱ䣬�����Ƴ�����
				 */
				//
				// // ͨ������ɱ���ý���
				// activityManager.killBackgroundProcesses(taskInfo.getPackageName());
				// // �Ƴ�������
				// systemTaskInfos.remove(taskInfo);

				killList.add(taskInfo);
				totalKillProcess++;
				totalKillMemory += taskInfo.getMemorySize();
			}
		}

		for (TaskInfo taskInfo : killList) {
			if (taskInfo.isUserApp()) {
				// �Ƴ�������
				userTaskInfos.remove(taskInfo);
			} else {
				// �Ƴ�������
				systemTaskInfos.remove(taskInfo);
			}
			// ͨ������ɱ���ý���
			activityManager.killBackgroundProcesses(taskInfo.getPackageName());
		}
		
		processCount -= totalKillProcess;
		tv_task_process_count.setText("��ǰ���̣�" + processCount + "��");
		
		availMem += totalKillMemory;
		tv_task_memory.setText("�����ڴ棺"
				+ Formatter.formatFileSize(TaskManagerActivity.this, availMem)
				+ "/"
				+ Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
		
		Toast.makeText(
				TaskManagerActivity.this,
				"������"
						+ totalKillProcess
						+ "��Ӧ�ã��ͷ�"
						+ Formatter.formatFileSize(TaskManagerActivity.this,
								totalKillMemory) + "�ڴ�", Toast.LENGTH_SHORT).show();

		// ˢ��UI
		adapter.notifyDataSetChanged();

	}
	
	/**
	 * ���ð�ť,�����ý���
	 * @param view
	 */
	public void openSetting(View view){
		startActivity(new Intent(this,TaskManagerSettingActivity.class));
	}
	
	
	

}
