/**  
 * @title: VDIcloudWS v04 Session.java 
 * @package com.opzoon.client.opzooncloud.session
 * @author maxiaochao
 * @date 2012-9-14
 * @version V04 
 */
package com.opzoon.ohvc.session;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.opzoon.appstatus.common.ParseJSON;
import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.domain.PersistentMessage;
import com.opzoon.appstatus.facade.AppStatusService;
import com.opzoon.appstatus.facade.impl.AppStatusServiceImpl;
import com.opzoon.ohvc.domain.Certificate;
import com.opzoon.ohvc.domain.Login;
import com.opzoon.ohvc.request.AuthentateProxy;
import com.opzoon.ohvc.response.UsernameOrPasswordException;

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

	private static Logger log = Logger.getLogger(Session.class);
	private static final AppStatusService appStatusService = new AppStatusServiceImpl();
	private static final ConcurrentHashMap<String, Certificate> CERTIFICATE_MAP = new ConcurrentHashMap<String, Certificate>();
	private static final ConcurrentHashMap<String, Object> session = new ConcurrentHashMap<String, Object>();
	private static final Cache<Object> CACHE = Cache.getInstance();
	private static final AtomicReference<Thread> shutdownHook;
	private static final CountDownLatch latch = new CountDownLatch(1);
	private static Monitor MONITOR;
	static {
		shutdownHook = new AtomicReference<Thread>();
		registerShutdownHook();
	}

	public static void process() {
		try {
			latch.countDown();
		} catch (Exception e) {
		}
	}

	/**
	 * @return Returns the oPZOON_CLOUD_KEY.
	 */
	public static String getSessionId(String baseUrl) {
		return "opzoon_certificate_"+baseUrl;
	}

	/**
	 * @return Returns the oPZOON_CLOUD_KEY.
	 */
	private static String getCacheId(String key) {
		return  "cache_"+key;
	}

	/**
	 * @return Returns the session.
	 */
	public static void setAuthentication(final Certificate certificate,
			final String ip) throws Exception {

		setCertificate(certificate, ip);
		Session.process();

	}

	@SuppressWarnings("unchecked")
	public static <V> V setCache(String key, V value, long time,
			TimeUnit timeUnit) {
		String cacheKey = getCacheId(key);
		V v = (V) session.put(cacheKey, value);
		CACHE.put(cacheKey, value, time, timeUnit);
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
	public static Certificate getCertificate(final String baseUrl)
			throws Exception, UsernameOrPasswordException {

		Certificate certificate = (Certificate) CERTIFICATE_MAP
				.get(getSessionId(baseUrl));
		if (certificate == null) {
			try {
				PersistentMessage message = appStatusService
						.getPersistentMessageById(baseUrl);
				Certificate older = ParseJSON.getGSON().fromJson(
						message.getMessage(), Certificate.class);
				certificate = older;
				reLogin(older, baseUrl);
				latch.await(20, TimeUnit.SECONDS);
				certificate = (Certificate) CERTIFICATE_MAP
						.get(getSessionId(baseUrl));
			} catch (Exception e) {
				throw new UsernameOrPasswordException(
						"{errorcode:401,message: Authorization token is invalid}");
			}
		} else if (certificate != null && certificate.getCookie() == null) {
			reLogin(certificate, baseUrl);
			latch.await(20, TimeUnit.SECONDS);
			certificate = (Certificate) CERTIFICATE_MAP
					.get(getSessionId(baseUrl));
		} else {
			return certificate;
		}

		return certificate;
	}

	@SuppressWarnings("unchecked")
	public static <V> V setCache(String key, V value) {
		V v = (V) session.put(getCacheId(key), value);
		return v;
	}

	@SuppressWarnings("unchecked")
	public static <V> void whenCacheValuechangeNotice(String key, V value)
			throws IllegalAccessException {
		V v2 = (V) session.get(getCacheId(key));
		if (v2 != null) {
			if (!v2.equals(value)) {
				if (MONITOR != null) {
					MONITOR.monitor();
				} else {
					throw new IllegalAccessException("Haven't register monitor!");
				}
			}
		}
		session.put(getCacheId(key), value);
	}
	public static <V> void whenDelayNotice(String key,Monitor monitor,long time ,TimeUnit timeUnit)
			throws IllegalAccessException {
		setCache(key, monitor, time, timeUnit);
	}
	public static Object getCache(String key) {
		String encryptionKey = getCacheId(key);
		Object v = session.get(encryptionKey);
		return v;
	}

	public static Object getCache(String seed, String key) {
		String encryptionKey = getCacheId(seed);
		Object v = session.get(encryptionKey);
		if (v instanceof State) {
			return ((State<?>) v).getCache(key);
		}
		return null;
	}

	public static boolean containsKey(String key) {
		return session.containsKey(getCacheId(key));
	}

	public static Object removeCache(String key) {
		return session.remove(getCacheId(key));
	}

	/**
	 * Session.getCertificate()
	 * 
	 * @param certificate2
	 * @param ip
	 * @return void
	 * @throws Exception
	 * @author：maxiaochao 2012-9-19 下午3:56:27
	 */
	private static Certificate setCertificate(final Certificate certificate,
			final String ip) throws Exception {

		final String key = getSessionId(ip);

		if (certificate != null)
			CERTIFICATE_MAP.put(key, certificate);
		if (!session.containsKey(getMonitorKeyByIp(ip))) {
			Runnable reloginThread = new Runnable() {
				@Override
				public void run() {
					for (Certificate certificate : CERTIFICATE_MAP.values()) {
						reLogin(certificate, certificate.getBaseUrl());
					}
				}
			};
			synchronized (session) {
				if (session.get(getMonitorKeyByIp(ip)) == null) {
					ScheduledFuture<?> reloginSchduled = ExcecutorUtil
							.invokeSchedule(reloginThread, 1, 60,
									TimeUnit.SECONDS);
					session.put(getMonitorKeyByIp(ip), reloginSchduled);
				}
			}
		}
		return certificate;
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
	 * Register a shutdown hook with the JVM runtime, closing this context on
	 * JVM shutdown unless it has already been closed at that time.
	 * <p>
	 * Delegates to <code>doStop()</code> for the actual closing procedure.
	 * 
	 * @see java.lang.Runtime#addShutdownHook
	 * @see #close()
	 * @see #doStop()
	 */
	private static void registerShutdownHook() {

		if (shutdownHook.get() == null) {
			log.info("ShutdownHook is running...");
			shutdownHook.set(new Thread(new Runnable() {
				public void run() {
					ExcecutorUtil.shutdownCommonPool();
				}
			}));
			Runtime.getRuntime().addShutdownHook(shutdownHook.get());
		}
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

	/**
	 * Session.stopSessionByPlatformName(String IP) 依据平台ip关闭会话
	 * 
	 * @param vdiPlatformCloudstack
	 * @return void
	 * @author：maxiaochao 2012-9-17 上午9:56:39
	 */
	public static void stopSessionByPlatformName(String baseUrl) {
		removeAuthentication(baseUrl);
	}

	/**
	 * Session.stopSessionByPlatformName(String IP) 依据平台ip关闭会话
	 * 
	 * @param vdiPlatformCloudstack
	 * @return void
	 * @author：maxiaochao 2012-9-17 上午9:56:39
	 */
	private static String getMonitorKeyByIp(String baseUrl) {

		return "opzoon_monitor_"+baseUrl;

	}

	public static void clear() {
		session.clear();
	}

	/**
	 * 
	 * @param ip
	 */
	@SuppressWarnings("rawtypes")
	public static void removeAuthentication(String ip) {
		session.remove(getSessionId(ip));
		try {
			appStatusService.removePersistentMessage(ip);
			ScheduledFuture future = (ScheduledFuture) session
					.get(getMonitorKeyByIp(ip));
			if (future != null) {
				future.cancel(true);
				session.remove(getMonitorKeyByIp(ip));
			}

		} catch (AppstatusRestException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param certificate
	 * @param ip
	 * @return
	 */
	public static Certificate reLogin(Certificate certificate, String ip) {
		try {
			String proxyName = certificate.getProxyName();
			Class<?> proxy = Class.forName(proxyName);
			log.info(" Auto Authorization token . proxyName [" + proxyName
					+ "]");
			AuthentateProxy authentateProxy = (AuthentateProxy) proxy
					.newInstance();

			authentateProxy.start(ip,
					new Login()
							.setUsername(certificate.getUser().getUsername())
							.setPassword(certificate.getUser().getPassword()));
			log.info(" Auto Authorization token . Username ["
					+ certificate.getUser().getUsername() + "]");

			log.info(" Auto Authorization token . Password ["
					+ certificate.getUser().getPassword() + "]");
			try{
				appStatusService
						.getPersistentMessageById(ip);
			}catch(Exception e){
				distributeLogin(certificate, ip);
			}
		} catch (Exception e) {
			log.info("message: Authorization token is invalid [" + ip
					+ "] will get zk certificate");
			try {
				PersistentMessage message = appStatusService
						.getPersistentMessageById(ip);
				Certificate older = ParseJSON.getGSON().fromJson(
						message.getMessage(), Certificate.class);
				certificate = older;
				if (older != null) {
					appStatusService.removePersistentMessage(ip);
					reLogin(older, ip);
				} else {
					log.error("errorcode:401,message: Authorization token is invalid ["
							+ ip + "]");
				}
			} catch (Exception e1) {
				log.error("error getPersistentMessageById :" + e.getMessage());
			}
		}
		return certificate;
	}

	/**
	 * 
	 * @param key
	 */
	public static void removeCacheByOrigin(String key) {
		session.remove(key);
	}

	public static void distributeLogin(Certificate certificate, String ip) {
		int i=0;
		while(true){
		try {
			if(i>3){
				break;
			}
			PersistentMessage message = new PersistentMessage();
			message.setClassname(certificate.getProxyName());
			message.setId(ip);
			message.setMethod("start");
			message.setMessage(ParseJSON.getGSON().toJson(certificate));
			List<Object> array = new ArrayList<Object>();
			array.add(ip);
			array.add(new Login().setUsername(
					certificate.getUser().getUsername()).setPassword(
					certificate.getUser().getPassword()));
			message.setParameters(array);
			log.info("setAuthentication : zk publishPersistentMessage ["
					+ message + "]");

			appStatusService.publishPersistentMessage(message);
			break;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("distributeLogin", e);
			i++;
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e1) {
			}
		}
		}
	}

	public static boolean isInetAddress(String ip) {
		String reg = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
		if (!matches(ip, reg)) {
			try {
				String add = InetAddress.getByName(ip).getHostAddress();
				return matches(add, reg);
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Title: matches : d
	 * 
	 * @param value
	 * @param regular
	 * @param
	 * @return boolean
	 * @throws
	 */
	private static boolean matches(String value, String regular) {
		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public static <V> State<V> getStateBySeed(String seed) {
		State<V> c = new State<V>(seed);
		return c;
	}

	// public static <V> State<V> getDistributeStateBySeed(String seed) {
	// State<V> c = new State<V>(seed);
	// List<Object> ass = new ArrayList<Object>();
	// ass.add(seed);
	// try {
	// distributeSetPersistentCache(ass, "getStateBySeed");
	// } catch (AppstatusRestException e) {
	// e.printStackTrace();
	// }
	// return c;
	// }

	// public static <V> void putDistributeSetPersistentCache(String key, Object
	// v) {
	// List<Object> ass = new ArrayList<Object>();
	// ass.add(key);
	// ass.add(v);
	//
	// try {
	// distributeSetPersistentCache(ass, "setCache");
	// } catch (AppstatusRestException e) {
	// e.printStackTrace();
	// }
	// }

//	private static <V> void distributeSetPersistentCache(
//			List<Object> parametters, String method)
//			throws AppstatusRestException {
//		String dkey = "distributeCache";
//		PersistentMessage message = new PersistentMessage();
//		// message.setId(""")
//		message.setId(dkey);
//		// message.setMessage("{\"+key\"}:\""
//		// +key+ "\",\"value\":\""
//		// +value+ "\"}");
//		message.setClassname("com.opzoon.ohvc.session.Session");
//		message.setMethod(method);
//		message.setParameters(parametters);
//		PersistentMessage p = appStatusService.getPersistentMessageById(dkey);
//
//		if (p != null) {
//			synchronized (p) {
//				appStatusService.removePersistentMessage(dkey);
//				appStatusService.publishPersistentMessage(message);
//			}
//		} else {
//			appStatusService.publishPersistentMessage(message);
//		}
//	}

	public static void registerMonitor(Monitor monitor) {
		MONITOR = monitor;
	}
}
