package com.vdi.support.desktop.lls.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.desktop.HostDao;
import com.vdi.support.desktop.lls.domain.resource.Host;
import com.vdi.support.desktop.lls.services.AsynchounousHostService;
import com.vdi.support.desktop.lls.services.AsynchronousService;
@Service
public class AsynchousHostServiceImpl implements AsynchounousHostService{

	private @Autowired HostDao hostDao;
	private @Autowired AsynchronousService asynchousHostService;
	@Override
	public String createHost(Host host) {
		host.setAction(Host.CREATE_HOST_ACTION);
		return asynchousHostService.excute(host);
	}

}
