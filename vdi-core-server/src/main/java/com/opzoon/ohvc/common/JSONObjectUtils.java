package com.opzoon.ohvc.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.opzoon.ohvc.common.anotation.ArrayAnnotation;
import com.opzoon.ohvc.common.anotation.Sub;
import com.opzoon.ohvc.domain.Agent;

/**
 * 
 * @author maxiaochao
 * 
 */
public class JSONObjectUtils {

	/**
	 * 不允许外部实例
	 */
	private JSONObjectUtils() {
	}

	public static JSONArray getRelationships() {
		return new JSONArray();
	}

	/**
	 * 
	 * @param java
	 *            pojo
	 * @return
	 */
	public static JSONObject buildJSONObject(Object object) {
		return (JSONObject) JSONObject.wrap(object);
	}

	/**
	 * 将cloudStack返回的正确response list字符串转换成需要的java List
	 * 
	 * @param jsonStr
	 *            json字符串，格式为： { \"response" : { "count":1 ,"template" : [
	 *            {"id":"1","name":"myname"},{"id":"2","name":"myname2"}] } }
	 * @param typeName
	 *            类型在json中的名称，如"template"
	 * @param descClass
	 *            需要转换成的list里的类型
	 * @return 返回descClass类型的List
	 * @throws Exception
	 */
	public static <T> List<T> getObjectList(String jsonStr, String typeName,
			Class descClass) throws Exception {
		JSONObject jsonObj = new JSONObject(jsonStr);
		String key = (String) jsonObj.keys().next();
		jsonObj = jsonObj.getJSONObject(key);
		if (!jsonObj.has(typeName))
			return null;
		JSONArray jsonArr = (JSONArray) jsonObj.get(typeName);
		List<T> objList = new ArrayList<T>();

		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject jsonObj2 = (JSONObject) jsonArr.get(i);
			Object obj2 = getObjectByJSONObject(jsonObj2, descClass);
			objList.add((T) obj2);
		}
		return objList;
	}

	// public static Object getObjectByJSONObject(String jsonStr, Class
	// descClass) throws Exception
	// {
	// JSONObject jsonObj = new JSONObject(jsonStr);
	// return getObjectByJSONObject(jsonObj, descClass);
	// }

	/**
	 * 通过Json对象和class获得java对象。 不支持List、map、Set类型，
	 * 支持基本对象类型，支持Date（需要继续开发，指定格式），支持非基本类型成员变量
	 * JSONObjectUtils.getObjectByJSONObject()
	 * 
	 * @param jsonObj
	 * @param descClass
	 *            要转换成的类型
	 * @return
	 * @throws Exception
	 * @return Object
	 * @author：tanyunhua 2012-9-12 下午2:19:11
	 */
	public static Object getObjectByJSONObject(JSONObject jsonObj,
			Class descClass) throws Exception {
		if (jsonObj == null || descClass == null) {
			return null;
		}
		Object obj = descClass.newInstance();
		Field[] fields = descClass.getDeclaredFields();
		for (Field f : fields) {
			String fieldName = f.getName();
			Object value = null;
			try {
				value = (Object) jsonObj.get(fieldName);
			} catch (Exception e) {
				Annotation subAnnot = f.getAnnotation(Sub.class);
				if (subAnnot != null) {
					Sub b = (Sub) subAnnot;
					Annotation arrAnnot = f
							.getAnnotation(ArrayAnnotation.class);
					if (arrAnnot != null) {
						JSONArray jsonArr = jsonObj.getJSONArray(b.name());
						value = jsonArr.getJSONObject(0).get(b.subName());
					} else {
						JSONObject jsonSub = jsonObj.getJSONObject(b.name());
						value = jsonSub.get(b.subName());
					}
					f.setAccessible(true);
					f.set(obj, value);
				}
				continue;
			}
			if (value == null)
				continue;
			Object propertiObj = null;
			if (!isWrapClass(f.getType())) {
				if (f.getType().equals(java.util.Date.class)) {
					// 2012-09-14T15:35:46+0800
					propertiObj = dateFormat.parse(value.toString());
				} else {
					propertiObj = getObjectByJSONObject(jsonObj, f.getType());
				}
			} else {
				propertiObj = value;
			}
			f.setAccessible(true);
			f.set(obj, propertiObj);
		}
		return obj;
	}

	private static DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * 解析实例
	 * 
	 * @return: T
	 * @param jsonResult
	 * @param classz
	 * @param refence
	 * @return
	 * @throws Exception
	 */
	public static <T> T parseOpzoonInstance(String jsonResult, Class<T> classz,
			T... refence) throws Exception {
		T object = null;
		if (refence != null && refence.length > 0) {
			object = (T) refence;
		} else {
			object = classz.newInstance();
		}
		try {
			JSONObject jso = new JSONObject(jsonResult);
			parseOpzoonInstanceByJsonAndObject(jso, object);
			return object;
		} catch (Exception e) {
			throw e;
		}
	}

	public static boolean isWrapClass(Class clz) {
		try {
			return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			if (clz.getName().equals("java.lang.String"))
				return true;
			else if (clz.getName().equals("org.json.JSONObject"))
				return true;
			else if (clz.getName().equals(JSONArray.class.getName()))
				return true;
			return false;
		}
	}

	/**
	 * cloudstack异步调用中，返回的json字符串含有jobid，获取此jobid
	 * 
	 * @param jsonStr
	 *            json字符串，格式为： { "response" :
	 *            {"jobid":"0da92604-fcf2-4c62-924e-5a6a0a930dec"} }
	 * @return jobid
	 * @throws Exception
	 */
	public static String getJobid(String jsonStr) throws Exception {
		JSONObject jsonObj = new JSONObject(jsonStr);
		String key = (String) jsonObj.keys().next();
		jsonObj = jsonObj.getJSONObject(key);
		return jsonObj.getString("jobid");
	}

	public static Integer getJobstatus(String jsonStr) throws Exception {
		JSONObject jsonObj = new JSONObject(jsonStr);
		String key = (String) jsonObj.keys().next();
		jsonObj = jsonObj.getJSONObject(key);
		return jsonObj.getInt("jobstatus");
	}

	/**
	 * 判断是否是基本类型与string类型 OpzoonUtils.isPrimitiveOrString()
	 * 
	 * @param clz
	 * @return
	 * @return boolean Author：maxiaochao 2012-9-26 上午10:32:34
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isPrimitiveOrString(Class clz) {
		try {
			if (clz.equals(String.class)) {
				return true;
			}
			return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

	public static Agent parseAgent(String str) throws Exception {
		
		Agent agent =new Gson().fromJson(str, Agent.class);

		return agent;
	}

	/**
	 * 
	 * 根据所传的 jsonObject 与java对象进行解析 目的是：对有些属性已经付值，有些没有赋值的java对象进行补充赋值
	 * 
	 * @return: void
	 * @param jso
	 * @param object
	 * @throws Exception
	 */
	public static <T> void parseOpzoonInstanceByJsonAndObject(JSONObject jso,
			T object) throws Exception {
		if (object == null) {
			throw new Exception(
					"you are parameter [object] is null,please input not-null object!");
		}
		if (jso == null) {
			throw new Exception(
					"you are parameter [JSONObject] is null,please input not-null JSONObject!");
		}
		String[] names = JSONObject.getNames(jso);
		@SuppressWarnings("unchecked")
		Class<T> classz = (Class<T>) object.getClass();
		for (int i = 0; i < names.length; i++) {
			Object value = jso.get(names[i]);
			String name = names[i];
			String key = name.substring(0, 1).toUpperCase();
			String methodName = "set" + key + name.substring(1);
			boolean isSetValue = false;
			if (isPrimitiveOrString(value.getClass())
					|| value.getClass().equals(JSONObject.class)
					|| value.getClass().equals(JSONArray.class)) {
				isSetValue = true;
			} else {
				isSetValue = false;
			}

			if (isSetValue) {
				try{
				Method method = classz.getMethod(methodName, value.getClass());
				method.invoke(object, value);
				}catch (Exception e) {
					
				}
			}
		}
	}

}
