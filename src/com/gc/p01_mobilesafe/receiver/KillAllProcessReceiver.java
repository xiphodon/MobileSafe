package com.gc.p01_mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * �������н��̵Ĺ㲥������
 * @author guochang
 *
 */
public class KillAllProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		//�õ����̹�����
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		//��õ�ǰ���е����н���
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
	
		//�������н���
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
		}
		
		Toast.makeText(context, "������ϣ���ǰ����״̬����~", Toast.LENGTH_SHORT).show();
	}

}
