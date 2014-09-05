package com.vdi.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.SyncOrgnazation;

@Service
public class SyncOrgnazationImpl implements SyncOrgnazation {
	@Autowired
	private OrganizationDao organizationDao;

	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(LdapStateSubject subject,
			LdapConfig config) {
		if (config.getStatus() != LdapConfig.SYNC) {
			return;
		}

	}

}
