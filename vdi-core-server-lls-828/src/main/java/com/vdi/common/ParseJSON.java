/**   
 * @Title: ParseJSON.java 
 * @Package com.opzoon.appstatus.common 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Nathan   
 * @date 2013-7-19 下午4:46:10 
 * @version V1.0   
 */
package com.vdi.common;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * @ClassName: ParseJSON
 * @Description: 解析
 * @author david
 * @date 2013-7-19 下午4:46:10
 */
public class ParseJSON {
	private static ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 *             Title: parseClusterConfigure Description: parse
	 *             ClusterConfigure
	 * 
	 * @param config
	 *            string
	 * @return List<ParseJSON>
	 * @throws
	 */
	public static String toJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T fromJson(String json, Class<T> classOfT) {
		try {
			return objectMapper.readValue(json, classOfT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T fromJson(String json, TypeReference<T> classOfT) {
		try {
			return objectMapper.readValue(json, classOfT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ObjectMapper getJson() {
		return objectMapper;
	}

	public static <T> T convertObjectToDomain(Object object,
			TypeReference<T> type) {
		return ParseJSON.getJson().convertValue(object, type);
	}

	public static <T> T convertObjectToDomain(Object object, Class<T> class1) {
		return ParseJSON.getJson().convertValue(object, class1);
	}
}
