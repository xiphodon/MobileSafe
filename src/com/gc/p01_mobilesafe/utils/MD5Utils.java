package com.gc.p01_mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class MD5Utils {
	public final static String encode(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// ���MD5ժҪ�㷨�� MessageDigest ����
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// ʹ��ָ�����ֽڸ���ժҪ
			mdInst.update(btInput);
			// �������
			byte[] md = mdInst.digest();
			// ������ת����ʮ�����Ƶ��ַ�����ʽ
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// public static void main(String[] args) {
	// System.out.print(MD5Utils.encode("password"));
	// }

	
//	/**
//	 * md5���ܵ��㷨
//	 * @param text
//	 * @return
//	 */
//	public static String encode(String text){
//		try {
//			MessageDigest digest = MessageDigest.getInstance("md5");
//			byte[] result = digest.digest(text.getBytes());
//			StringBuilder sb  =new StringBuilder();
//			for(byte b : result){
//				int number = b&0xff; // ���� +1 ;
//				String hex = Integer.toHexString(number);
//				if(hex.length()==1){
//					sb.append("0"+hex);
//				}else{
//					sb.append(hex);
//				}
//			}
//			return sb.toString();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//			//can't reach
//			return "";
//		}
//	}
//	
//	
	
	
	
	
	/**
     * ��ȡ���ļ���MD5(����������)
     * @param sourceDir
     * @return
     * ��������
     */
	public static String getFileMd5(String sourceDir) {
		
		File file = new File(sourceDir);
		
		try {
			FileInputStream fis = new FileInputStream(file);
			
			byte[] buffer =  new byte[1024];
			
			int len = -1;
			//��ȡ������ժҪ
			MessageDigest messageDigest = MessageDigest.getInstance("md5");
			
			while((len = fis.read(buffer))!= -1){
				
				messageDigest.update(buffer, 0, len);
				
			}
			byte[] result = messageDigest.digest();
			
			StringBuffer sb = new StringBuffer();
			
			for(byte b : result){
				int number = b&0xff; // ���� +1 ;
				String hex = Integer.toHexString(number);
				if(hex.length()==1){
					sb.append("0"+hex);
				}else{
					sb.append(hex);
				}
			}
			return sb.toString();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
}