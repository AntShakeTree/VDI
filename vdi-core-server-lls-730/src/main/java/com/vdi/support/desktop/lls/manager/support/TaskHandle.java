/**
 * Project Name:vdi-core-server-lls
 * File Name:TaskHandle.java
 * Package Name:com.vdi.support.desktop.lls.manager.support
 * Date:2014年8月12日上午11:25:47
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.support.desktop.lls.manager.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vdi.common.ParseJSON;
import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.HostDao;
import com.vdi.dao.desktop.domain.ComputePoolBuild;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.dao.desktop.domain.HostBulid;
import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.support.desktop.lls.domain.resource.ComputePool;
import com.vdi.support.desktop.lls.domain.resource.Host;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.support.desktop.lls.services.ComputePoolService;
import com.vdi.support.desktop.lls.services.HostService;

@Component
public class TaskHandle {
	@Autowired
	ComputePoolDao computePoolDao;
	@Autowired
	ComputePoolService computePoolService;
	@Autowired HostService hostService;
	@Autowired HostDao hostDao;
	public void handle(Task task) {
		if (task.getErrorCode() != null && task.getErrorCode() != 0) {
			return;
		}
		switch (task.getActionName()) {
		case ComputePool.CREATE_COMPUTEPOOL_ACTION:{
			ComputePool b = ParseJSON.convertObjectToDomain(task.getContent(),
					ComputePool.class);
			String cid = b.getComputePoolIdentity();
			ComputePool b2 = this.computePoolService.getComputePool(cid);
			b2 = ParseJSON.convertObjectToDomain(b2.getContent(),
					ComputePool.class);
			// ~~
			ComputePoolEntity computePoolEntity= new ComputePoolBuild(new ComputePoolEntity(), b2).entity_cpuamount().entity_computePoolIdentity().entity_cpurest().entity_dispatchtype().entity_memoryrest().entity_status().entity_computepoolname().bulidComputePoolEntity();
			computePoolDao.update(computePoolEntity);
		}
		break;
		case ComputePool.DELETE_COMPUTEPOOL_ACTION:{
			ComputePool b = ParseJSON.convertObjectToDomain(task.getContent(),
					ComputePool.class);
			String cid = b.getComputePoolIdentity();
			ComputePool b2 = this.computePoolService.getComputePool(cid);
			b2 = ParseJSON.convertObjectToDomain(b2.getContent(),
					ComputePool.class);
			ComputePoolEntity delecomputePoolEntity =computePoolDao.findOneByKey("computepoolname", b2.getComputePoolName());
			computePoolDao.delete(delecomputePoolEntity);
			break;
		}
		case Host.CREATE_HOST_ACTION:{
			Host host = ParseJSON.convertObjectToDomain(task.getContent(),Host.class);
			String hostIdentity=host.getHostIdentity();
			Host hostE=hostService.getHost(hostIdentity);
			hostE=ParseJSON.convertObjectToDomain(hostE.getContent(),
					Host.class);
			HostEntity entity = new HostBulid(new HostEntity(), hostE).hostEntity_hostIdentity().hostEntity_hostname().hostEntity_ipaddress().hostEntity_status().bulidHostEntity();
			hostDao.save(entity);
		}
		break;
		case Host.DELETE_HOST_ACTION:{
			
		}
		default:
			break;
		}
	}
}
