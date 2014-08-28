/**
 * Project Name:vdi-core-server-lls
 * File Name:ComputePoolEntityBuild.java
 * Package Name:com.vdi.dao.desktop.domain
 * Date:2014年8月12日下午3:08:32
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.dao.desktop.domain;

import com.vdi.support.desktop.lls.domain.resource.ComputePool;

public class ComputePoolEntityBuild {
	private ComputePool computePool = new ComputePool();

	public ComputePoolEntityBuild setDispatchtype(String dispatchtype) {
		this.computePool.setDispatchType(dispatchtype);
		return this;
	}

	public ComputePoolEntityBuild setComputePoolName(String computePoolName) {
		this.computePool.setComputePoolName(computePoolName);
		return this;
	}

	

	public ComputePoolEntityBuild setComputePoolIdentity(String computePoolIdentity) {
		this.computePool.setComputePoolIdentity(computePoolIdentity);
		return this;
	}

	public ComputePool build() {
		return this.computePool;
	}
}
