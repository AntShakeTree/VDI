package com.vdi.service.user.impl;

import org.springframework.stereotype.Service;

import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.service.user.RemoveOrganizationObserver;
import com.vdi.service.user.LdapStateSubject;
@Service
public class RemoveOrganizationObserverImpl implements
		RemoveOrganizationObserver {

	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(
			LdapStateSubject stateSubject, LdapConfig config) {
		// TODO Auto-generated method stub

	}

}
