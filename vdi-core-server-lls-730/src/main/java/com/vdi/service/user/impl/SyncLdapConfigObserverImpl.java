package com.vdi.service.user.impl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.LdapConfigDao;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.dao.user.domain.Organization;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.SyncLdapConfigObserver;
import com.vdi.service.user.SyncUserObserver;

@Service
public class SyncLdapConfigObserverImpl implements SyncLdapConfigObserver {
	private UserMapBridge config;

	@Autowired
	private LdapConfigDao ldapConfigDao;
	@Autowired
	private OrganizationDao organizationDao;
	@Autowired
	private SyncUserObserver syncUserObserver;
	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(
			LdapStateSubject stateSubject) throws Exception {
		if (config.getStatus() != UserMapBridge.SYNC) {
			return;
		}
		try {
			List<Organization> os = LdapSupport.findAllOrganazations(config);
			for (Organization organization : os) {
				organization.setStatus(UserMapBridge.SYNC_USER);
				organizationDao.save(organization);
			}
			config.setOrganizations(os);
			config.setStatus(UserMapBridge.SYNC_USER);
			stateSubject.registerStateChangeObserver(syncUserObserver, config);
		} catch (Exception e) {
			config.getEntity().setStatus(UserMapBridge.ERROR);
			ldapConfigDao.update(config.getEntity());
		}
	}

	@Override
	public void setLdapConfig(UserMapBridge config) {
		this.config = config;
	}

}
