/**
 * Project Name:vdi-core-server-lls
 * File Name:AsynchronousComputePoolService.java
 * Package Name:com.vdi.support.desktop.lls.services
 * Date:2014年8月11日下午4:00:05
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.support.desktop.lls.services;

import com.vdi.support.desktop.lls.domain.resource.ComputePool;
import com.vdi.vo.req.DeleteComputePool;

public interface AsynchronousComputePoolService {
	public String createComputePool(ComputePool computePool);

	public void deleteComputePool(DeleteComputePool entity);
}
