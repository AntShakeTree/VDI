package com.vdi.service.user.impl;

import org.springframework.stereotype.Service;

import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.RemoveLdapConfigObserver;

@Service
public class RemoveLdapConfigObserverImpl implements RemoveLdapConfigObserver {
	private LdapConfig config;

	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(
			LdapStateSubject stateSubject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLdapConfig(LdapConfig config) {
		this.config = config;
	}
}
