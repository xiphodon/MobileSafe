package com.gc.p01_mobilesafe.receiver;

import com.gc.p01_mobilesafe.db.dao.AddressDAO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 监听去电   广播接受者(在   AddressService 中已采用动态注册)
 * 需要去电权限    android.permission.PROCESS_OUTGOING_CALLS
 * @author guochang
 *
 */
public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String number = getResultData();
		String address = AddressDAO.getAddress(number);
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();
	}

}
