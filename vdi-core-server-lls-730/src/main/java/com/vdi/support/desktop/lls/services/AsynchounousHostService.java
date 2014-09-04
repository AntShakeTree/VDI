package com.vdi.support.desktop.lls.services;

import com.vdi.support.desktop.lls.domain.resource.Host;

public interface AsynchounousHostService {
//	HostResponse 
	String createHost(Host host);
	String removeHostFromComputePool(Host host);
	String deleteHost(Host host);
	String addHostToComputePool(Host host);
	//
}
