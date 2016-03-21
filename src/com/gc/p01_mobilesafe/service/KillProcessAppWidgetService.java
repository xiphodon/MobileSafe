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
 * ����С�ؼ���������̷���
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

		// ����С�ؼ�������
		widgetManager = AppWidgetManager.getInstance(this);

		// ��ʱ��
		Timer timer = new Timer();
		// ��ʼ��һ����ʱ����
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("KillProcessAppWidgetService");

				// ������1�������ģ�2������һ���㲥�����������չ㲥
				ComponentName provider = new ComponentName(
						getApplicationContext(), MyAppWidgetProvider.class);
				// ����һ�� "Զ��View"(������1����ǰ������2���ؼ�����)
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);

				// ��ǰ���еĽ�����
				int processCount = SystemInfoUtils
						.getProcessCount(getApplicationContext());
				// Զ�̿ؼ����ò����ļ��е�����(��ǰһ�����ж��ٸ�����)
				views.setTextViewText(R.id.process_count, "�������е������"
						+ processCount + "��");

				// ��ǰ�����ڴ�
				long availMem = SystemInfoUtils
						.getAvailMem(getApplicationContext());

				// Զ�̿ؼ����ò����ļ��е�����(��ǰ�����ڴ�)
				views.setTextViewText(
						R.id.process_memory,
						"�����ڴ棺"
								+ Formatter.formatFileSize(
										getApplicationContext(), availMem));

				//��ʽ����
				Intent intent = new Intent();
				intent.setAction("KillAllProcess");
				// ��ʱ��ͼ(����һ���㲥����������̵Ĺ㲥�������յ��ù㲥���������)
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent, 0);

				// ���ð�ť����¼�(��ʱ��ͼ����¼�)
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

				// ��������С����
				widgetManager.updateAppWidget(provider, views);
			}
		};
		// ÿ��5����ˢ��һ������С�ؼ�(��ʱ��1�����������ÿ��3�����һ�¶�ʱ����)
		timer.schedule(timerTask, 1, 3000);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
