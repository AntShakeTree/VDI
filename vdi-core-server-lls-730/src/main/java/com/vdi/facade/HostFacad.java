package com.vdi.facade;

import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.vo.req.HostIdReq;
import com.vdi.vo.res.HostResponse;
import com.vdi.vo.res.JobResponse;
import com.vdi.vo.res.ListHostResponse;

public interface HostFacad {
	JobResponse createHost(HostEntity entity);

	ListHostResponse listHost(HostEntity entity);
	
	HostResponse getHost(HostIdReq req);
	
}
