package com.vdi.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.DomainDao;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.User;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.user.SyncUserObserver;
import com.vdi.service.user.UserStateSubject;

@Service
public class SyncUserObserverImpl implements SyncUserObserver {
	private @Autowired UserDao userDao;
	private @Autowired DomainDao domainDao;
	private UserMapBridge config;
	private @Autowired OrganizationDao orgnazaionDao;
	@Override
	public void whenUserStateChangeUpdateByLdapconfig(
			UserStateSubject stateSubject) {
		if (config.getStatus() != UserMapBridge.SYNC_USER) {
			return;
		}
		for (Organization organization : config.getOrganizations()) {
			List<User> us = LdapSupport.findUsers(config, organization);
			for (User user : us) {
				User hql=new User();
				hql.setOrganizationid(organization.getIdorganization());
				hql.setUsername(user.getUsername());
				List<User> users = userDao.listRequest(hql);
				if(users!=null&&users.size()>0){
					userDao.update(user);
				}else{
					user.setOrganizationid(organization.getIdorganization());
					user.setDomainguid(config.getGuid());
					userDao.save(user);
				}
			}
			organization.setStatus(UserMapBridge.NORMAL);
			orgnazaionDao.update(organization);
		}
		config.setStatus(UserMapBridge.NORMAL);
		Domain dao =domainDao.get(Domain.class,config.getGuid());
		dao.setStatus(UserMapBridge.NORMAL);
		domainDao.update(dao);
	}
	@Override
	public void setUserMapBridge(UserMapBridge config) {
		this.config=config;
	}

}
