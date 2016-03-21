package com.gc.p01_mobilesafe.test;

import java.util.List;
import java.util.Random;

import com.gc.p01_mobilesafe.bean.BlackNumberInfo;
import com.gc.p01_mobilesafe.db.dao.BlackNumberDAO;

import android.test.AndroidTestCase;

/**
 * ≤‚ ‘¿‡
 * @author guochang
 *
 */
public class Test extends AndroidTestCase {

	public void testAdd(){
		BlackNumberDAO blackNumberDAO = new BlackNumberDAO(getContext());
		Random random = new Random();
		for(int i=0;i<100;i++){
			long number = 12345678901L + i;
			int mode = random.nextInt(3) + 1;
			blackNumberDAO.add(number + "", mode + "");
		}
	}
	
	public void testDelete(){
		BlackNumberDAO blackNumberDAO = new BlackNumberDAO(getContext());
		boolean delete = blackNumberDAO.delete("12345678905");
		assertEquals(true, delete);
	}
	
	public void testFind(){
		BlackNumberDAO blackNumberDAO = new BlackNumberDAO(getContext());
		String mode = blackNumberDAO.findNumber("12345678910");
		System.out.println(mode);
	}
	
	public void testFindAll(){
		BlackNumberDAO blackNumberDAO = new BlackNumberDAO(getContext());
		List<BlackNumberInfo> list = blackNumberDAO.findAll();
		for (BlackNumberInfo blackNumberInfo : list) {
			System.out.println(blackNumberInfo.getNumber() + ":" + blackNumberInfo.getMode() + "---");
		}
	}
}
