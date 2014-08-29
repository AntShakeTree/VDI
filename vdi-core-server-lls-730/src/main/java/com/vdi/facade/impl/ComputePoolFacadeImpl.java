/**
 * Project Name:vdi-core-server-lls
 * File Name:ComputePoolFacadeImpl.java
 * Package Name:com.vdi.service.impl
 * Date:2014年8月11日下午2:33:16
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.facade.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.vdi.common.ErrorCode;
import com.vdi.common.ExcecutorUtil;
import com.vdi.common.VDIBeanUtils;
import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.domain.ComputePoolBuild;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.facade.ComputePoolFacade;
import com.vdi.service.desktop.DeleteComputePoolTask;
import com.vdi.support.desktop.lls.domain.resource.ComputePool;
import com.vdi.support.desktop.lls.services.AsynchronousComputePoolService;
import com.vdi.support.desktop.lls.services.ComputePoolService;
import com.vdi.vo.req.DeleteComputePool;
import com.vdi.vo.res.ComputePoolRes;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.Job;
import com.vdi.vo.res.JobResponse;
import com.vdi.vo.res.ListComputePool;
import com.vdi.vo.res.ListComputePool.ComputePoolList;
import com.vdi.vo.res.Response;

@Service
public class ComputePoolFacadeImpl implements ComputePoolFacade {
	@Autowired
	AsynchronousComputePoolService asynchronousComputePoolService;
	@Autowired
	ComputePoolDao computePoolDao;
	@Autowired
	ComputePoolService computePoolService;
	@Override
	public JobResponse createComputePool(ComputePoolEntity entity) {
		computePoolDao.save(entity);
		
		String taskid =asynchronousComputePoolService
				.createComputePool(new ComputePoolBuild(entity,new ComputePool())
						.setComputePoolName()
						.setDispatchtype().bulidComputePool());
		JobResponse res = new JobResponse();
		res.setHead(new Header().setError(0));
		Job body=new Job();
		body.setJobid(taskid);
		res.setBody(body);
		return res;
	}

	@Override
	public ListComputePool listComputePool(ComputePoolEntity entity) {
		ListComputePool res = new ListComputePool();
		ComputePoolList body=new ComputePoolList();
		List<ComputePoolEntity> cs =computePoolDao.listRequest(entity);
		
		
		
		
		List<ComputePool> cps=computePoolService.listComputePool(new ComputePoolBuild(entity, new ComputePool()).setComputePoolIdentity().setComputePoolName().bulidComputePool());
		for (ComputePool computePool : cps) {
			String identity=computePool.getComputePoolIdentity();
			boolean isEx=false;
			for (ComputePoolEntity computePoolEntity : cs) {
				System.out.println(computePoolEntity.getComputepoolidentity());
				if(!StringUtils.isEmpty(identity)){
					if(computePoolEntity.getComputepoolidentity().equals(identity)){
						isEx=true;
						computePoolEntity=new ComputePoolBuild(computePoolEntity, computePool).entity_computePoolIdentity().entity_cpuamount().entity_cpurest().entity_dispatchtype().entity_memoryamount().entity_memoryrest().entity_status().bulidComputePoolEntity();
						computePoolDao.update(computePoolEntity);
					}
				}	
			}
			if(!isEx){
				ComputePoolEntity e =new ComputePoolBuild(entity, computePool).entity_computePoolIdentity().entity_computepoolname().entity_cpuamount().entity_cpurest().entity_dispatchtype().entity_memoryamount().entity_memoryrest().entity_status().bulidComputePoolEntity();
				e.setIdcomputepool(null);
				computePoolDao.save(e);
				cs=computePoolDao.listRequest(entity);
			}
		}
		body.setList(cs);
		res.setBody(body);
		return res;
	}

	@Override
	public Header updateComputePool(ComputePoolEntity entity) {
		Assert.notNull(entity);
		Assert.notNull(entity.getIdcomputepool());
		ComputePoolEntity dao =computePoolDao.get(ComputePoolEntity.class,entity.getIdcomputepool());
		Assert.notNull(dao);
		VDIBeanUtils.copyPropertiesByNotNull(entity, dao, null);
		computePoolDao.update(dao);
		return new Header().setError(0);
	}

	@Override
	@Transactional
	public JobResponse deleteComputePool(DeleteComputePool entity) {
		Assert.notNull(entity);
		JobResponse jobResponse=new JobResponse();
		Job body=new Job();
		jobResponse.setBody(body);
		Header head=new Header();
		jobResponse.setHead(head.setError(0));
		String task =asynchronousComputePoolService.deleteComputePool(entity);
		body.setError(0);
		body.setJobid(task);
		return jobResponse;
	}

	@Override
	public ComputePoolRes getComputPool(ComputePoolEntity entity) {
		ComputePoolRes res=new ComputePoolRes();
		Header head=new Header();
		res.setHead(head);
		if(entity.getIdcomputepool()==null){
			res.setBody(null);
			head.setError(ErrorCode.BAD_REQ);
			return res;
		}
		entity = computePoolDao.get(ComputePoolEntity.class,entity.getIdcomputepool());
		String identity=entity.getComputepoolidentity();
		if(!StringUtils.isEmpty(identity)){
			ComputePool computePool=computePoolService.getComputePool(identity);
				entity=new ComputePoolBuild(entity, computePool).entity_computePoolIdentity().entity_cpuamount().entity_cpurest().entity_dispatchtype().entity_memoryamount().entity_memoryrest().entity_status().bulidComputePoolEntity();
				computePoolDao.update(entity);
		}
		res.setBody(entity);
		return res;
	}

	@Override
	@Transactional
	public void testSave(List<ComputePoolEntity> es) {
		// TODO Auto-generated method stub
		for (ComputePoolEntity computePoolEntity : es) {
			computePoolDao.save(computePoolEntity);
		}
	}
}
