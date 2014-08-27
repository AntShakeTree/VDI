/**
 * 
 */
package com.opzoon.ohvc.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author maxiaochao
 * @version V04
 */
public class ConfigUtil {
	private static final Properties PROPERTIES = new Properties();
	static {
		InputStream inputStream = ConfigUtil.class.getResourceAsStream("/config.properties");
		try {
			PROPERTIES.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		 
		    try {
			inputStream.close();
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	}

	public static String getBykey(String key) {
		return PROPERTIES.getProperty(key);
	}


	public static void setCloudStackURL(String key){
		PROPERTIES.setProperty("vdi.cs.ip", key);
	}
}
