package com.vdi.support.desktop.lls.services.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.resource.Host;
import com.vdi.support.desktop.lls.manager.LLSSendMessage;
import com.vdi.support.desktop.lls.services.HostService;

@Component("hostService")
public class HostServiceImpl implements HostService {
	@Autowired
	@Qualifier("llsHandle")
	private LLSSendMessage llsSendMessage;

	@Override
	public List<Host> listHost(Host host) {
		Host hostMessage = new Host();
		hostMessage.setAction(Host.LIST_HOST_ACTION);
	    host = (host==null) ? new Host():host;
		hostMessage.setMapper(host);
		hostMessage  =llsSendMessage.sendMessage(hostMessage,Host.class);
		return ParseJSON.convertObjectToDomain(hostMessage.getContent(),Host.getListHostType());
	}

	@Override
	public Host getHost(String hostId) {
		Host host = new Host();
		host.setAction(Host.GET_HOST_ACTION);
		host.setHostIdentity(hostId);
		return llsSendMessage.sendMessage(host,Host.class);
	}

}
