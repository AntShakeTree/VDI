package com.opzoon.ohvc.session;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ((#v#)) 
 *       爭議設計：
 *  	   原设计思路：cache作用肯定是提高性能，缓存量不大，就没引入memcache一类开源东西，自己写了个缓存处理。既然缓存，就存在啥时候清理的问题
 *       原本猜想，如果有个对象，我要缓存，但是我只需要缓存一段时间，那么如何处理？只是单纯的想提升性能，所以时间是个经验值，不能我单独开发出一套业务来处理
 *       所以，借鉴了mem实现原理他内部应用就是delayQueue。
 *       这个类，所有操作，基本都是为了确保，时间的准性，没啥营养，就是定时清理，java源码中的schemaFuture，也是这么玩的。
 *       
 * @author maxiaochao
 * @vsersion: V04
 * @since V04
 * @param <T>
 *            2012-9-27
 */
public class DelayItem<T> implements Delayed {
	/** Base of nanosecond timings, to avoid wrapping */
	private static final long NANO_ORIGIN = System.nanoTime();
	
	/**
	 * Returns nanosecond time offset by origin
	 */
	final static long now() {
		return System.nanoTime() - NANO_ORIGIN;
	}

	/**
	 * Sequence number to break scheduling ties, and in turn to guarantee FIFO
	 * order among tied entries.
	 */
	private static final AtomicLong sequencer = new AtomicLong(0);

	/** Sequence number to break ties FIFO */
	private final long sequenceNumber;

	/** The time the task is enabled to execute in nanoTime units */
	private final long time;

	private final T item;

	public DelayItem(T submit, long timeout) {
		this.time = now() + timeout;
		this.item = submit;
		this.sequenceNumber = sequencer.getAndIncrement();
	}

	public T getItem() {
		return this.item;
	}

	public long getDelay(TimeUnit unit) {
		long d = unit.convert(time - now(), TimeUnit.NANOSECONDS);
		return d;
	}

	public int compareTo(Delayed other) {
		if (other == this) // compare zero ONLY if same object
			return 0;
		if (other instanceof DelayItem) {
			DelayItem x = (DelayItem) other;
			long diff = time - x.time;
			if (diff < 0)
				return -1;
			else if (diff > 0)
				return 1;
			else if (sequenceNumber < x.sequenceNumber)
				return -1;
			else
				return 1;
		}
		long d = (getDelay(TimeUnit.NANOSECONDS) - other
				.getDelay(TimeUnit.NANOSECONDS));
		return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
	}
}
