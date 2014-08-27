/**   
 * @Title: ReadProperties.java 
 * Package com.opzoon.appstatus.common 
 * Description: 读取 appstatus.properties
 * @author maxiaochao  
 * @date 2013-10-11 上午10:27:38 lasttime：2013-11-8
 * @version V0.2.1023（迭代3）  
 */
package com.opzoon.appstatus.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import sun.util.logging.resources.logging;

/**
 * ClassName: ReadProperties Description: TODO(这里用一句话描述这个类的作用)
 * 
 * @author zhengyi
 * @date 2013-10-11 上午10:27:38
 * 
 */
public class ReadProperties 
{
//	private static final Properties PROPERTIES = new Properties();
//
//	static {
//		InputStream inputStream = ReadProperties.class.getResourceAsStream("/appstatus.properties");
//		try {
//			PROPERTIES.load(inputStream);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				inputStream.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public static String readProp(String key) {
//		return PROPERTIES.getProperty(key);
//	}
	
	
	public static String readProp(String key) 
	{
		/*String result = "";
		Resource resource = new FileSystemResource(AppStatusConstants.PROPERTIES_PATH);
		try
		{
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			if (props != null && props.containsKey(key) && !"".equals(props.get(key)))
			{
				result = props.getProperty(key);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return result;*/
		Resource resource = new FileSystemResource(AppStatusConstants.PROPERTIES_PATH);
		return readPropertiesFile(resource, key);
	}
	
	public static String readProp(String filePath, String key) 
	{
		Resource resource = new FileSystemResource(filePath);
		return readPropertiesFile(resource, key);
	}
	
	public static String readPropertiesFile(Resource resource, String key) 
	{
		String result = "";
		try
		{
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			if (props != null && props.containsKey(key) && !"".equals(props.get(key)))
			{
				result = props.getProperty(key);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public static void replaceToMap(Map<String, String> map, String key, String val)
	{
		for (Map.Entry<String, String> item : map.entrySet())
		{
			if (item.getKey() != null && item.getKey().equals(key))
			{
				item.setValue(val);
			}
		}
	}
	
	public static Map<String, String> readPropertiesToMap(String filePath) throws IOException
	{
		Map<String, String> results = new TreeMap<String, String>();
		
		Resource resource = new FileSystemResource(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
		
		String tmp = null;
		while ((tmp = br.readLine()) != null)
		{
			if (tmp.contains("=") && !tmp.startsWith("#"))
			{
				String[] item = tmp.split("=");
				String key = item[0];
				String val = "";
				if (item.length > 1)
				{
					val = item[1];
				}
				results.put(key, val);
			}
		}
		br.close();
		return results;
	}
	
	public static void writeToProperties(Map<String, String> map, String destPath) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destPath)));
		Iterator<String> it = map.keySet().iterator();
		
		while (it.hasNext())
		{
			String key = it.next();
			bw.append(key + "=" + map.get(key));
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}

}
