/**  
	 * @title: VDIcloudWS v04 Session.java 
 * @package com.opzoon.client.opzooncloud.session
 * @author maxiaochao
 * @date 2012-9-14
 * @version V04 
 */
package com.vdi.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.vdi.common.cache.Cache;

/**
 * 会话管理
 * 
 * @ClassName: Session.java
 * @Description: Session.java
 * @author: maxiaochao
 * @date: 2012-9-14
 * @version: V04
 */
public class Session {

	private Session() {
	}

	private static final ConcurrentHashMap<Object, Object> session = new ConcurrentHashMap<Object, Object>();
	private static final Cache<Object> CACHE = Cache.getInstance();




	@SuppressWarnings("unchecked")
	public static <V> V setCache(String key, V value, long time,
			TimeUnit timeUnit) {
		V v = (V) session.put(key, value);
		CACHE.put(key, value, time, timeUnit);
		return v;
	}

	/**
	 * 为避免多线程冲突与多次重复登录造成证书的不一致，采用future阻塞技术与ConcurrentMap做全局缓存 perio = 30M
	 * 每隔30分钟会更新证书 Session.getCertificate()
	 * 
	 * @param ip
	 * @return
	 * @throws Exception
	 * @return Certificate
	 * @author：maxiaochao 2012-9-19 下午1:50:15
	 */

	@SuppressWarnings("unchecked")
	public static <V> V setCache(Object key, V value) {
		V v = (V) session.put((key), value);
		return v;
	}

	public static Object getCache(Object queryUtil) {
		return session.get(queryUtil);
	}

	public static boolean containsKey(String key) {
		return session.containsKey(key);
	}

	public static Object removeCache(String key) {
		return session.remove(key);
	}
	public static Object getCache(String seed, Object key) {
		Object v=getStateBySeed(seed).getCache(key);
		return v;
	}
	
	/**
	 * Session.cacheSize()
	 * 
	 * @return
	 * @return int
	 * @author：maxiaochao 2012-9-24 上午10:04:31
	 */
	public static int cacheSize() {
		return session.size();
	}



	/**
	 * Session.stopSession() 清理Map集合中的关于平台的缓存证书，关闭当前会话
	 * 
	 * @return void
	 * @author：maxiaochao 2012-9-17 下午5:52:20
	 */
	public static void stopSession() {
		session.clear();
	}

	public static void clear() {
		session.clear();
	}

	public static void removeCacheByOrigin(String key) {
		session.remove(key);
	}
	public static <V> State getStateBySeed(String seed) {
		return State.getinstance(seed);
	}
}
