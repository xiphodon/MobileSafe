package com.gc.p01_mobilesafe.receiver;

import com.gc.p01_mobilesafe.service.KillProcessAppWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

/**
 * (开启/停止-桌面小控件清理进程服务)
 * 桌面小部件：
 * 1,清单文件配置创建元数据，广播接收者
 * 2，新建广播接收者，
 * 3，新建xml文件，res/xml/目录下
 * 4，实现xml内部内容
 * @author guochang
 *
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

	/**
	 * 每次有新的桌面小控件生成的时候都会调用
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("onUpdate");
	}

	/**
	 * 每次删除桌面小控件的时候都会调用的方法
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		System.out.println("onDeleted");
	}

	/**
	 * 第一次创建的时候才会调用当前的生命周期的方法
	 */
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		System.out.println("onEnabled");
		
		//第一次创建桌面小控件时，开启桌面小控件清理进程服务
		Intent intent = new Intent(context,KillProcessAppWidgetService.class);
		context.startService(intent);
	}

	/**
	 * 当桌面上面所有的桌面小控件都删除的时候才调用当前这个方法
	 */
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		System.out.println("onDisabled");

		//当桌面上面所有的桌面小控件都删除时，停止桌面小控件清理进程服务
		Intent intent = new Intent(context,KillProcessAppWidgetService.class);
		context.stopService(intent);
	}


}
