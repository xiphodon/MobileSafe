package com.gc.p01_mobilesafe.service;

import java.util.List;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * ��ȡ��γ������� service
 * @author Administrator
 *
 */
public class LocationService extends Service {

	private LocationManager locationManager;
	private MyLocationListener myLocationListener;
	private SharedPreferences mPref;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		
		//��ȡϵͳ��λ����
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        //��ȡ����λ���ṩ��
//      List<String> allProviders = locationManager.getAllProviders();
//      System.out.println(allProviders);
        
        
        //���λ���ṩ�ߵ�"��׼"
        Criteria criteria = new Criteria();
        //�Ƿ������Ѷ�λ������3g��4g��λ��
        criteria.setCostAllowed(true);
        //��ȷ�ȣ����ã�
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        
        //��ȡ���  λ���ṩ�ߣ���������׼���Ƿ���ã�
        String bestProvider = locationManager.getBestProvider(criteria, true);
        
        
        myLocationListener = new MyLocationListener();
        //��ȡ��λ����(λ���ṩ�ߣ���С����ʱ�䣬��С���¾��룬����)
        locationManager.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
//      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
        
	}
	
	 class MyLocationListener implements LocationListener{

	    	//λ�ñ仯ʱ�ص�
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				String longitude = "���� �� " + location.getLongitude() + "��";
				String latitude = "γ�� �� " + location.getLatitude() + "��";
				String accuracy = "���� ��" + location.getAccuracy() + "��";
				String altitude = "���� �� " + location.getAltitude() + "��";
				
				mPref.edit().putString("location", longitude + " ; " + latitude).commit();
				
				//ֹͣ����service
				stopSelf();
			}

			//λ���ṩ��״̬�����仯ʱ�ص�
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				System.out.println("onStatusChanged");
			}

			//λ���ṩ�߿���ʱ�ص�������ʱ��
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				System.out.println("onProviderEnabled");
			}

			//λ���ṩ�߲�����ʱ�ص����ر�ʱ��
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				System.out.println("onProviderDisabled");
			}
	    	
	    }

	   /**
	     * activity����ʱ���ã��ر�GPS��λ����
	     */
	    @Override
		public void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	//�ر�GPS��λ����
	    	locationManager.removeUpdates(myLocationListener);
	    }
}
