/**
 * Project Name:vdi-core-server-lls
 * File Name:ComputePoolFacadeImpl.java
 * Package Name:com.vdi.service.impl
 * Date:2014年8月11日下午2:33:16
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.vdi.common.ExcecutorUtil;
import com.vdi.common.VDIBeanUtils;
import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.dao.desktop.domain.ComputePoolEntityBuild;
import com.vdi.facade.ComputePoolFacade;
import com.vdi.service.desktop.DeleteComputePoolTask;
import com.vdi.support.desktop.lls.services.AsynchronousComputePoolService;
import com.vdi.vo.req.DeleteComputePool;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListComputePool;
import com.vdi.vo.res.ListComputePool.ComputePoolList;

@Service
public class ComputePoolFacadeImpl implements ComputePoolFacade {
	@Autowired
	AsynchronousComputePoolService asynchronousComputePoolService;
	@Autowired
	ComputePoolDao computePoolDao;

	@Override
	public Header createComputePool(ComputePoolEntity entity) {
		computePoolDao.save(entity);
		
		asynchronousComputePoolService
				.createComputePool(new ComputePoolEntityBuild()
						.setComputePoolName(entity.getComputepoolname())
						.setDispatchtype(entity.getDispatchtype()).build());
	 
	 return new Header().setError(0);
	}

	@Override
	public ListComputePool listComputePool(ComputePoolEntity entity) {
		ListComputePool res = new ListComputePool();
		ComputePoolList body=new ComputePoolList();
		body.setList(computePoolDao.listRequest(entity));
		res.setBody(body);
		return res;
	}

	@Override
	public Header updateComputePool(ComputePoolEntity entity) {
		Assert.notNull(entity,"error=103");
		Assert.notNull(entity.getIdcomputepool());
		ComputePoolEntity dao =computePoolDao.get(ComputePoolEntity.class,entity.getIdcomputepool());
		Assert.notNull(dao);
		VDIBeanUtils.copyPropertiesByNotNull(entity, dao, null);
		computePoolDao.update(dao);
		return new Header().setError(0);
	}

	@Override
	@Transactional
	public Header deleteComputePool(DeleteComputePool entity) {
		Assert.notNull(entity);
		for (Long id :entity.getComputepoolids()) {
			computePoolDao.delete(computePoolDao.get(ComputePoolEntity.class, id));
		}
		ExcecutorUtil.execute(new DeleteComputePoolTask(entity,asynchronousComputePoolService));
		return new Header().setError(0);
	}
	
	
}
