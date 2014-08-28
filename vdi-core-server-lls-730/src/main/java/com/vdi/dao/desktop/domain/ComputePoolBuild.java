/**
 * Project Name:vdi-core-server-lls
 * File Name:ComputePoolEntityBuild.java
 * Package Name:com.vdi.dao.desktop.domain
 * Date:2014年8月12日下午3:08:32
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.dao.desktop.domain;

import static org.springframework.util.StringUtils.isEmpty;

import org.springframework.util.Assert;

import com.vdi.support.desktop.lls.domain.resource.ComputePool;

public class ComputePoolBuild {
	private ComputePool computePool;
	private ComputePoolEntity entity;

	public ComputePoolBuild setDispatchtype() {
		if (!isEmpty(entity.getDispatchtype())) {
			this.computePool.setDispatchType(entity.getDispatchtype());
		}
		return this;
	}

	public ComputePoolBuild setComputePoolName() {
		if (!isEmpty(entity.getComputepoolname()))
			this.computePool.setComputePoolName(entity.getComputepoolname());
		return this;
	}

	public ComputePoolBuild setComputePoolIdentity() {
		if (!isEmpty(entity.getComputePoolIdentity()))
			this.computePool.setComputePoolIdentity(entity
					.getComputePoolIdentity());
		return this;
	}

	

	public ComputePoolEntity bulidComputePoolEntity() {
		return entity;
	}

	public ComputePoolBuild entity_çomputepoolname() {
		if (!isEmpty(computePool.getComputePoolIdentity()))
			entity.setComputePoolIdentity(computePool.getComputePoolIdentity());
		return this;
	};

	public ComputePoolBuild entity_cpuamount() {
		this.entity.setCpuamount(computePool.getHostTotalCpuCoreNum());
		return this;
	};

	public ComputePoolBuild entity_cpurest() {
		entity.setCpurest(computePool.getHostTotalCpuCoreNum()
				- computePool.getWorkHostTotalCpuCoreNum());
		return this;
	};

	public ComputePoolBuild entity_memoryamount() {
		entity.setMemoryamount(computePool.getHostTotalMem());
		return this;
	};

	public ComputePoolBuild entity_memoryrest() {
		entity.setMemoryrest(computePool.getHostTotalMem()
				- computePool.getWorkHostTotalMem());
		return this;
	};

	public ComputePoolBuild entity_dispatchtype() {
		if (!isEmpty(computePool.getDispatchType()))
			entity.setDispatchtype(computePool.getDispatchType());
		return this;
	};

	public ComputePoolBuild entity_computePoolIdentity() {
		if (!isEmpty(computePool.getComputePoolIdentity())) {
			entity.setComputePoolIdentity(computePool.getComputePoolIdentity());
		}
		return this;
	};

	public ComputePoolBuild entity_status() {
		if(!isEmpty(computePool.getStatus())){
			switch (computePool.getStatus()) {
			case ComputePool.AVAILABLE:
				entity.setStatus(ComputePoolEntity.AVAILABLE);
				break;
			case ComputePool.CREATING:
				entity.setStatus(ComputePoolEntity.CREATING);
				break;
			case ComputePool.DELETING:
				entity.setStatus(ComputePoolEntity.DELETING);
				break;
			case ComputePool.HOSTADDING:
				entity.setStatus(ComputePoolEntity.HOSTADDING);
				break;
			case ComputePool.HOSTREMOVEING:
				entity.setStatus(ComputePoolEntity.HOSTREMOVEING);
				break;
			default:
				break;
			}
		}
		return this;
	};

	public ComputePoolBuild(ComputePoolEntity entity, ComputePool computePool) {
		Assert.notNull(entity);
		Assert.notNull(computePool);
		this.computePool = computePool;
		this.entity = entity;
	}
	
	public ComputePool bulidComputePool() {
		return this.computePool;
	}
}
