package com.vdi.service.user.impl;

import org.springframework.stereotype.Service;

import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.user.RemoveOrganizationObserver;
import com.vdi.service.user.UserStateSubject;
@Service
public class RemoveOrganizationObserverImpl implements
		RemoveOrganizationObserver {
	private UserMapBridge config;
	@Override
	public void whenUserStateChangeUpdateByLdapconfig(
			UserStateSubject stateSubject) {
		// TODO Auto-generated method stub

	}
	@Override
	public void setUserMapBridge(UserMapBridge config) {
		this.config=config;
	}

}
