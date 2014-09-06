package com.vdi.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.DomainDao;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.User;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.SyncUser;

@Service
public class SyncUserImpl implements SyncUser {
	private @Autowired UserDao userDao;
	private @Autowired DomainDao domainDao;
	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(
			LdapStateSubject stateSubject, LdapConfig config) {
		// TODO Auto-generated method stub
		if (config.getStatus() != LdapConfig.SYNC_USER) {
			return;
		}
		for (Organization organization : config.getOrganizations()) {
			List<User> us = LdapSupport.findUsers(config, organization);
			for (User user : us) {
				userDao.save(user);
			}
		}
		config.setStatus(LdapConfig.NORMAL);
		Domain dao =domainDao.get(Domain.class,config.getGuid());
		dao.setStatus(LdapConfig.NORMAL);
		domainDao.save(dao);
	}

}
