package com.vdi.common;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	public static String MD5(String md5Str) {
		MessageDigest md5;
		StringBuffer sb = new StringBuffer();
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			BigInteger pwInt = new BigInteger(1, md5.digest(md5Str.getBytes()));

			String pwStr = pwInt.toString(16);
			int padding = 32 - pwStr.length();
			for (int i = 0; i < padding; i++) {
				sb.append('0');
			}
			sb.append(pwStr);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			return "";
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param @param ip
	 * @param @return
	 * @return boolean
	 * @throws
	 * @since : v1.0.0.0
	 */
	public static boolean isInetAddress(String ip) {
		String reg = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
		if (!matches(ip, reg)) {
			try {
				String add = InetAddress.getByName(ip).getHostAddress();
				return matches(add, reg);
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Title: matches : d
	 * 
	 * @param value
	 * @param regular
	 * @param
	 * @return boolean
	 * @throws
	 */
	private static boolean matches(String value, String regular) {
		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(value); // 娴犮儵鐛欑拠锟�7.400.600.2娑撹桨绶�
		return matcher.matches();
	}
}
