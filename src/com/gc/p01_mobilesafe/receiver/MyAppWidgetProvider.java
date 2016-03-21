package com.gc.p01_mobilesafe.receiver;

import com.gc.p01_mobilesafe.service.KillProcessAppWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

/**
 * (����/ֹͣ-����С�ؼ�������̷���)
 * ����С������
 * 1,�嵥�ļ����ô���Ԫ���ݣ��㲥������
 * 2���½��㲥�����ߣ�
 * 3���½�xml�ļ���res/xml/Ŀ¼��
 * 4��ʵ��xml�ڲ�����
 * @author guochang
 *
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

	/**
	 * ÿ�����µ�����С�ؼ����ɵ�ʱ�򶼻����
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("onUpdate");
	}

	/**
	 * ÿ��ɾ������С�ؼ���ʱ�򶼻���õķ���
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		System.out.println("onDeleted");
	}

	/**
	 * ��һ�δ�����ʱ��Ż���õ�ǰ���������ڵķ���
	 */
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		System.out.println("onEnabled");
		
		//��һ�δ�������С�ؼ�ʱ����������С�ؼ�������̷���
		Intent intent = new Intent(context,KillProcessAppWidgetService.class);
		context.startService(intent);
	}

	/**
	 * �������������е�����С�ؼ���ɾ����ʱ��ŵ��õ�ǰ�������
	 */
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		System.out.println("onDisabled");

		//�������������е�����С�ؼ���ɾ��ʱ��ֹͣ����С�ؼ�������̷���
		Intent intent = new Intent(context,KillProcessAppWidgetService.class);
		context.stopService(intent);
	}


}
