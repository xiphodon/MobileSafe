package com.gc.p01_mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 读取流的工具类
 * @author GuoChang
 *
 */
public class StreamUtils {

	/**
	 * 把输入流以String类型返回
	 * @param in 输入流
	 * @return 转成的String类型的输入流
	 * @throws IOException 
	 */
	public static String readFromStream(InputStream in) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		
		while((len = in.read(buffer)) != -1){
			out.write(buffer, 0, len);
		}
		
		String result = out.toString();
		out.close();
		in.close();
		
		return result;
	}
}
