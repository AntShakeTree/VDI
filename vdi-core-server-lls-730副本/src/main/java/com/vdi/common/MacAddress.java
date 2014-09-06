package com.vdi.common;

import java.util.Random;
/**
 * 
 * @author maxc
 *
 */
public class MacAddress {
	/**
	 * 根据虚拟机类型生成随机Mac地址
	 * 
	 * @param hypervType
	 *            虚拟机类型 KVM: QEMU虚拟机 vmware: Vmware虚拟机 其他： 生成随机的Mac地址
	 * @return
	 */
	public static  String createRandomMacAddress() {
		return getRandChars(6);
	}

	/**
	 * 生成 2个随机的小写字母或者数字组成的串
	 * 
	 * @return
	 */
	private static String getRandChars(int len) {
		String multiChars = "";
		for (int i = 0; i < len; i++) {
			multiChars = multiChars.concat(":");
			String chars = getRandTwoChars();
			multiChars = multiChars.concat(chars);
		}
		if (len > 0) {
			multiChars = multiChars.substring(1);
		}
		return multiChars;
	}

	/**
	 * 生成2个随机的小写字母或者数字
	 * 
	 * @return
	 */
	private static String getRandTwoChars() {
		String chars = createFirstRandomChar();
		return chars.concat(createRandomChar());
	}

	/**
	 * 生成随机的小写字母或者数字
	 * 
	 * @return 随机的小写字母或者数字
	 */
	private static String createFirstRandomChar() {
		String[] chars = new String[] { "a","b","d" ,"c",  "e","f","0","1","3",
				"2",  "4", "5", "6",  "8" };
		Random rand = new Random();
		int rInt = rand.nextInt(chars.length);
		return chars[rInt];
	}
	/**
	 * 生成随机的小写字母或者数字
	 * 
	 * @return 随机的小写字母或者数字
	 */
	private static String createRandomChar() {
		String[] chars = new String[] { "a", "c",  "e", "0",
				"2",  "4",  "6",  "8" };
		Random rand = new Random();
		int rInt = rand.nextInt(chars.length);
		return chars[rInt];
	}

}
