/**
 * Project Name:vdi-core-server-lls
 * File Name:ComputePoolService.java
 * Package Name:com.vdi.service
 * Date:2014年8月11日下午2:25:24
 * Copyright (c) 2014 All Rights Reserved.
 *
 */

package com.vdi.facade;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.vo.req.ComputePoolIdReq;
import com.vdi.vo.req.DeleteComputePool;
import com.vdi.vo.res.ComputePoolRes;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.JobResponse;
import com.vdi.vo.res.ListComputePool;

public interface ComputePoolFacade {

	JobResponse createComputePool(ComputePoolEntity entity);
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	ListComputePool listComputePool(ComputePoolEntity entity);
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	Header updateComputePool(ComputePoolEntity entity);
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public JobResponse deleteComputePool(DeleteComputePool entity);
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ComputePoolRes getComputPool(ComputePoolIdReq entity);
	
	
}
