package com.vdi.service.user.impl;

import java.util.List;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.dao.user.domain.Organization;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.SyncOrgnazationObserver;
import com.vdi.service.user.SyncUserObserver;

@Service
public class SyncOrgnazationImpl implements SyncOrgnazationObserver {
	@Autowired
	private OrganizationDao organizationDao;
	@Autowired
	private SyncUserObserver syncUser;
	private LdapConfig config;
	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(LdapStateSubject subject) {
		if (config.getStatus() != LdapConfig.SYNC) {
			return;
		}
		try {
			List<Organization> ls = LdapSupport.findAllOrganazations(config);
			for (Organization organization : ls) {
				organizationDao.save(organization);
			}
			//
			config.setStatus(LdapConfig.SYNC_USER);
			config.setOrganizations(ls);
			subject.registerStateChangeObserver(syncUser, config);
		
		} catch (NamingException e) {
		}
	}
	@Override
	public void setLdapConfig(LdapConfig config) {
		this.config=config;
		
	}

}
