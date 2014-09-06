package com.vdi.service.user.impl;

import org.springframework.stereotype.Service;

import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.RemoveLdapConfigObserver;

@Service
public class RemoveLdapConfigObserverImpl implements RemoveLdapConfigObserver {

	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(
			LdapStateSubject stateSubject, LdapConfig config) {
		// TODO Auto-generated method stub
		
	}

}
