package com.gc.p01_mobilesafe.receiver;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.service.LocationService;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * 拦截短信
 * 
 * @author guochang
 * 
 */
public class SmsReceiver extends BroadcastReceiver {

	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	private SharedPreferences mPref;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		//获取设备策略服务
        mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //设备管理组件
        mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);
        
        mPref = context.getSharedPreferences("config", context.MODE_PRIVATE);
        
        //是否已开启安全保护
        boolean protect = mPref.getBoolean("protect", false);
        if(protect){

    		// 拿到短信的信息
    		// 短信信息封装在intent中
    		Bundle bundle = intent.getExtras();
    		// 以pdus（协议数据单元）为键，取出一个object数组，数组中的每一个元素都是一条短信
    		Object[] objects = (Object[]) bundle.get("pdus");

    		// 拿到广播中的所有的短信
    		for (Object object : objects) {
    			// 通过pdu来构造短信
    			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);

    			// 短信来源号码
    			String originatingAddress = sms.getOriginatingAddress();
    			// 短信内容
    			String messageBody = sms.getMessageBody();

    			// System.out.println(originatingAddress + ":" +messageBody);

    			if ("#*alarm*#".equals(messageBody)) {
    				// 播放报警音乐
    				MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
    				// 音量大小，左声道，右声道 都调成最大值
    				player.setVolume(1f, 1f);
    				// 循环播放
    				player.setLooping(true);
    				player.start();

    				// 阻止其他广播接受者接收这条广播 4.4后这样做，拦截短信不起作用
    				abortBroadcast();
    			} else if ("#*location*#".equals(messageBody)) {
    				// GPS定位，获取经度纬度
    				// 开启定位服务
    				context.startService(new Intent(context, LocationService.class));

    				String location = mPref.getString("location", "get location...");
    				String phone = mPref.getString("safe_phone", "");

    				System.out.println(location);

    				// 发送短信给安全号码（发短信权限）
    				SmsManager smsManager = SmsManager.getDefault();
    				smsManager.sendTextMessage(phone, null, location, null, null);

    				// 阻止其他广播接受者接收这条广播 4.4后这样做，拦截短信不起作用
    				abortBroadcast();
    			} else if ("#*lockscreen*#".equals(messageBody)) {
    				// 一键锁屏（加上密码）
    				// 是否已经激活设备
    				if (mDPM.isAdminActive(mDeviceAdminSample)) {

    					// 立刻锁屏
    					mDPM.lockNow();
    					// 重置锁屏密码，（参数：密码，0是不允许其他应用重置密码）
    					mDPM.resetPassword("123456", 0);
    				}

    			} else if ("#*wipedata*#".equals(messageBody)) {
    				// 清除数据
    				// 是否已经激活设备
    				if (mDPM.isAdminActive(mDeviceAdminSample)) {

    					//清除数据，恢复出厂设置
    					mDPM.wipeData(0);
    				}
    			}
    		}
        }
		
	}

}
