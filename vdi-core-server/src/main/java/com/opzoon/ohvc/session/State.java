package com.opzoon.ohvc.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class State<T> {

	private String seed;
	private AtomicBoolean isOpen = new AtomicBoolean(false);
	private long timestamp = new Date().getTime();

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	// ====================0000000000000000000000000000=============================
	public void putCache(String key, T value) throws IllegalAccessException {
		if (!isOpen.get()) {
			throw new IllegalAccessException(
					"The state's door is close. plese open this door.");
		}
		CacheValue c = new CacheValue();
		c.setTimestamp(timestamp);
		c.setValue(value);
		getMAP(seed).put(key, c);

	}
	// ====================0000000000000000000000000000=============================
	public void removeCache(String key) throws IllegalAccessException {
		if (!isOpen.get()) {
			throw new IllegalAccessException(
					"The state's door is close. plese open this door.");
		}

		getMAP(seed).remove(key);

	}
	public void putCacheWhenChangeValueLocalNotice(String key, T value,
			Monitor m) throws IllegalAccessException {
		if (!isOpen.get()) {
			throw new IllegalAccessException(
					"The state's door is close. plese open this door.");
		}
		@SuppressWarnings("unchecked")
		T v2 = (T) getMAP(seed).get(key);
		if (v2 != null && !v2.equals(value)) {
			if (m != null) {
				m.monitor();
			} else {
				throw new IllegalAccessException("Haven't register monitor");
			}
			CacheValue c = new CacheValue();
			c.setTimestamp(timestamp);
			c.setValue(value);
			getMAP(seed).put(key, c);
		}
	}

	@SuppressWarnings("unchecked")
	public T getCache(String key) {
		CacheValue c = getMAP(seed).get(key);
		if (c == null) {
			return null;
		}
		return (T) c.getValue();
	}

	public void clearValidDataBySeed(String seed) throws IllegalAccessException {
		if (!isOpen.get()){
		    throw new IllegalAccessException("The door had been closed!");
		}
		for (String key : getMAP(seed).keySet()) {
			if (getMAP(seed).get(key).getTimestamp() < timestamp) {
				getMAP(seed).remove(key);
			}
		}
	}

	public String getKey() {
		return seed;
	}

	public State(String seed) {
		super();
		this.seed = seed;
	}

	// ====================0000000000000000000000000000=============================
	// public static class CollectionSingle {
	@SuppressWarnings({ "unchecked" })
	private static ConcurrentHashMap<String, CacheValue> getMAP(String seed) {
		if (Session.getCache(seed) == null) {
			ConcurrentHashMap<String, CacheValue> seedMap = new ConcurrentHashMap<String, CacheValue>();
			Session.setCache(seed, seedMap);
			return seedMap;
		} else {
			return (ConcurrentHashMap<String, CacheValue>) Session
					.getCache(seed);
		}
	}

	public int size() {
		return getMAP(seed).size();
	}

	// }
	// ====================0000000000000000000000000000=============================
	@SuppressWarnings("unchecked")
	public void gc() {
		((ConcurrentHashMap<String, CacheValue>) Session.getCache(seed))
				.clear();
		Session.removeCache(seed);
	}

	@SuppressWarnings("unchecked")
	public List<T> listAll() {
		Collection<CacheValue> cs = getMAP(seed).values();
		List<T> list = new ArrayList<T>(cs.size());
		for (CacheValue value : cs) {
			list.add((T) value.getValue());
		}
		return list;
	}
	/**
	 * 
	 * @throws IllegalAccessException the door must be close,default the door was closed.
	 */
	public void openDoor() throws IllegalAccessException {
		if (!isOpen.get()) {
			this.timestamp = new Date().getTime();
		} else {
			throw new IllegalAccessException("The door had been opened!");
		}
		isOpen.set(true);
	}
	/**
	 * 
	 * @throws IllegalAccessException The door must be opened.
	 */
	public void closeDoor() throws IllegalAccessException {
		if (isOpen.get()){
			isOpen.set(false);
		}else{
		    throw new IllegalAccessException("The door had been closed!");
		}
	}
}
