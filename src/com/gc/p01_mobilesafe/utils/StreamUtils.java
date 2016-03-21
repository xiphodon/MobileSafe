package com.gc.p01_mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ��ȡ���Ĺ�����
 * @author GuoChang
 *
 */
public class StreamUtils {

	/**
	 * ����������String���ͷ���
	 * @param in ������
	 * @return ת�ɵ�String���͵�������
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
