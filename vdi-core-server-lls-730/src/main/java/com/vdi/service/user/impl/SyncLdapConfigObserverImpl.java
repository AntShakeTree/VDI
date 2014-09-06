package com.vdi.service.user.impl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.LdapConfigDao;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.dao.user.domain.Organization;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.SyncLdapConfigObserver;
import com.vdi.service.user.SyncUserObserver;

@Service
public class SyncLdapConfigObserverImpl implements SyncLdapConfigObserver {
	private LdapConfig config;

	@Autowired
	private LdapConfigDao ldapConfigDao;
	@Autowired
	private OrganizationDao organizationDao;
	@Autowired
	private SyncUserObserver syncUserObserver;
	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(
			LdapStateSubject stateSubject) throws Exception {
		if (config.getStatus() != LdapConfig.SYNC) {
			return;
		}
		try {
			List<Organization> os = LdapSupport.findAllOrganazations(config);
			for (Organization organization : os) {
				organization.setStatus(LdapConfig.SYNC_USER);
				organizationDao.save(organization);
			}
			config.setOrganizations(os);
			config.setStatus(LdapConfig.SYNC_USER);
			stateSubject.registerStateChangeObserver(syncUserObserver, config);
		} catch (Exception e) {
			config.getEntity().setStatus(LdapConfig.ERROR);
			ldapConfigDao.update(config.getEntity());
		}
	}

	@Override
	public void setLdapConfig(LdapConfig config) {
		this.config = config;
	}

}
