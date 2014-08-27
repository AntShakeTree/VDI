/**  
 * @title: VDIcloudWS v04 OpzoonUtils.java 
 * @package com.opzoon.common
 * @author maxiaochao
 * @date 2012-9-13
 * @version V04 
 */
package com.opzoon.ohvc.common;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.google.gson.Gson;
import com.opzoon.ohvc.common.anotation.DaoName;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.request.PageRequest;

/**
 * 工具类
 * 
 * ClassName: OpzoonUtils.java
 * @Description: OpzoonUtils.java
 * @author: maxiaochao
 * @date: 2012-9-13
 * @version: V04
 */
public class OpzoonUtils {
	private static Gson gson = new Gson();
	/** 
	 * @return gson 
	 */
	public static Gson getGson() {
		return gson;
	}

	private OpzoonUtils() {
	};

	public static String MD5(String md5Str) {
		MessageDigest md5;
		// make sure our MD5 hash value is 32 digits long...
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
	 * 1. Signs a string with a secret key using SHA-1 2. Base64 encode the result 3. URL encode the final result
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public static String signHmacSHA1(String request, String key) {
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
			mac.init(keySpec);
			mac.update(request.getBytes());
			byte[] encryptedBytes = mac.doFinal();
			return URLEncoder.encode(Base64.encodeBase64String(encryptedBytes), "UTF-8");
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * @Title: isIp
	 * @Description: 判断参数是否是IP
	 * @param @param ip
	 * @param @return
	 * @return boolean
	 * @throws
	 * @author : david
	 * @since : v1.0.0.0
	 * @date : 2012-12-18 上午11:06:55
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
		;
		return true;
	}

	/**
	 * @Title: matches
	 * @Description: 正则方式查看是否匹配
	 * @param @param value
	 * @param @param regular
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean matches(String value, String regular) {
		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(value); // 以验证127.400.600.2为例
		return matcher.matches();
	}

	

	/**
	 * @Title: covertDomainToIp
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param domain
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String covertDomainToIp(String domain) {
		try {
			return InetAddress.getByName(domain).getHostAddress();
		} catch (Exception e) {
			return domain;
		}
	}

	/**
	 * @param <T>
	 * @return
	 * @Title: getHqlByDomain
	 * @Description: 获得hql
	 * @param @param req 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static <T> QueryUtil getHqlByDomain(PageRequest<T> req) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		BeanWrapper bean = new BeanWrapperImpl(req);
		PropertyDescriptor[] pros = bean.getPropertyDescriptors();
		StringBuffer hql = new StringBuffer();
		hql.append(" from ");
		DaoName daoName = req.getClass().getAnnotation(DaoName.class);
		if (daoName != null) {
			hql.append(daoName.name());
		} else {
			hql.append(req.getClass().getSimpleName());
		}
		hql.append(" where ");
		List<Object> strs = new ArrayList<Object>();
		for (PropertyDescriptor propertyDescriptor : pros) {
			Method methodGetX = propertyDescriptor.getReadMethod(); // Read对应get()方法
			Method methodSet = propertyDescriptor.getWriteMethod();
			String properyName = propertyDescriptor.getName();
			if (methodGetX != null && methodSet != null) {
				Object reValue = methodGetX.invoke(req);
				if (reValue == null||(reValue+"").equals("")) {
					continue;
				}
				if (isContainsPageProperty(properyName)) {
					continue;
				}
				strs.add(reValue);
				hql.append(" " + propertyDescriptor.getName()).append(" = ? ").append("and ");
			}

		}
		hql.append(" 1=1 ");
		return new QueryUtil(hql.toString(), strs);
	}

	public static boolean isContainsPageProperty(String properyName) {
		String[] sts = ConfigUtil.getBykey("page.property").split(",");
		for (String string : sts) {
			if (properyName.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}

	/** 
	* @Title: getHeadByString 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param object
	* @param @return    设定文件 
	* @return Head    返回类型 
	* @throws 
	*/
	public static Head getHeadByString(String json) {
		// TODO Auto-generated method stub
		return gson.fromJson(json, Head.class);
	}
	
	// FIXME TEMP
	private static interface BeanWrapper {
		
		PropertyDescriptor[] getPropertyDescriptors();
		
	}
	
	private static class BeanWrapperImpl implements BeanWrapper {
		
		private final PropertyDescriptor[] propertyDescriptors;

		public BeanWrapperImpl(Object o) {
			List<PropertyDescriptor> propertyDescriptorList = new LinkedList<PropertyDescriptor>();
			for (Class<?> clazz = o.getClass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
				for (Field field : clazz.getDeclaredFields()) {
					if (0 < (field.getModifiers() & Modifier.STATIC)) {
						continue;
					}
					PropertyDescriptor propertyDescriptor = null;
					try {
						propertyDescriptor = new PropertyDescriptor(field.getName(), clazz);
					} catch (IntrospectionException e) {}
					propertyDescriptorList.add(propertyDescriptor);
				}
			}
			propertyDescriptors = propertyDescriptorList.toArray(new PropertyDescriptor[0]);
		}

		public PropertyDescriptor[] getPropertyDescriptors() {
			return propertyDescriptors;
		}
		
	}

}
