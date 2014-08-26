package com.vdi.support.desktop.lls.services;


import java.util.List;

import org.springframework.stereotype.Service;

import com.vdi.support.desktop.lls.domain.resource.Host;

@Service
public interface HostService {
	List<Host>  listHost(Host host);
	Host getHost(String hostId);
}
