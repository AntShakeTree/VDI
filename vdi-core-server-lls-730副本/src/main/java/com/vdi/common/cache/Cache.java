package com.vdi.common.cache;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import com.vdi.common.ExcecutorUtil;
import com.vdi.common.Session;

/**
 *       
 * @author mxc
 * @param <V>
 */
public class Cache<V> {
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

		while (isRun) {
			try {
				DelayItem<Pair<String, V>> delayItem = q.take();

				if (delayItem != null) {
					Pair<String, V> pair = delayItem.getItem();
					Session.removeCacheByOrigin(pair.key);
				} else {
					break;
				}

			} catch (InterruptedException e) {
				this.isRun = false;
				break;
			}
		}
	}

	// 添加缓存对象
	public void put(String key, V value, long time, TimeUnit unit) {
		long nanoTime = TimeUnit.NANOSECONDS.convert(time, unit);
		q.put(new DelayItem<Pair<String, V>>(new Pair<String, V>(key, value),
				nanoTime));
		
	}

}