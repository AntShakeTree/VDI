package com.vdi.facade;

import org.springframework.security.access.prepost.PreAuthorize;

import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.vo.req.HostIdReq;
import com.vdi.vo.res.HostResponse;
import com.vdi.vo.res.JobResponse;
import com.vdi.vo.res.ListHostResponse;

public interface HostFacad {
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	JobResponse createHost(HostEntity entity);
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	ListHostResponse listHost(HostEntity entity);
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	HostResponse getHost(HostIdReq req);
	
}
