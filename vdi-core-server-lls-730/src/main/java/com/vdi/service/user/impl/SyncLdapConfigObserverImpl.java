package com.vdi.service.user.impl;

import org.springframework.stereotype.Service;

import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.SyncLdapConfigObserver;
@Service
public class SyncLdapConfigObserverImpl implements SyncLdapConfigObserver {

	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(
			LdapStateSubject stateSubject, LdapConfig config) {
		// TODO Auto-generated method stub

	}

}
