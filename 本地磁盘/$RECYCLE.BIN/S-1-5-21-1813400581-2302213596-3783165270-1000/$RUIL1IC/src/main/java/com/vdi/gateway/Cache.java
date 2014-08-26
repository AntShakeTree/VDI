package com.vdi.gateway;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

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
			Session.getPool().execute(daemonThread);
		}
	}
	private void daemonCheck() {

		while (isRun) {
			try {
				DelayItem<Pair<String, V>> delayItem = q.take();
				if (delayItem != null) {
					
					Pair<String, V> pair = delayItem.getItem();
					Object v = pair.value;
					if (v instanceof Monitor) {
						((Monitor)v).monitor();
					}
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

	public void put(String key, V value, long time, TimeUnit unit) {
		long nanoTime = TimeUnit.NANOSECONDS.convert(time, unit);
		q.put(new DelayItem<Pair<String, V>>(new Pair<String, V>(key, value),
				nanoTime));
		
	}
	
}