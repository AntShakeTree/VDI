/**   
 * @Title: BeanCrawlerUtils.java 
 * @Package com.crawler.common 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author david   
 * @date 2013-1-1 下午2:48:57 
 * @version V1.0.0.0   
 */
package com.opzoon.vdi.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * 扩展 Apache Commons BeanCrawlerUtils, 提供一些反射方面缺失功能的封装.
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

	protected static final Log logger = LogFactory.getLog(BeanUtils.class);

	private BeanUtils() {
	}

	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 * 
	 * @throws NoSuchFieldException
	 *             如果没有该Field时抛出.
	 */
	public static Field getDeclaredField(Object object, String propertyName)
			throws NoSuchFieldException {
		Assert.notNull(object);
		Assert.hasText(propertyName);
		return getDeclaredField(object.getClass(), propertyName);
	}
	/**
	 * put object to map。
	 * @param
	 * @param req
	 * @return
	 */
	public static  HashMap<String, Object> putPerpertiesAndValueMap(Object req) {
		HashMap<String, Object> reqmap = new HashMap<String, Object>();
		Class dClass = req.getClass();
		Field fields[] = BeanUtils.getDeclaredFields(dClass);

		for (Field field : fields) {
			Method gMethod = null;
			try {
				String mname = BeanUtils.getGetterName(field.getType(),
						field.getName());
			
				gMethod = dClass.getMethod(mname, null);
			} catch (NoSuchMethodException e) {
				// e.printStackTrace();
			} catch (SecurityException e) {
				// e.printStackTrace();
			}
			if (gMethod != null) {
				Object value;
				boolean gAccessible = gMethod.isAccessible();
				try {
					gMethod.setAccessible(true);
					value = gMethod.invoke(req, null);
					if (value != null) {
						reqmap.put(field.getName(),  value);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} finally {
					gMethod.setAccessible(gAccessible);
				}

			}
		}
		return reqmap;
	}

	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 * 
	 * @throws NoSuchFieldException
	 *             如果没有该Field时抛出.
	 */
	public static Field getDeclaredField(Class clazz, String propertyName)
			throws NoSuchFieldException {
		Assert.notNull(clazz);
		Assert.hasText(propertyName);
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(propertyName);
			} catch (NoSuchFieldException e) {
				// Field不在当前类定义,继续向上转型
			}
		}
		throw new NoSuchFieldException("No such field: " + clazz.getName()
				+ '.' + propertyName);
	}

	/**
	 * 暴力获取对象变量值,忽略private,protected修饰符的限制.
	 * 
	 * @throws NoSuchFieldException
	 *             如果没有该Field时抛出.
	 */
	public static Object forceGetProperty(Object object, String propertyName)
			throws NoSuchFieldException {
		Assert.notNull(object);
		Assert.hasText(propertyName);

		Field field = getDeclaredField(object, propertyName);

		boolean accessible = field.isAccessible();
		field.setAccessible(true);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			logger.info("error wont' happen");
		}
		field.setAccessible(accessible);
		return result;
	}

	/**
	 * 暴力设置对象变量值,忽略private,protected修饰符的限制.
	 * 
	 * @throws NoSuchFieldException
	 *             如果没有该Field时抛出.
	 */
	public static void forceSetProperty(Object object, String propertyName,
			Object newValue) throws NoSuchFieldException {
		Assert.notNull(object);
		Assert.hasText(propertyName);

		Field field = getDeclaredField(object, propertyName);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(object, newValue);
		} catch (IllegalAccessException e) {
			logger.info("Error won't happen");
		}
		field.setAccessible(accessible);
	}

	/**
	 * 暴力调用对象函数,忽略private,protected修饰符的限制.
	 * 
	 * @throws NoSuchMethodException
	 *             如果没有该Method时抛出.
	 */
	public static Object invokePrivateMethod(Object object, String methodName,
			Object... params) throws NoSuchMethodException {
		Assert.notNull(object);
		Assert.hasText(methodName);
		Class[] types = new Class[params.length];
		for (int i = 0; i < params.length; i++) {
			types[i] = params[i].getClass();
		}

		Class clazz = object.getClass();
		Method method = null;
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				method = superClass.getDeclaredMethod(methodName, types);
				break;
			} catch (NoSuchMethodException e) {
				// 方法不在当前类定义,继续向上转型
			}
		}

		if (method == null)
			throw new NoSuchMethodException("No Such Method:"
					+ clazz.getSimpleName() + methodName);

		boolean accessible = method.isAccessible();
		method.setAccessible(true);
		Object result = null;
		try {
			result = method.invoke(object, params);
		} catch (Exception e) {
			ReflectionUtils.handleReflectionException(e);
		}
		method.setAccessible(accessible);
		return result;
	}

	/**
	 * 按Filed的类型取得Field列表.
	 */
	public static List<Field> getFieldsByType(Object object, Class type) {
		List<Field> list = new ArrayList<Field>();
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getType().isAssignableFrom(type)) {
				list.add(field);
			}
		}
		return list;
	}

	/**
	 * 判读对象是否基本类型
	 * 
	 * @param object
	 * @param types
	 * @return
	 */
	public static boolean typeAllow(Object object) {
		if (object instanceof String) {
			return true;
		} else if (object instanceof Long) {
			return true;
		} else if (object instanceof Integer) {
			return true;
		} else if (object instanceof Double) {
			return true;
		} else if (object instanceof Float) {
			return true;
		} else if (object instanceof Boolean) {
			return true;
		}
		return false;
	}

	/**
	 * 按FiledName获得Field的类型.
	 */
	public static Class getPropertyType(Class type, String name)
			throws NoSuchFieldException {
		return getDeclaredField(type, name).getType();
	}

	/**
	 * 获得field的getter函数名称.
	 */
	public static String getGetterName(Class<?> type, String fieldName) {
		Assert.notNull(type, "Type required");
		Assert.hasText(fieldName, "FieldName required");

		if (type.getName().equals("boolean")) {
			return "is"
					+ org.apache.commons.lang.StringUtils.capitalize(fieldName);
		} else {
			return "get"
					+ org.apache.commons.lang.StringUtils.capitalize(fieldName);
		}
	}

	/**
	 * 获得field的getter函数,如果找不到该方法,返回null.
	 */
	public static Method getGetterMethod(Class type, String fieldName) {
		try {
			return type.getMethod(getGetterName(type, fieldName));
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 基于反射,实现bean属性拷贝
	 */
	public static void copyPropertiesByModel(Object dest, Object orig) {
		try {
			copyProperties(dest, orig);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 */
	public static Field[] getDeclaredFields(Class clazz) {
		Assert.notNull(clazz);
		Field[] field = new Field[] {};
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			field = superClass.getDeclaredFields();
			if (field.length != 0)
				break;
		}
		return field;
	}

	/**
	 * 拷贝简单POJO的属性,只对简单的原生属性进行拷贝,如String,Long等
	 * 
	 * @param target
	 *            目标对象
	 * @param src
	 *            源对象
	 * @param override
	 *            对为null的属性是否拷贝,true 拷贝,false 不拷贝
	 */
	public static void copyProperties(Object source, Object target,
			boolean override) {
		if (target == null || source == null) {
			return;
		}
		if (override) {
			try {
				copyProperties(source, target);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Class dClass = target.getClass();
			Class sClass = source.getClass();
			Class type = null;
			List<Field> fieldsList = new ArrayList<Field>();
			Field fields[] = null;
			for (Class superClass = sClass; superClass != Object.class; superClass = superClass
					.getSuperclass()) {
				fields = superClass.getDeclaredFields();
				if (fields.length != 0) {
					for (Field field : fields) {
						type = field.getType();
						if (type.isPrimitive() || type.equals(String.class)
								|| type.equals(Long.class)
								|| type.equals(Integer.class)
								|| type.equals(Boolean.class)
								|| type.equals(Float.class)
								|| type.equals(Double.class)
								|| type.equals(Byte.class)
								|| type.equals(Short.class)) {
							fieldsList.add(field);
						}
					}
				}
			}

			for (Field field : fieldsList) {
				Method gMethod = null;
				Method sMethod = null;
				try {
					gMethod = sClass.getMethod(
							getGetterName(field.getType(), field.getName()),
							null);
					sMethod = dClass.getMethod(
							"set"
									+ org.apache.commons.lang.StringUtils
											.capitalize(field.getName()),
							field.getType());
				} catch (NoSuchMethodException e) {
					// e.printStackTrace();
				} catch (SecurityException e) {
					// e.printStackTrace();
				}
				if (gMethod != null && sMethod != null) {
					Object value;
					boolean gAccessible = gMethod.isAccessible();
					boolean sAccessible = sMethod.isAccessible();
					try {
						gMethod.setAccessible(true);
						value = gMethod.invoke(source, null);
						if (value != null) {
							sMethod.setAccessible(true);
							sMethod.invoke(target, value);
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} finally {
						gMethod.setAccessible(gAccessible);
						sMethod.setAccessible(sAccessible);
					}

				}
			}
		}
	}
}
