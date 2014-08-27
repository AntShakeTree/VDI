package com.opzoon.ohvc.session;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
/**
 * 爭議設計：
 *  	   原设计思路：cache作用肯定是提高性能，缓存量不大，就没引入memcache一类开源东西，自己写了个缓存处理。既然缓存，就存在啥时候清理的问题
 *       原本猜想，如果有个对象，我要缓存，但是我只需要缓存一段时间，那么如何处理？只是单纯的想提升性能，所以时间是个经验值，不能我单独开发出一套业务来处理
 *       所以，借鉴了mem实现原理他内部应用就是delayQueue。
 *       //这个类的争议点出现在我的程序如果出现意外，java对象回收不了了。为了验证这一说法，我测试了N+1次，耗费了一下午，没出现这问题啊，都给定点清了。
 *       //本对象是个单例，值new了一次，而且目前为避免争议，已经不用了。
 *       
 * @author david
 * @version: V04 
 * @since V04
 * @param <V>
 * 2012-12-1
 */
public class Cache<V> {
	private static final Logger LOG = Logger.getLogger(Cache.class.getName());
	private DelayQueue<DelayItem<Pair<String, V>>> q = new DelayQueue<DelayItem<Pair<String, V>>>();
	private Thread daemonThread;
	private volatile boolean isRun = true;
	private static final Cache<Object> CACHE = new Cache<Object>();

	public static Cache<Object> getInstance() {
		return CACHE;
	}
	/**
	 * 一共就new了一次，不允许从外部new ，是单例 一旦实例化，那么就开个守护线程定點清理，为啥守护线程，斐守护线程，jvm关闭后可能还会允许。
	 * 題外話音：除了守護線程，其餘的所有線程我都設計了hook，關jvm，我就關它。
	 * @author david
	 */
	
	private Cache() {

		Runnable daemonTask = new Runnable() {
			public void run() {
				daemonCheck();
			}
		};

		daemonThread = new Thread(daemonTask);
		daemonThread.setDaemon(true);
		daemonThread.setName("Cache Daemon");
		if (isRun) {
			ExcecutorUtil.execute(daemonThread);
		}
	}
	/**
	 * ((#v#)):（拆弹类）take会wait，而且内部实现的非常好，看不懂多好，但是作者承诺非常高效。所以比通过程序睡觉醒了干活还精神。
	 * 	         ！！：第80行，我已经把对象从cache中清理了。gc会回收。  			 
	 * 	 
	 * @return: void
	 */
	private void daemonCheck() {

		LOG.info("cache service started.");
		while (isRun) {
			try {
				DelayItem<Pair<String, V>> delayItem = q.take();
				if (delayItem != null) {
					// 超时对象处理
					Pair<String, V> pair = delayItem.getItem();
					Object v = pair.value;
					if (v instanceof Monitor) {
						((Monitor)v).monitor();
					}
					Session.removeCacheByOrigin(pair.key);
					v=null;
				} else {
					break;
				}

			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
				this.isRun = false;
				break;
			}
		}

		LOG.info("cache service stopped.");
	}

	// 添加缓存对象
	public void put(String key, V value, long time, TimeUnit unit) {
		long nanoTime = TimeUnit.NANOSECONDS.convert(time, unit);
		q.put(new DelayItem<Pair<String, V>>(new Pair<String, V>(key, value),
				nanoTime));
		
	}

}