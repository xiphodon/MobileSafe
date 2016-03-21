package com.gc.p01_mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.receiver.MyAppWidgetProvider;
import com.gc.p01_mobilesafe.utils.SystemInfoUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

/**
 * 桌面小控件（清理进程服务）
 * 
 * @author Administrator
 * 
 */
public class KillProcessAppWidgetService extends Service {

	private AppWidgetManager widgetManager;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		// 桌面小控件管理器
		widgetManager = AppWidgetManager.getInstance(this);

		// 定时器
		Timer timer = new Timer();
		// 初始化一个定时任务
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("KillProcessAppWidgetService");

				// 参数：1，上下文，2，由哪一个广播接收者来接收广播
				ComponentName provider = new ComponentName(
						getApplicationContext(), MyAppWidgetProvider.class);
				// 创建一个 "远程View"(参数，1，当前包名，2，控件布局)
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);

				// 当前运行的进程数
				int processCount = SystemInfoUtils
						.getProcessCount(getApplicationContext());
				// 远程控件设置布局文件中的数据(当前一共运行多少个进程)
				views.setTextViewText(R.id.process_count, "正在运行的软件："
						+ processCount + "个");

				// 当前可用内存
				long availMem = SystemInfoUtils
						.getAvailMem(getApplicationContext());

				// 远程控件设置布局文件中的数据(当前可用内存)
				views.setTextViewText(
						R.id.process_memory,
						"可用内存："
								+ Formatter.formatFileSize(
										getApplicationContext(), availMem));

				//隐式启动
				Intent intent = new Intent();
				intent.setAction("KillAllProcess");
				// 延时意图(发送一条广播，让清理进程的广播接收者收到该广播后清理进程)
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent, 0);

				// 设置按钮点击事件(延时意图点击事件)
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

				// 更新桌面小部件
				widgetManager.updateAppWidget(provider, views);
			}
		};
		// 每隔5秒钟刷新一下桌面小控件(定时器1毫秒后启动，每隔3秒调用一下定时任务)
		timer.schedule(timerTask, 1, 3000);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
