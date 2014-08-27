/**
 * 
 */
package com.opzoon.vdi.core.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.json.JSONObject;

import com.opzoon.ohvc.common.GenerateURL;
import com.opzoon.ohvc.common.OpzoonJSONData;
import com.opzoon.ohvc.common.Validator;
import com.opzoon.ohvc.common.ValidatorManager;
import com.opzoon.ohvc.common.anotation.Json;

/**
 * @author maxiaochao
 * @version V04 2012-9-6
 */
public class BaseDomain<T> implements ValidatorManager<T>, OpzoonJSONData {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opzoon.client.domain.ValidatorManager#validate()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T validate() {
		// TODO Auto-generated method stub
		return (T) Validator.validate(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opzoon.client.domain.OpzoonJSONData#buildJSONObject()
	 */
	@SuppressWarnings({"unchecked"})
	@Override
	public JSONObject buildJSONObject() {

		return parseJSONObject((T) this);
	}
	private JSONObject parseJSONObject(Object bean) {

		JSONObject jsonObject = new JSONObject();
		@SuppressWarnings("rawtypes")
		Class klass = bean.getClass();
		boolean includeSuperClass = klass.getClassLoader() != null;
		Method[] methods = (includeSuperClass) ? klass.getMethods() : klass
				.getDeclaredMethods();
		for (int i = 0; i < methods.length; i += 1) {

			try {
				Method method = methods[i];
				if (Modifier.isPublic(method.getModifiers())) {
					String name = method.getName();
					String key = "";
					if (name.startsWith("get")) {
						if (name.equals("getClass")
								|| name.equals("getDeclaringClass")) {
							key = "";
						} else {
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
						Field field = klass.getDeclaredField(key);
						if (field.getAnnotation(Json.class) != null) {
							result = JSONObject.wrap(result);
						}

						if (result != null) {
							jsonObject.put(key, JSONObject.wrap(result));
						}
					}
				}
			} catch (Exception ignore) {
				ignore.printStackTrace();
				return null;
			}
		}
		return jsonObject;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return GenerateURL.generateApiURL((T) this);
	}

}
