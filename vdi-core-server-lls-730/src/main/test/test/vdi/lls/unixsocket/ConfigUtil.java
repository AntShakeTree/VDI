package test.vdi.lls.unixsocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ClassName: ConfigUtil
 * @author : mxc
 * @version:1.0.0.0
 */
public class  ConfigUtil{
	private static final Properties PROPERTIES = new Properties();
	private  static String LLS_CONFIG="/test.properties";
	public static void loadConfigFileByPath(String path)  {
		InputStream inputStream = ConfigUtil.class.getResourceAsStream(path);
		try {
			PROPERTIES.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getLLSConfigByKey(String key) {
		ConfigUtil.loadConfigFileByPath(LLS_CONFIG);
		return PROPERTIES.getProperty(key);
	}
	public static String getKey(String key) {
		return PROPERTIES.getProperty(key);
	}

//	public static void main(String[] args) {
//		System.out.println(getLLSConfigByKey("lls.sorcket.timeout"));
//	}
}
