package com.opzoon.appstatus.facade.impl;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.opzoon.appstatus.common.exception.AppstatusExceptionHandle;
import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.domain.OpzoonDistributeLock;
import com.opzoon.appstatus.facade.AppstatusDistributeLockService;

@Service("appstatusDistributeLockService")
public class AppstatusDistributeLockServiceImpl implements
		AppstatusDistributeLockService {
	private static Logger log = Logger
			.getLogger(AppstatusDistributeLockServiceImpl.class);

	@Override
	public boolean acquired(OpzoonDistributeLock opzoonDistributeLock,
			long time, TimeUnit unit) throws AppstatusRestException {
		InterProcessMutex lock = opzoonDistributeLock.getLock();
		log.info("<=Appstatus=> acquired ");
		try {
			return lock.acquire(time, unit);
		} catch (Exception e) {
			e.printStackTrace();
			throw AppstatusExceptionHandle.throwAppstatusException(e);
		}
	}

	@Override
	public void release(OpzoonDistributeLock lock)
			throws AppstatusRestException {

		try {
			lock.getLock().release();
		} catch (Exception e) {
			throw AppstatusExceptionHandle.throwAppstatusException(e);
		}
	}

	@Override
	public void acquired(OpzoonDistributeLock lock)
			throws AppstatusRestException {
		InterProcessMutex lock2 = lock.getLock();
		try {
			lock2.acquire();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppstatusExceptionHandle.throwAppstatusException(e);
		}

	}

	@Override
	public boolean isAcquiredInThisProcess(OpzoonDistributeLock lock)
			throws Exception {
		return lock.getLock().isAcquiredInThisProcess();
	}

}
