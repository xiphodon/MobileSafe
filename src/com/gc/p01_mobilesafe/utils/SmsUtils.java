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
 * 短信备份工具类
 * @author guochang
 *
 */
public class SmsUtils {

	public static boolean backUp(Context context, ProgressDialog progressDialog){
		
		
		//1,备份短信到SD卡，先检查SD卡是否存在
		//2,使用内容观察者获取短信
		//3,备份短信到SD卡，即写到SD卡
		
		//检查SD卡是否挂载好
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//获得内容观察者
			ContentResolver contentResolver = context.getContentResolver();
			//获得短信路径
			Uri uri = Uri.parse("content://sms/");
			//查找短信的"address","date","type","body" 字段，分别是  地址，时间，类型（1为接收，2为发送），内容
			Cursor cursor = contentResolver.query(uri, new String[]{"address","date","type","body"}, null, null, null);
		
			//一共有多少条数据，并设置ProgressDialog的最大值
			int count = cursor.getCount();
			progressDialog.setMax(count);
			
			//初始化进度条进度
			int progress = 0;

			//得到序列化器
			XmlSerializer xs = Xml.newSerializer();
			File file = new File(Environment.getExternalStorageDirectory(),"backUpSms.xml");
			try {
				FileOutputStream fos = new FileOutputStream(file);
				//指定用什么编码生成XML
				xs.setOutput(fos, "utf-8");
				//第二个参数表示xml文件是否为独立文件
				xs.startDocument("utf-8", true);
				
				xs.startTag(null, "message");
				//设置XmlSerializer的节点值，第二个参数是名字，第三个参数是值
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
					//文本加密，参数1：加密种子（密钥），参数2：加密文本
					xs.text(Crypto.encrypt("123", cursor.getString(cursor.getColumnIndex("body"))));
					xs.endTag(null, "body");
					
					xs.endTag(null, "sms");
					
					//设置进度条进度（每序列化完成一条短信时更新一下进度条）
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
