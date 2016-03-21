package com.gc.p01_mobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Xml;
import android.widget.Toast;

/**
 * ���ű��ݹ�����
 * @author guochang
 *
 */
public class SmsUtils {

	public static boolean backUp(Context context, ProgressDialog progressDialog){
		
		
		//1,���ݶ��ŵ�SD�����ȼ��SD���Ƿ����
		//2,ʹ�����ݹ۲��߻�ȡ����
		//3,���ݶ��ŵ�SD������д��SD��
		
		//���SD���Ƿ���غ�
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//������ݹ۲���
			ContentResolver contentResolver = context.getContentResolver();
			//��ö���·��
			Uri uri = Uri.parse("content://sms/");
			//���Ҷ��ŵ�"address","date","type","body" �ֶΣ��ֱ���  ��ַ��ʱ�䣬���ͣ�1Ϊ���գ�2Ϊ���ͣ�������
			Cursor cursor = contentResolver.query(uri, new String[]{"address","date","type","body"}, null, null, null);
		
			//һ���ж��������ݣ�������ProgressDialog�����ֵ
			int count = cursor.getCount();
			progressDialog.setMax(count);
			
			//��ʼ������������
			int progress = 0;

			//�õ����л���
			XmlSerializer xs = Xml.newSerializer();
			File file = new File(Environment.getExternalStorageDirectory(),"backUpSms.xml");
			try {
				FileOutputStream fos = new FileOutputStream(file);
				//ָ����ʲô��������XML
				xs.setOutput(fos, "utf-8");
				//�ڶ���������ʾxml�ļ��Ƿ�Ϊ�����ļ�
				xs.startDocument("utf-8", true);
				
				xs.startTag(null, "message");
				//����XmlSerializer�Ľڵ�ֵ���ڶ������������֣�������������ֵ
				xs.attribute(null, "size", String.valueOf(count));
				
				while(cursor.moveToNext()){
					xs.startTag(null, "sms");
					
					xs.startTag(null, "address");
					xs.text(cursor.getString(cursor.getColumnIndex("address")));
					xs.endTag(null, "address");
					
					xs.startTag(null, "date");
					xs.text(cursor.getString(cursor.getColumnIndex("date")));
					xs.endTag(null, "date");
					
					xs.startTag(null, "type");
					xs.text(cursor.getString(cursor.getColumnIndex("type")));
					xs.endTag(null, "type");
					
					xs.startTag(null, "body");
					//�ı����ܣ�����1���������ӣ���Կ��������2�������ı�
					xs.text(Crypto.encrypt("123", cursor.getString(cursor.getColumnIndex("body"))));
					xs.endTag(null, "body");
					
					xs.endTag(null, "sms");
					
					//���ý��������ȣ�ÿ���л����һ������ʱ����һ�½�������
					progress++;
					progressDialog.setProgress(progress);
					
				}
				cursor.close();
				xs.endTag(null, "message");
				xs.endDocument();
				fos.flush();
				xs.flush();
				fos.close();
				
				return true;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return false;
	}
}
