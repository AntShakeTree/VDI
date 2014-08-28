/**
 * Project Name:vdi-core-server-lls
 * File Name:AsynchronousComputePoolServiceImpl.java
 * Package Name:com.vdi.support.desktop.lls.services.impl
 * Date:2014年8月11日下午4:02:37
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.support.desktop.lls.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.dao.desktop.domain.ComputePoolBuild;
import com.vdi.support.desktop.lls.domain.resource.ComputePool;
import com.vdi.support.desktop.lls.services.AsynchronousComputePoolService;
import com.vdi.vo.req.DeleteComputePool;

@Service
public class AsynchronousComputePoolServiceImpl implements
		AsynchronousComputePoolService {
	@Autowired
	AsynchronousServiceImpl asynchronousServiceImpl;
	@Autowired
	ComputePoolDao computePoolDao;

	@Override
	public String createComputePool(ComputePool computePool) {
		computePool.setAction(ComputePool.CREATE_COMPUTEPOOL_ACTION);
		return asynchronousServiceImpl.excute(computePool);
	}

	@Override
	public void deleteComputePool(DeleteComputePool entity) {
		for (long id : entity.getComputepoolids()) {
			ComputePoolEntity e = computePoolDao.get(ComputePoolEntity.class,
					id);
			ComputePool pool =new ComputePoolBuild(e,new ComputePool()).setComputePoolIdentity().bulidComputePool();
			pool.setAction(ComputePool.DELETE_COMPUTEPOOL_ACTION);
			asynchronousServiceImpl.excute(pool);
		}
	}

}
