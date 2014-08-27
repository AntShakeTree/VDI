package com.opzoon.appstatus.domain;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.opzoon.appstatus.manager.NodeManager;
/**
 * OpzoonDistributeLock 分布式锁
 * @author david
 * * @version V0.2.1023（迭代3）
 * Date：2013-11-08
 */
public class OpzoonDistributeLock {
	private InterProcessMutex lock;
	private static String LOCK_PATH = "/cluster/lock/";

	public OpzoonDistributeLock(String key) {
		//lock = new InterProcessMutex(NodeManager.getCLIENT(), LOCK_PATH + key);
	}

	public InterProcessMutex getLock() {
		return lock;
	}

}
