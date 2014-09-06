package com.vdi.service.desktop.impl;

import org.springframework.stereotype.Service;

import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.desktop.DestroyConnectionObserver;
import com.vdi.service.user.UserStateSubject;
@Service
public class DestroyConnectionObserverImpl implements DestroyConnectionObserver {
	private UserMapBridge config;
	@Override
	public void whenUserStateChangeUpdateByLdapconfig(
			UserStateSubject stateSubject) throws Exception {
		
	}

	@Override
	public void setUserMapBridge(UserMapBridge config) {
		this.config=config;
	}

}
