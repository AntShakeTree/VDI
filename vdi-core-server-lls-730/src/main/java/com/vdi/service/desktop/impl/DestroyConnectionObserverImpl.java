package com.vdi.service.desktop.impl;

import org.springframework.stereotype.Service;

import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.desktop.DestroyConnectionObserver;
import com.vdi.service.user.LdapStateSubject;
@Service
public class DestroyConnectionObserverImpl implements DestroyConnectionObserver {
	private UserMapBridge config;
	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(
			LdapStateSubject stateSubject) throws Exception {
		
	}

	@Override
	public void setLdapConfig(UserMapBridge config) {
		this.config=config;
	}

}
