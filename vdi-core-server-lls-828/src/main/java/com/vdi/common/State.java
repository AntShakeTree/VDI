package com.vdi.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class State {

	private static String SEED;
	private AtomicBoolean isOpen = new AtomicBoolean(false);
	private long timestamp = new Date().getTime();
	private final static State state = new State();

	private State() {
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	// ====================0000000000000000000000000000=============================
	public <T> void putCache(Object key, T value) throws IllegalAccessException {
		if (!isOpen.get()) {
			throw new IllegalAccessException(
					"The state's door is close. plese open this door.");
		}

		getMAP(SEED).put(key, value);

	}

	// ====================0000000000000000000000000000=============================
	public void removeCache(Object key) throws IllegalAccessException {
		if (!isOpen.get()) {
			throw new IllegalAccessException(
					"The state's door is close. plese open this door.");
		}
		getMAP(SEED).remove(key);

	}

	public <T> void putCacheWhenChangeValueLocalNotice(String key, T value,
			Monitor m) throws IllegalAccessException {
		if (!isOpen.get()) {
			throw new IllegalAccessException(
					"The state's door is close. plese open this door.");
		}
		@SuppressWarnings("unchecked")
		T v2 = (T) getMAP(SEED).get(key);
		if (v2 != null && !v2.equals(value)) {
			if (m != null) {
				m.monitor();
			} else {
				throw new IllegalAccessException("Haven't register monitor");
			}
			CacheValue c = new CacheValue();
			c.setTimestamp(timestamp);
			c.setValue(value);
			getMAP(SEED).put(key, c);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getCache(Object key) {

		Object c = getMAP(SEED).get(key);
		if (c == null) {
			return null;
		}
		if (c instanceof CacheValue) {
			return (T) ((CacheValue) c).getValue();
		}
		return (T) c;
	}

	public void clearValidDataBySEED(String SEED) throws IllegalAccessException {
		if (!isOpen.get()) {
			throw new IllegalAccessException("The door had been closed!");
		}
		for (Object key : getMAP(SEED).keySet()) {
			Object acheValue = getMAP(SEED).get(key);
			if (acheValue instanceof CacheValue) {
				CacheValue cacheValue = (CacheValue) acheValue;
				if (cacheValue.getTimestamp() < timestamp) {
					getMAP(SEED).remove(key);
				}
			} else {
				getMAP(SEED).remove(key);
			}
		}
	}

	public String getKey() {
		return SEED;
	}

	public static State getinstance(String seed) {
		SEED = seed;
		return state;
	}

	// ====================0000000000000000000000000000=============================
	// public static class CollectionSingle {
	@SuppressWarnings({ "unchecked" })
	private static ConcurrentHashMap<Object, Object> getMAP(String SEED) {
		if (Session.getCache(SEED) == null) {
			ConcurrentHashMap<Object, Object> SEEDMap = new ConcurrentHashMap<Object, Object>();
			Session.setCache(SEED, SEEDMap);
			return SEEDMap;
		} else {
			return (ConcurrentHashMap<Object, Object>) Session.getCache(SEED);
		}
	}

	public int size() {
		return getMAP(SEED).size();
	}

	// }
	// ====================0000000000000000000000000000=============================
	@SuppressWarnings("unchecked")//判断是否是分布式，是分布式需要同步清理
	public void gc() {
		Object o = Session.getCache(SEED);
		if (o != null) {
			ConcurrentHashMap<Object, Object> map = (ConcurrentHashMap<Object, Object>) o;
			map.clear();
		}
		Session.removeCache(SEED);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> listAll() {
		Collection<Object> cs = getMAP(SEED).values();
		List<T> list = new ArrayList<T>(cs.size());
		for (Object value : cs) {
			list.add((T) value);
		}
		return list;
	}

	/**
	 * 
	 * @throws IllegalAccessException
	 *             the door must be close,default the door was closed.
	 */
	public State openDoor() throws IllegalAccessException {
		if (!isOpen.get()) {
			this.timestamp = new Date().getTime();
		} else {
			throw new IllegalAccessException("The door had been opened!");
		}
		isOpen.set(true);
		return this;
	}

	/**
	 * 
	 * @throws IllegalAccessException
	 *             The door must be opened.
	 */
	public void closeDoor() throws IllegalAccessException {
		if (isOpen.get()) {
			isOpen.set(false);
		} else {
			throw new IllegalAccessException("The door had been closed!");
		}
	}

	public PolicyValue getPolicyValue(Object key) {
		// if()
		Object object = getCache(key);
		if (object instanceof PolicyValue) {
			return (PolicyValue) object;
		} else {
			return null;
		}
	}

	public State removePolicyValue(Object key) throws IllegalAccessException {
		// if()
		Object object = getCache(key);
		if (object instanceof PolicyValue) {
			PolicyValue policyValue = (PolicyValue) object;
			for (Object k : policyValue.getKeys()) {
				this.removeCache(k);
			}
			policyValue.clear();
			this.removeCache(key);
		}
		return this;
	}

	public PolicyValue instancePolicyValue(List<Object> keys) {

		return new PolicyValue().setKeys(keys);
	}

	/**
	 * putPolicy: A collection of Group key <br/>
	 * 
	 * @author tree
	 * @since JDK 1.7
	 */
	public State putPolicy(Object policyKey, List<Object> values) {
		try {
			this.putCache(policyKey, instancePolicyValue(values));
		} catch (IllegalAccessException e) {
		}
		return this;
	}

	public static void main(String[] args) throws IllegalAccessException {
		List<Object> values = new ArrayList<Object>();
		values.add("vvv");
		State.getinstance("zzz").openDoor().putPolicy("aaa", values)
				.closeDoor();
		System.out.println(State.getinstance("zzz").getPolicyValue("aaa")
				.getKeys().get(0));
		State.getinstance("zzz").openDoor().removePolicyValue("aaa")
				.closeDoor();
		System.out.println(State.getinstance("zzz").getPolicyValue("aaa")
				.getKeys().get(0));
	}


	static class CacheValue {
		private long timestamp;
		private Object value;

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

	}
	@SuppressWarnings("unchecked")
	public <T> List<T> queryListByPolicyValue(List<Object> keys){
		List<T> tc =new ArrayList<T>();
		for (Object key :keys ) {
			tc.add((T)state.getCache(key));
		}
		return tc;
	}
}
