/**  
 * @title: VDIcloudWS v04 GenerateURL.java 
 * @package com.opzoon.client.cloudstack.request
 * @author maxiaochao
 * @date 2012-9-13
 * @version V04 
 */
package com.opzoon.ohvc.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;

import com.opzoon.ohvc.common.anotation.MD5;

/**
 * URL 管理工具类
 * 
 * @ClassName: GenerateURL.java
 * @Description: GenerateURL.java
 * @author: maxiaochao
 * @date: 2012-9-13
 * @version: V04
 */
public class GenerateURL {

	public static String generateApiURL(Object bean) {

		@SuppressWarnings("rawtypes")
		Class klass = bean.getClass();
		boolean includeSuperClass = klass.getClassLoader() != null;
		Method[] methods = (includeSuperClass) ? klass.getMethods() : klass
				.getDeclaredMethods();
		StringBuilder builder = new StringBuilder(methods.length * 3);
		Method cmethod;
		try {
			cmethod = klass.getMethod("getCommand", (Class[]) null);
		} catch (NoSuchMethodException e) {
			cmethod = null;
		}
		try {
			if (cmethod != null) {
				builder.append("command=")
						.append(cmethod.invoke(bean, (Object[]) null))
						.append("&");
			}

			for (int i = 0; i < methods.length; i += 1) {

				Method method = methods[i];

				if (Modifier.isPublic(method.getModifiers())) {
					String name = method.getName();
					String key = "";
					if (name.startsWith("get")) {
						if (name.equals("getClass")
								|| name.equals("getDeclaringClass")
								|| name.equals("getCommand")) {
							key = "";
						}

						else {
							key = name.substring(3);
						}
					} else if (name.startsWith("is")) {
						key = name.substring(2);
					}
					if (key.length() > 0
							&& Character.isUpperCase(key.charAt(0))
							&& method.getParameterTypes().length == 0) {
						if (key.length() == 1) {
							key = key.toLowerCase();
						} else if (!Character.isUpperCase(key.charAt(1))) {
							key = key.substring(0, 1).toLowerCase()
									+ key.substring(1);
						}

						Object result = method.invoke(bean, (Object[]) null);
						if (result != null) {
							Field field = klass.getDeclaredField(key);
							if (field.getAnnotation(MD5.class) != null) {
								result = OpzoonUtils.MD5(result.toString());
							}

							String encoderesult = URLEncoder.encode(
									result + "", "UTF-8");
							//
							builder.append(key.toLowerCase()).append("=")
									.append(encoderesult).append("&");
						}
					}
				}

			}
		} catch (Exception ignore) {
			ignore.printStackTrace();
			return null;
		}
		return builder.toString().substring(0, builder.length() - 1);
	}

	public static String getFinalUrl(String host, String apiUrl,
			String sessionKey) {
		String finalUrl = host + "?" + apiUrl + "&response=json"
				+ "&sessionkey=" + sessionKey;
		return finalUrl;
	}
}
