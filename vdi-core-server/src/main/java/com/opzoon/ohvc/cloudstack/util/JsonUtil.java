package com.opzoon.ohvc.cloudstack.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.opzoon.ohvc.common.anotation.ArrayAnnotation;
import com.opzoon.ohvc.common.anotation.Sub;
import com.opzoon.ohvc.common.anotation.TargetField;

public class JsonUtil {

	/**
	 * 将cloudStack返回的正确response list字符串转换成需要的java List
	 * @param jsonStr json字符串，格式为：
	 * { \"response" : { "count":1 ,"template" : [  {"id":"1","name":"myname"},{"id":"2","name":"myname2"}] } }
	 * @param typeName 类型在json中的名称，如"template"
	 * @param descClass 需要转换成的list里的类型
	 * @return  返回descClass类型的List
	 * @throws Exception
	 */
	public static <T> List<T> getObjectList(String jsonStr, String typeName, Class descClass) throws Exception
	{
	    JSONObject jsonObj = new JSONObject(jsonStr);
	    String key = (String)jsonObj.keys().next();
	    jsonObj = jsonObj.getJSONObject(key);
	    if(!jsonObj.has(typeName))
	    	return null;
		JSONArray jsonArr = (JSONArray)jsonObj.get(typeName);
		List <T>   objList = new   ArrayList <T> ();
		
		for(int i = 0; i < jsonArr.length(); i++)
		{
			JSONObject jsonObj2 = (JSONObject) jsonArr.get(i);
			Object obj2 = getObjectByJSONObject(jsonObj2, descClass);
			objList.add((T) obj2);
		}
		return objList;
	}
	
	/**
	 * 通过Json对象和class获得java对象。
	 * 不支持List、map、Set类型，
	 * 支持基本对象类型，支持Date（需要继续开发，指定格式），支持非基本类型成员变量
	 * JSONObjectUtils.getObjectByJSONObject()
	 * @param jsonObj
	 * @param descClass 要转换成的类型
	 * @return
	 * @throws Exception
	 * @return Object
	 * @author：tanyunhua
	 * 2012-9-12 下午2:19:11
	 */
	public static Object getObjectByJSONObject(JSONObject jsonObj, Class descClass) throws Exception
	{
	    if(jsonObj == null || descClass == null)
	    {
	    	return null;
	    }
	    Object obj = descClass.newInstance();
	    Field[] fields = descClass.getDeclaredFields();
	    for(Field f: fields)
	    {
			String fieldName = f.getName();
			Object value = null;
			Annotation target = f.getAnnotation(TargetField.class);
			if(target != null)
			{
				try{
					TargetField t = (TargetField)target;
					value = jsonObj.get(t.target());
					f.setAccessible(true);
					f.set(obj, value);
				}catch(Exception e)
				{
				}
				continue;
			}
			try{
				value = (Object)jsonObj.get(fieldName);
			}catch(Exception e)
			{
				Annotation subAnnot = f.getAnnotation(Sub.class);
				if(subAnnot != null)
				{
					Sub b = (Sub)subAnnot;
					Annotation arrAnnot = f.getAnnotation(ArrayAnnotation.class);
					if(arrAnnot != null)
					{
						JSONArray jsonArr = jsonObj.getJSONArray(b.name());
						value = jsonArr.getJSONObject(0).get(b.subName());
					}
					else
					{
						JSONObject jsonSub = jsonObj.getJSONObject(b.name());
						value = jsonSub.get(b.subName());
					}
					f.setAccessible(true);
					f.set(obj, value);
				}
				continue;
			}
			if(value == null)
			    continue;
			Object propertiObj = null;
			if(!isWrapClass(f.getType()))
			{
				if(f.getType().equals(java.util.Date.class))
			    {
					//2012-09-14T15:35:46+0800
					propertiObj = dateFormat.parse(value.toString());
			    }
				else
				{
				    propertiObj = getObjectByJSONObject(jsonObj, f.getType());
				}
			}
			else
			{
				propertiObj = value;
			}
			f.setAccessible(true);
			f.set(obj, propertiObj);
	    }
	    return obj; 
	}

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	public static boolean isWrapClass(Class clz) {
	    try {
	    	return ((Class)clz.getField("TYPE").get(null)).isPrimitive(); 
	    } catch (Exception e) {
			if(clz.getName().equals("java.lang.String"))
			    return true;
			else if(clz.getName().equals("org.json.JSONObject"))
				return true;
			else if(clz.getName().equals("java.lang.Object"))
				return true;
			return false;
	    }
	}
	
	/**
	 * cloudstack异步调用中，返回的json字符串含有jobid，获取此jobid
	 * @param jsonStr  json字符串，格式为：
	 * { "response" : {"jobid":"0da92604-fcf2-4c62-924e-5a6a0a930dec"} }
	 * @return jobid
	 * @throws Exception
	 */
	public static String getJobid(String jsonStr) throws Exception
	{
	    JSONObject jsonObj = new JSONObject(jsonStr);
	    String key = (String)jsonObj.keys().next();
	    jsonObj = jsonObj.getJSONObject(key);
		return jsonObj.getString("jobid");
	}
	
	public static Integer getJobstatus(String jsonStr) throws Exception
	{
	    JSONObject jsonObj = new JSONObject(jsonStr);
	    String key = (String)jsonObj.keys().next();
	    jsonObj = jsonObj.getJSONObject(key);
		return jsonObj.getInt("jobstatus");
	}
}
