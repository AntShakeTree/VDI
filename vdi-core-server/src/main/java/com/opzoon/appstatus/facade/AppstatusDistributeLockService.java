package com.opzoon.appstatus.facade;

import java.util.concurrent.TimeUnit;

import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.domain.OpzoonDistributeLock;

/**
 * AppstatusDistributeLockService 分布式锁的服务发布
 * 
 * @author david
 * @version V0.2.1023（迭代3） Date：2013-11-08
 */
public interface AppstatusDistributeLockService {
	// void lock()
	/**
	 * 依据key 获得一把锁
	 * 
	 * @param key
	 * @param time
	 * @param unit
	 * @return
	 * @throws Exception
	 */
	boolean acquired(OpzoonDistributeLock lock, long time, TimeUnit unit)
			throws AppstatusRestException;

	/**
	 * 死等，必须获得锁
	 * 
	 * @param key
	 */
	void acquired(OpzoonDistributeLock lock) throws AppstatusRestException;

	/**
	 * 释放锁
	 * 
	 * @param key
	 */
	void release(OpzoonDistributeLock lock) throws AppstatusRestException;

	/**
	 * 本节点是否已经处理了
	 * 
	 * @param lock
	 * @throws Exception
	 */
	boolean isAcquiredInThisProcess(OpzoonDistributeLock lock) throws Exception;
}
